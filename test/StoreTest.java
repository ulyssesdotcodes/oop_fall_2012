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
  private String fileToParse = "./input/ABCDE.java";

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
    assertTrue(pkg.size() == 5);
  }

  @Test
  public void rightClassNames() {
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

    assertTrue(e.getParent() == c);
    assertTrue(d.getParent() == c);
    assertTrue(c.getParent() == a);
    assertTrue(b.getParent() == a);
    assertTrue(a.getParent() == null);
  }

  @Test
  public void fieldsAreSetCorrectly() {
    Klass a = pkg.get("A");
    ArrayList<Klass.Field> fields = a.getFields();
    assertTrue(fields.size() == 2);
    assertTrue(fields.get(0).getName().equals("fieldOne"));
    assertTrue(fields.get(0).getType().getName().equals("int32_t"));
    assertTrue(fields.get(1).getName().equals("fieldTwo"));
    assertTrue(fields.get(1).getType().getName().equals("String"));
  }

  @Test
  public void methodsAreSetCorrectly() {
    Klass a = pkg.get("A");
    ArrayList<Klass.Method> methods = a.getMethods();
    assertTrue(methods.size() == 6);

    assertTrue(methods.get(0).getName().equals("f1"));
    assertTrue(null == methods.get(0).getType()); // void

    assertTrue(methods.get(1).getName().equals("f2"));
    assertTrue(null != methods.get(1).getType());
    assertTrue(methods.get(1).getParameters().get(0).getName().equals("i"));
    assertTrue(methods.get(1).getParameters().get(0).getType()
                                                    .getName()
                                                    .equals("int32_t"));


    assertTrue(methods.get(2).getName().equals("f3"));
    assertTrue(null != methods.get(2).getType());
    assertTrue(methods.get(2).getParameters().get(0).getName().equals("i"));
    assertTrue(methods.get(2).getParameters().get(0).getType()
                                                    .getName()
                                                    .equals("int32_t"));
    assertTrue(methods.get(2).getParameters().get(1).getName().equals("str"));
    assertTrue(methods.get(2).getParameters().get(1).getType()
                                                    .getName()
                                                    .equals("String"));

    assertTrue(methods.get(3).getName().equals("f4"));
    assertTrue(methods.get(3).getType().getName().equals("String"));

    assertTrue(methods.get(4).getName().equals("f5"));
    assertTrue(null != methods.get(4).getType());
    assertTrue(methods.get(4).getParameters().get(0).getName().equals("i"));
    assertTrue(methods.get(4).getParameters().get(0).getType()
                                                    .getName()
                                                    .equals("int32_t"));

    assertTrue(methods.get(5).getName().equals("f6"));
    assertTrue(null != methods.get(5).getType());
    assertTrue(methods.get(5).getParameters().get(0).getName().equals("i"));
    assertTrue(methods.get(5).getParameters().get(0).getType()
                                                    .getName()
                                                    .equals("int32_t"));
    assertTrue(methods.get(5).getParameters().get(1).getName().equals("str"));
    assertTrue(methods.get(5).getParameters().get(1).getType()
                                                    .getName()
                                                    .equals("String"));

  }

  
}
