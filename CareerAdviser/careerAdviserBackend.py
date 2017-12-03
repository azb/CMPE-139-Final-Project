
# coding: utf-8

# In[3]:

import sys
print "Running ", sys.argv[0]
print "Number of arguments: ", len(sys.argv)
print "The arguments are: " , str(sys.argv)

f=open("resuts.csv", "a+")
f.truncate()
f.write(str(sys.argv[1]))

f.close()
print "Press Enter to Exit"
raw_input()


# In[ ]:



