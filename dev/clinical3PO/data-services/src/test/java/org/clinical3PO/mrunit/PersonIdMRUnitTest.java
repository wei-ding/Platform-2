package org.clinical3PO.mrunit;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.junit.Before;
import org.junit.Test;

import org.clinical3PO.hadoop.mappers.PersonIdMapper;
import org.clinical3PO.hadoop.reducers.PersonIdReducer;

public class PersonIdMRUnitTest {

	private MapDriver<LongWritable, Text, Text, Text> mapDriver;
	private ReduceDriver<Text, Text, NullWritable, Text> reduceDriver;
	private MapReduceDriver<LongWritable, Text, Text, Text, NullWritable, Text> mapReduceDriver;

	@Before
	public void setUp() throws IOException {

		// Setup the mapper
		PersonIdMapper personIdMapper = new PersonIdMapper();
		mapDriver = new MapDriver<LongWritable, Text, Text, Text>();

		// Setup the reducer
		PersonIdReducer personIdReducer = new PersonIdReducer();
		reduceDriver = new ReduceDriver<Text, Text, NullWritable, Text>();

		mapReduceDriver = new MapReduceDriver<LongWritable, Text, Text, Text, NullWritable, Text>();

		Configuration conf =  mapReduceDriver.getConfiguration();
		conf.set("personId", "138312");
		conf.set("fs.defaultFS", "file:///");
		conf.set("mapreduce.jobtracker.address", "local");
		// This file is used to get the full descriptions of the attributes that are measured
		conf.set("conceptFile", "src/test/resources/concept.txt");

		mapDriver.setMapper(personIdMapper);
		reduceDriver.setReducer(personIdReducer);

		mapReduceDriver.setMapper(personIdMapper);
		mapReduceDriver.setReducer(personIdReducer);
		
	}

	@Test
	public void mapReduceTest() throws Exception {

		String input = "OBSERVATION;2676118;138312;16;01/01/14;06:08;4.9;NULL;NULL;NULL;NULL;NULL;0;0;NULL;NULL;NULL;NULL";
		mapReduceDriver.withInput(new LongWritable(1234567), new Text(input));
		input = "OBSERVATION;2676118;138312;36;01/01/14;06:08;14.5;NULL;NULL;NULL;NULL;NULL;0;0;NULL;NULL;NULL;NULL";
		mapReduceDriver.withInput(new LongWritable(1234567), new Text(input));
		
		// The below line should be ignored (we are looking for patient with id 138312
		input = "OBSERVATION;2676118;138313;36;01/01/14;06:08;14.5;NULL;NULL;NULL;NULL;NULL;0;0;NULL;NULL;NULL;NULL";
		mapReduceDriver.withInput(new LongWritable(1234567), new Text(input));

		mapReduceDriver.withOutput(NullWritable.get(), new Text(
				"K;01/01/14;06:08;4.9;mEq/L;"));
		mapReduceDriver.withOutput(NullWritable.get(), new Text(
				"WBC;01/01/14;06:08;14.5;cells/nL;"));

		mapReduceDriver.runTest();

	}

}
