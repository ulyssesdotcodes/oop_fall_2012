package qimpp;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.PrintWriter;

import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;
import xtc.tree.Location;
import xtc.tree.Printer;

import xtc.util.Tool;

/** A generator for C++ class header files from a Java syntax tree
*  
*   This class handles inheritance, function declarations, and vtable generation.
*   @author QIMPP
*/
public class HeaderWriter{
  
  private Printer fileout;
  private GNode[] roots;
  private int indentDepth = 0;
  
  /** Constructor. Opens a new file called defined_classes.h
  *
  * The GNode passed in should contain a modified tree created by InheritanceManager.
  * HeaderGenerator should just format and print the fields and methods it is given,
  * determining which methods, order of fields, inheritance of fields etc. should not be
  * done here.
  *
  * A single header for all implemented classes is desirable as there may be type
  * cross-references in the defined types (Point may contain a Color, and Color may contain
  * a method returning a Point, so they must know about each other through
  * forward-declarations in C++)
  * 
  *@param roots The QimppClassDeclaration nodes for the classes we want to create a header for
  */
  public HeaderWriter(){

    try {
      fileout = new Printer(new PrintWriter("defined_classes.h"));
      
    } catch(Exception e) {
      System.err.println("Couldn't open file to write out!");
      System.exit(1);  
    }
  }
  
  /** Write out the header */
  public void generateHeader(GNode[] roots){

    this.roots = roots;
  
    for (int i = 0; i < roots.length; i++){
      writeTypeDeclaration(i);
      writeAlias(i);    
    }   
    
    for (int i = 0; i < roots.length; i++){
      writeStruct(i);
    }
  }
  
  /** Write out the internal names of the structs and vtables for each class 
  *
  * @param index the index of the class we are writing
  */
  private void writeTypeDeclaration(int index){
    fileout.setLevel(indentDepth);
    // ClassDeclaration field 1 is the name of the class
    fileout.p("struct __").p(roots[index].getString(1)).p(";\n");
    fileout.p("struct __").p(roots[index].getString(1)).p("_VT;\n").flush();
  }
  
  /** Write out the typedefs so pretty-printing class names is easier on the programmer and
  * the eyes 
  *
  * @param index the index of the class we are writing
  */
  private void writeAlias(int index){
    fileout.p("typedef __").p(roots[index].getString(1)).p("* ").p(roots[index].getString(1));
    fileout.p(";\n").flush();
  }
  
  /** Write out the struct definition for a given class, with all its newly defined methods */
  private void writeStruct(int index){
    fileout.p("struct __").p(roots[index].getString(1)).p("{\n");
    indentDepth++;
    fileout.setLevel(indentDepth);
    
    writeVptr(index);
    writeConstructor(index);
    writeFields(index);
    writeMethods(index);
    writeClass(index);
    writeVtable(index);
    
    indentDepth--;
    fileout.setLevel(indentDepth);
    fileout.p("}\n").flush();
  }
  
  private void writeVptr(int index){
    fileout.p("__").p(roots[index].getString(1)).p("_VT* __vptr;\n").flush();
  }
  
  private void writeConstructor(int index){
  
  }
  
  private void writeFields(int index){
  
  }
  
  private void writeMethods(int index){
  
  }
  
  private void writeClass(int index){
  
  }
  
  private void writeVtable(int index){
  
  }
}
