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
public class HeaderWriter extends Visitor{
  
  private Printer printer;
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
  
  // TODO: Need to change the HeaderWriter to take a GNode, instead of an array of GNodes
  
  public HeaderWriter(Printer printer) {
    this.printer = printer;
    printer.register(this);  
  }
  
  /** Write out the header */
  // public void generateHeader(Node n){
   // new Visitor() {
      public void visitCompilationUnit(GNode n){
        //pretty sure nothing needs to be done here
        visit(n);
      }

      public void visitDeclarations(GNode n){
        //nothing needs to be done within this node
        visit(n);
      }
	  
      public void visitDeclaration(GNode n){
        //right now, this is printing slightly out of order. change later?
        writeTypeDeclaration(n);
	      //writeAlias(n);
      }

      public void visitClasses(GNode n){
        //nothing needs to be done within this node
        visit(n);
      }

      public void visitClassDeclaration(GNode n){
	      writeStruct(n);
	      //writeVTStruct(n); 
        
      }

      public void visitFields(GNode n){
        visit(n);
      }

      public void visitFieldDeclaration(GNode n){
        visit(n);
      }

      public void visitImplementedMethods(GNode n){
        visit(n);
      }

      public void visitMethodDeclaration(GNode n){
        visit(n);
      }

      public void visit(GNode n){
        for (Object o : n) if (o instanceof Node) dispatch((Node)o);
      }
   // }.dispatch(n);
      
      /*
    writeDependencies();
    // Store all of the GNodes required to write the header in roots
    this.roots = roots;
  
    for (int i = 0; i < roots.length; i++){
      // ONLY COMMENTED OUT FOR TESTING PURPOSES
      //writeTypeDeclaration(i);
      writeAlias(i);    
      writeStruct(i);
      writeVTStruct(i);
      }   */
    
  //}
  
  /** Write out the dependencies for the header */
  //TODO: this method should probably do more. Not sure ATM.
  private void writeDependencies() {
    printer.p("#pragma once").pln();
    printer.p("#include \"java_lang.h\"").pln(); 
    printer.p("#include <stdint.h>").pln().pln().flush();
  }
  
  /** Write out the internal names of the structs and vtables for each class 
  *
  * @param index the index of the class we are writing
  */
  public void writeTypeDeclaration(GNode node){
    // ClassDeclaration field 1 is the name of the class
    
    printer.p("struct __").p(name(node)).p(";\n");
    printer.p("struct __").p(name(node)).p("_VT;\n").pln().flush();
  }
  
  /** Write out the typedefs so pretty-printing class names is easier on the programmer and
  * the eyes 
  *
  * @param node the node being examined
  */
  private void writeAlias(GNode node){
      printer.p("typedef __").p(name(node)).p("* ").p(name(node));
    printer.p(";\n").pln().flush();
  }
 
// ==================
// STRUCT STUFF //
// ===================

  /** Write out the struct definition for a given class, with all its newly defined methods 
  *  
  * @param node  the node being written
  */
  // Using java_lang.h as a basis, NOT skeleton.h
  private void writeStruct(GNode node){
    printer.p("struct __").p(name(node)).p(" {\n");
    printer.incr();

      writeVPtr(node);
      plnFlush();
//      writeFields(node);
//      plnFlush();      
      writeConstructor(node);
      plnFlush();
      writeMethods(node);
      plnFlush();
//      writeClass();
//      plnFlush();
//      writeVTable(node);
    
    printer.decr(); 
    
    printer.p("};\n").pln().flush();
  }

  
  private void writeVPtr(GNode node){
    indentOut().p("__").p(name(node)).p("_VT* __vptr;\n").flush();
  }
 
  /** 
   * The constructor.
   *
   * @param index the index of the class we are writing.
   */ 
  private void writeConstructor(GNode node){
    indentOut().p("__").p(name(node)).p("(");

    // Now for parameters:
    // Iterate through initializing types and fields. Use Type.

    // For now, assuming only Object, therefore no parameters in constructor.
    
    printer.p(");").flush();
  }
 

  /**
   * Write the fields that belong each type of object. For example, String
   * has <code>std::string data</code>, and Class has <code>String name</code>
   * and <code>Class parent</code>.
   *
   * @param index The index of the class we are writing.
   */
  private void writeFields(GNode node) {
    //Interate through the FieldDeclarations
   for(Iterator<Object> iter = node.getGeneric(2).iterator(); iter.hasNext();){
      Object objCurrent = iter.next();
      if(objCurrent == null || objCurrent instanceof String) continue;
      GNode current = (GNode) objCurrent;
      if(current.hasName("FieldDeclaration"))
        //For now just get the first field declared
        indentOut().p(Type.translate(current)).p(" ").p(current.getGeneric(2).getGeneric(0).getString(0)).p(";").pln();
    }
    
  }
  
  
  private void writeMethods(GNode node){
    //
  }
  
  private void writeClass(){
    indentOut().pln("static Class __class();").flush(); 
  }
  
  private void writeVTable(GNode node){
    indentOut().p("static ").p(name(node)).pln("_VT __vtable;").flush();
  }



// =======================
// VTABLE STRUCT STUFF
// ======================


  /** Write out the struct definition of a class's VTable 
  * @param i the index of the class we are writing
  */
  private void writeVTStruct(GNode node) {
    printer.p("struct __").p(name(node)).p("_VT {\n");
    printer.incr();

      // initialize __isa
      indentOut().p("Class __isa;");  
      plnFlush();
      writeObjectInheritedVTMethods(node);
      plnFlush();
      writeInheritedVTMethods(node);
      plnFlush();
      writeVTMethods(node);
      plnFlush();
      
      writeVTConstructor(node);
      printer.incr();
        plnFlush();
        // set __isa to the Class
        indentOut().p(": __isa(__").p(name(node)).p("::__class()),");
        plnFlush();
        writeObjectInheritedVTAddresses(node);
        plnFlush();
        writeInheritedVTAddresses(node);
        plnFlush();
        writeVTAddresses(node);
        printer.p(" {\n");  
      printer.decr();
      indentOut().p("}\n");
    printer.p("};\n").pln().flush();
  }

  /** Write out all the inherited methods of Object, since every class extends Object
   *
   * @param i the index of the class we are writing */
  private void writeObjectInheritedVTMethods(GNode node) {
    indentOut().p("int32_t (*hashCode)(").p(name(node)).p(");\n");
    indentOut().p("bool (*equals)(").p(name(node)).p(", Object);\n");
    indentOut().p("Class (*getClass)(").p(name(node)).p(");\n");
    indentOut().p("String (*toString)(").p(name(node)).p(");\n").flush();
  }

  /** Write out all the inherited methods of its superclass(es)
   * @param i the index of the class we are writing */
  // TODO: this
  private void writeInheritedVTMethods(GNode node) {

  }

  /** Write out all the classe's own methods
   * @param i the index of the class we are writing */
  // TODO: this
  private void writeVTMethods(GNode node) {
  
  }

  /** Write out the VT Constructor 
   * @param i the index of the class we are writing */
  private void writeVTConstructor(GNode node) {
    indentOut().p("__").p(name(node)).p("_VT()");
  }

  /** Write out all the inherited Object VT method addresses
   * @param i the index of the class we are writing */
  // TODO: not sure if this is exactly what we want
  private void writeObjectInheritedVTAddresses(GNode node) {
    indentOut().p("hashCode((int32_t(*)(").p(name(node)).p("))&__Object::hashCode),\n");
    indentOut().p("equals((bool(*)(").p(name(node)).p(",Object))&__Object::equals),\n");
    indentOut().p("getClass((Class(*)(").p(name(node)).p("))&__Object::getClass),\n");
    indentOut().p("toString((String(*)(").p(name(node)).p("))&__Object::toString)\n").flush();
  }

  /** Write out all the inherited VT addresses of the class' superclass(es)' methods
   * @param i the index of the class we are writing */
  // TODO: this
  private void writeInheritedVTAddresses(GNode node) {

  }

  /** Write out all the VT addresses of the class' own methods
   * @param i the index of the class we are writing */
  // TODO: this
  private void writeVTAddresses(GNode node) {

  }


// =======================
// OTHER SHIT (OTHERWISE KNOWN AS UTILITY METHODS)
// =======================

  private String name(GNode n) {
    String name = n.getString(1);
    return name;
      //.substring(1,name.length() - 1);
  }
  
  private Printer indentOut(){
    return printer.indent();
  }

  private Printer plnFlush(){
    return printer.pln().flush();
  }
}
