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
  
  * TODO: Packages
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
    // Store all of the GNodes required to write the header in roots
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
    fileout.incr();
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
  // Using java_lang.h as a basis, NOT skeleton.h
  private void writeStruct(int index){
    fileout.p("struct __").p(roots[index].getString(1)).p("{\n");
    fileout.incr();
    
      writeVptr(index);
      plnFlush();
      writeFields(index);
      plnFlush();
      
      writeConstructor(index);
      plnFlush();
      writeMethods(index);
      plnFlush();
      writeClass(index);
      plnFlush();
      writeVtable(index);
    
    fileout.decr();
    
    //TODO: write struct __[Class]_VT
    
    fileout.p("};\n").flush();
  }
  
  private void writeVptr(int index){
    fileout.indent().p("__").p(roots[index].getString(1)).p("_VT* __vptr;\n").flush();
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
  
  private void writeMethods(int index){
    
  }
  
  private void writeClass(int index){
 		indentOut().pln("static Class __class();").flush(); 
  }
  
  private void writeVtable(int index){
 		indentOut().p("static ").p(roots[index].getString(1)).pln("_VT __vtable;").flush();
  }
  
  private Printer indentOut(){
    return fileout.indent();
  }
  
  private Printer plnFlush(){
    return fileout.pln().flush();
  }
}
