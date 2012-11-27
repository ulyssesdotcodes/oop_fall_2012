package qimpp;

import static org.junit.Assert.*;
import org.junit.*;

import java.util.HashMap;

/**
 * Test suite for Scope.java.
 * 
 * Remember to fully qualify source files.
 * Run the following to run this file alone:
 *
 * <code>java org.junit.runner.JUnitCore qimpp.ScopeTest</code>
 *
 * @author Qimpp
 */
public class ScopeTest {

  /** Default constructor. */
  public ScopeTest() {}

  SymbolTable table;

  @Before
  public void createScope() {
    table = new SymbolTable("::");
  }

  @Test
  public void testRoot() {
    assertTrue(table.current().isRoot());
  }
  
}
