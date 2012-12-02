package qimpp;

import xtc.tree.Node;
import xtc.tree.GNode;

import static org.junit.Assert.*;
import org.junit.rules.ExpectedException;
import org.junit.*;


/**
 * Test suite for InheritanceTreeTest.java.
 * TODO: Package management
 *
 * <code>java org.junit.runner.JUnitCore qimpp.InheritanceTreeTest</code>
 *
 * @author Qimpp
 */
public class InheritanceTreeTest {

  GNode root;

  @Before
  public void setUpTree() {
    this.root = GNode.create("Object");
    this.root.addNode("Class");
    this.root.addNode("String");
  }

  @Test
  public void addRoot() {
    
  }

  // ===========================================================================



}
