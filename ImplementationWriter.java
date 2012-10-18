

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

public class ImplementationWriter extends Visitor {

	/** 
	 * The flag for whether to line up declarations and statements with
	 * their source locations.
	 */
	protected final boolean lineUp;


	/**
	 * Create a new C++ printer.
	 *
	 * @param printer The printer.
	 */
	public CPlusPlusPrinter(Printer printer) {
		this(printer, false);
	}


	/** 
	 * Create a new C++ printer.
	 *
	 * @param printer The printer.
	 * @param lineUp Flag for whether to line up declarations and statements
	 *   with their source locations.
	 */
	public CPlusPlusPrinter(Printer printer, boolena lineUp) {
		this.printer = printer;
		this.lineUp  = lineUp;
		printer.register(this);
	}


	public void visitPragmaDirective(GNode n) {
		printer.p("#pragma").p(' ').pln(n.getNode(0));
	}

	public void visitIncludeDirective(GNode n) {

	}

	public void visitDefineDirective(GNode n) {

	}

	public void visitUsing(GNode n) {

	}

	public void visitNamespace(GNode n) {

	}

	public void visitClassDeclaration(GNode n) {

	}

	public void visitTypedefSpecifier(GNode n) {

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
			printer.p(' ').p(n.getNode(0));
		}
		printer.pln(';');
	}




}
