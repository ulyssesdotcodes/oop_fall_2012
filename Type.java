package qimpp;

import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;
import xtc.tree.Printer;

import xtc.util.Tool;

import java.util.HashMap;


/**
 * Converts Java PrimitiveType GNode objects into the corresponding C++ types.
 *
 * @author QIMPP
 */
class Type {
  /**
   * For reference:
   * Java primitive types: int, long, short, float, double, char, boolean
   * C++ primitive types: int, long int, short int, float, double, char, bool
   *
   * Then there's weird stuff with signed vs unsigned char that Grimm will
   * try to trip us up on.
   */

  // not using generics to specify key, value types, so need to suppress warnings
  @SuppressWarnings(value = "unchecked")
  static HashMap<String, String> primitives = new HashMap<String, String>() {{
    put("long", "signed int64_t");
    put("int", "int32_t");
    put("short", "signed int16_t");
    put("byte", "signed int8_t");
    put("float", "float");
    put("double", "double");
    put("char", "char");
    put("boolean", "bool");
  }};

  /**
   * Get C++ equivalent of primative type.
   *
   * @param type Name of primitive type.
   * @returns C++ equivalent of primative type.
   */
  static String primitiveType(String type){
    return primitives.get(type);
  }
 
  /**
   * Get C++ qualified identifier.
   *
   * @param type Name of qualified type.
   * @returns C++ equivalent of qualified type.
   */
  static String qualifiedIdentifier(String type){
    if(type.equals("String")) return "java::lang::String";
    else return type;
  }
}
