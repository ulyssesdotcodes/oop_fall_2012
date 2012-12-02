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
  // Nothing to see here.

  /** Unqualified C++ name. */
  abstract String name();

  /** Qualified C++ name. */
  abstract String qualifiedName();

  /** Dimensions of array. */
  int dimensions;

  public int dimensions() {
    return this.dimensions;
  }

  public void dimensions(int dims) {
    if (this.name().equals("void")) { return; }
    this.dimensions = dims;
  }
}
