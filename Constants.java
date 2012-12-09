
package qimpp;

import java.util.HashMap;

public class Constants {

  public static final boolean DEBUG = true;


  public static final String QUALIFIER = "::";

  public static final char START_OPAQUE = '(';
  public static final char END_OPAQUE = ')';

  public static final char JAVA_ESCAPE = 0x08;

  public static final String SCOPE = "qimpp.Constants.Scope";

  public static final String TYPE = "qimpp.Constants.Type";


  // Constants for the classification of PrimaryIdentifiers and SelectionExpressions
  public static final String IDENTIFIER_TYPE = "qimpp.Constants.IdentifierType";
  public static final String IDENTIFIER_DECLARATION = "qimpp.Constants.IdentifierDeclaration";

  public static final String CLASS_IDENTIFIER = "qimpp.Constants.ClassIdentifier";
  public static final String FIELD_IDENTIFIER = "qimpp.Constants.FieldIdentifier";
  public static final String STACKVAR_IDENTIFIER = "qimpp.Constants.StackvarIdentifier";
  public static final String QUALIFIED_CLASS_IDENTIFIER = "qimpp.Constants.QualifiedClassIdentifier";
  public static final String FOREIGN_CLASS_FIELD_IDENTIFIER = "qimpp.Constants.ForeignClassFieldIdentifier";
  public static final String PRINT_IDENTIFIER = "qimpp.Constants.PrintIdentifier";

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
