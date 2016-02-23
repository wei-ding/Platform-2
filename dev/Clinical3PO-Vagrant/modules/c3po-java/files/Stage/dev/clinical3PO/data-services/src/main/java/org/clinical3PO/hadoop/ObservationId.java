package org.clinical3PO.hadoop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.clinical3PO.environment.AppUtil;
import org.clinical3PO.environment.EnvironmentType;
import org.clinical3PO.hadoop.mappers.DeathMapper;
import org.clinical3PO.hadoop.mappers.ObservationIdMapper;
import org.clinical3PO.hadoop.reducers.ObservationIdReducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObservationId extends Configured implements Tool {

	private static final Logger logger = LoggerFactory.getLogger(ObservationId.class);

	public static class KeyPartitioner extends Partitioner<Text, Text> {

		@Override
		public int getPartition(Text key, Text value, int numPartitions) {
			String[] keyArr = key.toString().split("~");
			return (keyArr[0].hashCode() & Integer.MAX_VALUE) % numPartitions;
		}
	}

	public static class GroupComparator extends WritableComparator {

		public GroupComparator() {
			super(Text.class, true);
		}

		@Override
		public int compare(WritableComparable w1, WritableComparable w2) {
			Text t1 = (Text) w1;
			Text t2 = (Text) w2;
			String[] key1Arr = t1.toString().split("~");
			String[] key2Arr = t2.toString().split("~");
			return(key1Arr[0].compareTo(key2Arr[0]));
		}
	}

	@Override
	public int run(String[] args) throws Exception {
		if (args.length != 6) {
			System.err.println("Usage: ObservationId <input path> <output path> <concept file> <patient id> <observation id> <death file>");
			return(-1);
		}

		String personId = args[3];
		String observationId = args[4];
		String conceptFileInHadoop = args[2];
		
		Configuration conf =  getConf();
		conf.set("personId", personId);

		String envType = AppUtil.getProperty("environment.type");
		conf.set("envType",envType);

		FileSystem fs = FileSystem.get(conf);

		Path conceptFile = new Path(conceptFileInHadoop);

		if (!fs.exists(conceptFile)) {
			logger.error (conceptFileInHadoop + " is not found in HDFS");
			return(-1);
		}
		if (!fs.isFile(conceptFile)) {
			logger.error (conceptFileInHadoop + " is directory, not a file");
			return(-1);
		}

		String line;
		String[] conceptIdArr = null;
		boolean observationIdFound = false;
		BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(new Path(conceptFileInHadoop))));
		try {
			line = br.readLine();
			while (line != null) {
				conceptIdArr = line.split(";");
				final String conceptId = conceptIdArr[0].trim();
				final String propertyName = conceptIdArr[1].trim();
				String valueUnits = conceptIdArr[4].trim();

				if(envType !=null && EnvironmentType.DEVELOPMENT == EnvironmentType.valueOf(envType)) {
					logger.info ("Size of array : " + conceptIdArr.length);
					logger.info ("concept_id : " + conceptId);
					logger.info ("property_name : " + propertyName);
					logger.info ("value_units : " + valueUnits);
				}

				if (!conceptId.equals("src_concept_id")) { // skipping 1st line
					if (propertyName.equals(observationId)) {
						if (valueUnits.length() == 0)
							valueUnits = "No Unit";
						conf.set("observationId", conceptId);
						conf.set("observationIdName", conceptIdArr[2]);
						conf.set("observationIdUnit", valueUnits);
						observationIdFound = true;
						break;
					}
				}

				// read the next line
				line = br.readLine();
			}
		} catch (IOException e) {
			logger.error ("Error while reading concept file");
		} finally {
			br.close();
		}

		if (!observationIdFound) { // Input Observation Id is not valid
			System.err.println("Given Observation Id " +  observationId + " is not found in concept.txt file");
			return(-1);
		}

		Job job = Job.getInstance(conf, "Observation Id Search + " + observationId.trim() + " for " + personId.trim());
		job.setJarByClass(ObservationId.class);
		job.setJobName("Observation Id " + observationId.trim() + " Search");

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		MultipleInputs.addInputPath(job, new Path(args[0]),	TextInputFormat.class, ObservationIdMapper.class);
		MultipleInputs.addInputPath(job, new Path(args[5]),	TextInputFormat.class, DeathMapper.class);
		TextOutputFormat.setOutputPath(job, new Path(args[1]));

		job.setReducerClass(ObservationIdReducer.class);
		job.setPartitionerClass(KeyPartitioner.class);
		job.setGroupingComparatorClass(GroupComparator.class);

		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		job.setNumReduceTasks(1);

		return(job.waitForCompletion(true) ? 0 : 1);
	}

	public static void main(String[] args) throws Exception {

		int exitCode = ToolRunner.run(new ObservationId(), args);
		System.exit(exitCode);
	}
}