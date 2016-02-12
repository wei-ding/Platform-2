package org.clinical3PO.learn.main;

import java.io.File;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.clinical3PO.learn.util.C3POTimeRange;

public class FECmdLine {
	
	//classification feature - in this version, set with the -ca switch.
	//attribute/bin combination
	public String classAttribute; 
	public String classBinTimestamp;
	public C3POTimeRange globalTimeRange;
	public long classBinTime;		//hm. as parsed by time range
	public int noOfReducers;
	
	//paths - hadoop style - to filter configuration file, feature extraction config file,
	//input directory, output directory
	public String filterConfigFilePath;
	public String filterConfigFileName;		//filename snipped from config file path with File.getName()
	public String feConfigFilePath;
	public String feConfigFileName;			//filename snipped from config file path with File.getName()
	public String inputDirectory;
	public String outputDirectory;
	public String outputArffPathAndName;	//optional: output arff file name, defaults to output.arff in output dir 
	public boolean includeUnknownClass;
	public boolean includePatientID;		//optional: debug thing, default is false, includes patient ID as first attribute if true
	public boolean excludeClassPropFromFeatureVector;	//optional: if true, don't include class property bins in feature vector, only as class. default false
	public String conceptDirectory;
	
	//prefix for hadoop properties related to this class
	private static final String feCmdlinePrefix = "c3fe";

	
	//command line parser!
	//adapted from ArffGenS2, qv.
	public boolean parseCommandLine(String[] args) throws Exception
	{		
		if(args == null || args.length < 1) {
			
			// * later have others: bin width, feature config file, etc.
			
			System.err.println("Usage: " + this.getClass().getName() + " -b (start time) -e (end time) -ca (class attribute) -ct (class bin timestamp) -c (filter config file) "
					+ "-fec (feature extraction config) [-iuc] -id inputDirectory -od OutputDirectory");
			System.err.println("Where");
			System.err.println("-b (start time) is the earliest time stamp to include for attributes (inclusive)");
			System.err.println("-e (end time) is the latest time stamp to include for attributes (exclusive)");
			System.err.println("IN BOTH CASES TIMESTAMPS ARE GIVEN as hh:mm (hour:minute)");
			System.err.println("-c (filter config file) points to the configuration file used to guide generation of the input files (filter config)");
			System.err.println("-fec (feature config file) points to feature extraction configuration file");
			System.err.println("-ca (class attribute) names a patient attribute to use for classification, e.g. diasabp");
			System.err.println("-ct (timestamp) tells what time we want to predict the classification value. What will be used is the");
			System.err.println("     value of the bin in which that timestamp falls (not including any values that might have been found after that");
			System.err.println("     but within the same bin.)");
			System.err.println("-iuc, optional: means that instances with unknown class value are written out (and for discrete class attr \"unk\" is included)");
			System.err.println("      by default, instances with unknown class are not printed out");
			System.err.println("-nocpiv, optional: no class property in vector, i.e., do not emit feature vector bins for the property that is used");
			System.err.println("      as the -ca class property. By default, the class property IS included in the feature vector.");
			System.err.println("-pid, optional: means that instances each get patient ID attribute for debugging. String attribute in quotes.");
			System.err.println("      by default, patient IDs not added to the feature vector");
			System.err.println("-id inputDirectory points to the hadoop-style input directory");
			System.err.println("-od OutputDirectory points to the hadoop-style output directory - should not exist before running");
			System.err.println("-arffname (path/filename) optional - output path/file for arff file, defaults to output.arff in output directory");
			System.exit(1);
		}
		
		//here are things we need to know from the command line.
		inputDirectory = null;
		outputDirectory = null;
		outputArffPathAndName = null;
		feConfigFilePath = null;
		filterConfigFilePath = null;
		classAttribute = null;
		classBinTimestamp = null;				//NOT SURE HOW TO HANDLE THIS. Let's say always use last; will be derived
		
		//we'll read these from -b and -e and use a C3POTimeRange object to contain the time range if they're both given
		String startTimestamp = null;
		String endTimestamp = null;
		
		includeUnknownClass = false;			//by default, don't allow unknown class
		includePatientID = false;				//by default don't include patient ID in output
		excludeClassPropFromFeatureVector = false;	//by default, do not exclude class property bins from feature vector
		
		for(int j=0;j<args.length;j++) {
			String arg = args[j];
			
			if(arg.equals("-b")) {
				j++;
				if(j >= args.length) {
					throw new Exception("ERROR: -b given without parameter");
				}
				startTimestamp = args[j];			//will error-trap later
			} else if(arg.equals("-e")) {
				j++;
				if(j >= args.length) {
					throw new Exception("ERROR: -e given without parameter");
				}
				endTimestamp = args[j];			//will error-trap later
			} else if(arg.equals("-ca")) {
				j++;
				if(j >= args.length) {
					throw new Exception("ERROR: -ca given without parameter");
				}
				classAttribute = args[j].toLowerCase();
			} else if(arg.equals("-ct")) {
				j++;
				if(j >= args.length) {
					throw new Exception("ERROR: -ct given without parameter");
				}
				classBinTimestamp = args[j];			//will error-trap later
			} else if(arg.equals("-c")) {
				j++;
				if(j >= args.length) {
					throw new Exception("ERROR: -c given without parameter");
				}
				filterConfigFilePath = args[j];
			} else if(arg.equals("-fec")) {
				j++;
				if(j >= args.length) {
					throw new Exception("ERROR: -fec given without parameter");
				}
				feConfigFilePath = args[j];
			} else if(arg.equals("-iuc")) {
				//no trapping on this, it's just a switch
				includeUnknownClass = true;
			} else if(arg.equals("-pid")) {
				//no trapping on this, it's just a switch
				includePatientID = true;
			} else if(arg.equals("-nocpiv")) {
				//no trapping on this, it's just a switch
				excludeClassPropFromFeatureVector = true;
			} else if(arg.equals("-id")) {
				j++;
				if(j >= args.length) {
					throw new Exception("ERROR: -id given without parameter");
				}
				inputDirectory = args[j];
			} else if(arg.equals("-od")) {
				j++;
				if(j >= args.length) {
					throw new Exception("ERROR: -od given without parameter");
				}
				outputDirectory = args[j];
			} else if(arg.equals("-arffname")) {
				j++;
				if(j >= args.length) {
					throw new Exception("ERROR: -arffname given without parameter");
				}
				outputArffPathAndName = args[j];
			} else {
				//not a switch or parameter... ???
				System.err.println("WARNING: skipping unrecognized input parameter \"" + arg + "\"");
			}
		}
		
		return sanityCheck(startTimestamp,endTimestamp);
	}
		
	private boolean sanityCheck(String startTimestamp, String endTimestamp) throws Exception {
		//Sanity check input - do we use 
		//no longer a problem if(startTimestamp == null) throw new Exception("ERROR: no start timestamp given with -b, required");
		//no longer a problem if(endTimestamp == null) throw new Exception("ERROR: no end timestamp given with -b, required");
		if(classAttribute == null) throw new Exception("ERROR: no class attribute given with -ca, required");
		if(classBinTimestamp == null) throw new Exception("ERROR: no class bin timestamp given with -ct, required");
		if(inputDirectory == null) throw new Exception("ERROR: no input directory given with -id, required");
		if(outputDirectory == null) throw new Exception("ERROR: no output directory given with -od, required");
		if(filterConfigFilePath == null) throw new Exception("ERROR: no filter config file given with -c, required");
		if(feConfigFilePath == null) throw new Exception("ERROR: no feature extraction config file given with -fec, required");
		
		//construct default output arff path/name if none given
		String fileSeparator = File.separator;
		if(outputArffPathAndName == null) {
			if(outputDirectory.endsWith(fileSeparator)) outputArffPathAndName = outputDirectory + "output.arff";
			else  outputArffPathAndName = outputDirectory.concat(fileSeparator).concat("output.arff");
		}
		
		//no longer a problem, see below if(configFilePath == null) throw new Exception("ERROR: no config file given with -c, required");
		try {
			if(startTimestamp != null && endTimestamp != null) {
				globalTimeRange = new C3POTimeRange(startTimestamp,endTimestamp);
			} else {
				globalTimeRange = new C3POTimeRange();		//covers full range of everything
				//if we have a start OR end given, use it
				if(startTimestamp != null) globalTimeRange.setStartTime(C3POTimeRange.interpretTimeStamp(startTimestamp));
				if(endTimestamp != null) globalTimeRange.setEndTime(C3POTimeRange.interpretTimeStamp(endTimestamp));
			}
		} catch (Exception e) {
			System.err.println("ERROR: problem with start and end timestamps:");
			throw e;
		}
		
		//further: make sure the start and end are in the right order
		if(globalTimeRange.getStartTime() >= globalTimeRange.getEndTime()) {
			throw new Exception("ERROR: end time is before or same as start time");
		}

		//make sure class bin time is ok
		try {
			classBinTime = C3POTimeRange.interpretTimeStamp(classBinTimestamp);
		} catch (Exception e) {
			System.err.println("ERROR: problem with start and end timestamps:");
			throw e;
		}
		
		//get filenames from config file paths
		feConfigFileName = (new File(feConfigFilePath).getName());
		filterConfigFileName = (new File(filterConfigFilePath).getName());
		
		//print out what we know about our setup
		System.err.println("ARFFGEN CONFIGURATION:");
		System.err.println("-- global time range: " + globalTimeRange);
		System.err.println("-- allowing unknown-class instances: " + includeUnknownClass);
		System.err.println("-- excluding class property from feature vector: " + excludeClassPropFromFeatureVector);
		System.err.println("-- writing patient ID to arff output: " + includePatientID);
		System.err.println("-- classification attribute: " + classAttribute);
		System.err.println("-- classification time: " + classBinTimestamp + " (seconds: " + classBinTime + ")");
		System.err.println("-- input directory: " + inputDirectory);
		System.err.println("-- output directory: " + outputDirectory);
		System.err.println("-- output arff file: " + outputArffPathAndName);
		System.err.println("-- filter config file name: " + filterConfigFileName +  " path: " + filterConfigFilePath);
		System.err.println("-- feature extraction config file name: " + feConfigFileName + " path: " + feConfigFilePath);

		return true;
	}
	
	
	public boolean getCommandLineFromConfiguration(Configuration conf) throws Exception {
		String[] argy = null;
		
		//fetch command line arguments and construct a MRATCmdLine from them
		int numArgs;
		if(conf.get("numArgs") != null) {
			//System.err.println("Reducer gets numArgs from conf as " + conf.get("numArgs"));
			numArgs = Integer.valueOf(conf.get("numArgs"));
			
			//FETCH ARGUMENTS INTO AN ARRAY AND build a cmdline out of that.
			argy = new String[numArgs];
			for(int j=0;j<numArgs;j++) {
				//	System.err.println("-- arg " + j + ": |" + conf.get("arg" + Integer.toString(j)) + "|");
				argy[j] = conf.get("arg" + Integer.toString(j));
				if(argy[j] == null) {
					throw new Exception("MRATCmdLine ERROR: got null for command line argument " + j);
				}
			}
			
		} else {
			
			//new: attempt to use the new config properties
			System.err.println("-- no extra-hadoop command line arguments found; getting configuration from " + feCmdlinePrefix + " properties");
			return getCommandLineFromConfHadoopProperties(conf);			
			
			//throw new Exception("MRATCmdLine ERROR: couldn't find number of command line arguments");
		}
		
		
		return parseCommandLine(argy); 
	}
	
	
	/* new: let's switch to this using entirely hadoop -D things - but be backward compatible; can
	 * call directly for new way or from the function above for the old way
	  -D c3fe.starttime=00:00[:00]    -b (start time) is the earliest time stamp to include for attributes (inclusive)
	  -D c3fe.endtime=00:00[:00]      -e (end time) is the latest time stamp to include for attributes (exclusive)
	  -D c3fe.filterconfig=(filename) -c (filter config file) points to the configuration file used to guide generation of the input files (filter config)
	  -D c3fe.feconfig=(filename)     -fec (feature config file) points to feature extraction configuration file
	  -D c3fe.classproperty=(prop)    -ca (class attribute) names a patient attribute to use for classification, e.g. diasabp
	  -D c3fe.classtime=00:00[:00]    -ct (timestamp) tells what time we want to predict the classification value. What will be used is the
			    value of the bin in which that timestamp falls (not including any values that might have been found after that
			    but within the same bin.)
	  -D c3fe.unkclass=(true|false)   -iuc, optional: means that instances with unknown class value are written out (and for discrete class attr \"unk\" is included)
	      		by default false, i.e., instances with unknown class are not printed out
      -D c3fe.excludecpropfromvector=(true|false)  -nocpiv, optional: no class property in vector, i.e., do not emit feature vector bins for the property that is used");
			                           as the -ca class property. By default, the class property IS included in the feature vector.
      -D c3fe.incpid=(true|false)	  -pid, optional: debug switch to include patient ID as first attribute of output feature vectors
      	      	by default false, i.e., patient ID not emitted to feature vectors
	  -D c3fe.arffname=(path/file)    -arffname (path/filename) optional - output path/file for arff file, defaults to output.arff in output directory
	  -D c3fe.inputdir=inputdir       -id inputDirectory points to the hadoop-style input directory
	  -D c3fe.outputdir=outputdir     -od OutputDirectory points to the hadoop-style output directory - should not exist before running
	*/
	
	public boolean getCommandLineFromConfHadoopProperties(Configuration conf) throws Exception {
		//here are things we need to know from the command line.
		inputDirectory = null;
		outputDirectory = null;
		outputArffPathAndName = null;
		feConfigFilePath = null;
		filterConfigFilePath = null;
		classAttribute = null;
		classBinTimestamp = null;				//NOT SURE HOW TO HANDLE THIS. Let's say always use last; will be derived
		noOfReducers = 0;
		conceptDirectory = null;
		
		//we'll read these from -b and -e and use a C3POTimeRange object to contain the time range if they're both given
		String startTimestamp = null;
		String endTimestamp = null;
		
		includeUnknownClass = false;			//by default, don't allow unknown class
		includePatientID = false;				//by default don't include patient ID in output
		
		for (Entry<String, String> entry: conf) {
			//System.err.printf("%s=%s\n", entry.getKey(), entry.getValue());
			
			if(entry.getKey().startsWith(feCmdlinePrefix)) {
				if(entry.getKey().length()<feCmdlinePrefix.length()+2) {
					System.err.println("WARNING: skipping property + \"" + entry.getKey() + "\"");
				} else {
					//+1 to skip dot
					String arg=entry.getKey().substring(feCmdlinePrefix.length()+1);
					if(arg.equals("starttime")) {
						startTimestamp = entry.getValue();//will error-trap later
					} else if(arg.equals("endtime")) {
						endTimestamp = entry.getValue();			//will error-trap later
					} else if(arg.equals("classproperty")) {
						classAttribute = entry.getValue().toLowerCase();
					} else if(arg.equals("classtime")) {
						classBinTimestamp = entry.getValue();			//will error-trap later
					} else if(arg.equals("filterconfig")) {
						filterConfigFilePath = entry.getValue();
					} else if(arg.equals("feconfig")) {
						feConfigFilePath = entry.getValue();
					} else if(arg.equals("unkclass")) {
						if(entry.getValue().equalsIgnoreCase("true")) includeUnknownClass = true;
						else includeUnknownClass = false;
					} else if(arg.equals("excludecpropfromvector")) {
						if(entry.getValue().equalsIgnoreCase("true")) excludeClassPropFromFeatureVector = true;
						else excludeClassPropFromFeatureVector = false;
					} else if(arg.equals("incpid")) {
						if(entry.getValue().equalsIgnoreCase("true")) includePatientID = true;
						else includePatientID = false;
					} else if(arg.equals("inputdir")) {
						inputDirectory = entry.getValue();
					} else if(arg.equals("outputdir")) {
						outputDirectory = entry.getValue();
					} else if(arg.equals("arffname")) {
						outputArffPathAndName = entry.getValue();
					} else if(arg.equals("noOfReducers")) {
						noOfReducers = Integer.parseInt(entry.getValue());
					} else if(arg.equals("conceptFile")) {
						conceptDirectory = entry.getValue();
					} else {
						//not a switch or parameter... ???
						System.err.println("WARNING: skipping unrecognized input parameter \"" + arg + "\"");
					}
				}
			}
		}
		return sanityCheck(startTimestamp, endTimestamp);
	}
	
}	
