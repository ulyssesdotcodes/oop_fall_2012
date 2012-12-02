package qimpp;

import xtc.tree.Node;
import xtc.tree.GNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;

/**
 * The key here is to make the ordering of nodes very clear.
 * My idea here is something like this. To make the code
 * readable, we should strive to make the span (number of children)
 * of each node small. Example function "build..." calls are contrived.
 *
 * public Node makeMethodDeclaration (...) {
 *   GNode methodDeclaration =
 *     GNode.create("MethodDeclaration", NodeSizes.METHOD_DECLARATION);
 *
 *   methodDeclaration.set(0, buildPrimaryIdentifier(...));
 *   methodDeclaration.set(1, buildReturnType(...));
 *   methodDeclaration.set(2, buildFormalParameters(...));
 *   methodDeclaration.set(3, buildBlock(...));
 *
 *   return methodDeclaration;
 * }
 *
 * See Stroustrup's C++ language specification guide.
 *
 */


/**
 * Creates Node branches for different C++ syntactic constructs.
 *
 * @author Qimpp
 */
public class ConstructFactory {

  // TODO: Clean up constants that aren't actually implemented.
  // NS => "NodeSizes"
  class NS {
    // Declarations - p. 23
    public static final int DECLARATION             = 4;
    public static final int MEMBER_DEFINITION       = 3;
    public static final int METHOD_DEFINITION       = 3;
    public static final int METHOD_DECLARATION      = 5;
    public static final int QUALIFIFED_IDENTIFIER   = 1;

    // DataLayoutures - p. 37
    public static final int STRUCT                  = 3;

    // Declarators - p. 55
    public static final int DECLARATOR              = 2;
      public static final int DIRECT_DECLARATOR     = 3;
      public static final int POINTER_OPERATOR      = 2;
      public static final int DECLARATOR_NAME       = 2; 
      public static final int FUNCTION_DECLARATOR   = 5;
    public static final int FIELD_DECLARATION       = 3;
 
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
    public static final int QUALIFIED_IDENTIFIER    = 1;

    // Classes - p. 199
    public static final int CLASS_DECLARATION       = 3;
    public static final int CLASS_SPECIFIER         = 2;
    public static final int CLASS_MEMBER            = 1;

    // Exceptions - p.317
    public static final int TRY_BLOCK               = 2;
    public static final int CATCH_BLOCK             = 2;
    public static final int THROW_EXPRESSION        = 1;

    // Other
    public static final int MODIFIER                = 1;
    public static final int DIMENSIONS              = 1;
    public static final int EXTENSION               = 1;
    public static final int TYPE                    = 2;
    public static final int FORMAL_PARAMETER        = 2;
  }


  public ConstructFactory() {

  }

  /** Current class */
  Klass currentClass;


  /**
   * Take a name and determines whether it is a fundamental type
   * or a qualified type name.
   */
  protected Node formatAsTypeName(String name) {
    if (PrimitiveType.isJavaPrimitive(name)) {
      return GNode.create("FundamentalType", name);
    } else { return GNode.create("QualifiedType", name); }
  }

  /**
   * Take a name and prepend "__" to it, making it for internal
   * representation.
   */
  protected String internal(String name) { return "__" + name; }


  // ===========================================================================

  // translationUnit is more accurate than compilationUnit
  // http://stackoverflow.com/questions/1106149/what-is-a-translation-unit-in-c
  public Node buildTranslationUnit(HashMap<String,Klass> thePackage) {       // DONE
    GNode translationUnit =
      GNode.create("TranslationUnit");

    translationUnit.add(buildDefaultDirectives());

    for (Object o : thePackage.values()) {
      translationUnit
        .add(buildClassDeclaration((Klass)o));
      currentClass = null;
    }
    
    return translationUnit;
  }

  /** Build default directives. */
  public Node buildDefaultDirectives() {                                // DONE
    GNode pragma = GNode.create("Pragma", "once");

    GNode include = GNode.create("IncludeDirectives");
    include.add(GNode.create("QuotedForm", "java_lang.h"));
    include.add(GNode.create("AngleBracketForm", "stdint"));
    include.add(GNode.create("AngleBracketForm", "sstream"));

    return GNode.create("Directives", pragma, include);
  }


  // ===========================================================================

  /** Build class declaration branch. */
  public Node buildClassDeclaration(Klass klass) {                      // DONE
    Klass currentClass = klass;
    GNode classDeclaration =
      GNode.create("ClassDeclaration",
          buildPointerTypedef(klass.name()),      /* 0 */
          buildDataLayoutDeclaration(klass),      /* 1 */
          buildVTDeclaration(klass),              /* 2 */
          buildQualifiedIdentifier(klass.name()), /* 3 */
          buildExtension(klass.parent()),         /* 4 */
          buildClassBody(klass.fields(),          /* 5 */
                         klass.methods()));
    return classDeclaration;
  }

  /** Build class struct branch. */
  public Node buildDataLayoutDeclaration(Klass klass) {
    return GNode.create("DataLayoutDeclaration",
      internal(klass.name()),
      buildDataLayoutClassFields(klass.fields(), klass.name()),
      buildDataLayoutConstructor(klass.methods()),
      buildDataLayoutImplementedMethods(klass));
  }

  // TODO
  public Node buildDataLayoutClassFields(ArrayList<Klass.Field> fields, 
                                     String name) {
    GNode structClassFields = GNode.create("DataLayoutClassFields");
   
    // vtable pointer
    structClassFields.add(internal(name) + "_VT* __vptr");
    
    for (Klass.Field field : fields) {
      structClassFields.add(field.type().name() + " " 
                          + field.name());
    }

    return structClassFields;
  }

  // TODO: How do we represent the constructor?
  public Node buildDataLayoutConstructor(ArrayList<Klass.Method> methods) {
    return GNode.create("DataLayoutConstructor");
  }

  // TODO
  public Node buildDataLayoutImplementedMethods(Klass klass) {
    GNode structImplementedMethods = 
      GNode.create("DataLayoutImplementedMethods");

    for (Klass.Method method : klass.methods()) {
      if (method.implementor() == klass) {
        structImplementedMethods.add(buildDataLayoutImplementedMethod(method));
      }
    }

    return structImplementedMethods;
  }

  public Node buildDataLayoutImplementedMethod(Klass.Method method) {
    String typename = 
      (null == method.type() ? "void" : method.type().name());

    return GNode.create("ImplementedMethod",
      method.isStatic(),                             /* 0 */
      typename,                                      /* 1 */
      method.name(),                                 /* 2 */
      buildMethodParameterTypes(method));            /* 3 */
  }

  public Node buildMethodParameterTypes(Klass.Method method) {
    GNode methodParameterTypes = 
      GNode.create("MethodParameterTypes");

    // always add own as implicit "this"
    methodParameterTypes.add(method.of().name());

    for (ParameterVariable pv : method.parameters()) {
      methodParameterTypes.add(pv.type().qualifiedName());
    }

    return methodParameterTypes;
  }

  // VTABLE ====================================================================

  // TODO: use methods and constructor call when ready
  /** Build class struct vtable branch. */
  public Node buildVTDeclaration(Klass klass) {
    return GNode.create("VTDeclaration",
      internal(klass.name()),
      buildVTMethods(klass),
      buildVTConstructor(klass));
  }

  // TODO
  /** Build class struct vtable methods branch. */
  public Node buildVTMethods(Klass klass) {
    GNode vtMethods = GNode.create("VTMethods");

    for (Klass.Method method : klass.methods()) {
      vtMethods.add(buildVTMethod(method));
    }

    return vtMethods;
  }

  /** Build class struct vtable method branch. */
  public Node buildVTMethod(Klass.Method method) {
    String returnType = "void";
    if (null != method.type()) { returnType = method.type().name(); }
    return GNode.create("VTMethod",
        returnType,                           /* 0 */
        method.name(),                        /* 1 */
        buildMethodParameterTypes(method));   /* 2 */
  }

  // TODO
  /** Build vtable constructor branch. */
  public Node buildVTConstructor(Klass klass) {
    GNode vtConstructor = GNode.create("VTConstructor",
        internal(klass.name()),         /* 0 */
        buildVTInitializations(klass)); /* 1 */

    return vtConstructor;
  }

  /** Build vtable initializations branch. */
  public Node buildVTInitializations(Klass klass) {
    GNode vtInitializatons = GNode.create("VTInitializations");

    for (Klass.Method method : klass.methods()) {
      vtInitializatons.add(buildVTInitialization(method));
    }

    return vtInitializatons;
  }

  // TODO
  /** Build vtable initializer branch. */
  public Node buildVTInitialization(Klass.Method method) {
    String returnType = "void";
    if (null != method.type()) { returnType = method.type().name(); }
    return GNode.create("VTInitialization",      
        method.name(),                          /* 0 */
        returnType,                             /* 1 */
        buildMethodParameterTypes(method),      /* 2 */
        internal(method.implementor().name())); /* 3 */
  }

  /** Build class typedef node. */
  public Node buildPointerTypedef(String aliasName) {
    return GNode.create("PointerTypedef",
      internal(aliasName),
      aliasName);
  }

  /** Build class identifier branch. */
  public Node buildQualifiedIdentifier(String qualifiedIdentifier) {    // DONE
    return GNode.create("QualifiedIdentifier", qualifiedIdentifier); 
  }

  /** Build class inheritance branch. */
  public Node buildExtension(Klass parent) {                            // DONE
    if (null == parent) { 
      return GNode.create("Extension", null);
    } else { return GNode.create("Extension", buildType(parent.type())); }
  }

  /** Build type branch. */
  public Node buildType(Type type) {                                    // DONE
    GNode typeNode = GNode.create("Type", NS.TYPE);

    if (null == type) {
      return (GNode)typeNode.add(GNode.create("FundamentalType", "void")); /*0*/
    } else { 
      typeNode.add(formatAsTypeName(type.name()));                      /*0*/
      if (type.hasDimensions()) { // TODO: Implement array indication in Type
        typeNode.add(buildDimensions()); /* 1 */
      } else { typeNode.add(null); }     /* 1 */
    }

    return typeNode;
  }

  // TODO
  /** Build class body branch. */
  public Node buildClassBody(ArrayList<Klass.Field> fields, 
                             ArrayList<Klass.Method> methods) {               
    GNode classBody = GNode.create("ClassBody",
        buildConstructor(/* TODO */),  /* 0 */
        buildFields(fields),           /* 1 */
        buildMethods(methods));        /* 2 */
    return classBody; 
  }


  // TODO
  /** Build constructor branch. */
  public Node buildConstructor(/* TODO */) {
    return GNode.create("Constructor");
        //constructor.qualifiedName(),
        //constructor.arguments()
        //constructor.initializers());
  }


  // ===========================================================================

  /** Build fields branch. */
  public Node buildFields(ArrayList<Klass.Field> fields) {
    GNode fieldsNode = GNode.create("Fields");

    Iterator it = fields.iterator();
    while (it.hasNext()) {
      fieldsNode.add(buildFieldDeclaration((Klass.Field)it.next()));
    }
   
    return fieldsNode;
  }

  /** Build field declaration branch. */
  public Node buildFieldDeclaration(Klass.Field field) {
    GNode fieldDeclaration =
      GNode.create("FieldDeclaration",
          buildModifiers(field.isStatic()),                     /* 0 */
          buildType(field.type()),                           /* 1 */
          buildDeclarators((GNode)field.initialization()));  /* 2 */
    return fieldDeclaration;
  }

  /** 
   * Build modifiers branch. Since there we're going from the more constrained
   * private notion in Java to the less constrained C++, we can skip worrying
   * about private / public / protected modifiers. We DO need to care about
   * static modifiers though.
   */
  public Node buildModifiers(boolean isStatic) {
    GNode modifiers = GNode.create("Modifiers");
    
    if (isStatic) {
      GNode modifier  = GNode.create("Modifier", "static");
      modifiers.add(modifier);
    }

    return modifiers;
  }

  /** Build dimensions node. */
  public Node buildDimensions() {
    return GNode.create("Dimensions", "[");
  }

  /**
   * Build declarators branch. 
   *
   * TODO: This only holds one declarator at the moment,
   * but it should hold more when we can handle definitions like
   * double i, j = 3;
   */
  public Node buildDeclarators(GNode declarator) {
    return GNode.create("Declarators", declarator);
  } 

  // ===========================================================================

  /** Build methods branch. */
  public Node buildMethods(ArrayList<Klass.Method> methods) {
    GNode methodsNode = GNode.create("Methods");

    for (Klass.Method method : methods) {
      if (method.implementor() == currentClass) {
        methodsNode.add(buildMethodDeclaration(method));
      }
    }

    return methodsNode;
  }
  
  /** Build method declaration branch. */
  public Node buildMethodDeclaration(Klass.Method method) {
    GNode methodDeclaration =
      GNode.create("MethodDeclaration",
          buildModifiers(method.isStatic()),           /* 0 */
          buildType(method.type()),                    /* 1 */
          method.name(),                               /* 2 */
          buildFormalParameters(method.parameters()),  /* 3 */
          method.body());                              /* 4 */
    return methodDeclaration;
  }
  
  /** Build formal parameters branch. */
  public Node buildFormalParameters(ArrayList<ParameterVariable> parameters) {
    GNode formalParameters = GNode.create("FormalParameters");

    Iterator it = parameters.iterator();
    while (it.hasNext()) {
      formalParameters.add(buildFormalParameter((ParameterVariable)it.next()));
    }

    return formalParameters;
  }

  /** Build formal parameter branch. */
  public Node buildFormalParameter(ParameterVariable parameter) {
    GNode parameterNode = GNode.create("FormalParameter",
        buildType(parameter.type()),                   /* 0 */
        parameter.name());                             /* 1 */
    return parameterNode;
  }

  // ===========================================================================

}




