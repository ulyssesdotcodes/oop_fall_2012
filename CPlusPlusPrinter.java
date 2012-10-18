

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
 * A pretty printer for C++.
 *
 * @author QIMPP
 */

public class CPlusPlusPrinter extends Visitor {

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

	public void visitReturnStatement(GNode n) {

	}

	



}
