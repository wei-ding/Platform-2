package gov.va.research.red.ex;

import gov.va.research.red.CVResult;
import gov.va.research.red.CVScore;
import gov.va.research.red.ex.REDExCrossValidator;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class REDExCrossValidatorTest {

	private static final Logger LOG = LoggerFactory.getLogger(REDExCrossValidatorTest.class);

	private static final String TEST_VTT_FILENAME = "weight1000.vtt";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	
	@Test
	public void testCrossValidate() {
		try {
			URL vttFileURL = getClass().getClassLoader().getResource(TEST_VTT_FILENAME);
			Assert.assertNotNull(vttFileURL);
			File vttFile = null;
			try {
				vttFile = new File(vttFileURL.toURI());
			} catch (URISyntaxException e) {
				throw new AssertionError(e);
			}
			Assert.assertNotNull(vttFile);
			Assert.assertTrue(vttFile.exists());
			REDExCrossValidator rexcv = new REDExCrossValidator();
			List<CVResult> results = rexcv.crossValidate(Arrays.asList(new File[] { vttFile }), "weight", 10, true, -1);
			int i = 0;
			for (CVResult result : results) {
				LOG.info("--- Run " + (i++) + " ---");
				LOG.info(result.getScore().getEvaluation());
			}
			LOG.info("--- Aggregate ---");
			CVResult aggregate = CVResult.aggregate(results);
			LOG.info(aggregate.getScore().getEvaluation());
			
			Assert.assertTrue("Accuracy is very bad", aggregate.getScore().calcAccuracy() > 0.25f);

		} catch (IOException e) {
			throw new AssertionError("Cross validation failed.", e);
		}
	}
}
