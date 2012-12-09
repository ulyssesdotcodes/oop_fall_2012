package qimpp;
import xtc.tree.*;
import java.util.*;

public class MethodResolver {
  /**
   * @param methodName the unmangled method name, printers should mangle names by argument types
   * @param classDeclaration the Type node of the calling class
   * @param argumentTypes a Node containing the Types of the arguments
   * @param inheritanceTree the data structure describing the inheritance relationships between translated classes
   * @return a GNode the mangled method name, and the return Type node
  */
  public static GNode resolve (String methodName, GNode classType, GNode argumentTypes, InheritanceTreeManager inheritanceTree ) {
    //TODO: Implement overloading. For now we just return the first method with the right name
    GNode classDeclaration = inheritanceTree.getClassDeclarationNode(Disambiguator.getDotDelimitedName(classType.getGeneric(0)));
    ArrayList<GNode> nameMatches = findNameMatches(methodName, classDeclaration); 
     
    return GNode.create("CallInfo", nameMatches.get(0).getString(0), nameMatches.get(0).getGeneric(1));
  }

  private static ArrayList<GNode> findNameMatches(String methodName, GNode classDeclaration){
    GNode methodContainer = classDeclaration.getGeneric(4);
    ArrayList<GNode> matches = new ArrayList<GNode>();
    for (int i = 0; i < methodContainer.size(); i++){
      GNode method = methodContainer.getGeneric(i);
      if (method.getName().equals("InheritedMethodContainer")){
        if (method.getGeneric(0).getString(0).equals(methodName))
          matches.add(method.getGeneric(0));
      }
      else{
        if (method.getString(0).equals(methodName)) {
          matches.add(method);
        }
      }
    }
    return matches;
  }
} 
