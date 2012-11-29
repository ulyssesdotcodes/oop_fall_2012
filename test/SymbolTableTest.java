package qimpp;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import xtc.lang.JavaFiveParser;

import xtc.parser.ParseException;
import xtc.parser.Result;

import xtc.tree.Node;
import xtc.tree.Visitor;

import xtc.util.Tool;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * TODO : RE-DO THIS BECAUSE ABCDE.java HAS BEEN MODIFIED.
 *
 * Test suite for SymbolTable.java.
 *
 * Remember to fully qualify source files.
 * Run the following to run this file alone:
 *
 * <code>java org.junit.runner.JUnitCore qimpp.SymbolTableTest</code>
 *
 * @author Qimpp
 */
public class SymbolTableTest extends Tool {

  /** Symbol table. */
  protected SymbolTable table;

  /** Node tree. */
  protected Node tree;

  /** File to parse. */
  private String fileToParse = "./input/ABCDE.java";
  private String secondFileToParse = "./input/FGH.java";

  // ==========================================================
 
  public String getName() {
    return "Testing our SymbolTable.";
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

  // ===========================================================

  /** Default constructor. */
  public SymbolTableTest() {}
 
  @Before 
  public void setUp() throws IOException, ParseException {
    run(fileToParse);
    table = new SymbolTable();
    table.incorporate(this.tree); 
    resetToRoot();
  }

  @After
  public void after() {
    resetToRoot();
  }

  /** Resets scope to root. */
  public void resetToRoot() {
    table.setScope(table.root());
  };

  @Test
  public void classes() {
    assertTrue(null != table.lookupScope("A"));
    assertTrue(null != table.lookupScope("B"));
    assertTrue(null != table.lookupScope("C"));
    assertTrue(null != table.lookupScope("D"));
    assertTrue(null != table.lookupScope("E"));
  }

  @Test
  public void methods() {
    table.enter("A");     // class
    table.enter("foo");   // method
    
    assertTrue(null != table.lookupScope("A"));
    assertTrue(null != table.lookupScope("B"));
    assertTrue(null != table.lookupScope("C"));
    assertTrue(null != table.lookupScope("D"));
    assertTrue(null != table.lookupScope("E"));
    assertTrue(null != table.lookupScope("foo")); 
    assertTrue(null == table.lookupScope("bar")); 
    
    resetToRoot();

    table.enter("D");
    table.enter("bar");
    assertTrue(null != table.lookupScope("A"));
    assertTrue(null != table.lookupScope("B"));
    assertTrue(null != table.lookupScope("C"));
    assertTrue(null != table.lookupScope("D"));
    assertTrue(null != table.lookupScope("E"));
    assertTrue(null != table.lookupScope("bar")); 
    assertTrue(null == table.lookupScope("foo"));

    resetToRoot();

    table.enter("E");
    table.enter("baz");
    assertTrue(null != table.getScope("::A::foo"));
    assertTrue(null != table.getScope("::A"));
    assertTrue(null == table.getScope("A"));
    assertTrue(null == table.getScope("F"));
    assertTrue(null == table.getScope("E::baz")); // note, need to fully resolve
  }

  @Test
  public void getScope() {
    SymbolTable.Scope scope = table.current().getNested("A");
    assertTrue(scope.getName().equals("A"));
    assertTrue(scope.getQualifiedName().equals("::A"));
  }

  @Test
  public void incorporateSecondFile() throws IOException, ParseException {
    run(secondFileToParse);
    table.incorporate(this.tree);
    assertTrue(null != table.lookupScope("A"));
    assertTrue(null != table.lookupScope("B"));
    assertTrue(null != table.lookupScope("C"));
    assertTrue(null != table.lookupScope("D"));
    assertTrue(null != table.lookupScope("E"));
    assertTrue(null != table.lookupScope("F"));
    assertTrue(null != table.lookupScope("G"));
    assertTrue(null != table.lookupScope("H"));
  }
}
