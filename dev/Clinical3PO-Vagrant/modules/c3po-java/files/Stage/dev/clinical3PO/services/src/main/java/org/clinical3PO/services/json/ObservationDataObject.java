package org.clinical3PO.services.json;

import org.clinical3PO.services.json.TimeSeries;

public class ObservationDataObject {
    private String patient_number;  
    private String patient_id;  
    private String attribute;
    private String color;  
    
    private TimeSeries[] time_series;

	public String getPatient_number() {
		return patient_number;
	}

	public void setPatient_number(String patient_number) {
		this.patient_number = patient_number;
	}

	public String getPatient_id() {
		return patient_id;
	}

	public void setPatient_id(String patient_id) {
		this.patient_id = patient_id;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public TimeSeries[] getTimeSeries() {
		return time_series;
	}

	public void setTimeSeries(TimeSeries[] timeSeries) {
		this.time_series = timeSeries;
	}  
  
    //getter and setter methods  
  

}
