import xtc.tree.*;

public class MethodResolver {
  /**
   * @param methodName the unmangled method name, printers should mangle names by argument types
   * @param classDeclaration the Type node of the calling class
   * @param argumentTypes a Node containing the Types of the arguments
   * @param inheritanceTree the data structure describing the inheritance relationships between translated classes
   * @return a GNode the mangled method name, and the return Type node
  */
  public static GNode resolve (String methodName, GNode classType, GNode argumentTypes, InheritanceTreeManager inheritanceTree ) {
    return null;
  }
} 
