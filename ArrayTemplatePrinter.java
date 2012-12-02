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


public class ArrayTemplatePrinter extends Visitor{

  Printer printer;
  
  /**
   * Constructor
   * @param printer the printer for the implementation file
   */
  ArrayTemplatePrinter( Printer printer ) {
    this.printer = printer;
    printer.register(this);
  }
  
  //TODO: Figure out a better way to do this or centralize this method
  /**
   * Convert a non-underscored name to an underscored name
   * @param cppQualifiedName the fully-qualified classname delimited by ::
   */
  static String getUnderscoredName( String cppQualifiedName ){
    String[] nameParts = cppQualifiedName.split("::");
    StringBuilder underscoredName = new StringBuilder();

    for ( int i = 0; i < nameParts.length - 1; i++ ) {
      underscoredName.append(nameParts[i]);
    }

    underscoredName.append("__");
    underscoredName.append(nameParts[0]);

    return underscoredName.toString();
  }


  /** Visit the class declaration, print the relevant info and go no deeper */
  public void visitClassDeclaration( GNode n ) {
    String name = n.getString(0);
    String parent = n.getGeneric(0).getGeneric(0).getGeneric(0).getString(0);
    
    printer.pln("template<>");
    printer.p("java::lang::Class").p(" < ").p(name).p(" >::__class() {").pln();
    printer.incr();
    
    printer.indent().p("static java::lang::Class k =").pln()
      .p("new java::lang::__Class(literal(\"[L")
      .p(name.replace("::", ".")).p(";\"),")
      .p("Array< ").p(parent).p(" >::__class(),")
      .p(getUnderscoredName(name)).p("::__class());)")
      .pln()
      .p("return k;");

    printer.decr();
    printer.flush();

    return;
  }

  /** Visit the specified Node. */
	public void visit(Node n) {
		for (Object o : n) if (o instanceof Node) dispatch((Node)o);
	}


}
