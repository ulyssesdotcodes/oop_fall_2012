#/usr/bin/python
"""
A simple Python script that runs all the JUnit tests in ./test/
"""

import os
import re

os.chdir("test")
files = os.listdir(os.getcwd())

tests = []
for filename in files:
  print re.match(r"(.+Test\.java)", filename), filename
  if os.path.isfile(filename) and re.match(r"(.+Test\.java)", filename):
    tests.append(filename)

count = 0
os.chdir("..")

for test in tests:
  print "\n\n----------------------------"
  print "Testing " + test
  print "----------------------------\n"
  
  # Run the tests (make sure to remove the ".java" extension
  os.system( "java org.junit.runner.JUnitCore qimpp." + test[:-5] )

  count += 1
  

print "Ran " + str(count) + " tests"
