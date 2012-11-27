package qimpp;

import qimpp.Store;
import qimpp.Klass;

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
public class StoreTest {

  /** Default constructor. */
  public StoreTest() {}

  /**
   * Tests:
   * 1. √ Add some number of classes, and test if they indeed got added.
   * 2. Test for members.
   * 3. Test scope?
   * 4. Handle overwritten and overloaded methods.
   */

  @Test
  public void addClasses() {
    Store store = new Store();
    store.add(new Klass("A"));
    store.add(new Klass("B"));
    store.add(new Klass("C"));
    assertTrue(store.getClasses().size() == 3);
  }

  @Test
  public void addMembers() {

  }

  @Test
  public void inheritsFromObject() {

  }














  
}
