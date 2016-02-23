package org.clinical3PO.model;

public class JobSearchDetails {
	
	private String searchOn;
	private String searchType;
	private String searchParameters;
	private String scriptParameters;
	private String scriptType;
	private String accumuloRoles;
	
	public JobSearchDetails(){
		searchOn="";
		searchType="";
		searchParameters="";
		scriptParameters="";
		accumuloRoles="";
	}
	
	public String getSearchOn() {
		return searchOn;
	}
	public void setSearchOn(String searchOn) {
		this.searchOn = searchOn;
	}
	public String getSearchType() {
		return searchType;
	}
	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}
	public String getSearchParameters() {
		return searchParameters;
	}
	public void setSearchParameters(String searchParameters) {
		this.searchParameters = searchParameters;
	}

	public String getScriptType() {
		return scriptType;
	}

	public void setScriptType(String scriptType) {
		this.scriptType = scriptType;
	}

	public String getScriptParameters() {
		return scriptParameters;
	}

	public void setScriptParameters(String scriptParameters) {
		this.scriptParameters = scriptParameters;
	}

	public String getAccumuloRoles() {
		return accumuloRoles;
	}

	public void setAccumuloRoles(String accumuloRoles) {
		this.accumuloRoles = accumuloRoles;
	}
}
