package qimpp;

import static org.junit.Assert.*;
import org.junit.*;

import java.util.ArrayList;

/**
 * Test suite for Klass.java.
 * 
 * Remember to fully qualify source files.
 * Run the following to run this file alone:
 *
 * <code>java org.junit.runner.JUnitCore qimpp.KlassTest</code>
 *
 * @author Qimpp
 */
public class KlassTest {

  /** Default constructor. */
  public KlassTest() {}

  @Test
  public void createClass() {
    Klass klass = new Klass("A");
    assertTrue(null == klass.getParent());  // so, "Object"
    assertTrue(0 == klass.getFields().size());
    assertTrue(0 == klass.getMethods().size()); 
  }

  @Test
  public void inheritanceTree() {
    /**
     *        A
     *       / \
     *      B   C
     *         / \
     *        D   E
     */
    Klass a = new Klass("A");
    Klass b = new Klass("B", a);
    Klass c = new Klass("C", a);
    Klass d = new Klass("D", c);
    Klass e = new Klass("E", c);

    assertTrue(null == a.getParent());
    assertTrue(a == b.getParent());
    assertTrue(a == c.getParent());
    assertTrue(a == d.getParent().getParent());
    assertTrue(a == e.getParent().getParent());
    assertTrue(c == d.getParent());
    assertTrue(c == e.getParent());
  }

  // TODO: Implement and test the setting of initializations
  @Test
  public void instantiateWithFields() throws Exception {
    Klass klass = new Klass("A");

    // primitive type

    // qualified type

  }

  // TODO: Implement and test the setting of bodies
  @Test
  public void instantiateWithMethods() {

    // primitive return type, no parameters

    // primitive return type, several parameters

    // qualified return type, no parameters

    // qualified return type, several parameters 
    
  }

  
}
