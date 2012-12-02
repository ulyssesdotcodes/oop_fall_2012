package qimpp;

import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

/**
 * Extends abstract Type class to represent qualified types.
 *
 * @author Qimpp
 */
public class QualifiedType extends Type {

  /** Qualified C++ type names. */
  private String name;
  private String qualifiedName;
  private String context;
  private String[] ancestry;

  // ===========================================================================

  /**
   * Constructor
   */
  public QualifiedType(String name) {
    this(new String[]{name});
  }

  public QualifiedType(String[] ancestry) {
    this(ancestry, 0);
  }

  public QualifiedType(String[] ancestry, int dimensions) {

    // set name
    this.name = ancestry[ancestry.length - 1];

    // set dimensions
    this.dimensions = dimensions;
    
    // set qualified name
    StringBuilder qualifiedName = new StringBuilder();
    for (int i = 0; i < ancestry.length; i++) {
      qualifiedName.append(Constants.QUALIFIER);
      qualifiedName.append(ancestry[i]);
    }
    this.qualifiedName = qualifiedName.toString();

    // in case already a predefined type
    if (this.name.equals("Object")) {
      this.qualifiedName = "::java::lang::Object";
    } else if (this.name.equals("String")) {
      this.qualifiedName = "::java::lang::String";
    } else if (this.name.equals("Class")) {
      this.qualifiedName = "::java::lang::Class";
    }
   
    // set context 
    StringBuilder context = new StringBuilder();
    if (ancestry.length == 1) {
      context.append(Constants.QUALIFIER);
    } else {
      for (int i = 0; i < ancestry.length - 1; i++) {
        context.append(Constants.QUALIFIER);
        context.append(ancestry[i]);
      }
    }
    this.context = context.toString();

    // fill the ancestry
    this.ancestry = ancestry;
    for (int i = 0; i < ancestry.length; i++) {
      this.ancestry[i] = ancestry[i];
    }

  }
  
  // ===========================================================================

  public String name() {
    String name = this.name;
    for (int i = 0; i < this.dimensions(); i++) {
      name += "[]";
    }
    return name;
  }

  // TODO: Handle multiple dimensions.
  public String qualifiedName() {
    String qualifiedName = this.qualifiedName;
    for (int i = 0; i < this.dimensions(); i++) {
      qualifiedName += "[]";
    }
    return qualifiedName;
  }

  public String context() {
    return this.context;
  }

}

