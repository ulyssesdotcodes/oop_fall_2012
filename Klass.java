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
    Klass implementor;
    Type type;
    boolean isStatic;

    /**
     * Accessor for name.
     *
     * @return field name.
     */
    public String getName() {
      return this.name;
    } 

    /**
     * Setter for name.
     *
     * @param field name.
     * @return member.
     */
    public Member setName(String name) {
      this.name = name;
      return this;
    }

    /**
     * Accessor for type.
     *
     * @return field type.
     */
    public Type getType() {
      return this.type;
    }

    /**
     * Setter for type.
     *
     * @param field type.
     * @return member.
     */
    public Member setType(Type type) {
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
  }

  // ===========================================================================

  /**
   * Field of a class. Is a member.
   */
  class Field extends Member {

    /** Field initialization. */
    Node initialization;

    public Field() {
      this.implementor  = Klass.this;
      this.name           = null;
      this.type           = null;
      this.initialization = null;
      this.isStatic       = false;
      Klass.this.fields.add(this);
    }

    /**
     * Accessor for field initialization block.
     *
     * @return field initialization block.
     */
    public Node getInitialization() {
      return this.initialization;
    }

    /**
     * Setter for field initialization block.
     *
     * @param field initialization block.
     */
    public void setInitialization(Node initialization) {
      this.initialization = initialization;
    }
  
    public String toString() {
      return this.type.getQualifiedName() + ' ' +
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

    /** Body. */
    // TODO: Right now, I'm just copying the method block.
    Node body;

    public Method() {
      this.implementor  = Klass.this;
      this.type         = null; // to set in Analyze.java via setType
      this.name         = null; // to set in Analyze.java via setName
      this.body         = null; // to set in Analyze.java via setBody
      this.parameters   = new ArrayList<ParameterVariable>();
      this.isStatic     = false;
      Klass.this.methods.add(this);
    }
    
    /**
     * Accessor for parameters.
     *
     * @return method parameters, if any.
     */
    public ArrayList<ParameterVariable> getParameters() {
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
     * Accessor for method body.
     *
     * @return method body.
     */
    public Node getBody() {
      return this.body;
    }

    /**
     * Set method body.
     *
     * @param body Node method body.
     */
    public void setBody(Node body) {
      this.body = body;
    }

    public String toString() {
      String tmp = this.type.getQualifiedName() + ' ' + this.name + '(';
      Iterator<ParameterVariable> it = parameters.iterator();
      while (it.hasNext()) {
        ParameterVariable parameter = it.next();
        tmp += parameter.getType().getQualifiedName() + ' ' + 
          parameter.getName(); 
        if (it.hasNext()) {
          tmp += ", ";
        }
      }
      return tmp;
    }

  }


  // ===========================================================================

  /** Class name. */
  String name;

  /** Parent class, if any. */
  Klass parent;

  /** Class fields. */
  ArrayList<Field> fields;

  /** Methods of a class. */
  ArrayList<Method> methods;

  /** Scope of the class. */
  Scope scope;

  /**
   * Klass constructor.
   * This singular overloaded form explicitly states no parent, so it
   * should inherit from Object. In this case, null implies Object.
   *
   * @param name Name of the class.
   */
  public Klass(String name) {
    this(name, null);
  }

  /**
   * Klass constructor.
   *
   * @param name Name of the class.
   * @param parent The parent class.
   */
  public Klass(String name, Klass parent) {
    this(name, parent, new ArrayList<Field>(), new ArrayList<Method>());
  }

  /**
   * Klass constructor.
   *
   * @param name Name of the class.
   * @param parent The parent class.
   * @param fields The class member fields.
   * @param methods The class member methods.
   */
  public Klass(String name,
               Klass parent,
               ArrayList<Field> fields,
               ArrayList<Method> methods) {
    this.name    = name;
    this.parent  = parent;
    this.fields  = fields;
    this.methods = methods;
  }

  /**
   * Get class parent.
   *
   * @return class parent.
   */
  public Klass getParent() {
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
  public Klass setParent(Klass parent) {
    this.parent = parent;
    return this;
  }

  /**
   * Get methods of the class.
   *
   * @return methods of the class.
   */
  public ArrayList<Method> getMethods() {
    return this.methods;
  }

  /**
   * Get fields of the class.
   *
   * @return fields of the class.
   */
  public ArrayList<Field> getFields() {
    return this.fields;
  }

  /**
   * Get the scope of the class.
   *
   * @return scope of the class.
   */
  //public Scope getScope() {
  //  return this.scope;
  //}
}
