package qimpp;
import xtc.tree.*;
import java.util.*;

public class MethodResolver {

  private static InheritanceTreeManager inheritanceTree;

  /**
   * @param methodName the unmangled method name, printers should mangle 
   *  names by argument types
   * @param classDeclaration the Type node of the calling class
   * @param argumentTypes a Node containing the Types of the arguments
   * @param inheritanceTree the data structure describing the inheritance 
   *  relationships between translated classes
   * @return a GNode the mangled method name, and the return Type node
  */

  public static GNode resolve (String methodName, GNode classType, GNode argTypes, InheritanceTreeManager inheritanceTree, int callType ) {
    //TODO: Implement overloading. For now we just return the first method with the right name
    String className = Disambiguator.getDotDelimitedName(classType.getGeneric(0));
    GNode classDeclaration = inheritanceTree.getClassDeclarationNode(className);
    System.err.println("class declaration: " + classDeclaration);
    System.err.println("RESOLVING");
    System.err.println(methodName);
    System.err.println(argTypes);
    ArrayList<GNode> nameMatches = findNameMatches(methodName, 
                                                   classDeclaration); 
    ArrayList<GNode> argLengthMatches = findArgLengthMatches(nameMatches, 
                                                             argTypes.size());
    ArrayList<GNode> argCastMatches = findArgCastMatches(argLengthMatches, 
                                                         argTypes);
    System.err.println("ARGCAST MATCHES:");
    System.err.println(argCastMatches);

    GNode calledMethod = getMostSpecific(argCastMatches);

    MethodResolver.inheritanceTree = inheritanceTree;
    methodName = Type.getCppMangledMethodName(calledMethod);
     
    return GNode.create("CallInfo", methodName, calledMethod.getGeneric(1));
  }

  /**
   * Get the best match
   */
  private static GNode getMostSpecific(ArrayList<GNode> possibleMatches){
    // Bubble up, because I'm lazy
    // This will work without complaint if the result is actually ambiguous,
    // we are assuming the Java program actually works.

    for (int i = 0; i < possibleMatches.size() - 1; i++){
      GNode formalParameters = possibleMatches.get(i).getGeneric(2);
      GNode sourceTypes = GNode.create("ArgTypes");
      for ( Object o : formalParameters ){
        sourceTypes.add(((GNode)o).getGeneric(1));
      }

      formalParameters = possibleMatches.get(i+1).getGeneric(2);
      GNode targetTypes = GNode.create("ArgTypes");
      for (Object o : formalParameters ) {
        targetTypes.add(((GNode) o).getGeneric(1));
      }

      if (isCastable(sourceTypes, targetTypes)){
        possibleMatches.set(i + 1, possibleMatches.get(i));
      }
    }

    return possibleMatches.get(possibleMatches.size() - 1);
  }

  /**
   * Check if one type is upcastable to another
   */
  private static boolean isCastable(GNode sourceArgTypes, GNode targetArgTypes){
   System.err.println("CHECKING ARGS");
   System.err.println(sourceArgTypes);
   System.err.println(targetArgTypes);
   for (int i = 0; i < targetArgTypes.size(); i++){
    // They must both be PrimitiveType or both QualifiedIdentifier
    GNode targetType = targetArgTypes.getGeneric(i);
    GNode sourceType = sourceArgTypes.getGeneric(i);

    if (!targetType.getGeneric(0).getName().equals(sourceType.getGeneric(0).getName())){
      System.err.println("PRIMITIVE/QUALIFIED MISMATCH"); 
      return false;
    }
    
    if (targetType.getGeneric(0).getName().equals("QualifiedIdentifier")){
      if (!isClassCastable(sourceType, targetType)){
        System.err.println("NOT UPCASTABLE");
        return false;
      }
    }

    // Primitive type
    else {
      if (!isPrimitiveTypeCastable(sourceType, targetType)){
        System.err.println("PRIMITIVE TYPE NOT UPCASTABLE");
        return false;
      }
    }
   }

   return true;
  } 

  /**
   * Determine if one class type is castable to another
   */
  private static boolean isClassCastable(GNode sourceType, GNode targetType){
    String sourceName = Disambiguator.getDotDelimitedName(sourceType.getGeneric(0));
    String targetName = Disambiguator.getDotDelimitedName(targetType.getGeneric(0));

    if (sourceName.equals(targetName)) return true;

    GNode sourceClassTreeNode = inheritanceTree.getClassTreeNode(sourceName);
    GNode targetClassTreeNode = inheritanceTree.getClassTreeNode(targetName);

    while (sourceName != "java.lang.Object"){
      sourceClassTreeNode = (GNode)sourceClassTreeNode.getProperty(InheritanceTreeManager.PARENT_CLASS);
      sourceName = ((GNode)sourceClassTreeNode.getProperty(InheritanceTreeManager.CLASS_DECLARATION)).getString(0);

      if (sourceName.equals(targetName)) return true;
    }

    return false;
  }

  /**
   * Determine if an primitive argument of one type can be casted to another
   */
  private static boolean isPrimitiveTypeCastable(GNode sourceType, GNode targetType){
    String sourceName = sourceType.getGeneric(0).getString(0);
    String targetName = targetType.getGeneric(0).getString(0);

    return Type.canWiden(sourceName, targetName);    
  }

  /**
   * Get all the methods with matching length of arguments
   */
  private static ArrayList<GNode> findArgLengthMatches(ArrayList<GNode> nameMatches, int numArgs){
    ArrayList<GNode> lengthMatches = new ArrayList<GNode>();

    for (GNode n : nameMatches){
      GNode args = n.getGeneric(2);
      if (args.size() == numArgs){
        lengthMatches.add(n);
      }
    }

    return lengthMatches;
  }

  /**
   * Get all the methods whose arguments can be cast to from the given argument types
   */
  private static ArrayList<GNode> findArgCastMatches(ArrayList<GNode> argLengthMatches, GNode argTypes){
    ArrayList<GNode> argCastMatches = new ArrayList<GNode>();    
    
    for ( Object o : argLengthMatches ){
      GNode formalParameters = ((GNode)o).getGeneric(2);
      GNode targetArgTypes = GNode.create("ArgTypes");
      for ( Object p : formalParameters ){
        targetArgTypes.add(((GNode)p).getGeneric(1)); 
      }
      if (isCastable(argTypes, targetArgTypes)){
        argCastMatches.add((GNode)o);
      }
    }
    return argCastMatches;
  }

  private static ArrayList<GNode> findNameMatches(String methodName,
                                                  GNode classDeclaration) {
    GNode methodContainer = classDeclaration.getGeneric(4);
    ArrayList<GNode> matches = new ArrayList<GNode>();
    //System.err.println(methodContainer);
    //System.err.println("MethodContainer size");
    //System.err.println(methodContainer.size());
    for (int i = 0; i < methodContainer.size(); i++){
      GNode method = methodContainer.getGeneric(i);
      if (method.getName().equals("InheritedMethodContainer")){
        if (method.getGeneric(0).getString(0).equals(methodName))
          matches.add(method.getGeneric(0));
      }
      else{
        System.out.println("Method name");
        System.out.println(method.getString(0));
        if (method.getString(0).equals(methodName)) {

          matches.add(method);
        }
      }
    }
    return matches;
  }
} 
