

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


  protected final Printer printer;

	/**
	 * The current class in the traversal.
	 */
	protected String currentClass;
  protected GNode mainMethod;

	/** 
	 * Create a new C++ printer.
	 *
	 * @param printer The printer.
	 * @param lineUp Flag for whether to line up declarations and statements
	 *   with their source locations.
	 */
	public ImplementationPrinter(Printer printer) {
		this.printer = printer;
		printer.register(this);
	}

	public void visitCompilationUnit(GNode n) {
		printer.pln("#include \"out.h\"\n");
		visit(n);
		writeMainMethod();
		printer.flush();
	}


	public void visitDefineDirective(GNode n) {

	}


	public void visitUsing(GNode n) {

	}

	public void visitNamespace(GNode n) {

	}

	public void visitClassDeclaration(GNode n) {
		this.currentClass = n.getString(0);

		// .class
		printer.p("java::lang::Class __").p(this.currentClass)
			.pln("::__class() {").incr();
    indentOut()
      .p("return new java::lang::__Class(__rt::literal(\"")
			.p(this.currentClass).p("\"), ");
		dispatch(n.getGeneric(1));
		printer.pln("::__class());").pln("}");

		// vtable
		printer.p("__").p(this.currentClass).p("_VT ")
			.p("__").p(this.currentClass).pln("::__vtable;");

		visit(n.getGeneric(2));
		
		//visit(n.getGeneric(3));
		
		visit(n.getGeneric(4));
		printer.flush();
	}
	
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
	}

	public void visitParent(GNode n) {
		visit(n);
	}

	public void visitImplementedMethods(GNode n) {
		visit(n);
	}

	/** Only visited in implemented methods */
	public void visitMethodDeclaration(GNode n) {
		if (n.getString(0).equals("main")) {
      mainMethod = n;
      return;
    }
    dispatch(n.getGeneric(1)); // return type
		printer.p(" __").p(this.currentClass);
		printer.p("::").p(n.getString(0)); // method name
		dispatch(n.getGeneric(2)); // parameters
		printer.pln(" {");
    printer.incr();
    indentOut();
		dispatch(n.getGeneric(3)); // block
    printer.decr();
		printer.pln("}");
		printer.flush();
	}
	
	public void visitReturnType(GNode n) {
    try {
      if (n.get(0) != null) {
        visit(n);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
	}

	public void visitFrom(GNode n) {
		visit(n);
	}

	public void visitExpression(GNode n) {
		dispatch(n.getGeneric(0));
		printer.p(' ').p(n.getString(1)).p(' ');
		dispatch(n.getGeneric(2));
		printer.pln(";");
	}

	public void visitPrimaryIdentifier(GNode n) {
		printer.p(n.getString(0));
	}

	public void visitInstance(GNode n) {
		printer.p("__this->");
		visit(n);
	}

	public void visitStringLiteral(GNode n) {
		printer.p("__rt::literal(").p(n.getString(0)).p(')');
	}

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

	public void visitType(GNode n) {
		visit(n);
	}

  public void visitPrimitiveType(GNode n) {
    printer.p(n.getString(0));
  }

  public void visitQualifiedIdentifier(GNode n) { 
		for (Iterator<?> iter = n.iterator(); iter.hasNext(); ) {
			printer.p((String)iter.next());
			if (iter.hasNext()) {
				printer.p("::");
			}
		}
  }

	public void visitFormalParameter(GNode n) {
		dispatch(n.getGeneric(1));
		printer.p(' ').p(n.getString(0));
	}

	public void visitBreakStatement(GNode n) {
		printer.pln("break;");
	}

	public void visitContinueStatement(GNode n) {
		printer.pln("continue;");
	}

	public void visitReturnStatement(GNode n) {
		printer.p("return");
		if (null != n.getNode(0)) {
			printer.p(' ');
			dispatch(n.getNode(0));
		}
		printer.pln(';');
	}

  public void visitPrintExpression(GNode n) {
    printer.p("cout <<");
    visit(n);
    printer.pln(";");
  }

  public void visitOption(GNode n) {
    // Do nothing for now
  }

  public void visitArguments(GNode n) {
    visit(n); // one string literal for now
  }

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

	public void visit(Node n) {
		for (Object o : n) if (o instanceof Node) dispatch((Node)o);
	}

  public void writeMainMethod() {
    printer.p("int main() {").incr();
    indentOut();
    visit(mainMethod);
    printer.decr().pln("return 0;");
    printer.pln("}");
  }

  /** Utility methods **/

  private Printer indentOut() {
    return printer.indent();
  }

}
