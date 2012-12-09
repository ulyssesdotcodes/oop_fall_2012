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
public class Klass {
  /**
   * Member of a class.
   */
  abstract class Member {
    String name;            // might need to be freshly generated
    // TODO: also need a method for generating fully qualified names
    Klass implementor;
    Klass of;
    Node body;
    Type type;
    boolean isStatic;

    /**
     * Accessor for name.
     *
     * @return field name.
     */
    public String name() {
      return this.name;
    } 

    /**
     * Setter for name.
     *
     * @param field name.
     * @return member.
     */
    public Member name(String name) {
      this.name = name;
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
      this.name           = null;
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
        // TODO: match implementations on more than just name
        if (field.name().equals(this.name)) {
          field.implementor = Klass.this;
          field.body(this.body());
          return;
        }  
      }
      Klass.this.fields.add(this);
    }

    public String toString() {
      return this.type.qualifiedName() + ' ' +
        this.name + ';';
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
    String resolvedName;

    public Method() {
      this.implementor  = Klass.this;
      this.type         = null; // to set in Analyze.java via type(..)
      this.name         = null; // to set in Analyze.java via name(..)
      this.body         = null; // to set in Analyze.java via body(..)
      this.resolvedName = this.name;
      this.parameters   = new ArrayList<ParameterVariable>();
      this.isStatic     = false;
      this.of           = Klass.this;
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
        // TODO: match implementations on more than just name
        if (method.name().equals(this.name) &&
            method.parameters().size() == this.parameters.size()) {
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
     * Determines whether this method is more specific than the
     * specified method. Assumes the method is applicable.
     *
     * TODO: Hacky casting.
     *
     * @param m2 Method to compare.
     * @return whether this method is more specific than the specified method.
     */
    public boolean isMoreSpecificThan(Method m2) {
      Method m1 = this; 
      ArrayList<ParameterVariable> m1P = m1.parameters();
      ArrayList<ParameterVariable> m2P = m2.parameters();

      for (int i = 0 ; i < m1P.size(); i++) {
        Type t1 = m1P.get(i).type();
        Type t2 = m2P.get(i).type();
        if (t1 instanceof QualifiedType && t2 instanceof QualifiedType) {
          if (((QualifiedType)t1).inheritsFrom((QualifiedType)t2)) return false;
        } else if (t1 instanceof PrimitiveType && t2 instanceof PrimitiveType) {
          if (((PrimitiveType)t1).inheritsFrom((PrimitiveType)t2)) return false;
        } else {
          return false;
        }
      }

      return true;
    }

    public String toString() {
      String tmp = this.type.qualifiedName() + ' ' + this.name + '(';
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

  // TODO: Might delete.
  class Constructor {
    String name;
    String qualifiedName;
    ArrayList<ParameterVariable> arguments;
      // could be expression, so use GNode
    ArrayList<Node> initializers;  // also could be expression

    public Constructor() {
      this.name           = Klass.this.name;
      this.qualifiedName = Klass.this.qualifiedName;
    }


    
  }


  // ===========================================================================

  /** Class name. */
  String name;

  /** Fully qualified class name. */
  String qualifiedName;

  /** Parent class, if any. */
  Klass parent;

  /** Type of class. */
  QualifiedType type;

  /** Class fields. */
  ArrayList<Field> fields;

  /** Methods of a class. */
  ArrayList<Method> methods;

  /** Scope of the class. */
  Scope scope;

  /**
   * Klass constructor. If a class doesn't explicitly inherit from
   * anything, it inherits from Object. Create it as so:
   *   
   *   new Klass("A", new Klass("Object", null))
   *
   * An object should not be instantiated with no parent, unless that
   * parent's name is "Object"
   *
   * @param name Name of the class.
   * @param parent The parent class.
   */
  public Klass(String name, Klass parent) {
    // copy methods and fields from parent if it exists
    // TODO: only inherit accessible fields (i.e. not private ones)
    ArrayList<Method> methods = generateObjectMethods();
    ArrayList<Field> fields = new ArrayList<Field>();
    if (null != parent) { 
      methods = new ArrayList<Method>(parent.methods());
      fields = new ArrayList<Field>(parent.fields());
    }

    this.name           = name;
    this.qualifiedName  = Utilities.resolve(name, false);
    this.parent         = parent;
    this.fields         = fields;
    this.methods        = methods;
    this.type           = new QualifiedType(name);
  }

  public ArrayList<Method> generateObjectMethods() {
    // First: Object methods
    Method hashCode = new Method();
    hashCode.makeStatic();
    hashCode.name("hashCode");
    hashCode.type(new PrimitiveType("int"));
    hashCode.implementor(this);

    Method equals = new Method();
    equals.makeStatic();
    equals.name("equals");
    equals.type(new PrimitiveType("boolean"));
    equals.implementor(this);
    equals.addParameter(new ParameterVariable(new QualifiedType("Object"), 
                                              "other"));

    Method getClass = new Method();
    getClass.makeStatic();
    getClass.name("getClass");
    getClass.type(new QualifiedType("Class"));
    getClass.implementor(this);

    Method toString = new Method();
    toString.makeStatic();
    toString.name("toString");
    toString.type(new QualifiedType("String"));
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
   * TODO: Can we figure out a way to get rid of this?
   *  It's powerful but it seems like it could 
   *  potentially cause issues in class linking.
   *
   *  @param parent Parent class to set.
   *  @return this Klass object.
   */
  public Klass parent(Klass parent) {
    this.parent = parent;
    return this;
  }

  /**
   * Get methods of the class.
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
   * Get type of the class.
   *
   * @return type of the class.
   */
  public QualifiedType type() {
    return this.type;
  }

  /**
   * Get the scope of the class.
   *
   * @return scope of the class.
   */
  //public Scope getScope() {
  //  return this.scope;
  //}
  
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
}
