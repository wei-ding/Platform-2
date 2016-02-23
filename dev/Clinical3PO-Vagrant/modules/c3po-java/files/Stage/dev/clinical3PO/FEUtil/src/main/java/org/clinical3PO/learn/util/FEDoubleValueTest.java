/**
 * 
 */
package org.clinical3PO.learn.util;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author sean
 *
 */
public class FEDoubleValueTest {

	/**
	 * Test method for {@link org.clinical3PO.learn.util.FEDoubleValue#getValueForString(java.lang.String)}.
	 */
	@Test (expected = NumberFormatException.class)
	public void testGetValueForString() throws Exception {
		FEDoubleValue v = new FEDoubleValue();
		
		//try a normal number
		assertEquals("FEDoubleValue should get 3.0 for getValueForString(\"3.0\")",3.0,v.getValueForString("3.0"));

		//try a normal number with whitespace
		assertEquals("FEDoubleValue should get 99.99 for getValueForString(\"\t99.99   \")",99.99,v.getValueForString("\t99.99   "));

		//try a negative number
		assertEquals("FEDoubleValue should get -32768.6477 for getValueForString(\"-32768.6477\")",-32768.6477,v.getValueForString("-32768.6477"));
		
		//this should be the last test, throwing the expected exception NumberFormatException
		v.getValueForString("IllegalString");
	}

}
