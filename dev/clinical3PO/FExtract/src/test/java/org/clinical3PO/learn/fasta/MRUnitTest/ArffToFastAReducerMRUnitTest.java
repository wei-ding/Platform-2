package org.clinical3PO.learn.fasta.MRUnitTest;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.clinical3PO.learn.fasta.ArffToFastAReducer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ArffToFastAReducer.class)
public class ArffToFastAReducerMRUnitTest {
	private ReduceDriver<Text,Text,Text, NullWritable> reduceDriver;
	private ArffToFastAReducer arfftofastareducer;
	
	@Before
	public void setUp() throws IOException{
		
		arfftofastareducer = new ArffToFastAReducer();
		reduceDriver = new ReduceDriver<Text, Text,Text, NullWritable>();
		
		//reduceDriver = ReduceDriver.newReduceDriver(new ArffToFastAReducer());
		Configuration conf = reduceDriver.getConfiguration();
		conf.set("fs.defaultFS", "file:///");
		conf.set("mapreduce.jobtracker.address", "local");
		conf.set("pidFlag","true");
		conf.set("propertiesMap", "{\"diasabp\":{\"min\":\"10\",\"max\":\"100\",\"descritize_out\":\"F\",\"descritize_in\":\"E\"},\"creatinine\":{\"min\":\"0.7\",\"max\":\"1.3\",\"descritize_out\":\"B\",\"descritize_in\":\"A\"},\"paco2\":{\"min\":\"38\",\"max\":\"42\",\"descritize_out\":\"J\",\"descritize_in\":\"I\"},\"nisysabp\":{\"min\":\"10\",\"max\":\"100\",\"descritize_out\":\"L\",\"descritize_in\":\"K\"},\"cholesterol\":{\"min\":\"4\",\"max\":\"5\",\"descritize_out\":\"H\",\"descritize_in\":\"G\"},\"mg\":{\"min\":\"1.5\",\"max\":\"2.0\",\"descritize_out\":\"D\",\"descritize_in\":\"C\"}}}");
		conf.set("relation", "diasabp");
		conf.set("properties","{\"pidFlag\":true,\"propertiesMap\":{\"diasabp\":{\"min\":\"10\",\"max\":\"100\",\"descritize_out\":\"F\",\"descritize_in\":\"E\"},\"creatinine\":{\"min\":\"0.7\",\"max\":\"1.3\",\"descritize_out\":\"B\",\"descritize_in\":\"A\"},\"paco2\":{\"min\":\"38\",\"max\":\"42\",\"descritize_out\":\"J\",\"descritize_in\":\"I\"},\"nisysabp\":{\"min\":\"10\",\"max\":\"100\",\"descritize_out\":\"L\",\"descritize_in\":\"K\"},\"cholesterol\":{\"min\":\"4\",\"max\":\"5\",\"descritize_out\":\"H\",\"descritize_in\":\"G\"},\"mg\":{\"min\":\"1.5\",\"max\":\"2.0\",\"descritize_out\":\"D\",\"descritize_in\":\"C\"}}}");
									    
		reduceDriver.setReducer(arfftofastareducer);
	}
	
	@Test
	public void reducerMRUnit() throws IOException,InterruptedException{
		// ReduceDriver reduceDriver = new ReduceDriver(new ArffToFastAReducer());
		String output=">pid 132662 |class diasabp |attribute diasabp_fbin_ebasmean"+
"\nNEEEEEEEEEEEEEEEEY";
		ArrayList<Text> list = new ArrayList<Text>(1);
		list.add(new Text("132662,?,65.0,65.5,56.0,65.0,63.0,51.0,55.0,52.0,52.0,54.0,53.0,46.0,44.0,45.0,43.0,44.0,verylow"));
	
		reduceDriver.withInput(new Text("diasabp_fbin_ebasmean"),list);
		reduceDriver.withPathOutput(new Text(output),NullWritable.get(),"diasabp-fbin-ebasmean");		
		reduceDriver.runTest();
	}
	@Test
	public void MultireducerMRUnit() throws IOException,InterruptedException{
		// ReduceDriver reduceDriver = new ReduceDriver(new ArffToFastAReducer());
		String output=">pid 132662 |class diasabp |attribute diasabp_fbin_ebasmean"+
"\nNEEEEEEEEEEEEEEEEY";
		String output2=">pid 132662 |class diasabp |attribute diasabp_fbin_ebasmean"+
"\nNNNEEEEEEEEEEEEENZ";
		ArrayList<Text> list = new ArrayList<Text>(2);
		list.add(new Text("132662,?,65.0,65.5,56.0,65.0,63.0,51.0,55.0,52.0,52.0,54.0,53.0,46.0,44.0,45.0,43.0,44.0,verylow"));
		list.add(new Text("132662,?,?,?,86.25,77.75,76.66666666666667,53.666666666666664,77.5,76.5,74.5,70.5,74.0,63.0,54.0,53.5,53.75,?,low"));
	
		reduceDriver.withInput(new Text("diasabp_fbin_ebasmean"),list);
		reduceDriver.withPathOutput(new Text(output),NullWritable.get(),"diasabp-fbin-ebasmean");	
		reduceDriver.withPathOutput(new Text(output2),NullWritable.get(),"diasabp-fbin-ebasmean");	

		reduceDriver.runTest();
	}

	
}

