#!/usr/bin/python
"""
A simple Python demo suite, which uses qimmp.Translator
"""

import os
import re
import sys


def runVerbose(filename):

  print "\n\n----------------------------"
  print "Testing " + filename 
  print "----------------------------\n"


  os.system("pwd")
  # Translate

  os.system( "java qimpp.QimppTranslator qimpp/demo/" + filename +" > translator.output" )

  # Compile

  compile_succeded = (0 == os.system("g++ out.cc java_lang.cc"))

  # Run test and put output into file
  os.system( "java qimpp.demo." + filename.split(".")[0] + " > java.output" ) 
  
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

def runQuick(filenames):

  total, ok, fail_diff, fail_compile, fail_translate = 0, 0, 0, 0, 0
  
  for filename in filenames:
    total += 1
    print "Testing " + filename + ": " ,
    
    translate_succeeded = (0 == os.system( "java qimpp.QimppTranslator qimpp/demo/" + filename +" > translator.output 2> translator.err" ))
    
    if not translate_succeeded:
      print "FAIL - translation"
      fail_translate += 1
      continue
      
    compile_succeded = (0 == os.system("g++ out.cc java_lang.cc 1> gcc.output 2> gcc.err"))
    
    if not compile_succeded:
      print "FAIL - compilation"
      fail_compile += 1
      continue
      
    # Run output file
    os.system( "./a.out > cpp.output" )
      
    # Run test and put output into file
    os.system( "java qimpp.demo." + filename.split(".")[0] + " > java.output" )
    
    java_output = open("java.output").read()
    cpp_output = open("cpp.output").read()
    
    if (java_output == cpp_output):
      print "OK"
      ok += 1
      
    else:
      print "FAIL - diff"
      fail_diff += 1
  
  print """
  OK: %d/%d 
  FAIL -diff: %d | -compilation: %d | -translation: %d
  """ % (ok, total, fail_diff, fail_compile, fail_translate)

    
#Main

#Figure out which tests we'll run
files = os.listdir(os.getcwd())

numtests = 0
testFilenames = []
dontTest = ["RedPoint.java"]

for filename in files:
  if os.path.isfile(filename) and re.match(".*\\.java$", filename) and filename not in dontTest:
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

# Make sure all test files are built
os.system("make")
# Change directory to the source root
os.chdir("../..")
os.system("rm java.output cpp.output a.out out.cc out.h")

# Loop through all test files, or if a single one is specified, do that
# one
if len(testFilenames) == 1:
  runVerbose(testFilenames[0])
  
if len(testFilenames) > 1:
  runQuick(testFilenames)
