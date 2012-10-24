

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
		visit(n);
		printer.pln("int main() {}");
		printer.flush();
	}

	public void visitPragmaDirective(GNode n) {
		printer.p("#pragma ").pln(n.getString(0));
	}

	public void visitIncludeDirectives(GNode n) {
		visit(n);
		printer.pln();
	}

	public void visitQuotedForm(GNode n) {
		printer.p("#include \"").p(n.getString(0)).pln("\"");
	}

	public void visitAngleBracketForm(GNode n) {
		printer.p("#include <").p(n.getString(0)).pln(">");
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
			.pln("::__class() {")
			.p("return new java::lang::__Class(__rt::literal(")
			.p(this.currentClass).p("), ");
		visit(n.getGeneric(2));
		printer.p("::__class());").pln("}");

		// vtable
		printer.p("__").p(this.currentClass).p("_VT ")
			.p("__").p(this.currentClass).pln("::__vtable;");

		// class constructor
		printer.p("__").p(this.currentClass).p("::__")
			.p(this.currentClass)
			.pln("() : __vptr(&__vtable) {");
		visit(n);
		printer.pln("}");
	}

	public void visitParent(GNode n) {
		printer.p(n.getString(0));
	}

	public void visitImplementedMethods(GNode n) {
		visit(n);
	}

	/** Only visited in implemented methods */
	public void visitMethodDeclaration(GNode n) {
		visit(n.getGeneric(1)); // return type
		printer.p(" __").p(this.currentClass).p("::");
		printer.p("::").p(n.getString(0)); // method name
		visit(n.getGeneric(2)); // parameters
		printer.p(" {");
		visit(n.getGeneric(3)); // block
	}

	public void visitFrom(GNode n) {
		printer.p(n.getString(0));	
	}

	public void visitExpression(GNode n) {
		visit(n.getGeneric(0));
		printer.p(' ').p(n.getString(1)).p(' ');
		visit(n.getGeneric(2));
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
		printer.p(n.getString(0));
	}

	public void visitFormalParameter(GNode n) {
		visit(n.getGeneric(1));
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
			visit(n.getNode(0));
		}
		printer.pln(';');
	}

	public void visitStringConcatExpression(GNode n) {
		printer.p("new java::lang::__String(");
		for (Iterator<?> iter = n.iterator(); iter.hasNext(); ) {
			visit((Node)iter.next());
			printer.p("->data");
			if (iter.hasNext()) {
				printer.p(" + ");
			}
		}

	}
	
	

	public void visit(Node n) {
		for (Object o : n) if (o instanceof Node) dispatch((Node)o);
	}
}
