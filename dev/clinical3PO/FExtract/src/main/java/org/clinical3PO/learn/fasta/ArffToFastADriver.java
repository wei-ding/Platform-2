package org.clinical3PO.learn.fasta;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.LazyOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import com.google.gson.Gson;

/**
 * Main/Driver class of Hadoop.
 * 
 * @author 3129891
 *
 */
public class ArffToFastADriver extends Configured implements Tool {

	public ArffToFastADriver() {
	}
	
	public static void main(String[] args) throws Exception {

		int exit = ToolRunner.run(new Configuration(), new ArffToFastADriver(), args);
		System.exit(exit);
	}

	public int run(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

		Configuration conf = this.getConf();
		
		/*
		 * Helps to read and separate arguments from to program.
		 * Read Hadoop arguments(-D) into conf, other arguments into string[].
		 */
		String[] otherParams = new GenericOptionsParser(conf, args).getRemainingArgs();

		if(otherParams.length != 1) {
			System.out.println("Path for xml file in LOCAL FILE SYSTEM is not set. " );
			System.exit(0);
		} 
		
		//Code to read properties file from Local FS.
		Path propertiesFile = new Path(otherParams[0]);
		FileSystem localFS = FileSystem.get(propertiesFile.toUri(), conf);
		
		if(!localFS.exists(propertiesFile)) {
			System.out.println("Path for xml file in LOCAL FILE SYSTEM is WRONG");
			System.exit(0);
		}
		
		Path input = new Path(conf.get("c3fe.inputdir"));
		FileSystem fs = FileSystem.get(conf);

		if(!fs.exists(input)) {
			System.exit(0);
		}
		
		//Object creation & method call
		ArffHeaderReader arffHeader = new ArffHeaderReader();
		arffHeader.readParseArffHeaders(fs.open(input));
		
		/*
		 * The following class parses properties file(xml format) located in local FS.
		 * After parse get the object and pass it on to Gson API. 
		 * 
		 * Setting pidFlag(personID flag) so that mapper and reducer will react accordingly.
		 */
		ArffToFastAProperties object = new ArffToFastAProperties(arffHeader.isPidFlag());
		boolean flag = object.parsePropertiesFile(localFS.open(propertiesFile));
		if(!flag) {
			System.out.println("Properties file not loaded");
			System.exit(0);
		}

		/*
		 * Gson is a Java library that can be used to convert Java Objects into their JSON representation. 
		 * It can also be used to convert a JSON string to an equivalent Java object. 
		 * Gson can work with arbitrary Java objects including pre-existing objects that you do not have source-code of
		 */
		Gson gson = new Gson();
		String serializedObjet = gson.toJson(object);
		
		//setting conf with 'object-converted-json-string'
		conf.set("properties", serializedObjet);

		Set<String> attributeSet = arffHeader.getAttributeSet();
		StringBuilder sb = new StringBuilder();
		System.out.println("----------------------------------------------");
		System.out.println(attributeSet);
		System.out.println("----------------------------------------------");
		
		// for mapper and reducer to access the order of attributes from arff file, construct a string with comma separator and add to conf.
		for(String attribute : attributeSet) {
			sb.append(attribute).append(",");
		}

		conf.setInt("bins", arffHeader.getBins());
		conf.set("relation", arffHeader.getRelation());
		conf.set("attribute", sb.substring(0, sb.length()-1));
		sb.delete(0, sb.length());
		sb = null;

		Job job = Job.getInstance(conf, "FASTA FILE GENERATION");

		job.setMapperClass(ArffToFastAMapper.class);
		job.setPartitionerClass(APartitioner.class);
		job.setReducerClass(ArffToFastAReducer.class);
		job.setNumReduceTasks(attributeSet.size());

		FileInputFormat.setInputPaths(job, input);
		FileOutputFormat.setOutputPath(job, new Path(conf.get("c3fe.outputdir")));

		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(NullWritable.class);
		
		/*
		 * A Convenience class that creates output lazily.
		 * Story: 
		 * MapReduce by default creates part-r-00000 files(sequence if partition is implemented) when reducer is executing.
		 * But,	in this program we are implementing MultipleOutputs in Reducer class, which means we are using
		 * 	user-defined file names instead of part-r-0000 sequence.
		 * On program execution, along with user-defined output files, MapReduce would create empty part-r-0000 files.
		 * To avoid empty files we use LazyOutputFormat class.
		 *  
		 * LazyOutputFormat.class helps "not to create part-r-0000 files unless, MapReduce writes into context.write() function". 
		 */
		LazyOutputFormat.setOutputFormatClass(job, TextOutputFormat.class);
		
		job.setJarByClass(ArffToFastADriver.class);
		return job.waitForCompletion(true) ? 0:1;
	}

	/**
	 * This is a custom partitioner.
	 * 
	 * Q:- How many times getPartition() method gets called ?
	 * A:- n-number of context.write() in mapper == n-times getPartition() method calls.
	 * 
	 * Now, the requirement to write this custom partitioner is:
	 * For n-unique keys from mapper, we have to start n-reducer's. Also, note that there could be almost same key with very minimal
	 * 	change of 1 or 2 characters (so, (key.toString.char(0) % reducers) doesn't work).
	 * With the help of Java-Map object, put all unique keys (for the 1st time) as keys and size of map as value.
	 * If the unique key is existing, retrieve the value and return.
	 *  
	 * @author 3129891
	 *
	 */
	public static class APartitioner extends Partitioner<Text, Text> {

		private Map<String, Integer> map = null;

		public APartitioner() {
			map = new HashMap<String, Integer>();
		}

		@Override
		public int getPartition(Text key, Text value, int numOfReducers) {

			String temp = key.toString();
			if(map.containsKey(temp)) {
				return map.get(temp);
			} else {
				int count = map.size();
				map.put(temp, count);
				return count;
			}			
		}
	}
}