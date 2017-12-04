
# coding: utf-8

# In[3]:

import sys
print "Running ", sys.argv[0]
print "Number of arguments: ", len(sys.argv)
print "The arguments are: " , str(sys.argv)

#write to results file for jobs results from KNN
f=open("results.csv", "a+")
f.truncate()
#f.write(str(sys.argv[1])+" ")
f.write("Software Engineer at Google,175000,Lead Systems Engineer at Lockheed Martin,170000,Senior Software Engineer at Apple,165000,Content Manager at Microsoft,155000,Senior Hacker at CIA,145000")
f.close()

#write to results file for regression analysis of skills
f=open("results2.csv", "a+")
f.truncate()
f.write("C++,75000,Python,74000,C#,73000,Data Mining,72000,Java,71000,Airplane Mechanic,44000,Car Driver,25000")
#f.write(str(sys.argv[1])+" ")
#f.write(str(sys.argv[2]))
f.close()

print "Press Enter to Exit"
raw_input()


# In[ ]:



