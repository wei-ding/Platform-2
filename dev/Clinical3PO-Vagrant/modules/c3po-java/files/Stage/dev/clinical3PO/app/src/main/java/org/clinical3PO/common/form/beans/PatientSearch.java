package org.clinical3PO.common.form.beans;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class PatientSearch {

	private String patientId;
	private String patientIds;
	private String observationId;
	private String observationIds;
	private String colorCode;

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getPatientIds() {
		return patientIds;
	}

	public void setPatientIds(String patientIds) {
		this.patientIds = patientIds;
	}

	public String getObservationId() {
		return observationId;
	}

	public void setObservationId(String observationId) {
		this.observationId = observationId;
	}

	public String getObservationIds() {
		return observationIds;
	}

	public void setObservationIds(String observationIds) {
		this.observationIds = observationIds;
	}

	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}

	public String toString() {

		return new ToStringBuilder(this).append("patientId", patientId)
				.append("patientIds", patientIds)
				.append("observationId", observationId)
				.append("observationIds", observationIds)
				.append("colorCode", colorCode).toString();

	}
}
