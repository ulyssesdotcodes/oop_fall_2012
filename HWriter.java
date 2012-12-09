package qimpp;

import xtc.tree.Visitor;
import xtc.tree.Node;
import xtc.tree.GNode;
import xtc.tree.Printer;

import java.util.Iterator;

/**
 * Writes the header file from the C++ AST.
 * It might help with debugging to put our comments in the 
 * actual outputted code.
 *
 * TODO: Remove all the p(' ') with something like s().
 *  It's clearer and less visually confusing.
 *
 * TODO: There MUST be a better way to do indenting. Calling 
 *  indent() every single time is horrible.
 *
 * TODO: (11/30) Vivek's remaining work on this is:
 *  1) ordering of declarations
 *  2) constructor initialization
 *  3) vtable struct
 *
 * @author Qimpp
 */
public class HWriter extends Visitor {

  // WC => "Write Constants"
  class WC {
    public static final String RT_PTR = "__rt::Ptr";
  }

  // Into => Indexes
  class Into {
    public static final int STRUCT_DECLARATION = 1;
  }


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
  public void visitTranslationUnit(GNode n) {
    // Reset the state.
    isDeclaration   = false;
    isStatement     = false;
    isNested        = false;
    isIfElse        = false;
    isFunctionDef   = false;
    precedence      = PREC_BASE;

    if (lineUp) printer.line(1);
    printer.pln("// ==========================================================")
      .pln("//               .h Header file              ")
      .pln("// ==========================================================")
      .pln();
    visit(n);
  }

  /** Visit the specified directives node. */
  //public void visitDirectives() {}

  /** Visit the specified pragma node. */
  public void visitPragma(GNode n) {
    printer.p("#pragma").p(' ').pln(n.getString(0));
  }

  /** Visit the specified include directives node. */
  public void visitIncludeDirectives(GNode n) { 
    visit(n); printer.pln();
  }
  
  /** Visit the specified quoted form include node. */
  public void visitQuotedForm(GNode n) {
    printer.p("#include").p(' ').p('\"').p(n.getString(0)).pln('\"');
  }

  /** Visit the specified angle bracket form include node. */
  public void visitAngleBracketForm(GNode n) {
    printer.p("#include").p(' ').p('<').p(n.getString(0)).pln('>');
  }


  // ===========================================================================

  /** Flag for forward declarating a class struct. */
  boolean forwardDeclareStruct; 

  public void prepareClassDeclaration() {
    forwardDeclareStruct = true;
  }

  public void endClassDeclaration() {
    forwardDeclareStruct = false;
  }

  /** Visit the specified class declaration node. */
  public void visitClassDeclaration(GNode n) {
    printer.pln("// ================== NEW CLASS ======================");
    prepareClassDeclaration();
    dispatch(n.getGeneric(Into.STRUCT_DECLARATION)); // forward declare

    visit(n);

    endClassDeclaration();
  }

  // TODO
  /** Visit specified struct declaration branch. */
  public void visitDataLayoutDeclaration(GNode n) {
    if (forwardDeclareStruct) {
      printer.pln("// Forward declare data layout and vtables.");
      printer.p("struct").p(' ').p(n.getString(0)).pln(';');
      printer.p("struct").p(' ').p(n.getString(0)).p("_VT").pln(';');
      printer.pln();
      forwardDeclareStruct = false;
    } else {
      prepareNested();
      boolean nested = startStatement(STMT_ANY, n);
      printer.pln("// The data layout");
      printer.p("struct").p(' ').p(n.getString(0)).p(' ').pln('{');

      visit(n);

      printer
        .indent().pln("// The function returning the class object")
        .indent().pln("static ::java::lang::Class __class();").pln()
        .indent().pln("// The vtable")
        .indent().p("static").p(' ').p(n.getString(0)).p("_VT").p(' ')
        .p("__vtable").pln(';');

      endStatement(nested);
      printer.p('}').pln(';').pln();
    }
  }

  /** Visit specified struct class fields branch. */
  public void visitDataLayoutClassFields(GNode n) {
    for (Object o : n) { printer.indent().p((String)o).pln(';'); }
    printer.pln();
  }

  /** Visit specified struct constructor branch. */
  public void visitDataLayoutConstructor(GNode n) {
    printer.indent().pln("// The constructor.");
    printer.indent().p(n.getString(0)).p('(');
    visit(n);
    printer.p(')').pln(';').pln();
  }

  /** Visit specified constructor arguments. */
  public void visitDataLayoutConstructorArguments(GNode n) {
    for (Iterator<?> iter = n.iterator(); iter.hasNext(); ) {
      printer.p((String)iter.next());
      if (iter.hasNext()) {
        printer.p(',').p(' ');
      }
    }
  }


  /** Visit specified struct implemented methods branch. */
  public void visitDataLayoutImplementedMethods(GNode n) {
    if (n.size() > 0) { 
      printer.indent().pln("// implemented methods");
      for (Object o : n) {
        Node m = (Node)o;
        if (m.getName().equals("ImplementedMethod")) {
          printer.indent();
          if (m.getBoolean(0)) { printer.p("static").p(' '); }
          printer.p(m.getString(1)).p(' ');
          printer.p(m.getString(2))
            .p('(');
          dispatch(m.getGeneric(3));
          printer.p(')').pln(';');
        }
      }
      printer.pln();
    }
  }

  // VTABLE ====================================================================

  /** Visit specified VT declaration node. */
  public void visitVTDeclaration(GNode n) {
    printer.pln("// vtable layout");
    printer.p("struct").p(' ').p(n.getString(0)).p("_VT").p(' ').pln('{');
    printer.incr().indent().pln("::java::lang::Class __isa;");
    printer.indent().p("void (*__delete)(::").p(n.getString(0)).p("*)").pln(';');
    visit(n);
    printer.decr().p('}').pln(';').pln();
  }

  /** Visit specified VT method node. */
  public void visitVTMethod(GNode n) {
    printer.indent().p(n.getString(0)).p(' ')
      .p('(').p('*').p(n.getString(1)).p(')')
      .p('(');
    dispatch(n.getGeneric(2));
    printer.p(')').pln(';'); 
  }

  /** Visit specified VT constructor node. */
  public void visitVTConstructor(GNode n) {
    // print the isa and delete methods first
    printer.pln().indent().p(n.getString(0)).p("_VT").p(' ').pln("()");
    printer.indent().p(':').p(' ').p("__isa(").p(n.getString(0))
      .p(Constants.QUALIFIER).p("__class()").p(')').pln(',').incr();
    printer.indent().p("__delete(&__rt::__delete").p('<').p(' ')
      .p(n.getString(0)).p(' ').p('>').p(')').pln(',');

    visit(n);

    printer.decr();
  }

  /** Visit specified VT initializations node. */
  public void visitVTInitializations(GNode n) {
    for (Iterator<?> iter = n.iterator(); iter.hasNext(); ) {
      dispatch((GNode)iter.next());
      if (iter.hasNext()) {
        printer.pln(',');
      }
    }
    printer.p(' ').pln("{}");
  }

  /** Visit specified VT initialization node. */
  public void visitVTInitialization(GNode n) {
    printer.indent().p(n.getString(0)).p('(').p('(')
      .p(n.getString(1)).p("(*)").p('(');
    dispatch(n.getGeneric(2)); // MethodParameterTypes
    printer.p(')').p(')').p('&').p(n.getString(3))
      .p(Constants.QUALIFIER).p(n.getString(0)).p(')');
  }

  // ===========================================================================


  /** Visit specified struct implemented method types branch. */
  public void visitMethodParameterTypes(GNode n) {
    for (Iterator<?> iter = n.iterator(); iter.hasNext(); ) {
      printer.p((String)iter.next());
      if (iter.hasNext()) {
        printer.p(',').p(' ');
      }
    }
  }

  /** Visit specified typedef node for aliasing from a smart pointer. */
  public void visitPointerTypedef(GNode n) {
    printer.indent()
      .pln("// Definition of type names, which are equivalent to Java "
        + "semantics.")
      .p("typedef").p(' ')
      .p(WC.RT_PTR).p('<').p(n.getString(0)).p('>').p(' ')
      .p(n.getString(1)).pln(';');
    printer.pln();
  }

  boolean writingField = false;

  /** Visit specified modifier node. */
  public void visitModifier(GNode n) {
    if (writingField) {
      printer.p(n.getString(0)).p(' ');
    }
  }

  /** Visit specified type node. */
  public void visitType(GNode n) {
    if (writingField) {
      visit(n);
      if (null != n.get(1)) { printer.p("[]"); }
    }
  }

  /** Visit specified qualified type node. */
  public void visitQualifiedType(GNode n) {
    if (writingField) { printer.p(n.getString(0)).p(' '); }
  }

  /** Visit specified fundamental type node. */
  public void visitFundamentalType(GNode n) {
    if (writingField) { printer.p(n.getString(0)).p(' '); }
  }

  /** 
   * Visit specified declarator node.
   * TODO: Note, we only can handle single declarations
   *  in a single line, at this point. See Store.Analyzer
   *  to learn more.
   */
  public void visitDeclarator(GNode n) {
    if (writingField) { printer.p(n.getString(0)).pln(';'); }
  }


  /** The constructor */
  public void visitConstructor(GNode n) {
  }

  






  public void visit(Node n) {
    for (Object o : n) {
      if (o instanceof Node) dispatch((Node)o);
    }
  }


}
