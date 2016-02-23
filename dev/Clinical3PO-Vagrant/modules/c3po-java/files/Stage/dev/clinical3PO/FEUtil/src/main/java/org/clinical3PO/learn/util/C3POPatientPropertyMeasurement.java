package org.clinical3PO.learn.util;


public class C3POPatientPropertyMeasurement {
	/** 
	 * little inner class for recording value/status pairs for features
	 * @author sean
	 *
	 */
	public static final String okFeatureStatus = "OK";
	public static final String errorFeatureStatus = "err";
	public static final String unknownFeatureStatus = "?";
	public static final String statusFeatureType = "{"+okFeatureStatus+","+errorFeatureStatus+","+unknownFeatureStatus+"}";
	
	//TODO: CHANGE THE VALUE TO BE A THING BASED ON C3POFeatureExtractionValueType
	//- here we will probably store an Object and the value type does validation of those.
	public Object value;		// value
	public String status;		// OK, err, etc
	public long timestamp;		// in seconds, a la C3POTimeRange timestamps, qv.
	public boolean isWithinTimeRange;	//true if this measurement was taken within the time range for the binner that owns this measurement.

	public C3POPatientPropertyMeasurement(Object v, String t) throws Exception {
		value = v;
		status = C3POPatientPropertyMeasurement.okFeatureStatus;
		timestamp = C3POTimeRange.interpretTimeStamp(t);
		isWithinTimeRange = true;
	}

	public C3POPatientPropertyMeasurement(Object v, long timeInSeconds) throws Exception {
		value = v;
		status = C3POPatientPropertyMeasurement.okFeatureStatus;
		timestamp = timeInSeconds;
		isWithinTimeRange = true;
	}
	
	public C3POPatientPropertyMeasurement(Object v, String s, String t) throws Exception {
		value = v;
		status = s;
		timestamp = C3POTimeRange.interpretTimeStamp(t);
		isWithinTimeRange = true;
	}

	public C3POPatientPropertyMeasurement(Object v, String s, long timeInSeconds) throws Exception {
		value = v;
		status = s;
		timestamp = timeInSeconds;
		isWithinTimeRange = true;
	}
}
