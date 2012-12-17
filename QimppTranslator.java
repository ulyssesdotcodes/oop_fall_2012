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
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import xtc.lang.JavaFiveParser;

import xtc.parser.ParseException;
import xtc.parser.Result;

import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;
import xtc.tree.Location;
import xtc.tree.Printer;

import xtc.util.Tool;

import qimpp.SymbolTable;

/**
 * A translator from (a subset of) Java to (a subset of) C++.
 *
 * Uses visitor and the return Object of dispatch()
 *
 *
 *
 * @author QIMPP
 * @version 0.1
 */
public class QimppTranslator extends Tool {
  
    
  GNode currentClass, currentMethod, currentConstructor, parentClassNode;
  String currentClassName;
  String currentPackageName;
  String parentName;
  CPPAST cppast;
  InheritanceTreeManager treeManager;
  GNode root;
  //bool inReturnStatement;
  HashMap<String, String> currentNameMap;
  HashMap<String, Boolean> enqueued;
  LinkedList<Node> readQueue;

  boolean inBlock;
  boolean processImmediately;
  int blockDepth = 0;

  /** Create a new translator. */
  public QimppTranslator() {
    cppast = new CPPAST();
  }

  public String getName() {
    return "Java to C++ Translator";
  }

  public String getCopy() {
    return "(C) 2012 qimpp";
  }

  public void init() {
    super.init();
  }

  public void prepare() {
    super.prepare();

    // Perform consistency checks on command line arguments.
  }
  /**
   * Locates the file to translate
   *
   * @param name the name of the file
   * @return the file
   */
  public File locate(String name) throws IOException {
    File file = super.locate(name);
    if (Integer.MAX_VALUE < file.length()) {
      throw new IllegalArgumentException(file + ": file too large");
    }
    return file;
  }
  /**
   * Parses the file with the Java Five Parser
   *
   * @param in the reader
   * @param file the file
   * @return the result as a root node
   */
  public Node parse(Reader in, File file) throws IOException, ParseException {
    JavaFiveParser parser =
      new JavaFiveParser(in, file.toString(), (int)file.length());
    Result result = parser.pCompilationUnit(0);
    return (Node)parser.value(result);
  }
  
  /**
   * Run the translator
   *
   * @param args the arguments
   */
  public void run(String[] args){
    treeManager = new InheritanceTreeManager(cppast.generateObjectClassDeclaration()); 
    readQueue = new LinkedList<Node>();
    enqueued = new HashMap<String, Boolean>();
    String[] stringQualified = {"java", "lang", "String"};
    String[] classQualified = {"java", "lang", "Class"};
    String[] arrayQualified = {"__rt", "Array"};
    treeManager.insertClass(new ArrayList<String>(Arrays.asList(stringQualified)), null, cppast.generateStringClassDeclaration());
    treeManager.insertClass(new ArrayList<String>(Arrays.asList(classQualified)), null, cppast.generateClassClassDeclaration());
    treeManager.insertClass(new ArrayList<String>(Arrays.asList(arrayQualified)), null, cppast.generateArrayClassDeclaration());

    // This gets the class name from the command line of the root class. Fix this later, as it only supports one argument
    currentClassName = args[args.length - 1];
    
    super.run(args);
    //cppast.printAST();
  }

  /**
   * Locates, opens, and parses the file.
   *
   * @param name the name of the file
   */
  public void processEnqueue(String name) throws IOException, ParseException {

    // Locate the file.
    File file = locate(name);

    // Open the file.
    Reader in = runtime.getReader(file);

    // Parse the file.
    Node root;
    try {
      root = parse(in, file);
    } finally {
      // Close the file.
      try {
        in.close();
      } catch (IOException x) {
        // Ignore.
      }
    }

    if (!enqueued.containsKey(name)){
      // Process the AST.
      readQueue.add(root);
      enqueued.put(name, new Boolean(true));

    }
  }

  int processDepth = -1;
  /**
   * Processes the parsed file
   *
   * @param node the root node of the parsed file
   */
  public void process(Node node) {
    // Create a hashmap to hold maps of ambiguous names to unambiguous names
    processDepth++;
    if(currentNameMap == null){
      currentNameMap = new HashMap<String, String>() {{
        put("String", "java.lang.String");
        put("Object", "java.lang.Object");
        put("Class", "java.lang.Class");
        put("Exception", "java.lang.Exception"); 
      }};
    }
    currentPackageName = "";
    //inReturnStatement = false;

    // First, get contextual information with an initial visit of types and the package declaration
    
    Visitor initialVisitor = new Visitor () {
    
        /**
         * Visits Class Declaration node.
         *
         * @param n the node to visit.
         */
        public void visitClassDeclaration(GNode n) {
        
          //Add the current class to the cppast, and set it as the current class global variable.
          String qualifiedClassName = currentPackageName + "." + n.getString(1);
          currentClass = cppast.addClass(qualifiedClassName);
          currentClassName = qualifiedClassName;
          parentClassNode = currentClass;

          currentNameMap.put(n.getString(1), qualifiedClassName);
          
          //add the current class to the inheritance tree, but parent it to Object for now
          String[] qualifiedArray = qualifiedClassName.split("\\.");
          treeManager.insertClass(new ArrayList<String>(Arrays.asList(qualifiedArray)), null, currentClass);
         
          System.err.println("Inserted class in tree manager ");
          System.err.println(new ArrayList<String>(Arrays.asList(qualifiedArray))); 

          staticInitializerBlock = GNode.create("Block");
          staticInitializerStatements = GNode.create("Block");

          visit(n);

          addStaticInitializerMethod();
          setStaticInitializerMethodInstructions();

          if (currentConstructor == null){
            currentConstructor = cppast.addConstructor(currentClass);
            cppast.setConstructorInstructions(GNode.create("Block"), currentConstructor);
          }


        }

        /**
         * Visits Class Declaration node. Sets the current package name context.
         *
         * @param n the node to visit.
         */
        public void visitPackageDeclaration(GNode n){

          GNode qualifiedIdentifier = n.getGeneric(1);
          StringBuilder packageNameBuilder = new StringBuilder();

          for ( int i = 0; i < qualifiedIdentifier.size(); i++ ){
            packageNameBuilder.append(qualifiedIdentifier.getString(i));
            if ( i < qualifiedIdentifier.size() - 1 ) {
              packageNameBuilder.append(".");
            }
          }

          currentPackageName = packageNameBuilder.toString();
        }

        /**
         * Visits Import Declaration node. 
         *
         * @param n the node to visit.
         */        
         public void visitImportDeclaration(GNode n){
          //  Assuming it's not a * import
          GNode qualifiedIdentifier = n.getGeneric(1);
          StringBuilder qualifiedNameBuilder = new StringBuilder();
          String unqualifiedName = "";

          for ( int i = 0; i < qualifiedIdentifier.size(); i++ ) {
            qualifiedNameBuilder.append(qualifiedIdentifier.getString(i));

            if ( i < qualifiedIdentifier.size() - 1 ) {
              qualifiedNameBuilder.append(".");
            }
            else {
              unqualifiedName = qualifiedIdentifier.getString(i);
            }
          }

          // Add the disambiguation to the hashmap
          currentNameMap.put(unqualifiedName, qualifiedNameBuilder.toString());
        }
        /**
         * Visits Field Declaration node. Adds field to CPPAST.
         *
         * @param n the node to visit.
         */
        public void visitFieldDeclaration(GNode n) {
          //Get the string by dispatching the Type GNode
          dispatch(n.getGeneric(0));
          dispatch(n.getGeneric(2));
          System.err.println("FIELD DECLARATION ~");
                    
          GNode type = (GNode)dispatch(n.getGeneric(1));
          //Create a new GNode to hold all of the declarators;
          GNode declarators = n.getGeneric(2);

          //Loop through all declarators in this field declaration, dispatch them, and add each returned string plus type as its own field to the currentClass
          //There may be multiple e.g. Java: double x,y,z; => C++: double x; double y; double z;
          for(int i = 0; i < declarators.size(); i++){
            String name = (String)dispatch(declarators.getGeneric(i));
            GNode statement = declarators.getGeneric(i).getGeneric(2);
            // Add the initialization to the static initializer if there is an initialization
            System.err.println(inBlock + " NAME: " + name);
            if (blockDepth == 0) {

              if (statement != null){
                addStaticInitializerStatement(name, statement);
              }
              GNode currentField = cppast.addField(currentClassName.replace('.', '_') + "_" + name, name, type, currentClass, false);
              GNode modifiers = n.getGeneric(0);

              for (Object o : modifiers){
                currentField.setProperty(((GNode)o).getString(0), new Boolean(true));
              }
            }
          }
        }
        /**
         * Visits Block node. Increases Block Depth and visits children.
         *
         * @param n the node to visit.
         * @return n.
         */
        public GNode visitBlock(GNode n) {
          blockDepth++;
          visit(n); 
          blockDepth--;
          return n;
        }

        GNode staticInitializerBlock;
        GNode staticInitializerStatements;
        GNode staticInitializerMethod;
        
        /**
         * Visits Block Declaration node
         *
         * @param n the node to visit.
         */
        public void visitBlockDeclaration(GNode n){
          visit(n);
          String modifier = n.getString(0);
          if (modifier != null && modifier.equals("static"))
            addStaticInitializerBlock(n.getGeneric(1));
        }
        /**
         * Adds a Static Initializer Method to the CPPAST.
         */
        public void addStaticInitializerMethod(){
          String methodName = "__static_init";
          GNode returnType = GNode.create("ReturnType", GNode.create("PrimitiveType", "void"), null);
          GNode parameters = GNode.create("FormalParameters");
          staticInitializerMethod = cppast.addMethod(methodName, returnType, currentClass, parameters);

          
          staticInitializerMethod.setProperty("static", new Boolean(true));
        }

        /**
         * Adds a Static Initializer Statement to the CPPAST.
         *
         * @param identifier the type of identifier
         * @param statement the statement
         */
        public void addStaticInitializerStatement(String identifier, GNode statement){
          
          GNode assgnStatement = GNode.create("ExpressionStatement", GNode.create("Expression", GNode.create("PrimaryIdentifier", identifier),
                                                                        "=", statement));
          staticInitializerStatements.add(assgnStatement);
        }

        /**
         * Adds a Static Initializer Block to the CPPAST.
         *
         * @param block the block to add
         */
        public void addStaticInitializerBlock(GNode block){
          for (Object o : block){
            staticInitializerBlock.add(o);
          }  
        }

        /**
         * Sets Static Initializer Method instructions in the CPPAST.
         */
        public void setStaticInitializerMethodInstructions(){
          GNode block = GNode.create("Block");
          for (Object o : staticInitializerStatements){
            block.add(o);
          }
          for (Object o : staticInitializerBlock){
            block.add(o);
          }
          cppast.setMethodInstructions(block, staticInitializerMethod);
        }


        /**
         * Visits Method Declaration node.
         *
         *@param n the node to visit.
         */
        public void visitMethodDeclaration(GNode n) {
          //TODO: math names and remove
          try{
            String methodName = n.getString(3);
            /*
            if (methodName.equals("m8")){
              System.out.println("m8!!!!!");
              System.exit();
            }*/
            if (methodName.equals("main")) {
              n.set(2, GNode.create("Type", 
                    GNode.create("PrimitiveType", "int"), 
                    null));

            }
            GNode returnType = (GNode)dispatch(n.getGeneric(2));
            GNode parameters = (GNode)dispatch(n.getGeneric(4));
            currentMethod = cppast.addMethod(methodName, returnType, currentClass, parameters);
            //Add the method block gotten by dispatching the block node

          dispatch(n.getGeneric(7));
          GNode block = n.getGeneric(7);
          cppast.setMethodInstructions(block, currentMethod);

          // Add any modifiers as a property of the method node
          GNode modifiers = n.getGeneric(0);

          for (Object o : modifiers){
            currentMethod.setProperty(((GNode)o).getString(0), new Boolean(true));
          }
           
          } catch(Exception e) { e.printStackTrace(); }
        }

        /**
         * Visits Formal Parameter node.
         *
         * @param n the node to visit.
         * @return the parameter
         */
        public GNode visitFormalParameter(GNode n){
          
          //Create a parameter by getting the name and dispatching the type
          GNode param = GNode.create("FormalParameter");
          param.add(n.getString(3));
          param.addNode((GNode)dispatch(n.getGeneric(1)));
          return param;
        }
 
        /**
         * Visits FormalParameters node. Runs through all parameters and adds the result to a GNode.
         *
         * @param n the node to visit.
         * @return the parameters.
         */
        public GNode visitFormalParameters(GNode n){
          visit(n);
          //Loop through all the params dispatching them and adding the result to a parameters GNode
          GNode parameters = GNode.create("FormalParameters");
          for(Object o : n){
            parameters.add((GNode)dispatch((GNode)o));
          }
          return parameters;
        }
        
        /**
         * Visits an New Class Expression node. NewClassExpression holds a QualifiedIdentifier not wrapped by a type. 
         * We need to disambiguate it if possible.
         *
         * @param n the node to visit.
         */
        public void visitNewClassExpression(GNode n) {
          // Make sure this type has been translated 
          System.err.println(n.getGeneric(2));
          GNode type = GNode.create("Type", n.getGeneric(2), null);
          type = visitType(type);
          n.set(2, type.getGeneric(0));
        }

        /** 
         * Visit all types. If it is unknown, process the file for that type. 
         * If it is known, fully qualify it.
         *
         * @param n the node to visit.
         * @return the fully qualified node.
         */
        public GNode visitType(GNode n) {
          //Determine the type translated into C++ using 
          //Type.primitiveType(String) and Type.qualifiedIdentifier(String)
          visit(n);

          GNode identifier = n.getGeneric(0);
          String typename = Disambiguator.getDotDelimitedName(identifier);
          System.err.println("STRING");
          System.err.println(typename);

          if(identifier.hasName("PrimitiveType")){
            return n;
          }
          
          if ( currentNameMap.get(typename) != null ) {
             // Create a QualifiedIdentifier node for the typename 
             // using the now obviously ill-named Disambiguator
             //TODO: Give Disambiguator a name that reflects its purpose, 
             //which is just to construct a type node from a name.
             // Maybe stick that function in CPPAST.

             String qualifiedName = currentNameMap.get(typename);
             GNode qualifiedIdentifierNode =
               Disambiguator.disambiguate(qualifiedName);
             n.set(0, qualifiedIdentifierNode);
             System.err.println("REPLACING REFERENCE: " + qualifiedName);

             typename = qualifiedName;
          }

          // Fix this later in treeManager
          if ( typename.equals("java.lang.String") 
              || typename.equals("java.lang.Class") 
              || typename.equals("java.lang.Object") 
              || typename.equals("java.lang.Exception")
             ) {
            GNode type = GNode.create("Type");
            type.add(Disambiguator.disambiguate(typename));
            System.err.println("TYPE!");
            System.err.println(n);
            type.add(n.get(1));
            return type;
          } 
          // If the name is our name map, 
          // then replace it with the fully qualified name
          
          else {
            //System.err.println("Split: " + typename.split("\\.").length);
            //System.err.println("Adding typename: " + typename);
            System.err.println(typename);
            String[] qualified = typename.split("\\.");
            
            if (typename.equals(currentClassName)){
              System.err.println("SELF-VISIT");
              return n;
            }
            
            // Reset currentClassName when we come back
            String tempClassName = currentClassName;
            currentClassName = typename;
            
            String tempPackageName = currentPackageName;
            GNode tempClass = currentClass;

            GNode tempStatInitBlock = staticInitializerBlock;
            GNode tempStatInitStat = staticInitializerStatements;
            GNode tempStatInitMethod = staticInitializerMethod;

            GNode tempCurrentConstructor = currentConstructor;
            currentConstructor = null;

            HashMap<String, String> tempNameMap = currentNameMap;
            
            // disambiguate() - figure out the fully qualified name
            // Later we'll keep track of already-imported types,
            // and we'll automatically skip those or expand them
            // as necessary
            // For now we'll support only explicitly qualified name: 
            //  "qimpp.Foo" ["qimpp", "Foo"]
            System.err.println(qualified);
            GNode classTreeNode = 
              treeManager.dereference(new ArrayList(Arrays.asList(qualified)));

            if (classTreeNode == null) {
                try{
                  if (processImmediately){
                    process(typename.replace(".", "/")+".java");
                    enqueued.put(typename.replace(".", "/")+".java", new Boolean(true));
                  }
                  else
                    processEnqueue(typename.replace(".", "/")+".java");
                } catch (Exception e) {
                  // If we can't find it in the source root, then it must be a reference to a file in the current package
                  try {
                    System.out.println("Can't find it in the source root");
                     String currentPackageQualifiedTypename = currentPackageName + "." + typename;
                     currentNameMap.put(typename, currentPackageQualifiedTypename);
                     if (processImmediately){
                      process(currentPackageQualifiedTypename.replace(".", "/")+".java");
                      enqueued.put(currentPackageQualifiedTypename.replace(".", "/")+".java", new Boolean(true));
                     }
                     else
                      processEnqueue(currentPackageQualifiedTypename.replace(".", "/")+".java");
                     
                     // Copy-paste yay...
                     String qualifiedName = currentNameMap.get(typename);
                     GNode qualifiedIdentifierNode = Disambiguator.disambiguate(qualifiedName);
                     n.set(0, qualifiedIdentifierNode);
                  }
                  catch (Exception f){
                    System.err.println("Cannot parse " + typename + " " + e);
                    cppast.printAST();
                    e.printStackTrace();
                    System.exit(1);
                  }
                }
                // Fail and crash with error if the file cannot be located

            }
            
            currentClassName = tempClassName;
            currentPackageName = tempPackageName;
            //currentNameMap = tempNameMap;

            currentClass = tempClass;
            currentConstructor = tempCurrentConstructor;
            
            staticInitializerBlock = tempStatInitBlock;
            staticInitializerStatements = tempStatInitStat;
            staticInitializerMethod = tempStatInitMethod;

            return n;

          }
        }


        /**
         * Visits Declarator node. 
         *
         * @param n the node to visit.
         * @return the name of the declarator
         */
        public String visitDeclarator(GNode n) {
          visit(n);
          //A declarator just needs to return the name of it right now
          return n.getString(0);
        }

        int selectionExpressionDepth = 0;
        StringBuilder selectionExpressionBuilder;
        /**
         * Visit a selection expression, and if it's referring to a class 
         * type by name, make sure that type is translated.
         *
         * @param n the node to visit.
         */
        public void visitSelectionExpression(GNode n){
          if (selectionExpressionDepth == 0){
            selectionExpressionBuilder = new StringBuilder();
          }
          selectionExpressionDepth++;
          dispatch(n.getGeneric(0));
          selectionExpressionDepth--;
          String name = n.getString(1);
          selectionExpressionBuilder.append(".");
          selectionExpressionBuilder.append(name);

          // If this SelectionExpression is referring to a class, 
          // handle it as a type, making sure it's translated
          if (currentNameMap.containsKey(selectionExpressionBuilder.toString())) {
            GNode type = GNode.create("Type", 
                GNode.create("QualifiedIdentifier", 
                  selectionExpressionBuilder.toString()),
                  null);
            visitType(type);
          }
        }

        /**
         * Visit a PrimaryIdentifier node.
         *
         * @param n the node to visit.
         */
        public void visitPrimaryIdentifier(GNode n) {
          // Check if this is a ambiguous name, and if it is, replace it with the fully qualified name
          String name = n.getString(0);
          //if(currentNameMap.containsKey(name)) name = currentNameMap.get(name);
          // Make sure it's not an unvisited name in the local namespace
          //TODO: This assumes that class names are capitalized
          String s;
          if (currentNameMap.containsKey(name) && !name.equals("System")){
            GNode type = GNode.create("Type", 
                GNode.create("QualifiedIdentifier",
                name));
            visitType(type);
            n.set(0, currentNameMap.get(name));
          } 
          // If we're inside a SelectionExpression, build the expression
          if (selectionExpressionDepth > 0){
            selectionExpressionBuilder.append(name);
          }
        }
        
        /**
         * Visits Void Type node.
         *
         * @param n the node to visit.
         * @return the fully qualified node
         */
        public GNode visitVoidType(GNode n){
          GNode type = GNode.create("Type");
          type.addNode(GNode.create("PrimitiveType")).getGeneric(0).add("void");
          return type;
        }


        /**
         * Visits Return Statement node.
         *
         * @param n the node to visit.
         * @return the node.
         */
        public GNode visitReturnStatement(GNode n){
          visit(n);
          return n;
        }


        /**
         * Visits Extension node.
         *
         * @param n the node to visit.
         * @return null.
         */
        public GNode visitExtension(GNode n){
        
          // Assume the name of the parent is fully qualified
          processImmediately = true;
          visit(n);
          processImmediately = false;

          GNode parentNameQualifiedIdentifier = n.getGeneric(0).getGeneric(0);
          parentName = Disambiguator.getDotDelimitedName(parentNameQualifiedIdentifier);
          
          parentClassNode = treeManager.getClassDeclarationNode(parentName);

          // Associate the current class with its parent's class Node
          currentClass.setProperty("ParentClassNode", parentClassNode);
          
          String parentNameQualified = parentClassNode.getString(0);
          
          // Add the parent's type to the current class's Parent Node
          currentClass.getGeneric(1).getGeneric(0).set(0, Disambiguator.disambiguate(parentNameQualified));
          
          // Add inherited mehthods and fields using the parent's class
          // TODO: Refactor here. Implemented and inherited methods should be interspersed, and there
          // should only be one argument to addAllInheritedMethods
          System.err.println("---------------\nADDING INHERITED METHODS\n---------------------");
          System.err.println("In class: " + currentClassName);
          cppast.addAllInheritedMethods(parentClassNode.getGeneric(4), currentClass);

          cppast.addAllInheritedFields(parentClassNode, currentClass); 
          parentClassNode = currentClass;
          
          //add the current class to the inheritance tree, but parent it to Object for now
          ArrayList parentQualified = new ArrayList<String>(Arrays.asList(parentName.split("\\.")));
          ArrayList childQualified = new ArrayList<String>(Arrays.asList(currentClassName.split("\\.")));
          System.err.println(parentQualified);
          System.err.println(childQualified);
          treeManager.reparent(childQualified, parentQualified);
          
          return null; 
        }

        /**
         * Visits Constructor Declaration node.
         *
         * @param n the node to visit.
         */
        public void visitConstructorDeclaration(GNode n) {
          System.err.println("VISITING CONSTRUCTOR!!!");
          System.err.println(currentClassName);
          //Add a constructor to currentClass and get the associated GNode
          currentConstructor = cppast.addConstructor(currentClass);

          //If there are formal parameters for the constructor, visit them and add them to the currentConstructor
          if(n.getGeneric(4) != null){ 
            
            System.err.println("ADDING PARAMS");
            cppast.setConstructorParameters((GNode)dispatch(n.getGeneric(4)), currentConstructor);
          }
          //If there are instructions in the block, visit them and add them to the constructor
          if(n.getGeneric(5) != null){
            System.err.println("ADDING INSTRUCTIONS");
            cppast.setConstructorInstructions((GNode)dispatch(n.getGeneric(5)), currentConstructor);
          }
        }



        /**
         * Generic Visit method
         *
         * @param n the node to visit.
         */
        public void visit(Node n) {
          //System.err.println("We are currently running " + currentClassName);
          for (Object o : n) if (o instanceof Node) dispatch((Node)o);
        }
    };

    initialVisitor.dispatch(node);

    if (processDepth == 0){
      // Reset currentClassName when we come back
      String tempPackageName = currentPackageName;
      GNode tempClass = currentClass;
      String tempClassName = currentClassName;
      GNode tempConstruct = currentConstructor;
      currentConstructor = null;

      Node target = readQueue.poll();
      while (target != null){
        process(target);
        target = readQueue.poll();
      }

      currentConstructor = tempConstruct;
      currentClassName = tempClassName;
      currentPackageName = tempPackageName;
      //currentNameMap = tempNameMap;

      currentClass = tempClass;
      
    }

    /** SYMBOL TABLE */
    SymbolTable table = new SymbolTable();
    table.incorporate(currentClass);
    // Now we can call .getProperty("qimpp.Constants.SCOPE") on certain
    // scope-defining nodes and we'll get back a Scope object (look in
    // SymbolTable).
    
    final BlockMangler mangler = new BlockMangler(currentClass, treeManager, new MethodResolver());

    new Visitor() {

      /**
       * Visit a block of code, and mangle it appropriately before we print it
       * Do not visit deeper here
       *
       * @param n the node to visit.
       */
      public GNode visitBlock(GNode n) {
        inBlock = true;
        GNode block = GNode.create("Block");
        if ( n.getProperty("Mangled") == null){ 
          mangler.mangle(n);
          n.setProperty("Mangled", new Boolean(true));
        }
        inBlock = false;

        return n;
      }

      /*public GNode visitCallExpression(GNode n) {
        // if this is a system call
        if (n.getGeneric(0).getGeneric(0).getString(0).equals("System")) {
          // if this is system.out
          if (n.getGeneric(0).getString(1).equals("out")) {
            // if this is system.out.print
            if (n.getString(2).equals("print")) {
              return cppast.addPrintExpression(null, n.getGeneric(3)); 
            }
          }
        }
        return n;
      }*/

      /**
       * Visits Class Body node.
       *
       * @param n the node to visit.
       */
      public void visitClassBody(GNode n){
        visit(n);
      }
        
        
      /**
       * Visits Class Declaration node.
       *
       * @param n the node to visit.
       */
      public void visitClassDeclaration(GNode n){
        // Add a default no-arg constructor. If there is an explicit constructor, we can add it
        // later.
        //Add a constructor to currentClass and get the associated GNode
        visit(n);
      }

      /**
       * Visits Compilation unit node.
       *
       * @param n the node to visit.
       */
      public void visitCompilationUnit(GNode n) {
        root = n;     
        //System.out.println("In QinppTranslator:visitCompilationUnit before visit(n)");
        visit(n);
        //System.out.println("In QinppTranslator:visitCompilationUnit after visit(n)");
        //Print the AST after we're done for debugging
        //cppast.printAST();
        
      }
      
      

      
      
      
      /**
       * Visits Expression Statement node.
       *
       * @param n the node to visit.
       */
      public GNode visitExpressionStatement(GNode n) {
        return n;
      }
      
      /**
       * Visits Expression node.
       *
       * @param n the node to visit.
       */
      public void visitExpression(GNode n){
        visit(n);
      }
      
      /**
       * Visits New Class Expression node.
       *
       * @param n the node to visit.
       */                      
      public void visitNewClassExpression(GNode n) {       
        visit(n);
      }  
      
      /**
       * Visits Package Declaration node.
       *
       * @param n the node to visit.
       */
      public void visitPackageDeclaration(GNode n){
        //System.out.println("Package " + dispatch(n.getGeneric(1)));
      }
      
      /**
       * Visits String Literal node.
       *
       * @param n the node to visit.
       */
      public void visitStringLiteral(GNode n){
        visit(n);
      }
        
        
      
      
      /**
       * Visits Type node and returns fully qualified Type node.
       *
       * @param n the node to visit.
       * @return the fully qualified node.
       */
      
      public GNode visitType(GNode n) {
        //Determine the type translated into C++ using Type.primitiveType(String) and Type.qualifiedIdentifier(String)
        visit(n);
        GNode identifier = n.getGeneric(0);
        String typename = identifier.getString(0);

        if (identifier.hasName("PrimitiveType")) {
          return n;
        }

        // Fix this later in treeManager
        else if ( typename.equals("String") || typename.equals("Class") || typename.equals("Object") ) {
          GNode type = GNode.create("Type");
          type.add(Disambiguator.disambiguate(typename));
          type.add(n.get(1));
          return type;
        } 

        else {

          GNode type = GNode.create("Type");
          type.add(Disambiguator.disambiguate(typename));
          type.add(n.get(1));
          return type;
        }
      }
      
      /**
       * Visits Return Statement node.
       *
       * @param n the node to visit.
       */
      
      public GNode visitReturnStatement(GNode n) {
        
        return n;
      }
       
      /**
       * Generic Visit method for Visitor.
       *
       * @param n the node to visit.
       */
      public void visit(Node n) {
        //System.err.println("We are currently running " + currentClassName);
        //
        for (Object o : n) if (o instanceof Node) dispatch((Node)o);
      }

    }.dispatch(currentClass);

    if (processDepth == 0){
      try{
          PrintWriter h = new PrintWriter("out.h");
          new HeaderWriter(new Printer(h)).dispatch(cppast.compilationUnit);
          cppast.printAST();

          PrintWriter cc = new PrintWriter("out.cc");
          new ImplementationPrinter(new Printer(cc), treeManager, cppast.compilationUnit).dispatch(cppast.compilationUnit);
        } catch (Exception e) {
          //System.out.println("Uh oh... " + e);
          e.printStackTrace();
        }
    }
    
    processDepth--;
  }

  /**
   * Run the translator with the specified command line arguments.
   *
   * @param args The command line arguments.
   */
  public static void main(String[] args) {
    new QimppTranslator().run(args);   
  }

}
