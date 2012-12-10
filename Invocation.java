
package qimpp;

import java.util.ArrayList;


/**
 * An object meant to help resolve methods.
 * It is especially useful for resolving method overloading.
 * 
 * @author Qimpp
 */
public class Invocation {
  /** Selectors for method invocation. */
  ArrayList<String> selectors;
  
  /** Name of the method. */
  String identifier;

  /** Invocation parameter types. */
  ArrayList<Type> arguments;

  // ===========================================================================

  public Invocation(ArrayList<String> selectors,
                    String identifier,
                    ArrayList<Type> arguments) {
    this.selectors  = selectors;
    this.identifier = identifier;
    this.arguments  = arguments;
  }
                    
  /**
   * Get the selectors.
   *
   * @return selectors.
   */
  public ArrayList<String> selectors() {
    return this.selectors;
  }

  /**
   * Get the name of the method.
   *
   * @return method name.
   */
  public String identifier() {
    return this.identifier;
  }

  /**
   * Get the invocation parameters.
   *
   * @return invocation parameters.
   */
  public ArrayList<Type> arguments() {
    return this.arguments;
  }
}
