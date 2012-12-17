/*
 * xtc - The eXTensible Compiler
 * Copyright (C) 2012 Robert Grimm
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
 * USA.
 */
package qimpp;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.PrintWriter;
import java.util.HashMap;

import xtc.lang.JavaFiveParser;

import xtc.parser.ParseException;
import xtc.parser.Result;

import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;
import xtc.tree.Location;
import xtc.tree.Printer;

import xtc.util.Tool;

import java.util.ArrayList;

/**
 * A translator from (a subset of) Java to (a subset of) C++.
 *
 * @author QIMPP
 * @version $Revision$
 */
public class CPPAST {
  public GNode compilationUnit, directives, declarations, classes;
  HashMap<String, GNode> classesMap;
  HashMap<String, GNode> currentFieldMap; 
  HashMap<String, ArrayList<GNode> > currentMethodMap;

  /** Constructor */  
  public CPPAST() {
      compilationUnit = GNode.create("CompilationUnit");
      compilationUnit.addNode(createDefaultDirectives());
      declarations = GNode.create("Declarations");
      compilationUnit.addNode(declarations);
      classes = GNode.create("Classes");
      compilationUnit.addNode(classes);
      classesMap = new HashMap<String, GNode>();
      
      //setup HashMaps as needed
      setupMaps();
  }

  /** Creates default directives in AST. */
  GNode createDefaultDirectives() {
    directives = GNode.create("Directives");
    directives.addNode(GNode.create("Pragma")).getGeneric(directives.size()-1).add("once");
   
    GNode includeDirectives = GNode.create("IncludeDirectives");
    includeDirectives.addNode(GNode.create("QuotedForm")).getGeneric(includeDirectives.size()-1).add("java_lang");
    includeDirectives.addNode(GNode.create("AngleBracketForm")).getGeneric(includeDirectives.size()-1).add("stdint");
    
    directives.addNode(includeDirectives);
    return directives;
  }
  
  
  /**
   * Add class node.
   *
   * @param name Name of node.
   * @param parent Parent node name.
   * @returns class node.
   */  
  GNode addClass(String name, String parent){
    System.out.println("Adding class " + name);
  
    //Add to Structs
    GNode declaration = GNode.create("Declaration");
    declaration.add(name);
    declaration.addNode(GNode.create("Struct"));
    declaration.add(2, null);
    declarations.addNode(declaration);
    
    //Add to Classes with Name node, Fields node, ImplementedMethods node, and InheritedMethods node
    GNode classNode = GNode.create("ClassDeclaration");
    classNode.add(name);
    if(parent != null){
      classNode.addNode(GNode.create("Parent")).getNode(classNode.size()-1).add(parent);
    } else {
      classNode.addNode(GNode.create("Parent")).getNode(classNode.size()-1).add(generateObjectType());
    }
    classNode.addNode(GNode.create("Constructors"));
    classNode.addNode(GNode.create("Fields"));
    classNode.addNode(GNode.create("Methods"));
    currentFieldMap = new HashMap<String,GNode>();
    classNode.setProperty("FieldMap", currentFieldMap);

    currentMethodMap = new HashMap<String, ArrayList<GNode> >();
    classNode.setProperty("MethodMap", currentMethodMap);

    classes.addNode(classNode);
    
    System.out.println("Class added");
    return classNode;
  }
 
  /**
   * Add class node.
   *
   * @param name Name of node.
   * @returns class node.
   */
  GNode addClass(String name){
    return addClass(name, null);
  }
  
  //Adding, getting, and removing fields
 
  /**
   * Add field node.
   *
   * @param fullName Name of node, prepended with the fully qualified class name of the class that owns it
   * @param ambigName the ambiguous name, no prepend, to be entered into the field map
   * @param type Type node.
   * @param classNode ClassDeclaration node.
   * @returns field node.
   */ 
  GNode addField(String fullName, String ambigName, GNode type, GNode classNode, boolean isStatic){
    //Get the fields node
    //String classFieldName = classNode.getString(0) + "_" + name;
      
    GNode fieldNode = GNode.create("FieldDeclaration");
    fieldNode.add(fullName);
    fieldNode.addNode(type);
    classNode.getGeneric(3).addNode(fieldNode);
    currentFieldMap.put(ambigName, fieldNode);
    if(isStatic)
      fieldNode.setProperty("static", true);
    return fieldNode;
  }

  /**
   * Overload without adding the name to the field map
   */
  GNode addField(String name, GNode type, GNode classNode, boolean isStatic){
    GNode fieldNode = GNode.create("FieldDeclaration");
    fieldNode.add(name);
    fieldNode.add(type);
    classNode.getGeneric(3).addNode(fieldNode);
    if(isStatic)
      fieldNode.setProperty("static", true);
    return fieldNode;
  }
  
  /** 
   * Add constructor node.
   *
   * @param classNode The class node.
   * @returns constructor node.
   */
  GNode addConstructor(GNode classNode){
    GNode constructorNode = GNode.create("ConstructorDeclaration");
    constructorNode.add(null);
    constructorNode.addNode(GNode.create("Block"));
    classNode.getGeneric(2).addNode(constructorNode);
    return constructorNode;
  }

  /**
   * Add constructor instruction node.
   *
   * @param instruction Instruction node.
   * @param constructor Constructor node.
   */
  void addConstructorInstruction(GNode instruction, GNode constructor) {
    constructor.getGeneric(1).addNode(instruction);
  }

  /**
   * Add constructor parameter node.
   *
   * @param paramType Parameter type node.
   * @param param Parameter name.
   * @param constructor constructor.
   */  
  void addConstructorParameter(GNode paramType, String param, GNode constructor) {
    if(constructor.getGeneric(0) == null) constructor.add(0, GNode.create("Parameters"));
    GNode formalParameter = GNode.create("FormalParameter");
    formalParameter.add(param);
    formalParameter.addNode(paramType);
    constructor.getGeneric(0).addNode(formalParameter);
  }
 
  /**
   * Set constructor parameters with parameters node.
   *
   * @param parameters Parameters node.
   * @param constructor Constructors node.
   */ 
  void setConstructorParameters(GNode parameters, GNode constructor){
    constructor.remove(2);
    constructor.add(2, parameters);
  }

  /**
   * Set constructor instructions with block node.
   *
   * @param block Block node.
   * @param constructor Constructor node.
   */  
  void setConstructorInstructions(GNode block, GNode constructor){
    constructor.remove(1);
    constructor.add(1, block);
  }


  


  /**
   * Add method node.
   *
   * @param name Name of method node.
   * @param returnType Return type node for method.
   * @param classNode Class node.
   * @returns method node.
   */
  GNode addMethod(String name, GNode returnType, GNode classNode, GNode parameters) {
    // If this is the first method we are adding, add Object's methods first
    if(classNode.getGeneric(4).size() == 0){
      addAllInheritedMethods(generateObjectMethods(), classNode);
    }
  
    GNode methodNode = GNode.create("ImplementedMethodDeclaration");
    methodNode.add(name);
    methodNode.addNode(GNode.create("ReturnType")).getGeneric(methodNode.size()-1).add(returnType.getGeneric(0));
    methodNode.add(GNode.create("FormalParameters"));
    methodNode.addNode(GNode.create("Block"));
    
    //Find if a method exists with the same name and input. If it does, overwrite it with the new implemented method
    boolean overridingMethod = false;
    GNode methodsNode = classNode.getGeneric(4);
    
    setMethodParameters(parameters, methodNode);
    System.err.println("METHOD NAME");
    System.err.println(Type.getCppMangledMethodName(methodNode));
    for(int i = 0; i < methodsNode.size(); i++){
      GNode method = methodsNode.getGeneric(i);
      if(method.getName().equals("InheritedMethodContainer")) method = method.getGeneric(0);
        //TODO: Check for exact match
      if(Type.getCppMangledMethodName(method).equals(Type.getCppMangledMethodName(methodNode))){
        System.err.println("REPLACING METHOD");
        System.err.println("at " + i + " is " + Type.getCppMangledMethodName(method));
        methodsNode.set(i, methodNode);
        overridingMethod = true;
        break;
      }
    }
    
    //Otherwise just add it to the bottom of the methods
    if(!overridingMethod)
      methodsNode.addNode(methodNode);
    
    return methodNode;
  }

  /**
   * Add method node. Overload for inherited methods
   *
   * @param name Name of method node.
   * @param returnType Return type node for method.
   * @param classNode Class node.
   * @param from Name of class the method inherits from.
   * @returns method node.
   */
  GNode addMethod(String name, GNode returnType, GNode classNode, String from, GNode parameters) {
    GNode methodNode = GNode.create("InheritedMethodDeclaration");
    methodNode.add(name);
    methodNode.addNode(GNode.create("ReturnType")).getGeneric(methodNode.size()-1).add(returnType.getGeneric(0));
    methodNode.add(GNode.create("FormalParameters"));
    methodNode.addNode(GNode.create("From")).getNode(methodNode.size()-1).add(from);
    //Find if a method exists with the same name and input. If it does, overwrite it with the new implemented method
    boolean overridingMethod = false;
    GNode methodsNode = classNode.getGeneric(4);
    for(int i = 0; i < methodsNode.size(); i++){
      GNode method = methodsNode.getGeneric(i);
      if(method.getName().equals("InheritedMethodContainer")) method = method.getGeneric(0);
      if(Type.getCppMangledMethodName(method).equals(Type.getCppMangledMethodName(methodNode))){
        methodsNode.set(i, methodNode);
        overridingMethod = true;
        break;
      }
    }
    //Otherwise just add it to the bottom of the methods
    if(!overridingMethod)
      methodsNode.addNode(methodNode);
    return methodNode;
  }

  /**
   * Add method instruction node.
   *
   * @param instruction Instruction node.
   * @param method Method node.
   */  
  void addMethodInstruction(GNode instruction, GNode method) {
    method.getGeneric(3).addNode(instruction);
  }
 
  /**
   * Set method instruction node.
   *
   * @param block Block node.
   * @param method Method node.
   */ 
  void setMethodInstructions(GNode block, GNode method) {
    System.out.println("In CPPAST:setMethodInstructions");
    method.remove(3);
    method.add(3, block);
  }

  /*
   * Add method parameter.
   *
   * @param paramType Parameter type node.
   * @param param Parameter name.
   * @param method Method node.
   * @returns formal parameter node.
   */  
  GNode addMethodParameter(GNode paramType, String param, GNode method) {
    GNode formalParameter = GNode.create("FormalParameter");
    formalParameter.add(param);
    formalParameter.addNode(paramType);
    method.getGeneric(2).addNode(formalParameter);
    return formalParameter;
  }
  
    /**
    * Adds all inherited methods to the AST.
    *
    *@param parentMethods the parent method.
    *@param currentClass the current class.
    */
  void addAllInheritedMethods(GNode parentMethods, GNode currentClass){
    for(int i = 0; i < parentMethods.size(); i++){
      //By default just assume the method itself is inherited and already has a container
      GNode inheritedMethod = parentMethods.getGeneric(i);
      if(!parentMethods.getGeneric(i).getName().equals("InheritedMethodContainer")){
        //Create a inherited method container
        inheritedMethod = GNode.create("InheritedMethodContainer");

        //Add it to the container
        inheritedMethod.addNode(parentMethods.getGeneric(i));

        //Insert the parent in a from node in the inherited container
        inheritedMethod.add(GNode.create("From")).getGeneric(1).addNode(currentClass.getGeneric(1));
      }
      //Add the parent method to the class. If it is inherited from further up the tree than the parent it will already be formatted as an inherited method
      // Make sure we don't add private or static methods
      
      if (inheritedMethod.getProperty("static") == null && inheritedMethod.getProperty("private") == null)
        currentClass.getGeneric(4).addNode(inheritedMethod);
    }
  }


    /**
    * Adds all inherited fields to the AST.
    *
    *@param parentMethods the parent method.
    *@param currentClass the current class.
    */
  void addAllInheritedFields(GNode parentClassNode, GNode currentClass){
    currentClass.setProperty("FieldMap", new HashMap<String,GNode>((HashMap<String,GNode>)parentClassNode.getProperty("FieldMap")));
    currentFieldMap = (HashMap<String,GNode>)currentClass.getProperty("FieldMap");

    for(Object fieldobj : parentClassNode.getGeneric(3)){
      GNode field = (GNode)fieldobj;
      addField(field.getString(0), field.getGeneric(1), currentClass, null != field.getProperty("static"));
    }
  }
  

  /**
   * Add print expression.
   *
   * @param option An option, if any.
   * @param args Arguments node.
   * @returns print expression node.
   */  
  GNode addPrintExpression(String option, GNode args) {
    GNode print = GNode.create("PrintExpression");
    print.add("cout");
    print.add(GNode.create("Option", option));
    print.add(args);
    return print;
  }

  /**
   * Set method parameters.
   *
   * @param parameters Parameters node.
   * @param method Method node.
   */
  void setMethodParameters(GNode parameters, GNode method){
    method.remove(2);
    method.add(2, parameters);
  }

  /**
   * Get class node from name.
   *
   * @param name Class name.
   * @returns class node.
   */
  GNode getClass(String name) {
      return classesMap.get(name);
  }
  
  /**
   * Get the index of a field with name in the class given by classNode.
   *
   * @param name Field name.
   * @param classNode Class node.
   * @returns index of class node.
   */
  int getFieldIndex(String name, GNode classNode){
    GNode fieldsOfClass = classNode.getGeneric(1);
    for(int i = 0; i < fieldsOfClass.size(); i++){
      if(getGNodeName(fieldsOfClass.getGeneric(i)).equals(name))
        return i;
    }
    return -1;
  }

  /**
   * Remove field of a class node.
   *
   * @param name Field name.
   * @param classNode Class node.
   */  
  void removeField(String name, GNode classNode){
    int fieldIndex = getFieldIndex(name, classNode);
    if(fieldIndex != -1) classNode.getGeneric(1).remove(fieldIndex);
  }
  
  //Adding, getting, and removing methods  
  
  /**
   * Get index of inherited method.
   *
   * @param name Inherited method name.
   * @param classNode Class node.
   * @return index of inherited method node.
   */
  int getInheritedMethodIndex(String name, GNode classNode) {
    GNode inheritedMethods = classNode.getGeneric(5);
    for(int i=0; i < inheritedMethods.size(); i++){
      if(getGNodeName(inheritedMethods.getGeneric(i)).equals(name))
        return i;
    }
    return -1;      
  }
  
  
  /**
   * Remove inherited method.
   *
   * @param name Inherited method name.
   * @param classNode Class node.
   */  
  void removeInheritedMethod(GNode methodDeclaration, GNode classNode) {
    final String name = methodDeclaration.getString(3);
    int methodIndex = getInheritedMethodIndex(name, classNode);
    if(methodIndex != -1) classNode.getGeneric(5).remove(methodIndex);
    
    //System.out.println("RemoveInheritedMethod " + classNode);
    
    new Visitor () {
      
     /**
      * Visits Inherited Methods node. Removes overwritten methods.
      *
      *@param n the node to visit.
      */
      public void visitInheritedMethods( GNode n ) {
      
        //System.out.println("Removing extras: " + " methodName " + name);
        for (Object o : n){
          Boolean matches = false;
          if (o instanceof Node){ 
            matches = (Boolean)dispatch((Node)o);
            //System.out.println("Matches: "  + matches );
          }
          if ( matches == true ) {
            n.remove(n.indexOf(o));
            //System.out.println("REMOVED " + name);
          }
        }
      }
      

    /**
    * Visits Method Declaration node.
    *
    *@param n the node to visit.
    *@return true if the method name equals the class name
    */      
      public Boolean visitMethodDeclaration ( GNode n ) {
        //System.out.println("*** " + name + " " + n.getString(0));
        if (n.getString(0).equals(name)){
          return true;
        }
        
        else {
          return false;
        }
      }
      /**
       * Generic Visit method.
       *
       *@param n the node to visit.
       */
      public void visit(Node n) {
        ////System.out.println("We're hitting this");
        for (Object o : n) if (o instanceof Node) dispatch((Node)o);
      }
    }.dispatch(classNode);
  }
  
  //Utility methods
  GNode generateObjectType(){
    GNode qi = GNode.create("QualifiedIdentifier");
    qi.add("java").add("lang").add("Object");
    GNode type = GNode.create("Type");
    type.add(qi);
    type.add(null);
    return type;
  }

  GNode generateObjectClassDeclaration(){
    GNode methods = generateObjectMethods();
    GNode objectDec = 
      GNode.create("ClassDeclaration", "java.lang.Object", null, null, null, methods);
    return objectDec;
  }

  //Utility methods
  GNode generateArrayType(){
    GNode qi = GNode.create("QualifiedIdentifier");
    qi.add("__rt").add("Array");
    GNode type = GNode.create("Type");
    type.add(qi);
    type.add(null);
    return type;
  }

  GNode generateArrayClassDeclaration(){
    GNode methods = generateArrayMethods(); // duplicate
    GNode lenField = GNode.create("FieldDeclaration", "length",
                                 GNode.create("Type", GNode.create("PrimitiveType", "int")));
    GNode fields = GNode.create("FieldDeclarations", lenField ); 
    GNode ArrayDec = 
      GNode.create("ClassDeclaration", "rt.Array", null, null, fields, methods);

    HashMap<String, GNode> arrFieldMap = new HashMap<String, GNode>();
    arrFieldMap.put("length", lenField);

    ArrayDec.setProperty("FieldMap", arrFieldMap);
    return ArrayDec;
  }

  GNode generateStringClassDeclaration(){
    GNode methods = generateStringMethods();
    GNode stringDec = GNode.create("ClassDeclaration", "java.lang.String", null, null, null, methods);
    return stringDec;
  }

  GNode generateClassClassDeclaration(){
    GNode methods = generateClassMethods();
    GNode objectDec = GNode.create("ClassDeclaration", "java.lang.Class", null, null, null, methods);
    return objectDec;
  }
  
  GNode generateObjectMethods() {
    GNode objectMethods = GNode.create("Methods");
    GNode objectType = generateObjectType();
    
    //Hashcode
    GNode objectMethod = GNode.create("ImplementedMethodDeclaration");
    objectMethod.add("hashCode");
    objectMethod.add(GNode.create("ReturnType")).getGeneric(1).add(GNode.create("PrimitiveType")).getGeneric(0).add("int");
    objectMethod.addNode(GNode.create("FormalParameters"));
    //objectMethod.addNode(GNode.create("From")).getGeneric(3).addNode(objectType);
    objectMethod.addNode(GNode.create("Block"));
    objectMethods.add(objectMethod);
    
    //equals
    objectMethod = GNode.create("ImplementedMethodDeclaration");
    objectMethod.add("equals");
    objectMethod.add(GNode.create("ReturnType")).getGeneric(1).add(GNode.create("PrimitiveType")).getGeneric(0).add("boolean");
    objectMethod.addNode(GNode.create("FormalParameters")).getGeneric(2).addNode(GNode.create("FormalParameter")).getGeneric(0).add("obj").addNode(objectType);
    //objectMethod.addNode(GNode.create("From")).getGeneric(3).addNode(objectType);
    objectMethod.addNode(GNode.create("Block"));
    objectMethods.add(objectMethod);
    
    //getClass
    objectMethod = GNode.create("ImplementedMethodDeclaration");
    objectMethod.add("getClass");
    objectMethod.add(GNode.create("ReturnType")).getGeneric(1).add(GNode.create("QualifiedIdentifier")).getGeneric(0).add("java").add("lang").add("Class");
    objectMethod.addNode(GNode.create("FormalParameters"));
    //objectMethod.addNode(GNode.create("From")).getGeneric(3).addNode(objectType);
    objectMethod.addNode(GNode.create("Block"));
    objectMethods.add(objectMethod);
    
    //toString
    objectMethod = GNode.create("ImplementedMethodDeclaration");
    objectMethod.add("toString");
    objectMethod.add(GNode.create("ReturnType")).getGeneric(1).add(GNode.create("QualifiedIdentifier")).getGeneric(0).add("java").add("lang").add("String");
    objectMethod.addNode(GNode.create("FormalParameters"));
    //objectMethod.addNode(GNode.create("From")).getGeneric(3).addNode(objectType);
    objectMethod.addNode(GNode.create("Block"));
    objectMethods.add(objectMethod);
    
    return objectMethods;
  }

  
  GNode generateArrayMethods() {
    GNode objectMethods = GNode.create("Methods");
    GNode objectType = generateObjectType();
    
    //Hashcode
    GNode objectMethod = GNode.create("ImplementedMethodDeclaration");
    objectMethod.add("hashCode");
    objectMethod.add(GNode.create("ReturnType")).getGeneric(1).add(GNode.create("PrimitiveType")).getGeneric(0).add("int");
    objectMethod.addNode(GNode.create("FormalParameters"));
    //objectMethod.addNode(GNode.create("From")).getGeneric(3).addNode(objectType);
    objectMethod.addNode(GNode.create("Block"));
    objectMethods.add(objectMethod);
    
    //equals
    objectMethod = GNode.create("ImplementedMethodDeclaration");
    objectMethod.add("equals");
    objectMethod.add(GNode.create("ReturnType")).getGeneric(1).add(GNode.create("PrimitiveType")).getGeneric(0).add("boolean");
    objectMethod.addNode(GNode.create("FormalParameters")).getGeneric(2).addNode(GNode.create("FormalParameter")).getGeneric(0).add("obj").addNode(objectType);
    //objectMethod.addNode(GNode.create("From")).getGeneric(3).addNode(objectType);
    objectMethod.addNode(GNode.create("Block"));
    objectMethods.add(objectMethod);
    
    //getClass
    objectMethod = GNode.create("ImplementedMethodDeclaration");
    objectMethod.add("getClass");
    objectMethod.add(GNode.create("ReturnType")).getGeneric(1).add(GNode.create("QualifiedIdentifier")).getGeneric(0).add("java").add("lang").add("Class");
    objectMethod.addNode(GNode.create("FormalParameters"));
    //objectMethod.addNode(GNode.create("From")).getGeneric(3).addNode(objectType);
    objectMethod.addNode(GNode.create("Block"));
    objectMethods.add(objectMethod);
    
    //toString
    objectMethod = GNode.create("ImplementedMethodDeclaration");
    objectMethod.add("toString");
    objectMethod.add(GNode.create("ReturnType")).getGeneric(1).add(GNode.create("QualifiedIdentifier")).getGeneric(0).add("java").add("lang").add("String");
    objectMethod.addNode(GNode.create("FormalParameters"));
    //objectMethod.addNode(GNode.create("From")).getGeneric(3).addNode(objectType);
    objectMethod.addNode(GNode.create("Block"));
    objectMethods.add(objectMethod);
    
    return objectMethods;
  }

  GNode generateStringMethods(){
    GNode objectMethods = GNode.create("Methods");
    GNode objectType = generateObjectType();
    
    //Hashcode
    GNode objectMethod = GNode.create("ImplementedMethodDeclaration");
    objectMethod.add("hashCode");
    objectMethod.add(GNode.create("ReturnType")).getGeneric(1).add(GNode.create("PrimitiveType")).getGeneric(0).add("int");
    objectMethod.addNode(GNode.create("FormalParameters"));
    //objectMethod.addNode(GNode.create("From")).getGeneric(3).addNode(objectType);
    objectMethod.addNode(GNode.create("Block"));
    objectMethods.add(objectMethod);
    
    //equals
    objectMethod = GNode.create("ImplementedMethodDeclaration");
    objectMethod.add("equals");
    objectMethod.add(GNode.create("ReturnType")).getGeneric(1).add(GNode.create("PrimitiveType")).getGeneric(0).add("boolean");
    objectMethod.addNode(GNode.create("FormalParameters")).getGeneric(2).addNode(GNode.create("FormalParameter")).getGeneric(0).add("obj").addNode(objectType);
    //objectMethod.addNode(GNode.create("From")).getGeneric(3).addNode(objectType);
    objectMethod.addNode(GNode.create("Block"));
    objectMethods.add(objectMethod);
    
    //getClass
    objectMethod = GNode.create("ImplementedMethodDeclaration");
    objectMethod.add("getClass");
    objectMethod.add(GNode.create("ReturnType")).getGeneric(1).add(GNode.create("QualifiedIdentifier")).getGeneric(0).add("java").add("lang").add("Class");
    objectMethod.addNode(GNode.create("FormalParameters"));
    //objectMethod.addNode(GNode.create("From")).getGeneric(3).addNode(objectType);
    objectMethod.addNode(GNode.create("Block"));
    objectMethods.add(objectMethod);
    
    //toString
    objectMethod = GNode.create("ImplementedMethodDeclaration");
    objectMethod.add("toString");
    objectMethod.add(GNode.create("ReturnType")).getGeneric(1).add(GNode.create("QualifiedIdentifier")).getGeneric(0).add("java").add("lang").add("String");
    objectMethod.addNode(GNode.create("FormalParameters"));
    //objectMethod.addNode(GNode.create("From")).getGeneric(3).addNode(objectType);
    objectMethod.addNode(GNode.create("Block"));
    objectMethods.add(objectMethod);
    
    objectMethod = GNode.create("ImplementedMethodDeclaration");
    objectMethod.add("length");
    objectMethod.add(GNode.create("ReturnType", GNode.create("PrimitiveType", "int"), null));
    objectMethod.addNode(GNode.create("FormalParameters"));
    //objectMethod.addNode(GNode.create("From")).getGeneric(3).addNode(objectType);
    objectMethod.addNode(GNode.create("Block"));
    objectMethods.add(objectMethod);

    objectMethod = GNode.create("ImplementedMethodDeclaration");
    objectMethod.add("charAt");
    objectMethod.add(GNode.create("ReturnType", GNode.create("PrimitiveType", "char"), null));
    objectMethod.addNode(GNode.create("FormalParameters", GNode.create("FormalParameter", "i", GNode.create("Type", GNode.create("PrimitiveType", "int")))));
    //objectMethod.addNode(GNode.create("From")).getGeneric(3).addNode(objectType);
    objectMethod.addNode(GNode.create("Block"));
    objectMethods.add(objectMethod);

    
    return objectMethods;
  }

  GNode generateClassMethods(){
    GNode objectMethods = GNode.create("Methods");
    GNode objectType = generateObjectType();
    
    //Hashcode
    GNode objectMethod = GNode.create("ImplementedMethodDeclaration");
    objectMethod.add("hashCode");
    objectMethod.add(GNode.create("ReturnType")).getGeneric(1).add(GNode.create("PrimitiveType")).getGeneric(0).add("int");
    objectMethod.addNode(GNode.create("FormalParameters"));
    //objectMethod.addNode(GNode.create("From")).getGeneric(3).addNode(objectType);
    objectMethod.addNode(GNode.create("Block"));
    objectMethods.add(objectMethod);
    
    //equals
    objectMethod = GNode.create("ImplementedMethodDeclaration");
    objectMethod.add("equals");
    objectMethod.add(GNode.create("ReturnType")).getGeneric(1).add(GNode.create("PrimitiveType")).getGeneric(0).add("boolean");
    objectMethod.addNode(GNode.create("FormalParameters")).getGeneric(2).addNode(GNode.create("FormalParameter")).getGeneric(0).add("obj").addNode(objectType);
    //objectMethod.addNode(GNode.create("From")).getGeneric(3).addNode(objectType);
    objectMethod.addNode(GNode.create("Block"));
    objectMethods.add(objectMethod);
    
    //getClass
    objectMethod = GNode.create("ImplementedMethodDeclaration");
    objectMethod.add("getClass");
    objectMethod.add(GNode.create("ReturnType")).getGeneric(1).add(GNode.create("QualifiedIdentifier")).getGeneric(0).add("java").add("lang").add("Class");
    objectMethod.addNode(GNode.create("FormalParameters"));
    //objectMethod.addNode(GNode.create("From")).getGeneric(3).addNode(objectType);
    objectMethod.addNode(GNode.create("Block"));
    objectMethods.add(objectMethod);
    
    //toString
    objectMethod = GNode.create("ImplementedMethodDeclaration");
    objectMethod.add("toString");
    objectMethod.add(GNode.create("ReturnType")).getGeneric(1).add(GNode.create("QualifiedIdentifier")).add(null).getGeneric(0).add("java").add("lang").add("String");
    objectMethod.addNode(GNode.create("FormalParameters"));
    //objectMethod.addNode(GNode.create("From")).getGeneric(3).addNode(objectType);
    objectMethod.addNode(GNode.create("Block"));
    objectMethods.add(objectMethod);

    //getName
    objectMethod = GNode.create("ImplementedMethodDeclaration");
    objectMethod.add("getName");
    objectMethod.add(GNode.create("ReturnType")).getGeneric(1).add(GNode.create("QualifiedIdentifier")).add(null).getGeneric(0).add("java").add("lang").add("String");
    objectMethod.addNode(GNode.create("FormalParameters"));
    //objectMethod.addNode(GNode.create("From")).getGeneric(3).addNode(objectType);
    objectMethod.addNode(GNode.create("Block"));
    objectMethods.add(objectMethod);

    //getParentClass
    objectMethod = GNode.create("ImplementedMethodDeclaration");
    objectMethod.add("getSuperclass");
    objectMethod.add(GNode.create("ReturnType")).getGeneric(1).add(GNode.create("QualifiedIdentifier")).add(null).getGeneric(0).add("java").add("lang").add("Class");
    objectMethod.addNode(GNode.create("FormalParameters"));
    //objectMethod.addNode(GNode.create("From")).getGeneric(3).addNode(objectType);
    objectMethod.addNode(GNode.create("Block"));
    objectMethods.add(objectMethod);

    //isPrimitive
    objectMethod = GNode.create("ImplementedMethodDeclaration");
    objectMethod.add("isPrimitive");
    objectMethod.add(GNode.create("ReturnType")).getGeneric(1).add(GNode.create("PrimitiveType")).add(null).getGeneric(0).add("boolean");
    objectMethod.addNode(GNode.create("FormalParameters"));
    //objectMethod.addNode(GNode.create("From")).getGeneric(3).addNode(objectType);
    objectMethod.addNode(GNode.create("Block"));
    objectMethods.add(objectMethod);

    //isArray
    objectMethod = GNode.create("ImplementedMethodDeclaration");
    objectMethod.add("isArray");
    objectMethod.add(GNode.create("ReturnType")).getGeneric(1).add(GNode.create("PrimitiveType")).add(null).getGeneric(0).add("boolean");
    objectMethod.addNode(GNode.create("FormalParameters"));
    //objectMethod.addNode(GNode.create("From")).getGeneric(3).addNode(objectType);
    objectMethod.addNode(GNode.create("Block"));
    objectMethods.add(objectMethod);

    //getComponentType
    objectMethod = GNode.create("ImplementedMethodDeclaration");
    objectMethod.add("isArray");
    objectMethod.add(GNode.create("ReturnType")).getGeneric(1).add(GNode.create("PrimitiveType")).add(null).getGeneric(0).add("boolean");
    objectMethod.addNode(GNode.create("FormalParameters"));
    //objectMethod.addNode(GNode.create("From")).getGeneric(3).addNode(objectType);
    objectMethod.addNode(GNode.create("Block"));
    objectMethods.add(objectMethod);


    //isInstance
    objectMethod = GNode.create("ImplementedMethodDeclaration");
    objectMethod.add("isInstance");
    objectMethod.add(GNode.create("ReturnType")).getGeneric(1).add(GNode.create("PrimitiveType")).add(null).getGeneric(0).add("boolean");
    objectMethod.addNode(GNode.create("FormalParameters", GNode.create("FormalParameter", "i", GNode.create("Type", GNode.create("QualifiedIdentifier", "java", "lang", "Class"), null))));
    //objectMethod.addNode(GNode.create("From")).getGeneric(3).addNode(objectType);
    objectMethod.addNode(GNode.create("Block"));
    objectMethods.add(objectMethod);

    return objectMethods;
  }
  
 
  //Utility methods

  /**
   * Get name of node.
   *
   * @param n GNode.
   * @return name of node.
   */  
  String getGNodeName(GNode n){
    return n.getString(0);
  }
 
  HashMap<String, Integer> GNodeNameToTypeLoc;
  HashMap<String, Boolean> GNodeNameToHasUnderscores;
  HashMap<String, String> JavaPrimitiveTypeToCPP;

  void setupMaps(){
    GNodeNameToTypeLoc = new HashMap<String, Integer>();
    GNodeNameToTypeLoc.put("Parent", 0);
    GNodeNameToTypeLoc.put("FieldDeclaration", 1);
    GNodeNameToTypeLoc.put("MethodDeclaration", 1);
    GNodeNameToTypeLoc.put("FormalParameter", 1);

    GNodeNameToHasUnderscores = new HashMap<String, Boolean>();
    GNodeNameToHasUnderscores.put("Parent", true);
    GNodeNameToHasUnderscores.put("FieldDeclaration", false);
    GNodeNameToHasUnderscores.put("MethodDeclaration", false);
    GNodeNameToHasUnderscores.put("FormalParameter", false);

    JavaPrimitiveTypeToCPP = new HashMap<String, String>();
    JavaPrimitiveTypeToCPP.put("long", "signed int65_t"); 
    JavaPrimitiveTypeToCPP.put("int", "int32_t"); 
    JavaPrimitiveTypeToCPP.put("short", "signed int16_t"); 
    JavaPrimitiveTypeToCPP.put("byte", "signed int8_t"); 
    JavaPrimitiveTypeToCPP.put("float", "float"); 
    JavaPrimitiveTypeToCPP.put("double", "double"); 
    JavaPrimitiveTypeToCPP.put("char", "char"); 
  }
  
  String getGNodeType(GNode n){
    GNode typeNode = n.getGeneric(GNodeNameToTypeLoc.get(n.getName()));
    GNode identifier = n.getGeneric(0);
    if(identifier.getName().equals("PrimitiveType")){
      return JavaPrimitiveTypeToCPP.get(identifier.getGeneric(0));
    } else if(identifier.getName().equals("QualifiedIdentifier")){
      boolean hasUnderscore = GNodeNameToHasUnderscores.get(n.getName());
    }
    return typeNode.getName(); 
  }

  public void printAST(){
    Printer p = new Printer(System.out);
    p.format(compilationUnit).flush();
  }
}

