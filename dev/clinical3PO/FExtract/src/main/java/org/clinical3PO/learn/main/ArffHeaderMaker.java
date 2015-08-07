/*
 * MRATArffHeaderMaker - single-process program to emit the ARFF header that will go on the file
 * created by the map/reduce-based ARFF vector creation program (yet to be named) and the
 * postprocess on its output that reconciles the map/reduce output. Those programs will ultimately emit
 * only vector data. Prepend the header emitted by this program to the vectors emitted by them,
 * and the .arff will be complete.
 * 
 *  This program writes its output to arffheader.txt in output directory (given in MRATCmdLine arguments).
 */

package org.clinical3PO.learn.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.clinical3PO.learn.util.C3POFilterConfiguration;
import org.clinical3PO.learn.util.FEConfiguration;
import org.clinical3PO.learn.util.FEStrategyBase;

public class ArffHeaderMaker {
	
	//configuration files:
	C3POFilterConfiguration filtconf;
	FEConfiguration feconf;
	//command line configuration!
	FECmdLine cmdline;
	
	public boolean emitClassAttribute(PrintWriter pw) throws Exception {
		FEStrategyBase strat;
		
		strat = feconf.getClassStrategyForProperty(cmdline.classAttribute);
		
//		pw.println("@attribute " + FEConfiguration.classPropertyPrefix + cmdline.classAttribute + " " + strat.getEvaluator().getWekaAttributeType());
		
		// Replacing with previous line to generate proper arff format
		pw.println("@attribute " + FEConfiguration.classPropertyPrefix + " " + strat.getEvaluator().getWekaAttributeType());
		
		return true;
	}
	
	public boolean emitAttributesForProperty(PrintWriter pw, String prop) throws Exception {
		ArrayList<FEStrategyBase> strats;
		FEStrategyBase strat;
		
		//debug:
		//System.err.println("-- emitAttrFroProperty - pw = " + pw + " prop = " + prop + " feconf = " + feconf);
		
		//find all the strategies associated with this property. If none, 
		//use the *default* one(s).
		strats = feconf.getStrategiesForProperty(prop);
		
		//debug
		/*
		System.err.println("Found " + strats.size() + " strategies for " + prop);
		for(int j=0;j<strats.size();j++) {
			strat = strats.get(j);
			if(strat == null) {
				System.err.println("Strategy is null!");
			} else {
				System.err.println("Strategy " + j);
				System.err.println("Propname: " + strat.getPropertyName());
				System.err.println("Binner: " + strat.getBinner());
				System.err.println("Evaluator: " + strat.getEvaluator());
				System.err.println("Valuetype: " + strat.getValueType());
			}
		}
		*/		
		
		//construct the attribute names for this property! Might make this into a function
		//on feature extraction config
		//(class name)_(binner infix)_(evaluator infix)_(strategy index)_(valuetype infix)_bin(bin index)
		//outermost loop is strategy; then bin.
		for(int j=0;j<strats.size();j++) {
			strat = strats.get(j);
			String attrBase = prop + "_" +
					strat.getBinner().getFeatureNameInfix() + "_" +
					strat.getEvaluator().getFeatureNameInfix() + "_" +
					Integer.toString(j) + "_" +
					strat.getValueType().getFeatureNameInfix() + 
					"_bin";
			for(int b=0;b<strat.getBinner().getNumBins();b++) {
				pw.println("@attribute " + attrBase + Integer.toString(b) + " " + strat.getEvaluator().getWekaAttributeType());
			}
		}
		
		return true;
	}
	
	//abstracted version: should work on hdfs if the printwriter were constructed there and the config commandline
	//and files read there.
	public boolean writeArffHeader(PrintWriter pw, FECmdLine cmdl, C3POFilterConfiguration filc, FEConfiguration fec) throws Exception {
		
		//copy configs to globals!
		filtconf = filc;
		feconf = fec;
		cmdline = cmdl;
		
		//let's make the relation be relation_(class name)
		//and send an extra newline just to make it look nice
		pw.println("@relation relation_" + cmdline.classAttribute);
		pw.println();
		
		//THIS IS THE REALLY IMPORTANT STUFF BECAUSE IT WILL BE A LOT LIKE THE LOOP USED IN THE
		//VECTOR CREATION STEP!
		
		//BUT FIRST: if we're going to emit patient ID, do that.
		//CURRENTLY ASSUMING IT'LL BE A STRING.
		//NO ACTUALLY  NUMERIC
		if(cmdline.includePatientID) {
			pw.println("@attribute PatientID_debug NUMERIC");
		}
		
		//so. The absolute central thing is to step through the properties in the right order in every
		//case. And that order is stored in filtconf's propertiesInOrder arraylist! 
		for(String prop:filtconf.getPropertiesInOrder()) {
			
			//****************************************** I THINK THIS IS WHERE TO EXCLUDE THE
			//******* CLASS PROPERTY IF cmdline.excludeClassPropFromFeatureVector is true
			if(!cmdline.excludeClassPropFromFeatureVector || !prop.equals(cmdline.classAttribute)) {
				System.err.println("- Handling property |" + prop + "|");		//debug
				if(!emitAttributesForProperty(pw, prop)) {
					System.err.println("Failed to emit property " + prop);
					pw.close();
					System.exit(1);
				}
			} else {
				//verbose debug
				System.err.println("NOTE SKIPPING feature vector segment for class property " + cmdline.classAttribute);
			}
		}
		
		if(!emitClassAttribute(pw)) {
			System.err.println("Failed to emit class attribute");
			pw.close();
			System.exit(1);
		}
		
		
		//And then our data section marker, with a nice clean newline in front of it.
		pw.println();
		pw.println("@data");
		
		//let's say caller should close pw; it may want to add more stuff.
		
		return true;
	}

	//initial version: works on ordinary filesystem.
	public boolean makeArffHeader(String[] args) {
		try {
			
			// HERE IS THE SECTION THAT ALL COMPONENTS HAVE TO DO to set up -------------------------------------------
			// the tricky bit about setting it up as a function is that the file handling
			// of the filter config and ... well, all of it... is somewhat different under hadoop.
			// so for now I will yank and paste.
			cmdline = new FECmdLine();
			filtconf = new C3POFilterConfiguration();
			feconf = new FEConfiguration();
			
			System.err.println("Parsing command line...");
			if(!cmdline.parseCommandLine(args)) {
				System.err.println("Failed to read command line");
				System.exit(1);
			}

			
			//ok so. got the command line read. Read the config files
			System.err.println("Accumulating filter config file " + cmdline.filterConfigFilePath + "...");
			FileInputStream fis = new FileInputStream(cmdline.filterConfigFilePath);
			if(!filtconf.readPropertyConfiguration(new InputStreamReader(fis))) {
				System.err.println("Failed to read filter configuration file");
				System.exit(1);
			}
			fis.close();

			//TODO THERE WILL BE MORE THAN ONE OF THESE!
			//TODO THERE WILL BE MORE THAN ONE OF THESE!
			//TODO THERE WILL BE MORE THAN ONE OF THESE!
			//TODO THERE WILL BE MORE THAN ONE OF THESE!
			//TODO THERE WILL BE MORE THAN ONE OF THESE!

			System.err.println("Reading feature extraction config file " + cmdline.feConfigFilePath + "...");
			fis = new FileInputStream(cmdline.feConfigFilePath);
			
			if(feconf.accumulateFromTextConfigFile(fis,cmdline.classAttribute)) {
				//DEBUG OUTPUT???
			} else {
				throw new Exception("ERROR: unable to read feature extraction config file " + cmdline.feConfigFilePath);
			}
			fis.close();
			
			//check to make sure feconf is ok after all the accumulations done
			if(!feconf.validateAfterAccumulation(cmdline.globalTimeRange,filtconf,cmdline.classAttribute,cmdline.classBinTime)) {
				throw new Exception("ERROR: incorrect feature extraction configuration");
			}
			// end HERE IS THE SECTION THAT ALL COMPONENTS HAVE TO DO to set up ---------------------------------------

			
			//start up out output file
			File outputDir = new File(cmdline.outputDirectory);
			if(!outputDir.exists()) {
				System.err.println("NOTE: creating output directory " + cmdline.outputDirectory);
				outputDir.mkdirs();
			}
			
			//make sure output dir has a file separator on the end
			String outdir = cmdline.outputDirectory;
			if(!outdir.endsWith(System.getProperty("file.separator"))) {
				outdir = outdir + System.getProperty("file.separator");
			}
			String outfilepath = outdir + "arffheader.txt";
			System.err.println("Opening " + outfilepath + " to write...");
			PrintWriter pw = new PrintWriter(new FileWriter(outfilepath));
			
			System.err.println("Emitting ARFF header to " + outfilepath + "...");
			
			if(writeArffHeader(pw, cmdline, filtconf, feconf)) {
				System.err.println("Successfully wrote: " + outfilepath);
			} else {
				System.err.println("Failed to create arff file header: " + outfilepath);
				pw.close(); 
				return false;
			}
			
			// begin move to subroutine -----------------------------------------------------------------
			/*
			//let's make the relation be relation_(class name)
			//and send an extra newline just to make it look nice
			pw.println("@relation relation_" + cmdline.classAttribute);
			pw.println();
			
			//THIS IS THE REALLY IMPORTANT STUFF BECAUSE IT WILL BE A LOT LIKE THE LOOP USED IN THE
			//VECTOR CREATION STEP!
			//so. The absolute central thing is to step through the properties in the right order in every
			//case. And that order is stored in filtconf's propertiesInOrder arraylist! 
			for(String prop:filtconf.getPropertiesInOrder()) {
				System.err.println("- Handling property |" + prop + "|");
				if(!emitAttributesForProperty(pw, prop)) {
					System.err.println("Failed to emit property " + prop);
					pw.close();
					System.exit(1);
				}
			}
			
			if(!emitClassAttribute(pw)) {
				System.err.println("Failed to emit class attribute");
				pw.close();
				System.exit(1);
			}
			
			
			//And then our data section marker, with a nice clean newline in front of it.
			pw.println();
			pw.println("@data");
			*/
			
			// end move to subroutine -------------------------------------------------------------------

			pw.close();
			
			//moved System.err.println("Successfully wrote: " + outfilepath);
			
			//and that's that.
		} catch (Exception e) {
			System.err.println("Failed to create arff file header");
			e.printStackTrace();
			System.exit(1);
		}

		return true;
	}

	public static void main(String[] args) {
		ArffHeaderMaker mahm = new ArffHeaderMaker();
		if(mahm.makeArffHeader(args) != true) {
			System.err.println("Arff header creation failed");
			System.exit(1);
		}
	}

}
