package org.clinical3PO.learn.main;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.clinical3PO.learn.util.C3POFilterConfiguration;
import org.clinical3PO.learn.util.C3POPatientPropertyMeasurement;
import org.clinical3PO.learn.util.C3POTimeRange;
import org.clinical3PO.learn.util.FEConfiguration;
import org.clinical3PO.learn.util.FEEvaluatorBase;
import org.clinical3PO.learn.util.FEStrategyBase;

/**
 	First pass dummy reducer:
    - so dummy reducer could just do something like this: it receives all those tab-sep property/binvalue strings for each patient ID, yes?
    - create a treemap<string,string>
    - for each string, make an entry to that treemap 
      - split by tab
      - key = token[0] (propname)
      - value = rest of tokens joined by ","
    - then iterate through the keys of the treemap (IN REAL VERSION THIS WILL INSTEAD ITERATE THROUGH A LIST OF THE PROPERTIES WE WANT,
      gleaned either from a property selection file or scanning the input patient data or something. But for now, don't worry about having
      all the lines have the same bunch of stuff?)
 ***** REMEMBER that if we have multiple reducers there might be gaps (multiple reducers emitting parts of the patient vector.) So, if
      there's a prop missing from the input, don't stew or error, just emit like x,x,x, for its number of bins - ? How do we know
      how many bins? Also from the global feature profile, however that is communicated.
    - emit those; output is PID as key and whole feature vector (all the comma-sep bin value strings in the map, themselves separated by commas)
      - that will be very close to arff format, postprocess can knit together the output files and reconcile multiple incomplete vectors for a 
        given PID, etc.
 */

public class FEReducer extends Reducer<Text, Text, Text, Text> {

	C3POFilterConfiguration filtconf;
	FEConfiguration feconf;
	FECmdLine cmdline;

	private static final int NumRequiredTokens = 2;		//number of fixed-position tokens expected
	private static final int IDToken = 0;
	private static final int PropertyNameToken = 1;

	private static final String regexp = "[\\s,;]+";
	private TreeSet<String> PIDsThatHaveOutput;
	private HashMap<String, String> conceptProperties_Map;

	private Text key_ = null;
	private Text value_ = null;


	@Override
	public void setup(Context context) {

		key_ = new Text();
		value_ = new Text();

		PIDsThatHaveOutput = new TreeSet<String>();
		conceptProperties_Map = new HashMap<String, String>();

		try {

			Configuration conf = context.getConfiguration();

			//handle command line arguments from main
			System.err.println("Reducer getting command line: ---");
			cmdline = new FECmdLine();
			if(!cmdline.getCommandLineFromConfiguration(conf)) {
				System.err.println("Reducer ERROR: command line parse failed");
				cmdline = null;
			}

			// method call
			loadAndMatchObsevationIdsWithConceptIds(conf);


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
		} catch (Exception e) {
			//error!
			System.err.println("ERROR retrieving reducer configuration");
			e.printStackTrace();
		}
	}

	/**
	 * In order to pick observation attribute based on the number from observation file, reading concept file 
	 * and storing first two attributes(numeric-value, observation-name) in the map as key-value pairs.
	 * 
	 * @param conf
	 * @throws IOException
	 */
	private void loadAndMatchObsevationIdsWithConceptIds(Configuration conf) throws IOException {

		String line = null;
		FileSystem fs = null;
		BufferedReader reader = null;

		try {
			fs = FileSystem.get(conf);
			reader = new BufferedReader(new InputStreamReader(fs.open(new Path(cmdline.conceptDirectory))));

			line = reader.readLine(); // Since there's header in line-1, ignoring it
			line = reader.readLine();
			String[] conceptTokens = null;
			while(line != null && !line.isEmpty()) {

				conceptTokens = line.split(regexp);
				conceptProperties_Map.put(conceptTokens[0], conceptTokens[1].toLowerCase());
				line = reader.readLine();
				conceptTokens = null;
			}
		} catch(IOException e) {
			System.err.print(e);
		} finally {
			if(reader != null)
				reader.close();
		}
	}

	private StringBuilder builder = new StringBuilder();

	@Override
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

		String[] units = key.toString().split("~");
		builder.append(units[0]+ " "+units[1]).append(" ");
		for(Text val:values) {
			builder.append(val).append(" ");
		}

		String[] toks = builder.toString().split(regexp);
		builder.delete(0, builder.length());

		ArrayList<String> processLineResults = null;
		try {
			processLineResults = processLine(toks);

			if(processLineResults != null) {
				if(processLineResults.isEmpty()) {
					//gross... need to fetch pid; if we got non-null, there is one.
					String patientID = toks[IDToken];

					//Emit a dummy line to output or it's possible nothing at all will get written for this patient!
					//TODO MAKE A SET<STRING> OF pids that have had anything emitted for them, dummy or not, and
					//don't emit a dummy if this pid is in there
					if(!PIDsThatHaveOutput.contains(patientID)) {
						//							System.err.println(patientID +" " + C3POFilterConfiguration.dummyPropertyName+"\t0\tx");
					}
				} else {
					String[] output = reducerProcessing(processLineResults).split("\\s");
					processLineResults = null;

					key_.set(output[0]);
					value_.set(output[1]);
					context.write(key_, value_);
					key_.clear();
					value_.clear();
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
	}

	private String reducerProcessing(ArrayList<String> listOfProcessedData) throws Exception { 

		//new way: What we get in should be like this:
		//key = patient ID
		//values = strings that consist of
		//  property name \t strategy # \t weka string
		//  - special case strategy number might be "class", in which case what we have is the class value.
		//    see below re: its handling.
		//   - reducer does like ArffHeaderMaker and steps through filtconf.getPropertiesInOrder(),
		//     and for each of those steps through the strategies.
		//     - if there is an entry for that property and strategy number, append its weka string
		//     - if not, null out the values in the evaluator for it and call toWekaString() on it
		//       to get the missing-property string (currently all ?)

		//so first, build us something like a TreeMap of property name to 
		//TreeMap of strategy to weka String? Sure, let's try that.
		//IS IT MEMORY-PROHIBITIVE? let's worry about that later when we get BIG data.
		//I think this gets called for each patient, right? So unless our vectors our huge, this
		//is not going to be a big problem.
		Map<String,HashMap<String,String>> propMap = new HashMap<String, HashMap<String,String>>();  

		//WE CAN'T DO THE INCLUDE-UNKNOWN-CLASS DETERMINATION AT THIS POINT - IT HAS TO BE DONE AT RECONCILE TIME.
		//boolean skipThisInstance = false;		//for use when we have unknown-class instance and are not emitting those

		String key = null;
		String val = null;
		for(String lineResult:listOfProcessedData) {
			String[] keyval = lineResult.split("\t",2);
			key = keyval[0];
			val = keyval[1];

			//CURRENTLY NO ERROR TRAPPING
			String[] toks = val.split("\\t");
			//so, toks[0] = property name, toks[1] = strategy # or "class", toks[2] = its weka vector subset
			//but that might change so let's not hardcode
			String propName = toks[0];
			String stratNum = toks[1];
			String wekaStr = toks[2];
			if(!propMap.containsKey(propName)) {
				propMap.put(propName, new HashMap<String,String>());
			}
			//dummy property can occur more than once (and will a lot.) 
			if(propMap.get(propName).containsKey(stratNum) && !propName.equals(C3POFilterConfiguration.dummyPropertyName)) {
				throw new InterruptedException("Strategy number " + stratNum + " occurs more than once for property " + propName);
			}
			propMap.get(propName).put(stratNum, wekaStr);
		}

		StringBuffer instanceLine = new StringBuffer();

		//HERE if we're going to emit patient ID we should emit patient ID. If this is done
		//by multiple reducers, they'll collide, but they'll all be the same value
		//surround with quotes because who knows what this is going to be, and we're currently
		//planning to have it be a string. Maybe do replacement of " with \" ... TEST THIS
		//ACTUALLY we'll write them as numbers. ASSUME THEY'RE LEGIT NUMBERS.
		if(cmdline.includePatientID) {
			//string way with quotes and escaped quotes instanceLine.append("\"" + key.toString().replaceAll("\"", "\\\"") + "\",");
			//number way - TODO later make a mapping thing from PID->index?
			//FOR NOW ASSUME THEY'RE LEGIT - or try to force to integer...? That'll throw an exception if
			//it doesn't work.
			instanceLine.append(key.toString() + ",");
		}

		//THEN we can do that iteration a la a header maker

		for(String prop:filtconf.getPropertiesInOrder()) {

			//****************************************** I THINK THIS IS WHERE TO EXCLUDE THE
			//******* CLASS PROPERTY IF cmdline.excludeClassPropFromFeatureVector is true
			if(!cmdline.excludeClassPropFromFeatureVector || !prop.equals(cmdline.classAttribute)) {

				//System.err.println("- Handling property |" + prop + "|");
				ArrayList<FEStrategyBase> strats = feconf.getStrategiesForProperty(prop);
				for(int j=0;j<strats.size();j++) {
					FEStrategyBase strat = strats.get(j);
					if(!propMap.containsKey(prop) || !propMap.get(prop).containsKey(Integer.toString(j))) {
						//TODO Fetch the all-x or all-? or whatever weka string of the appropriate length
						//and append it to instanceLine
						//kind of kludgy
						strat.getEvaluator().setValues(null);
						String wekstr = strat.getEvaluator().toWekaString(strat.getBinner());
						instanceLine.append(wekstr);
					} else {
						//we have a weka-string for this subset of the vector! put it in.
						instanceLine.append(propMap.get(prop).get(Integer.toString(j)));
					}
					//add a comma after. There won't be one after the last value, bc that's the class,
					//emitted below.
					instanceLine.append(",");
				}
			} else {
				//verbose debug
				System.err.println("NOTE SKIPPING feature vector segment for class property " + cmdline.classAttribute);
			}
		}

		//THEN APPEND CLASS
		//mapper should have sent along a line that has the special class name 
		//(class property prefix + class property name given on command line) plus a zero for strategy
		//plus the class value.
		String classProp = FEConfiguration.classPropertyPrefix+cmdline.classAttribute;
		//let's say it's an error if one wasn't made? Or no, let's emit an unk? I suppose it should be a 
		//settable strategy.
		//TODO FIGURE OUT WHAT TO DO 
		//TODO FIGURE OUT WHAT TO DO 
		//TODO FIGURE OUT WHAT TO DO 
		if(!propMap.containsKey(classProp)) {
			instanceLine.append("?");

			/* ACTUALLY WE CAN'T KNOW AT THIS POINT THAT THE FEATURE VECTOR IS COMPLETE - THE POSTPROCESS THAT
			 * RECONCILES LINES NEEDS TO HANDLE THIS.
				if(cmdline.includeUnknownClass) {
					System.err.println("WARNING: patient ID " + key.toString() + " has no class value: using \"?\"");
				} else {
					skipThisInstance = true;
				}
			 */
		} else {
			//TODO DANGER HARDCODED ZERO STRATEGY
			instanceLine.append(propMap.get(classProp).get("0"));
		}
		return key+" "+instanceLine.toString();
	}

	//separating out the core again to make it not need hadoop stuff.
	//this version returns the string we will use as the value in the output or null on error
	//actually returns PID + \t + that
	private String applyStrategyToLine(String[] toks, FEStrategyBase strat, int stratnum, boolean isClass) throws Exception {

		//NOW THIS IS DONE WITH BINNER AND EVALUATOR! what do we need to do?
		ArrayList<C3POPatientPropertyMeasurement> measurements;
		long timestamp;
		Object value;
		StringBuffer valstr;
		String patientID = toks[IDToken];
		String propertyName = conceptProperties_Map.get(toks[1]);


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

	//Function taken out of C3POScrum2ArffGen
	//refactoring to remove the hadoop-dependent stuff from it and make it more JUnit-tractable
	//now returns string of key + \t + value - may return multiple, if this is the class property, so do ArrayList, null on error
	private ArrayList<String> handleLine(String[] toks) throws Exception {

		//sanity check - there should be an even number of tokens after the required
		if(((toks.length - NumRequiredTokens) % 2) == 0) {
			//deal with it - split off ID and feature
			String patientID = toks[IDToken];
			String propertyName = conceptProperties_Map.get(toks[1]);

			if(patientID != null && propertyName != null) {

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
						String res1 = applyStrategyToLine(toks, strat, j, false); 
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
		}

		//Something went wrong... but not really an error? Return empty arraylist
		return new ArrayList<String>();
	}

	//continuing refactor to make JUnit-friendly
	//null on error, empty result if there just isn't anything to emit
	private ArrayList<String> processLine(String[] toks) throws Exception {

		//there need to be at least enough tokens in the line for there to be a property name.
		//THIS WAS >= AND SHOULD BE >!!!!!!!!
		if(toks.length > PropertyNameToken) {

			String propertyName = conceptProperties_Map.get(toks[1]);

			if(propertyName != null) {

				//property is included IF it is explicitly included 
				//config is null OR config has null attrs used list OR config attrs used list is empty
				//but now we disallow null filter config. so: let's require that filter config uses the property.
				if(filtconf.usesProperty(propertyName)) {
					return handleLine(toks);
				} else {
					//not a property we're interested in, but not an error; return an empty array list.
					return new ArrayList<String>();
				} 
			}
		} else {
			//DANGER EXTREMELY VERBOSE possibly
			System.err.println("Mapper WARNING: line too short - needed " + (PropertyNameToken+1) + " tokens or more, got " + toks.length + " - check for extraneous files in FExtract input directory");
			//			System.err.println("Line was \"" + theLine + "\"");
		}

		//error...?
		return null;
	}
}
