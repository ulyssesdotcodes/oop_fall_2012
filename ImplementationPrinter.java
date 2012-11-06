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

/**
 * A pretty printer for C++ implementation.
 *
 * @author QIMPP
 */
public class ImplementationPrinter extends Visitor {

  /**
   * The printer.
   */
  protected final Printer printer;

	/**
	 * The current class in the traversal.
	 */
	protected String currentClass;

  /**
   * The main method that is printed at the end of the C++ file.
   */
  protected GNode mainMethod;

	/** 
	 * Create a new C++ printer.
	 *
	 * @param printer The printer.
	 */
	public ImplementationPrinter(Printer printer) {
		this.printer = printer;
		printer.register(this);
	}

  /** Visit the specified compilation unit node. */
	public void visitCompilationUnit(GNode n) {
		printer.p("#include \"out.h\"\n");
		printer.p("#include <iostream>\n");
    printer.p("#include <sstream>\n");
    printer.pln();
    visit(n);
    		printer.flush();
	}

  /** Visit the specified define preprocessing directive node. */
	public void visitDefineDirective(GNode n) {
    // Do nothing for now.
	}

  /** Visit the specified using preprocessing node. */
	public void visitUsing(GNode n) {
    // Do nothing for now.
	}

  /** Visit the specified namespace node. */
	public void visitNamespace(GNode n) {
    // Do nothing for now.
	}

  /** Visit the specified class declaration node. */
	public void visitClassDeclaration(GNode n) {
		this.currentClass = n.getString(0);

		// .class
		printer.p("java::lang::Class __").p(this.currentClass)
			.pln("::__class() {");
    printer.incr();
    indentOut()
      .p("return new java::lang::__Class(__rt::literal(\"")
			.p(this.currentClass).p("\"), ");
		dispatch(n.getGeneric(1));
		printer.pln("::__class());").pln("}\n");

		// vtable
		printer.p("__").p(this.currentClass).p("_VT ")
			.p("__").p(this.currentClass).pln("::__vtable;\n");
    
    printer.decr();
		visit(n.getGeneric(2));
		
		//visit(n.getGeneric(3));
		
		visit(n.getGeneric(4));
		printer.flush();
    printer.pln();
	}

  /** Visit the specified constructor declaration node. */  
	public void visitConstructorDeclaration(GNode n){
	  // class constructor
	  printer.p("__").p(this.currentClass).p("::__")
			.p(this.currentClass)
			.pln("() : __vptr(&__vtable) {");
    printer.incr();
    indentOut();
	  dispatch(n.getGeneric(1));
    printer.decr();
		printer.pln("}");
    printer.pln();
	}

  /** Visit the specified parent class node. */
	public void visitParent(GNode n) {
		visit(n);
	}

  /** Visit the specified implemented methods node. */
	public void visitImplementedMethods(GNode n) {
		visit(n);
	}

	/** 
   * Visit the specified method declaration node.
   * Only visited in implemented methods.
   */
	public void visitMethodDeclaration(GNode n) {
    boolean inMain = false;

  	if (n.getString(0).equals("main")) {
        mainMethod = n;
        inMain = true;
    }

    dispatch(n.getGeneric(1)); // return type

    if (!inMain) {
      printer.p(" __").p(this.currentClass);
      printer.p("::").p(n.getString(0)); // method name  
      dispatch(n.getGeneric(2)); // parameters
    } else {
      printer.p(" main(int argc, char** argv)"); // method name
    }
    dispatch(n.getGeneric(3)); // block
		printer.flush();
	}

  public void visitBlock(GNode n) {
    printer.pln(" {");
    printer.incr();
    indentOut();
    visit(n); // block
    printer.decr();
    printer.pln("}\n");
  }

  /** Visit the specified return type node. */  
	public void visitReturnType(GNode n) {
    try {
      if (n.get(0) != null) {
        visit(n);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
	}

  /** Visit the specified from class node. */
	public void visitFrom(GNode n) {
		visit(n);
	}

  /** Visit the specified expression node. */
	public void visitExpression(GNode n) {
		
    dispatch(n.getGeneric(0));
		printer.p(' ').p(n.getString(1)).p(' ');
		dispatch(n.getGeneric(2));
		printer.pln(";");
	}

  /** Visit the specified primary identifier node. */
	public void visitPrimaryIdentifier(GNode n) {
		printer.p(n.getString(0));
	}

  /** Visit the specified instance node. */
	public void visitInstance(GNode n) {
		printer.p("__this->");
		visit(n);
	}

  /** Visit the specified string literal node. */
	public void visitStringLiteral(GNode n) {
		printer.p("__rt::literal(").p(n.getString(0)).p(')');
	}

  /** Visit the specified formal parameters node. */
	public void visitFormalParameters(GNode n) {
		printer.p('(').p(this.currentClass).p(" __this");
		for (Iterator<?> iter = n.iterator(); iter.hasNext(); ) {
			if (iter.hasNext()) {
				printer.p(", ");
			}
			printer.p(((Node)iter.next()).getString(0));
		}
		printer.p(')');
	}

  /** Visit the specified type node. */
	public void visitType(GNode n) {
		visit(n);
	}

  /** Visit the specified primitive type node. */
  public void visitPrimitiveType(GNode n) {
    printer.p(n.getString(0));
  }

  /** Visit the specified qualified identifier node. */
  public void visitQualifiedIdentifier(GNode n) { 
		for (Iterator<?> iter = n.iterator(); iter.hasNext(); ) {
			printer.p((String)iter.next());
			if (iter.hasNext()) {
				printer.p("::");
			}
		}
  }

  /** Visit the specified formal parameter node. */
	public void visitFormalParameter(GNode n) {
		dispatch(n.getGeneric(1));
		printer.p(' ').p(n.getString(0));
	}

  /** Visit the specified break statement node. */
	public void visitBreakStatement(GNode n) {
		printer.pln("break;\n");
	}

  /** Visit the specified continue statement node. */
	public void visitContinueStatement(GNode n) {
		printer.pln("continue;\n");
	}

  /** Visit the specified return statement node. */
	public void visitReturnStatement(GNode n) {
		printer.p("return");
		if (null != n.getNode(0)) {
			printer.p(' ');
			dispatch(n.getNode(0));
		}
		printer.p(";\n");
	}

  /** Visit the specified print expression node. */
  public void visitPrintExpression(GNode n) {
    printer.p("cout <<");
    visit(n);
    printer.pln(";\n");
  }

  /** Visit the specified option node. */
  public void visitOption(GNode n) {
    // Do nothing for now
  }

  /** Visit the specified arguments node. */
  public void visitArguments(GNode n) {
    visit(n); // one string literal for now
  }

  /** Visit the specified string concatination expression node. */
	public void visitStringConcatExpression(GNode n) {
		printer.p("new java::lang::__String(");
		for (Iterator<?> iter = n.iterator(); iter.hasNext(); ) {
			dispatch((Node)iter.next());
			printer.p("->data");
			if (iter.hasNext()) {
				printer.p(" + ");
			}
		}
	}

  /** Visit the specified Node. */
	public void visit(Node n) {
		for (Object o : n) if (o instanceof Node) dispatch((Node)o);
	}



  /** Utility methods **/

  private Printer indentOut() {
    return printer.indent();
  }

}
