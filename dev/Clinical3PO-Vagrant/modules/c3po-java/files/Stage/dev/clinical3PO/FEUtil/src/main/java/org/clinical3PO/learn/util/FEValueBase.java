package org.clinical3PO.learn.util;

public abstract class FEValueBase {
	
	//TODO: SOME WAY OF LETTING EVALUATOR KNOW WHAT TYPE THIS IS
	//TODO: SOME WAY OF LETTING EVALUATOR KNOW WHAT TYPE THIS IS
	//TODO: SOME WAY OF LETTING EVALUATOR KNOW WHAT TYPE THIS IS
	//TODO: SOME WAY OF LETTING EVALUATOR KNOW WHAT TYPE THIS IS
	//TODO: SOME WAY OF LETTING EVALUATOR KNOW WHAT TYPE THIS IS
	//TODO: SOME WAY OF LETTING EVALUATOR KNOW WHAT TYPE THIS IS
	//TODO: SOME WAY OF LETTING EVALUATOR KNOW WHAT TYPE THIS IS
	//might just check the class name
	
	//default ctor
	public FEValueBase() {
		
	}
	
	/**
	 * getFeatureNameInfix returns a string that characterizes the subclass for readability in Weka (or other) feature
	 * names, e.g. say we're extracting features for a property diasabp using a fixed width binner, the fixed width
	 * binner might return "bfix" as its infix, leading to a feature name like 
	 * diasabp_(binner infix)_(evaluator infix)_(strategy index)_(valuetype infix)
	 * This allows us to have multiple strategies for a property and have unique feature names among them.
	 * And value types!
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
	
	/**
	 * getValueForString is meant for use by the mapper, which gets in a list of timestamp-value pairs
	 * as strings, and calls this to turn the string value token into an Object to put in a
	 * C3POPatientPropertyMeasurement and then be processed by the binner and evaluator. Phew.
	 * @param token
	 * @return
	 * @throws Exception
	 */
	public abstract Object getValueForString(String token) throws Exception;
	
}
