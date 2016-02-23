package org.clinical3PO.learn.util;

/**
 * this class represents the strategy for converting patient measurements to weka feature values
 * FOR ONE PROPERTY. so, you'll have one of these for each property you care about in a set of 
 * patient data files, or more likely one for each that has a non-default behavior plus one for handling
 * default cases.
 * @author u0176876
 *
 */
public class FEBasicStrategy extends FEStrategyBase {

	
	public FEBasicStrategy() {
		//TODO: THIS SHOULD INSTANTIATE THE DEFAULT EXTRACTOR.
		//currently I think of this as hour-wide bins over the global or propspec time range,
		//continuous value derived from averaging bin members,  
		//...hm.
		//let's null these out, make the user specify defaults.
		//was super(new C3POFeatureExtractionFixedWidthBinner(),new C3POFeatureExtractionBasicEvaluator(),new C3POFeatureExtractionDoubleValueType(),"");
		super("");
	}

	public FEBasicStrategy(String propName) {
		//TODO: THIS SHOULD INSTANTIATE THE DEFAULT EXTRACTOR.
		//currently I think of this as hour-wide bins over the global or propspec time range,
		//continuous value derived from averaging bin members,  
		//let's null these out, make the user specify defaults.
		//was super(new C3POFeatureExtractionFixedWidthBinner(),new C3POFeatureExtractionBasicEvaluator(),new C3POFeatureExtractionDoubleValueType(),propName);
		super(propName);
	}
	
	//TODO: methods
	//- TODO: one for doBinning with time range handed in as a parameter - that gets thrown
	//  to the binning object. Takes in a treemap of Long to Vector<Double>, I think, where the key is the
	//  interpreted version a la C3POTimeRange.interpretTimeStamp() and the value is all 
	//  values for the relevant property reported at that exact time stamp (there could be more than one.)
	//  result is a Vector of whatever the values are - Vector<Double>? How does that 
	//  generalize to discretized types? May need a FeatureValue type which can be subclassed
	//  into Continuous or Discrete or whatever.
	//  Actually that's going a bit too far; it should just return a Vector<Vector<Double>>,
	//  where element(n) is the set of values for bin n, and might be empty.
	//  - only need time range and raw values, bin width or whatever is a property
	//    of the binning object itself. 
	//  - THIS IS NOT A METHOD OF THIS CLASS, it's a method call on a binning object.
	//- TODO: one to create the values from the bins - or can that be further subdivided?
	//  takes the result of doBinning as an input
	
	/**
	 * instantiateFrom parses a parameter block from the feature extraction strategy file
	 * (later there will be an instantiateFromOntology, probably)
	 * @param paramBlock - tokens from a config file, see FeatureExtractionConfiguration
	 * @param startLine - line number in script file where its contents start, for error reporting
	 * @return
	 * @throws Exception
	 */
	//PROBABLY WON'T USE
	//@Override
	//public boolean instantiateFrom(String paramBlock, int startLine) throws Exception {
	//	return false; 				//TODO write real guts
	//}
	
}
