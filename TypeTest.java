package qimpp;

import qimpp.Type;
import xtc.tree.Visitor;
import xtc.tree.GNode;

import static org.junit.Assert.*;
import org.junit.*;

public class TypeTest {
  GNode primitiveType;
  GNode qualifiedType;

  @Before public void initialize() {
    primitiveType = GNode.create("PrimitiveType");
    primitiveType.add("int");
    qualifiedType = GNode.create("QualifiedIdentifier");
    qualifiedType.add("ClassDeclaration");
  }

	@Test
	public void testPrimitives() {
    String cppType = Type.primitives
        .get(primitiveType.getString(0));
    assertEquals(cppType, "int32_t");
	}

	@Test
	public void testQualitifed() {
    assert( 1 + 1 == 2);
	}
}
