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

  /** Constructor. */
  public ConstructFactory() {}
  
  // ===========================================================================

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
   *
   * @param name Name to make internal.
   */
  protected String internal(String name) { return "__" + name; }

  /**
   * Format a Java type by resolving it to the right namespace.
   * The inputted name could be an internal representation.
   *
   * @param name Java type candidate.
   * @return formatted, resolved type if an Object
   */
  protected String formatAsObject(String name) {
    if (name.endsWith("Object")) { 
      return Constants.QUALIFIER
        + "java"
        + Constants.QUALIFIER
        + "lang"
        + Constants.QUALIFIER + name; 
    }
    return name;
  }

  // ===========================================================================

  // translationUnit is more accurate than compilationUnit
  // http://stackoverflow.com/questions/1106149/what-is-a-translation-unit-in-c
  public Node buildTranslationUnit(HashMap<String,Klass> thePackage) {
    GNode translationUnit =
      GNode.create("TranslationUnit");

    translationUnit.add(buildDefaultDirectives());
    Klass.Method main = null;

    for (Object o : thePackage.values()) {
      Klass k = (Klass)o;
      if (!Constants.JAVA_LANG.containsKey(k.name())) {
        translationUnit.add(buildClassDeclaration(k));
        if (k.containsMain()) {
          main = k.main();
        }
      }
    }

    translationUnit.add(buildMainMethod(main));
    
    return translationUnit;
  }

  public Node buildMainMethod(Klass.Method main) {
    return GNode.create("MainMethod", 
        Utilities.resolve(main.implementor().name(), true)
        + Constants.QUALIFIER + "main()");
  }

  /** Build default directives. */
  public Node buildDefaultDirectives() {
    GNode pragma = GNode.create("Pragma", "once");

    GNode include = GNode.create("IncludeDirectives");
    include.add(GNode.create("QuotedForm", "java_lang.h"));
    include.add(GNode.create("QuotedForm", "ptr.h"));
    include.add(GNode.create("AngleBracketForm", "sstream"));

    return GNode.create("Directives", pragma, include);
  }


  // ===========================================================================

  /** Build class declaration branch. */
  public Node buildClassDeclaration(Klass klass) {
    GNode classDeclaration =
      GNode.create("ClassDeclaration",
          buildPointerTypedef(klass.name()),      /* 0 */
          buildDataLayoutDeclaration(klass),      /* 1 */
          buildVTDeclaration(klass),              /* 2 */
          klass.name(),                           /* 3 */
          buildExtension(klass.parent()),         /* 4 */
          buildClassBody(klass),                  /* 5 */
          buildArrayTemplate(klass));             /* 6 */
    return classDeclaration;
  }

  /** Build class struct branch. */
  public Node buildDataLayoutDeclaration(Klass klass) {
    return GNode.create("DataLayoutDeclaration",
      internal(klass.name()),
      buildDataLayoutClassFields(klass.fields(), klass.name()),
      buildDataLayoutConstructor(klass),
      buildDataLayoutImplementedMethods(klass));
  }

  public Node buildDataLayoutClassFields(ArrayList<Klass.Field> fields, 
                                     String className) {
    GNode structClassFields = GNode.create("DataLayoutClassFields");

    structClassFields.add(internal(className) + "_VT* __vptr");
    
    for (Klass.Field field : fields) {
      structClassFields.add(field.type().qualifiedName() + " " 
                          + field.identifier());
    }

    return structClassFields;
  }

  public Node buildDataLayoutConstructorArguments(ArrayList<Klass.Field> fields) {
    GNode constructorArgs = GNode.create("DataLayoutConstructorArguments");
    
    for (Klass.Field field : fields) {
      constructorArgs.add(field.type().qualifiedName() + " " 
                        + field.identifier());
    }

    return constructorArgs;
  }

  public Node buildDataLayoutConstructor(Klass klass) {
    return GNode.create("DataLayoutConstructor",
        internal(klass.name()),
        buildDataLayoutConstructorArguments(klass.fields()));
  }


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
    return GNode.create("ImplementedMethod",
      method.isStatic(),                             /* 0 */
      method.type().qualifiedName(),                 /* 1 */
      method.resolvedIdentifier(),                   /* 2 */
      buildMethodParameterTypes(method));            /* 3 */
  }

  public Node buildMethodParameterTypes(Klass.Method method) {
    GNode methodParameterTypes = 
      GNode.create("MethodParameterTypes");

    // except for the main method, always add own as implicit "this"
    if (!method.isMain()) {
      methodParameterTypes.add(method.implementor().qualifiedName());

      for (ParameterVariable pv : method.parameters()) {
        methodParameterTypes.add(pv.type().qualifiedName());
      }
    }

    return methodParameterTypes;
  }

  // VTABLE ====================================================================

  /** Build class struct vtable branch. */
  public Node buildVTDeclaration(Klass klass) {
    return GNode.create("VTDeclaration",
      internal(klass.name()),
      buildVTMethods(klass),
      buildVTConstructor(klass));
  }

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
    if (null != method.type()) { returnType = method.type().qualifiedName(); }
    return GNode.create("VTMethod",
        returnType,                           /* 0 */
        method.resolvedIdentifier(),          /* 1 */
        buildMethodParameterTypes(method));   /* 2 */
  }

  /** Build vtable constructor branch. */
  public Node buildVTConstructor(Klass klass) {
    GNode vtConstructor = GNode.create("VTConstructor",
        internal(klass.name()),                 /* 0 */
        buildVTInitializations(klass));         /* 1 */

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

  /** Build vtable initializer branch. */
  public Node buildVTInitialization(Klass.Method method) {
    String returnType = "void";
    if (null != method.type()) { returnType = method.type().qualifiedName(); }
    return GNode.create("VTInitialization",      
        method.resolvedIdentifier(),            /* 0 */
        returnType,                             /* 1 */
        buildMethodParameterTypes(method),      /* 2 */
        Utilities.resolve(method                /* 3 */
                          .implementor()
                          .name(), true));

    // ::java::lang::__Object::doThisThing
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
    } else { return GNode.create("Extension", buildType(parent)); }
  }

  /** Build type branch. */
  public Node buildType(Type type) {                                    // DONE
    return GNode.create("Type",
        formatAsTypeName(type.qualifiedName()),
        buildDimensions(type.dimensions()));
  }

  // CLASS BODY ================================================================

  /** Build class body branch. */
  public Node buildClassBody(Klass klass) {               
    GNode classBody = GNode.create("ClassBody",
        buildConstructor(klass),               /* 0 */
        buildMethods(klass));                  /* 1 */
    return classBody; 
  }

  /** Build constructor branch. */
  public Node buildConstructor(Klass klass) {
    return GNode.create("Constructor",
        Utilities.resolve(klass.name(), true)
      + Constants.QUALIFIER + internal(klass.name()),
        buildConstructorArguments(klass.fields()),
        buildConstructorInitializations(klass.fields()));
  }

  /** Build constructor arguments branch. */
  public Node buildConstructorArguments(ArrayList<Klass.Field> fields) {
    GNode constructorArguments = GNode.create("ConstructorArguments");
    
    for (Klass.Field field : fields) {
      constructorArguments.add(field.type().qualifiedName() + " " 
                          + field.identifier());
    }

    return constructorArguments;
  }

  /** Build constructor initializers branch. */
  public Node buildConstructorInitializations(ArrayList<Klass.Field> fields) {
    GNode constructorInitializations = 
      GNode.create("ConstructorInitializations");
    
    for (Klass.Field field : fields) {
      constructorInitializations.add(field.identifier());
    }

    return constructorInitializations;
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
          buildModifiers(field.isStatic()),                 /* 0 */
          buildType(field.type()),                          /* 1 */
          buildDeclarators((GNode)field.body()));           /* 2 */
    return fieldDeclaration;
  }

  /** Build modifiers branch. */
  public Node buildModifiers(boolean isStatic) {
    GNode modifiers = GNode.create("Modifiers");
    
    if (isStatic) {
      GNode modifier  = GNode.create("Modifier", "static");
      modifiers.add(modifier);
    }

    return modifiers;
  }

  // Probably can remove this.
  /** Build dimensions node. */
  public Node buildDimensions(int dimensions) {
    String dims = "";
    for (int i = 0; i < dimensions; i++) {
      dims += "[]";
    }
    return GNode.create("Dimensions", dims);
  }

  /** Build declarators branch. */
  public Node buildDeclarators(GNode declarator) {
    return GNode.create("Declarators", declarator);
  } 

  // METHODS ===================================================================

  /** Build methods branch. */
  public Node buildMethods(Klass klass) {
    return GNode.create("Methods",
        (Utilities.resolve(klass.name(), true)
      + Constants.QUALIFIER + "__class")
        .substring(Constants.QUALIFIER.length()),             /* 0 */
        Utilities.resolve(klass.name(), false)
          .replaceAll(Constants.QUALIFIER,
                      Constants.JAVA_QUALIFIER)
          .substring(Constants.JAVA_QUALIFIER.length()),      /* 1 */
        Utilities.resolve(klass.parent().name(), true)
      + Constants.QUALIFIER + "__class",                      /* 2 */
        internal(klass.name()),                               /* 3 */
      buildMethodDeclarations(klass));                        /* 4 */

  }
     

  /** Build methods declarations branch. */
  public Node buildMethodDeclarations(Klass klass) {
    GNode methodDeclarations = GNode.create("MethodDeclarations");

    for (Klass.Method method : klass.methods()) {
      if (method.implementor() == klass) {
        methodDeclarations.add(buildMethodDeclaration(method));
      }
    }

    return methodDeclarations;
  }
  
  /** Build method declaration branch. */
  public Node buildMethodDeclaration(Klass.Method method) {
    GNode methodDeclaration =
      GNode.create("MethodDeclaration",
          method.type().qualifiedName(),                        /* 1 */
          internal(method.implementor().name())
        + Constants.QUALIFIER + method.resolvedIdentifier(),    /* 2 */
          buildFormalParameters(method.parameters(),
                                method.implementor()
                                  .qualifiedName(),
                                method.isMain()),               /* 3 */
          method.body());                                       /* 4 */
    return methodDeclaration;
  }
  
  /** Build formal parameters branch. */
  public Node buildFormalParameters(ArrayList<ParameterVariable> parameters,
                                    String qualifiedType,
                                    boolean isMain) {
    GNode formalParameters = GNode.create("FormalParameters");

    // implicit "this"
    if (!isMain) {
      formalParameters.add(qualifiedType + " __this");

      Iterator it = parameters.iterator();
      while (it.hasNext()) {
        ParameterVariable parameter = (ParameterVariable)it.next();
        String pStr = parameter.type().qualifiedName() + " "
          + parameter.name() + parameter.type().dimensionsSuffix();
        formalParameters.add(pStr);
      }
    }

    return formalParameters;
  }

  // ===========================================================================

  /** Build array template for the specified class. */
  public Node buildArrayTemplate(Klass klass) {
    GNode runtime       = GNode.create("Runtime");
    GNode arrayTemplate = GNode.create("ArrayTemplate",
        klass.qualifiedName(),                                /* 0 */
        klass.encoding() + Utilities.resolve(klass.name(), false)
          .replaceAll(Constants.QUALIFIER,
                      Constants.JAVA_QUALIFIER)
          .substring(Constants.JAVA_QUALIFIER.length()),      /* 1 */
        klass.parent().qualifiedName(),
        Utilities.resolve(klass.name(), true));               /* 2 */
    return runtime.add(arrayTemplate);
  }


}

