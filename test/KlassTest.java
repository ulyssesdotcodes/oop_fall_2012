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

  @Test
  public void instantiateWithFields() throws Exception {
    Klass klass = new Klass("A");

    // primitive type
    Klass.Field fieldOne = klass.new Field("foo", new PrimitiveType("int"));
    assertTrue(1 == klass.getFields().size());
    assertTrue(fieldOne.getName().equals("foo"));
    assertTrue(fieldOne.getType().getName().equals("int32_t"));

    // qualified type
    Klass.Field fieldTwo = klass.new Field("bar", new QualifiedType("B", "C"));
    assertTrue(2 == klass.getFields().size());
    assertTrue(fieldTwo.getName().equals("bar"));
    assertTrue(fieldTwo.getType().getName().equals("C"));
    assertTrue(fieldTwo.getType().getQualifiedName().equals("::B::C"));

    // do we need to initialize?
  }

  @Test
  public void instantiateWithMethods() throws Exception {
    Klass klass = new Klass("A");

    // primitive return type, no parameters
    Klass.Method methodOne = klass.new Method("doFoo", 
                                              new PrimitiveType("int"));
    assertTrue(1 == klass.getMethods().size());
    assertTrue(methodOne.getName().equals("doFoo"));
    assertTrue(methodOne.getType().getName().equals("int32_t"));
    assertTrue(0 == methodOne.getParameters().size());

    // primitive return type, several parameters
    
    final ParameterVariable p1 = 
      new ParameterVariable(new PrimitiveType("int"), "hello", true);
    final ParameterVariable p2 = 
      new ParameterVariable(new QualifiedType("B"), "b");
    ArrayList<ParameterVariable> parameters = 
      new ArrayList<ParameterVariable> () {{
      add(p1);
      add(p2);
    }};
    Klass.Method methodTwo = klass.new Method("doBar",
                                              new PrimitiveType("boolean"));
    assertTrue(2 == klass.getMethods().size());
    assertTrue(methodTwo.getName().equals("doBar"));
    assertTrue(methodTwo.getType().getName().equals("bool"));
    assertTrue(2 == methodTwo.getParameters().size());

    assertTrue(methodTwo.getParameters().get(0).getName().equals("hello"));
    assertTrue(methodTwo.getParameters().get(0).isPointer());
    assertTrue(methodTwo.getParameters().get(1).getName().equals("b"));
    assertTrue(!methodTwo.getParameters().get(1).isPointer());

    
    // qualified return type, no parameters
    
    // qualified return type, several parameters 

  }  

  
}
