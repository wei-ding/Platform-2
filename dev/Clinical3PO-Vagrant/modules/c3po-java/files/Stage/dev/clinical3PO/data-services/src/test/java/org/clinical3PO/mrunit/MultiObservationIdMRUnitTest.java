package org.clinical3PO.mrunit;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.MultipleInputsMapReduceDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.clinical3PO.hadoop.mappers.MultiDeathMapper;
import org.clinical3PO.hadoop.mappers.MultiObservationIdMapper;
import org.clinical3PO.hadoop.reducers.MultiObservationIdReducer;
import org.junit.Test;

public class MultiObservationIdMRUnitTest {
	private MapDriver<LongWritable, Text, Text, Text> mapDriver;
	private ReduceDriver<Text, Text, Text, Text> reduceDriver;
	private MultipleInputsMapReduceDriver<Text,Text,Text,Text> multiMapReduceDriver;
	@Test
	public void mapReduceTest() throws Exception {
		MultiObservationIdMapper multiObservationIdMapper = new MultiObservationIdMapper();
		mapDriver = new MapDriver<LongWritable, Text, Text, Text>();
		MultiDeathMapper multiDeathMapper=new MultiDeathMapper();
		mapDriver =new MapDriver<LongWritable,Text,Text,Text>();
		MultiObservationIdReducer multiObservationIdReducer = new MultiObservationIdReducer();
		reduceDriver = new ReduceDriver<Text, Text, Text, Text>();
		
		multiMapReduceDriver=new MultipleInputsMapReduceDriver<Text,Text,Text,Text>();
		Configuration conf =  multiMapReduceDriver.getConfiguration();
		conf.set("observationFile", "src/test/resources/observation.txt");
		conf.set("conceptFile", "src/test/resources/concept.txt");
		conf.set("inputParam", "Creatinine~138312,138539~ff0000#k~138312,138539~ff0000#");
		conf.set("mapreduce.jobtracker.address", "local");
		conf.set("observationDetails", "Creatinine~8~Serum creatinine~mg/dL#k~16~Serum potassium~mEq/L#");
		conf.set("observation", "Creatinine,k");
		conf.set("personId","138312,138539");
		mapDriver.setMapper(multiObservationIdMapper);
		mapDriver.setMapper(multiDeathMapper);
		reduceDriver.setReducer(multiObservationIdReducer);
		
		multiMapReduceDriver.addMapper(multiObservationIdMapper);
		multiMapReduceDriver.addMapper(multiDeathMapper);
		multiMapReduceDriver.setReducer(multiObservationIdReducer);
		
		String input1="DEATH	138312	2014-01-06	43	NULL	NULL";
		String input2="OBSERVATION;2676118;138312;8;01/01/14;06:08;4.9;NULL;NULL;NULL;NULL;NULL;0;0;NULL;NULL;NULL;NULL";
	    
		multiMapReduceDriver.withInput(multiDeathMapper,new LongWritable(1234567),new Text(input1));
		multiMapReduceDriver.withInput(multiObservationIdMapper,new LongWritable(1234567),new Text(input2));
		input2="OBSERVATION;2676118;138312;16;01/01/14;06:08;4.9;NULL;NULL;NULL;NULL;NULL;0;0;NULL;NULL;NULL;NULL";
		multiMapReduceDriver.withInput(multiObservationIdMapper,new LongWritable(1234567),new Text(input2));

		//input1="No DEATH	138539	2014-01-06	43	NULL	NULL";
		input2 = "OBSERVATION;2676118;138539;8;01/01/14;06:08;4.9;NULL;NULL;NULL;NULL;NULL;0;0;NULL;NULL;NULL;NULL";
		//multiMapReduceDriver.withInput(multiDeathMapper,new LongWritable(1234567),new Text(input1));
		multiMapReduceDriver.withInput(multiObservationIdMapper,new LongWritable(1234567),new Text(input2));
		input2 = "OBSERVATION;2676118;138539;16;01/01/14;06:08;4.9;NULL;NULL;NULL;NULL;NULL;0;0;NULL;NULL;NULL;NULL";
		multiMapReduceDriver.withInput(multiObservationIdMapper,new LongWritable(1234567),new Text(input2));
    	multiMapReduceDriver.withOutput(new Text("Creatinine~138312"), new Text("01/01/14;06:08;4.9;mg/dL;Serum creatinine;Death;"));
       	multiMapReduceDriver.withOutput(new Text("Creatinine~138539"), new Text("01/01/14;06:08;4.9;mg/dL;Serum creatinine;Alive;"));		
    	multiMapReduceDriver.withOutput(new Text("k~138312"), new Text("01/01/14;06:08;4.9;mEq/L;Serum potassium;Death;"));
       	multiMapReduceDriver.withOutput(new Text("k~138539"), new Text("01/01/14;06:08;4.9;mEq/L;Serum potassium;Alive;"));		
    	multiMapReduceDriver.runTest();
	}

}
