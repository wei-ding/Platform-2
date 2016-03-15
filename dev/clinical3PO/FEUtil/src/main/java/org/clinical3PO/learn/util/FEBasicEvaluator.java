package org.clinical3PO.learn.util;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * The default version of C3POFeatureExtractionEvaluator emits a continuous (double) value for each bin
 * can calculate it as the mean, min, or max of the values in the bin if there are more than one 
 * can 
 * @author u0176876
 *
 */
public class FEBasicEvaluator extends FEEvaluatorBase {

	//constants
	
	//enum for strategy for handling multi-value bins: mean, min, max, ...
	public static final int derivationMean = 0;				//i.e. value of multi-valued bin reduced to arithmetic mean of values
	public static final int derivationMin = 1;				//" min of values
	public static final int derivationMax = 2;				//" max of values
	public static final int derivationSum = 3;				//" sum of values
	//others to be added later...?
	Double lastLegitValue;			//for the pass-2 figuring of last-legitimate-value
	
	//TODO:
	//data members.
	//enum for strategy for handling multi-value bins: mean, min, max, ...
	int derivationStrategy;
	
	//default ctor
	public FEBasicEvaluator() {
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
		//default derivation strategy
		derivationStrategy = derivationMean;
		//default error handling strategy
		binErrorStrategy = binErrorAlwaysUnk;
		//default empty bin strategy
		emptyBinStrategy = emptyBinUseLastLegitOrUnk;
		//last legit value init
		lastLegitValue = null;
		
		//we can't really know legit low and high - that's for detailed ctors or settors
		legitLow = 0.0;
		legitHigh = 0.0;
	}
	
	//detailed ctor needs a String[] token block
	
	
	
	//helper functions for evaluate, evaluate one bin based on error and derivation strategies. This is for the first pass in evaluate() below,
	//so doesn't do any empty-bin handling.
	public C3POPatientPropertyMeasurement evaluateBinPass1(ArrayList<C3POPatientPropertyMeasurement> bin) throws Exception {
		
		double val = -1.0;
		String stat = C3POPatientPropertyMeasurement.okFeatureStatus;
		long timestamp = 0;		//dummy... probably won't change...????
	
		double aggregateValue = 0;
		double numLegitValues = 0;
		//init max and min to be less than and greater than everything, resp; make sure this works
		double maxValue = Double.NEGATIVE_INFINITY;
		double minValue = Double.POSITIVE_INFINITY;
		
		//see if there are any errors among the values. 
		boolean hadErrors = false;
		
		//step through values given.
		//if the value is outside legit range, act according to binErrorStrategy.
		//otherwise accumulate according to derivationStrategy.
		for(C3POPatientPropertyMeasurement meas:bin) {
			
			//ONLY CONSIDER VALUES WHOSE IN-RANGE FLAG IS TRUE
			if(meas.isWithinTimeRange) {
				//convert the bin Object into a double, if possible. 
				//ASSUMING THAT THEY'RE STORED AS Double (the class, not the primitive.)
				double inval = ((Double)meas.value).doubleValue();
				
				boolean isError = false;
				//let's consider any value outside of legit range OR which has a non-OK status to be an error
				//remember legitness INCLUDES low value but not high one
				if(inval < legitLow || inval >= legitHigh || !meas.status.equals(C3POPatientPropertyMeasurement.okFeatureStatus)) {
					isError = true;
				} 
				if(isError) {
					//how do we deal with an error value? based on binErrorStrategy.
					//if it's binErrorAlwaysUnk, we'll want to stick the whole bin with unknown
					//if it's binErrorAlwaysErr, " error
					//- so at this point for either of those, just say hadErrors = true, and the error handling bit down below will catch this
					//if it's one of the "ignore" types, don't do anything here.
					//otherwise throw an exception
					switch(binErrorStrategy) {
					case binErrorAlwaysUnk:
					case binErrorAlwaysErr:
						hadErrors = true;
						break;
					case binErrorIgnoreOrErr:
					case binErrorIgnoreOrUnk:
						//do nothing here
						break;
					default:
						throw new Exception("C3POFeatureExtractionBasicEvaluator.evaluateBinPass1 unknown bin error strategy");
					}
			  	} else {
					//value is OK, do the various housekeepings
					numLegitValues += 1.0;
					aggregateValue += inval;
					if(inval < minValue) minValue = inval;
					if(inval > maxValue) maxValue = inval;
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
		
		//apply derivation strategy (maybe don't bother if hadErrors? Let's do anyway, just in case
		//there are strategies one day that think that's ok
		if(numLegitValues == 0.0) {
			//there were no legitimate values in the bin; note an error in every case currently known
			//TODO ACTUALLY THIS IS NO LONGER TRUE; there may just not have been any within range.
			//TODO ********************************************************************************
			//TODO ********************************************************************************
			//TODO ********************************************************************************
			//FOR NOW LEAVE IT LIKE IT IS
			//TODO ********************************************************************************
			//TODO ********************************************************************************
			//TODO ********************************************************************************
			switch(binErrorStrategy) {
			case binErrorAlwaysUnk:
			case binErrorAlwaysErr:
			case binErrorIgnoreOrErr:
			case binErrorIgnoreOrUnk:
				hadErrors = true;
				break;
			default:
				throw new Exception("C3POFeatureExtractionBasicEvaluator.evaluateBinPass1 unknown bin error strategy");
			}
		} else {
			//there were legit values, so we can get a meaningful value out.
			switch(derivationStrategy) {
			case derivationMax: val = maxValue; break;
			case derivationMin: val = minValue; break;
			case derivationMean: 
				//mean is aggregateValue / numLegitValues; if numLegitValues is 0.0, use val of 0.0, status of error (set hadErrors = true)
				//but that case is handled above.
				val = aggregateValue / numLegitValues;
				break;
			case derivationSum:
				val = aggregateValue;
				break;
			default:
				//unknown derivation strategy. Die.
				throw new Exception("C3POFeatureExtractionBasicEvaluator.evaluateBinPass1 unknown derivation strategy");
			}
		}		
		
		//finally apply error strategy:  If we want to emit an error for the whole bin, change stat to
		//C3POPatientPropertyMeasurement.errorFeatureStatus; for unk, change it to
		//C3POPatientPropertyMeasurement.unknownFeatureStatus;
		//at this point hadErrors will be true if we want to emit error/unknown, so that's all we need to check. 
		/*
		public static final int binErrorIgnoreOrUnk = 0;		//i.e. just ignore erroneous values and apply the derivation strategy to the remainder; if none, emit unknown 
		public static final int binErrorIgnoreOrErr = 1;		//i.e. just ignore erroneous values and apply the derivation strategy to the remainder; if none, emit error 
		public static final int binErrorAlwaysUnk = 2;			//i.e. if any error values in the bin, emit unknown 
		public static final int binErrorAlwaysErr = 3;			//i.e. if any error values in the bin, emit error 
		*/
		if(hadErrors) {
			switch(binErrorStrategy) {
			case binErrorAlwaysUnk:
			case binErrorIgnoreOrUnk:
				stat = C3POPatientPropertyMeasurement.unknownFeatureStatus;
				break;
			case binErrorAlwaysErr:
			case binErrorIgnoreOrErr:
				stat = C3POPatientPropertyMeasurement.errorFeatureStatus;
				break;
			default:
				throw new Exception("C3POFeatureExtractionBasicEvaluator.evaluateBinPass1 unknown bin error strategy");
			}
		}		
		
		return new C3POPatientPropertyMeasurement(val,stat,timestamp);
	}
	
	//second pass bin value finder: empty bin filling thing
	/*
		by should now have all the values ever reported for that property, regardless of time,
  		plus a flag on each for whether it is within the time range. So the different empty-bin strategies can do whatever they do
  		wrt last-legit, etc. even ignoring the fact that the measurement occurred before the time range.
	 */
	public boolean evaluateBinPass2(int binindex) throws Exception {
		//TODO TEMP: don't do anything that relies on future stuff - but we don't have defines for them yet anyway
		C3POPatientPropertyMeasurement meas = null;
		
		//should we also consider an unk to be an empty bin? Let's see what happens
		if(values.get(binindex) == null || !values.get(binindex).status.equals(C3POPatientPropertyMeasurement.okFeatureStatus)) {
			//create a measurement object to stick in there
			//assuming timestamp isn't important anymore.
			meas = new C3POPatientPropertyMeasurement(null,0);
			
			switch(this.emptyBinStrategy) {
			case emptyBinUseUnknown:
				meas.status = C3POPatientPropertyMeasurement.unknownFeatureStatus;
				break;
				
			case emptyBinUseLastLegitOrErr:
				if(lastLegitValue == null) {
					//there is no last-legit! use err.
					meas.status = C3POPatientPropertyMeasurement.errorFeatureStatus;
				} else {
					//there is a last-legit! use it.
					meas.status = C3POPatientPropertyMeasurement.okFeatureStatus;
					meas.value = lastLegitValue;
				}
				break;
				
			case emptyBinUseLastLegitOrUnk:
				if(lastLegitValue == null) {
					//there is no last-legit! use unk.
					meas.status = C3POPatientPropertyMeasurement.unknownFeatureStatus;
				} else {
					//there is a last-legit! use it.
					meas.status = C3POPatientPropertyMeasurement.okFeatureStatus;
					meas.value = lastLegitValue;
				}
				break;
				
			default:
				throw new Exception("Unsupported empty bin strategy " + emptyBinStrategy);
			}
			
			//then stick it where it belongs
			values.set(binindex, meas);
			
		} else {
			//no need to tweak values(binindex), but do memorize the non-null value as the most recent legitimate 
			//one in case we're using such a strategy. But do this ONLY IF IT'S NOT UNK OR ERR!
			//NOTE THIS DOESN'T WORRY ABOUT WHETHER THEY'RE IN TIME RANGE. which is by design, I guess.
			if(values.get(binindex).status.equals(C3POPatientPropertyMeasurement.okFeatureStatus)) {
				lastLegitValue = (Double)(values.get(binindex).value);
			}
		}
		
		return true;
	}
	
	//TODO: evaluation based on the binner.getBins method - so maybe just pass in a binner?
	//WHAT THIS DOES IS COME UP WITH A VALUE FOR EVERY BIN THE BINNER HAS, EVEN THOSE BEFORE OR AFTER
	//THE TIME RANGE. SO BINNER'S FIRSTUSED IS THE INDEX OF THE FIRST VALUES ENTRY TO EMIT.
	@Override
	public boolean evaluate(FEBinnerBase binner) throws Exception {
		//sanity checks
		if(binner == null) {
			throw new Exception("FEBasicEvaluator.evaluate - null binner passed");
		}
		if(binner.getBins() == null) {
			throw new Exception("FEBasicEvaluator.evaluate - binner returns null bins");
		}
		
		//so: create our parallel values ArrayList by stepping through the bins.
		//Let's do two passes: first one doesn't do anything about empty bins; second pass applies empty bin strategy.
		values = new ArrayList<C3POPatientPropertyMeasurement>();
		
		//debug
		//System.err.println("Evaluate pass 1 running on " + binner.getBins().size() + " bins");
		
		for(ArrayList<C3POPatientPropertyMeasurement> bin:binner.getBins()) {
			if(bin == null || bin.isEmpty()) {
				//if empty or null, just add a null; second pass will figure out what to do
				//System.err.println("-- bin empty - adding null");		//VERBOSE DEBUG
				values.add(null);
			} else  {
				//System.err.println("-- bin nonempty - adding " + evaluateBinPass1(bin));		//VERBOSE DEBUG
				values.add(evaluateBinPass1(bin));
			}
		}
		
		//TODO: Second pass! Apply empty-bin strategy
		//so the value needs to be aware of context; maybe just pass its index and not its object,
		//and second pass function can use that to fetch it out of values and also know its context.
		
		//last legit value init; may not get used, but init anyway
		lastLegitValue = null;

		//debug
		//System.err.println("Evaluate pass 2 running on " + values.size() + " bins");
		
		for(int j=0; j< values.size(); j++) {
			if(!evaluateBinPass2(j)) {
				throw new Exception("evaluateBinPass2() failed");
			}
			//System.err.println("-- bin " + j + " value " + values.get(j));  //VERBOSE DEBUG
		}
		
		return true;			//TODO temp
	}
	
	//TODO: toString renders as a comma-separated weka-type feature ArrayList substring?
	//or should I make a toWekaString - that's probably better, somewhat less particular-software-chauvinist
	//THIS IS KIND OF THE DEFAULT implementation for this - think of how I can carry it around; probably later
	//have a multiple-level inheritance hierarchy
	/*
	 * ******************* THIS IS IMPORTANT!
    I FORGOT THAT IT MIGHT BE THE CASE THAT A GIVEN PATIENT NEVER HAS VALUES FOR A CERTAIN PROP!
    so how about this.
    let's say we don't emit x,x,x - we emit like ?,?,? if there's no entry for a given property
    - evaluator will need to know how many bins to emit.
    - then postprocess can look through and see - if it has multiple entries for a patient, choose
      any property-segment that has something other than ?,?,?
    - might want to have a way of saying what the policy is for missing properties, but for now
      always do ?,?,? 
	 * (non-Javadoc)
	 * @see org.clinical3PO.util.C3POFeatureExtractionEvaluator#toWekaString()
	 */
	@Override
	public String toWekaString(FEBinnerBase binner) throws Exception {
		StringBuffer buf = new StringBuffer();

		if(binner == null) {
			throw new Exception("FEBasicEvaluator.toWekaString - null binner");
		}
		
		if(values == null) {
			//TODO THIS SHOULD PROBABLY EMIT x,x,x,x, right?
			//for now, if we don't have any values at all for a given property, emit all ? for the
			//related segment.
			//actually let's do xs and watch for those. can postproc with a sed to replace "x," with "?,"
			
			//TODO ***************************************************************************************************
			//TODO ***************************************************************************************************
			//TODO ***************************************************************************************************
			//TODO ***************************************************************************************************
			//TODO ***************************************************************************************************
			//TODO ***************************************************************************************************
			//TODO ***************************************************************************************************
			//for now do x.
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
		// let's return "ebas" plus derivation strategy - max, min, mean; unk for other
		switch (derivationStrategy) {
		case derivationMax: return "ebasmax";
		case derivationMin: return "ebasmin";
		case derivationMean: return "ebasmean";
		case derivationSum: return "ebassum";
		}
		return "ebasunk";
	}
	
	/**
	 * instantiateFrom parses a parameter block from the feature extraction strategy file
	 * (later there will be an instantiateFromOntology, probably)
	 * @param paramBlock - tokens from a config file, see FeatureExtractionConfiguration
	 * @param startLine - line number in script file where its contents start, for error reporting
	 * @return
	 * @throws Exception
	 */
	@Override
	public boolean instantiateFrom(String paramBlock, int startLine) throws Exception {
		//System.err.println("-- basic evaluator instantiateFrom() got start line: " + startLine + " param block: |" + paramBlock + "|");
		
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
				
				/* here's an example
				 * 	derivation mean
	    			errorStrategy alwaysUnk 
    				emptyBinStrategy lastLegit
        			legitLow 0.0
        			legitHigh 1000000.0
				 */
				/*
				 * 	//enum for strategy for handling erroneous values in a bin: either marked as error or value out of range
					public static final int binErrorIgnoreOrUnk = 0;		//i.e. just ignore erroneous values and apply the derivation strategy to the remainder; if none, emit unknown 
					public static final int binErrorIgnoreOrErr = 1;		//i.e. just ignore erroneous values and apply the derivation strategy to the remainder; if none, emit error 
					public static final int binErrorAlwaysUnk = 2;			//i.e. if any error values in the bin, emit unknown 
					public static final int binErrorAlwaysErr = 3;			//i.e. if any error values in the bin, emit error 
					
					//enum for strategy for handling empty bins
					public static final int emptyBinUseUnknown = 0;				//i.e. just report empty bin as unknown
					public static final int emptyBinUseLastLegitOrUnk = 1;		// use most recent past non-unk, non-error bin's value, if any; if none, use unknown
					public static final int emptyBinUseLastLegitOrErr = 2;		// use most recent past non-unk, non-error bin's value, if any; if none, use error

				 */
				
				if(toks[0].equalsIgnoreCase("derivation")) {
					if(toks[1].equalsIgnoreCase("mean")) {
						derivationStrategy = derivationMean;
					} else if(toks[1].equalsIgnoreCase("min")) {
						derivationStrategy = derivationMin;
					} else if(toks[1].equalsIgnoreCase("max")) {
						derivationStrategy = derivationMax;
					} else if(toks[1].equalsIgnoreCase("sum")) {
						derivationStrategy = derivationSum;
					} else {
						s.close();
						throw new Exception("Unrecognized derivation strategy in evaluator block: " + toks[1] + " line " + lineNum);
					}
				} else if(toks[0].equalsIgnoreCase("errorStrategy")) {
					if(toks[1].equalsIgnoreCase("IgnoreOrUnk")) {
						binErrorStrategy = binErrorIgnoreOrUnk;
					} else if(toks[1].equalsIgnoreCase("IgnoreOrErr")) {
						binErrorStrategy = binErrorIgnoreOrErr;
					} else if(toks[1].equalsIgnoreCase("AlwaysUnk")) {
						binErrorStrategy = binErrorAlwaysUnk;
					} else if(toks[1].equalsIgnoreCase("AlwaysErr")) {
						binErrorStrategy = binErrorAlwaysErr;
					} else {
						s.close();
						throw new Exception("Unrecognized error strategy in evaluator block: " + toks[1] + " line " + lineNum);
					}
				} else if(toks[0].equalsIgnoreCase("emptyBinStrategy")) {
					if(toks[1].equalsIgnoreCase("Unknown")) {
						emptyBinStrategy = emptyBinUseUnknown;
					} else if(toks[1].equalsIgnoreCase("LastLegitOrUnk")) {
						emptyBinStrategy = emptyBinUseLastLegitOrUnk;
					} else if(toks[1].equalsIgnoreCase("LastLegitOrErr")) {
						emptyBinStrategy = emptyBinUseLastLegitOrErr;
					} else {
						s.close();
						throw new Exception("Unrecognized empty bin strategy in evaluator block: " + toks[1] + " line " + lineNum);
					}
				} else if(toks[0].equalsIgnoreCase("legitLow")) {
					double lo = Double.valueOf(toks[1]);
					setLegitLow(lo);
				} else if(toks[0].equalsIgnoreCase("legitHigh")) {
					double hi = Double.valueOf(toks[1]);
					setLegitHigh(hi);
				} else if(toks[0].equalsIgnoreCase("descrete") || toks[0].equalsIgnoreCase("discrete")) {
					setDiscrete(toks[1].trim());
				}
				//DON'T WORRY ABOUT UNRECOGNIZED so we can subclass and have it call this to handle
				//the superclass bits.
			}			
			lineNum++;
		}
		
		s.close();
		
		return true;	//TODO TEMP DO TRUE IF IT WORKED
	}

	@Override
	public String getWekaAttributeType() {
		// since basic evaluator emits a double, call it Weka NUMERIC
		return "NUMERIC";
	}

	@Override
	public String getDescreteValue(String s) {
		return getDiscrete();
	}
	
}
