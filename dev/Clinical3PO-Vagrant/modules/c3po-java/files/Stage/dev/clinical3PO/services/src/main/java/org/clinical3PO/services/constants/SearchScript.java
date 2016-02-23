package org.clinical3PO.services.constants;

public enum SearchScript {
	
	PATIENTID("1"), OBSERVATIONID("2"), 
	PATIENTIDRESTRICTED("3"), OBSERVATIONIDRESTRICTED("4"),
	PATIENTIDUSERPREFS("5"), BATCHSEARCHRESTRICTED("6"),
	CROSSVALIDATION("7"), PATIENTIDHIVE("8"), OBSERVATIONIDHIVE("9"),
	PATIENTIDUSERPREFSHIVE("10"),PATIENTIDR("11"),OBSERVATIONIDR("12"),PATIENTIDUSERPREFSR("13"),
	FEMLFLEX("14"), NLPSEARCH("15"), FEUGENE("16");
	
	private String searchScript;
	
	private SearchScript(String searchScript){
		this.searchScript=searchScript;
	}
	
	public String getSearchScript(){
		return searchScript;
	}
}
