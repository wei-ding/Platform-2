package org.clinical3PO.learn.main;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.TreeMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.clinical3PO.learn.util.C3POFilterConfiguration;
import org.clinical3PO.learn.util.FEConfiguration;
import org.clinical3PO.learn.util.FEEvaluatorBase;

public class FEMain {


	/**
	 * helper function to read files
	 * from http://stackoverflow.com/questions/326390/how-to-create-a-java-string-from-the-contents-of-a-file
	 * THIS IS FOR PLAIN OLD JAVA ON FILESYSTEM, don't know if it'll work on hadoop
	 * adapting to use an input stream, should work on 'doop
	 */
	private static String readFile(InputStream is) throws IOException {
		StringBuffer fileContents = new StringBuffer();
		Scanner scanner = new Scanner(is);
		String lineSeparator = System.getProperty("line.separator");
		try {
			while(scanner.hasNextLine()) {        
				fileContents.append(scanner.nextLine() + lineSeparator);
			}
			return fileContents.toString();
		} finally {
			scanner.close();
		}
	}	

	/**
	 * helper function to read vectors from map/reduce output. May well end up splitting this off into a
	 * separate class or separate map/reduce task
	 * 
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
								}
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

	//cribbed from cookbook chapter 1 word count thing
	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();	// initialize configuration
		
		// To read both Hadoop parameters(-D prefixed) and regular arguments. 
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();

		//parse parameters - this is really just for first-pass checking, the mappers and redders will do
		//their own version of it.
		FECmdLine cmdline = new FECmdLine();
		
		if(otherArgs.length != 0) {
			System.err.println("-- getting configuration from command line arguments");
			System.err.println("(If you want to use the hadoop configuration properties to configure this,");
			System.err.println("be sure there are no command line arguments after the hadoop-specific arguments.)");
			if(!cmdline.parseCommandLine(otherArgs)) {		//was just args
				System.err.println("ERROR: command line incorrect");
				System.exit(1);
			}
		} 
		
		//if there are no otherArgs, let's try just fetching it out of hadoop properties.
		else {		
			//new way:
			System.err.println("-- getting configuration from hadoop configuration properties");
			if(!cmdline.getCommandLineFromConfHadoopProperties(conf)) {
				System.err.println("ERROR: property configuration incorrect");
			}
		}


		//Build distributed cache BEFORE constructing job.
		//see http://stackoverflow.com/questions/13746561/accessing-files-in-hadoop-distributed-cache
		/*
		 * Configuration conf = new Configuration();
			DistributedCache.addCacheFile(new URI("/user/peter/cacheFile/testCache1"), conf);
			Job job = new Job(conf, "wordcount");
			/uufs/chpc.utah.edu/common/home/u0176876/bigdata/Clinical3PO/Scrums/Scrum5/FETests/filterconfig1.txt
		 */

		/* DEPRECATED HORRIBLE!
		//THIS WORKED ON EMBER and seems to on my laptop too
		//DistributedCache.addCacheFile(new URI("/uufs/chpc.utah.edu/common/home/u0176876/bigdata/Clinical3PO/Scrums/Scrum5/FETests/filterconfig1.txt"), conf);
		//see if this works on anywhere - current directory
		//System.err.println("adding \"./filterconfig.txt\" to dist cache");
		DistributedCache.addCacheFile(new URI(cmdline.filterConfigFilePath), conf);
		DistributedCache.addCacheFile(new URI(cmdline.feConfigFilePath), conf);
		//let's try one we know doesn't exist - and YAY?, it does fail.
		//DistributedCache.addCacheFile(new URI("/uufs/chpc.utah.edu/common/home/u0176876/bigdata/Clinical3PO/Scrums/Scrum5/FETests/hovbart.txt"), conf);
		 */

		//let's try this! This is a plain old java program, right?
		//see if we can just send the config files as strings on the conf.
		//I DON'T KNOW IF THIS WILL WORK ON A REAL HADOOP CLUSTER!
		//let's do it this way:
		FileSystem fs = FileSystem.get(conf);
		FSDataInputStream FSInputStream = null;
		Path path = null;
		FileSystem localFS = null;

		System.err.println("Reading filter config into conf string...");
		
		/*
		 * Below are the two lines of code to read files from localFS (file:///) instead of hdfs (hdfs:///) 
		 */
		path = new Path(cmdline.filterConfigFilePath); 
		localFS = FileSystem.get(path.toUri(), conf);
		FSInputStream = localFS.open(path);
		String filtconfContents = readFile(FSInputStream);
		conf.set("filtconfContents", filtconfContents);
		System.err.println("################################################");
		System.err.println(filtconfContents);
		FSInputStream = null;
		localFS = null;

		System.err.println("Reading feature extraction config into conf string...");
		
		/*
		 * Below are the two lines of code to read files from localFS (file:///) instead of hdfs (hdfs:///) 
		 */
		path = new Path(cmdline.feConfigFilePath);
		localFS = FileSystem.get(path.toUri(), conf);
		FSInputStream = localFS.open(path);
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
		InputStream is;
		//configuration files:
		C3POFilterConfiguration filtconf = null;
		FEConfiguration feconf = null;

		if(conf.get("filtconfContents") != null) {
			filtconf = new C3POFilterConfiguration();
			filtconfContents = conf.get("filtconfContents");
			System.err.println("-- filter config contents: " + filtconfContents.length() + " chars");
			is  = new ByteArrayInputStream(filtconfContents.getBytes(Charset.forName("UTF-8")));
			if(filtconf.readPropertyConfiguration(new InputStreamReader(is))) {
				System.err.println("--- filter configuration read successfully! attrs");
				//DEBUG print out
				for(String attr:filtconf.getPropertiesUsed().keySet()) {
					System.err.println();
					System.err.print(attr);
					System.err.println();
					if(filtconf.getPropertiesUsed().get(attr) != null) {
						//print out time range
						System.err.println(" " + filtconf.getPropertiesUsed().get(attr));
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
		} else {
			throw new Exception("No filter configuration given");
		}

		//TODO LATER THERE WILL BE MORE THAN ONE OF THESE
		if(conf.get("feconfContents") != null) {
			feconfContents = conf.get("feconfContents");
			System.err.println("-- feature ext config contents: " + feconfContents.length() + " chars");

			is  = new ByteArrayInputStream(feconfContents.getBytes(Charset.forName("UTF-8")));
			if(feconf == null) feconf = new FEConfiguration();

			if(feconf.accumulateFromTextConfigFile(is,cmdline.classAttribute)) {
				System.err.println("--- feature ext. configuration read successfully!");
				//DEBUG OUTPUT???
			} else {
				is.close();
				throw new Exception("ERROR: unable to read feature extraction config file");
			}
			is.close();

			//check to make sure feconf is ok after all the accumulations done
			if(!feconf.validateAfterAccumulation(cmdline.globalTimeRange,filtconf,cmdline.classAttribute,cmdline.classBinTime)) {
				throw new Exception("ERROR: incorrect feature extraction configuration");
			}

		} else {
			throw new Exception("No feature extraction configuration given");
		}

		//OK! all the command line / configuration stuff appears to be OK. Start the job.

		Job job = Job.getInstance(conf, "Map/Reduce ARFF Test");
		//what does this do? Finds out which jar file contains the given class and uses it as the jar all nodes should run...?
		//see http://stackoverflow.com/questions/3912267/hadoop-query-regarding-setjarbyclass-method-of-job-class and
		//http://search-hadoop.com/m/R7Uxr1dp4h&subj=setJarByClass+question
		job.setJarByClass(FEMain.class);
		job.setMapperClass(FEMapper.class);
		job.setReducerClass(FEReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(cmdline.inputDirectory));
		FileOutputFormat.setOutputPath(job, new Path(cmdline.outputDirectory));
		job.setNumReduceTasks(cmdline.noOfReducers);
		//original: System.exit(job.waitForCompletion(true) ? 0 : 1);
		boolean jobSuccessful = job.waitForCompletion(true);

		//TODO: EXPERIMENT
		//Here is a thing to try: wait for the completion of the job here and do the arff header emitting and
		//feature vector reconciliation here - we should be able to see all the output part_x files and stuff in the hdfs.
		if(jobSuccessful) {
			System.err.println("-- Feature vector extraction successful. Creating arff header and output arff file");
			//first pass: Let's create the arff header first - output dir/arffhdr.txt?
			//try this. We need a PrintWriter, command line object, and the configuration file objects.
			//this is what we want 
			//was String hdrPathStr = cmdline.outputDirectory+ "/arffhdr.txt";
			String arffPathStr = cmdline.outputArffPathAndName+"/"+cmdline.outputArffPathAndName+".arff";
			//try this debug - doesn't help
			//String hdrPathStr = "arffhdr.txt";
			Path arffPath = new Path(arffPathStr);
			System.err.println("Path for arff file is |" + arffPath + "|");
			FSDataOutputStream fsdo = fs.create(arffPath, true);
			PrintWriter pw = new PrintWriter(fsdo);

			//we haven't to this point read the config files in the main! So read them. 
			//maybe should do this up above so that we fail out even before starting the job if they don't work -
			//so off I go to do that.

			//debug:
			//System.err.println("-- filter config: " + filtconf);
			//System.err.println("-- feat.ext. config: " + feconf);

			ArffHeaderMaker hm = new ArffHeaderMaker();
			boolean hdrresult = hm.writeArffHeader(pw, cmdline, filtconf, feconf);
			//used to flush/close fsdis and pw here, but now we're adding the feature vectors

			if(!hdrresult) {
				System.err.println("Failed to create arff header! |" + arffPathStr + "|");
			} else {
				System.err.println("-- Successfully wrote arff header |" + arffPathStr + "|");

				//OK! Got an arff header. Now let's look at the vector files we need to reconcile.
				//Assume they're all in the output directory. So see what's in there! Assume that we don't
				//need to do recursive descent - that's what the boolean on the listfiles call is.
				RemoteIterator<LocatedFileStatus> filelistIter = fs.listFiles(new Path(cmdline.outputDirectory), false);
				System.err.println("Reconciling feature vectors...");

				while(filelistIter.hasNext()) {
					LocatedFileStatus filstat = filelistIter.next();
					//first let's just see what we've got. Try the path fields.
					//debug System.err.println(filstat.getPath().toString());
					if(filstat.getPath().getName().startsWith("part-r-")) {
						//aha, a "part" file.
						System.err.println("-- processing: " + filstat.getPath().getName());

						FSInputStream = fs.open(filstat.getPath());
						TreeMap<String,String> featureVectors = accumulateForReconcile(FSInputStream);

						if(featureVectors != null && !featureVectors.isEmpty()) {

							System.err.println("-- done: " + filstat.getPath().getName());

							//then once that's done, emit all the feature vectors, replacing any occurrence of the absent-feature
							//value with ? - TODO LATER THIS MAY CHANGE
							//System.err.println("Emitting feature vectors...");
							//open arff file for append and emit the strings on there
							for(String pid:featureVectors.keySet()) {
								//I THINK THIS IS RIGHT - this is a replace all of a with b, yes? And literal rather than regex
								//for the match? That's what I want.
								String vec = featureVectors.get(pid).replace(FEEvaluatorBase.absentFeatureValue, "?");

								//emit feature vector to our arff in progress.
								//HERE IS WHERE WE LEAVE OUT UNKNOWN-CLASS VECTORS.
								if(cmdline.includeUnknownClass) {
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
						}
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
		} else {
			System.err.println("NOTE: Feature vector extraction job failed; not creating arff header or output arff file");
		}

		System.exit(jobSuccessful ? 0 : 1);
	}	
}
