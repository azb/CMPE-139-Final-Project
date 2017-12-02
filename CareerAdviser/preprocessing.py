# This script takes downloaded oe data and parses it into a more usable format
#
# USAGE: python preprocessing.py
# OR:    python preprocessing.py path/to/source/oe-data-dir
#
# It saves the preprocessed files in csv format into a new directory called 'processed/'

import pandas as pd
import sys
import time
start_time = time.time()

# default source directory
oe_dir = 'data/oestats/'

# user can also specify source directory as the first cmdline argument
if len(sys.argv) == 2:
    oe_dir = sys.argv[1]
print "Source directory is: '{}'\nBeginning to process...".format(oe_dir)

# relevant filenames are defined here
fname = {
    'datatype': 'oe.datatype.txt',
    'occupation': 'oe.occupation.txt',
    'industry': 'oe.industry.txt',
    'current data': 'oe.data.0.Current.txt',
    'all data': 'oe.data.1.AllData.txt',
    'series': 'oe.series.txt'
}

# here we parse the relevant files into separate pandas dataframes

# datatype of value
task_start = time.time()
path = oe_dir + fname['datatype']
print "Reading {}...".format(path)
datatype = pd.read_csv(path, sep='\t', index_col=False)
print "Read successful. (took {:.3f} s)".format(time.time() - task_start)

# occupation from occupation code
task_start = time.time()
path = oe_dir + fname['occupation']
print "Reading {}...".format(path)
occupation = pd.read_csv(path, sep='\t', index_col=False)
occupation = occupation[['occupation_code','occupation_name']].set_index('occupation_code')
print "Read successful. (took {:.3f} s)".format(time.time() - task_start)

# industry from industry code
task_start = time.time()
path = oe_dir + fname['industry']
print "Reading {}...".format(path)
industry = pd.read_csv(path, sep='\t', index_col=False)
industry = industry[['industry_code','industry_name']].set_index('industry_code')
print "Read successful. (took {:.3f} s)".format(time.time() - task_start)

# current occupation data
task_start = time.time()
path = oe_dir + fname['current data']
print "Reading {}...".format(path)
data_cur = pd.read_csv(path, sep='\t', index_col=False,
                       dtype={'series_id': str, 'value': str})
data_cur = data_cur[['series_id','value']].set_index('series_id')
print "Read successful. (took {:.3f} s)".format(time.time() - task_start)

# all occupation data
task_start = time.time()
path = oe_dir + fname['all data']
print "Reading {}...".format(path)
data_all = pd.read_csv(path, sep='\t', index_col=False,
                       dtype={'series_id': str, 'value': str})
data_all = data_all[['series_id','value']].set_index('series_id')
print "Read successful. (took {:.3f} s)".format(time.time() - task_start)

# obtaining wage information

# fields for wages
task_start = time.time()
path = oe_dir + fname['series']
print "Reading {}...".format(path)
trivial_fields = ['begin_year','begin_period','end_year','end_period', 'seasonal'] # = (2016, A01, 2016, A01, U)
location_fields = ['series_id','areatype_code', 'state_code', 'area_code', 'sector_code']
series_fields = ['series_id', 'industry_code', 'occupation_code', 'datatype_code', 'series_title']
desired_fields = series_fields + location_fields[1:]

series = pd.read_csv(path, sep='\t', index_col=False,
                     dtype={'series_id': str,
                            'industry_code': str,
                            'occupation_code': str,
                            'datatype_code': int,
                            'series_title': str,
                            'areatype_code': str,
                            'state_code': int,
                            'area_code': str,
                            'sector_code': str},
                     usecols=desired_fields)

series_loc = series[location_fields].set_index('series_id')
series = series[series_fields].set_index('series_id')
print "Read successful. (took {:.3f} s)".format(time.time() - task_start)

# filter the rows in series to those with relevant values
print "Retrieving only entries related to mean annual wage."
old_series_size = series.size
series = series[series['datatype_code'] == 4]
print "Entries in series reduced from {} to {}".format(old_series_size, series.size)

# associate values with series data
task_start = time.time()
print "Joining series data with corresponding values from current data table..."
merged = series.join(data_cur)

# convert values from strings to floats
values = pd.to_numeric(merged['value'], errors='coerce')
merged.pop('value')
merged['value'] = values
print "Join operation completed successfully. (took {:.3f} s)".format(time.time() - task_start)

# write processed values to new csv files in a new folder
task_start = time.time()
print "Exporting results to .csv files..."
processed_dir = "processed/"
frames = {'datatype.csv': datatype,
          'occupation.csv': occupation,
          'industry.csv': industry,
          'series.csv': merged,
          'series-location.csv': series_loc}
for filename, dataframe in frames.iteritems():
    print "    Exporting {}...".format("processed/"+filename)
    dataframe.to_csv("processed/" + filename, sep='\t')
print "Export successful. (took {:.3f} s)".format(time.time() - task_start)

end_time = time.time()
print "Processing is complete. {:.3f} seconds have elapsed.".format(end_time - start_time)
