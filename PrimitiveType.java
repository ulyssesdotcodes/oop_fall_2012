package qimpp;

import java.util.Map;
import java.util.HashMap;

/**
 * Extends abstract Type class.
 *
 * @author Qimpp
 */
public class PrimitiveType extends Type {
  /** Maps Java primitives to C++ primitives. */
  protected static HashMap<String, String> ccPrimitives = 
    new HashMap<String, String>() {{
    put("long", "int64_t");
    put("int", "int32_t");
    put("short", "int16_t");
    put("byte", "int8_t");
    put("char", "char");
    put("float", "float");
    put("double", "double");
    put("boolean", "bool");
    put("void", "void");    // void isn't actually a type
  }};

  private String javaName;

  public boolean inheritsFrom(PrimitiveType pt2) {
    if (this.qualifiedName().equals(pt2.qualifiedName())) return true;
    return false;
  }

  // ===========================================================================

  /**
   * Constructor. Be careful to pass in a Java primitive type that exists.
   *
   * @param name Name of Java primitive to translate to C++.
   */
  public PrimitiveType(String name) {
    this.name          = ccPrimitives.get(name);
    this.javaName      = name;
    this.qualifiedName = ccPrimitives.get(name);
  }
  
  // ===========================================================================

  /**
   * Determine whether a type candidate is a Java primitive.
   *
   * @param candidate A Java primitive type candidate.
   * @return Boolean value indicating whether the typeCandidate is 
   * indeed a Java primitive type.
   */
  public static boolean isJavaPrimitive(String javaCandidate) {
    return ccPrimitives.get(javaCandidate) instanceof String ? true : false;
  }

  // ===========================================================================

  /**
   * Get encoding of primitive.
   *
   * @return encoding character.
   */
  public char encoding() {
    return Constants.ENCODING.get(this.javaName);
  }

  /**
   * Unqualified C++ type name.
   *
   * @return translated unqualified C++ primitive type.
   */
  public String name() {
    return this.name;
  }

  /**
   * Qualified C++ type name. For primitives, the qualified name is the 
   * unqualified name.
   *
   * @return translated qualified C++ primitive type.
   */
  public String qualifiedName() {
    return this.qualifiedName;
  }
}

