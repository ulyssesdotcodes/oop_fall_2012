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
    // TODO

    // .context() should help with the internal 
    // class usage requirement, i.e. __Foo.
  }


}
