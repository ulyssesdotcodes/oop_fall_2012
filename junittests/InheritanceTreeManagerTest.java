package qimpp;

import xtc.tree.Node;
import xtc.tree.GNode;
import xtc.tree.Visitor;
import qimpp.InheritanceTreeManager;

import static org.junit.Assert.assertEquals;
import junit.framework.JUnit4TestAdapter;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Tests for InheritanceTreeManager. Template. Not done yet
 *
 */
public class InheritanceTreeManagerTest  {

        InheritanceTreeManager treeManager;

        @Before public void setUp() {

          //
          // Insert some initial values into the inheritance
          // tree
          //

          //System.out.println("Running setup");
          treeManager = new InheritanceTreeManager(
              GNode.create("ClassDeclaration"));
          // Put in some example classes
          GNode newClass = GNode.create("ClassDeclaration", "Point");

          //System.out.println("Inserting Point");
          ArrayList<String> point = new ArrayList<String>();
            point.add("qimpp");
            point.add("Point");
          treeManager.insertClass(point, null, newClass);

          //System.out.println("Inserting ColorPoint");
          //System.out.println(point.get(0) + " " + point.get(1));

          //System.out.println("Point really in?: " + Boolean.toString(treeManager.dereference(point) != null));

          newClass = GNode.create("ClassDeclaration", "ColorPoint");
          ArrayList<String> ColorPoint = new ArrayList<String>();
            ColorPoint.add("qimpp");
            ColorPoint.add("ColorPoint");
          treeManager.insertClass(ColorPoint, point, newClass);
          //System.out.println("ColorPoint really in?: " + Boolean.toString(treeManager.dereference(ColorPoint) != null));

          //
          // Test that classes with the same name in different
          // packages are distinct
          //
          newClass = GNode.create("ClassDeclaration", "OtherColorPoint");
          ColorPoint = new ArrayList<String>( 
              Arrays.asList("org", "fake", "ColorPoint") );
          treeManager.insertClass(ColorPoint, null, newClass);

          System.out.println(treeManager.toString());
        }

        @Test public void testDereference() {
          ArrayList<String> point = new ArrayList<String>();
            point.add("qimpp");
            point.add("Point");
          GNode pointHopefully = treeManager.dereference(point);
          assertTrue(pointHopefully != null);
          //System.out.println("Test:Point really in?: " + Boolean.toString(treeManager.dereference(point) != null));

          pointHopefully = (GNode) treeManager.dereference(point).getProperty("ClassDeclaration");
          assertTrue("Point".equals(pointHopefully.getString(0)));

          // Test that classes in the same sub-namespace are
          // inserted correctly
          point = new ArrayList<String>( Arrays.asList("qimpp",
                "ColorPoint") );
          ////System.out.println("Returned node name = " + treeManager.dereference(point).getName());
          pointHopefully = (GNode) treeManager.dereference(point).getProperty("ClassDeclaration");
          
          assertTrue(pointHopefully.getString(0).equals("ColorPoint"));
          // Test that getClassTreeNode(ArrayList<String>) aliases
          // dereference
          pointHopefully = treeManager.getClassTreeNode(point);
          //assertTrue("Point".equals((GNode)pointHopefully.get(0)));

        }

        /** See that inheritance is properly preserved */
        @Test public void testInheritance () {
          ArrayList<String> point = new ArrayList<String>(
             Arrays.asList("qimpp", "Point") );
          GNode pointNode = treeManager.dereference(point);

          ArrayList<String> colorPoint = new ArrayList<String>(
              Arrays.asList("qimpp", "ColorPoint") );
          GNode colorNode = treeManager.dereference(colorPoint);

          assertTrue( colorNode != null );
          assertTrue( pointNode != null );
          GNode g = (GNode)(treeManager.getParent(colorNode).getProperty("ClassDeclaration"));
          System.out.println("\n" + g.getString(0));

          assertTrue( treeManager.getParent(colorNode) == pointNode );

          //
          // Test distinction between package names
          //

          ArrayList<String> otherColor =
            new ArrayList<String>( Arrays.asList("org", "fake",
                  "ColorPoint"));
          GNode otherColorNode = treeManager.dereference(otherColor);

          assertTrue( otherColorNode != null );
          assertTrue( treeManager.getParent( otherColorNode )
                        != treeManager.getParent(colorNode) );
          assertTrue( otherColorNode != colorNode );
        }
         
        public static junit.framework.Test suite() {
                 return new JUnit4TestAdapter(InheritanceTreeManagerTest.class);
        }

        /*
        public int unused;
        @Test public void divideByZero() {
                int zero= 0;
                int result= 8/zero;
                unused= result; // avoid warning for not using result
        }*/
         
        /*
        @Test public void testEquals() {
                assertEquals(12, 12);
                assertEquals(12L, 12L);
                assertEquals(new Long(12), new Long(12));

                assertEquals("Size", 12, 13);
                assertEquals("Capacity", 12.0, 11.99, 0.0);
        }*/

        @Test public void testDisambiguate(){
            
        }

        @Test public void testGetClassTreeNode() {

        }

        @Test public void testGetParent() {

        }

}
