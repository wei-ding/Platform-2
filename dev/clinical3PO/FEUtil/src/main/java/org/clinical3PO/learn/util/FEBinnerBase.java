package org.clinical3PO.learn.util;

import java.util.ArrayList;

public abstract class FEBinnerBase {

	//TODO: SHOULD TIME RANGE BE PART OF THIS OR IS IT PART OF THE PARENT STRATEGY CLASS?
	//might as well make a copy here
	C3POTimeRange range;
	
	//TODO: What about the actual bins? Do they reside here or are they just returned by a method?
	//I suppose let's have them reside here, then we could accumulate values on to them with multiple calls
	//to the binning method.
	//Different binners can handle these things differently, but on the whole I think they'll gather all
	//the measurements that come at them and label them wrt whether they're within the strategy's time range (isWithinTimeRange = true
	//on the particular property measeurement object. 
	//firstUsedBin is (generally) the index into bins for the first bin that contains measurements from within the time range;
	//and the start time for the time range is the start time for that bin.
	//there can be bins earlier than firstUsedBin in case evaluators want to use pre-time-range measurements (they'll
	//be marked with isWithinTimeRange == false.)

	ArrayList<ArrayList<C3POPatientPropertyMeasurement>> bins;
	int firstUsedBin;

	public FEBinnerBase() {
		//what is the default range? Hm. Let's make it a property of FilterConfiguration
		//TODO: this will do for now.
		//null it out and require callers to set it - from global or per-property time range
		range = null; //was new C3POTimeRange(0,C3POFilterConfiguration.defaultNumberOfHours * 3600);
		bins = null;			//bins will be allocated by the act of binning, so no need to
								//start them up now.
		firstUsedBin = 0;
	}

	/* let's disuse this for now. IF I BRING IT BACK WILL NEED HACKING UP
	//detailed ctor - give hh:mm[:ss] for start and end time  
	public C3POFeatureExtractionBinner(String starttime, String endtime) throws Exception {
		range = new C3POTimeRange(starttime, endtime);
		bins = null;			//allow the accumulating method to allocate.
		firstUsedBin = 0;
	}
	*/
	
	public C3POTimeRange getRange() {
		return range;
	}

	public void setRange(C3POTimeRange range) throws Exception {
		this.range = range;
	}

	public ArrayList<ArrayList<C3POPatientPropertyMeasurement>> getBins() {
		return bins;
	}

	/* I don't think this will happen unless we need it for a copy ctor or something
	public void setBins(Vector<Vector<C3POPatientPropertyMeasurement>> bins) {
		this.bins = bins;
	}
	*/
	
	public int getFirstUsedBin() {
		return firstUsedBin;
	}

	public void setFirstUsedBin(int firstUsedBin) {
		this.firstUsedBin = firstUsedBin;
	}

	//must-override methods
	/**
	 * getNumBins returns the number of bins this binner will generate WITHIN THE TIME RANGE GIVEN. it may have more.
	 * The main use I have in mind for this is just to figure out how many features 
	 * to reserve for the related property.
	 * returns -1 on error
	 * @return
	 */
	public abstract int getNumBins() throws Exception;
	/**
	 * getBinsForTime, given a time (assumed to be in seconds), figures out which bin it falls in
	 * @param timestamp
	 * @return
	 * @throws Exception
	 */
	public abstract int getBinForTime(long timestamp) throws Exception;
	
	/**
	 * clearBins should be called for each new set of patient data so old values don't stick around
	 * @throws Exception
	 */
	public abstract void clearBins() throws Exception;
	
	/**
	 * accumulateBins takes in a set of timestamp/value/status objects and assigns them to where they belong in
	 * the bins data member.
	 * @param measurements
	 * @return
	 * @throws Exception
	 */
	public abstract boolean accumulateBins(ArrayList<C3POPatientPropertyMeasurement> measurements) throws Exception;
	
	/**
	 * getFeatureNameInfix returns a string that characterizes the subclass for readability in Weka (or other) feature
	 * names, e.g. say we're extracting features for a property diasabp using a fixed width binner, the fixed width
	 * binner might return "bfix" as its infix, leading to a feature name like diasabp_bfix_(evaluator infix)_(strategy index)
	 * This allows us to have multiple strategies for a property and have unique feature names among them.
	 * @return
	 */
	public abstract String getFeatureNameInfix(); 
	
	/**
	 * instantiateFrom parses a parameter block from the feature extraction strategy file
	 * (later there will be an instantiateFromOntology, probably)
	 * @param paramBlock - parameter block lines from a config file, see FeatureExtractionConfiguration
	 * @param startLine - line number in script file where its contents start, for error reporting
	 * @return
	 * @throws Exception
	 */
	public abstract boolean instantiateFrom(String paramBlock, int startLine) throws Exception;
	
}
