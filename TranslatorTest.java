import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

import qimpp.Translator;


/**
 * @author QIMPP
 */
public class TranslatorTest {
		
	Translator t = new Translator();

	@Test
	public void getName() {
		assertEquals("Java to C++ Translator", t.getName());
	}

	@Test
	public void run() {
		
	}
}
