package org.clinical3PO.mappers;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.clinical3PO.hadoop.mappers.ObservationIdMapper;
import org.junit.Before;
import org.junit.Test;

public class ObservationIdMapperTest {
	private MapDriver<LongWritable, Text, Text, Text> mapDriver;

	@Before
	public void setUp() {

		// Setup the mapper
		ObservationIdMapper observationIdMapper = new ObservationIdMapper();
		mapDriver = new MapDriver<LongWritable, Text, Text, Text>();

		// Setup up the configuration, pass patientid that we are searching
		Configuration conf = mapDriver.getConfiguration();
		conf.set("personId","138312");
		conf.set("observationId", "16");

		mapDriver.setMapper(observationIdMapper);

	}

	@Test
	public void ObservationIdRecord() throws IOException {
				String input = "OBSERVATION;2676118;138312;16;01/01/14;06:08;4.9;NULL;NULL;NULL;NULL;NULL;0;0;NULL;NULL;NULL;NULL";

		mapDriver.withInput(new LongWritable(1234567), new Text(input));
		mapDriver.withOutput(new Text("138312~1"), new Text("01/01/14;06:08;4.9;"));
		
		mapDriver.runTest();
	}
	
	@Test
	public void InvalidPatientNoRecord() throws IOException {
		String input = "OBSERVATION;2676118;138312;1;01/01/14;06:08;4.9;NULL;NULL;NULL;NULL;NULL;0;0;NULL;NULL;NULL;NULL";
		
		mapDriver.withInput(new LongWritable(1234567), new Text(input));
		// Patient id does not match, so there is no output from mapper
		mapDriver.runTest();
	}

}
