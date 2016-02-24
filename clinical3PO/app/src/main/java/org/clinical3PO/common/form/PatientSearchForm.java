package org.clinical3PO.common.form;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class PatientSearchForm {
		
	@Size(min=3, max=8)
	@NotNull
	
	private String patientId;

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}
}
