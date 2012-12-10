package qimpp;

/**
 * Represents a type, which handles translation from Java to C++.
 * Subclasses include:
 * 
 * PrimitiveType
 * Klass (QualifiedType)
 *
 * @author Qimpp
 */
public abstract class Type {
  /** Unqualified C++ name. */
  String name;
  
  /** Qualified C++ name. */
  String qualifiedName;
  
  /** Dimensions of array. */
  int dimensions;

  abstract String name();
  abstract String qualifiedName();

  public int dimensions() {
    return this.dimensions;
  }

  public void dimensions(int dims) {
    this.dimensions = dims;
  }

  public String dimensionsSuffix() {
    StringBuilder dims = new StringBuilder();
    for (int i = 0; i < dimensions; i++) {
      dims.append("[]");
    }
    return dims.toString();
  }

  public boolean equals(Object o) {
    if (o == this) { return true; }
    if (o == null || o.getClass() != this.getClass()) { return false; }

    Type t = (Type)o;
    return t.qualifiedName().equals(this.qualifiedName());
  }
}
