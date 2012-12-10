package qimpp;

import java.util.Iterator;
import java.util.ArrayList;

import xtc.tree.Node;

import qimpp.SymbolTable.Scope;

/**
 * Maintains class types expressive fields and methods.
 *
 * @author Qimpp
 */
public class Klass extends Type {
  /**
   * Member of a class.
   */
  abstract class Member {
    String identifier;
    Klass implementor;
    Klass of;
    Node body;
    Type type;
    boolean isStatic;
    String access;

    /**
     * Accessor for name.
     *
     * @return field name.
     */
    public String identifier() {
      return this.identifier;
    } 

    /**
     * Setter for name.
     *
     * @param field name.
     * @return member.
     */
    public Member identifier(String identifier) {
      this.identifier = identifier;
      return this;
    }

    /**
     * Accessor for type.
     *
     * @return field type.
     */
    public Type type() {
      return this.type;
    }

    /**
     * Setter for type.
     *
     * @param field type.
     * @return member.
     */
    public Member type(Type type) {
      this.type = type;
      return this;
    }

    /**
     * Accessor for whether the member is static.
     *
     * @return isStatic.
     */
    public boolean isStatic() {
      return this.isStatic;
    }

    /**
     * Setter for whether the member is static.
     *
     */
    public void makeStatic() {
      this.isStatic = true;
    }
  
    /**
     * Getter for implementor.
     *
     * @return implementor.
     */
    public Klass implementor() {
      return this.implementor;
    }

    /** 
     * Setter for implementor.
     *
     * @param implementing class.
     */
    public void implementor(Klass implementor) {
      this.implementor = implementor;
    }
  
    /**
     * Getter for getting the class a member is part *of*.
     *
     * @return class the member is a member of.
     */
    public Klass of() {
      return this.of;
    }
    
    /** 
     * Accessor for method body.
     *
     * @return method body.
     */
    public Node body() {
      return this.body;
    }

    /**
     * Set method body.
     *
     * @param body Node method body.
     */
    public void body(Node body) {
      this.body = body;
    }
  }

  // ===========================================================================

  /**
   * Field of a class. Is a member.
   */
  class Field extends Member {

    public Field() {
      this.implementor    = Klass.this;
      this.identifier     = null;
      this.type           = null;
      this.body           = null;
      this.isStatic       = false;
      this.of             = Klass.this;
    }

    /**
     * At the end of setting the field's properties, incorporate this
     * field into the fields of the class.
     */
    public void incorporate() {
      for (Field field : Klass.this.fields) {
        if (field.identifier().equals(this.identifier)) {
          field.implementor = Klass.this;
          field.body(this.body());
          return;
        }  
      }
      Klass.this.fields.add(this);
    }

    public String toString() {
      return this.type.qualifiedName() + ' ' +
        this.identifier + ';';
    }
  }

  // ===========================================================================

  /**
   * Method of a class. Is a member.
   */
  class Method extends Member {
 
    /** Method parameters list. */
    ArrayList<ParameterVariable> parameters;

    /** Resolved name. */
    String resolvedIdentifier;

    public Method() {
      this.implementor        = Klass.this;
      this.type               = null; // to set in Analyze.java via type(..)
      this.identifier         = null; // to set in Analyze.java via name(..)
      this.body               = null; // to set in Analyze.java via body(..)
      this.resolvedIdentifier = null; // to set in BodyAnalyze.java
      this.parameters         = new ArrayList<ParameterVariable>();
      this.isStatic           = false;
      this.of                 = Klass.this;
    }

    /**
     * Test if two methods' are equivalent, as measured by identifier,
     * parameter size and type.
     *
     * @return whether two methods are effectively equivalent.
     */
    public boolean isEquivalentTo(Method m2) {
      Method m1 = this;
      boolean b1 = m2.identifier().equals(m1.identifier);
      boolean b2 = m2.parameters().size() == m1.parameters.size();
      if (!(b1 && b2)) { return false; }
      else {
        for (int i = 0; i < m1.parameters().size(); i++) {
          Type t1 = m1.parameters().get(i).type();
          Type t2 = m2.parameters().get(i).type();
          if (!t1.equals(t2)) {
             return false;
          }
        }
        return true;
      }
    }

    /** 
     * At the end of setting the method's properties, incorporate this 
     * method into the methods of a class.
     *
     * If it implements a parent class' method, then set the appropriate
     * implementor in this class's methods list (already inherited).
     * If it's new, we add it.
     */
    public void incorporate() {
      for (Method method : Klass.this.methods) {
        if (this.isEquivalentTo(method)) {
          method.implementor = Klass.this;
          method.body(this.body());
          return;
        }  
      }
      Klass.this.methods.add(this);
    }
    
    /**
     * Accessor for parameters.
     *
     * @return method parameters, if any.
     */
    public ArrayList<ParameterVariable> parameters() {
      return this.parameters;
    }

    /**
     * Add parameter to a method's parameters.
     *
     * @param parameter A method parameter.
     */
    public void addParameter(ParameterVariable parameter) {
      this.parameters.add(parameter); 
    }

    /**
     * Test whether an invocation's argument types are convertible
     * to this method's parameter types. Assumes arguments size
     * matches this method's parameter size.
     *
     * @return whether an invocation's argument types are convertible
     *  to this method's parameter types.
     */
    public boolean areParametersConvertible(ArrayList<Type> arguments) {

      for (int i = 0; i < arguments.size(); i++) {
        Type argType    = arguments.get(i);
        Type paramType  = this.parameters().get(i).type();
        boolean convertible = true;

        if (argType instanceof PrimitiveType
         && paramType instanceof PrimitiveType) {

          convertible = argType.equals(paramType);

        } else if (argType instanceof Klass
                && paramType instanceof Klass) {

          Klass argQualifiedType    = (Klass)argType;
          Klass paramQualifiedType  = (Klass)paramType;
          convertible = argQualifiedType
            .hasParent(paramQualifiedType); 

        } else { return false; }
        if (!convertible) { return false; }
      }

      return true;
    }

    /**
     * Determines whether this method is more specific than the
     * specified method. Assumes the method is applicable and
     * all of the parameters can be converted.
     *
     *
     * @param m2 Method to compare.
     * @return whether this method is more specific than the specified method.
     */
    public boolean isMoreSpecificThan(Method m2) {
      if (this == m2) { return false; }
      Method m1 = this; 
      ArrayList<ParameterVariable> m1p = m1.parameters();
      ArrayList<ParameterVariable> m2p = m2.parameters();

      for (int i = 0; i < m1p.size(); i++) {
        Type m1t    = m1p.get(i).type();
        Type m2t    = m2p.get(i).type();
        boolean moreSpecific = true;

        if (m1t instanceof PrimitiveType
         && m2t instanceof PrimitiveType) {

          moreSpecific = m1t.equals(m2t);

        } else if (m1t instanceof Klass
                && m2t instanceof Klass) {

          Klass k1    = (Klass)m1t;
          Klass k2    = (Klass)m2t;
          moreSpecific = k1.hasParent(k2); 

        } else { return false; }
        if (!moreSpecific) { return false; }
      }

      return true;
    }

    /**
     * Whether this method is the main method.
     *
     * @return whether this method is the main method.
     */
    public boolean isMain() {
      if (this.identifier().equals("main") &&
          this.isStatic() &&
          this.type().qualifiedName().equals("void") &&
          this.parameters().size() == 1 &&
          this.parameters().get(0).type().dimensions() == 1 &&
          this.parameters().get(0).type()
            .qualifiedName().equals(Constants.JAVA_LANG_STRING)) {
          return true;
      } else { return false; }
    }

    /**
     * Accessor for resolved identifier.
     *
     * @return resolved method identifier.
     */
    public String resolvedIdentifier() {
      if (null == this.resolvedIdentifier) {
        return this.identifier();
      }
      return this.resolvedIdentifier;
    }

    /**
     * Setter for resolved identifier.
     *
     * @param resolved method identifier.
     */
    public void resolvedIdentifier(String resolvedIdentifier) {
      this.resolvedIdentifier = resolvedIdentifier;
    }

    public String toString() {
      String tmp = this.type().qualifiedName() + ' ' + identifier() + '(';
      Iterator<ParameterVariable> it = parameters.iterator();
      while (it.hasNext()) {
        ParameterVariable parameter = it.next();
        tmp += parameter.type().qualifiedName() + ' ' + 
          parameter.name(); 
        if (it.hasNext()) {
          tmp += ", ";
        }
      }
      tmp += ')';
      return tmp;
    }
  }

  // ===========================================================================

  /** Class name. */
  String name;

  /** Fully qualified class name. */
  String qualifiedName;

  /** Parent class, if any. */
  Klass parent;

  /** Class fields. */
  ArrayList<Field> fields;

  /** Methods of a class. */
  ArrayList<Method> methods;

  /**
   * Klass constructor.
   *
   * @param name Name of the class.
   * @param parent The parent class.
   */
  public Klass(String name, Klass parent) {
    ArrayList<Method> methods = new ArrayList<Method>();
    ArrayList<Field> fields   = new ArrayList<Field>();
    if (name.equals("Class") || name.equals("String")) {
      // Do nothing.
    } else if (name.equals("Object")) { 
      methods = generateObjectMethods();
    } else {
      methods = new ArrayList<Method>(parent.methods());
      fields = new ArrayList<Field>(parent.fields());
    }

    this.name           = name;
    this.qualifiedName  = Utilities.resolve(name, false);
    this.parent         = parent;
    this.fields         = fields;
    this.methods        = methods;
    
    // in case already a predefined type
    if (this.name.equals("Object")) {
      this.qualifiedName = "::java::lang::Object";
    } else if (this.name.equals("String")) {
      this.qualifiedName = "::java::lang::String";
    } else if (this.name.equals("Class")) {
      this.qualifiedName = "::java::lang::Class";
    }
  }

  public Klass(Klass other) {
    this.name           = other.name();
    this.qualifiedName  = other.qualifiedName();
    this.parent         = other.parent();
    this.fields         = other.fields();
    this.methods        = other.methods();
  }

  public ArrayList<Method> generateObjectMethods() {
    // First: Object methods
    Method hashCode = new Method();
    hashCode.makeStatic();
    hashCode.identifier("hashCode");
    hashCode.type(new PrimitiveType("int"));
    hashCode.implementor(this);

    Method equals = new Method();
    equals.makeStatic();
    equals.identifier("equals");
    equals.type(new PrimitiveType("boolean"));
    equals.implementor(this);
    equals.addParameter(new ParameterVariable(this, 
                                              "other"));

    Method getClass = new Method();
    getClass.makeStatic();
    getClass.identifier("getClass");
    getClass.type(new Klass("Class", this));
    getClass.implementor(this);

    Method toString = new Method();
    toString.makeStatic();
    toString.identifier("toString");
    toString.type(new Klass("String", this));
    toString.implementor(this);

    ArrayList<Method> objectMethods = new ArrayList<Method>();
    objectMethods.add(hashCode);
    objectMethods.add(equals);
    objectMethods.add(getClass);
    objectMethods.add(toString);
    return objectMethods;
  }

  /**
   * Analyze member bodies.
   */
  public void restructureBodies() {
    for (Method m : methods()) {
      BodyAnalyzer.restructure(m);
    }

    for (Field f : fields()) {
      BodyAnalyzer.restructure(f);
    }
  }

  /**
   * Get class parent.
   *
   * @return class parent.
   */
  public Klass parent() {
    return this.parent;
  }

  /**
   * Set class parent.
   *
   * @param parent Parent class to set.
   * @return this Klass object.
   */
  public Klass parent(Klass parent) {
    this.parent = parent;
    return this;
  }

  /**
   * Determine whether this instance has the specified parent type.
   *
   * @param parentCandidate qualified type ancestor candidate.
   * @return whether this instancehas the specified parent type.
   */
  public boolean hasParent(Klass parentCandidate) {
    Klass k = this;
    while (null != k) {
      if (k.equals(parentCandidate)) return true;
      k = k.parent();
    }
    return false;
  }

  /**
   * Whether this class contains the main method.
   *
   * @return whether this class contains the main method.
   */
  public boolean containsMain() {
    for (Method m : methods()) {
      if (m.isMain()) return true;
    }
    return false;
  }

  /**
   * Gets main method if it exists. Otherwise returns null.
   *
   * @return main method.
   */
  public Method main() {
    for (Method m : methods()) {
      if (m.isMain()) return m;
    }
    return null;
  }

  /**
   * Get methods of the class, excluding main.
   *
   * @return methods of the class.
   */
  public ArrayList<Method> methods() {
    return this.methods;
  }

  /**
   * Get fields of the class.
   *
   * @return fields of the class.
   */
  public ArrayList<Field> fields() {
    return this.fields;
  }

  /**
   * Get the name of the class.
   *
   * @return name of the class.
   */
  public String name() {
    return this.name;
  }

  /**
   * Get the qualified name of this class.
   *
   * @return qualified name of the class.
   */
  public String qualifiedName() {
    return this.qualifiedName;
  }

  public String toString() {
    return name();
  }
}
