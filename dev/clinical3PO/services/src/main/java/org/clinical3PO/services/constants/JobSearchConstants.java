package org.clinical3PO.services.constants;

public enum JobSearchConstants {
	PATIENTIDS("Patient IDS"), PATIENTID("Patient ID"), OBSERVATIONID("Observation ID"), OBSERVATIONIDS("Observation IDS"), COLORCODE("Color Code"), 
	BINS("Number of bins"), CLASSIFIER("Classification Algorithm"), FOLDS("Number of Folds"), NoITERATIONS("Number of Iterations"), 
	XML("Update Hive Table With NLP Genearted XML file details"), CLASSPROPERTY("Feature Extraction Class Property"),
	CLASSTIME("Feature Extraction Class Time"), STARTTIME("Feature Extraction Start Time"), ENDTIME("Feature Extraction End Time");
	
	private String searchKey;
	
	private JobSearchConstants(String searchKey){
		this.searchKey=searchKey;
	}
	
	public String getSearchKey(){
		return searchKey;
	}
}
