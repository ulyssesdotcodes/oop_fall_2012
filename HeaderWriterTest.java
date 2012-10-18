package qimpp;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;
import java.io.File;
import qimpp.HeaderWriter;
import xtc.*;
import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;
import xtc.tree.Location;
import xtc.tree.Printer; 
import xtc.util.Tool;

/**
 * @author QIMPP
 */
public class HeaderWriterTest {
	
  public static Scanner in;  
	HeaderWriter hw = new HeaderWriter();
  File file = new File("testiles/out.h");
	
  @Test
	public void writeTypeDeclarationTest() {
    GNode foo	= GNode.create("Declaration", "Struct", "\"Foo\"", null);
    GNode bar	= GNode.create("Declaration", "Struct", "\"Bar\"", null);
    GNode declarations = GNode.create("Declarations", foo, bar);
    // System.out.println(declarations.toString());
    hw.generateHeader(declarations);
  }
}
