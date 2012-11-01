#/usr/bin/python
"""
A simple Python testing suite, which uses qimmp.Translator
"""

import os
import re


#Figure out which tests we'll run
files = os.listdir(os.getcwd())

numtests = 0

for filename in files:
  if os.path.isfile(filename) and re.match("^Test.*\\.java$", filename):
    numtests += 1

# Loop through all test files

count = 1
while count <= numtests:

  print "\n\n----------------------------"
  print "Testing " + "Test"+str(count)
  print "----------------------------\n"

  
  # Translate
  
  os.system( "java qimpp.QimppTranslator " + "Test"+str(count)+".java" )

  # Compile

  compile_succeded = not os.system("g++ java_lang.cc out.cc")

  # Run test and put output into file

  os.system( "java qimpp.tests.Test"+str(count) + " > java.output" ) 
  
  # Check compilation return code

  print "\n"

  if not compile_succeded:
    print "Failed to compile!"
  
  else:
    # Run output file

    # Run java test

    # Diff output files

    pass

  count += 1
  


