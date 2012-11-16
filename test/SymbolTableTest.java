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
 * Test suite for SymbolTable.java.
 * Test file is Point.java.
 * IMPORTANT: Make sure fileToParse has the right location.
 *
 * Remember to fully qualify source files.
 * Run the following to run this file alone:
 *
 * <code>java org.junit.runner.JUnitCore qimpp.SymbolTableTest</code>
 *
 * @author Qimpp
 */
public class SymbolTableTest extends Tool {

/*

   Based on the following scope tree.
  
:: = {
  ::Point = {
    DIMENSIONS = "declaration";
    ORIGIN = "declaration";
    coordinates = "declaration";
    ::__constructor_0 = {
    };
    ::getCoordinate = {
    };
    ::getDistanceFrom = {
      distanceSquared = "declaration";
      ::__for_2 = {
        i = "declaration";
        ::__tmp_3 = {
          diff = "declaration";
        };
      };
      ::__tmp_1 = {
        a = "declaration";
      };
    };
    ::main = {
      p1 = "declaration";
    };
    ::toString = {
    };
  };
};

*/

  /** Symbol table. */
  protected SymbolTable table;

  /** Node tree. */
  protected Node tree;

  /** File to parse. */
  private String fileToParse = "/Users/vivek/Point.java";

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

  public void run() throws IOException, ParseException {
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

  /** Resets scope to root. */
  public void resetToRoot() {
    table.setScope(table.root());
  };

  @Before
  public void createTable() throws IOException, ParseException {
    run();
    table = new SymbolTable("");
    table.incorporate(this.tree);
  }

  @Test
  public void forLoopDeclarationInScope() {
    /*
     * With the following code, we want the declared i to be visible 
     * within the inner loop but we don't want diff, declared 
     * in the body, to be visible in the for statement definition.
     *
     * <code>
     * for (int i = 0; i<DIMENSIONS; i++) {
     *   double diff = this.getCoordinate(i) - p.getCoordinate(i);
     *   distanceSquared += diff * diff;
     * }
     * </code>
     */
 
    resetToRoot();

    // get to the right place (for statement) in the scope tree 
    table.enter("Point");
    table.enter("getDistanceFrom");
    table.enter("__for_2");
    table.enter("__tmp_3");

    // make sure we're at the right place
    // i.e. no children scopes
    assertTrue(table.current().hasSymbols());
    assertFalse(table.current().hasNested());

    // check we have scope of both i and diff
    assertTrue(null != table.lookup("i"));
    assertTrue(null != table.lookup("diff"));

    // go up one level
    table.exit();

    // check we have scope of i but not diff
    assertTrue(null != table.lookup("i"));
    assertFalse(null != table.lookup("diff"));
   
    // done! Set scope to root.
    resetToRoot();
  }

  @Test
  public void classFieldsInScope() {
    /**
     * Traverse all the scopes, and make sure
     * that class fields are visible in all scopes.
     */

    resetToRoot();

    // class field scope at class level
    table.enter("Point");
    assertTrue(null != table.lookup("ORIGIN"));

    // class field scope at method level
    table.enter("getDistanceFrom");
    assertTrue(null != table.lookup("ORIGIN"));

    // class field scope at for statement level
    table.enter("__for_2");
    assertTrue(null != table.lookup("ORIGIN"));

    // class field scope at block level
    table.enter("__tmp_3");
    assertTrue(null != table.lookup("ORIGIN"));

    resetToRoot();
  }

  @Test
  public void correctQualifiedNames() {
    resetToRoot();

    // class field scope at class level
    table.enter("Point");
    System.out.println(table.current().getQualifiedName());
    assertTrue(table.current().getQualifiedName()
        .equals("::Point"));

    // class field scope at method level
    table.enter("getDistanceFrom");
    assertTrue(table.current().getQualifiedName()
        .equals("::Point::getDistanceFrom"));

    // class field scope at for statement level
    table.enter("__for_2");
    assertTrue(table.current().getQualifiedName()
        .equals("::Point::getDistanceFrom::__for_2"));

    // class field scope at block level
    table.enter("__tmp_3");
    assertTrue(table.current().getQualifiedName()
        .equals("::Point::getDistanceFrom::__for_2::__tmp_3"));

    resetToRoot();
  }

}
