package org.clinical3PO.common.form.beans;

import java.util.ArrayList;
import java.util.List;

public class PatientSearchList {

	private List<PatientSearch> personList = new ArrayList<PatientSearch>();

	public PatientSearchList() {
	}

	public PatientSearchList(List<PatientSearch> personList) {
		this.personList = personList;
	}

	public List<PatientSearch> getPersonList() {
		return personList;
	}

	public void setPersonList(List<PatientSearch> personList) {
		this.personList = personList;
	}

}
