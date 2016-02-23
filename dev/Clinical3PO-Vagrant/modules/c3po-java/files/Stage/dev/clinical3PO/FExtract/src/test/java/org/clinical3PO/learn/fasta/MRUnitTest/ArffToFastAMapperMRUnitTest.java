package org.clinical3PO.learn.fasta.MRUnitTest;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.clinical3PO.learn.fasta.ArffToFastAMapper;
import org.junit.Before;
import org.junit.Test;

public class ArffToFastAMapperMRUnitTest {
	
		private MapDriver<LongWritable, Text, Text, Text> mapDriver;
		
		@Before
		public void setUp() {

			// Setup the mapper
			ArffToFastAMapper arfftofastamapper = new ArffToFastAMapper();
			mapDriver = new MapDriver<LongWritable, Text, Text, Text>();
			Configuration conf = mapDriver.getConfiguration();
			conf.set("attribute","diasabp_fbin_ebasmean");
			conf.set("properties","{\"pidFlag\":false,\"propertiesMap\":{\"diasabp\":{\"min\":\"10\",\"max\":\"100\",\"descritize_out\":\"F\",\"descritize_in\":\"E\"},\"creatinine\":{\"min\":\"0.7\",\"max\":\"1.3\",\"descritize_out\":\"B\",\"descritize_in\":\"A\"},\"paco2\":{\"min\":\"38\",\"max\":\"42\",\"descritize_out\":\"J\",\"descritize_in\":\"I\"},\"nisysabp\":{\"min\":\"10\",\"max\":\"100\",\"descritize_out\":\"L\",\"descritize_in\":\"K\"},\"cholesterol\":{\"min\":\"4\",\"max\":\"5\",\"descritize_out\":\"H\",\"descritize_in\":\"G\"},\"mg\":{\"min\":\"1.5\",\"max\":\"2.0\",\"descritize_out\":\"D\",\"descritize_in\":\"C\"}}}");

			mapDriver.setMapper(arfftofastamapper);

		}
		@Test
		public void mapperFasta() throws IOException {
			String input="132662,?,65.0,65.5,56.0,65.0,63.0,51.0,55.0,52.0,52.0,54.0,53.0,46.0,44.0,45.0,43.0,44.0,verylow";
			Configuration conf = mapDriver.getConfiguration();
			conf.set("attribute","diasabp_fbin_ebasmean");
			conf.set("properties","{\"pidFlag\":true,\"propertiesMap\":{\"diasabp\":{\"min\":\"10\",\"max\":\"100\",\"descritize_out\":\"F\",\"descritize_in\":\"E\"},\"creatinine\":{\"min\":\"0.7\",\"max\":\"1.3\",\"descritize_out\":\"B\",\"descritize_in\":\"A\"},\"paco2\":{\"min\":\"38\",\"max\":\"42\",\"descritize_out\":\"J\",\"descritize_in\":\"I\"},\"nisysabp\":{\"min\":\"10\",\"max\":\"100\",\"descritize_out\":\"L\",\"descritize_in\":\"K\"},\"cholesterol\":{\"min\":\"4\",\"max\":\"5\",\"descritize_out\":\"H\",\"descritize_in\":\"G\"},\"mg\":{\"min\":\"1.5\",\"max\":\"2.0\",\"descritize_out\":\"D\",\"descritize_in\":\"C\"}}}");
		
			mapDriver.withInput(new LongWritable(), new Text(input));
			mapDriver.withOutput(new Text("diasabp_fbin_ebasmean"), new Text("132662,?,65.0,65.5,56.0,65.0,63.0,51.0,55.0,52.0,52.0,54.0,53.0,46.0,44.0,45.0,43.0,44.0,verylow"));
			//System.out.println(mapDriver.getExpectedOutputs());
			mapDriver.runTest();
		}
		@Test
		public void MultimapperFasta() throws IOException {
			String input1="132662,?,65.0,65.5,56.0,65.0,63.0,51.0,55.0,52.0,52.0,54.0,53.0,46.0,44.0,45.0,43.0,44.0,verylow";
			String input2="132893,?,?,?,86.25,77.75,76.66666666666667,53.666666666666664,77.5,76.5,74.5,70.5,74.0,63.0,54.0,53.5,53.75,?,low";
			Configuration conf = mapDriver.getConfiguration();
			conf.set("attribute","diasabp_fbin_ebasmean");
			conf.set("properties","{\"pidFlag\":true,\"propertiesMap\":{\"diasabp\":{\"min\":\"10\",\"max\":\"100\",\"descritize_out\":\"F\",\"descritize_in\":\"E\"},\"creatinine\":{\"min\":\"0.7\",\"max\":\"1.3\",\"descritize_out\":\"B\",\"descritize_in\":\"A\"},\"paco2\":{\"min\":\"38\",\"max\":\"42\",\"descritize_out\":\"J\",\"descritize_in\":\"I\"},\"nisysabp\":{\"min\":\"10\",\"max\":\"100\",\"descritize_out\":\"L\",\"descritize_in\":\"K\"},\"cholesterol\":{\"min\":\"4\",\"max\":\"5\",\"descritize_out\":\"H\",\"descritize_in\":\"G\"},\"mg\":{\"min\":\"1.5\",\"max\":\"2.0\",\"descritize_out\":\"D\",\"descritize_in\":\"C\"}}}");
			String output1="132662,?,65.0,65.5,56.0,65.0,63.0,51.0,55.0,52.0,52.0,54.0,53.0,46.0,44.0,45.0,43.0,44.0,verylow";
			String output2="132893,?,?,?,86.25,77.75,76.66666666666667,53.666666666666664,77.5,76.5,74.5,70.5,74.0,63.0,54.0,53.5,53.75,?,low";

			mapDriver.withInput(new LongWritable(), new Text(input1));
			mapDriver.withOutput(new Text("diasabp_fbin_ebasmean"), new Text(output1));
			mapDriver.withInput(new LongWritable(), new Text(input2));
			mapDriver.withOutput(new Text("diasabp_fbin_ebasmean"), new Text(output2));
			//System.out.println(mapDriver.getExpectedOutputs());
			mapDriver.runTest();
		}
		@Test
		public void multi1mapperFasta() throws IOException {
			String input="132662,?,65.0,65.5,56.0,65.0,63.0,51.0,55.0,52.0,52.0,54.0,53.0,46.0,44.0,45.0,43.0,44.0,?,?,?,86.25,77.75,76.66666666666667,53.666666666666664,77.5,76.5,74.5,70.5,74.0,63.0,54.0,53.5,53.75,?,verylow";
			Configuration conf = mapDriver.getConfiguration();
			conf.set("attribute","diasabp_fbin_ebasmean,nisysabp_fbin_ebasmean");
			conf.set("properties","{\"pidFlag\":true,\"propertiesMap\":{\"diasabp\":{\"min\":\"10\",\"max\":\"100\",\"descritize_out\":\"F\",\"descritize_in\":\"E\"},\"creatinine\":{\"min\":\"0.7\",\"max\":\"1.3\",\"descritize_out\":\"B\",\"descritize_in\":\"A\"},\"paco2\":{\"min\":\"38\",\"max\":\"42\",\"descritize_out\":\"J\",\"descritize_in\":\"I\"},\"nisysabp\":{\"min\":\"10\",\"max\":\"100\",\"descritize_out\":\"L\",\"descritize_in\":\"K\"},\"cholesterol\":{\"min\":\"4\",\"max\":\"5\",\"descritize_out\":\"H\",\"descritize_in\":\"G\"},\"mg\":{\"min\":\"1.5\",\"max\":\"2.0\",\"descritize_out\":\"D\",\"descritize_in\":\"C\"}}}");
		
			mapDriver.withInput(new LongWritable(), new Text(input));
			mapDriver.withOutput(new Text("diasabp_fbin_ebasmean"), new Text("132662,?,65.0,65.5,56.0,65.0,63.0,51.0,55.0,52.0,52.0,54.0,53.0,46.0,44.0,45.0,43.0,44.0,verylow"));
			mapDriver.withOutput(new Text("nisysabp_fbin_ebasmean"),new Text("132662,?,?,?,86.25,77.75,76.66666666666667,53.666666666666664,77.5,76.5,74.5,70.5,74.0,63.0,54.0,53.5,53.75,?,verylow"));

			//System.out.println(mapDriver.getExpectedOutputs());
			mapDriver.runTest();
		}
}
