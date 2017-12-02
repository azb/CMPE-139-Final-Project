import pandas as pd
import sys

print "There are {} cmdline arg(s).\nThese are: {}".format(len(sys.argv), sys.argv)

jobs = {'title': ['poopsmith', 'code monkey', 'burger-flipper'],
        'description': ['you get to shovel poop all day.',
                       'congrats mr java programmer #4096 now code my coffee for me on my desk in 10 mins',
                       'US.obesity++'],
        'salary': ['$1', '$200000', '$min wage']}

print "Exporting results to 'results.csv'..."

# generate dataframe from dictionary
df = pd.DataFrame(data=jobs)

# orient columns correctly
df = df.reindex(columns=['title', 'salary', 'description'])

# write to a csv file
df.to_csv("results.csv", sep='\t')
print "Results successfully written to 'results.csv'."
