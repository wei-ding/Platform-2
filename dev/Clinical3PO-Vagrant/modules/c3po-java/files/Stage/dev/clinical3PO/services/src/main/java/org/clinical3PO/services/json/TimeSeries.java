package org.clinical3PO.services.json;

public class TimeSeries implements Comparable<TimeSeries>{
	private String time;
	private String value;

	@Override
	public int compareTo(TimeSeries compareTimeSeries) {
		float compareTime=Float.parseFloat(((TimeSeries)compareTimeSeries).getTime());
		float queryTime=Float.parseFloat(this.time);
		if (queryTime>compareTime){
			return 1;
		}
		else{
			return -1;
		}		
	}

	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
