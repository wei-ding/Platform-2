package org.clinical3PO.learn.util;

import java.util.ArrayList;

public class FEDiscreteValue extends
		FEValueBase {
	
	ArrayList<String> values;

	@Override
	public String getFeatureNameInfix() {
		return "dsc";
	}

	@Override
	public boolean instantiateFrom(String paramBlock, int startLine)
			throws Exception {
		
		//so, all we expect is a line with the word "values" and the comma+ws* separated list of
		//allowable values.
		//values verylow, low, normal , borderline, high
		
		throw new Exception("FEDiscreteValue.instantiateFrom not implemented yet");
		
		//return false;
	}

	//here just make sure the token, stripped of any whitespace, is in values.
	//Let's say it needs to be case-sensitive.
	@Override
	public Object getValueForString(String token) throws Exception {
		String striptok = token.trim();
		
		for(int j=0;j<values.size();j++) {
			if(striptok.equals(values.get(j))) return striptok;
		}
		
		throw new Exception("Value \"" + striptok + "\" not found in values (note they're case-sensitive)");
	}

}
