'''
Created on Dec 2, 2017

@author: Rafael
'''

import pandas as pd 
import csv

def getEntriesWithSkill(  skill, jobDes = pd.read_csv("data job posts.csv", usecols=["jobpost", "Title"]) ):
    
    arrSkill = skill
    
    ##if the skill param is a string seperated by " | ", turn it into an array
    if (isinstance(skill, list) != True):
        arrSkill = skill.split('|')
        skill = arrSkill
    
     
    ##Creates csv file
    with open('keggleJobs.csv', 'wb') as csvfile:
                   
    ##checks each job post
        for index, row in jobDes.iterrows():
            
            match = False;
            
            ##compares skill array with the post
            for s in arrSkill:
                if s.lower() in row['jobpost'].lower(): 
                    match = True;
            if (match):
                
                ##creates a csv file from arrTitle
                filewriter = csv.writer(csvfile, delimiter=',',quotechar='|', quoting=csv.QUOTE_MINIMAL)
                
                ##Verify right index goes with right job NOTE: indeces seem to be off by 2
                filewriter.writerow([index+2])
                
                ## This writes the jobe title as well
                ##filewriter.writerow([index+2, str(row['Title']).replace('\r', '').replace('\n', '')])
                
    dfKeg = pd.read_csv("keggleJobs.csv")
    return dfKeg 


##test 
s = "Visual|java|C++"## 456024
##s= ['Visual', 'java', 'C++']
## size is weird?? but the func return csv
print (getEntriesWithSkill(s).size )

