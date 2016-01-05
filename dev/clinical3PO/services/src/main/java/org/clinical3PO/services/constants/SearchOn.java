package org.clinical3PO.services.constants;

public enum SearchOn {
	PATIENTID("Patient ID"), PATIENTNAME("Patient Name"), OBSERVATIONID("Observation ID"), 
	PATIENTIDRESTRICTED("Patient ID Restricted"), OBSERVATIONIDRESTRICTED("Observation ID Restricted"),
	PATIENTIDUSERPREFS("Patient ID User Prefs"), PATIENTIDUSERPREFSBATCH("Patient ID User Prefs - Batch"), BATCHSEARCHRESTRICTED("Patient ID User Prefs - Batch Restricted"),
	CROSSVALIDATION("Cross Validation"), PATIENTIDHIVE("Patient ID Hive"), OBSERVATIONIDHIVE("Observation ID Hive"),
	PATIENTIDUSERPREFSBATCHHIVE("Patient ID User Prefs - Hive"),PATIENTIDR("Patient ID R"),OBSERVATIONIDR("Observation ID R"),
	PATIENTIDUSERPREFSBATCHR("Patient ID User Prefs R- Batch"), NLPse("NLP To Hive Update"),
	FEMLFLEX("Feature Extraction - MlFlex"), FEUGENE("Feature Extraction - Ugene");
	
	private String searchOn;
	
	private SearchOn(String searchOn){
		this.searchOn=searchOn;
	}
	
	public String getSearchOn(){
		return searchOn;
	}
}
