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

import java.util.Iterator;

import xtc.tree.LineMarker;
import xtc.tree.Node;
import xtc.tree.GNode;
import xtc.tree.Pragma;
import xtc.tree.Printer;
import xtc.tree.SourceIdentity;
import xtc.tree.Token;
import xtc.tree.Visitor;

import java.util.HashMap;
/**
 * Prints the proper template for arrays.
 *
 * @author QIMPP
 * Version 0.1
 */
 
public class ArrayTemplatePrinter extends Visitor {

  Printer printer;

  /**
   * Constructor
   * @param printer the printer for the implementation file
   */
  ArrayTemplatePrinter(Printer printer) {
    this.printer = printer;
    printer.register(this);
  }
  
  /**
   * Convert a non-underscored name to an underscored name
   * @param cppQualifiedName the fully-qualified classname delimited by ::
   */
  static String getUnderscoredName(String cppQualifiedName) {
    String[] nameParts = cppQualifiedName.split("::");
    StringBuilder underscoredName = new StringBuilder();

    for (int i = 0; i < nameParts.length - 1; i++) {
      underscoredName.append(nameParts[i]);
      underscoredName.append("::");
    }

    underscoredName.append("__");
    underscoredName.append(nameParts[nameParts.length - 1]);

    return underscoredName.toString();
  }

  /** 
   * Visit the class declaration, print the relevant info and go no deeper 
   *
   * @param n the node to visit.
   */
  public void visitClassDeclaration( GNode n ) {
    String name = n.getString(0);
    name = name.replaceAll("\\.", "::");
    GNode parentType = n.getGeneric(1).getGeneric(0).getGeneric(0);

    String parent = Disambiguator.getDotDelimitedName(parentType);
    parent = parent.replaceAll("\\.", "::");
    
    printer.pln().p("namespace __rt{").pln().incr();

    printer.indent().pln("template<>");
    printer.indent().p("java::lang::Class").p(" __rt::Array< ")
      .p(name).p(" >::__class() {").pln();
    printer.incr();
    
    printer.indent().p("static java::lang::Class k =").pln()
      .indent().p("new java::lang::__Class(literal(\"[L")
      .p(name.replace("::", ".")).p(";\"),").pln()
      .indent().p("Array< ").p(parent).p(" >::__class(),")
      .p(getUnderscoredName(name)).p("::__class());")
      .pln()
      .indent().p("return k;").pln()
      .indent().p("}").pln();

    printer.decr().decr().p("}").pln();
    printer.flush();
  }

  /** Visit the specified Node. */
	public void visit(Node n) {
		for (Object o : n) if (o instanceof Node) dispatch((Node)o);
	}
}
