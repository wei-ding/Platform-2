package org.clinical3PO.learn.util;

public class FEDoubleValue extends
		FEValueBase {
	
	//default value
	public static final double DefaultValue = -1.0;
	
	double value;
	
	public FEDoubleValue() {
		super();
		value = DefaultValue;
	}

	public FEDoubleValue(double val) {
		super();
		value = val;
	}

	
	
	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	@Override
	public String getFeatureNameInfix() {
		return "dbl";
	}

	@Override
	public boolean instantiateFrom(String paramBlock, int startLine)
			throws Exception {
		// Currently there isn't anything we need out of a param block, so allow anything.
		return true;
	}

	//getValueForString is easy for this one: just get the double value (exception will be thrown if it's unsuitable.)
	//maybe trim the token, just to be on the safe side.
	@Override
	public Object getValueForString(String token) throws Exception {
		return Double.valueOf(token.trim());
	}

}
