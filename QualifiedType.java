package qimpp;

import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

/**
 * Extends abstract Type class to represent qualified types.
 *
 * TODO: Understand relationship between Klass and Type.
 *  Right now a Klass has-a QualifiedType. Does QualifiedType have-a Klass?
 *
 * @author Qimpp
 */
public class QualifiedType extends Type {

  /** Qualified C++ type names. */
  private String context;
  private String[] ancestry;

  // ===========================================================================

  /**
   * Determines whether this QualifiedType instance is a java.lang object.
   *
   * @return whether this QualifiedType instance is a java.lang object.
   */
  public boolean isPredefinedType() {
    return (this.qualifiedName().equals("::java::lang::Object")) ||
           (this.qualifiedName().equals("::java::lang::String")) ||
           (this.qualifiedName().equals("::java::lang::Class"));
  }

  /**
   * Determine whether this QualifiedType instance inherits from the
   * specified QualifiedType object.
   *
   * @param qt2 qualified type ancestor candidate.
   * @return whether this QualifiedType instance inherits from the
   * specified QualifiedType object.
   */
  public boolean inheritsFrom(QualifiedType qt2) {
    QualifiedType qt1 = this;
    while (null != qt1) {
      if (qt1.qualifiedName().equals(qt2.qualifiedName())) return true;
      if (!qt1.isPredefinedType()) {
        qt1 = Store.getClass(this.name).parent().type();
      } else { qt1 = null; }
    }
    return false;
  }

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
    return name(true);
  }

  public String name(boolean withDimensions) {
    String name = this.name;
    if (withDimensions) {
      for (int i = 0; i < this.dimensions(); i++) {
        name += "[]";
      }
    }
    return name;
  }

  public String qualifiedName() {
    return qualifiedName(true);
  }

  public String qualifiedName(boolean withDimensions) {
    String qualifiedName = this.qualifiedName;
    if (withDimensions) {
      for (int i = 0; i < this.dimensions(); i++) {
        qualifiedName += "[]";
      }
    }
    return qualifiedName;
  }

  public String context() {
    return this.context;
  }

}

