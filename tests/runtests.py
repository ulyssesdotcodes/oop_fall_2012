#!/usr/bin/python
"""
A simple Python testing suite, which uses qimmp.Translator
"""

import os
import re
import sys


#Figure out which tests we'll run
files = os.listdir(os.getcwd())

numtests = 0
testFilenames = []

for filename in files:
  if os.path.isfile(filename) and re.match("^Test.*\\.java$", filename):
    numtests += 1
    testFilenames.append(filename)

print "\n===================="
# Check if a specific test was specified on the command line
try:
  targetFile = sys.argv[1];
  if not targetFile in testFilenames:
    raise TypeError("No such test file")
  testFilenames = [targetFile]
except TypeError as e:
  print e
  exit(1)
except:
    pass # No first argument

# Change directory to the source root
os.chdir("../..")

# Loop through all test files, or if a single one is specified, do that
# one

for filename in testFilenames:

  print "\n\n----------------------------"
  print "Testing " + filename 
  print "----------------------------\n"


  os.system("pwd")  
  # Translate

  os.system( "java qimpp.QimppTranslator qimpp/tests/" + filename +" > translator.output" )

  # Compile

  compile_succeded = (0 == os.system("g++ out.cc java_lang.cc"))

  # Run test and put output into file
  os.system( "java qimpp.tests." + filename.split(".")[0] + " > java.output" ) 
  
  # Check compilation return code

  print "\n"

  if not compile_succeded:
    print "Failed to compile!"
  
  else:
    # Run output file
    os.system( "./a.out > cpp.output" )
    # Diff output files
    print "\n==============================="
    print "DIFF"
    print "==============================="
    os.system( "diff java.output cpp.output" )

  


