package org.clinical3PO.reducers;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.clinical3PO.hadoop.reducers.ObservationIdReducer;
import org.junit.Before;
import org.junit.Test;

public class ObservationIdReducerTest {
	private ReduceDriver<Text, Text,Text, Text> reduceDriver;

	@Before
	public void setUp() {

		// Setup the reducer
		ObservationIdReducer observationIdReducer = new ObservationIdReducer();
		reduceDriver = new ReduceDriver<Text, Text, Text, Text>();

		Configuration conf = reduceDriver.getConfiguration();
        
		conf.set("observationIdUnit","mEq/L");
		conf.set("observationIdName","Serum potassium");
		conf.set("mapreduce.jobtracker.address", "local");
		reduceDriver.setReducer(observationIdReducer);

	}

	@Test
	public void observationIdRecord() throws IOException {

		ArrayList<Text> list = new ArrayList<Text>(2);
		
		list.add(new Text("DEATH"));
		list.add(new Text("01/01/14;06:08;4.9;"));
	
		reduceDriver.withInput(new Text("138312~1"), list);
		reduceDriver.withOutput(new Text("138312"),new Text("01/01/14;06:08;4.9;mEq/L;Serum potassium;DEATH;"));

		reduceDriver.runTest();
	}
	
	

}
