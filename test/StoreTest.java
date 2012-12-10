package qimpp;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import java.util.Iterator;
import java.util.HashMap;
import java.util.ArrayList;

import xtc.lang.JavaFiveParser;

import xtc.parser.ParseException;
import xtc.parser.Result;

import xtc.tree.Node;
import xtc.tree.Visitor;

import xtc.util.Tool;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * Test suite for Store.java.
 * 
 * Remember to fully qualify source files.
 * Run the following to run this file alone:
 *
 * <code>java org.junit.runner.JUnitCore qimpp.StoreTest</code>
 *
 * @author Qimpp
 */
public class StoreTest extends Tool {

  /** Store object. */
  protected Store store;

  /** Node tree. */
  protected Node tree;

  /** The package of classes. */
  protected HashMap<String, Klass> pkg;

  /** File to parse. */
  private String fileToParse = "../input/ABCDE.java";

  // ===========================================================================

  public String getName() {
    return "Testing our Store.";
  }

  public Node parse(Reader in, File file) throws IOException, ParseException {
    JavaFiveParser parser =
      new JavaFiveParser(in, file.toString(), (int)file.length());
    Result result = parser.pCompilationUnit(0);
    return (Node)parser.value(result);
  }

  public void run(String fileToParse) throws IOException, ParseException {
    File file = locate(fileToParse);
    if (Integer.MAX_VALUE < file.length()) {
      throw new IllegalArgumentException(file + ": file too large");
    }
    Reader in = runtime.getReader(file);
    Node node = parse(in, file);
    process(node);
  }

  public void process(Node node) {
    this.tree = node;
  }

  // ===========================================================================

  /** Default constructor. */
  public StoreTest() {}

  @Before
  public void setUp() throws IOException, ParseException {
    run(fileToParse);
    store = new Store();
    pkg = store.decomposeJavaAST(this.tree);
  }

  @Test
  public void rightPackageSize() {
    // This includes the java.lang main classes.
    assertTrue(pkg.size() == 5 + 3);
  }

  @Test
  public void rightClassNames() {
    assertTrue(pkg.containsKey("Object"));
    assertTrue(pkg.containsKey("String"));
    assertTrue(pkg.containsKey("Class"));
    assertTrue(pkg.containsKey("A"));
    assertTrue(pkg.containsKey("B"));
    assertTrue(pkg.containsKey("C"));
    assertTrue(pkg.containsKey("D"));
    assertTrue(pkg.containsKey("E"));
  }

  @Test
  public void rightClassInheritance() {
    Klass a = pkg.get("A");
    Klass b = pkg.get("B");
    Klass c = pkg.get("C");
    Klass d = pkg.get("D");
    Klass e = pkg.get("E");

    assertTrue(e.parent() == c);
    assertTrue(d.parent() == c);
    assertTrue(c.parent() == a);
    assertTrue(b.parent() == a);
    assertTrue(a.parent() != null);
    assertTrue(a.parent().name().equals("Object"));
  }

  @Test
  public void fieldsAreSetCorrectly() {
    Klass a = pkg.get("A");
    ArrayList<Klass.Field> fields = a.fields();
    assertTrue(fields.size() == 2);
    assertTrue(fields.get(0).identifier().equals("fieldOne"));
    assertTrue(fields.get(0).type().name().equals("int32_t"));
    assertTrue(fields.get(1).identifier().equals("fieldTwo"));
    assertTrue(fields.get(1).type().name().equals("String"));
  }

  @Test
  public void methodsAreSetCorrectly() {
    Klass a = pkg.get("A");
    ArrayList<Klass.Method> methods = a.methods();
    assertTrue(methods.size() == 10);

    assertTrue(methods.get(4).identifier().equals("f1"));
    assertTrue(methods.get(4).type().name().equals("void")); // void

    assertTrue(methods.get(5).identifier().equals("f2"));
    assertTrue(null != methods.get(5).type());
    assertTrue(methods.get(5).parameters().get(0).name().equals("i"));
    assertTrue(methods.get(5).parameters().get(0).type()
                                                    .name()
                                                    .equals("int32_t"));


    assertTrue(methods.get(6).identifier().equals("f3"));
    assertTrue(null != methods.get(6).type());
    assertTrue(methods.get(6).parameters().get(0).name().equals("i"));
    assertTrue(methods.get(6).parameters().get(0).type()
                                                    .name()
                                                    .equals("int32_t"));
    assertTrue(methods.get(6).parameters().get(1).name().equals("str"));
    assertTrue(methods.get(6).parameters().get(1).type()
                                                    .name()
                                                    .equals("String"));

    assertTrue(methods.get(7).identifier().equals("f4"));
    assertTrue(methods.get(7).type().name().equals("String"));

    assertTrue(methods.get(8).identifier().equals("f5"));
    assertTrue(null != methods.get(8).type());
    assertTrue(methods.get(8).parameters().get(0).name().equals("i"));
    assertTrue(methods.get(8).parameters().get(0).type()
                                                    .name()
                                                    .equals("int32_t"));

    assertTrue(methods.get(9).identifier().equals("f6"));
    assertTrue(null != methods.get(9).type());
    assertTrue(methods.get(9).parameters().get(0).name().equals("i"));
    assertTrue(methods.get(9).parameters().get(0).type()
                                                    .name()
                                                    .equals("int32_t"));
    assertTrue(methods.get(9).parameters().get(1).name().equals("str"));
    assertTrue(methods.get(9).parameters().get(1).type()
                                                    .name()
                                                    .equals("String"));

  }

  
}
