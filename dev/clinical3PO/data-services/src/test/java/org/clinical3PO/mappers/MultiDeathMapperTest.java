package org.clinical3PO.mappers;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.clinical3PO.hadoop.mappers.MultiDeathMapper;
import org.junit.Before;
import org.junit.Test;

public class MultiDeathMapperTest {
	private MapDriver<LongWritable, Text, Text, Text> mapDriver;
	@Before
	public void setup(){
		MultiDeathMapper multiDeathMapper = new MultiDeathMapper();
		mapDriver = new MapDriver<LongWritable, Text, Text, Text>();
		Configuration conf=mapDriver.getConfiguration();
		
		conf.set("inputParam","Creatinine~138312,138539~ff0000#");
		conf.set("observationDetails", "Creatinine~8~Serum creatinine~mg/dL#");

		mapDriver.setMapper(multiDeathMapper);	

	}
	@Test
	public void DeathMapperRecord() throws IOException {
		String input = "DEATH	138312	2013-01-06	43	NULL	NULL";
		mapDriver.withInput(new LongWritable(1234567), new Text(input));
		input="NO DEATH	138539	2013-01-06	43	NULL	NULL";
		mapDriver.withOutput(new Text("Creatinine~138312"), new Text("Death"));
		mapDriver.withOutput(new Text("Creatinine~138539"), new Text("Alive"));
		mapDriver.runTest();

	}
}