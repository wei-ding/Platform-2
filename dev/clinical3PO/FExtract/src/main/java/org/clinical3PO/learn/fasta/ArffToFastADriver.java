package org.clinical3PO.learn.fasta;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
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

	public int run(String[] args) throws Exception {

		Configuration conf = this.getConf();

		/*
		 * Helps to read and separate arguments from to program.
		 * Read Hadoop arguments(-D) into conf, other arguments into string[].
		 */
		@SuppressWarnings("unused")
		String[] otherParams = new GenericOptionsParser(conf, args).getRemainingArgs();

		/*
		 * Reading the input configuration files(which decides on what features data is to be fetched and on what parameters)
		 * Code to read the Files from Local File System.
		 * Below are the two lines of code to read files from localFS (file:///) instead of hdfs (hdfs:///) 
		 */
		Path propertiesFile = new Path(conf.get("c3fe.feconfig")); 	// Creating object for file path
		FileSystem localFS = FileSystem.get(propertiesFile.toUri(), conf);		// Initializing FileSystem to get the connection
		if(!localFS.exists(propertiesFile)) {
			System.err.println("Path doesn't exists: "+ propertiesFile.getName() + " \nCheck the path properly.");
			System.exit(1);
		}

		/*
		 * Reading properties from the basicFEConfig.txt file.
		 */
		// method call
		String configFileAsString = readFile(localFS.open(propertiesFile));
		conf.set("configFileAsString", configFileAsString);

		Path input = new Path(conf.get("c3fe.inputdir"));
		FileSystem fs = FileSystem.get(conf);

		if(!fs.exists(input)) {
			System.exit(0);
		}

		//Object creation & method call
		ArffHeaderReader arffHeader = new ArffHeaderReader();
		arffHeader.readParseArffHeaders(fs.open(input));

		String patientIdBoolen = String.valueOf(arffHeader.isPidFlag());
		conf.set("patientIdBoolen", patientIdBoolen);
		
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

	/**
	 * helper function to read files
	 * from http://stackoverflow.com/questions/326390/how-to-create-a-java-string-from-the-contents-of-a-file
	 * THIS IS FOR PLAIN OLD JAVA ON FILESYSTEM, don't know if it'll work on hadoop
	 * adapting to use an input stream, should work on hadoop
	 */
	private static String readFile(InputStream is) throws IOException {

		StringBuffer fileContents = new StringBuffer();
		Scanner scanner = new Scanner(is);

		// If the line separator is changed, please do so in setup() method in FEConceptMapper.java
		String lineSeparator = System.getProperty("line.separator");
		try {
			while(scanner.hasNextLine()) {      
				fileContents.append(scanner.nextLine() + lineSeparator);
			}
		} finally {
			scanner.close();
		}
		return fileContents.toString();
	}
}