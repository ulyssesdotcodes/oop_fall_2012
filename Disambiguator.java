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

  /**
   * Turns a QualifiedIdentifier node into a java-style dot-delimited name
   */
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

  /**
   * Turns a QualifiedIdentifier node into a underscore-delimited name
   */
  public static String getUnderscoreDelimitedName(GNode qualifiedIdentifier){
    StringBuilder nameBuilder = new StringBuilder();
    for ( int i = 0; i < qualifiedIdentifier.size(); i++ ) {
      nameBuilder.append(qualifiedIdentifier.getString(i));
      if ( i < qualifiedIdentifier.size() - 1 ) {
        nameBuilder.append('_');
      } 
    }

    return nameBuilder.toString();
  }

  /**
   * Turns a QualifiedIdentifier into a :: delimited name
   */
  public static String getColonDelimitedName(GNode qualifiedIdentifier){
    StringBuilder nameBuilder = new StringBuilder();
    for ( int i = 0; i < qualifiedIdentifier.size(); i++ ) {
      nameBuilder.append(qualifiedIdentifier.getString(i));
      if ( i < qualifiedIdentifier.size() - 1 ) {
        nameBuilder.append("::");
      } 
    }

    return nameBuilder.toString();
  }

  /*
   * Gets the mangled method name for declaration for overloading purposes
   * @param methodDeclaration a MethodDeclaration node
   * @return The mangled method name
   */
  public static String getMethodOverloadName(GNode methodDeclaration){
    StringBuilder name = new StringBuilder();
    name.append(methodDeclaration.getString(3));

    GNode formalParameters = methodDeclaration.getGeneric(4);
    for (Object o : formalParameters){
      name.append('_');
      GNode type = ((GNode)o).getGeneric(1).getGeneric(0);
      if (type.getName().equals("QualifiedIdentifier")){
        name.append(getUnderscoreDelimitedName(type));
      }
      else {
        name.append(type.getString(0));
      }
    }

    return name.toString();
  }
  
  /*
  setPackage()
  addImport()
  clearImports()
  */
}
