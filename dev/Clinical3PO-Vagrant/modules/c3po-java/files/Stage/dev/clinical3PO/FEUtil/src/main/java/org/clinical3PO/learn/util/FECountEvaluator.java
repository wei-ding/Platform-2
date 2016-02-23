package org.clinical3PO.learn.util;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * FECountEvaluator just reports how many measurements are in each bin.
 * - how to handle illegal values and such? 
 * - can count ALL measurements regardless of legality
 * - OR all legit
 * - OR all non-legit
 * @author sean
 *
 */

public class FECountEvaluator extends FEEvaluatorBase {

	//flags for whether to count in legit and non-legit value: at least one of these must be true.
	public boolean reportLegal;
	public boolean reportIllegal;
	
	//default ctor
	public FECountEvaluator() {
		super();
		
		//TODO: default behavior is like this:
		//- bins with no errors get the MEAN of all values unweighted
		//- bins with errors get UNKNOWN
		//- empty bins get last legitimate value found (i.e., most recent past), UNKNOWN if none
		
		//super() does some of this - but let's be sure we get what we want
		//to start, the values ArrayList is null. Will get allocated by evaluate().
		values = null;
		//default error value 
		errValue = defaultErrorValue;
		//default unk value
		unkValue = defaultUnknownValue;
		//default error handling strategy
		binErrorStrategy = binErrorAlwaysUnk;
		
		//by default, report only count of legitimate values
		reportLegal = true;
		reportIllegal = false;
		
		//empty bin strategy is kind of irrelevant for this evaluator: just report zero values.
		
		//we can't really know legit low and high - that's for detailed ctors or settors
		legitLow = 0.0;
		legitHigh = 0.0;
	}
	
	public C3POPatientPropertyMeasurement evaluateBinPass1(ArrayList<C3POPatientPropertyMeasurement> bin) throws Exception {
		
		//Error handling is special on this kind of evaluator - there are no erroneous values 
		//or unknown ones, right? so the status is always OK. We will make a var for it just in case.
		String stat = C3POPatientPropertyMeasurement.okFeatureStatus;
		long timestamp = 0;		//dummy... probably won't change...????
		int numLegitValues = 0;
		int numNonLegitValues = 0;
		
		for(C3POPatientPropertyMeasurement meas:bin) {
			
			//ONLY CONSIDER VALUES WHOSE IN-RANGE FLAG IS TRUE
			if(meas.isWithinTimeRange) {
				//convert the bin Object into a double, if possible. 
				//ASSUMING THAT THEY'RE STORED AS Double (the class, not the primitive.)
				//this is going to throw an exception if the valuetype isn't Double, yuck
				double inval = ((Double)meas.value).doubleValue();
				
				boolean isError = false;
				//let's consider any value outside of legit range OR which has a non-OK status to be an error
				//remember legitness INCLUDES low value but not high one
				if(inval < legitLow || inval >= legitHigh || !meas.status.equals(C3POPatientPropertyMeasurement.okFeatureStatus)) {
					isError = true;
				} 
				if(isError) {
					//how do we deal with an error value? Count it as non-legit.
					numNonLegitValues += 1;
			  	} else {
					//value is OK, do the various housekeepings
					numLegitValues += 1;
				}
			} else {
				//measurement out of time range. So even if it's error or unk, 
				//don't reflect. 
				//but let it be known there were non-erroneous, if there were out-of-time OK ones? No,
				//TODO ********************************************************************************
				//TODO ********************************************************************************
				//TODO ********************************************************************************
				//FOR NOW LEAVE IT LIKE IT IS
				//TODO ********************************************************************************
				//TODO ********************************************************************************
				//TODO ********************************************************************************
			}
		}
		
		int theVal = 0;
		
		//reckon value, taking into account whether we want legit or non legit or both.
		if(reportLegal) theVal += numLegitValues;
		if(reportIllegal) theVal += numNonLegitValues;
		
		return new C3POPatientPropertyMeasurement(new Integer(theVal),stat,timestamp);
	}
			
	@Override
	public boolean evaluate(FEBinnerBase binner) throws Exception {
		// This evaluator is simple enough that it can do it in one pass: no reference to 
		// any other bin is needed for the empty bin handling.
		//sanity checks
		if(binner == null) {
			throw new Exception("FECountEvaluator.evaluate - null binner passed");
		}
		if(binner.getBins() == null) {
			throw new Exception("FECountEvaluator.evaluate - binner returns null bins");
		}
		
		//so: create our parallel values ArrayList by stepping through the bins.
		values = new ArrayList<C3POPatientPropertyMeasurement>();
	
		for(ArrayList<C3POPatientPropertyMeasurement> bin:binner.getBins()) {
			if(bin == null || bin.isEmpty()) {
				//if empty or null, report zero values. (hardcoded OK status, zero timestamp.)
				values.add(new C3POPatientPropertyMeasurement(new Integer(0),C3POPatientPropertyMeasurement.okFeatureStatus,0));
			} else  {
				//System.err.println("-- bin nonempty - adding " + evaluateBinPass1(bin));		//VERBOSE DEBUG
				//HERE NEED TO DO LEGIT AND ERROR HANDLING!!!!!!!!!!!!!!!!
				//HERE NEED TO DO LEGIT AND ERROR HANDLING!!!!!!!!!!!!!!!!
				//HERE NEED TO DO LEGIT AND ERROR HANDLING!!!!!!!!!!!!!!!!
				//HERE NEED TO DO LEGIT AND ERROR HANDLING!!!!!!!!!!!!!!!!
				values.add(evaluateBinPass1(bin));
			}
		}
		
		return true;
	}

	@Override
	public String getWekaAttributeType() {
		// since count evaluator emits an int, call it Weka NUMERIC - I think weka has an integer type,
		// but let's go with this for now...???
		return "NUMERIC";
	}

	@Override
	public String toWekaString(FEBinnerBase binner) throws Exception {
		StringBuffer buf = new StringBuffer();

		if(binner == null) {
			throw new Exception("FECountEvaluator.toWekaString - null binner");
		}
		
		if(values == null) {
			//if we don't have any values at all for a given property, emit all absent-feature for the
			//related segment.
			for(int j=0;j<binner.getNumBins();j++) {
				//NOTE ALWAYS USE the absentFeatureValue value for absent features, do not hardcode
				buf.append(FEEvaluatorBase.absentFeatureValue);
				if(j!= binner.getNumBins()-1) buf.append(",");		//commas after all but the last value
			}
		} else {
			//we do have values!
			C3POPatientPropertyMeasurement meas = null;
			
			//sanity check values length against expected # bins
			//THIS IS NO LONGER RIGHT... now binner has bins for before start time, potentially.
			//but now it should be the same as the actual number of bins, not expected. 
			
			if(values.size() != binner.getBins().size()) {
				throw new Exception("Mismatch in number of values (" + values.size() + ") and number of bins (" + binner.getBins().size() + ")");
			}
			
			//so: just emit the values starting at first used bin.
			for(int j=0; j< binner.getNumBins();j++) {
				meas = values.get(j + binner.getFirstUsedBin());
				//emit (null) for null values, error value for erroneous, unk value for unknowns, or actual measurement value for normals.
				if(meas == null) buf.append("(null)"); 
				else if(meas.status.equals(C3POPatientPropertyMeasurement.errorFeatureStatus)) buf.append(errValue);
				else if(meas.status.equals(C3POPatientPropertyMeasurement.unknownFeatureStatus)) buf.append(unkValue);
				else buf.append(meas.value.toString());
				if(j!= binner.getNumBins()-1) buf.append(",");		//commas after all but the last value
			}
		}
		
		return buf.toString();
	}

	@Override
	public String getFeatureNameInfix() {
		// let's return "ect" plus "l" for legal and "i" for illegal, so
		// we'll get ectl, ecti, or ectil
		StringBuffer inf = new StringBuffer("ect");
		
		if(reportIllegal) inf.append("i");
		if(reportLegal) inf.append("l");
		
		return inf.toString();
	}

	@Override
	public boolean instantiateFrom(String paramBlock, int startLine)
			throws Exception {
		//System.err.println("-- basic evaluator instantiateFrom() got start line: " + startLine + " param block: |" + paramBlock + "|");
		
		//establish defaults: report legal AND illegal false
		reportLegal = false;
		reportIllegal = false;
		
		//so, step through it line by line and look for keyword/value pairs
		Scanner s = new Scanner(paramBlock);
		s.useDelimiter(System.getProperty("line.separator"));
		
		int lineNum = startLine;
		while(s.hasNext()) {
			String theLine = s.next();

			//there can be blank lines, and they're OK - represent comment lines in the source. Skip them.
			if(theLine.length() > 0) {
				//System.err.println("Got a line: |" + theLine + "|, source file line " + lineNum);
				
				
				//HERE BREAK IT UP AND PARSE IT
				String[] toks = theLine.split("\\s+",2);
				
				/* here's an example - at least one of reportLegal and reportIllegal must be true;
				 * if no line is given for the value, default to false
				 * error strategy is actually ignored, as is emptybin
				 * 	reportLegal true
				    reportIllegal false
	    			errorStrategy alwaysUnk 
    				emptyBinStrategy lastLegit
        			legitLow 0.0
        			legitHigh 1000000.0
				 */
				
				if(toks[0].equalsIgnoreCase("reportLegal")) {
					if(toks[1].equalsIgnoreCase("true")) {
						reportLegal = true;
					} else if(toks[1].equalsIgnoreCase("false")) {
						reportLegal = false;
					} else {
						s.close();
						throw new Exception("Unrecognized reportLegal value in evaluator block: " + toks[1] + " line " + lineNum);
					}
				} else if(toks[0].equalsIgnoreCase("reportIllegal")) {
					if(toks[1].equalsIgnoreCase("true")) {
						reportIllegal = true;
					} else if(toks[1].equalsIgnoreCase("false")) {
						reportIllegal = false;
					} else {
						s.close();
						throw new Exception("Unrecognized reportIllegal value in evaluator block: " + toks[1] + " line " + lineNum);
					}
				} else if(toks[0].equalsIgnoreCase("legitLow")) {
					double lo = Double.valueOf(toks[1]);
					setLegitLow(lo);
				} else if(toks[0].equalsIgnoreCase("legitHigh")) {
					double hi = Double.valueOf(toks[1]);
					setLegitHigh(hi);
				}
				//DON'T WORRY ABOUT UNRECOGNIZED so we can subclass and have it call this to handle
				//the superclass bits.
			}			
			lineNum++;
		}
		
		s.close();
		
		//sanity check: one of report legal or illegal must be true.
		if(!reportIllegal && !reportLegal) {
			throw new Exception("One of reportLegal or reportIllegal must be true and neither is, in evaluator block starting line " + startLine);
		}
		
		return true;
	}

}
