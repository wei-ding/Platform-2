package org.clinical3PO.reducers;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.clinical3PO.hadoop.reducers.MultiObservationIdReducer;
import org.junit.Before;
import org.junit.Test;

public class MultiObservationIdReducerTest {
	private ReduceDriver<Text, Text,Text, Text> reduceDriver;
	@Before
	public void setUp() {

		// Setup the reducer
		MultiObservationIdReducer multiObservationIdReducer = new MultiObservationIdReducer();
		reduceDriver = new ReduceDriver<Text, Text, Text, Text>();

		Configuration conf = reduceDriver.getConfiguration();
		conf.set("observationDetails", "Creatinine~8~Serum creatinine~mg/dL#BUN~6~Blood urea nitrogen~mg/dL#");
		
		reduceDriver.setReducer(multiObservationIdReducer);

}
	@Test
	public void MultiObservationIdRecord() throws IOException {
     ArrayList<Text> list = new ArrayList<Text>(1);
     list.add(new Text("Alive"));
     list.add(new Text("01/01/14;06:08;4.9;"));
     ArrayList<Text> list1 = new ArrayList<Text>(1);
     list1.add(new Text("Alive"));
     list1.add(new Text("01/01/14;06:08;4.9;"));
     reduceDriver.withInput(new Text("Creatinine~138312"), list);
     reduceDriver.withInput(new Text("BUN~138312"), list1);
     reduceDriver.withOutput(new Text("Creatinine~138312"), new Text("01/01/14;06:08;4.9;mg/dL;Serum creatinine;Alive;"));
     reduceDriver.withOutput(new Text("BUN~138312"), new Text("01/01/14;06:08;4.9;mg/dL;Blood urea nitrogen;Alive;"));

     reduceDriver.runTest();
	}
	@Test
	public void MultiObservationsIdRecord() throws IOException {
     ArrayList<Text> list = new ArrayList<Text>(4);
     list.add(new Text("Dead"));
     list.add(new Text("01/01/14;06:08;4.9;"));
     ArrayList<Text>list1=new ArrayList<Text>(4);
     list1.add(new Text("Alive"));
	 list1.add(new Text("01/01/14;06:08;5.12;"));
	 reduceDriver.withInput(new Text("Creatinine~138312"), list);
	 reduceDriver.withInput(new Text("BUN~138312"), list1);
	 reduceDriver.withOutput(new Text("Creatinine~138312"), new Text("01/01/14;06:08;4.9;mg/dL;Serum creatinine;Dead;"));
	 reduceDriver.withOutput(new Text("BUN~138312"), new Text("01/01/14;06:08;5.12;mg/dL;Blood urea nitrogen;Alive;"));
	 reduceDriver.runTest();
	}
}
