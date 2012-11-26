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
  abstract String getName();

  /** Qualified C++ name. */
  abstract String getQualifiedName();
}
