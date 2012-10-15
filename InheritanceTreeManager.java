package qimpp;

import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;

import xtc.tree.Printer;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.ArrayList;

/** Maintains a tree with pointers class definitions for all classes referenced in the program being translated 
 *
 *  @author QIMPP
 * */

public class InheritanceTreeManager {

  /* TODO: Implement package-based dereferencing: right now we're just naively looking by name */
  /** Maps class names to their class tree nodes */
  HashMap<String, GNode> classNameMap;

  /** Root of the package/class tree */
  GNode root;
  /** The class everyone inherits from (Object) */
  GNode rootClassNode;

  /** Construct the tree with the given root object node (java.lang.Object)
   *
   * @param rootClass The fully qualified name of the root class ({"java", "lang", "Object"})
   * @param rootClassDeclaration the ClassDeclaration of the root class containing its fields and methods
   * */
  public InheritanceTreeManager(String[] rootClass, GNode rootClassDeclaration){
    root = GNode.create("ClassStructureRoot");

    GNode rootClassTreeNode = GNode.create("ClassTreeNode");
    rootClassTreeNode.setProperty("ClassName", rootClass[rootClass.length - 1]);
    rootClassTreeNode.setProperty("ClassDeclaration", rootClassDeclaration);
    
    insertClass(rootClass, rootClassTreeNode);

    // Set a direct reference to what everyone inherits from
    this.rootClassNode = rootClassTreeNode;
  }

  private final static String[] rootClassName = {"java", "lang", "Object"};

  /** An overload where java.lang.Object is implicit */
  public InheritanceTreeManager(GNode rootClassDeclaration) {  
    this(rootClassName, rootClassDeclaration);

  }

  /** Insert a tree node into the tree. Its name and parent should already be set 
   * @param className the fully qualified name of the node
   * @param classTreeNode the node containing the classDefinition and a pointer to the parent of this object*/
  public boolean insertClass(String[] className, GNode classTreeNode){
    if ( null != dereference(className)){
      // This class should not already be inserted! 
      return false;
    }
    else{
      // Starting at the deepest already-defined point in the tree, make new entries
      GNode n = deepestPackageNode;
      GNode current = n;
      for(int i = dereferenceDepth; i < className.length - 1; i++){
        GNode newQualifier = GNode.create("PackageQualifier");
        newQualifier.setProperty("QualifierName", className[i]);
        current.add(newQualifier);
        current = newQualifier;

      }

      // Finally, add the actual class tree node
      current.add(classTreeNode);
      return true; //success
    }
  }


  //
  // State variables for dereference()
  //

  /** The node found by dereference() */
  private GNode foundNode;

  /** The furthest we got dereferencing the node in the package tree */
  private GNode deepestPackageNode;

  /** Index of the qualifier we're up to */
  private int dereferenceDepth;

  /** Get the ClassNode based on its fully qualified package name 
   *  Also sets deepestPackageNode
   * */
  private GNode dereference(final String[] className){
    // Traverse with state variables
    foundNode = null;
    dereferenceDepth = 0;
    deepestPackageNode = null;

    new Visitor() {

      public void visitClassTreeNode(GNode n){
        if (getClassTreeNodeName(n).equals(className[dereferenceDepth])){
          foundNode = n;
        }
      }

      public void visitPackageQualifier(GNode n){
        if (n.getStringProperty("QualifierName").equals(className)) {
          dereferenceDepth++;
          deepestPackageNode = n;
          visit(n);
        }
      }

      public void visit(Node n) {
        for (Object o : n) if (o instanceof Node) dispatch((Node)o);
      }

    }.dispatch(root);

    return foundNode;
  }

  private String getClassTreeNodeName(GNode n){
    return n.getStringProperty("ClassName");
  }

  /** Gets an ordered list of the inheritance hierarchy for a class 
   *
   * @param className an array of Strings defining the fully qualified name of the class we want
   * For example, the array for Point in package org.xtc should be ["org", "xtc", "Point"]
   * For Point in the root package (the "src" directory) should just be ["Point"]
   * */
  public ArrayList<GNode> getParentList(String[] className){
    return null;
  }

  /** Inserts a class into the tree and parents it to the given object
   *
   *  @param className the fully-qualified name of the class to insert
   *  @param parentName the fully-qualified name of the classes parent, or null if it's Object
   *
   *  @throws RuntimeException with the message "No such parent class [className]"
   *  */
  public void insertClass(String[] className, String[] parentName, GNode classDeclaration){
    GNode classTreeNode = GNode.create("ClassTreeNode");
    if ( null == parentName ) {
      // If the parent isn't specified, set it to Object
      classTreeNode.setProperty("ParentClass", rootClassNode);
    } 
    else{
      GNode parent = dereference(parentName);
      if ( null == parent) {

        String parentNameString = "";
        for (int i = 0; i < parentName.length; i++){
          parentNameString += parentName[i] + ".";
        }

        throw new RuntimeException("No such parent class " + parentName[parentName.length - 1]);
      }

      classTreeNode.setProperty("ParentClass", parent);
    }

    classTreeNode.setProperty("ClassName", className[className.length - 1]);
    classTreeNode.setProperty("ClassDeclaration", classDeclaration);

    insertClass(className, classTreeNode);
  } 



}
