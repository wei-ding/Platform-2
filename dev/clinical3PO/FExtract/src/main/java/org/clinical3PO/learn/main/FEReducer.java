package org.clinical3PO.learn.main;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.TreeMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.clinical3PO.learn.util.C3POFilterConfiguration;
import org.clinical3PO.learn.util.FEConfiguration;
import org.clinical3PO.learn.util.FEStrategyBase;

/*
 * First pass dummy reducer:
 *   - so dummy reducer could just do something like this: it receives all those tab-sep property/binvalue strings for each patient ID, yes?
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
	
	//NEW AND USEFUL:
	//configuration files:
	C3POFilterConfiguration filtconf;
	FEConfiguration feconf;
	//command line configuration!
	FECmdLine cmdline;
	
	/* OLD STUFF =====================================================================================================
	TreeMap<String,String> binvals; 	//property name -> string consisting of comma-separated bin values for all bins for that property
	Vector<String> propList;			//list of the properties we expect for a patient
	
	//helper function similar to perl "join" - given an array of strings, returns a single string with all of those
	//strings stuck together with given separator in between (and not before or after.)
	//skip is how many tokens at the beginning of the array to skip (0 for all of them; this is meant for use with
	//reduce  below handing in the entire array with propname, value, value, value... and joining up just the values.
	//heh, I though i would use this for constructing the feature lines too, but I will end up assembling them on the fly
	//this might be useful later, though
	protected String join(String[] toks, String separator, int skip) {
		if(toks.length <= skip) return "";
		StringBuffer buf = new StringBuffer();
		for(int j=skip;j<toks.length;j++) {
			buf.append(toks[j]);
			if(j!=toks.length-1) buf.append(separator);
		}
		return buf.toString();
	}
	
	@Override
	public void setup(Context context) {
		try {
			Configuration conf = context.getConfiguration();
			//handle command line arguments from main
			System.err.println("Reducer getting command line: ---");
			cmdline = new MRATCmdLine();
			if(!cmdline.getCommandLineFromConfiguration(conf)) {
				System.err.println("Reducer ERROR: command line parse failed");
				cmdline = null;
			}
		} catch (Exception e) {
			System.err.println("DANGER: reducer setup() failed");
			e.printStackTrace();
		}
	}
	end OLD STUFF =================================================================================================== */

	
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
			System.err.println("Reducer getting command line: ---");
			cmdline = new FECmdLine();
			if(!cmdline.getCommandLineFromConfiguration(conf)) {
				System.err.println("Reducer ERROR: command line parse failed");
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
			

			/* OLD DEPRECATEY WAY
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
						throw new Exception("Reducer ERROR: multiple filter config files given");
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
						throw new Exception("Reducer ERROR: multiple feature extraction config files given");
					}
					
				} else {
					System.err.println("UNRECOGNIZED FILE, skipping");
				}
				
			}
			
			*/
		} catch (Exception e) {
			//error!
			System.err.println("ERROR retrieving reducer configuration");
			e.printStackTrace();
		}
		
	}
	
	
	public void reduce(Text key, Iterable<Text> values, org.apache.hadoop.mapreduce.Reducer<Text, Text, Text, Text>.Context context)
		throws IOException, InterruptedException {
		
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
		TreeMap<String,TreeMap<String,String>> propMap = new TreeMap<String,TreeMap<String,String>>();  
		
		//WE CAN'T DO THE INCLUDE-UNKNOWN-CLASS DETERMINATION AT THIS POINT - IT HAS TO BE DONE AT RECONCILE TIME.
		//boolean skipThisInstance = false;		//for use when we have unknown-class instance and are not emitting those
		
		for(Text val:values) {
			//CURRENTLY NO ERROR TRAPPING
			String[] toks = val.toString().split("\\t");
			//so, toks[0] = property name, toks[1] = strategy # or "class", toks[2] = its weka vector subset
			//but that might change so let's not hardcode
			String propName = toks[0];
			String stratNum = toks[1];
			String wekaStr = toks[2];
			if(!propMap.containsKey(propName)) {
				propMap.put(propName, new TreeMap<String,String>());
			}
			//dummy property can occur more than once (and will a lot.) 
			if(propMap.get(propName).containsKey(stratNum) && !propName.equals(C3POFilterConfiguration.dummyPropertyName)) {
				throw new InterruptedException("Strategy number " + stratNum + " occurs more than once for property " + propName);
			}
			propMap.get(propName).put(stratNum, wekaStr);
		}
		
		try {
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
			
			
			//always write instance line - we can't know whether the instance needs to be skipped, yet.
			//if(!skipThisInstance) {
				context.write(key, new Text(instanceLine.toString()));
			//}
			
			
		} catch (Exception e) {
			//TODO ERROR
			System.err.println("Error in reducer!");
			e.printStackTrace();
		}

		
		/* OLD STUFF =====================================================================================================

		//clear out our Treemap... might be a better way to do this like new it in a ctor, and just empty it here, but for now.
		binvals = new TreeMap<String,String>();
		
		//first version - and probably subsequent versions - for each of the strings in values, split on tab, use the first token
		//as the key into the TreeMap (it should be the property name) and a joining of the rest by "," as the value
		for(Text val:values) {
			//CURRENTLY NO ERROR TRAPPING
			String[] toks = val.toString().split("\\t");
			binvals.put(toks[0], join(toks,",",1));		//toks[0] = property name, all others are the values
		}
		
		//first version - just step through the keys in binvals. later this will be driven by some list of the properties we
		//want in the order we want them. We will store this in the vector propList, say.
		Vector<String> propList = new Vector<String>(binvals.keySet());		// WILL BE REPLACED BY A READING FROM A CONFIG FILE or something
		
		//so: this loop should be able to go unchanged from first test version to real ones - or at least less changed than it might have been. 
		//step through property 
		StringBuffer instanceLine = new StringBuffer();
		for(String prop:propList) {
			if(binvals.containsKey(prop)) {
				//we found bin values for this property!
				instanceLine.append(binvals.get(prop));
			} else {
				//no bin values found for this property! Emit "x,x,x" with one x for every bin there should be for this property
				// figure this out; for first run, this should never come up. But put in a dummy thing
				instanceLine.append("(missing property " + prop + ")");
			}
			//append a comma - always do this, because we'll be appending a class afterward, yes?
			instanceLine.append(",");
		}
		
		//then just emit that line to our output!
		context.write(key, new Text(instanceLine.toString()));
		end OLD STUFF =================================================================================================== */
	}
}
