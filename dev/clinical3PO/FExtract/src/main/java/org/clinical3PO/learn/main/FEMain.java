package org.clinical3PO.learn.main;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.LazyOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.clinical3PO.learn.util.C3POFilterConfiguration;
import org.clinical3PO.learn.util.FEConfiguration;
import org.clinical3PO.learn.util.FEEvaluatorBase;

import com.google.gson.Gson;

/**
 * Is a Hadoop program and this class acts as a driver.
 * Brief: 
 * 		Capture input params, parse and assign to defined object.
 * 		Pre-calculate the required stuff such as basicFEConfig.txt and filterConfig.txt files.
 * 		Initiate Hadoop job to query concept.txt for required info.
 * 		Initiate Hadoop job on observation.txt, providing concept job output as input.
 * 		Access, Read and Parse the output from observation Hadoop job to generate Arff. 
 * @author 3129891
 *
 */
public class FEMain {

	private static String feAbsentValueReplaceWithNewSting = "?";
	
	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();	// initialize configuration

		// To read both Hadoop parameters(-D prefixed) and regular arguments. 
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();

		//parse parameters - this is really just for first-pass checking, the mappers and redders will do their own version of it.
		FECmdLine cmdLine = new FECmdLine();

		/*
		 * This condition checks if the input params are Hadoop Parsed(-D) or normal arguments.
		 * Two different parsers are available to do the job. One for Hadoop parsed arguments, other for normal.
		 */
		if(otherArgs.length != 0) {
			System.err.println("-- getting configuration from command line arguments");
			System.err.println("(If you want to use the hadoop configuration properties to configure this,");
			System.err.println("be sure there are no command line arguments after the hadoop-specific arguments.)");
			if(!cmdLine.parseCommandLine(otherArgs)) {		//was just args
				System.err.println("ERROR: command line incorrect");
				System.exit(1);
			}
		} 

		//if there are no otherArgs, let's try just fetching it out of hadoop properties.
		else {		
			//new way:
			System.err.println("-- getting configuration from hadoop configuration properties");
			if(!cmdLine.getCommandLineFromConfHadoopProperties(conf)) {
				System.err.println("ERROR: property configuration incorrect");
			}
		}

		conf.set("classProperty", cmdLine.classAttribute);
		FileSystem fs = FileSystem.get(conf);
		FSDataInputStream FSInputStream = null;
		Path loaclPath = null;
		FileSystem localFS = null;

		/*
		 * Reading the input configuration files(which decides on what features data is to be fetched and on what parameters)
		 * Code to read the Files from Local File System.
		 * Below are the two lines of code to read files from localFS (file:///) instead of hdfs (hdfs:///) 
		 */
		loaclPath = new Path(cmdLine.filterConfigFilePath); 	// Creating object for file path
		localFS = FileSystem.get(loaclPath.toUri(), conf);		// Initializing FileSystem to get the connection
		if(!localFS.exists(loaclPath)) {
			System.err.println("Path doesn't exists: "+ loaclPath.getName() + " \nCheck the path properly.");
			System.exit(1);
		}
		FSInputStream = localFS.open(loaclPath);	// connect and read file through a stream.
		
		// method call
		String filtconfContents = readFile(FSInputStream);
		conf.set("filtconfContents", filtconfContents);
		System.err.println("################################################");
		System.err.println(filtconfContents);
		FSInputStream = null;
		localFS = null;
		loaclPath = null;
		System.err.println("Reading feature extraction config into conf string...");

		/*
		 * Below are the two lines of code to read files from localFS (file:///) instead of hdfs (hdfs:///) 
		 */
		loaclPath = new Path(cmdLine.feConfigFilePath);			// Creating object for file path
		localFS = FileSystem.get(loaclPath.toUri(), conf);		// Initializing FileSystem to get the connection
		if(!localFS.exists(loaclPath)) {
			System.err.println("Path doesn't exists: "+ loaclPath.getName() + " \nCheck the path properly.");
			System.exit(1);
		}
		FSInputStream = localFS.open(loaclPath);	// connect and read file through a stream.
		
		// method call
		String feconfContents = readFile(FSInputStream);
		conf.set("feconfContents", feconfContents);
		System.err.println("################################################");
		System.err.println(feconfContents);
		FSInputStream = null;
		localFS = null;

		//so let's add our cmdline parameters to the configuration and see how that looks.
		//could conceivably just add all the cmdline args and have the mappers and reducers run their own parse on it?
		//trying what's at http://www.thecloudavenue.com/2011/11/passing-parameters-to-mappers-and.html
		//all this did use args
		//new: DON'T DO THIS if there aren't any otherArgs - those are for backward compatibility
		//and if we're using the hadoop-property way with the c3fe.x things, this will throw it off.
		if(otherArgs.length > 0) {
			conf.set("numArgs", Integer.toString(otherArgs.length));
			for(int j=0;j<otherArgs.length;j++) {
				conf.set("arg" + Integer.toString(j), otherArgs[j]);
			}
		}

		//HERE READ THE CONFIGURATION FILES - VERIFY THAT THEY ARE CORRECT BEFORE SPINNING UP THE JOB
		//and also they're needed for the arff generation step, if I end up doing that in this program.

		// parsing configuration files:
		C3POFilterConfiguration filtConf = null;
		if(conf.get("filtconfContents") != null) {

			/*
			 *  method call
			 *  
			 *  Parsing the configuration file - filterConfig (contains, for which class feature do we need to fetch features)
			 */
			filtConf = parseFilterConfigParameters(conf.get("filtconfContents"));		
		} else {
			throw new Exception("No filter configuration given");
		}

		FEConfiguration feConf = null;
		if(conf.get("feconfContents") != null) {

			/*
			 *  method call
			 *  
			 *  Parsing the configuration file - basicFEConf (for every class properties were declared. 
			 *  eg: binwidth, deviation type, legit type etc...)
			 */
			feConf = parseFeConfigParameters(conf.get("feconfContents"), cmdLine, filtConf);
		} else {
			throw new Exception("No feature extraction configuration given");
		}

		/**
		 * This Driver deals with two Job's.
		 *  1) A Job on Concept.txt to get relevant information.
		 *  2) A Job on Input file to extract features using filters, such as concepts, date and time frames. 
		 */
		String conceptOutPutTemporaryFile = "conTempOut";	// Temporary path for dropping output from concept-Job
		Path conceptOutput = new Path(conceptOutPutTemporaryFile);
		fs.delete(conceptOutput, true);
		
		Job job_conceptFile = Job.getInstance(conf, "FE-ConceptExtract");
		
		FileInputFormat.setInputPaths(job_conceptFile, new Path(cmdLine.conceptDirectory));
		FileOutputFormat.setOutputPath(job_conceptFile, conceptOutput);

		job_conceptFile.setMapperClass(FEConceptMapper.class);
		job_conceptFile.setMapOutputKeyClass(Text.class);
		job_conceptFile.setMapOutputValueClass(NullWritable.class);

		job_conceptFile.setJarByClass(FEMain.class);

		LazyOutputFormat.setOutputFormatClass(job_conceptFile, TextOutputFormat.class);
		int result = job_conceptFile.waitForCompletion(true) ? 0 : 1;

		if(result == 1) {
			System.err.println("Program to parse Concept file isn't sucesful.");
			System.exit(result);
		}
		
		if(!fs.exists(conceptOutput)) {
			System.err.println("Path doesn't exists: "+ conceptOutput.getName() + " \nCheck the path properly.");
			System.exit(1);
		}

		/*
		 * method call
		 * 
		 * Reads output from concept-job output
		 */
		Object[] arrayOfObjects = readInputPaths(fs.open(new Path(conceptOutPutTemporaryFile+"/part-r-00000")));
		fs.delete(conceptOutput, true);	// delete the output file for concept-job

		/*
		 * method call
		 * 
		 * Parse the output from concept-job to get a map of key value pair.
		 * Convert the class into JSON using Gson class and get the reference to the JSON object.
		 * Set the reference to the conf so that it could be accessed and used in Mapper/Reducer.
		 */
		FEUtils utilsObject = FEUtils.getFEUtils(arrayOfObjects);
		Gson gson = new Gson();
		String conceptData = gson.toJson(utilsObject);
		conf.set("conceptData", conceptData);

		//OK! all the command line / configuration stuff appears to be OK. Start the job.
		Job feCoreJob = Job.getInstance(conf, "FE-ArffGen");

		feCoreJob.setJarByClass(FEMain.class);
		
		feCoreJob.setMapperClass(FECoreMapper.class);
		feCoreJob.setPartitionerClass(FECorePartitioner.class);
		feCoreJob.setSortComparatorClass(FESortComparator.class);
		feCoreJob.setGroupingComparatorClass(FEGroupingComparator.class);
		feCoreJob.setReducerClass(FECoreReducer.class);

		feCoreJob.setMapOutputKeyClass(FEDataObjects.class);
		feCoreJob.setMapOutputValueClass(NullWritable.class);
		feCoreJob.setOutputKeyClass(Text.class);
		feCoreJob.setOutputValueClass(Text.class);

		FileInputFormat.addInputPath(feCoreJob, new Path(cmdLine.inputDirectory));
		FileOutputFormat.setOutputPath(feCoreJob, new Path(cmdLine.outputDirectory));

		feCoreJob.setNumReduceTasks(cmdLine.noOfReducers);

		boolean jobSuccessful = feCoreJob.waitForCompletion(true);

		/**
		 * wait for the completion of the job here and do the arff header emitting and feature vector reconciliation here.
		 * Parse all the output part_r-00000 files in the hdfs
		 */
		if(jobSuccessful) {

			// method call
			constructingArffFromOutPut(fs, cmdLine, feConf, filtConf);
		} else {
			System.err.println("NOTE: Feature vector extraction job failed; not creating arff header or output arff file");
		}

		System.exit(jobSuccessful ? 0 : 1);
	}

	/**
	 * 
	 * @param filtconfContents
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	private static C3POFilterConfiguration parseFilterConfigParameters(String filtconfContents) throws IOException, Exception {

		C3POFilterConfiguration filterConf = new C3POFilterConfiguration();
		System.err.println("-- filter config contents: " + filtconfContents.length() + " chars");
		InputStream is  = new ByteArrayInputStream(filtconfContents.getBytes(Charset.forName("UTF-8")));
		if(filterConf.readPropertyConfiguration(new InputStreamReader(is))) {
			System.err.println("--- filter configuration read successfully! attrs");
			//DEBUG print out
			for(String attr:filterConf.getPropertiesUsed().keySet()) {
				System.err.println();
				System.err.print(attr);
				System.err.println();
				if(filterConf.getPropertiesUsed().get(attr) != null) {
					//print out time range
					System.err.println(" " + filterConf.getPropertiesUsed().get(attr));
				} else {
					//blank, assume global time range
					System.err.println();
				}
			}
			is.close();
		} else {
			is.close();
			throw new Exception("ERROR: unable to read filter config file");
		}
		return filterConf;
	}

	/**
	 * 
	 * @param feConfContents
	 * @param cmdLine
	 * @param filterConf
	 * @return
	 * @throws Exception
	 */
	private static FEConfiguration parseFeConfigParameters(String feConfContents, FECmdLine cmdLine, C3POFilterConfiguration filterConf) throws Exception {

		FEConfiguration feConf = new FEConfiguration();
		System.err.println("-- feature ext config contents: " + feConfContents.length() + " chars");

		InputStream is = new ByteArrayInputStream(feConfContents.getBytes(Charset.forName("UTF-8")));

		if(feConf.accumulateFromTextConfigFile(is, cmdLine.classAttribute)) {
			System.err.println("--- feature ext. configuration read successfully!");
			//DEBUG OUTPUT???
		} else {
			is.close();
			throw new Exception("ERROR: unable to read feature extraction config file");
		}
		is.close();

		//check to make sure feconf is ok after all the accumulations done
		if(!feConf.validateAfterAccumulation(cmdLine.globalTimeRange,filterConf,cmdLine.classAttribute,cmdLine.classBinTime)) {
			throw new Exception("ERROR: incorrect feature extraction configuration");
		}
		return feConf;
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
	
	/**
	 * Read and parse the configuration file for list of observations, of whose Feature's to be extracted.
	 * 
	 * @param fsdis
	 * @return list of observations as list
	 * @throws IOException 
	 */
	private static Object[] readInputPaths(FSDataInputStream fsdis) throws IOException {

		List<String> list = null;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(fsdis));
			list = new ArrayList<String>();

			String line = null;
			while((line=br.readLine()) != null) {

				if(line.isEmpty()) continue;
				System.err.println("------------ " + line);
				list.add(line.trim());
			}
		} catch (IOException e1) {
			System.err.println("File Not Found" + e1);
			System.exit(0);
		} finally {
			if(br != null)	br.close();
		}
		return list.toArray();
	}

	/**
	 * 
	 * @param fs
	 * @param cmdLine
	 * @param feConf
	 * @param filterConf
	 * @throws Exception
	 */
	private static void constructingArffFromOutPut(FileSystem fs, FECmdLine cmdLine, FEConfiguration feConf, C3POFilterConfiguration filterConf) throws Exception {

		/*
		 * first pass: Let's create the arff header first - output dir/arffhdr.txt?
		 * try this. We need a PrintWriter, command line object, and the configuration file objects.  
		 */
		System.err.println("-- Feature vector extraction successful. Creating arff header and output arff file");
		String arffPathStr = cmdLine.outputArffPathAndName+"/"+cmdLine.outputArffPathAndName+".arff";
		Path arffPath = new Path(arffPathStr);
		System.err.println("Path for arff file is |" + arffPath + "|");
		FSDataOutputStream fsdo = fs.create(arffPath, true);
		PrintWriter pw = new PrintWriter(fsdo);

		/*
		 * Adding ARFF Header to the .arff file, based on the configurations.
		 * ArffHeaderMaker class helps to do so.
		 */
		ArffHeaderMaker arffHeaderMaker = new ArffHeaderMaker();
		boolean hdrresult = arffHeaderMaker.writeArffHeader(pw, cmdLine, filterConf, feConf);

		if(!hdrresult) {
			System.err.println("Failed to create arff header! |" + arffPathStr + "|");
		} else {
			System.err.println("-- Successfully wrote arff header |" + arffPathStr + "|");

			//OK! Got an arff header. Now let's look at the vector files we need to reconcile.
			//Assume they're all in the output directory. So see what's in there! Assume that we don't
			//need to do recursive descent - that's what the boolean on the listfiles call is.
			RemoteIterator<LocatedFileStatus> filelistIter = fs.listFiles(new Path(cmdLine.outputDirectory), false);
			System.err.println("Reconciling feature vectors...");

			System.err.println("CONSTRUCTING ARFF BY PARSING HADOOP OUTPUT");
			while(filelistIter.hasNext()) {

				LocatedFileStatus filstat = filelistIter.next();
				System.err.println("-- FileName: " + filstat.getPath().getName());

				//first let's just see what we've got. Try the path fields.
				//debug System.err.println(filstat.getPath().toString());
				if(filstat.getPath().getName().startsWith("part-r-")) {

					System.err.println("-- processing: " + filstat.getPath().getName());
					FSDataInputStream FSInputStream = fs.open(filstat.getPath());
					TreeMap<String,String> featureVectors = accumulateForReconcile(FSInputStream);

					if(featureVectors != null && !featureVectors.isEmpty()) {

						//then once that's done, emit all the feature vectors, replacing any occurrence of the absent-feature
						//value with ? - TODO LATER THIS MAY CHANGE
						//System.err.println("Emitting feature vectors...");
						//open arff file for append and emit the strings on there
						for(String pid : featureVectors.keySet()) {
							//I THINK THIS IS RIGHT - this is a replace all of a with b, yes? And literal rather than regex
							//for the match? That's what I want.
							String vec = featureVectors.get(pid).replace(FEEvaluatorBase.absentFeatureValue, feAbsentValueReplaceWithNewSting);

							//emit feature vector to our arff in progress.
							//HERE IS WHERE WE LEAVE OUT UNKNOWN-CLASS VECTORS.
							if(cmdLine.includeUnknownClass) {
								pw.println(vec);				//just print it in every case
							} else {
								//check for the last comma-delim thing being a ? - we can hardcode that because arff.
								//it really should be the last thing in the whole line, yes? check to be sure there's
								//a comma before it
								//DANGER MAKE THIS SMARTER IF NEEDED
								if(!vec.endsWith(",?")) {
									pw.println(vec);			//print it out if not ending with ?
								}
							}
						}
						featureVectors = null;
						System.err.println("-- done: " + filstat.getPath().getName());
					}
				} else {
					System.err.println("Not relevent file to parse. Skipping File. ");
				}
			}

			//send an extra newline just in case - nice for files to have one at the end.
			pw.println();
		}
		//explicit flush and close of everything bc we're currently getting nothing out:
		//YES YOU HAVE TO DO THIS!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		pw.flush();
		fsdo.flush();
		pw.close();
		fsdo.close();
	}

	/**
	 * helper function to read vectors from map/reduce output. May well end up splitting this off into a
	 * separate class or separate map/reduce task
	 * 
	 * @param is
	 * @return map
	 * @throws IOException
	 */
	private static TreeMap<String,String> accumulateForReconcile(InputStream is) throws IOException {

		LineNumberReader lnr = new LineNumberReader(new InputStreamReader(is));
		String theLine = null;
		TreeMap<String,String> vecs = new TreeMap<String,String>();

		try {
			do {
				theLine = lnr.readLine();
				if(theLine != null) {
					//we should have PID, tab, comma-sep vector data.
					//toks[0] will be PID, toks[1] the entire vector as a string.
					String[] toks = theLine.split("\t", 2); 
					
					//sanity check pid and stuff?
					if(toks[0].isEmpty()) {
						//error - no data - though just warn, this may come up
						System.err.println("accumulateForReconcile WARNING - empty PID");

					} else if(toks[1].isEmpty()) {
						//error - no data - though just warn, this may come up
						System.err.println("accumulateForReconcile WARNING - empty feature vector");

					} else {
						//should be legit PID/vector
						//if the pid doesn't exist in vecs, just record toks[0]->toks[1].
						if(!vecs.containsKey(toks[0])) {
							vecs.put(toks[0], toks[1]);
						} else {
							//there is already a feature vector for the PID - reconcile missing feature values
							//here.
							String[] featvalsOld = vecs.get(toks[0]).split(",");
							String[] featvalsNew = toks[1].split(",");

							//TODO here make sure they have the same number of values
							if(featvalsOld.length != featvalsNew.length) {
								System.err.println("accumulateForReconcile WARNING - old and new feature vectors for pid |" + toks[0] + "| mismatch in length, input file line " + lnr.getLineNumber());
							} else {
								StringBuffer newValsVector = new StringBuffer();
								for(int j=0;j<featvalsOld.length;j++) {
									
									if(j == (featvalsOld.length-1)) {
										if(!featvalsOld[j].equals(FEEvaluatorBase.absentFeatureValue) && !featvalsOld[j].equals(feAbsentValueReplaceWithNewSting)) {
											newValsVector.append(featvalsOld[j]);
										} else if(!featvalsNew[j].equals(FEEvaluatorBase.absentFeatureValue) && !featvalsNew[j].equals(feAbsentValueReplaceWithNewSting)) {
											newValsVector.append(featvalsNew[j]);
										} else {
											newValsVector.append(featvalsOld[j]);
										}
										break;
									}
									//for each value, there are four possibilities:
									if(featvalsOld[j].equals(FEEvaluatorBase.absentFeatureValue)) {
										if(featvalsNew[j].equals(FEEvaluatorBase.absentFeatureValue)) {
											//old = absent-feature, new = absent-feature: emit absent-feature
											newValsVector.append(FEEvaluatorBase.absentFeatureValue);
										} else {
											//old = absent-feature, new = something else: emit the something else
											newValsVector.append(featvalsNew[j]);
										}
										//and append a comma unless we're at the end of the vector.
										if(j!=featvalsOld.length-1) newValsVector.append(",");
									} else {
										if(featvalsNew[j].equals(FEEvaluatorBase.absentFeatureValue)) {
											//old = something-else, new = absent feature: emit the something else
											newValsVector.append(featvalsOld[j]);
										} else {
											//old = something else, new = something else: error! warn and stay with old one?
											System.err.println("accumulateForReconcile WARNING - old and new feature vectors for pid |" + toks[0] + "| both have non-absent values at position " + j + ", input file line " + lnr.getLineNumber());
											newValsVector.append(featvalsOld[j]);
										}
										//and append a comma unless we're at the end of the vector.
										if(j!=featvalsOld.length-1) newValsVector.append(",");
									}
								}	// for-loop
								vecs.put(toks[0], newValsVector.toString());
							}
						}
					}
				}
			} while(theLine != null);

			return vecs;
		} finally {
			lnr.close();
		}
	}
}
