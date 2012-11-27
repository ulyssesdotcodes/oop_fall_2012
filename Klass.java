package qimpp;

import java.util.ArrayList;

import qimpp.Type;


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

    /**
     * Accessor for name.
     *
     * @return field name.
     */
    public String getName() {
      return this.name;
    } 

    /**
     * Accessor for type.
     *
     * @return field type.
     */
    public Type getType() {
      return this.type;
    }
  }

  // ===========================================================================

  /**
   * Field of a class. Is a member.
   */
  class Field extends Member {

    /**
     * Field constructor.
     *
     * @param name The field name.
     * @param type The type of the field.
     */
    public Field(String name, Type type) {
      this.implementor  = Klass.this;
      this.name         = name;
      this.type         = type;

      Klass.this.fields.add(this);
    } 
  
  }

  // ===========================================================================

  /**
   * Method of a class. Is a member.
   */
  class Method extends Member {
    
    /** Method parameters list. */
    ArrayList<Type> parameters;

    /** Body. */
    // TODO


    /**
     * Method constructor.
     *
     * @param name The method name.
     * @param type The return type of the method.
     */
    public Method(String name,
                  Type type) {
      this(name, type, new ArrayList<Type>());
    }

    /**
     * Method constructor.
     *
     * @param name The method name.
     * @param type The return type of the method.
     * @param parameters The method parameters, if any.
     */
    public Method(String name,
                  Type type,
                  ArrayList<Type> parameters) {
      this.implementor  = Klass.this;
      this.name         = name;
      this.type         = type;
      this.parameters   = parameters;

      Klass.this.methods.add(this);
    }

    /**
     * Accessor for parameters.
     *
     * @return method parameters, if any.
     */
    public ArrayList<Type> getParameters() {
      return this.parameters;
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




}
