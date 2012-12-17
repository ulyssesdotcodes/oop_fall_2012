package qimpp;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import qimpp.HeaderWriter;
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
public class HeaderWriterTest {
	
  ByteArrayOutputStream out = new ByteArrayOutputStream();
  Printer printer = new Printer(out);  
  
  @Test
	public void writeTypeDeclarationTest() {
    GNode foo	= GNode.create("Declaration", "Struct", "Foo", null);
    GNode bar	= GNode.create("Declaration", "Struct", "Bar", null);
    GNode declarations = GNode.create("Declarations", foo, bar);
    new HeaderWriter(printer).dispatch(declarations);
    try {
      String output = out.toString("UTF8");
      String compare = "struct __Foo;\nstruct __Foo_VT;\nstruct __Bar;\nstruct __Bar_VT;";
      assert(output.equals(compare));
    }
    catch(UnsupportedEncodingException e) {
      
    }
  }
 
 
  @Test
  public void writeStructTest() {
    GNode modifiers = GNode.create("Modifiers");
    GNode express = GNode.create("Expression", GNode.create("PrimaryIdentifier", "\"zebra\""), "\"=\"", GNode.create("StringLiteral", "\"In the room\""));
    GNode expressionStatement = GNode.create("ExpressionStatement", express);
    GNode block = GNode.create("Block", expressionStatement);
    GNode constructor = GNode.create("Constructor", null, block);
    GNode cd = GNode.create("ClassDeclaration", modifiers, "Foo", constructor);
    
    //hw.generateHeader(cd);
  } 
}
