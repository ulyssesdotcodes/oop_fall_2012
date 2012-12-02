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

  /** Whether array. TODO: Don't initialize here. */
  boolean dimensions = false;

  public boolean hasDimensions() {
    return this.dimensions;
  }
}
