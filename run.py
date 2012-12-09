#/usr/bin/python
"""
A simple Python script that runs everything.
"""

import os
import re
import sys

# 1. run unit tests
os.system("python test/run_junit_tests.py")

# 2. run translator and run specified integration test
os.system("java qimpp.Qimpp -printCppAST " + sys.argv[1])

# 3. compile outputted C++ code
os.system("g++ ./output/java_lang.cc ./output/out.cc")

# 4. run executable
os.system("./a.out")


