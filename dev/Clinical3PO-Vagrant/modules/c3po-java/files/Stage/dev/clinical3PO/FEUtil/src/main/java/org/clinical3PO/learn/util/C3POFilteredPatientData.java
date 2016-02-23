package org.clinical3PO.learn.util;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.TreeMap;
import java.util.Vector;

/**
 * class for handling the filtered patient data files written by Wim's filtering toolchain
 * Format is like this, per line: - see below.
 * THE IDEA IS YOU ALLOCATE ONE OF THESE, accumulate possibly several files with it, and then fetch its featuresFound and foundValues members
 * 
 * @author sean
 *
 */

public class C3POFilteredPatientData {
	
	/**
	 * constants for the positions of fixed-position tokens within the input lines
	 */
	public static final int NumRequiredTokens = 2;		//number of fixed-position tokens expected
	public static final int IDToken = 0;
	public static final int FeatureNameToken = 1;

	//then the giant intermediate form of everything
	//outermost level maps patient ID string to:
	//  map of feature name string to a 
	//    map indexed by bin 0..47 for hour to a vector of ValueStatus for each bin. 
	//  and if there is no entry for that bin, do what?
	TreeMap<String,TreeMap<String,TreeMap<Integer,Vector<C3POPatientPropertyMeasurement>>>> foundValues;
	TreeMap<String,String> featuresFound;
	
	
	
	public TreeMap<String, TreeMap<String, TreeMap<Integer, Vector<C3POPatientPropertyMeasurement>>>> getFoundValues() {
		return foundValues;
	}

	public void setFoundValues(
			TreeMap<String, TreeMap<String, TreeMap<Integer, Vector<C3POPatientPropertyMeasurement>>>> foundValues) {
		this.foundValues = foundValues;
	}

	public TreeMap<String, String> getFeaturesFound() {
		return featuresFound;
	}

	public void setFeaturesFound(TreeMap<String, String> featuresFound) {
		this.featuresFound = featuresFound;
	}
	
	//ctor
	
	public C3POFilteredPatientData() {
		// TODO Auto-generated constructor stub
		foundValues = new TreeMap<String,TreeMap<String,TreeMap<Integer,Vector<C3POPatientPropertyMeasurement>>>>();
		featuresFound = new TreeMap<String,String>();
	}
	

	//getMinBinForAttr, given an attribute, returns the lowest numbered bin recorded for it, if any
	//-1 on error. Not sure if this is really useful.
	public int getMinBinForAttr(String attr) {
		if(foundValues == null) return -1;
		int MinBin = Integer.MAX_VALUE;
		for(String pid:foundValues.keySet()) {
			if(foundValues.get(pid).containsKey(attr)) {
				TreeMap<Integer,Vector<C3POPatientPropertyMeasurement>> bins = foundValues.get(pid).get(attr);
				//since this is a treemap, the first key is the minimum bin, yes?
				int localmin = bins.firstKey().intValue();
				if(localmin < MinBin) MinBin = localmin;
			}
		}
		//if minbin is integer max, we never found any bins...?
		if(MinBin == Integer.MAX_VALUE) return -1;
		return MinBin;
	}
	
	//getMaxBinForAttr, given an attribute, returns the lowest numbered bin recorded for it, if any
	//-1 on error. Not sure if this is really useful.
	public int getMaxBinForAttr(String attr) {
		if(foundValues == null) return -1;
		int MaxBin = Integer.MIN_VALUE;
		for(String pid:foundValues.keySet()) {
			if(foundValues.get(pid).containsKey(attr)) {
				TreeMap<Integer,Vector<C3POPatientPropertyMeasurement>> bins = foundValues.get(pid).get(attr);
				//since this is a treemap, the last key is the maximum bin, yes?
				int localmax = bins.lastKey().intValue();
				if(localmax > MaxBin) MaxBin = localmax;
			}
		}
		//if maxbin is integer min, we never found any bins...?
		if(MaxBin == Integer.MIN_VALUE) return -1;
		return MaxBin;
	}

	/**
	 * addValueToBin, once a timestamp:value pair has been vetted for a given attribute - including its passing muster
	 * wrt the timestamp and attribute filters used to derive the input file - just records it in foundValues.
	 * This method doesn't do any of that vetting itself, accepts all comers.
	 * @param patientID
	 * @param feature
	 * @param bin - index of bin to put value in: currently the same as the timestamp's hour
	 * although this will remain the same even if that changes (caller needs to worry about deriving bin index correctly)
	 * @param value
	 * @param status = true for OK, false for error - later may branch out
	 * @return
	 */
	
	public boolean addValueToBin(String patientID, String attr, int bin, double value, String status) throws Exception {
		//see if there's a feature set for this patient
		if(!foundValues.containsKey(patientID)) {
			foundValues.put(patientID, new TreeMap<String,TreeMap<Integer,Vector<C3POPatientPropertyMeasurement>>>());
		}
		
		//see if there's a set of values for this feature
		if(!foundValues.get(patientID).containsKey(attr)) {
			foundValues.get(patientID).put(attr, new TreeMap<Integer,Vector<C3POPatientPropertyMeasurement>>());
		}
		
		//see if there's a bin for this hour
		if(!foundValues.get(patientID).get(attr).containsKey(Integer.valueOf(bin))) {
			foundValues.get(patientID).get(attr).put(Integer.valueOf(bin),new Vector<C3POPatientPropertyMeasurement>());
		}
		
		//Add the new value/status to the bin for this hour
		Vector<C3POPatientPropertyMeasurement> vecvs = foundValues.get(patientID).get(attr).get(Integer.valueOf(bin));
		
		//TODO DANGER KLUDGE HORROR: I think this class is going to go away anyway, but for now need to patch
		//it by adding a timestamp. We'll just call it in seconds, 3600 * bin...?
		//TODO DANGER KLUDGE HARDCODE
		//TODO DANGER KLUDGE HARDCODE
		//TODO DANGER KLUDGE HARDCODE
		//TODO DANGER KLUDGE HARDCODE
		//TODO DANGER KLUDGE HARDCODE
		//TODO DANGER KLUDGE HARDCODE
		//TODO DANGER KLUDGE HARDCODE
		//TODO DANGER KLUDGE HARDCODE
		//TODO DANGER KLUDGE HARDCODE
		vecvs.add(new C3POPatientPropertyMeasurement(value,status,bin*3600));

		return true;
	}
	
	/**
	 * handleTSValuePairs assumes handleLine has been called and has verified that there are an even
	 * number of tokens after the required ones
	 * 
	 * @param patientID - derived by caller (though for the moment it's also in toks)
	 * @param attr - "
	 * @param toks
	 * @param fcfg - filter configuration used for creating the given input file
	 * if null, use all attributes and all bins found? Sure. Includes global time range.
	 * @return
	 * @throws Exception
	 * 
    *********************************
    10/22/13: NOTE FOR NOW I'M GOING TO DO HOUR BINS AND SAY WE TELL IT OUR GLOBAL RANGE IS 05:22 TO 07:17 I WILL 
    REJECT VALUES EARLIER THAN 5:22 AND LATER THAN 7:16 BUT STILL HAVE BINS FOR 5:00-5:59, 6:00-6:59,
    7:00-7:59 - ASK RE WHAT TO DO ABOUT THAT - like normalize so that the first bin is 5:22-6:21, etc?
    *********************************
	 * 
	 * 
	 * 
	 */
	public boolean handleTSValuePairs(String patientID, String attr, String[] toks, C3POFilterConfiguration fcfg) throws Exception {
		//called by handleLine
		
		//step by twos since these are ts/value pairs
		//so: hour is the bin we want to stick this in, minute can be discarded.
		//FIRST PASS we need to come up with the mean - so we need to know the individual values
		//and the numbers of them. Or really just running total and number of values - 
		//PER PATIENT. But! more compatible with eventual map/reduce version and alternate versions
		//that do max or min or whatever (or all of those!) 
		//this really just parses into a data structure which we then postprocess... woe.
		//sure, OK. 

		//new 10/22/13: if fcfg is non-null:
		//- if the attr isn't in fcfg, bail.
		//ACTUALLY SPECIAL CASE IF USESATTR IS EMPTY OR NULL, ALLOW ANY. (this is how no-config-loaded is handled in arffgen2 - might want a better
		//way to express it...)
		//so actually. if fcfg == null, or attrs used is null/empty, accept; otherwise usesAttribute(attr) must be so.
		if(fcfg != null && fcfg.getPropertiesUsed() != null && !fcfg.getPropertiesUsed().isEmpty()) {
			//non-null config with non-null/empty attrs used. Must be in there to bother.
			if(!fcfg.usesProperty(attr)) {
				//this attribute isn't in config. Done!
				return true;
			}
		}
		
		for(int j=C3POFilteredPatientData.NumRequiredTokens; j < toks.length; j+=2) {

			//new 10/22/13: if fcfg is non-null:
			//- if the attr/timestamp pair isn't "interesting" (attr isn't in the list or timestamp out of range)
			//  don't bother doing any of this.
			//toks[j] should be the timestamp.
			//remember to allow any attr if attrs used is null/empty
			//or rather:
			//if fcfg is non-null and its attrs-used non-null/empty, check for interestingness.
			//so, if fcfg IS null or its attrs-used null/empty, assume interesting.
			if(fcfg == null || fcfg.getPropertiesUsed() == null || fcfg.getPropertiesUsed().isEmpty() || fcfg.interesting(attr, toks[j])) {
				
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
					addValueToBin(patientID, attr, binIndex, value, C3POPatientPropertyMeasurement.okFeatureStatus);
					
				} catch (NumberFormatException e) {
					//bad value; record an error status for it (HEY ERROR TRAP)
					addValueToBin(patientID, attr, binIndex, -1.0, C3POPatientPropertyMeasurement.errorFeatureStatus);
				}
			}
				
		}

		return true;
	}
	
	//note that timestamps appear to be given in random order.
	/*
	 140500      hr      47:22      58      40:22      65      27:22      89      04:22      85      
	 	18:22      74      33:22      77      19:22      79      45:22      69      34:22      75      
	 	04:52      77      32:22      81      05:22      78      03:22      71      20:22      93      
	 	01:22      75      21:22      93      31:22      88      44:22      68      39:22      76      
	 	22:22      86      06:22      76      38:22      76      30:22      91      23:22      88      
	 	07:22      78      17:22      76      02:22      67      37:22      79      29:22      82      
	 	07:37      78      16:22      77      36:22      73      01:52      68      15:22      78      
	 	42:22      68      24:22      84      46:22      68      07:52      81      08:22      76      
	 	25:22      83      14:22      76      28:22      87      09:22      80      13:22      77      
	 	26:22      91      10:22      80      12:22      77      	

	 */
	public boolean handleLine(String theLine, C3POFilterConfiguration fcfg) throws Exception {
		//assume caller avoids blanks and nulls getting here
		//break it up by \s+ and handle it
		String[] toks = theLine.split("\\s+");
		
		if(toks.length >= C3POFilteredPatientData.NumRequiredTokens) {
			
			//sanity check - there should be an even number of tokens after the required
			if(((toks.length - C3POFilteredPatientData.NumRequiredTokens) % 2) == 0) {
				//deal with it - split off ID and feature
				String patientID = toks[C3POFilteredPatientData.IDToken];
				String featureName = toks[C3POFilteredPatientData.FeatureNameToken];
				
				//here record the feature in featuresFound - this is massively redundant
				//but that's what TreeMaps are all about - boil the redundant down to unique.
				//debug RIP OUT 
				//if(!featuresFound.containsKey(featureName))	System.err.println("Found new feature: " + featureName);
				//TODO: DANGER! FOR NOW, 
				featuresFound.put(featureName, "NUMERIC");
				
				//then digest the ts/val pairs
				//TODO ERROR TRAP
				handleTSValuePairs(patientID, featureName, toks, fcfg);
			} else {
				//??? badly formed - for now, warn
				System.err.println("C3POFilteredPatientData.handleLine WARNING: line with odd number of tokens after required");
				System.err.println(theLine);
			}
			
		} else {
			//??? badly formed - for now, warn
			System.err.println("C3POFilteredPatientData.handleLine WARNING: line with fewer than " + C3POFilteredPatientData.NumRequiredTokens + " tokens:");
			System.err.println(theLine);
		}
		
		return true;
	}
	
	/**
	 * accumulateInput adds the given file to its foundValues and featuresFound.
	 * @param isr - inputstreamreader for the file of filtered patient data
	 * @param fcfg - filter configuration used for creating the given input file
	 * if null, use all attributes and all bins found
	 * @return
	 * @throws Exception
	 */
	public boolean accumulateInput(InputStreamReader isr, C3POFilterConfiguration fcfg) throws Exception {
			
		LineNumberReader lnr = new LineNumberReader(isr);

		//debug RIP OUT 
		//System.err.println("-- in C3POFPD.accumulateInput()");
		
		String theLine = lnr.readLine();
		
		while (theLine != null) {
			
			//ignore blank lines
			if(!theLine.isEmpty()) {
				
				//debug RIP OUT 
				//System.err.println("-- line: |" + theLine + "|");
				
				if(!handleLine(theLine,fcfg)) {
					//ERROR?
					//return null;
				}
			}
			
			theLine = lnr.readLine();
			if(theLine!=null) theLine = theLine.trim();
		}
		
		
		return true;
	}

}
