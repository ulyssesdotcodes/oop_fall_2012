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
  public void inheritObjectMethods() {
    Klass klass = new Klass("A", new Klass("Object", null));
    assertTrue(klass.parent().name().equals("Object"));
    assertTrue(0 == klass.fields().size());
    assertTrue(4 == klass.methods().size()); // Object methods! Whoohoo!
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
    Klass a = new Klass("A", new Klass("Object", null));
    Klass b = new Klass("B", a);
    Klass c = new Klass("C", a);
    Klass d = new Klass("D", c);
    Klass e = new Klass("E", c);

    assertTrue(a.parent().name().equals("Object"));
    assertTrue(a == b.parent());
    assertTrue(a == c.parent());
    assertTrue(a == d.parent().parent());
    assertTrue(a == e.parent().parent());
    assertTrue(c == d.parent());
    assertTrue(c == e.parent());
  }

  @Test
  public void incorporateMethods() {
    Klass object = new Klass("Object", null);

    Klass a = new Klass("A", object);
   
    // new A().foo() 
    Klass.Method foo = a.new Method();
    foo.identifier("foo");
    foo.incorporate();

    // new A().bar()
    Klass.Method bar = a.new Method();
    bar.identifier("bar");
    bar.incorporate();

    // new A().baz
    Klass.Field baz = a.new Field();
    baz.identifier("baz");
    baz.incorporate();

    Klass b = new Klass("B", a);

    // new B().foo()
    foo = b.new Method();
    foo.identifier("foo");
    foo.incorporate();

    // new B().toString()
    Klass.Method toString = b.new Method();
    toString.identifier("toString");
    toString.incorporate();

    Klass c = new Klass("C", b);

    // new C().baz
    baz = c.new Field();
    baz.identifier("baz");
    baz.incorporate();

    // new C().toString
    toString = c.new Method();
    toString.identifier("toString");
    toString.incorporate();

    // new C().qux()
    Klass.Method qux = c.new Method();
    qux.identifier("qux");
    qux.incorporate();

    // Test correct sizes
    assertTrue(4 == object.methods().size());
    assertTrue(6 == a.methods().size());
    assertTrue(6 == b.methods().size());
    assertTrue(7 == c.methods().size());

    assertTrue(0 == object.fields().size());
    assertTrue(1 == a.fields().size());
    assertTrue(1 == b.fields().size());
    assertTrue(1 == c.fields().size());

    // Test correct implementors
    ArrayList<Klass.Method> methods = c.methods();
    assertTrue(methods.get(0).identifier().equals("hashCode"));
    assertTrue(methods.get(0).implementor().equals(object));

    assertTrue(methods.get(1).identifier().equals("equals"));
    assertTrue(methods.get(1).implementor().equals(object));

    assertTrue(methods.get(2).identifier().equals("getClass"));
    assertTrue(methods.get(2).implementor().equals(object));

    assertTrue(methods.get(3).identifier().equals("toString"));
    assertTrue(methods.get(3).implementor().equals(c));

    assertTrue(methods.get(4).identifier().equals("foo"));
    assertTrue(methods.get(4).implementor().equals(b));

    assertTrue(methods.get(5).identifier().equals("bar"));
    assertTrue(methods.get(5).implementor().equals(a));

    assertTrue(methods.get(6).identifier().equals("qux"));
    assertTrue(methods.get(6).implementor().equals(c));

    ArrayList<Klass.Field> fields = c.fields();
    assertTrue(fields.get(0).identifier().equals("baz"));
    assertTrue(fields.get(0).implementor().equals(c));
  }
  
}
