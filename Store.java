
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
 * underlying the project (11/28/2012).
 *
 * @author Qimpp
 */
class Store {

  /**
   * Classes in store.
   *
   * An ArrayList might not be the best choice, because
   * it means expensive lookup. However, a HashMap 
   * means slight duplication in using className to key
   * and also instantiate the Klass object.
   */
  private HashMap<String, Klass> pkg;

  public Store() {
    pkg = new HashMap<String, Klass>();
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

  // ===========================================================================

  /**
   * This inner class takes in a Java AST whose nodes
   * have been annotated with Scope properties, and
   * decomposes the Java AST, extracting then storing the 
   * relavant meta-information in meta Java object classes
   * like Klass, Method, Variable, Type, etc.
   */
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

    public Analyzer() {
      this.currentClass   = null;
      this.currentMethod  = null;
      this.currentField   = null;
    }

    /**
     * Add class to the package of classes list.
     *
     * @param klass Class to append.
     */
    public void addClass(String className, Klass klass) {
      Store.this.pkg.put(className, klass);
    }

    // =========================================================================

    class Indices {
      public static final int CLASS_NAME          = 1;
    }

    // =========================================================================

    // Let's analyze a class!

    /** Visit specified class declaration node and add class. */
    public void visitClassDeclaration(GNode n) {
      buildingClass = true;
      Klass parent = null;

      String extendsName = fetchParentClassName(n.getGeneric(3));
      if (pkg.containsKey(extendsName)) {
        parent = pkg.get(extendsName); 
      }
      
      String className = n.getString(Indices.CLASS_NAME);
      if (pkg.containsKey(className)) {
        currentClass = pkg.get(className);
        currentClass.setParent(parent);
      } else { currentClass = new Klass(className, parent); }

      visit(n);

      // Before we return from this method, we should have all the 
      // information we need to make the class in currentClass.
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

    // =========================================================================

    // Let's analyze that class' fields!

    // TODO: Handle the case of "int i, j;" 
    /** Visit specified field declaration node and add field to class. */    
    public void visitFieldDeclaration(GNode n) {
      buildingField = true;
      currentField = currentClass.new Field();

      visit(n); 

      currentField = null;
      buildingField = false;
    }

 
    /** Visit specified qualified identifier node. */
    public void visitQualifiedIdentifier(GNode n) {
      if (buildingField && (null == currentField.getType())) {
        currentField.setType(new QualifiedType(n.getString(0)));
      }
    }

    /** Visit specified primitive type node. */
    public void visitPrimitiveType(GNode n) {
      if (buildingField && (null == currentField.getType())) {
        currentField.setType(new PrimitiveType(n.getString(0)));
      }
    }

    // TODO: Handle the case of "int i, j;" See #visitFieldDeclaration
    /** Visit specified declarator node. */
    public void visitDeclarator(GNode n) {
      if (buildingField) {
        currentField.setName(n.getString(0));
      }
    }

    // =========================================================================

    // Let's analyze that class' methods! 
    
    public void visitMethodDeclaration(GNode n) {
      buildingMethod = true;
      currentMethod = currentClass.new Method();

      visit(n);

      currentMethod = null;
      buildingMethod = false;
    }

    // =========================================================================

    public void visitCompilationUnit(GNode n) { visit(n); }

    /** Visit the specified node. */
    public void visit(Node n) {
      for (Object o : n) {
        if (o instanceof Node) dispatch((Node)o);
      }  
    }
  }

  public HashMap<String, Klass> decomposeJavaAST(Node n) {
    new Analyzer().dispatch(n);
    return this.pkg;
  }


}
