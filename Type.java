package qimpp;

import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;
import xtc.tree.Printer;

import xtc.util.Tool;

import java.util.HashMap;


/**
 * Converts Java PrimitiveType GNode objects into the corresponding C++ types.
 *
 * @author QIMPP
 */
class Type {
	/**
	 * For reference:
	 * Java primitive types: int, long, short, float, double, char, boolean
	 * C++ primitive types: int, long int, short int, float, double, char, bool
	 *
	 * Then there's weird stuff with signed vs unsigned char that Grimm will
	 * try to trip us up on.
	 */

	// not using generics to specify key, value types, so need to suppress warnings
	@SuppressWarnings(value = "unchecked")
	static HashMap<String, String> primitives = new HashMap<String, String>() {{
		put("long", "signed int64_t");
		put("int", "int32_t");
		put("short", "signed int16_t");
		put("byte", "signed int8_t");
		put("float", "float");
		put("double", "double");
		put("char", "char");
	}};

	static String translate(GNode fieldDec) {
	  
	  GNode fieldType = fieldDec.getGeneric(1).getGeneric(0);
	  
	  //Convert the variable type if it's primitive, otherwise just output it
	  
	  System.out.println(fieldType);
	  if(fieldType.hasName("PrimitiveType"))
	    return primitives.get(fieldType.getString(0));
	  else
	    return fieldType.getString(0);
	  
	  /* OLD
		return (String)new Visitor() {
			// if visiting primitive type
			String visitPrimitiveType(GNode n) {
				return (String) primitives.get(n.getString(0));
			}

			// if visiting qualified identifier
			String visitQualifiedIdentifier(GNode n) {
				return n.getString(0);
			}

			public String visit(Node n) {
		   	  for (Object o : n) if (o instanceof Node) dispatch((Node)o);
			}
		}.dispatch(n);
		*/
	}
}
