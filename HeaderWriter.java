package qimpp;
import qimpp.Type;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.PrintWriter;
import java.util.Iterator;

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
  *
  * Useful Notes:
  *
  * ClassBody is contained in roots[index].getGeneric(2)
  * Use .indent() to indent when needed. The Printer doesn't do it automatically. 
  * Use .incr() to increase indentation, and .decr() to decrease intentation.
  * ATTN: must call .incr() before you can call .indent()! Otherwise .indent() will do nothing 
  * TODO: Packages
  * 
  *@param roots The QimppClassDeclaration nodes for the classes we want to create a header for
  */
  public HeaderWriter(){

    try {
      fileout = new Printer(new PrintWriter("testfiles/out.h"));
      
    } catch(Exception e) {
      System.err.println("Couldn't open file to write out!");
      System.exit(1);  
    }
  }
  
  /** Write out the header */
  public void generateHeader(GNode[] roots){
    writeDependencies();
    // Store all of the GNodes required to write the header in roots
    this.roots = roots;
  
    for (int i = 0; i < roots.length; i++){
      writeTypeDeclaration(i);
      writeAlias(i);    
      writeStruct(i);
      writeVTStruct(i);
    }   
    
  }
  
  /** Write out the dependencies for the header */
  //TODO: this method should probably do more. Not sure ATM.
  private void writeDependencies() {
    fileout.p("#pragma once").pln();
    fileout.p("#include \"java_lang.h\"").pln(); 
    fileout.p("#include <stdint.h>").pln().pln().flush();
  }
  
  /** Write out the internal names of the structs and vtables for each class 
  *
  * @param index the index of the class we are writing
  */
  private void writeTypeDeclaration(int index){
    // ClassDeclaration field 1 is the name of the class
    fileout.p("struct __").p(roots[index].getString(1)).p(";\n");
    fileout.p("struct __").p(roots[index].getString(1)).p("_VT;\n").pln().flush();
  }
  
  /** Write out the typedefs so pretty-printing class names is easier on the programmer and
  * the eyes 
  *
  * @param index the index of the class we are writing
  */
  private void writeAlias(int index){
    fileout.p("typedef __").p(roots[index].getString(1)).p("* ").p(roots[index].getString(1));
    fileout.p(";\n").pln().flush();
  }
 
// ==================
// STRUCT STUFF //
// ===================

  /** Write out the struct definition for a given class, with all its newly defined methods 
  * @param index the index of the class we are writing
  */
  // Using java_lang.h as a basis, NOT skeleton.h
  private void writeStruct(int index){
    fileout.p("struct __").p(roots[index].getString(1)).p(" {\n");
    fileout.incr();

      writeVPtr(index);
      plnFlush();
      writeFields(index);
      plnFlush();
      
      writeConstructor(index);
      plnFlush();
      writeObjectInheritedMethods(index);
      plnFlush();
      writeMethods(index);
      plnFlush();
      writeClass(index);
      plnFlush();
      writeVTable(index);
    
    fileout.decr(); 
    
    fileout.p("};\n").pln().flush();
  }

  
  private void writeVPtr(int index){
    indentOut().p("__").p(roots[index].getString(1)).p("_VT* __vptr;\n").flush();
  }
 
  /** 
   * The constructor.
   *
   * @param index the index of the class we are writing.
   */ 
  private void writeConstructor(int index){
    indentOut().p("__").p(roots[index].getString(1)).p("(");

    // Now for parameters:
    // Iterate through initializing types and fields. Use Type.

    // For now, assuming only Object, therefore no parameters in constructor.
    
    fileout.p(");").flush();
  }
 

  /**
   * Write the fields that belong each type of object. For example, String
   * has <code>std::string data</code>, and Class has <code>String name</code>
   * and <code>Class parent</code>.
   *
   * @param index The index of the class we are writing.
   */
  private void writeFields(int index) {
    //Interate through the FieldDeclarations
    for(Iterator<Object> iter = roots[index].getGeneric(2).iterator(); iter.hasNext();){
      Object objCurrent = iter.next();
      if(objCurrent == null || objCurrent instanceof String) continue;
      GNode current = (GNode) objCurrent;
      if(current.hasName("FieldDeclaration"))
        //For now just get the first field declared
        indentOut().p(Type.translate(current)).p(" ").p(current.getGeneric(2).getGeneric(0).getString(0)).p(";").pln();
    }
    
  }
  
  private void writeObjectInheritedMethods(int i) {
    indentOut().p("static int32_t hashCode(").p(name(i)).p(");\n");
    indentOut().p("static bool equals(").p(name(i)).p(", Object);\n");
    indentOut().p("static Class getClass(").p(name(i)).p(");\n");
    indentOut().p("static String toString(").p(name(i)).p(");\n").flush();
  }

  private void writeMethods(int index){
    
  }
  
  private void writeClass(int index){
    indentOut().pln("static Class __class();").flush(); 
  }
  
  private void writeVTable(int index){
    indentOut().p("static ").p(roots[index].getString(1)).pln("_VT __vtable;").flush();
  }



// =======================
// VTABLE STRUCT STUFF
// ======================


  /** Write out the struct definition of a class's VTable 
  * @param i the index of the class we are writing
  */
  private void writeVTStruct(int i) {
    fileout.p("struct __").p(roots[i].getString(1)).p("_VT {\n");
    fileout.incr();

      indentOut().p("Class __isa;");  
      plnFlush();
      writeObjectInheritedVTMethods(i);
      plnFlush();
      writeInheritedVTMethods(i);
      plnFlush();
      writeVTMethods(i);
      plnFlush();
      
      indentOut().p("__").p(name(i)).p("_VT()");
      fileout.incr();
        plnFlush();
        indentOut().p(": __isa(__").p(name(i)).p("::__class()),");
        plnFlush();
        writeObjectInheritedVTAddresses(i);
        plnFlush();
        writeInheritedVTAddresses(i);
        plnFlush();
        writeVTAddresses(i);
        fileout.p(" {\n");  
      fileout.decr();
      indentOut().p("}\n");
    fileout.p("};\n").pln().flush();
  }

  /** Write out all the inherited methods of Object, since every class extends Object
   * @param i the index of the class we are writing */
  private void writeObjectInheritedVTMethods(int i) {
    indentOut().p("int32_t (*hashCode)(").p(name(i)).p(");\n");
    indentOut().p("bool (*equals)(").p(name(i)).p(", Object);\n");
    indentOut().p("Class (*getClass)(").p(name(i)).p(");\n");
    indentOut().p("String (*toString)(").p(name(i)).p(");\n").flush();
  }

  /** Write out all the inherited methods of its superclass(es)
   * @param i the index of the class we are writing */
  // TODO: this
  private void writeInheritedVTMethods(int i) {

  }

  /** Write out all the classe's own methods
   * @param i the index of the class we are writing */
  // TODO: this
  private void writeVTMethods(int i) {

  }

  /** Write out all the inherited Object VT addresses
   * @param i the index of the class we are writing */
  // TODO: not sure if this is exactly what we want
  private void writeObjectInheritedVTAddresses(int i) {
    indentOut().p("hashCode((int32_t(*)(").p(name(i)).p("))&__Object::hashCode),\n");
    // class must override equals()
    indentOut().p("equals(&__").p(name(i)).p("::equals),\n");
    indentOut().p("getClass(Class(*)(").p(name(i)).p(")&__Object::getClass),\n");
    // class must override toString()
    indentOut().p("toString(&__").p(name(i)).p("::toString)\n").flush();
  }

  /** Write out all the inherited VT addresses of the class' superclass(es)
   * @param i the index of the class we are writing */
  // TODO: this
  private void writeInheritedVTAddresses(int i) {

  }

  /** Write out all the VT addresses of the class' own methods
   * @param i the index of the class we are writing */
  // TODO: this
  private void writeVTAddresses(int i) {

  }


// =======================
// OTHER SHIT
// =======================

  private String name(int index) {
    return roots[index].getString(1);
  }
  
  private Printer indentOut(){
    return fileout.indent();
  }

  private Printer plnFlush(){
    return fileout.pln().flush();
  }
}
