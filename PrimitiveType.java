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
  }};

  /** Primitive type names. */
  private String ccName;

  // ===========================================================================

  /**
   * Constructor. Be careful to pass in a Java primitive type that exists.
   *
   * @param name Name of Java primitive to translate to C++.
   */
  public PrimitiveType(String name) throws Exception {
    if (!isJavaPrimitive(name)) {
      throw new Exception(name + " is not a Java primitive type.");
    }

    this.ccName = ccPrimitives.get(name);
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
   * Unqualified C++ type name.
   *
   * @return translated unqualified C++ primitive type.
   */
  public String getName() {
    return this.ccName;
  }

  /**
   * Qualified C++ type name. For primitives, the qualified name is the 
   * unqualified name.
   *
   * @return translated qualified C++ primitive type.
   */
  public String getQualifiedName() {
    return this.ccName;
  }
}

