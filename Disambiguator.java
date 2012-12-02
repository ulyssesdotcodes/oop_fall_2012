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
  public static GNode disambiguate(String name){
  
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

  public static String getDotDelimitedName(GNode qualifiedIdentifier){
    StringBuilder nameBuilder = new StringBuilder();
    for ( int i = 0; i < qualifiedIdentifier.size(); i++ ) {
      nameBuilder.append(qualifiedIdentifier.getString(i));
      if ( i < qualifiedIdentifier.size() - 1 ) {
        nameBuilder.append(".");
      } 
    }

    return nameBuilder.toString();
  }
  
  /*
  setPackage()
  addImport()
  clearImports()
  */
}
