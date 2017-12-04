# NOTE: before running on a machine for the first time
# pip install pandas
# pip install numpy
# pip install scipy
# pip install fuzzyset
import pandas as pd
import numpy as np
from scipy.sparse import csr_matrix
from fuzzyset import FuzzySet
import time
import sys

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

# find the similarity between two rows
def sim(row1, row2, normalized=False):
    if normalized is False:
        row1 = csr_l2normalize(row1, copy=True)
        row2 = csr_l2normalize(row2, copy=True)
    return row1.dot(row2.T).todense().item()

# fetch the user input
skills = sys.argv[1].split('|')
jobs = sys.argv[2].split('|')

# load the csv into memory
start = time.time()
print "Loading csv into memory..."
all_jobs = pd.read_csv('data/adzuna/Train_rev1.csv')
print "Loading complete. (took {:.3f} s)".format(time.time() - start)

# load the csr into memory
task_start = time.time()
print "Loading from processed/job_descriptions.npz..."
descs, tids = load_csr('processed/job_descriptions.npz')
print "File successfully loaded. (took {:.3f} s)".format(time.time() - task_start)

# generate a fuzzy set with all the terms in the job description corpus
task_start = time.time()
print "Creating fuzzy set of skills..."
fuzzy_terms = FuzzySet(tids.keys())
print "Fuzzy set created. (took {:.3f} s)".format(time.time() - task_start)

# generate a fuzzy set with all the job titles
task_start = time.time()
print "Creating fuzzy set of skills..."
fuzzy_titles = FuzzySet([job for job in all_jobs['Title'] if type(job) == str])
print "Fuzzy set created. (took {:.3f} s)".format(time.time() - task_start)

# generate a skill vector
ind = [get_id(skill, tids, fuzzy_terms) for skill in skills]
val = [1] * len(ind)
ptr = [0, len(ind)]
user_vector = csr_matrix((val, ind, ptr), shape = [1, mat.shape[0]], dtype=np.double)
#csr_l2normalize(user_vector)

# add skills from previous jobs to user's skill vector
for j in jobs:
    closest = fuzzy_titles[j][0][1]
    index = np.where(all_jobs['Title']==closest)
    user_vector = user_vector + descs[index]
csr_l2normalize(user_vector)

# compare user's skill vector with job descriptions in database
similar_jobs = [(i, sim(user_vector, descs[i], normalized=True)) for i in range(descs.shape[0])]
sorted = similar_jobs.sort(key=lambda x: x[1])
nearest_idx = [ listing[0] for listing in sorted[0:5] ]
