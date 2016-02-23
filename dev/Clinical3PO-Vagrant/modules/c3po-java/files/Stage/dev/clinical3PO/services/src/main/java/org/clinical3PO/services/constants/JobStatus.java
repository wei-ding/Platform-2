package org.clinical3PO.services.constants;

public enum JobStatus {
	INPROGRESS("IN-PROGRESS"), FAILED("FAILED"), FINISHED("FINISHED");

	private String jobStatus;

	private JobStatus(String jobStatus){
		this.jobStatus=jobStatus;
	}

	public String getJobStatus(){
		return jobStatus;
	}

}
