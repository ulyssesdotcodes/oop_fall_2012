
package qimpp;

import java.util.HashMap;

public class Constants {

  public static final boolean DEBUG = false;

  public static final String QUALIFIER = "::";
  public static final String JAVA_QUALIFIER = ".";

  public static final char START_OPAQUE = '(';
  public static final char END_OPAQUE = ')';

  public static final char JAVA_ESCAPE = 0x08;

  public static final String SCOPE = "qimpp.Constants.Scope";
  public static final String TYPE = "qimpp.Constants.Type";

  // ============================================================

  /** Output header. */
  public static final String OUTPUT_HEADER_FILE = "./output/out.h";
  
  /** Output implementation. */
  public static final String OUTPUT_IMPLEMENTATION_FILE = "./output/out.cc";

  // ============================================================
  
  /** Pre-defined type. */
  public static final HashMap<String, String> JAVA_LANG = 
    new HashMap<String, String>() {{
      put("Object", "");
      put("String", "");
      put("Class", "");
  }};

  public static final String JAVA_LANG_STRING = "::java::lang::String";
  public static final String JAVA_LANG_OBJECT = "::java::lang::Object";
  public static final String JAVA_LANG_CLASS = "::java::lang::Class";

  /** Primitive encoding characters. */
  public static final HashMap<String, Character> ENCODING = 
    new HashMap<String, Character>() {{
      put("boolean", 'Z');
      put("byte", 'B');
      put("char", 'C');
      put("double", 'D');
      put("float", 'F');
      put("int", 'I');
      put("long", 'J');
      put("short", 'S');
  }};


}
