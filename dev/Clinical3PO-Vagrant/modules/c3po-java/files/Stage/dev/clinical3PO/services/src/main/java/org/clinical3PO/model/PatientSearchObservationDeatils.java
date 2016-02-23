package org.clinical3PO.model;



public class PatientSearchObservationDeatils {

	private String property_name;
	private String observation_date;
	private String observation_time;
	private String value_as_number;
	private String value_units;
	
	public PatientSearchObservationDeatils(){
		property_name="";
		observation_date="";
		observation_time="";
		value_as_number="";
		value_units="";
	}

	public String getPropertyName() {
		return property_name;
	}

	public void setPropertyName(String property_name) {
		this.property_name = property_name;
	}

	public String getObservationDate() {
		return observation_date;
	}

	public void setObservationDate(String observation_date) {
		this.observation_date = observation_date;
	}

	public String getObservationTime() {
		return observation_time;
	}

	public void setObservationTime(String observation_time) {
		this.observation_time = observation_time;
	}

	public String getValueAsNumber() {
		return value_as_number;
	}

	public void setValueAsNumber(String value_as_number) {
		this.value_as_number = value_as_number;
	}

	public String getValueUnits() {
		return value_units;
	}

	public void setValueUnits(String value_units) {
		this.value_units = value_units;
	}
	
	public String toString() {
		
		return new StringBuffer().append(property_name).append(";")
				.append(observation_date).append(";")
				.append(observation_time).append(";")
				.append(value_as_number).append(";")
				.append(value_units).append(";").toString();
	}
}