package qimpp;

import xtc.tree.Visitor;
import xtc.tree.Node;
import xtc.tree.GNode;

import xtc.tree.Printer;

/**
 * Writes the header file from the C++ AST.
 *
 * @author Qimpp
 */
public class HWriter extends Visitor {

  /** The printer for this C++ printer. */ 
  protected final Printer printer;

  /**
   * The flag for printing additional parentheses to avoid
   * gcc warnings.
   */
  public static final boolean EXTRA_PARENTHESES = true;

  /** The base precedence level. */
  public static final int PREC_BASE    = 0;

  /** The flag for any statement besides an if or if-else statement. */
  public static final int STMT_ANY     = 0;

  /** The flag for an if statement. */
  public static final int STMT_IF      = 1;
  
  /** The flag for an if-else statement. */
  public static final int STMT_IF_ELSE = 2;

  /**
   * The flag for whether to line up declarations and statements with their
   * source locations.
   */
  protected final boolean lineUp;

  /** The flag for whether we just printed a declaration. */
  protected boolean isDeclaration;

  /** The flag for whether we just printed a statement. */
  protected boolean isStatement;

  /** 
   * The flag for whether the current statement requires nesting or
   * for whether the current declaration is nested within a for 
   * statement.
   */
  protected boolean isNested;

  /**
   * The flag for whether this statement is the else clause of an
   * if-else statement.
   */
  protected boolean isIfElse;

  /** 
   * The flag for whether this declarator is a function definition.
   */
  protected boolean isFunctionDef;

  /** The flag for whether the current identifier is internal or not. */
  protected boolean isInternal;

  /** The operator precedence level for the current expression. */
  protected int precedence;


  /** 
   * Create a new C++ printer.
   *
   * @param printer The printer.
   */
  public HWriter(Printer printer) {
    this(printer, false); 
  }

  /**
   * Create a new C++ printer.
   *
   * @param printer The printer.
   * @param lineUp The flag for whether to line up declarations and
   *  statements with their source locations.
   */
  public HWriter(Printer printer, boolean lineUp) {
    this.printer = printer;
    this.lineUp  = lineUp;
    printer.register(this);
  }

  // ===========================================================================

  // TODO: Probably can remove some of this, as it's not relevant to the header.
  /**
   * Print an expression as a truth value. This method prints the
   * specified node. If that node represents an assignment expression and
   * {@link #EXTRA_PARENTHESES} is <code>true</code>, this method adds an
   * extra set of parentheses around the expression to avoid gcc warnings.
   *
   * @param n The node to print
   */ 
  protected void formatAsTruthValue(Node n) {
    if (GNode.cast(n).hasName("AssignmentExpression")) {
      printer.p('(');
      visit(n);
      printer.p(')');
    } else {
      visit(n);
    }
  }

  /** DOC */
  protected boolean startStatement(int kind, Node node) {
    if (isIfElse && ((STMT_IF == kind) || (STMT_IF_ELSE == kind))) {
      isNested = false;
    } else {
      if (lineUp) {
        printer.lineUp(node);
      } else {
        if (isDeclaration) {
          printer.pln();
        }
      }
      if (isNested) {
        printer.incr();
      }
    }
    
    boolean nested = isNested;
    isNested       = false;
    return nested;
  }

  /**
   * Prepare for a nested statement.
   *
   * @see #startStatement
   */
  protected void prepareNested() {
    isDeclaration = false;
    isStatement   = false;
    isNested      = true;
  }
  
  /**
   * End a statement.
   *
   * @see #startStatement
   *
   * @param nested The flag for whether the current statement is nested.
   */
  protected void endStatement(boolean nested) {
    if (nested) {
      printer.decr();
    }
    isDeclaration = false;
    isStatement   = true;
  }

  /**
   * Enter an expression context. The new context has the specified precedence
   * level.
   *
   * @see #exitContext(int)
   *
   * @param prec The precedence level for the expression context.
   * @return The previous precedence level.
   */
  protected int enterContext(int prec) {
    int old     = precedence;
    precedence  = prec;
    return old;
  }

  /**
  * Enter an expression context.  The new context is appropriate for
  * an operand opposite the associativity of the current operator.
  * For example, when printing an additive expression, this method
  * should be called before printing the second operand, as additive
  * operators associate left-to-right.
  *
  * @see #exitContext(int)
  * 
  * @return The previous precedence level.
  */
  protected int enterContext() {
    int old     = precedence;
    precedence += 1;
    return old;
  }

  /**
   * Exit an expression context.
   *
   * @see #enterContext(int)
   * @see #enterContext()
   *
   * @param prec The previous precedence level.
   */
  protected void exitContext(int prec) {
    precedence = prec;
  }

  /**
   * Start printing an expression at the specified operator precedence level.
   *
   * @see #endExpression(int)
   *
   * @param prec The expression's precedence level.
   * @return The previous precedence level.
   */
  protected int startExpression(int prec) {
    if (prec < precedence) {
      printer.p('(');
    }

    int old     = precedence;
    precedence  = prec;
    return old;
  }

  /**
   * Stop printing an expression.
   *
   * @see #startExpression(int)
   *
   * @param prec The previous precedence level.
   */
  protected void endExpression(int prec) {
    if (precedence < prec) {
      printer.p(')');
    }
    precedence = prec;
  }

  // ===========================================================================
  

  /** Visit the specified translation unit node. */
  public void visitCompilationUnit(GNode n) {
    // Reset the state.
    isDeclaration   = false;
    isStatement     = false;
    isNested        = false;
    isIfElse        = false;
    isFunctionDef   = false;
    precedence      = PREC_BASE;

    if (lineUp) printer.line(1);

    visit(n);
  }

  /** Visit the specified assignment expression node. */
  public void visitAssignmentExpression(GNode n) {
    int prec1 = startExpression(20);
    int prec2 = enterContext();
    visit(n.getNode(0));
    exitContext(prec2);

    printer.p(' ').p(n.getString(1)).p(' ');
    visit(n.getNode(2));
    endExpression(prec1);
  }





  public void visit(Node n) {
    for (Object o : n) {
      if (o instanceof Node) dispatch((Node)o);
    }
  }


}
