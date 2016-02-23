package org.clinical3PO.learn.util;

public abstract class FEStrategyBase {
	//objects to do the various stages of feature extraction from a bunch of
	//raw time/value pairs.
	FEBinnerBase binner;
	FEEvaluatorBase evaluator;
	FEValueBase valueType;
	
	//flags for saying whether the binner is from another property's strategy, so that
	//no changes will be made to it
	boolean usesOtherPropertyBinner;
	boolean usesOtherPropertyEvaluator;
	boolean usesOtherPropertyValueType;
	
	//and the property to which this applies
	String propertyName;
	
	public FEStrategyBase() {
		//POSSIBLY TEMP: set up a valueType of DoubleValueType - so that strategies can assume it.
		//They can replace it if they want.
		//valueType = new C3POFeatureExtractionDoubleValueType();
		//now required to be explicit
		binner = null;
		evaluator = null;
		valueType = null;
		usesOtherPropertyBinner = false;
		usesOtherPropertyEvaluator = false;
		usesOtherPropertyValueType = false;
		
		valueType = null;
	}
	
	/**
	 * @param propName
	 */
	
	public FEStrategyBase(String propName) {
		//TODO what would super(string, string) call do? 
		propertyName = propName;
		//POSSIBLY TEMP: set up a valueType of DoubleValueType - so that strategies can assume it.
		//They can replace it if they want.
		//now required to be explicit.
		//valueType = new C3POFeatureExtractionDoubleValueType();
		binner = null;
		evaluator = null;
		valueType = null;
		usesOtherPropertyBinner = false;
		usesOtherPropertyEvaluator = false;
		usesOtherPropertyValueType = false;
	}
	
	/* let's not use this, I think, gets confusing with the uses-other
	public C3POFeatureExtractionStrategy(C3POFeatureExtractionBinner nubin, C3POFeatureExtractionEvaluator nuval, C3POFeatureExtractionValueType valtype, String nuprop) {
		binner = nubin;
		evaluator = nuval;
		propertyName = nuprop;
		valueType = valtype;
	}
	*/
	
	public FEBinnerBase getBinner() {
		return binner;
	}



	public void setBinner(FEBinnerBase binner) {
		this.binner = binner;
	}



	public FEEvaluatorBase getEvaluator() {
		return evaluator;
	}



	public void setEvaluator(FEEvaluatorBase evaluator) {
		this.evaluator = evaluator;
	}



	public FEValueBase getValueType() {
		return valueType;
	}

	public void setValueType(FEValueBase valueType) {
		this.valueType = valueType;
	}

	public boolean usesOtherPropertyBinner() {
		return usesOtherPropertyBinner;
	}

	public void setUsesOtherPropertyBinner(boolean usesOtherPropertyBinner) {
		this.usesOtherPropertyBinner = usesOtherPropertyBinner;
	}

	public boolean usesOtherPropertyEvaluator() {
		return usesOtherPropertyEvaluator;
	}

	public void setUsesOtherPropertyEvaluator(boolean usesOtherPropertyEvaluator) {
		this.usesOtherPropertyEvaluator = usesOtherPropertyEvaluator;
	}

	public boolean usesOtherPropertyValueType() {
		return usesOtherPropertyValueType;
	}

	public void setUsesOtherPropertyValueType(boolean usesOtherPropertyValueType) {
		this.usesOtherPropertyValueType = usesOtherPropertyValueType;
	}

	public String getPropertyName() {
		return propertyName;
	}



	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	/**
	 * instantiateFrom parses a parameter block from the feature extraction strategy file
	 * (later there will be an instantiateFromOntology, probably)
	 * @param paramBlock - tokens from a config file, see FeatureExtractionConfiguration
	 * @param startLine - line number in script file where its contents start, for error reporting
	 * @return
	 * @throws Exception
	 */
	//PROBABLY NOT USING ANYMORE
	//public abstract boolean instantiateFrom(String paramBlock, int startLine) throws Exception;


	/**
	 * getFeatureNameRoot returns a string that characterizes the subclass for readability in Weka (or other) feature
	 * names, e.g. say we're extracting features for a property diasabp using a fixed width binner, the fixed width
	 * binner might return "bfix" as its infix, leading to a feature name like diasabp_(binner_infix)_(evaluator infix)_(strategy index)_bin(bin number)
	 * "root" because this doesn't handle the strategy index or whatever might follow
	 * This allows us to have multiple strategies for a property and have unique feature names among them.
	 * non-abstract because this default implementation will probably do for most everything
	 * @return
	 */
	public String getFeatureNameRoot() {
		// TODO Auto-generated method stub
		return propertyName +  "_" + binner.getFeatureNameInfix() + evaluator.getFeatureNameInfix();
	}
	
}
