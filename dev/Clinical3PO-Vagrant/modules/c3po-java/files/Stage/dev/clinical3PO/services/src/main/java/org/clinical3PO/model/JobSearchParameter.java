package org.clinical3PO.model;

public class JobSearchParameter {
	private String key;
	private String value;
	private int groupId;
	
	public JobSearchParameter() {
		
	}
	
	public JobSearchParameter(String key, String value,int groupId) {
		this.key=key;
		this.value=value;
		this.groupId=groupId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	
}
