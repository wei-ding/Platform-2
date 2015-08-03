package org.clinical3PO.learn.main;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.TreeSet;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.clinical3PO.learn.util.C3POFilterConfiguration;
import org.clinical3PO.learn.util.C3POPatientPropertyMeasurement;
import org.clinical3PO.learn.util.C3POTimeRange;
import org.clinical3PO.learn.util.FEConfiguration;
import org.clinical3PO.learn.util.FEEvaluatorBase;
import org.clinical3PO.learn.util.FEStrategyBase;


public class FEMapper extends Mapper<LongWritable, Text, Text, Text> {

	
	
	/**
	 * constants for the positions of fixed-position tokens within the input lines
	 */
	public static final int NumRequiredTokens = 2;		//number of fixed-position tokens expected
	public static final int IDToken = 0;
	public static final int PropertyNameToken = 1;
	
	/* OLD WAY =====================================================================================================

	//number of bins - reckoning of bin will be harder if we don't have 48 (need to figure out which hour:minute
	//values go in each)
	public static final int DefaultNumBins = 48;
	public TreeMap<Integer,Vector<C3POPatientPropertyMeasurement>> bins;
	//classification feature - in this version, set with the -ca switch.
	//attribute/bin combination
	//also flag for whether output classification is continuous or discrete (high/low/normal)
	String classAttribute; 
	int classBin;
	boolean classContinuous;
	//values are ones we've found online for low and high diastolic; now we'll give range on cmdline
	double classMinNormal; // = 60.0;
	double classMaxNormal; //= 90.0;
	//dunno if we'd want to change these....
	String lowValue; // = "low";
	String normValue; // = "normal";
	String highValue; // = "high";
	String unkValue; // = "unk";		//MAY NOT ACTUALLY USE THIS
	C3POTimeRange globalTimeRange;
	boolean allowUnk;					//do we allow unknown-class instances? Does the mapper even worry about that?
    end OLD WAY ===================================================================================================== */

	//NEW AND USEFUL:
	//configuration files:
	C3POFilterConfiguration filtconf;
	FEConfiguration feconf;
	//command line configuration!
	FECmdLine cmdline;
	
	//keeping track of whether we've emitted anything for a given patient ID; if so, it will be in this set.
	//Use this to avoid emitting multiple dummy lines for a given patient (the dummy lines would be emitted when
	//processing a property that isn't in the filter config; it's possible that this mapper would emit nothing at all
	//for that patient otherwise - which might not be bad, but might also mean the patient isn't represented in the
	//final ARFF. For now, let's stay doing this.
	TreeSet<String> PIDsThatHaveOutput;

	/* OLD STUFF -------------------------------------------------------------------------------------
	// do I still even need this function?
	public void init() {
		//Not sure how to handle this - figure out if it's right to do it in ctor or what. At the moment calling from HandleLine which
		//will duplicate some minor effort AND IT'S ALL HARDCODEY
		classAttribute = null;
		classContinuous = false;
		classBin = -1;
		
		//dunno if we'd want to change these....
		lowValue = "low";
		normValue = "normal";
		highValue = "high";
		unkValue = "unk";		//MAY NOT ACTUALLY USE THIS
		allowUnk = false;
		globalTimeRange = null;
		
		filtconf = null;
		feconf = null;
		cmdline = null;
		
	}
	end OLD STUFF ------------------------------------------------------------------------------------*/
	
	//let's see if setup() is where to load config file and such
	@Override
	public void setup(Context context) {
		
		/* the following ends up doing this:
			Cache files:
			-- /uufs/chpc.utah.edu/common/home/u0176876/bigdata/Clinical3PO/Scrums/Scrum5/FETests/filterconfig1.txt
			local files:
			-- file:/tmp/hadoop-u0176876/mapred/local/1386883091171/filterconfig1.txt
		 * 
		 */
		
		//set up our set for whether we've emitted anything for a given PID to say we've not done any.
		PIDsThatHaveOutput = new TreeSet<String>();
		
		try {
			/* less interesting
			URI[] cachefiles = null;
			cachefiles = context.getCacheFiles();
			//see http://stackoverflow.com/questions/13746561/accessing-files-in-hadoop-distributed-cache
			
			System.err.println("Cache files:");
			for(URI uri:cachefiles) {
				System.err.println("-- " + uri);
				//let's see if I can open it...?
			}
			*/
			
			/*
			 * here's the deprecatey distcache way:
			 */
			Configuration conf = context.getConfiguration();
			
			//handle command line arguments from main
			System.err.println("Mapper getting command line: ---");
			cmdline = new FECmdLine();
			if(!cmdline.getCommandLineFromConfiguration(conf)) {
				System.err.println("Mapper ERROR: command line parse failed");
				cmdline = null;
			}
			
			//new, see if I can read the filtconf and feconf from strings given in conf
			//TODO LATER THERE WILL BE MULTIPLE FECONF?
			String filtconfContents;
			String feconfContents;
			InputStream is;
			
			if(conf.get("filtconfContents") != null) {
				filtconf = new C3POFilterConfiguration();
				filtconfContents = conf.get("filtconfContents");
				System.err.println("-- filter config contents: " + filtconfContents.length() + " chars");
				is  = new ByteArrayInputStream(filtconfContents.getBytes(Charset.forName("UTF-8")));
				if(filtconf.readPropertyConfiguration(new InputStreamReader(is))) {
					System.err.println("--- filter configuration read successfully! attrs");
					//DEBUG print out
					for(String attr:filtconf.getPropertiesUsed().keySet()) {
						System.err.print(attr);
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
			/* =====================================================================================
			 * TRYING TO AVOID DISTCACHE
			//read config files
			Path[] localFiles = DistributedCache.getLocalCacheFiles(conf);
			
			FileSystem fs = FileSystem.get(conf);
			FSDataInputStream fsdis = null;
			
			//null out configurations
			filtconf = null;
			feconf = null;
			
			//see if we can fetch filenames for the cmdline paths for config and feconfig files.
			
			//TODO DANGER LATER THERE WILL BE MULTIPLE FECONFIG FILES?

			System.err.println("local files:");
			for(Path path:localFiles) {
				
				//see if it's the filter config file
				String localFileName = path.getName();
				System.err.println("-- name |" + localFileName + "| path |" + path + "|");
				
				if(localFileName.equals(cmdline.filterConfigFileName)) {
					System.err.println("---- appears to be filter config; attempting to read");
					
					//HERE CHECK TO SEE IF WE'VE ALREADY READ ONE and if so yell
					//and if not try to read one
					if(filtconf == null) {
						filtconf = new C3POFilterConfiguration();
						fsdis = fs.open(path);
						if(filtconf.readPropertyConfiguration(new InputStreamReader(fsdis))) {
							System.err.println("--- filter configuration read successfully! attrs");
							//DEBUG print out
							for(String attr:filtconf.getPropertiesUsed().keySet()) {
								System.err.print(attr);
								if(filtconf.getPropertiesUsed().get(attr) != null) {
									//print out time range
									System.err.println(" " + filtconf.getPropertiesUsed().get(attr));
								} else {
									//blank, assume global time range
									System.err.println();
								}
								
							}
							
						} else {
							throw new Exception("ERROR: unable to read filter config file " + path);
						}
						fsdis.close();
						
					} else {
						throw new Exception("Mapper ERROR: multiple filter config files given");
					}
					
				} else if(localFileName.equals(cmdline.feConfigFileName)) {
					System.err.println("---- appears to be feature extraction config; attempting to read");
					
					//HERE CHECK TO SEE IF WE'VE ALREADY READ ONE and if so yell
					//and if not try to read one
					if(feconf == null) {
						feconf = new FEConfiguration();
						fsdis = fs.open(path);
						
						//TODO THERE WILL BE MORE THAN ONE OF THESE!
						//TODO THERE WILL BE MORE THAN ONE OF THESE!
						//TODO THERE WILL BE MORE THAN ONE OF THESE!
						//TODO THERE WILL BE MORE THAN ONE OF THESE!
						//TODO THERE WILL BE MORE THAN ONE OF THESE!
						if(feconf.accumulateFromTextConfigFile(fsdis)) {
							//DEBUG OUTPUT???
						} else {
							throw new Exception("ERROR: unable to read feature extraction config file " + path);
						}
						
						//check to make sure feconf is ok after all the accumulations done
						if(!feconf.validateAfterAccumulation(cmdline.globalTimeRange,filtconf,cmdline.classAttribute,cmdline.classBinTime)) {
							throw new Exception("ERROR: incorrect feature extraction configuration");
						}
						fsdis.close();
						
					} else {
						//TODO later this won't be a problem
						throw new Exception("Mapper ERROR: multiple feature extraction config files given");
					}
					
				} else {
					System.err.println("UNRECOGNIZED FILE, skipping");
				}
				
			}
			*/
			
		} catch (Exception e) {
			//error!
			System.err.println("ERROR retrieving mapper configuration");
			e.printStackTrace();
		}
		
		
	}

	/* OLD WAY =====================================================================================================
	public boolean addValueToBin(int binIndex, double value, String status) throws Exception {
		//see if there's a bin for this hour
		if(!bins.containsKey(Integer.valueOf(binIndex))) {
			bins.put(Integer.valueOf(binIndex),new Vector<C3POPatientPropertyMeasurement>());
		}
		
		//Add the new value/status to the bin for this hour
		Vector<C3POPatientPropertyMeasurement> vecvs = bins.get(Integer.valueOf(binIndex));
		
		//DANGER KLUDGE HORROR: I think this method is going to go away anyway, but for now need to patch
		//it by adding a timestamp. We'll just call it in seconds, 3600 * bin...?
		//DANGER KLUDGE HARDCODE

		vecvs.add(new C3POPatientPropertyMeasurement(value,status,3600*binIndex));
	
		return true;
	}

	
	//Copied from Scrum 2 C3POFilteredPatientData - will put the config stuff back in at some point.
	//
	// * handleTSValuePairs assumes handleLine has been called and has verified that there are an even
	// * number of tokens after the required ones
	// * 
	// * @param patientID - derived by caller (though for the moment it's also in toks)
	// * @param attr - "
	// * @param toks
	// * @param fcfg - filter configuration used for creating the given input file
	// * if null, use all attributes and all bins found? Sure. Includes global time range.
	// * @param context - hadoop context
	// * @return
	// * @throws Exception
	// * 
    //*********************************
    //10/22/13: NOTE FOR NOW I'M GOING TO DO HOUR BINS AND SAY WE TELL IT OUR GLOBAL RANGE IS 05:22 TO 07:17 I WILL 
    //REJECT VALUES EARLIER THAN 5:22 AND LATER THAN 7:16 BUT STILL HAVE BINS FOR 5:00-5:59, 6:00-6:59,
    //7:00-7:59 - ASK RE WHAT TO DO ABOUT THAT - like normalize so that the first bin is 5:22-6:21, etc?
    //*********************************
	// * 
	// * 
	//* 
	// *
	public boolean handleTSValuePairs(String patientID, String property, String[] toks, C3POFilterConfiguration fcfg, 
			Mapper<LongWritable, Text, Text, Text>.Context context) 
			throws Exception {
		//called by handleLine
		//since there is only one line bieng handled and we can do it all in here, we can just build the bins here, yes?
		//we will need to fetch from fcfg how many bins there are for the property
		//int numbins = DefaultNumBins;			/ derive from fcfg and a per-attr configuration but for now just use the default number
		bins = new TreeMap<Integer,Vector<C3POPatientPropertyMeasurement>>();
		
		//step by twos since these are ts/value pairs
		//so: hour is the bin we want to stick this in, minute can be discarded.
		//FIRST PASS we need to come up with the mean - so we need to know the individual values
		//and the numbers of them. Or really just running total and number of values - 
		//PER PATIENT. But! more compatible with eventual map/reduce version and alternate versions
		//that do max or min or whatever (or all of those!) 
		//this really just parses into a data structure which we then postprocess... woe.
		//sure, OK. 
		
		for(int j=C3POFilteredPatientData.NumRequiredTokens; j < toks.length; j+=2) {

			//new 10/22/13: if fcfg is non-null:
			//- if the attr/timestamp pair isn't "interesting" (attr isn't in the list or timestamp out of range)
			//  don't bother doing any of this.
			//toks[j] should be the timestamp.
			//remember to allow any attr if attrs used is null/empty
			//or rather:
			//if fcfg is non-null and its attrs-used non-null/empty, check for interestingness.
			//so, if fcfg IS null or its attrs-used null/empty, assume interesting.
			if(fcfg == null || fcfg.getPropertiesUsed() == null || fcfg.getPropertiesUsed().isEmpty() || fcfg.interesting(property, toks[j])) {
				
				//fetch ts/value numbers: format conversion from string throws exception if they're not legal
				//parse timestamp
				//LET'S STICK WITH THIS BECAUSE IT'S REALLY ABOUT BIN INDEX DERIVATION AND CURRENTLY 
				//WE'RE DOING HOUR BINS...
				String[] tstoks = toks[j].split(":",2);		//format is hour:minute, so split on colon
				int hour = Integer.valueOf(tstoks[0]);
				//int min = Integer.valueOf(tstoks[1]);
				double value = 0.0;
				//but here's the variable that will still be right semantically when we do not-necessarily-hour bins
				int binIndex;
				//for now, it's the same as hour.
				binIndex = hour;
				
				try {
					value = Double.valueOf(toks[j+1]);
					
					//record!
					
					//OK THIS NEEDS TO BE FIDDLED TO HANDLE UNKOWNS... or no, that's at arff-generation time
					//it's not unknown if we found it here.
					
					//put the newly found value in the bin (HEY ERROR TRAP)
					addValueToBin(binIndex, value, C3POPatientPropertyMeasurement.okFeatureStatus);
					
				} catch (NumberFormatException e) {
					//bad value; record an error status for it (HEY ERROR TRAP)
					addValueToBin(binIndex, -1.0, C3POPatientPropertyMeasurement.errorFeatureStatus);
				}
			}
				
		}

		return true;
	}
	end OLD WAY =====================================================================================================*/
	
	//separating out the core again to make it not need hadoop stuff.
	//this version returns the string we will use as the value in the output or null on error
	//actually returns PID + \t + that
	public String applyStrategyToLine(String[] toks, FEStrategyBase strat, int stratnum, 
			boolean isClass) throws Exception {
		//NOW THIS IS DONE WITH BINNER AND EVALUATOR! what do we need to do?
		ArrayList<C3POPatientPropertyMeasurement> measurements;
		long timestamp;
		Object value;
		StringBuffer valstr;
		
		String patientID = toks[IDToken];
		String propertyName = toks[PropertyNameToken];
		

		//OK. First, we have to convert all the timestamp/measurement pairs into 
		//an ArrayList of C3POPatientPropertyMeasurement objects.
		//and to do THAT we need to call a method on the strategy's valuetype, yes?
		//which takes a string (a token) and returns an Object that will be
		//stuck into the measurement.
		measurements = new ArrayList<C3POPatientPropertyMeasurement>();
		//so, stride through the tokens two by two, assuming that represents timestamp/value
		//pairs.
		//OMG I love how easy it is to do this with plugin-type classes! WOO!
		int p = 0;
		try {
			for(p = NumRequiredTokens;p<toks.length;p+=2) {
				timestamp = C3POTimeRange.interpretTimeStamp(toks[p]);
				value = strat.getValueType().getValueForString(toks[p+1]);
				measurements.add(new C3POPatientPropertyMeasurement(value, timestamp));
				//EXTREMELY VERBOSE DEBUG TODO RIP OUT
				//System.err.print(" ts: " + timestamp + " v: " + value);
			}
		} catch (Exception e) {
			System.err.println("FAILED to interpret timestamp/value pair \"" + toks[p] + "/" + toks[p+1] + "\" - check for extra files in FExtract input directory");
			throw(e);
		}
		//debug
		//System.err.println("\nFound " + measurements.size() + " measurements");
		
		// INVOKE BINNER!
		//System.err.println("Binning with a " + strat.getBinner().getClass() + "..."); //debug
		//memorize current time range in case we need to trample it for class kludge
		/* actually this should already be done in the class strategy. What on earth was I thinking with this?
		C3POTimeRange binnerRange = strat.getBinner().getRange();
		if(isClass) {
			//GROSS KLUDGE: replace the binner's range with a range from 0 to class timestamp+1 second
			//so that class timestamp is a legal time (remember end is exclusive)
			C3POTimeRange classRange = new C3POTimeRange(0,cmdline.classBinTime+1);
			//System.err.println("-- setting binner range to " + classRange);
			strat.getBinner().setRange(classRange);
		}
		*/
		
		strat.getBinner().clearBins();		// clear out any prior patients' data!
		strat.getBinner().accumulateBins(measurements);
		//System.err.println("Binner created " + strat.getBinner().getBins().size() + " bins overall, first used " + strat.getBinner().getFirstUsedBin()); //debug
		
		// INVOKE EVALUATOR!
		//System.err.println("Evaluating..."); //debug
		strat.getEvaluator().evaluate(strat.getBinner());
		
		//restore binner time range, if we trampled it for class - OR NOT, BECAUSE THAT WAS AWFUL
		if(isClass) {
			//but first fetch the bin that has the value we want in it
			int classvalbin = strat.getBinner().getBinForTime(cmdline.classBinTime);
			C3POPatientPropertyMeasurement classval = strat.getEvaluator().getValues().get(classvalbin); 		//had .value;
			
			//DEBUG:
			//System.err.println("Getting class bin for time " + cmdline.classBinTime + " -> " + classvalbin + " got " + classval);
			
			//VERBOSE DEBUG
			//System.err.println("class evaluator final values:" + strat.getEvaluator().toWekaString(strat.getBinner()));
			
			//emit special mapper line that has class property prefix prepended to prop name, then
			//the class value.
			valstr = new StringBuffer();
			valstr.append(FEConfiguration.classPropertyPrefix+propertyName);
			valstr.append("\t");
			valstr.append(stratnum);
			valstr.append("\t");
			
			//here interpret the class value.
			//was just valstr.append(classval.toString()); when classval was the .value; that caused trouble with unknowns
			//with null value.
			if(classval.status.equals(C3POPatientPropertyMeasurement.okFeatureStatus)) {
				valstr.append(classval.value.toString());
			} else if(classval.status.equals(C3POPatientPropertyMeasurement.unknownFeatureStatus)) {
				valstr.append(FEEvaluatorBase.defaultUnknownValue);		//???			
			} else if(classval.status.equals(C3POPatientPropertyMeasurement.errorFeatureStatus)) {
				valstr.append(FEEvaluatorBase.defaultErrorValue);		//???			
			} else {
				valstr.append(FEEvaluatorBase.defaultErrorValue);		//???			
			}
			//debug
			//System.err.println("Will emit line: |" + valstr.toString().substring(0,Math.min(80,valstr.length())) + "...|");
			
			//emit!!!!!!!!!!!!
			return patientID + "\t" + valstr.toString();
			//context.write(new Text(patientID), new Text(valstr.toString()));
			
			//GROSS KLUDGE: replace the binner's range with a range from 0 to class timestamp
			//System.err.println("-- restoring binner range to " + binnerRange);
			//if it was default binner, it would be null, yes? so set it to global range? Which I think
			//means do nothing? but trap against null because the range setter doesn't like that.
			//...no, let's set to global range? Feh.
			/* we shouldn't need to do this anymore.
			if(binnerRange != null) 
				strat.getBinner().setRange(binnerRange);
			else
				strat.getBinner().setRange(cmdline.globalTimeRange); 		//HOPEWORK
			*/
		} else {
			
			// TODO: I THINK NOW WE EMIT WHATEVER IT IS WE EMIT
			// might end up looking like 
			// key is patient ID
			// value is all of the following, tab-separated (construct into a StringBuffer called valstr)
			// - feature name without the bin number on it; so the same thing as I did for "attrBase"
			//   in the header writer - might out to make a function for this - whre prop
			//   is here known as propertyName and j is the same (index into strategies)
			//   String attrBase = prop + "_" +
			//		strat.getBinner().getFeatureNameInfix() + "_" +
			//		strat.getEvaluator().getFeatureNameInfix() + "_" +
			//		Integer.toString(j) + "_" +
			//		strat.getValueType().getFeatureNameInfix() + 
			//		"_bin";
			// - toWekaString() from the evaluator.
			// so... that way, what. how does the reducer use that, if we assume toWekaString is 
			// not internally tab-separated so it's just one big thing that can be written straight
			// to the output vector? We need to know order!
			// - actually, why not just emit propertyName tab j tab toWekaString()?
			//   the reducer would ... Hm.
			// - SHOULD I HAVE IT SO THE MAPPER JUST MAKES BINS AND REDUCER EVALUATES THEM?
			// - the argument for that is that mapper won't know which features are missing
			//   from this patient, and the reducer needs to have the binner and evaluator...
			// - I think I can do this:
			//   - bin and evaluate here, generating one line each for key = pid,
			//     value = propertyname \t strat# \t wekaString.
			//   - then reducer will get some number of those for the patient, and I'll
			//     need to see how that breaks up - if I should use the tab-sep or something
			//     else so the reducer just gets one big bar of stuff for each property/strategy.
			//     (looking at current mapper, I do the split on tab explicitly, so it should be ok.)
			//   - reducer does like ArffHeaderMaker and steps through filtconf.getPropertiesInOrder(),
			//     and for each of those steps through the strategies.
			//     - if there is an entry for that property and strategy number, append its weka string
			//     - if not, null out the values in the evaluator for it and call toWekaString() on it
			//       to get the missing-property string (currently all ?)
			// but after all that, here's what we'll write out:
			// context.write(new Text(patientID), new Text(valstr.toString()));
			valstr = new StringBuffer();
			valstr.append(propertyName);
			valstr.append("\t");
			valstr.append(stratnum);
			valstr.append("\t");
			valstr.append(strat.getEvaluator().toWekaString(strat.getBinner()));
			//debug
			//System.err.println("Will emit line: |" + valstr.toString().substring(0,Math.min(80,valstr.length())) + "...|");
			
			//emit!!!!!!!!!!!!
			return patientID + "\t" + valstr.toString();
			//context.write(new Text(patientID), new Text(valstr.toString()));
		}		
		//return null;
	}
	
	/*
	//separated the core of handleLine from it to make it easier to do the class strategy
	public boolean applyStrategyToLine(String[] toks, FEStrategyBase strat, int stratnum, 
			boolean isClass, Mapper<LongWritable, Text, Text, Text>.Context context) throws Exception {
		
		//so: run the core of this to get out the feature vector subset string we expect.
		String lineResult = applyStrategyToLineCore(toks, strat, stratnum, isClass);
		
		if(lineResult != null) {
			//what we got back is key + \t + value 
			String[] keyval = lineResult.split("\t",2);
			context.write(new Text(keyval[0]), new Text(keyval[1]));
			return true;
		}
		
		return false;
	}
	*/
	
	//Function taken out of C3POScrum2ArffGen
	//refactoring to remove the hadoop-dependent stuff from it and make it more JUnit-tractable
	//now returns string of key + \t + value - may return multiple, if this is the class property, so do ArrayList, null on error
	public ArrayList<String> handleLine(String[] toks) throws Exception {
		//assume caller avoids blanks and nulls getting here
		//break it up by \s+ and handle it
		//caller does this now
		//String[] toks = theLine.split("\\s+");
		
		//TEMP HERE SET SOME VALUES WHICH SHOULD BE SET BY CONFIG STUFF SOMEHOW ******************************************************************************
		//OLD WAY
		//init();
		
		//not sure the >= is right here; if it's =, there are no measurements.
		if(toks.length >= NumRequiredTokens) {
			
			//sanity check - there should be an even number of tokens after the required
			if(((toks.length - NumRequiredTokens) % 2) == 0) {
				//deal with it - split off ID and feature
				String patientID = toks[IDToken];
				String propertyName = toks[PropertyNameToken];
				
				//Debug: VERBOSE RIP OUT
				//System.err.println("Doing handleLine for PID " + patientID + " prop " + propertyName);
				
				/* OLD:
				//here record the feature in featuresFound - this is massively redundant
				//but that's what TreeMaps are all about - boil the redundant down to unique.
				//debug RIP OUT 
				//if(!featuresFound.containsKey(featureName))	System.err.println("Found new feature: " + featureName);
				//DANGER! FOR NOW, 
				featuresFound.put(featureName, "NUMERIC");
				*/
				
				//then digest the ts/val pairs
				//OLD WAY
				//handleTSValuePairs(patientID, featureName, toks, fcfg, context);

				//prepare to return results.
				ArrayList<String> results = new ArrayList<String>();
				
				
				//*************************************************************************************************************
				//*************************************************************************************************************
				//*************************************************************************************************************
				//SEAN NOTES: HERE is a place that I'd need to modify if we want to have it so that a given property 
				//is used as the class but not in the main feature vector.
				//and now that's flagged by cmdline.excludeClassPropFromFeatureVector = true.
				//*************************************************************************************************************
				//*************************************************************************************************************
				//*************************************************************************************************************
				
				
				
				ArrayList<FEStrategyBase> strats = feconf.getStrategiesForProperty(propertyName);
				
				//debug verbose TODO RIP OUT
				//System.err.println("Found " + strats.size() + " strategies");
				
				for(int j=0;j<strats.size();j++) {
					//the if() here checks to make sure we don't add class property bins to vector if we've
					//suppressed that through cmdline.excludeClassPropFromFeatureVector = true.
					if(!cmdline.excludeClassPropFromFeatureVector || !propertyName.equals(cmdline.classAttribute)) {
						FEStrategyBase strat = strats.get(j);
						String res1=applyStrategyToLine(toks, strat, j, false); 
						if(res1 == null) {
							throw new Exception("Failed to apply strategy " + j + " to pid " + patientID + " prop " + propertyName);
						}
						//got a legit result, add it to our output!
						results.add(res1);
					} else {
						//debug - quite verbose! Rip out
						System.err.println("NOTE SKIPPING feature vector segment for class property " + cmdline.classAttribute);
					}
				}
				
				//*************************************************************************************************************
				//*************************************************************************************************************
				//*************************************************************************************************************
				//SEAN NOTES: end HERE is a place that I'd need to modify if we want to have it so that a given property 
				//is used as the class but not in the main feature vector.
				//*************************************************************************************************************
				//*************************************************************************************************************
				//*************************************************************************************************************

				
				//now see if we're handling the class strategy, in which case handle it.
				if(propertyName.equals(cmdline.classAttribute)) {
					//System.err.println("-- handling class property");	//debug
					FEStrategyBase strat = feconf.getClassStrategyForProperty(propertyName);
					if(strat == null) {
						throw new Exception("No class strategy found for property " + propertyName);
					}
					
					String res2 = applyStrategyToLine(toks, strat, 0, true);
					
					if(res2 == null) {
						throw new Exception("Failed to apply class strategy to pid " + patientID + " prop " + propertyName);
					}
					
					//got a legit result, add it to our output!
					results.add(res2);
					
				}
				
				
				//and report!
				return results;
			} else {
				//??? badly formed - for now, warn
				System.err.println("S3MRATMapper.handleLine WARNING: line with odd number of tokens after required");
				for(String tok:toks) System.err.print(tok + " "); System.err.println();
			}
			
		} else {
			//??? badly formed - for now, warn
			System.err.println("S3MRATMapper.handleLine WARNING: line with fewer than " + NumRequiredTokens + " tokens:");
			for(String tok:toks) System.err.print(tok + " "); System.err.println();
		}
		
		//Something went wrong... but not really an error? Return empty arraylist
		return new ArrayList<String>();
	}
	
	/* OLD WAY =========================================================================================================
	//based on emitARFFLine from the older versions, emitPropertyBins emits the patientID / "propname\tbin1\tbin2..." pair
	//s.t. for that patient we have all the bins for the given property.
	public boolean emitPropertyBins(String patientID, String propertyName, C3POFilterConfiguration cfcg,boolean allowUnk,
			org.apache.hadoop.mapreduce.Mapper<LongWritable, Text, Text, Text>.Context context) throws Exception {
		// this will write a mapping of patient ID to a string with, say, property name then tab-separated values for all the bins
		// THIS IS GOING TO BE HUGELY OVERHAULED WRT the emitARFF used in scrum 2 so as to accommodate a feature generation config.
		//for now, just write out 48 bins. if there isn't a value for the bin, write a "?"
		StringBuffer valstr = new StringBuffer();
		
		//so: emit property name to our value string
		valstr.append(propertyName);
		
		//then all the bin values, prepending a tab so there are tabs between everything and not one on the end 
		// REPLACE THIS WITH REAL CALCULATIONS OF BIN VALUES~!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		//will end up using e.g. C3POPatientAttributeValueStatus val = null;
		for(int j=0;j<DefaultNumBins;j++) {		//DANGER HARDCODEY FIXX
			valstr.append("\t");		//Add tab separator before bin value
			if(bins.containsKey(Integer.valueOf(j))) {
				String theval = "?";
				double bintot = 0.0;
				Vector<C3POPatientPropertyMeasurement> pavsvec = bins.get(Integer.valueOf(j));
				//emit "?" for empty values vector or any erroneous value among them, average of values otherwise
				if(pavsvec != null && !pavsvec.isEmpty()) {
					boolean foundbad = false;
					for(C3POPatientPropertyMeasurement pavs:pavsvec) {
						if(!pavs.status.equals(C3POPatientPropertyMeasurement.okFeatureStatus)) {
							foundbad = true;
							break;
						}
						bintot += ((Double)(pavs.value)).doubleValue();
					}
					if(!foundbad) {
						theval = Double.toString((bintot / (double)pavsvec.size()));		//emit average; if foundbad is true, value will remain "?"
					}
				}
				valstr.append(theval);
			} else {
				//no values recorded for this bin, emit "?" - TODO FIX LATER
				valstr.append("?");
			}
		}
		
		context.write(new Text(patientID), new Text(valstr.toString()));		//hopework.
		return true;
	}
	end OLD WAY =====================================================================================================*/

	//continuing refactor to make JUnit-friendly
	//null on error, empty result if there just isn't anything to emit
	public ArrayList<String> processLine(String theLine) throws Exception {
		String[] toks = theLine.split("\\s+");

		//there need to be at least enough tokens in the line for there to be a property name.
		//THIS WAS >= AND SHOULD BE >!!!!!!!!
		if(toks.length > PropertyNameToken) {
			//String patientID = toks[IDToken];
			String propertyName = toks[PropertyNameToken];		
			//property is included IF it is explicitly included 
			//config is null OR config has null attrs used list OR config attrs used list is empty
			//but now we disallow null filter config. so: let's require that filter config uses the property.
			if(filtconf.usesProperty(propertyName)) {
				return handleLine(toks);
			} else {
				//not a property we're interested in, but not an error; return an empty array list.
				return new ArrayList<String>();
			} 
		} else {
			//DANGER EXTREMELY VERBOSE possibly
			System.err.println("Mapper WARNING: line too short - needed " + (PropertyNameToken+1) + " tokens or more, got " + toks.length + " - check for extraneous files in FExtract input directory");
			System.err.println("Line was \"" + theLine + "\"");
		}
		
		//error...?
		return null;
	}
		
	@Override
	public void map(LongWritable key, Text value,
			org.apache.hadoop.mapreduce.Mapper<LongWritable, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		//super.map(key, value, context);
		
		//OK so: We get in a line that looks like this:
		//140500      hr      47:22      58      40:22      65      27:22      89      04:22      85 
		//ie pid property timestamp1 value1 timestamp2 value2 ...
		//all ws-delimited (I used \\s+ before)
		//so looking in scrum 2's C3POFilteredPatientData and will copy functions from it
		
		//don't bother with any of this if the attribute isn't in our property selection configuration.
		//- if the attr isn't in fcfg, bail.
		//ACTUALLY SPECIAL CASE IF USESATTR IS EMPTY OR NULL, ALLOW ANY. (this is how no-config-loaded is handled in arffgen2 - might want a better
		//way to express it...)
		//so actually. if fcfg == null, or attrs used is null/empty, accept; otherwise usesAttribute(attr) must be so.
		//HOW TO DO THIS SO WE DON'T SPLIT THE STRING A BUNCHA TIMES?
		//split here, then hand the array over.
		
		String theLine = value.toString();
		
		try {
			ArrayList<String> processLineResults = processLine(theLine);
			
			if(processLineResults != null) {
				if(processLineResults.isEmpty()) {
					//gross... need to fetch pid; if we got non-null, there is one.
					String[] toks = theLine.split("\\s+");
					String patientID = toks[IDToken];
					
					//System.err.println("-- pid " + patientID + " empty handleline");
					
					//Emit a dummy line to output or it's possible nothing at all will get written for this patient!
					//TODO MAKE A SET<STRING> OF pids that have had anything emitted for them, dummy or not, and
					//don't emit a dummy if this pid is in there
					if(!PIDsThatHaveOutput.contains(patientID)) {
						context.write(new Text(patientID), new Text(C3POFilterConfiguration.dummyPropertyName+"\t0\tx"));
					}
				} else {
					//non-empty. Emit our lines, which are key + \t + value
					for(String lineResult:processLineResults) {
						String[] keyval = lineResult.split("\t",2);
						//System.err.println("-- pid " + keyval[0] + " handleline result: |" + keyval[1] + "|");
						context.write(new Text(keyval[0]), new Text(keyval[1]));
					}
				}
			
			} else {
				//error! 
				System.err.println("ERROR: null processLineResults");
			}
		} catch (Exception e) {
			//ERROR do what?
			e.printStackTrace();
			System.exit(1);
			
		}
		
		/* OLD WAY 
		//snip -------------------------------------------------------------------------
		String[] toks = theLine.split("\\s+");

		//there need to be at least enough tokens in the line for there to be a property name.
		if(toks.length >= PropertyNameToken) {
			String patientID = toks[IDToken];
			String propertyName = toks[PropertyNameToken];		
			//property is included IF it is explicitly included 
			//config is null OR config has null attrs used list OR config attrs used list is empty
			//but now we disallow null filter config. so: let's require that filter config uses the property.
			if(filtconf.usesProperty(propertyName)) {
				try {
					//VERBOSE DEBUG TODO RIP OUT
					//System.err.println("--- mapper handling line for pid " + patientID + " property " + propertyName);
					
					ArrayList<String> handleLineResults = handleLine(toks);
					
					if(handleLineResults != null) {
						//OLD WAY: emitPropertyBins(patientID,propertyName,fcfg,allowUnk,context);
						//that's now handled in handleLine.
						//so ... I don't think anything needs to be done.
						//except to say we've written something for this pid.
						for(String lineResult:handleLineResults) {
							String[] keyval = lineResult.split("\t",2);
							context.write(new Text(keyval[0]), new Text(keyval[1]));
						}
						
						//don't do this if results was non-null but empty?
						if(!handleLineResults.isEmpty()) {
							PIDsThatHaveOutput.add(patientID);
						}
					} else {
						//TODO: handleLine failed. Do what?
						throw new Exception("HandleLine failed");
					}
				} catch (Exception e) {
					//ERROR do what?
					e.printStackTrace();
					System.exit(1);
				}
			} else {
				//this isn't a property we're interested in. Do we need to do anything?
				//System.err.println("Property " + propertyName + " not interesting. Skipping");
				//Emit a dummy line to output or it's possible nothing at all will get written for this patient!
				//TODO MAKE A SET<STRING> OF pids that have had anything emitted for them, dummy or not, and
				//don't emit a dummy if this pid is in there
				if(!PIDsThatHaveOutput.contains(patientID)) {
					context.write(new Text(patientID), new Text(C3POFilterConfiguration.dummyPropertyName+"\t0\tx"));
				}
			} 
		} else {
			System.err.println("Mapper WARNING: line too short - needed " + PropertyNameToken + " or more, got " + toks.length);
		}
		*/
	}
	
}
