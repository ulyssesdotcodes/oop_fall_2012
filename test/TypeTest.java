package qimpp;

import qimpp.Type;
import qimpp.PrimitiveType;
import qimpp.QualifiedType;

import java.lang.Exception;

import static org.junit.Assert.*;
import org.junit.rules.ExpectedException;
import org.junit.*;


/**
 * Test suite for TypeTest.java
 *
 * <code>java org.junit.runner.JUnitCore qimpp.TypeTest</code>
 *
 * @author Qimpp
 */
public class TypeTest {

  @Rule 
  public ExpectedException exception = ExpectedException.none();

  // ===========================================================================

  @Test
  public void primitiveTypeConversions() throws Exception {
    assertTrue(new PrimitiveType("long").getName().equals("int64_t"));
    assertTrue(new PrimitiveType("int").getName().equals("int32_t"));
    assertTrue(new PrimitiveType("short").getName().equals("int16_t"));
    assertTrue(new PrimitiveType("byte").getName().equals("int8_t"));
    assertTrue(new PrimitiveType("char").getName().equals("char"));
    assertTrue(new PrimitiveType("float").getName().equals("float"));
    assertTrue(new PrimitiveType("double").getName().equals("double"));
    assertTrue(new PrimitiveType("boolean").getName().equals("bool"));

    assertTrue(new PrimitiveType("long").getQualifiedName().equals("int64_t"));
    assertTrue(new PrimitiveType("int").getQualifiedName().equals("int32_t"));
    assertTrue(new PrimitiveType("short").getQualifiedName().equals("int16_t"));
    assertTrue(new PrimitiveType("byte").getQualifiedName().equals("int8_t"));
    assertTrue(new PrimitiveType("char").getQualifiedName().equals("char"));
    assertTrue(new PrimitiveType("float").getQualifiedName().equals("float"));
    assertTrue(new PrimitiveType("double").getQualifiedName().equals("double"));
    assertTrue(new PrimitiveType("boolean").getQualifiedName().equals("bool"));
  }

  @Test
  public void incorrectPrimitiveTypeException() throws Exception {
    exception.expect(Exception.class);
    exception.expectMessage("robert grimm is not a Java primitive type.");
    new PrimitiveType("robert grimm");
  }

  // ===========================================================================

  @Test
  public void qualifiedType() throws Exception{
    assertTrue(new QualifiedType("A").getName().equals("A"));
    assertTrue(new QualifiedType("A", "B").getName().equals("B"));
    assertTrue(new QualifiedType("A", "B", "C").getName().equals("C"));
    assertTrue(new QualifiedType("A", "B", "C", "D").getName().equals("D"));
    assertTrue(new QualifiedType("A", "B", "C", "D", "E").getName().equals("E"));

    assertTrue(new QualifiedType("A")
        .getQualifiedName().equals("::A"));
    assertTrue(new QualifiedType("A", "B")
        .getQualifiedName().equals("::A::B"));
    assertTrue(new QualifiedType("A", "B", "C")
        .getQualifiedName().equals("::A::B::C"));
    assertTrue(new QualifiedType("A", "B", "C", "D")
        .getQualifiedName().equals("::A::B::C::D"));
    assertTrue(new QualifiedType("A", "B", "C", "D", "E")
        .getQualifiedName().equals("::A::B::C::D::E"));

    // This should help with the internal class usage requirement, i.e. __Foo.
    assertTrue(new QualifiedType("A")
        .getContext().equals("::"));
    assertTrue(new QualifiedType("A", "B")
        .getContext().equals("::A"));
    assertTrue(new QualifiedType("A", "B", "C")
        .getContext().equals("::A::B"));
    assertTrue(new QualifiedType("A", "B", "C", "D")
        .getContext().equals("::A::B::C"));
    assertTrue(new QualifiedType("A", "B", "C", "D", "E")
        .getContext().equals("::A::B::C::D"));
  }


}
