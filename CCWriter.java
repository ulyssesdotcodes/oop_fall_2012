package qimpp;

import xtc.tree.Visitor;
import xtc.tree.Node;
import xtc.tree.GNode;
import xtc.tree.Printer;
import xtc.tree.Token;

import java.util.Iterator;

/**
 * 
 *
 * @author Qimpp
 */
public class CCWriter extends Visitor {

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
  public CCWriter(Printer printer) {
    this(printer, false); 
  }

  /**
   * Create a new C++ printer.
   *
   * @param printer The printer.
   * @param lineUp The flag for whether to line up declarations and
   *  statements with their source locations.
   */
  public CCWriter(Printer printer, boolean lineUp) {
    this.printer = printer;
    this.lineUp  = lineUp;
    printer.register(this);
  }

  // ===========================================================================

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
  public void visitTranslationUnit(GNode n) {
    // Reset the state.
    isDeclaration   = false;
    isStatement     = false;
    isNested        = false;
    isIfElse        = false;
    isFunctionDef   = false;
    precedence      = PREC_BASE;

    if (lineUp) printer.line(1);
    printer.pln("// =========================================================")
      .pln("//               .cc Implementation file              ")
      .pln("// ==========================================================")
      .pln();

    printer.pln("#include \"out.h\"").pln();

    visit(n);
  }

  public void visitMainMethod(GNode n) {
    printer.pln("// main method!");
    printer.pln("int main() {").incr().indent()
      .p(n.getString(0)).pln(';')
      .indent().pln("return 0;").decr().pln('}');
  }


  // CONSTRUCTOR ===============================================================
  
  /** Visit the specified constructor node. */
  public void visitConstructor(GNode n) {
    printer.p(n.getString(0)).p('(');
  
    // constructor arguments
    dispatch(n.getGeneric(1));
    
    printer.pln(')').indent()
      .p(" : __vptr(&__vtable)");
    if (n.getGeneric(2).size() == 0) {
      printer.p(' ');
    } else { printer.p(',').p(' '); }
 
    // object initializations
    dispatch(n.getGeneric(2));

    printer.pln("{}").pln(); 
  }

  /** Visit the specififed constructor arguments node. */
  public void visitConstructorArguments(GNode n) {
    for (Iterator<?> iter = n.iterator(); iter.hasNext(); ) {
      printer.p((String)iter.next());
      if (iter.hasNext()) {
        printer.p(',').p(' ');
      }
    }
  }

  /** Visit the specified constructor initializations node. */
  public void visitConstructorInitializations(GNode n) {
    for (Iterator<?> iter = n.iterator(); iter.hasNext(); ) {
      String fieldName = (String)iter.next();
      printer.p(fieldName).p('(').p(fieldName).p(')');
      if (iter.hasNext()) {
        printer.p(',').p(' ');
      } else { printer.p(' '); }
    }
  }


  /** Visit the specified methods node. */
  public void visitMethods(GNode n) {
    visit(n);

    printer.pln("// Internal accessor for this class.")
      .p("::java::lang::Class").p(' ')
      .p(n.getString(0)).p("()").p(' ').pln('{')
      .incr().indent();
    printer.pln("static ::java::lang::Class k =");
    printer.incr().indent().p("new ::java::lang::__Class(__rt::literal")
      .p('(').p('"').p(n.getString(1)).p('"').p(')').p(',').p(' ')
      .p(n.getString(2)).p("()").p(')').pln(';').decr();
    printer.indent().pln("return k;").decr();
    printer.pln('}').pln();

    printer.p("// The vtable for ").p(n.getString(1)).p('.')
      .p(' ').pln("Note that this");
    printer.pln("// definition invokes the default no-arg vtable constructor.");
    printer.p(n.getString(3)).p("_VT").p(' ').p(n.getString(3))
      .p("::__vtable").pln(';').pln().pln();

  }

  /** Visit the specified method declaration node. */
  public void visitMethodDeclaration(GNode n) {
    printer.p(n.getString(0)).p(' ').p(n.getString(1)).p('(');
    dispatch(n.getGeneric(2)); // FormalParameters
    printer.p(')').p(' ').pln('{');
    dispatch(n.getGeneric(3)); // MethodBody
    printer.pln('}').pln();
  }

  /** Visit the formal parameters node. */
  public void visitFormalParameters(GNode n) {
    for (Iterator<?> iter = n.iterator(); iter.hasNext(); ) {
      String parameter = (String)iter.next();
      printer.p(parameter);
      if (iter.hasNext()) {
        printer.p(',').p(' ');
      }
    }
  }

  // BLOCK =====================================================================

  String primary;

  /** Visit the call expression node. */
  public void visitCallExpression(GNode n) {
    primary = n.getGeneric(0).getString(0);

    printer.p(n.getNode(0));
    printer.p("->__vptr->");
    printer.p(n.getString(2));
    printer.p('(');

    printer.p(n.getNode(3));

    printer.p(')').pln(';');
  }

  boolean inDeclarator = false;

  /** Visit declarator node. */
  public void visitDeclarator (GNode n) {
    inDeclarator = true;
    printer.p(n.getString(0));
    if (null != n.get(1)) {
      printer.p(n.getNode(1));
    }
    if (null != n.get(2)) {
      printer.p(" = ").p(n.getNode(2));
    }
    inDeclarator = false;
  }

  /** Visit primitive type node. */
  public void visitPrimitiveType(GNode n) {
    printer.p(new PrimitiveType(n.getString(0)).qualifiedName());
  }

  /** Visit integer literal node. */
  public void visitIntegerLiteral(GNode n) {
    printer.p(n.getString(0));
  }

  /** Visit the specified string literal node. */
  public void visitStringLiteral(GNode n) {
    printer.p("__rt::literal(");
    printer.p(n.getString(0));
    printer.p(')');
  }

  /** Visit primary identifier node. */
  public void visitPrimaryIdentifier(GNode n) {
    printer.p(n.getString(0).replace("\\.", "::"));
  }


  /** Visit qualified identifier node. */
  public void visitQualifiedIdentifier(GNode n) {
    if (inDeclarator) {
      printer.p("__" + n.getString(0));
    } else {
      printer.p(n.getString(0));
    }
  }

  /** Visit return statement node. */
  public void visitReturnStatement(GNode n) {
    printer.p("return");
    if (null != n.getNode(0)) {
      printer.p(' ');
      dispatch(n.getNode(0));
    }
    printer.p(";\n");
  }

  /** Visit print expression node. */
  public void visitPrintExpression(GNode n) {
    printer.p("std::cout << ");
    visit(n);
    printer.pln(" << std::endl;");  
  }


  /** Visit arguments node. */
  public void visitArguments(GNode n) {
    if (null != primary) {
      printer.p(primary);
    }
    for (Iterator<?> iter = n.iterator(); iter.hasNext(); ) {
      if (iter.hasNext()) {
        printer.p(", ");
      }      
      dispatch((Node)iter.next());
    }
  }

  /** Visit field declaration node. */
  public void visitFieldDeclaration(GNode n) {
    printer.indent().p(n.getNode(0)).p(n.getNode(1)).p(' ').p(n.getNode(2)).
      p(';').pln();
  }
  
  /** Visit new class expression node. */
  public void visitNewClassExpression(GNode n) {
    printer.p(" new ");
    dispatch(n.getGeneric(2));
    printer.p("(");
    dispatch(n.getGeneric(3));
    printer.p(")");
  }

  /** Visit the runtime node. */
  public void visitRuntime(GNode n) {
    printer.p("namespace").p(' ').p("__rt").p(' ').pln('{').incr();
    visit(n);
    printer.decr().indent().pln('}').pln();
  }

  /** Visit the array template node. */
  public void visitArrayTemplate(GNode n) {
    printer.indent().p("template").pln("<>");
    printer.indent().p(Constants.JAVA_LANG_CLASS).p(' ')
      .p("Array").p('<').p(' ').p(n.getString(0)).p(' ').p('>')
      .p("::").p("__class()").p(' ').pln('{').incr();
    printer.indent().p("static").p(' ').p(Constants.JAVA_LANG_CLASS).p(' ')
      .p('k').p(' ').pln('=').incr();
    printer.indent().p("new").p(' ').p("::java::lang::__Class(literal(\"[")
      .p(n.getString(1)).p(';').p('\"').p(')').pln(',').incr();
    printer.indent().p("Array< ").p(n.getString(2)).pln(" >::__class(),");
    printer.indent().p(n.getString(3)).pln("::__class());").decr().decr();
    printer.indent().pln("return k;");
    printer.decr().indent().pln('}');
  }

  /** Visit specified type node. */
  public void visitTypeNode(GNode n) {
    printer.p(n.getString(0)).p(' ');
  }

  public void visit(Node n) {
    for (Object o : n) {
      if (o instanceof Node) dispatch((Node)o);
    }
  }
}
