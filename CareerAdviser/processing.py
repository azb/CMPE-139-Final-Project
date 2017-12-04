# NOTE: before running on a machine for the first time
# pip install pandas
# pip install numpy
# pip install scipy
# pip install fuzzyset
import pandas as pd
import numpy as np
from scipy.sparse import csr_matrix
from collections import defaultdict, Counter
from fuzzyset import FuzzySet
import time
import re

white = re.compile("(<br />)|(\n)|(\t)|(\r)")
punct = re.compile("[\[\]?!\*,.()\"/]")
pay = re.compile("([A-Za-z]*\*\**[A-Za-z]*)|(\** [Pp]er [A-Za-z]*)")
header = re.compile("[A-Z]{2,}[^a-z0-9]*[A-Z]+: |[A-Za-z]+:")

def sanitize(desc):
    desc = white.sub(' ', desc)         # remove whitespace characters
    desc = pay.sub(' ', desc)           # remove censored salary info
    desc = punct.sub(' ', desc)         # remove punctuation (except ':')
    desc = header.sub(' ', desc)        # remove headers ('ALL-CAPS:')
    desc = re.sub(r" {2,}", ' ', desc)  # reduce multiple spaces to one
    return desc.lower()

def get_jobdesc_csr(df):
    descs = []
    # number all terms
    idx = {}
    tid = 0
    nnz = 0
    for desc in df['FullDescription'].values:
        terms = sanitize(desc).split()
        descs.append(terms)
        nnz += len(set(terms))
        for t in terms:
            if t not in idx:
                idx[t] = tid
                tid += 1

    # record document frequencies of terms as well
    docfreq = defaultdict(int)

    # get ind, val, & ptr arrays
    nrows = len(descs)
    ncols = len(idx)
    ind = np.zeros(nnz, dtype=np.int)
    val = np.zeros(nnz, dtype=np.double)
    ptr = np.zeros(nrows+1, dtype=np.int)
    i = 0
    n = 0
    for terms in descs:
        count = Counter(terms)
        keys = list(k for k,_ in count.most_common())
        l = len(keys)
        for j,k in enumerate(keys):
            ind[j+n] = idx[k]
            val[j+n] = count[k]
            docfreq[idx[k]] += count[k]
        ptr[i+1] = ptr[i] + l
        n += l
        i += 1

    mat = csr_matrix((val, ind, ptr), shape=(nrows, ncols), dtype=np.double)
    return mat, idx, docfreq

# scale matrix according to inverse document frequency
def csr_idf(mat, copy=False, **kargs):
    r""" Scale a CSR matrix by idf.
        Returns scaling factors as dict. If copy is True,
        returns scaled matrix and scaling factors.
        """
    if copy is True:
        mat = mat.copy()
    nrows = mat.shape[0]
    nnz = mat.nnz
    ind, val, ptr = mat.indices, mat.data, mat.indptr
    # document frequency
    df = defaultdict(int)
    for i in ind:
        df[i] += 1
    # inverse document frequency
    for k,v in df.items():
        df[k] = np.log(nrows / float(v))  ## df turns to idf - reusing memory
    # scale by idf
    for i in range(0, nnz):
        val[i] *= df[ind[i]]

    return df if copy is False else mat

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

# remove features that are shared among very few documents
def csr_filter_cols(mat, threshold=3):
    # find col indices with low frequency
    cts = defaultdict(int)
    for ind in mat.indices:
        cts[ind] += 1
    drop = np.unique([k for k,v in cts.iteritems() if v < threshold])
    coo = mat.tocoo()
    keep = ~np.in1d(coo.col, drop)

    # filter rows and cols to desired ones
    coo.row, coo.col, coo.data = coo.row[keep], coo.col[keep], coo.data[keep]

    # decrement the col indices
    coo.col -= drop.searchsorted(coo.col)

    # update the dimensions
    coo._shape = (coo.shape[0], coo.shape[1] - len(drop))
    mat = coo.tocsr()
    return mat

def save_csr(filename, mat, term_ids):
    np.savez(filename, val = mat.data,
             ind = mat.indices,
             ptr = mat.indptr,
             shape = mat.shape,
             tids = np.array([term_ids]))

def load_csr(filename):
    info = np.load(filename)
    ind, val, ptr = info['ind'], info['val'], info['ptr']
    mat = mat = csr_matrix((val, ind, ptr), shape=info['shape'], dtype=np.double)
    term_ids = info['tids'][0]
    return mat, term_ids

start = time.time()
print "Loading csv into memory..."
df = pd.read_csv('data/adzuna/Train_rev1.csv')
print "Loading complete. (took {:.3f} s)".format(time.time() - start)

task_start = time.time()
print "Converting to csr..."
mat, termids, docfreqs = get_jobdesc_csr(df)
print "Conversion complete. (took {:.3f} s)".format(time.time() - task_start)

task_start = time.time()
print "Scaling matrix by inverse document frequency..."
csr_idf(mat)
print "Scaling successful. (took {:.3f} s)".format(time.time() - task_start)

task_start = time.time()
print "Normalizing matrix by L2-norm..."
csr_l2normalize(mat)
print "Normalization successful. (took {:.3f} s)".format(time.time() - task_start)

task_start = time.time()
print "Saving to processed/job_descriptions.npz..."
save_csr('processed/job_descriptions.npz', mat)
print "File successfully saved. (took {:.3f} s)".format(time.time() - task_start)

print "Preprocessing finished. {:.3f} seconds have elapsed.".format(time.time() - start)
