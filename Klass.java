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
    final String name;            // might need to be freshly generated
    final Klass implementor;
    Type type;
  }

  /**
   * Field of a class. Is a member.
   */
  class Field extends Member {

    /**
     * Field constructor.
     *
     * @param name The field name.
     * @param implementor The class that implements this field.
     * @param type The type of the field.
     */
    public Field(String name, Klass implementor, Type type) {
      this.name         = name;
      this.implementor  = Klass.this;
      this.type         = type;
    } 
    
  }

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
     * @param implementor The class that implements the method.
     * @param type The return type of the method.
     * @param parameters The method parameters, if any.
     */
    public Method(String name,
                  Klass implementor, 
                  Type type,
                  ArrayList<Type> parameters) {
      this.name         = name;
      this.implementor  = Klass.this;
      this.type         = type;
      this.parameters   = parameters;
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


}
