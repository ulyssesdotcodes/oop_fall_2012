/*
Type(
  --QualifiedIdentifier(  
    "java",
    "lang",
    "String"
  )
)
*/

package qimpp;

import xtc.tree.GNode;

public class Disambiguator{
  public static String QUALIFIED_IDENTIFIER = "QualifiedIdentifier";


  /** Returns c++ qualified identifier for type name string */
  public GNode disambiguate(String name){
  
    GNode fullyQualifiedIdentifier = GNode.create(QUALIFIED_IDENTIFIER);
    
    if (name.equals("String") || name.equals("Object") || name.equals("Class")){
      fullyQualifiedIdentifier.add("java");
      fullyQualifiedIdentifier.add("lang");
      fullyQualifiedIdentifier.add(name);
    } else {
      String[] nameSplit = name.split("\\."); 
      for (int i = 0; i < nameSplit.length; i++){
        fullyQualifiedIdentifier.add(nameSplit[i]);
      }
    }
    
    return fullyQualifiedIdentifier;
  }
  
  /*
  setPackage()
  addImport()
  clearImports()
  */
}
