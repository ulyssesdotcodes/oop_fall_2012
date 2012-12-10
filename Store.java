
package qimpp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;

/**
 * Decompose and store Java AST as classes.
 * This assumes that the input's classes all
 * exist within one package. This is a key assumption
 * (Vivek, 11/28/2012).
 *
 * @author Qimpp
 */
class Store {

  static final Klass javaObject = new Klass("Object", null);
  static final Klass javaString = new Klass("String", javaObject);
  static final Klass javaClass  = new Klass("Class", javaObject);

  /**
   * Classes in store.
   *
   * An ArrayList might not be the best choice, because
   * it means expensive lookup. However, a HashMap 
   * means slight duplication in using className to key
   * and also instantiate the Klass object.
   */
  private static HashMap<String, Klass> pkg;

  public Store() {
    pkg = new HashMap<String, Klass>();
    pkg.put(javaObject.name(), javaObject);
    pkg.put(javaString.name(), javaString);
    pkg.put(javaClass.name(), javaClass);
  }

  /**
   * Get the package of classes.
   *
   * @return the package of classes.
   */
  public HashMap<String, Klass> getPackage() {
    return pkg;
  }

  /**
   * Create an iterator for the classes.
   *
   * @return an iterator of thePackage.
   */
  public Iterator unpack() {
    return pkg.entrySet().iterator();
  }

  /**
   * Get a qualified type by name.
   *
   * @return qualified type.
   */
  public static Klass getQualifiedType(String name) {
    return pkg.get(name);
  }

  // ===========================================================================

  class Analyzer extends Visitor { 

    /** The current class that we're constructing. */
    Klass currentClass;

    /** Flag for whether we're building a class. */
    boolean buildingClass;

    /** The current method that we're constucting. */
    Klass.Method currentMethod;

    /** Flag for whether we're building a method. */
    boolean buildingMethod;

    /** The current field that we're constructing. */
    Klass.Field currentField;

    /** Flag for whether we're building a field. */
    boolean buildingField;

    /** The current parameter that we're constructing. */
    ParameterVariable currentParameter;

    /** Flag for whether we're building a parameter. */
    boolean buildingParameter;

    public Analyzer() {
      this.currentClass     = null;
      this.currentMethod    = null;
      this.currentField     = null;
      this.currentParameter = null;
    }

    /**
     * Add class to the package of classes list.
     *
     * @param klass Class to append.
     */
    public void addClass(String className, Klass klass) {
      Store.this.pkg.put(className, klass);
    }
  }

  /**
   * 1. This inner class takes in a Java AST, and constructs meta
   * objects based on class inheritance.
   */
  class ClassAnalyzer extends Analyzer {

    public ClassAnalyzer() { super(); }

    /** Visit specified class declaration node and add class. */
    public void visitClassDeclaration(GNode n) {
      buildingClass = true;
      Klass parent = Store.getQualifiedType("Object");

      String extendsName = fetchParentClassName(n.getGeneric(3));
      if (pkg.containsKey(extendsName)) {
        parent = Store.getQualifiedType(extendsName); 
      }
      
      String className = n.getString(1);
      if (pkg.containsKey(className)) {
        currentClass = Store.getQualifiedType(className);
        currentClass.parent(parent);
      } else { currentClass = new Klass(className, parent); }

      visit(n);

      addClass(className, currentClass);
      currentClass = null;
      buildingClass = false;
    }

    /** Only reason to call this is to fetch the parent class name. */
    public String fetchParentClassName(Node n) {
      if (null != n) {
        return n
          .getGeneric(/* Type */ 0)
          .getGeneric(/* QualifiedIdentifier */ 0)
          .getString(/* The actual class name */ 0);
      }
      return null;
    }

    /** Visit the specified node. */
    public void visit(Node n) {
      for (Object o : n) {
        if (o instanceof Node) dispatch((Node)o);
      }
    }
  }

  /**
   * 2. This inner class takes in a JavaAST, and constructs meta
   * information for all of the class' members.
   */
  class MemberAnalyzer extends Analyzer {

    public MemberAnalyzer() { super(); }

    /** Visit specified class declaration node and add class. */
    public void visitClassDeclaration(GNode n) {
      currentClass = Store.getQualifiedType(n.getString(1));
      visit(n);
      currentClass = null;
    }

    // =========================================================================

    // Let's analyze fields

    /** 
     * Visit specified field declaration node and add field to class.
     * This applies solely to instance and class fields.
     */    
    public void visitFieldDeclaration(GNode n) {
      if (!buildingMethod) {
        buildingField = true;
        currentField = currentClass.new Field();

        visit(n); 

        currentField.incorporate();
        currentField = null;
        buildingField = false;
      }
    }

    /** Visit specified qualified identifier node. */
    public void visitQualifiedIdentifier(GNode n) {
      String typename = n.getString(0);
      Klass klass = Store.getQualifiedType(typename);
      if (buildingField && (null == currentField.type())) {
        currentField.type(new Klass(klass));
      } else if (buildingMethod) {
        if (null == currentMethod.type()) {
          currentMethod.type(new Klass(klass));
        }
        if (buildingParameter) {
          currentParameter.type(new Klass(klass));
        }
      }
    }

    /** Visit specified type node. */
    public void visitType(GNode n) {
      GNode dimensions = n.getGeneric(1);
      visit(n); // by now, should have type
      if (null == dimensions) { dimensions = GNode.create("ZeroDimensions"); }
      if (buildingField) {
        currentField.type().dimensions(dimensions.size());
      }
      if (buildingMethod && !buildingParameter) {
        currentMethod.type().dimensions(dimensions.size());
      }
      if (buildingParameter) {
        currentParameter.type().dimensions(dimensions.size());
      }
    }

    /** Visit specified primitive type node. */
    public void visitPrimitiveType(GNode n) {
      String typename = n.getString(0);
      if (buildingField && (null == currentField.type())) {
        currentField.type(new PrimitiveType(typename));
      } else if (buildingMethod) {
        if (null == currentMethod.type()) {
          currentMethod.type(new PrimitiveType(typename));
        }
        if (buildingParameter) {
          currentParameter.type(new PrimitiveType(typename));
        }
      }
    }

    /** Visit specified declarator node. */
    public void visitDeclarator(GNode n) {
      if (buildingField) {
        currentField.identifier(n.getString(0));
        currentField.body(n.getGeneric(2));
      }
    }

    /** Visit specified modifier node. */
    public void visitModifier(GNode n) {
      if (buildingField && (!currentField.isStatic())) {
        if (n.getString(0).equals("static")) {
          currentField.makeStatic();
        }
      }

      if (buildingMethod && (!currentMethod.isStatic())) {
        if (n.getString(0).equals("static")) {
          currentMethod.makeStatic();
        }
      }
    }

    // =========================================================================
   
    /** Visit specified method declaration node. */ 
    public void visitMethodDeclaration(GNode n) {
      buildingMethod = true;
      currentMethod = currentClass.new Method();
      currentMethod.identifier(n.getString(3));
      currentMethod.body(n.getGeneric(7));

      visit(n);

      currentMethod.incorporate();
      currentMethod = null;
      buildingMethod = false;
    }

    /** Visit specified void type node. */
    public void visitVoidType(GNode n) {
      if (buildingMethod) {
        currentMethod.type(new PrimitiveType("void"));
      }
    }
    
    /** Visit the specified constructor declaration node. Treat as method. */
    public void visitConstructorDeclaration(GNode n) {
      buildingMethod = true;
      currentMethod = currentClass.new Method();
      currentMethod.identifier(n.getString(2));

      visit(n);

      currentMethod = null;
      buildingMethod = false;
    }

    /** Visit specified formal parameter node. */
    public void visitFormalParameter(GNode n) {
      buildingParameter = true;
      currentParameter = new ParameterVariable();
      currentParameter.name(n.getString(3));
      
      visit(n); 

      currentMethod.addParameter(currentParameter);
      currentParameter = null;
      buildingParameter = false;  
    }
    
    /** Visit the specified node. */
    public void visit(Node n) {
      for (Object o : n) {
        if (o instanceof Node) dispatch((Node)o);
      }
    }

  }

  public HashMap<String, Klass> decomposeJavaAST(Node n) {
    new ClassAnalyzer().dispatch(n);
    new MemberAnalyzer().dispatch(n);
    
    // All classes should be in the package by now, so analyze their bodies:
    for (Klass k : this.getPackage().values()) {
      k.restructureBodies();
    }

    return this.getPackage();
  }
} // Store
