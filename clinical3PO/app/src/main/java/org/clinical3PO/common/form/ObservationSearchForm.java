package org.clinical3PO.common.form;

import org.clinical3PO.common.form.validator.ObservationPatientId;


public class ObservationSearchForm {
		
	@ObservationPatientId
	private String patientId;
	
	private String observationId;

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getObservationId() {
		return observationId;
	}

	public void setObservationId(String observationId) {
		this.observationId = observationId;
	}
}
