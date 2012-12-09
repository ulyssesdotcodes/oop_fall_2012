
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

  /** Invocation parameters. */
  ArrayList<ParameterVariable> parameters;

  // ===========================================================================

  public Invocation(ArrayList<String> selectors,
                    String identifier,
                    ArrayList<ParameterVariable> parameters) {
    this.selectors  = selectors;
    this.identifier = identifier;
    this.parameters = parameters;
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
  public ArrayList<ParameterVariable> parameters() {
    return this.parameters;
  }
}
