package org.clinical3PO.learn.util;

import java.util.ArrayList;

public abstract class FEEvaluatorBase {
	//values to be printed for bins whose value is erroneous or unknown, by default
	public static final String defaultErrorValue = "ERR";
	public static final String defaultUnknownValue = "?";
	
	//value that stands in for a feature value that's absent - see toWekaString
	public static final String absentFeatureValue = "*x*";
	
	//enum for strategy for handling erroneous values in a bin: either marked as error or value out of range
	public static final int binErrorIgnoreOrUnk = 0;		//i.e. just ignore erroneous values and apply the derivation strategy to the remainder; if none, emit unknown 
	public static final int binErrorIgnoreOrErr = 1;		//i.e. just ignore erroneous values and apply the derivation strategy to the remainder; if none, emit error 
	public static final int binErrorAlwaysUnk = 2;			//i.e. if any error values in the bin, emit unknown 
	public static final int binErrorAlwaysErr = 3;			//i.e. if any error values in the bin, emit error 
	
	//enum for strategy for handling empty bins
	public static final int emptyBinUseUnknown = 0;				//i.e. just report empty bin as unknown
	public static final int emptyBinUseLastLegitOrUnk = 1;		// use most recent past non-unk, non-error bin's value, if any; if none, use unknown
	public static final int emptyBinUseLastLegitOrErr = 2;		// use most recent past non-unk, non-error bin's value, if any; if none, use error
	//to implement later?
	//public static final int emptyBinUseInterpOrUnk = 3;			// use linearly interpolated value of most recent and closest future bin values; if either missing, use unknown
	//public static final int emptyBinUseInterpOrErr = 4;			// use linearly interpolated value of most recent and closest future bin values; if either missing, use error

	//data members
	//enum for strategy for handling erroneous values in a bin: either marked as error or value out of range
	protected int binErrorStrategy;
	//TODO: is there one for finding unknowns in a bin? Or would those be a class of input error?
	//enum for strategy for handling empty bins: unknown, previous-legit, interpolate between prev. and next legit, ...
	protected int emptyBinStrategy;
	
	//value to emit for erroneous values, default "ERR"?
	protected String errValue;
	//value to emit for unknowns, default "?"
	protected String unkValue;
	
	//legit range - DANGER! this is numeric-value chauvinist.
	double legitLow;				//any value less than this will be considered erroneous (low is inclusive)
	double legitHigh;				//any value greater than or equal to this this will be considered erroneous (high is exclusive)
	
	protected String discrete; 

	//ArrayList of actual values created by evaluate()
	//TODO: DOES THIS BELONG IN BASE CLASS? THERE MIGHT BE DIFFERENT WAYS TO REPRESENT OUTPUT VALUES
	protected ArrayList<C3POPatientPropertyMeasurement> values;		//won't use the timestamp fields. Or will it?
	
	//default ctor
	public FEEvaluatorBase() {
		//TODO: default behavior is like this:
		//- bins with errors get UNKNOWN
		//- empty bins get last legitimate value found (i.e., most recent past), UNKNOWN if none
		
		//to start, the values ArrayList is null. Will get allocated by evaluate().
		values = null;
		//default error value 
		errValue = defaultErrorValue;
		//default unk value
		unkValue = defaultUnknownValue;

		//default error handling strategy
		binErrorStrategy = binErrorAlwaysUnk;
		//default empty bin strategy
		emptyBinStrategy = emptyBinUseLastLegitOrUnk;
		
		//we can't really know legit low and high
		legitLow = 0.0;
		legitHigh = 0.0;
	}
	

	
	//gets and sets =============================================
	
	public int getBinErrorStrategy() {
		return binErrorStrategy;
	}



	public void setBinErrorStrategy(int binErrorStrategy) {
		this.binErrorStrategy = binErrorStrategy;
	}



	public int getEmptyBinStrategy() {
		return emptyBinStrategy;
	}



	public void setEmptyBinStrategy(int emptyBinStrategy) {
		this.emptyBinStrategy = emptyBinStrategy;
	}



	public String getErrValue() {
		return errValue;
	}



	public void setErrValue(String errValue) {
		this.errValue = errValue;
	}



	public String getUnkValue() {
		return unkValue;
	}



	public void setUnkValue(String unkValue) {
		this.unkValue = unkValue;
	}



	public double getLegitLow() {
		return legitLow;
	}



	public void setLegitLow(double legitLow) {
		this.legitLow = legitLow;
	}



	public double getLegitHigh() {
		return legitHigh;
	}



	public void setLegitHigh(double legitHigh) {
		this.legitHigh = legitHigh;
	}


	public ArrayList<C3POPatientPropertyMeasurement> getValues() {
		return values;
	}



	public void setValues(ArrayList<C3POPatientPropertyMeasurement> values) {
		this.values = values;
	}

	public void setDiscrete(String discrete) {
		this.discrete = discrete;
	}
	
	public String getDiscrete() {
		return discrete;
	}

	/**
	 * evaluate() does the actual derivation of bin values based on 
	 * @param binner
	 * @return
	 * @throws Exception
	 */
	public abstract boolean evaluate(FEBinnerBase binner) throws Exception;
	
	
	/**
	 * getWekaAttributeType returns the weka type that will be used for features derived
	 * using this evaluator, e.g. NUMERIC for BasicEvaluator, {val1,val2,...valn} for 
	 * POFeatureExtractionDiscretizingRangeEvaluator
	 * @return
	 */
	
	public abstract String getWekaAttributeType();
	
	/**
	 * toWekaString renders the evaluator's values as a comma-separated weka-type feature ArrayList substring
	 * needs the evaluation to have happened first. 
	 * takes the binner so that we know how many bins to emit.
	 * ************************************************************************************************
	 * ************************************************************************************************
	 * ************************************************************************************************
	 * NOTE THAT IF THE VALUES ArrayList IS NULL, THE CURRENT STANDARD IS TO EMIT ALL ? FOR THE
	 * SEGMENT FOR THIS EVALUATOR'S PROPERTY. That way, there is always a legitimate segmment for
	 * every property - once I write the postprocess that knits together several reducers' versions
	 * of a given patient's ArrayList, it should look segment by segment and take any segment that isn't
	 * all-"?", otherwise stick with all-"?". 
	 * LATER MAY HAVE A MECHANISM FOR SOME OTHER POLICY LIKE EMIT ERRORS FOR NULL
	 * ************************************************************************************************
	 * ************************************************************************************************
	 * ************************************************************************************************
	 * @return
	 * @throws Exception
	 */
	public abstract String toWekaString(FEBinnerBase binner) throws Exception;
	
	/**
	 * getFeatureNameInfix returns a string that characterizes the subclass for readability in Weka (or other) feature
	 * names, e.g. say we're extracting features for a property diasabp using a basic evaluator, the basic
	 * evaluator might return "ebasic" as its infix, leading to a feature name like diasabp_(binner infix)_ebasic_(strategy index)_bin(bin number)
	 * This allows us to have multiple strategies for a property and have unique feature names among them.
	 * @return
	 */
	public abstract String getFeatureNameInfix(); 
	
	/**
	 * instantiateFrom parses a parameter block from the feature extraction strategy file
	 * (later there will be an instantiateFromOntology, probably)
	 * @param paramBlock - tokens from a config file, see FeatureExtractionConfiguration
	 * @param startLine - line number in script file where its contents start, for error reporting
	 * @return
	 * @throws Exception
	 */
	public abstract boolean instantiateFrom(String paramBlock, int startLine) throws Exception;

	public abstract String getDescreteValue(String f);
}
