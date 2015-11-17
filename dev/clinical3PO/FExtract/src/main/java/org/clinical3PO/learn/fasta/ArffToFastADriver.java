package org.clinical3PO.learn.fasta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FSDataInputStream;
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

	private String relation = null;
	private int bins = 0;
	private Set<String> attributeSet = null;
	private boolean pidFlag = false;
	
	public ArffToFastADriver() {

		attributeSet = new LinkedHashSet<String>();
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
		
		//method call
		readParseArffHeaders(fs.open(input));
		
		/*
		 * The following class parses properties file(xml format) located in local FS.
		 * After parse get the object and pass it on to Gson API. 
		 * 
		 * Setting pidFlag(personID flag) so that mapper and reducer and react accordingly.
		 */
		ArffToFastAProperties object = new ArffToFastAProperties(pidFlag);
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

		StringBuilder sb = new StringBuilder();
		System.out.println("----------------------------------------------");
		System.out.println(attributeSet);
		System.out.println("----------------------------------------------");
		
		// for mapper and reducer to access the order of attributes from arff file, construct a string with comma separator and add to conf.
		for(String attribute : attributeSet) {
			sb.append(attribute).append(",");
		}

		conf.set("relation", relation);
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
	 * Reading(from hdfs) Arff file header to get count of unique number of attributes and relation type.
	 * While there's an information available(separated by '_') in each attribute, split accordingly and 
	 * 	store the in linked list.   
	 * @param fsds
	 * @throws IOException
	 */
	private void readParseArffHeaders(FSDataInputStream fsds) throws IOException {

		// Initializing reader to read arff file.
		BufferedReader br = new BufferedReader(new InputStreamReader(fsds));
		String arffHeader = null;
		StringBuilder sb = null;
		try {

			sb = new StringBuilder();
			arffHeader = br.readLine();	//line-by-line

			// Loop over the line for not null && not empty
			while(arffHeader != null) {

				if(!arffHeader.isEmpty()) {

					/*
					 * There are 3 string in each @attribute line.
					 * 1) @attribute itself
					 * 2) unique attribute string
					 * 3) type of unique attribute
					 * NOTE: we are not using 3rd string/value (type of unique attribute)
					 */
					String[] inputLine= arffHeader.split("\\s+");

					// very 1st line of arff file.
					if(inputLine[0].equals("@relation")) {
						relation = inputLine[1];
					} else if(inputLine[0].equals("@attribute")) {

						/*
						 * All the @attribute String values contain information(separated by '_') expect PatientID & class attributes.
						 * if helps to avoid PartientID and class attributes from insertion into set.
						 * 
						 * Only insert the unique name of the attribute instead of full name.
						 * REASON:
						 * n-unique attributes = n-fasta files with all patients of unique attributes.
						 * Program to process above statement, have to remember the order of attributes in arff header.
						 * Using LinkedSet to maintain the order. 
						 */
						String[] attribute = inputLine[1].split("_");
						if(attribute.length == 1 && attribute[0].equalsIgnoreCase("PatientID")) {
							pidFlag = true;
						} else if(!attribute[0].equals("class")) {

							sb.append(attribute[0]).append("_").append(attribute[1]).append("_").append(attribute[2]);
							if(!attributeSet.contains(sb.toString())) {
								attributeSet.add(sb.toString());
							}
						}
						sb.delete(0, sb.length());
					} else if(inputLine[0].equals("@data")) {	// hereafter arff data starts and header ends, so set 'conf' and break.
						break;
					} else {
						continue;
					}
				}
				arffHeader = br.readLine();		//read next line
			}
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			br.close();
		}
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