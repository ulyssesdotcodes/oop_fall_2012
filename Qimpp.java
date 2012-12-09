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
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.FileOutputStream;
import java.io.OutputStream;

import xtc.lang.JavaFiveParser;

import xtc.parser.ParseException;
import xtc.parser.Result;

import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;
import xtc.tree.Printer;

import xtc.util.Tool;

import qimpp.SymbolTable;

/**
 * A translator from (a subset of) Java to (a subset of) C++.
 *
 * @author Robert Grimm
 * @version $Revision$
 */
public class Qimpp extends Tool {

  /** Symbol table. */
  SymbolTable symbolTable;

  File ccFile;
  File hFile;
  FileOutputStream ccOut;
  FileOutputStream hOut;
  Printer hPrinter;
  Printer ccPrinter;
  Printer terminal;

  /** Create a new translator. */
  public Qimpp() throws IOException {
    this.symbolTable = new SymbolTable();
    prepareStreams(); 
  }

  public String getName() {
    return "Java to C++ Translator";
  }

  public String getCopy() {
    return "(C) 2012 Qimpp";
  }

  public void init() {
    super.init();

    /**
     * Difference between "print" and "write" in this usage is *persistence*:
     * To "print" means to flush to console.
     * To "write" means to flush to a file.
     */
    runtime
      .bool("printJavaAST", "printJavaAST", false, "Print Java AST.")
      .bool("printCppAST", "printCppAST", false, "Print translated C++ AST")
      .bool("writeSource", "writeSource", true, "Write source to out");
  }

  public void prepareStreams() throws FileNotFoundException {
    this.ccFile         = new File(Constants.OUTPUT_IMPLEMENTATION_FILE);
    this.ccOut          = new FileOutputStream(ccFile);
    this.hFile          = new File(Constants.OUTPUT_HEADER_FILE);
    this.hOut           = new FileOutputStream(hFile);
    this.hPrinter       = new Printer(hOut);
    this.ccPrinter      = new Printer(ccOut);
    this.terminal       = new Printer(System.out);
  }
  
  public void prepare() {
    super.prepare();
  }

  public File locate(String name) throws IOException {
    File file = super.locate(name);
    if (Integer.MAX_VALUE < file.length()) {
      throw new IllegalArgumentException(file + ": file too large");
    }
    return file;
  }

  public Node parse(Reader in, File file) throws IOException, ParseException {
    JavaFiveParser parser =
      new JavaFiveParser(in, file.toString(), (int)file.length());
    Result result = parser.pCompilationUnit(0);
    return (Node)parser.value(result);
  }

  public void process(Node node) {
    if (runtime.test("printJavaAST")) {
      runtime.console().format(node).pln().flush();
    }

    // - While there are multiple files to read in, keep incorporating table.
    symbolTable.incorporate(node);
    
    Store store = new Store();
    store.decomposeJavaAST(node);
   
    ConstructFactory factory = new ConstructFactory(); 
    GNode cNode = 
      (GNode)factory.buildTranslationUnit(store.getPackage(), store.getMain());


    if (runtime.test("printCppAST")) {
      runtime.console().format(cNode).pln().flush(); 
    }

    if (runtime.test("writeSource")) {
      new HWriter(hPrinter).dispatch(cNode);
      new HWriter(terminal).dispatch(cNode);
      terminal.flush();
      hPrinter.flush();

      new CCWriter(ccPrinter).dispatch(cNode);
      new CCWriter(terminal).dispatch(cNode);
      terminal.flush();
      ccPrinter.flush();
    }

    //System.out.println("*** Translated ***");
  }

  /**
   * Run the translator with the specified command line arguments.
   *
   * @param args The command line arguments.
   */
  public static void main(String[] args) throws IOException {
    new Qimpp().run(args);
  }

}
