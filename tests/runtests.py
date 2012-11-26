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

for filename in files:
  if os.path.isfile(filename) and re.match("^Test.*\\.java$", filename):
    numtests += 1

print "\n===================="
# Check if a specific test was specified on the command line
try:
  print "Selected test: " + sys.argv[1]
  testnumber = int(sys.argv[1])
except:
  testnumber = numtests + 1

if testnumber <= numtests and testnumber > 0:
  count = testnumber
  numtests = testnumber
else:
  count = 1

# Loop through all test files, or if a single one is specified, do that
# one

while count <= numtests:

  print "\n\n----------------------------"
  print "Testing " + "Test"+str(count)
  print "----------------------------\n"

  
  # Translate
  
  os.system( "java qimpp.QimppTranslator " + "Test"+str(count)+".java > /dev/null" )

  # Compile

  compile_succeded = (0 == os.system("g++ java_lang.cc out.cc"))

  # Run test and put output into file

  os.system( "java qimpp.tests.Test"+str(count) + " > java.output" ) 
  
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

  count += 1
  


