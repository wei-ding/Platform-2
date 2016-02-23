package org.clinical3PO.hadoop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.clinical3PO.environment.AppUtil;
import org.clinical3PO.environment.EnvironmentType;
import org.clinical3PO.hadoop.mappers.MultiDeathMapper;
import org.clinical3PO.hadoop.mappers.MultiObservationIdMapper;
import org.clinical3PO.hadoop.reducers.MultiObservationIdReducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiObservationId extends Configured implements Tool {

	private String envType = null;
	private static final Logger logger = LoggerFactory.getLogger(MultiObservationId.class);

	public static void main(String[] args) throws Exception {

		int exitCode = ToolRunner.run(new MultiObservationId(), args);
		System.exit(exitCode);
	}

	public int run(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

		if (args.length != 5) {
			System.err.println("Usage: MultiObservationId <input path> <output path> <concept file> <observation id & patient ids>");
			return(-1);
		}

		final String observationFilePath = args[0];
		final String outputFilePath = args[1];
		final String conceptFileInHadoop = args[2];
		final String inputedFileForProcessing = args[3];
		final String deathFileInHadoop = args[4];
		envType = AppUtil.getProperty("environment.type");

		// MapReduce Configuration 
		final Configuration conf = getConf();

		// Validating Concept File existence in Hadoop(HDFS).
		Path conceptFile = new Path(conceptFileInHadoop);

		// Creating HDFS File System instance
		FileSystem fs = FileSystem.get(conf);

		if ((!fs.exists(conceptFile)) && (!fs.isFile(conceptFile))) {
			logger.error (conceptFileInHadoop + " is not found in HDFS");
			logger.error (conceptFileInHadoop + " is directory, not a file");
			return(-1);
		}

		if(envType !=null && EnvironmentType.DEVELOPMENT == EnvironmentType.valueOf(envType)) {
			logger.info ("Input Param : " + inputedFileForProcessing);
		}

		// MapReduce Configuration Parameteres
		conf.set("envType",envType);
		conf.set("inputParam", inputedFileForProcessing);

		// calling method 
		Map<String, String> observationIdsFound = parsingInputStringForObservationId(inputedFileForProcessing);

		// calling method
		String observationDetails = iteratingOverConceptFile(observationIdsFound, conceptFileInHadoop, fs);

		for(Map.Entry<String, String> entry : observationIdsFound.entrySet()) {
			if (entry.getValue().equals("NOT FOUND")) {
				logger.error ("Given Observation Id " +  entry.getKey() + " is not found in concept.txt file");
				return(-1);
			}
		}
		observationIdsFound = null;

		conf.set("observationDetails", observationDetails);
		logger.info ("Observation Details : " + observationDetails);

		Job job = Job.getInstance(conf, "Multi Observation Id Search");

		job.setJarByClass(MultiObservationId.class);
		job.setJobName("Multi Observation Id Search");

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		TextOutputFormat.setOutputPath(job, new Path(outputFilePath));

		MultipleInputs.addInputPath(job, new Path(observationFilePath), TextInputFormat.class, MultiObservationIdMapper.class);
		MultipleInputs.addInputPath(job, new Path(deathFileInHadoop), TextInputFormat.class, MultiDeathMapper.class);

		job.setReducerClass(MultiObservationIdReducer.class);

		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		job.setNumReduceTasks(1);

		return(job.waitForCompletion(true) ? 0 : 1);
	}

	/**
	 * 
	 * @param inputedFileForProcessing
	 * @return
	 */
	private Map<String, String> parsingInputStringForObservationId(String inputedFileForProcessing) {

		StringTokenizer inidvidualString_observationWithIdsAndColor = new StringTokenizer(inputedFileForProcessing, "#");
		Map<String, String> observationIdsFound = new HashMap<String, String>();

		/*
		 * Iterating over each string contains observations_Ids_color.
		 * 
		 * On every iterated string, split using '~'. This will give Obs, Ids, color.
		 * 
		 * Inserting first element I.e. Observation into Map. 
		 */
		while(inidvidualString_observationWithIdsAndColor.hasMoreTokens()) {

			String observationIdWiseInput = inidvidualString_observationWithIdsAndColor.nextToken();
			StringTokenizer tokens_Obs_ids_color = new StringTokenizer(observationIdWiseInput, "~");

			String observation = tokens_Obs_ids_color.nextToken();
			observationIdsFound.put(observation, "NOT FOUND");

			if(envType !=null && EnvironmentType.DEVELOPMENT == EnvironmentType.valueOf(envType)) {
				logger.info ("observationIdWiseInput : " + observationIdWiseInput);
			}
			observationIdWiseInput = null;
			tokens_Obs_ids_color = null;
		}
		return observationIdsFound;
	}

	/**
	 * 
	 * @param observationIdsFound
	 * @param conceptFileInHadoop
	 * @param fs
	 * @return string
	 * @throws IOException 
	 */
	private String iteratingOverConceptFile(Map<String, String> observationIdsFound, 
			String conceptFileInHadoop, FileSystem fs) throws IOException {

		BufferedReader conceptReader = null;
		String observationDetails = null;
		try {
			String[] conceptIdArr = null;
			conceptReader = new BufferedReader(new InputStreamReader(fs.open(new Path(conceptFileInHadoop))));
			String line = conceptReader.readLine();
			while (line != null && !line.isEmpty()) {

				conceptIdArr = line.split(";");

				if(envType !=null && EnvironmentType.DEVELOPMENT == EnvironmentType.valueOf(envType)) {
					logger.info ("Value of line : " + line);
					logger.info ("Size of array : " + conceptIdArr.length);
					logger.info ("concept_id : " + conceptIdArr[0].trim());
					logger.info ("property_name : " + conceptIdArr[1].trim());
					logger.info ("value_units : " + conceptIdArr[4].trim());
				}

				for (Map.Entry<String, String> entry : observationIdsFound.entrySet()) {
					if (conceptIdArr[1].equals(entry.getKey())) {

						observationIdsFound.put(entry.getKey(), "FOUND");
						if (conceptIdArr[4].length() == 0)
							conceptIdArr[4] = "No Unit";
						if (observationDetails != null)
							observationDetails += "#" + entry.getKey() + "~" + conceptIdArr[0] + "~" + conceptIdArr[2] + "~" + conceptIdArr[4];
						else
							observationDetails = entry.getKey() + "~" + conceptIdArr[0] + "~" + conceptIdArr[2] + "~" + conceptIdArr[4];
						break;
					}
				}

				// read the next line
				line = conceptReader.readLine();
			}
		} catch (IOException e) {
			logger.error ("Error while reading concept file");
		} finally {
			if(conceptReader != null)
				conceptReader.close();
		}
		return observationDetails;
	}
}