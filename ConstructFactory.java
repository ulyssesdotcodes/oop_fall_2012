

package qimpp;


/**
 * The key here is to make the ordering of nodes very clear.
 * My idea here is something like this. To make the code
 * readable, we should strive to make the span (number of children)
 * of each node small. Example function calls are contrived.
 *
 * public Node makeMethodDeclaration (...) {
 *   GNode methodDeclaration =
 *     GNode.create("MethodDeclaration", NodeSizes.METHOD_DECLARATION);
 *
 *   methodDeclaration.set(0, makePrimaryIdentifier(...));
 *   methodDeclaration.set(1, makeReturnType(...));
 *   methodDeclaration.set(2, makeFormalParameters(...));
 *   methodDeclaration.set(3, makeBlock(...));
 *
 *   return methodDeclaration;
 * }
 *
 * Note, PrimaryIdentifier is not a string. It will probably be easier to 
 * default to adding most children as nodes. PrimaryIdentifier in this 
 * case has one child and it is a String identifying the method.
 * 
 * Here we can find a C++ language specification guide:
 * http://publib.boulder.ibm.com/iseries/v5r2/ic2924/books/c0948150.pdf
 */


/**
 * Creates Node branches for different C++ syntactic constructs.
 *
 * @author Qimpp
 */
public class ConstructFactory {

  // NS => "NodeSizes"
  class NS {
    // Declarations - p. 23
    public static final int DECLARATION             = 4;
    public static final int MEMBER_DEFINITION       = 3;
    public static final int METHOD_DEFINITION       = 3;

    // Structures - p. 37
    public static final int STRUCT                  = 3;

    // Declarators - p. 55
    public static final int DECLARATOR              = 2;
      public static final int DIRECT_DECLARATOR     = 3;
      public static final int POINTER_OPERATOR      = 2;
      public static final int DECLARATOR_NAME       = 2; 
      public static final int FUNCTION_DECLARATOR   = 5;
 
    // Expressions - p. 71
    public static final int EXPRESSION              = 2;
      public static final int UNARY_EXPRESSION      = 2;
      public static final int ASSIGNMENT_EXPRESSION = 2;
      public static final int SIMPLE_ASSIGNMENT     = 2;
      public static final int COMPOUND_ASSIGNMENT   = 2;
      public static final int CAST_EXPRESSION       = 2;
      public static final int CONDITIONAL_EXPRESSION= 2;

    // Operators - p. 87
    public static final int NEW_OPERATOR            = 2;
    public static final int DELETE_OPERATOR         = 2;
    public static final int MULTIPLICATION_OPERATOR = 2;
    public static final int DIVISION_OPERATOR       = 2;
    public static final int REMAINDER_OPERATOR      = 2;
    public static final int ADDITION_OPERATOR       = 2;
    public static final int SUBTRACTION_OPERATOR    = 2;
    public static final int BITWISE_SHIFT_OPERATOR  = 2;
    public static final int RELATIONAL_OPERATOR     = 2;
    public static final int EQUALITY_OPERATOR       = 2;
    public static final int BITWISE_AND_OPERATOR    = 2;
    public static final int BITWISE_OR_OPERATOR     = 2;
    public static final int BITWISE_OR_X_OPERATOR   = 2;
    public static final int LOGICAL_AND_OPERATOR    = 2;
    public static final int LOGICAL_OR_OPERATOR     = 2;
    public static final int DOT_POINTER             = 2;
    public static final int ARROW_POINTER           = 2;

    // Statements - p. 143
    public static final int EXPRESSION_STATEMENT    = 1;
    // BLOCK STATEMENT
    public static final int IF_STATEMENT            = 2;
    public static final int IF_ELSE_STATEMENT       = 3;
    public static final int SWITCH_STATEMENT        = 4;
    public static final int WHILE_STATEMENT         = 2;
    public static final int DO_WHILE_STATEMENT      = 2;
    public static final int FOR_STATEMENT           = 4;
    public static final int BREAK_STATEMENT         = 0;
    public static final int CONTINUE_STATEMENT      = 0;
    public static final int NULL_STATEMENT          = 0;
    public static final int RETURN_STATEMENT        = 1;

    // Preprocessor directives - p. 161
    public static final int PRAGMA_DIRECTIVE        = 1;

    // Namespaces - p. 177
    public static final int NAMESPACE               = 2;
    public static final int QUALIFIED_IDENTIFIER    = 2;

    // Classes - p. 199
    public static final int CLASS_DECLARATION       = 3;
    public static final int CLASS_SPECIFIER         = 2;
    public static final int CLASS_MEMBER            = 1;

    // Exceptions - p.317
    public static final int TRY_BLOCK               = 2;
    public static final int CATCH_BLOCK             = 2;
    public static final int THROW_EXPRESSION        = 1;

  }



  public ConstructFactory() {

  }


  /**
   * Take a name and determines whether it is a fundamental type
   * or a qualified type name.
   */
  protected Node formatAsTypeName(Name name) {
    if (PrimitiveType.isJavaPrimitive(name.get())) {
      return GNode.create("FundamentalType", name.get());
    } else { return GNode.create("QualifiedType", name.get()); }
  }


  // ===========================================================================

  // translationUnit is more accurate than compilationUnit
  // http://stackoverflow.com/questions/1106149/what-is-a-translation-unit-in-c
  public Node buildTranslationUnit(Package thePackage) {
    GNode translationUnit =
      Gnode.create("TranslationUnit");

    translationUnit.add(buildDefaultDirectives());

    // Dynamic number of children depending on number of classes,
    // so use an iterator.
    Iterator it = thePackage.unpack();
    
    while(it.hasNext()) {
      translationUnit.add(buildClassDeclaration(it.next());
    }

    return translationUnit;
  }


  public Node buildDeclaration(...) {
    GNode declaration = 
      GNode.create("Declaration", NS.DECLARATION);

    declaration.set(0, buildStorage(...));
    declaration.set(1, buildType(...));
    declaration.set(2, buildDeclarators(...));
    declaration.set(3, buildExpression(...));

    return declaration;
  }

  /** Build default directives. */
  public Node buildDefaultDirectives() {
    GNode pragma = GNode.create("Pragma", "once");
    GNode include = GNode.create("IncludeDirectives");
    include.add(GNode.create("QuotedForm", "java_lang");
    include.add(GNode.create("AngleBracketForm", "stdint");
    return GNode.create("Directives", pragma, include);
  }


  // ===========================================================================


  // TODO
  /** Build class declaration branch. */
  public Node buildClassDeclaration(Klass klass) {
    GNode classDeclaration =
      Gnode.create("ClassDeclaration", NS.CLASS_DECLARATION);

    classDeclaration.set(0, buildQualifiedIdentifier(klass.identifier));
    classDeclaration.set(1, buildExtension(klass.extension));
    classDeclaration.set(2, buildClassBody(klass.members));

    return classDeclaration;
  }

  // TODO
  /** Build class identifier branch. */
  public Node buildQualifiedIdentifier(...) {
    GNode className = GNode.create("QualifiedIdentifier", NS.CLASS_IDENTIFIER);
    className.set(0, ...); 
    return className;
  }

  // TODO
  /** Build class inheritance branch. */
  public Node buildExtension(Name name) {
    GNode extension = GNode.create("Extension", NS.EXTENSION);
    extension.set(0, buildType(name));
    return extension;
  }

  // TODO
  /** Build class body branch. */
  public Node buildClassBody(ArrayList<Member> members) {
    // Dynamic number of children.
    // First one is the constructor.
  }


  // ===========================================================================

  // TODO
  /** Build field declaration branch. */
  public Node buildFieldDeclaration(...) {
    GNode fieldDeclaration =
      GNode.create("FieldDeclaration", NS.FIELD_DECLARATION);

    fieldDeclaration.set(0, buildModifiers(...));
    fieldDeclaration.set(1, buildType(...));
    fieldDeclaration.set(2, buildDeclarators(...));

    return fieldDeclaration;
  }

  // TODO
  /** Build type branch. */
  public Node buildType(Name name) {
    GNode type =
      GNode.create("Type", NS.TYPE);

    type.set(0, formatAsTypeName(name));
    if (dimensions) {
      type.set(1, buildDimensions());
    } else { type.set(1, null); }

    return type;
  }

  // TODO
  /** Build dimensions node. */
  public static Node buildDimensions() {
    GNode dimensions =
      GNode.create("Dimensions", NS.DIMENSIONS);
    dimensions.set(0, "[");
    return dimensions;
  }

  // TODO
  /** Build declarator branch. */
  public Node buildDeclarator(...) {
    GNode declarator =
      GNode.create("Declarator", NS.DECLARATOR);

    declarator.set(0, buildIdentifier(...));
    declarator.set(1, build_);
    declarator.set(2, buildExpression(...));

    return declarator;
  }

  // TODO
  /** Build formal parameters node. */
  public Node buildFormalParameters(...) {
    // Dynamic number of children.
  }

  // TODO
  /** Build method declaration branch. */
  public Node buildMethodDeclaration(...) {
    GNode methodDeclaration =
      GNode.create("MethodDeclaration", NS.METHOD_DECLARATION);

    methodDeclaration.set(0, makePrimaryIdentifier(...));
    methodDeclaration.set(1, makeReturnType(...));
    methodDeclaration.set(2, makeFormalParameters(...));
    methodDeclaration.set(3, makeBlock(...));
  }

  // TODO
  /** Build using branch. */
  public Node buildUsing(Namespace namespace) {
    GNode using =
      GNode.create("Using", NS.USING);
    using.set(0, namespace.getFullyQualified());
    return namespace;
  }

  // ===========================================================================


  // TODO
  /** Build conditional expression branch. */
  public Node buildConditionalExpression(...) {
    GNode conditionalExpression =
      GNode.create("ConditionalExpression", NS.CONDITIONAL_EXPRESSION);

    conditionalExpression.set(0, NS.EXPRESSION);
    conditionalExpression.set(1, RELATIONAL_OPERATOR);
    conditionalExpression.set(2, NS.EXPRESSION);

    return conditionalExpression;
  }

  // ===========================================================================


  // TODO
  /** Build if statement branch. */
  public Node buildIfStatement(...) {
    GNode ifStatement =
      GNode.create("IfStatement", NS.IF_STATEMENT);

    ifStatement.set(0, buildConditionalExpression(...));
    ifStatement.set(1, buildBlock(...));

    return ifStatement;
  } 

  // TODO
  /** Build while statement branch. */
  public Node buildWhileStatement(...) {
    GNode whileStatement =
      GNode.create("WhileStatement", NS.WHILE_STATEMENT);

    whileStatement.set(0, buildExpression(...));
    whileStatement.set(1, buildBlock(...));

    return whileStatement;
  }



  /** Build break node. */
  public Node buildBreakStatement() {
    return GNode.create("BreakStatement");
  }

  /** Build continue node. */
  public Node buildContinueStatement() {
    return GNode.create("ContinueStatement");
  }

  /** Build primary identifier. */
  public Node buildPrimaryIdentifier(String identifier) {
    GNode primaryIdentifier =
      GNode.create("PrimaryIdentifier", NS.PRIMARY_IDENTIFIER);

    primaryIdentifier.set(0, identifier);

    return primaryIdentifier;
  } 

  /** Build qualified identifier. */
  public Node buildQualifiedIdentifier(...) {
    // Dynamic number of children.
  }


}










