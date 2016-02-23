package org.clinical3PO.reducers;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.junit.Before;
import org.junit.Test;

import org.clinical3PO.hadoop.reducers.PersonIdReducer;

public class PersonIdReducerTest {

	private ReduceDriver<Text, Text, NullWritable, Text> reduceDriver;

	@Before
	public void setUp() {

		// Setup the reducer
		PersonIdReducer personIdReducer = new PersonIdReducer();
		reduceDriver = new ReduceDriver<Text, Text, NullWritable, Text>();

		Configuration conf = reduceDriver.getConfiguration();

		conf.set("fs.defaultFS", "file:///");
		conf.set("mapreduce.jobtracker.address", "local");
		// This file is used to get the full descriptions of the attributes that are measured
		conf.set("conceptFile", "src/test/resources/concept.txt");

		reduceDriver.setReducer(personIdReducer);

	}

	@Test
	public void PatientIdOneRecord() throws IOException {

		ArrayList<Text> list = new ArrayList<Text>(1);
		
		list.add(new Text("16;01/01/14;06:08;4.9;"));

		reduceDriver.withInput(new Text("138312"), list);
		reduceDriver.withOutput(NullWritable.get(), new Text("K;01/01/14;06:08;4.9;mEq/L;"));

		reduceDriver.runTest();
	}
	
	@Test
	public void PatientIdMultipleRecords() throws IOException {

		ArrayList<Text> list = new ArrayList<Text>(2);
		
		list.add(new Text("16;01/01/14;06:08;4.9;"));
		list.add(new Text("36;01/01/14;06:08;14.5;"));

		reduceDriver.withInput(new Text("138312"), list);
		
		reduceDriver.withOutput(NullWritable.get(), new Text("K;01/01/14;06:08;4.9;mEq/L;"));
		reduceDriver.withOutput(NullWritable.get(), new Text("WBC;01/01/14;06:08;14.5;cells/nL;"));
		
		reduceDriver.runTest();
	}
	

}
