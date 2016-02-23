package org.clinical3PO.learn.util;

import java.util.ArrayList;

public class FESingletonBinner extends FEBinnerBase {

	public FESingletonBinner() {
		super();
		// initialize bins to have one bin.
		bins = new ArrayList<ArrayList<C3POPatientPropertyMeasurement>>();
		bins.add(new ArrayList<C3POPatientPropertyMeasurement>());
		firstUsedBin = 0;
	}

	// setting time range for the singleton is easy - no need to allocate the bins here or
	// anything.
	@Override
	public void setRange(C3POTimeRange range) throws Exception {
		if(range == null) {
			throw new Exception("null time range given to binner setRange");
		}
		//HERE sanity check the range further
		if(range.getStartTime() < 0) {
			throw new Exception("time range given to binner setRange has start time < 0");
		}
		if(range.getEndTime() <= range.getStartTime()) {
			throw new Exception("time range given to binner setRange has start time >= end time");
		}

		super.setRange(range);
	}
	
	@Override
	public int getNumBins() throws Exception {
		// there is always one bin.
		return 1;
	}

	@Override
	public int getBinForTime(long timestamp) throws Exception {
		if(timestamp < 0) { 
			throw new Exception("getBinForTime called with time < 0");
		}
		
		// there is always one bin, zero-relative.
		return 0;
	}

	@Override
	public void clearBins() throws Exception {
		//so: by now bins should be allocated. If not, yell.
		if(bins == null) {
			//this should be impossible since ctor creates - but still.
			throw new Exception("accumulateBins called before bins created");
		}

		for(int j=0;j<bins.size();j++) bins.set(j, new ArrayList<C3POPatientPropertyMeasurement>());
	}

	@Override
	public boolean accumulateBins(
			ArrayList<C3POPatientPropertyMeasurement> measurements)
			throws Exception {
		//so: by now bins should be allocated. If not, yell.
		if(bins == null) {
			throw new Exception("accumulateBins called before setRange created bins");
		}
		
		//this should be really easy: everything goes into one bin. 
		//assume at this point that firstUsedBin is right and all
		for(C3POPatientPropertyMeasurement meas:measurements) {
			//set the flag for whether this is within our time range. remember end time is exclusive,
			//so timestamp = to it is outside.
			if(meas.timestamp < getRange().getStartTime() || meas.timestamp >= getRange().getEndTime()) {
				meas.isWithinTimeRange = false;
			} else {
				//just in case this got falsed somewhere else
				meas.isWithinTimeRange = true;
			}
			
			int binnum = getBinForTime(meas.timestamp);

			//this really shouldn't happen
			if(binnum != 0) {
				throw new Exception("Bin number for timestamp " + meas.timestamp + " wrong: should be 0, is " + binnum);
			}
			if(bins.size() != 1) {
				throw new Exception("should be one bin, there are " + bins.size());
			}
			
			//put the measurement in its bin.
			bins.get(binnum).add(meas);
		}
		
		return true;
	}

	@Override
	public String getFeatureNameInfix() {
		return "sngb";
	}

	@Override
	public boolean instantiateFrom(String paramBlock, int startLine)
			throws Exception {
		
		/* This is the basic binner parser; singleton doesn't really need anything in its block
		 * that we know of so far but in case we get some this'll be here to hack up
		System.err.println("-- singleton binner instantiateFrom() got start line: " + startLine + " param block: |" + paramBlock + "|");
		//so, step through it line by line and look for keyword/value pairs
		Scanner s = new Scanner(paramBlock);
		s.useDelimiter(System.getProperty("line.separator"));
		int lineNum = startLine;
		while(s.hasNext()) {
			String theLine = s.next();
			//there can be blank lines, and they're OK - represent comment lines in the source. Skip them.
			if(theLine.length() > 0) {
				System.err.println("Got a line: |" + theLine + "|, source file line " + lineNum);
				//HERE BREAK IT UP AND PARSE IT
				String[] toks = theLine.split("\\s+",2);
				//TODO NEED TO ADD A THING FOR TIME RANGE!... IF THERE IS ONE...??????????
				if(toks[0].equalsIgnoreCase("binwidth")) {
					double wid = Double.valueOf(toks[1]);
					setBinWidth(wid);
				} else {
					s.close();
					throw new Exception("ERROR: unrecognized keyword " + toks[0] + ", line " + lineNum);
				}
			}			
			lineNum++;
		}
		s.close();
		*/
		
		return true;
	}

}
