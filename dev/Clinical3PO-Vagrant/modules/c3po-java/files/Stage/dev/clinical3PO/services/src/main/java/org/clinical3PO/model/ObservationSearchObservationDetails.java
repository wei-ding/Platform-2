package org.clinical3PO.model;

public class ObservationSearchObservationDetails {

	private String observation_date;
	private String observation_time;
	private String value_as_number;
	private String value_units;
	private String property_definition;
	private String death;
	private String patientId;

	public ObservationSearchObservationDetails() {

		observation_date="";
		observation_time="";
		value_as_number="";
		value_units="";
		property_definition="";
		death = "";
		patientId = "";
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

	public String getPropertyDefinition() {
		return property_definition;
	}

	public void setPropertyDefinition(String property_definition) {
		this.property_definition = property_definition;
	}

	public String getDeath() {
		return death;
	}

	public void setDeath(String death) {
		this.death = death;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String toString() {

		return new StringBuffer().append(patientId).append("\t")
				.append(observation_date).append(";")
				.append(observation_time).append(";")
				.append(value_as_number).append(";")
				.append(value_units).append(";")
				.append(property_definition).append(";")
				.append(death).append(";").toString();
	}
}