# NOTE: before running on a machine for the first time
# pip install pandas
# pip install numpy
# pip install scipy
# pip install fuzzyset
import pandas as pd
import numpy as np
from scipy.sparse import csr_matrix
from sklearn.neighbors import NearestNeighbors
from fuzzyset import FuzzySet
import time
import sys
import subprocess

# load a csr into memory from a file
def load_csr(filename):
    info = np.load(filename)
    ind, val, ptr = info['ind'], info['val'], info['ptr']
    mat = mat = csr_matrix((val, ind, ptr), shape=info['shape'], dtype=np.double)
    term_ids = info['tids'][0]
    return mat, term_ids

# get the term id for a skill
def get_id(term, term_ids, fuzzy_set):
    closest_term = fuzzy_set[term][0][1]
    return term_ids[closest_term]

# get an entry from the sparse matrix
def get(mat, row, col):
    return mat[row].getcol(col).toarray().tolist()[0][0]

# retrieve a list of row ids that contain a skill (by term id)
def rows_with_skill_id(sid, mat):
    rows = []
    contains = np.argwhere(mat.indices == sid).flatten().tolist()
    ptrs = mat.indptr.tolist
    last = 0
    for i in contains:
        for j in xrange(last, len(mat.indptr)):
            if mat.indptr[j] > i:
                row = j-1
                last = row
                break
        rows.append(row)
    return rows

# normalize matrix according to l2 norm
def csr_l2normalize(mat, copy=False, **kargs):
    r""" Normalize the rows of a CSR matrix by their L-2 norm.
        If copy is True, returns a copy of the normalized matrix.
        """
    if copy is True:
        mat = mat.copy()
    nrows = mat.shape[0]
    nnz = mat.nnz
    ind, val, ptr = mat.indices, mat.data, mat.indptr
    # normalize
    for i in range(nrows):
        rsum = 0.0
        for j in range(ptr[i], ptr[i+1]):
            rsum += val[j]**2
        if rsum == 0.0:
            continue  # do not normalize empty rows
        rsum = 1.0/np.sqrt(rsum)
        for j in range(ptr[i], ptr[i+1]):
            val[j] *= rsum

    if copy is True:
        return mat

# valuate a skill, given its term id
def valuate_skill(sid, mat, vals):
    rows = rows_with_skill_id(sid, mat)
    acc = 0.0
    for row in rows:
        acc += vals[row]
    return acc/len(rows), len(rows)

GBP_to_USD = 1.34

# used directories
jobs_file     = 'data/adzuna/Train_rev1.csv'
descs_file    = 'job_descriptions.npz'
jar_file      = 'dist/CareerAdviser.jar'
params_file   = 'params.txt'
jobs_output   = 'jobs.tsv'
skills_output = 'skills.tsv'

# if the user supplies the directory for the training data
if len(sys.argv) > 1:
    jobs_file = sys.argv[1]

# call the initial gui
subprocess.call("java -jar {}".format(jar_file), shell=True)

# fetch the user input
#skills = sys.argv[1].split('|')
#jobs = sys.argv[2].split('|')
#skills = ['c++', 'java', 'python']
#jobs = ['cashier', 'tutor']

print "Loading params from {}...".format(params_file)
with open(params_file, 'rb') as fin:
    params = fin.read().splitlines()
    skills = params[0].split('|')
    jobs = params[1].split('|')

print "User's skills: {}".format(skills)
print "User's past jobs: {}".format(jobs)

# load the csv into memory
start = time.time()
print "Loading csv into memory..."
all_jobs = pd.read_csv(jobs_file)
print "Loading complete. (took {:.3f} s)".format(time.time() - start)

# load the csr into memory
task_start = time.time()
print "Loading from processed/job_descriptions.npz..."
descs, tids = load_csr(descs_file)
print "File successfully loaded. (took {:.3f} s)".format(time.time() - task_start)

# generate a fuzzy set with all the terms in the job description corpus
task_start = time.time()
print "Creating fuzzy set of skills..."
fuzzy_terms = FuzzySet(tids.keys())
print "Fuzzy set created. (took {:.3f} s)".format(time.time() - task_start)

# generate a fuzzy set with all the job titles
task_start = time.time()
print "Creating fuzzy set of job titles..."
fuzzy_titles = FuzzySet([job for job in all_jobs['Title'] if type(job) == str])
print "Fuzzy set created. (took {:.3f} s)".format(time.time() - task_start)

# generate a skill vector
task_start = time.time()
print "Generating user's skills vector..."
ind = [get_id(skill, tids, fuzzy_terms) for skill in skills]
val = [1] * len(ind)
ptr = [0, len(ind)]
user_vector = csr_matrix((val, ind, ptr), shape = [1, descs.shape[1]], dtype=np.double)
print "Skills vector created. (took {:.3f} s)".format(time.time() - task_start)

# add skills from previous jobs to user's skill vector
task_start = time.time()
print "Adding skills inferred from previous jobs..."
for j in jobs:
    closest = fuzzy_titles[j][0][1]
    index = np.where(all_jobs['Title']==closest)[0][0]
    user_vector = user_vector + descs[index]
csr_l2normalize(user_vector)
print "Skills vector is complete. (took {:.3f} s)".format(time.time() - task_start)

# compare user's skill vector with job descriptions in database
task_start = time.time()
print "Finding relevant jobs..."
knn = NearestNeighbors(n_neighbors=5, metric='cosine', algorithm='brute')
knn.fit(descs)
answers = knn.kneighbors(user_vector, return_distance=False)
mask = np.zeros(descs.shape[0])
for i in answers[0]:
    mask[i] = 1
selected_jobs = all_jobs[pd.Series(np.array(mask), dtype=bool)]
selected_jobs = selected_jobs[['Title','FullDescription','SalaryNormalized']]
selected_jobs['Salary'] = selected_jobs['SalaryNormalized'] * GBP_to_USD
selected_jobs.pop('SalaryNormalized')
selected_jobs.columns = ['title', 'description', 'salary']
selected_jobs.set_index('title', inplace=True)
selected_jobs.sort_values(by=['salary'], inplace=True, ascending=False)
print "Retrieved 5 nearest jobs. (took {:.3f} s)".format(time.time() - task_start)

task_start = time.time()
print "Valuating user's skills..."
incomes = (all_jobs['SalaryNormalized'] * GBP_to_USD).tolist()
skill_results = [valuate_skill(s, descs, incomes) for s in ind]
skill_values = [s[0] for s in skill_results]
skill_num_jobs = [s[1] for s in skill_results]
skills_df = pd.DataFrame(data={'skill': skills, 'value': skill_values, 'quantity': skill_num_jobs})
skills_df = skills_df[['skill', 'value', 'quantity']]
skills_df.set_index('skill', inplace=True)
skills_df.sort_values(by=['value'], inplace=True, ascending=False)
print "Finished valuating skills. (took {:.3f} s)".format(time.time() - task_start)

task_start = time.time()
print "Writing results to {} and {}...".format(jobs_output, skills_output)
selected_jobs.to_csv(jobs_output, sep='\t')
skills_df.to_csv(skills_output, sep='\t')
print "Results successfully written. (took {:.3f} s)".format(time.time() - task_start)

end = time.time()

# Call the gui with the completed results
subprocess.call("java -jar {} potato".format(jar_file), shell=True)

print "Advisor has completed successfully. {:.3f} seconds have elapsed.".format(end - start)
