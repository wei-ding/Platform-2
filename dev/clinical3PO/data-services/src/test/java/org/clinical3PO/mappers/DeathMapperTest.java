package org.clinical3PO.mappers;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.clinical3PO.hadoop.mappers.DeathMapper;
import org.junit.Before;
import org.junit.Test;

public class DeathMapperTest {
	private MapDriver<LongWritable, Text, Text, Text> mapDriver;
	@Before
	public void setup(){
	    DeathMapper deathMapper = new DeathMapper();
		mapDriver = new MapDriver<LongWritable, Text, Text, Text>();
		Configuration conf=mapDriver.getConfiguration();
		//conf.set("person_id", "138312");
		conf.set("personId", "138312,138539");
		//conf.set("personIdArr[]", "138312,138539,1234567");
		//conf.set("personIds", "138312,DEATH");
		//conf.set("personIdArr", "138312,138539,1234567");
		mapDriver.setMapper(deathMapper);	
		
	}
	@Test
	public void DeathMapperRecord() throws IOException {
		String input = "DEATH	138312	2013-01-06	43	NULL	NULL";
		mapDriver.withInput(new LongWritable(1234567), new Text(input));
		input="NO DEATH	138539	2013-01-06	43	NULL	NULL";
		
		mapDriver.withOutput(new Text("138539~0"), new Text("Alive"));
		mapDriver.withOutput(new Text("138312~0"), new Text("Death"));		
		mapDriver.runTest();
		
	}

}
