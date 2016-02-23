package org.clinical3PO.learn.util;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Objects of this class receive a list of timestamp/value pairs from raw patient data
 * and gather it into a fixed number of "bins" - doesn't convert the values to a final
 * feature value, just packages them up
 * This is the default implementation, which uses bins that are fixed in width in seconds
 * inclusion is such that binstart <= given time < nextbinstart means that a value at the given
 * time is added to that bin. Will have to subclass if we want to use different schemes of
 * inclusion.
 * @author u0176876
 *
 */
public class FEBasicBinner extends FEBinnerBase {
	
	//TODO default values - note some non-final - so they'll be configurable and instantly reflected over
	//any objects of that type (within the same VM instance.)
	public static double defaultBinWidth = 3600.0;			// default one hour bins
	//not using public static double defaultBinOffset = 0.0;
	
	//TODO configuration params. such as:
	//bin width, in seconds
	double binWidth;
	
	
	public FEBasicBinner() {
		super();
		//TODO this should create the default behavior
		binWidth = FEBasicBinner.defaultBinWidth;				//hour-wide bins
		//not using yet binOffset = C3POFeatureExtractionBinner.defaultBinOffset;			//default to hour-on-the-hour
	}
	
	// ctor to instantiate from a list of tokens from a strategy configuration file
	//prolly won't use
	/*
	public C3POFeatureExtractionFixedWidthBinner(String paramBlock) throws Exception {
		//super(paramBlock);		//do we need one?
		instantiateFrom(paramBlock);
	}
	*/
	
	/* disuse for now.
	//detailed ctor - give bin width in hh:mm[:ss], same with start and end time  
	public C3POFeatureExtractionFixedWidthBinner(String nwidth, String starttime, String endtime) throws Exception {
		super(starttime,endtime);
		binWidth = (double)C3POTimeRange.interpretTimeStamp(nwidth);
	}
	*/

	//setters and getters
	public double getBinWidth() {
		return binWidth;
	}

	public void setBinWidth(double binWidth) {
		this.binWidth = binWidth;
	}

	public String toString() {
		return "C3POFeatureExtractionFixedWidthBinner width: " + getBinWidth() + " range " + getRange();
	}
	
	//HERE override SetRange to generate bins from 00:00 to the end of the given range and
	//set firstUsedBin to make it so that bins.get(firstUsedBin) starts with the start time for range.
	/*
    - Or, could just allocate the bins from 00:00:00 to time range and set firstUsedBin to be wherever the time range start lands,
      and make is so that start time is the earliest time that would go in that bin.
      NOTE THIS MAY MAKE IT WEIRD for the pre-start bins, like if start time is 1:30:17, and the width is an hour, the bins before it
      will start at 0:30:17 and -0:29:43... really that one will be at 00:00:00 but only grab stuff up to 0:30:16. FIGURE THAT OUT.
      though may get that for free by figuring out the bin offset relative to firstUsedBin by doing 
      (((measurement time stamp) - (binner start time)) / (bin width))
      and if the diff between stamps is negative, do a floor? and if positive, do a ceil?
      so in the case above, a time of 0:30:16 is second 1816, and start offset is second 5417. So,
      (1816-5417) = -3601
      (-3601 / 3600) = -1.000277777777777778
      floor(-1.000277777777777778) = -2
      therefore: since firstUsedBin = 2, that goes in bin 0. So! Looks like that'll do the job.
      So - firstUsedBin is calculated by ceil(start time second / bin width)?
      (5417/3600) = -1.504722222222222222
      ceil(1.504722222222222222) = 2
      so yay.
      Assume that times before 00:00:00 are illegal for start time or timestamps
    - then when an evaluator wants to work on it, it could be handed a new ArrayList that has only the stuff in the bins, or better,
      is allowed to operate on the whole range but is aware of the time range, firstUsedBin, etc.
	 */
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
		
		//so the total number of bins is ceil((end time) / (bin width))?
		//is that right? remember end time is exclusive, so if
		//say end time is 7200 seconds, we want two bins, not three. So yes, that should be right.
		bins = new ArrayList<ArrayList<C3POPatientPropertyMeasurement>>();
		int numb = (int)(Math.ceil((double)(range.getEndTime()) / binWidth));		//hopework
		for(int j=0;j<numb;j++) {
			//initialize the bin to an empty arraylist? Or should it be null? Let's do the empty list.
			bins.add(new ArrayList<C3POPatientPropertyMeasurement>());
		}
		firstUsedBin = (int)(Math.ceil((double)(range.getStartTime())/binWidth));
		
		//sanity check:
		if(getNumBins() != (numb-firstUsedBin)) {
			System.err.println("HEY! #allocated bins - firstUsedBin should be " + getNumBins() + " but is actually " +  (numb-firstUsedBin));
		}
		
		//debug
		//System.err.println("Binner set time range " + range + " bins allocated: " + numb + " first used: " + firstUsedBin);
	}
				
	
	//overrides of required methods
	// utility methods
	/**
	 * getNumBins returns the number of bins this binner will generate WITHIN THE TIME RANGE.
	 * The main use I have in mind for this is just to figure out how many features 
	 * to reserve for the related property.
	 * returns -1 on error
	 * @return
	 */
	public int getNumBins() throws Exception {

		//sanity check. Bin width must be positive and nonzero
		if(binWidth <= 0.0) {
			//report error?
			throw new Exception("C3POFeatureExtractionBinner.getNumBins() - illegal bin width of " + binWidth + "seconds, must be positive");
		}
		//also, start offset must be less than bin width?
		//not using offset yet.
		//TODO: sanity check range? Not sure how I would other than non-null or backwards times or start time
		//or end time < 0
		if(range == null) {
			throw new Exception("C3POFeatureExtractionBinner.getNumBins() - null time range (did you call validateAfterAccumulation() on the configuration?)");
		}
		if(range.getStartTime() < 0 || range.getEndTime() < 0 || range.getStartTime() >= range.getEndTime()) {
			throw new Exception("C3POFeatureExtractionBinner.getNumBins() - illegal time range " + range.toString());
		}
		
		//so: how many bins will we generate?
		//the simple answer is (length of the time range in seconds) / (bin width in seconds)
		//but. That'll either be right or off by one...?
		//say our bin width is 3600 and our duration is 3600*48. looks like we get 48 bins.
		//if our duration is that plus one second, 49, yes?
		//TODO: WHAT ABOUT THE CLASSIFICATION HOUR? Do we have to account for it, or should that
		//be handled before we get here? Let's say the burden is on the strategy setup stuff to
		//make sure the binner doesn't emit anything it shouldn't.
		//so for first pass just do that.
		double rangeDuration = Double.valueOf(range.getEndTime() - range.getStartTime()).doubleValue();
		
		//what's the "ceil" function in java? Math.ceil()
		return (int)Math.ceil(rangeDuration / binWidth);
	}
	
	@Override
	public int getBinForTime(long timestamp) throws Exception {
		
		//VERBOSE DEBUG TODO RIP OUT
		//System.err.print("-- finding bin for ts " + timestamp + ": ");
		
		if(timestamp < 0) { 
			throw new Exception("getBinForTime called with time < 0");
		}
		
		//now that we allocate from zero, it's just timestamp / binWidth floor and it's allowed to be past the end
		//no, that's not right. 
		//int binNum = (int)Math.floor( (double)(timestamp - range.getStartTime()) / binWidth);
		//it needs to reckon from first used bin, like firstusebin - ((timestamp-start)/binwidth) yes?
		//because if we're at start time, that puts it at firstusebin, exactly at the start.
		//is it always floor? what if we do start time + 1 second?
		//floor(1/binWidth) = 0.
		//and if we do start - 3600?
		//Floor(-3600/3600) = -1 - so I think that's right. Keep an eye on this.
		//wait, there's a sign wrong there; if timestamp > start time, we want a bin AFTER firstUsedBin, yes?
		//was firstUsed - , now do + AND KEEP AN EYE ON THIS
		//let's see - if timestamp is 3599 and starttime is 3600, and so is binwidth we get
		//1 + floor((3599-3600)/binWidth) = 1 + floor(-1/3600) should be 0. Try it.
		int binNum = firstUsedBin + (int)Math.floor((double)(timestamp - range.getStartTime())/binWidth);
		
		//VERBOSE DEBUG TODO RIP OUT
		//System.err.println(binNum);
		
		if(binNum < 0) {
			throw new Exception("Timestamp of " + C3POTimeRange.timeToTimestamp(timestamp) + " led to  negative bin number " + binNum);
		}
		
		return binNum;
		
		/* old
		//so... bin number is just floor of (timestamp - range.getStartTime()) / binWidth, right?
		//that should make it inclusive on bottom, exclusive on top.
		int binNum = (int)( (double)(timestamp - range.getStartTime()) / binWidth);
		if(binNum < 0) return 0;			//should this be an error?
		if(binNum >= getNumBins()) return (int)(getNumBins()-1);	//should this be an error?
		return binNum;
		*/
	}
	
	@Override
	public void clearBins() throws Exception {
		//so: by now bins should be allocated. If not, yell.
		if(bins == null) {
			throw new Exception("accumulateBins called before setRange created bins");
		}

		for(int j=0;j<bins.size();j++) bins.set(j, new ArrayList<C3POPatientPropertyMeasurement>());
	}
	
	@Override
	public boolean accumulateBins(ArrayList<C3POPatientPropertyMeasurement> measurements) throws Exception {
		
		
		//VERBOSE DEBUG TODO RIP OUT
		//System.err.println("Got here 1");
		
		//so: see if we already have bins. If not, allocate and initialize them.
		/* use set range for this - and the actual binning will be a bit more involved.
		if(bins == null) {
			bins = new Vector<Vector<C3POPatientPropertyMeasurement>>();
			for(int j=0;j<getNumBins();j++) bins.add(new Vector<C3POPatientPropertyMeasurement>());
		} else if(bins.size() != getNumBins()) {
			//uh oh. We have bins, but the wrong number - something's wrong.
			throw new Exception("C3POFeatureExtractionBinner.accumulateBins - number of bins has changed between calls from " + bins.size() + " to " + getNumBins());
		}
		//so: figure out which bin each measurement in the incoming bunch goes to and put it there.
		for(C3POPatientPropertyMeasurement meas:measurements) {
			bins.get(getBinForTime(meas.timestamp)).add(meas);
		}
		*/

		//so: by now bins should be allocated. If not, yell.
		if(bins == null) {
			throw new Exception("accumulateBins called before setRange created bins");
		}
		
		//VERBOSE DEBUG TODO RIP OUT
		//System.err.println("Got here 2");
		
		//assume at this point that firstUsedBin is right and all
		for(C3POPatientPropertyMeasurement meas:measurements) {
			//bins.get(getBinForTime(meas.timestamp)).add(meas);
			
			//VERBOSE DEBUG TODO RIP OUT
			//System.err.println("Got here 3");
			
			//set the flag for whether this is within our time range. remember end time is exclusive,
			//so timestamp = to it is outside.
			if(meas.timestamp < getRange().getStartTime() || meas.timestamp >= getRange().getEndTime()) {
				meas.isWithinTimeRange = false;
			} else {
				//just in case this got falsed somewhere else
				meas.isWithinTimeRange = true;
			}
			
			//VERBOSE DEBUG TODO RIP OUT
			//System.err.println("Got here 4");
			
			
			int binnum = getBinForTime(meas.timestamp);
			
			if(binnum < 0) {
				throw new Exception("Bin number for timestamp " + meas.timestamp + " negative");
			}
			
			//VERBOSE DEBUG TODO RIP OUT
			//System.err.println("Got here 5");
			
			
			//so - make sure we have ENOUGH bins for our new guy here. If not, add enough bins to accommodate it.
			if(binnum >= bins.size()) {
				int numnewbins = (binnum - (bins.size()-1));
				//debug
				//System.err.println("Warning: timestamp after binner range found, adding " + numnewbins + " bins to accommodate");
				for(int p=0;p<numnewbins;p++) bins.add(new ArrayList<C3POPatientPropertyMeasurement>());
			}
			
			//VERBOSE DEBUG TODO RIP OUT
			//System.err.println("Got here 6");
			
			//put the measurement in its bin.
			bins.get(binnum).add(meas);
			
			//VERBOSE DEBUG TODO RIP OUT
			//System.err.println(" binned " + meas + " in #" + binnum);
		}
		
		//VERBOSE DEBUG TODO RIP OUT
		//System.err.println("Got here x");

		
		return true;
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
		System.err.println("-- fixed width binner instantiateFrom() got start line: " + startLine + " param block: |" + paramBlock + "|");
		
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
		return true;	//TODO TEMP DO TRUE IF IT WORKED
	}

	/**
	 * getFeatureNameInfix - feature names are built out of property name, binner type, evaluator type, etc - so this
	 * needs to contribute a subset of that name for this binning strategy.  
	 * @return
	 */
	@Override
	public String getFeatureNameInfix() {
		return "fbin";
	}
	
}
