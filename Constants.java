
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


}
