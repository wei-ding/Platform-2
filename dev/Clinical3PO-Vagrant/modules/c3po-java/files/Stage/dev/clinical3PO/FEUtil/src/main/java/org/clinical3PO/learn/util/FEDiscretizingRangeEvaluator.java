package org.clinical3PO.learn.util;

import java.util.ArrayList;
import java.util.Scanner;


//let's try subclassing the basicEvaluator and just changing the bits that need it
public class FEDiscretizingRangeEvaluator extends
		FEBasicEvaluator {
	
	//discretized output variables: the ordered list of possible values,
	//and the cutoffs for them
	//such that if input value < cutoffs[0], use output val 0
	//else if input value < cutoffs[1], use output val 1
	//... 
	//else use output val n (so there must be 1 fewer cutoff than output values and output val n
	//is used for any value > cutoff n-1.
	ArrayList<String> outputValues;
	ArrayList<Double> cutoffs;
	
	
	
	public FEDiscretizingRangeEvaluator() {
		super();
		
		//output values are empty for now
		outputValues = null;
		cutoffs = null;
	}
	
	//evaluate() is the same as with BasicEvaluator - which means the values are being
	//stored as doubles but will be rendered as discrete - is that appropriate? No
	@Override
	public boolean evaluate(FEBinnerBase binner) throws Exception {
		if(super.evaluate(binner)) {
			
			//DEBUG:
			//System.err.println("Superclass values: " + toWekaString(binner));
			
			//here we get values as a bunch of doubles
			// do a pass over here and convert to output values.
			for(C3POPatientPropertyMeasurement meas:values) {
				if(meas == null) {
					//this shouldn't happen.
					throw new Exception("Found null value after superclass evaluate() call");
				} else if(meas.status.equals(C3POPatientPropertyMeasurement.okFeatureStatus)) {
					//DO THE RANGE CHECK
					String finalval = outputValues.get(0);		//start by assuming < lowest cutoff
					for(int j=0;j<cutoffs.size();j++) {
						//VERIFY THIS
						if(((Double)(meas.value)).doubleValue() >= ((Double)(cutoffs.get(j))).doubleValue()) {
							finalval = outputValues.get(j+1);
						}
					}
					meas.value = finalval;
				} else {
					//replace unks and errs' value with the actual ? or ERR
					meas.value = meas.status;
				}
			}
			
		} else {
			throw new Exception("superclass evaluate() call failed");
		}
		
		return false;			//TODO TEMP RIP OUT
	}
	
	//toWekaString as written for basic eval should work...????

	@Override
	public String getWekaAttributeType() {
		if(outputValues == null) return null;
		//our output type is a weka categorical eg {a,b,c,d}
		//emit { + comma-sep list of possible values + }
		StringBuffer buf = new StringBuffer("{");
		for(int j=0;j<outputValues.size();j++) {
			buf.append(outputValues.get(j));
			//no comma after last one
			if(j!=outputValues.size()-1) buf.append(",");
		}
		buf.append("}");
		return buf.toString();
	}


	
	@Override
	public String getFeatureNameInfix() {
		// let's return "edr" plus derivation strategy - max, min, mean; unk for other
		switch (derivationStrategy) {
		case derivationMax: return "edrmax";
		case derivationMin: return "edrmin";
		case derivationMean: return "edrmean";
		}
		return "edrunk";
	}

	@Override
	public boolean instantiateFrom(String paramBlock, int startLine)
			throws Exception {
		
		//get the superclass stuff filled in - it shouldn't complain about the subclass stuff
		if(!super.instantiateFrom(paramBlock,startLine)) {
			return false;
		}
		
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
				/*
					values verylow, low, normal , borderline, high
					cutoff 50, 75, 85, 95, 105
				 */
				if(toks[0].equalsIgnoreCase("values")) {
					String[] vals = toks[1].split("\\s*,\\s*");
					//later maybe do some policing of this
					outputValues =  new ArrayList<String>();
					for(String val:vals) outputValues.add(val);
				} else if(toks[0].equalsIgnoreCase("cutoff") || toks[0].equalsIgnoreCase("cutoffs")) {
					String[] cuts = toks[1].split(",\\s*");
					cutoffs = new ArrayList<Double>();
					for(String cut:cuts) cutoffs.add(Double.valueOf(cut));
				} 
				//DON'T WORRY ABOUT UNRECOGNIZED so we can subclass and have it call this to handle
				//the superclass bits.
			}			
			lineNum++;
		}
		
		//sanity check: must be one fewer element in cutoffs than in outputValues
		if(cutoffs == null || outputValues == null) {
			s.close();
			throw new Exception("Values or cutoffs missing in DiscretizingRange evaluator block");
		}
		if(cutoffs.size() != outputValues.size()-1) {
			s.close();
			throw new Exception("There should be 1 fewer cutoff than values in DiscretizingRange evaluator block, line " + lineNum);
		}

		s.close();
		return true;
	}

}
