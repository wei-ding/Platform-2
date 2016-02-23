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

public class ConceptDriverMain extends Configured implements Tool {

	final static Logger logger = Logger.getLogger(ConceptDriverMain.class);

	public static void main(String[] args) throws Exception {

		logger.info("  ACCUMULO FOR CONCEPT STARTED WITH MAPREDUCE  ");
		System.exit(ToolRunner.run(new ConceptDriverMain(), args));
	}

	@Override
	public int run(String[] args) throws Exception {

		if(args.length != 7){
			logger.info("INPUT PARAMS SHOULD BE 7. PLEASE RECHECK.");
			logger.info("EXITING PROGRAM");
			System.exit(0);
		}
		final String inputFile = args[0];
		final String accumuloTableName = args[1];
		final String accumuloInstance = args[2];
		final String zooKeepers = args[3];
		final String accumuloUser = args[4];
		final String accumuloPassword = args[5];
		final String outputFile = args[6];

		Configuration conf = new Configuration();

		conf.set("inputFile", inputFile);
		conf.set("accumuloTableName", accumuloTableName);
		conf.set("accumuloInstance", accumuloInstance);
		conf.set("zooKeepers", zooKeepers);
		conf.set("accumuloUser", accumuloUser);
		conf.set("accumuloPassword", accumuloPassword);

		Job job = Job.getInstance(conf, "Accumulo-Concept-DbInsert");
		job.setJarByClass(ConceptDriverMain.class);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		job.setSpeculativeExecution(false);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		FileInputFormat.addInputPath(job, new Path(inputFile));
		FileOutputFormat.setOutputPath(job, new Path(outputFile));

		job.setMapperClass(ConceptMapperMainDao.class);

		job.setNumReduceTasks(0);

		return job.waitForCompletion(true) ? 0 : 1;
	}
}