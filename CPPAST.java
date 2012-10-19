/*
 * xtc - The eXTensible Compiler
 * Copyright (C) 2012 Robert Grimm
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
 * USA.
 */
package qimpp;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.PrintWriter;

import xtc.lang.JavaFiveParser;

import xtc.parser.ParseException;
import xtc.parser.Result;

import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;
import xtc.tree.Location;
import xtc.tree.Printer;

import xtc.util.Tool;

/**
 * A translator from (a subset of) Java to (a subset of) C++.
 *
 * @author QIMPP
 * @version $Revision$
 */
public class CPPAST {
  GNode CompilationUnit;
  ArrayList<GNode> classes; 
  
  public CPPAST(){
      CompliationUnit = GNode.Create("CompilationUnit");
      classPointers = new ArrayList<GNode> classes;
  }
  
  /*Methods to add:
   *GNode addClass(String name) - adds a class and a struct to the tree returns a GNode to the class
   *GNode addField(String name, Object type, GNode class) - adds a field to a class returns the GNode of the field
   *GNode addMethod(String name, Object returnType, GNode class) - adds a method to a class returns the method GNode
   *GNode addMethodInstruction(GNode instruction, GNode method) - adds instruction to method
   *GNode addMethodParameter(Object paramType, GNode method) - adds a parameter to method
   *
   *GNode getClass(String name) - gets a GNode to a class by it's name
   *GNode getMethod(String name, GNode* class) - gets a GNode to a method by it's name
   *GNode getField(String name, GNode* class) - gets a GNode to a Field by it's name
   *
   *void removeMethod(String name, GNode* class) - removes a method from a class
  */
  
  
}
