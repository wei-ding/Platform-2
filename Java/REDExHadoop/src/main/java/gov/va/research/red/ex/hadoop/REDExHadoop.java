/*
 *  Copyright 2015 United States Department of Veterans Affairs,
 *		Health Services Research & Development Service
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 */
package gov.va.research.red.ex.hadoop;

import java.util.Arrays;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

/**
 * @author Doug Redd <doug_redd@gwu.edu>
 * Executes Hadoop Map-Reduce jobs to apply a REDEx model to clinical notes.
 * Input files must have a key on the first line and the text of the clinical note following,
 * one clinical note per file. The key is a pipe delimited set of patient ID, document ID,
 * and document date/time in ISO 8601 format, i.e. <patient ID>|<document ID>|<date/time>
 */
public class REDExHadoop extends Configured implements Tool {
	
	private static final Logger LOG = Logger.getLogger(REDExHadoop.class);

	public static void main(String[] args) throws Exception {
		int result = ToolRunner.run(new Configuration(), new REDExHadoop(), args);
		System.exit(result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.hadoop.util.Tool#run(java.lang.String[])
	 */
	@Override
	public int run(String[] args) throws Exception {
		System.err.println("> args = " + Arrays.asList(args));
		if (args.length != 3) {
			LOG.info("Usage: REDExHadoop <redex-model-file> <input file directory> <output file directory> [hadoop options]");
			System.exit(2);
		}
		getConf().set("redex.model.file", args[0]);
		getConf().set("annotation.type", args[1]);
		Job job = Job.getInstance(getConf(), "REDExHadoop");
		job.setJarByClass(REDExHadoop.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(MatchedElementWritable.class);

		job.setInputFormatClass(WholeFileInputFormat.class);

		job.setMapperClass(REDExMapper.class);
		job.setReducerClass(BioCReducer.class);

		FileInputFormat.addInputPath(job, new Path(args[2]));
		FileOutputFormat.setOutputPath(job, new Path(args[3]));

		return job.waitForCompletion(true) ? 0 : 1;
	}

}
