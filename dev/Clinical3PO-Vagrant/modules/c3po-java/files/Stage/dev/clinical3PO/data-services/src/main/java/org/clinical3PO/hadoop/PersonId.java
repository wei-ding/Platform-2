package org.clinical3PO.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.clinical3PO.environment.AppUtil;
import org.clinical3PO.hadoop.mappers.PersonIdMapper;
import org.clinical3PO.hadoop.reducers.PersonIdReducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersonId extends Configured implements Tool {

	private static final Logger logger = LoggerFactory.getLogger(PersonId.class);

	@Override
	public int run(String[] args) throws Exception {
		
		if (args.length != 4) {
			System.err.println("Usage: PersonId <input path> <output path> <category txt> <id>");
			return(-1);
		}
		Configuration conf = getConf();

		conf.set("inputFile", args[0]);
		conf.set("personId", args[3]);
		conf.set("envType",AppUtil.getProperty("environment.type"));
		
		FileSystem fs = FileSystem.get(conf);

		String personId = args[3];
		String conceptFileInHadoop = args[2];

		Path conceptFile = new Path(conceptFileInHadoop);

		if (!fs.exists(conceptFile)) {
			logger.error (conceptFileInHadoop + " is not found in HDFS");
			return(-1);
		}

		if (!fs.isFile(conceptFile)) {
			logger.error (conceptFileInHadoop + " is directory, not a file");
			return(-1);
		}

		conf.set("conceptFile", conceptFileInHadoop);

		Job job = Job.getInstance(conf, "Person Id Search - " + personId.trim());
		job.setJarByClass(PersonId.class);
		job.setJobName("Person Id " + personId.trim() + " Search");

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		TextInputFormat.addInputPath(job, new Path(args[0]));
		TextOutputFormat.setOutputPath(job, new Path(args[1]));

		job.setMapperClass(PersonIdMapper.class);
		job.setReducerClass(PersonIdReducer.class);

		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(Text.class);

		job.setNumReduceTasks(1);

		return(job.waitForCompletion(true) ? 0 : 1);
	}

	public static void main(String[] args) throws Exception {

		int exitCode = ToolRunner.run(new PersonId(), args);
		System.exit(exitCode);
	}
}