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

public class ArrayTemplatePrinter extends Visitor{

  Printer printer;

  HashMap<String, Boolean> doneClass;
  
  /**
   * Constructor
   * @param printer the printer for the implementation file
   */
  ArrayTemplatePrinter( Printer printer ) {
    this.printer = printer;
    doneClass = new HashMap<String, Boolean>();
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
      underscoredName.append("::");
    }

    underscoredName.append("__");
    underscoredName.append(nameParts[nameParts.length - 1]);

    return underscoredName.toString();
  }

  /** Visit the class declaration, print the relevant info and go no deeper */
  public void visitClassDeclaration( GNode n ) {
    String name = n.getString(0);
    if (doneClass.get(name) == null) {
      doneClass.put(name, true);
      if (n.getProperty("ParentClassNode") != null){
        visitClassDeclaration((GNode)n.getProperty("ParentClassNode"));
      }
      name = name.replaceAll("\\.", "::");
      GNode parentType = n.getGeneric(1).getGeneric(0).getGeneric(0);

      String parent = Disambiguator.getDotDelimitedName(parentType);
      parent = parent.replaceAll("\\.", "::");
      
      printer.p("namespace __rt{").pln().incr();

      printer.indent().pln("template<>");
      printer.indent().p("java::lang::Class").p(" __rt::Array< ").p(name).p(" >::__class() {").pln();
      printer.incr();
      
      printer.indent().p("static java::lang::Class k =").pln()
        .indent().p("new java::lang::__Class(literal(\"[L")
        .p(name.replace("::", ".")).p(";\"),").pln()
        .indent().p("Array< ").p(parent).p(" >::__class(),")
        .p(getUnderscoredName(name)).p("::__class());")
        .pln()
        .indent().p("return k;").pln()
        .indent().p("}").pln();
        

      printer.decr().decr().p("}");
      printer.flush();
    }

    return;
  }

  /** Visit the specified Node. */
	public void visit(Node n) {
		for (Object o : n) if (o instanceof Node) dispatch((Node)o);
	}


}
