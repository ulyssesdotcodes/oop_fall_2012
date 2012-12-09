package qimpp;

/**
 * Represents a type, which handles translation from Java to C++.
 * Subclasses include:
 * 
 * PrimitiveType
 * QualifiedType
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
  abstract String name(boolean withDimensions);

  abstract String qualifiedName();
  abstract String qualifiedName(boolean withDimensions);

  public int dimensions() {
    return this.dimensions;
  }

  public void dimensions(int dims) {
    if (this.name().equals("void")) { return; }
    this.dimensions = dims;
  }

  public String dimensionsTag() {
    StringBuilder dims = new StringBuilder();
    for (int i = 0; i < dimensions; i++) {
      dims.append("[]");
    }
    return dims.toString();
  }
}
