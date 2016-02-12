/**
 * 
 */
package org.clinical3PO.learn.main;

import org.apache.hadoop.conf.Configuration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author sean
 *
 */
public class FEMapperTest {
	
	//let's have a FEMapper built here that we can try out.
	static FEMapper fmap;
	static Configuration conf;
	

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.err.println("setUpBeforeClass()");
		fmap = new FEMapper();
		conf = new Configuration();
		
		//TODO
		//TODO
		//TODO
		//************************************************************
		//so HERE figure out how to spoof a configuration that the mapper
		//would use to load up a filter config file, feature vector config file,
		//and a command line.
		//looking into e.g. JUnit parameterized tests...
		//http://www.tutorialspoint.com/junit/junit_parameterized_test.htm
		//but I should be able to do something like this.
		String cmdline = "-c filterconfig.txt -fec baseFEConfig.txt -b 00:00 -e 06:00 -ca diasabp -ct 47:01 -id input -od output -arffname arffy.arff";
		String[] otherArgs = cmdline.split("\\s+");
		System.err.println("-- got " + otherArgs.length + " arguments");		//DEBUG mainly here to avoid compiler warning
		//TODO: BUT THAT DOESN'T DO GLOBBING OR ANYTHING LIKE THAT...
		//but. that should give us the same sort of thing we get in FEMain with this line:
		//String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		
		//THEN!
		
		//TODO
		//TODO
		//TODO
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.err.println("tearDownAfterClass()");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		System.err.println("setUp()");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		System.err.println("tearDown()");
	}

	/**
	 * Test method for {@link org.clinical3PO.learn.main.FEMapper#applyStrategyToLine(java.lang.String[], org.clinical3PO.learn.util.FEStrategyBase, int, boolean)}.
	 */
	@Test
	public void testApplyStrategyToLine() {
		//fail("Not yet implemented");
		System.err.println("testApplyStrategyToLine()");

	}

	/**
	 * Test method for {@link org.clinical3PO.learn.main.FEMapper#handleLine(java.lang.String[])}.
	 */
	@Test
	public void testHandleLine() {
		//fail("Not yet implemented");
		System.err.println("testHandleLine()");
	}

	/**
	 * Test method for {@link org.clinical3PO.learn.main.FEMapper#processLine(java.lang.String)}.
	 */
	@Test
	public void testProcessLine() {
		//fail("Not yet implemented");
		System.err.println("testProcessLine()");
	}

}
