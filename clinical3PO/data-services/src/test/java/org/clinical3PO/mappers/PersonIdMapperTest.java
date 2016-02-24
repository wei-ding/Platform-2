package org.clinical3PO.mappers;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.junit.Before;
import org.junit.Test;

import org.clinical3PO.hadoop.mappers.PersonIdMapper;

public class PersonIdMapperTest {

	private MapDriver<LongWritable, Text, Text, Text> mapDriver;

	@Before
	public void setUp() {

		// Setup the mapper
		PersonIdMapper personIdMapper = new PersonIdMapper();
		mapDriver = new MapDriver<LongWritable, Text, Text, Text>();

		// Setup up the configuration, pass patientid that we are searching
		Configuration conf = mapDriver.getConfiguration();
		conf.set("personId", "138312");

		mapDriver.setMapper(personIdMapper);

	}

	@Test
	public void PatientIdOneRecord() throws IOException {
		String input = "OBSERVATION;2676118;138312;16;01/01/14;06:08;4.9;NULL;NULL;NULL;NULL;NULL;0;0;NULL;NULL;NULL;NULL";
		
		mapDriver.withInput(new LongWritable(1234567), new Text(input));
		mapDriver.withOutput(new Text("138312"), new Text("16;01/01/14;06:08;4.9;"));
		
		mapDriver.runTest();
	}
	
	@Test
	public void InvalidPatientNoRecord() throws IOException {
		String input = "OBSERVATION;2676118;138313;16;01/01/14;06:08;4.9;NULL;NULL;NULL;NULL;NULL;0;0;NULL;NULL;NULL;NULL";
		
		mapDriver.withInput(new LongWritable(1234567), new Text(input));
		// Patient id does not match, so there is no output from mapper
		mapDriver.runTest();
	}
	

}
