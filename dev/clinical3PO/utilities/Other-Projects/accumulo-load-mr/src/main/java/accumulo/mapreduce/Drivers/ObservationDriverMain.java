package accumulo.mapreduce.Drivers;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;


public class ObservationDriverMain extends Configured implements Tool {

	final static Logger logger = Logger.getLogger(ObservationDriverMain.class);

	public static void main(String[] args) throws Exception {

		logger.info(" ACCUMULO FOR OBSERVATION STARTED WITH MAPREDUCE  ");

		int err = ToolRunner.run(new ObservationDriverMain(), args);
		System.exit(err);
	}

	@Override
	public int run(String[] args) throws Exception {

		if(args.length != 10){
			logger.info("INPUT PARAMS SHOULD BE 10. PLEASE RECHECK.");
			logger.info("EXITING PROGRAM");
			System.exit(0);
		}
		final String inputFile = args[0];
		final String accumuloTableName = args[1];
		final String accumuloInstance = args[2];
		final String zooKeepers = args[3];
		final String accumuloUser = args[4];
		final String accumuloPassword = args[5];
		final String accumuloTableName1 = args[6];
		final String accumuloTableName2 = args[7];
		final long minInputSplitSize = Long.parseLong(args[8]);
		final String outputFile = args[9];

		Configuration conf = new Configuration();

		conf.set("inputFile", inputFile);
		conf.set("accumuloTableName", accumuloTableName);
		conf.set("accumuloTableName1", accumuloTableName1);
		conf.set("accumuloTableName2", accumuloTableName2);
		conf.set("accumuloInstance", accumuloInstance);
		conf.set("zooKeepers", zooKeepers);
		conf.set("accumuloUser", accumuloUser);
		conf.set("accumuloPassword", accumuloPassword);

		Job job = Job.getInstance(conf, "accumulo-observation-mr");
		job.setJarByClass(ObservationDriverMain.class);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		job.setSpeculativeExecution(false);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		FileInputFormat.setMinInputSplitSize(job, minInputSplitSize);//988888888);
		FileInputFormat.addInputPath(job, new Path(inputFile));
		FileOutputFormat.setOutputPath(job, new Path(outputFile));

		job.setMapperClass(ObservationMapperMainDao.class);

		job.setNumReduceTasks(0);

		return job.waitForCompletion(true) ? 0 : 1;
	}
}