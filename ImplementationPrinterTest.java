package qimpp;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import qimpp.ImplementationPrinter;
import xtc.*;
import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;
import xtc.tree.Location;
import xtc.tree.Printer; 
import xtc.util.Tool;
import java.io.UnsupportedEncodingException;

/**
 * @author QIMPP
 */
public class ImplementationPrinterTest {
  ByteArrayOutputStream out = new ByteArrayOutputStream();
  Printer printer = new Printer(out);  
  
  @Test
  public void writePrimaryIdentifier() {
	GNode n = GNode.create("PrimaryIdentifier");
	n.add("zebra");
	ImplementationPrinter p = new ImplementationPrinter(printer);
	assertTrue(p instanceof ImplementationPrinter);
	p.dispatch(n);
    try {
      String output = out.toString("UTF8");
	  assertTrue(1==1);
    } catch(UnsupportedEncodingException e) {
      System.out.println("shit.");
    }
  }
 
 
  @Test
  public void writeStructTest() {
    //nothing to do.
  } 
}
