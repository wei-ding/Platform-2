package org.clinical3PO.services.constants;

public enum JobSearchConstants {
	PATIENTIDS("Patient IDS"), PATIENTID("Patient ID"), OBSERVATIONID("Observation ID"), OBSERVATIONIDS("Observation IDS"), COLORCODE("Color Code"), 
	BINS("Number of bins"), CLASSIFIER("Classification Algorithm"), FOLDS("Number of Folds"), NoITERATIONS("Number of Iterations");
	
	private String searchKey;
	
	private JobSearchConstants(String searchKey){
		this.searchKey=searchKey;
	}
	
	public String getSearchKey(){
		return searchKey;
	}
}
