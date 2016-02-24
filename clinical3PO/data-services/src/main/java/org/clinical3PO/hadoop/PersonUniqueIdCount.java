package org.clinical3PO.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.clinical3PO.environment.AppUtil;
import org.clinical3PO.hadoop.mappers.PatientUniqueCountMapper;
import org.clinical3PO.hadoop.reducers.PatientUniqueCountReducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersonUniqueIdCount extends Configured implements Tool {

	private static final Logger logger = LoggerFactory.getLogger(PersonUniqueIdCount.class);

	public int run(String[] args) throws Exception {

//		if (args.length != 2) {
//			System.err.println("Usage: PersonUniqueIdCount <input path> <output path> <id>");
//			logger.error("Usage: PersonUniqueIdCount <input path> <output path> <id>");
//			return(-1);
//		}

		Configuration conf = getConf();
//		conf.set("inputFile", args[0]);
		conf.set("envType",AppUtil.getProperty("environment.type"));

		Job job = Job.getInstance(conf, "Unique Count of Person Id Search");
		job.setJarByClass(PersonUniqueIdCount.class);
		job.setJobName("Unique Person Id Count");
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		TextInputFormat.addInputPath(job, new Path(args[0]));
		TextOutputFormat.setOutputPath(job, new Path(args[1]));

		job.setMapperClass(PatientUniqueCountMapper.class);
		job.setReducerClass(PatientUniqueCountReducer.class);

		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(NullWritable.class);
		
		job.setNumReduceTasks(1);

		return(job.waitForCompletion(true) ? 0 : 1);
	}

	public static void main(String[] args) throws Exception {

		int exitCode = ToolRunner.run(new PersonUniqueIdCount(), args);
		System.exit(exitCode);
	}
}