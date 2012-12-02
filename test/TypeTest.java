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
    assertTrue(new PrimitiveType("long").name().equals("int64_t"));
    assertTrue(new PrimitiveType("int").name().equals("int32_t"));
    assertTrue(new PrimitiveType("short").name().equals("int16_t"));
    assertTrue(new PrimitiveType("byte").name().equals("int8_t"));
    assertTrue(new PrimitiveType("char").name().equals("char"));
    assertTrue(new PrimitiveType("float").name().equals("float"));
    assertTrue(new PrimitiveType("double").name().equals("double"));
    assertTrue(new PrimitiveType("boolean").name().equals("bool"));

    assertTrue(new PrimitiveType("long").qualifiedName().equals("int64_t"));
    assertTrue(new PrimitiveType("int").qualifiedName().equals("int32_t"));
    assertTrue(new PrimitiveType("short").qualifiedName().equals("int16_t"));
    assertTrue(new PrimitiveType("byte").qualifiedName().equals("int8_t"));
    assertTrue(new PrimitiveType("char").qualifiedName().equals("char"));
    assertTrue(new PrimitiveType("float").qualifiedName().equals("float"));
    assertTrue(new PrimitiveType("double").qualifiedName().equals("double"));
    assertTrue(new PrimitiveType("boolean").qualifiedName().equals("bool"));
  }

  // ===========================================================================

  @Test
  public void qualifiedType() throws Exception{
    assertTrue(new QualifiedType("A").name().equals("A"));
    assertTrue(new QualifiedType("A", "B").name().equals("B"));
    assertTrue(new QualifiedType("A", "B", "C").name().equals("C"));
    assertTrue(new QualifiedType("A", "B", "C", "D").name().equals("D"));
    assertTrue(new QualifiedType("A", "B", "C", "D", "E").name().equals("E"));

    assertTrue(new QualifiedType("A")
        .qualifiedName().equals("::A"));
    assertTrue(new QualifiedType("A", "B")
        .qualifiedName().equals("::A::B"));
    assertTrue(new QualifiedType("A", "B", "C")
        .qualifiedName().equals("::A::B::C"));
    assertTrue(new QualifiedType("A", "B", "C", "D")
        .qualifiedName().equals("::A::B::C::D"));
    assertTrue(new QualifiedType("A", "B", "C", "D", "E")
        .qualifiedName().equals("::A::B::C::D::E"));

    // This should help with the internal class usage requirement, i.e. __Foo.
    assertTrue(new QualifiedType("A")
        .context().equals("::"));
    assertTrue(new QualifiedType("A", "B")
        .context().equals("::A"));
    assertTrue(new QualifiedType("A", "B", "C")
        .context().equals("::A::B"));
    assertTrue(new QualifiedType("A", "B", "C", "D")
        .context().equals("::A::B::C"));
    assertTrue(new QualifiedType("A", "B", "C", "D", "E")
        .context().equals("::A::B::C::D"));
  }


}
