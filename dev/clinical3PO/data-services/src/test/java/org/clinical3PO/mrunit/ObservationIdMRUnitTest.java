package org.clinical3PO.mrunit;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MultipleInputsMapReduceDriver;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.clinical3PO.hadoop.ObservationId.GroupComparator;
import org.clinical3PO.hadoop.mappers.DeathMapper;
import org.clinical3PO.hadoop.mappers.ObservationIdMapper;
import org.clinical3PO.hadoop.reducers.ObservationIdReducer;
import org.junit.Test;

public class ObservationIdMRUnitTest {
	
	private MapDriver<LongWritable, Text, Text, Text> mapDriver;
	private ReduceDriver<Text, Text, Text, Text> reduceDriver;
	private MultipleInputsMapReduceDriver<Text,Text,Text,Text> multiMapReduceDriver;
  
	@Test
	public void mapReduceTest() throws Exception {
		
		DeathMapper deathMapper=new DeathMapper();;
		mapDriver=new MapDriver<LongWritable, Text, Text, Text>();
		ObservationIdMapper observationIdMapper = new ObservationIdMapper();
		mapDriver = new MapDriver<LongWritable, Text, Text, Text>();
		ObservationIdReducer observationIdReducer = new ObservationIdReducer();
		reduceDriver = new ReduceDriver<Text, Text, Text, Text>();		
		multiMapReduceDriver=new MultipleInputsMapReduceDriver<Text,Text,Text,Text>();
		
		GroupComparator c=new GroupComparator();
        Configuration conf1= multiMapReduceDriver.getConfiguration();
        conf1.set("personId","138312,138539");
        conf1.set("person_id","138312");
		conf1.set("mapreduce.jobtracker.address", "local");
		// This file is used to get the full descriptions of the attributes that are measured
		
		conf1.set("observationId", "16");
		conf1.set("observationIdName", "Serum potassium");
		conf1.set("observationIdUnit","mEq/L");	
		conf1.set("conceptFileInHadoop", "src/test/resource/concept.txt");
		
		mapDriver.setMapper(observationIdMapper);
		mapDriver.setMapper(deathMapper);
		reduceDriver.setReducer(observationIdReducer);  
		
		multiMapReduceDriver.addMapper(observationIdMapper);
		multiMapReduceDriver.addMapper(deathMapper);
		multiMapReduceDriver.setReducer(observationIdReducer);
	    
		multiMapReduceDriver.setKeyGroupingComparator(c);

		String input1="DEATH	138312	2014-01-06	43	NULL	NULL";
		String input2 = "OBSERVATION;2676118;138312;16;01/01/14;06:08;4.9;NULL;NULL;NULL;NULL;NULL;0;0;NULL;NULL;NULL;NULL";
	    
		multiMapReduceDriver.withInput(deathMapper,new LongWritable(1234567),new Text(input1));
		multiMapReduceDriver.withInput(observationIdMapper,new LongWritable(1234567),new Text(input2));
		
		input2 = "OBSERVATION;2676118;138539;16;01/01/14;06:08;4.9;NULL;NULL;NULL;NULL;NULL;0;0;NULL;NULL;NULL;NULL";
		multiMapReduceDriver.withInput(deathMapper,new LongWritable(1234567),new Text(input1));
		multiMapReduceDriver.withInput(observationIdMapper,new LongWritable(1234567),new Text(input2));
		
    	multiMapReduceDriver.withOutput(new Text("138312"), new Text("01/01/14;06:08;4.9;mEq/L;Serum potassium;Death;"));		
    	multiMapReduceDriver.withOutput(new Text("138539"), new Text("01/01/14;06:08;4.9;mEq/L;Serum potassium;Alive;"));		
		
		multiMapReduceDriver.runTest();
	}
}
