package org.clinical3PO.services.dao.model;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

import org.clinical3PO.model.JobSearchParameter;

public class JobSearch {
	private int id;
	private Date createdDate;
	private String outputFileName;
	private String searchOn;
	// This is local directory where the hadoop output files are copied.
	private String outputDirectory;
	private Date searchStartTime;
	private Date searchEndTime;
	private String status;
	// This is directory under hadoop
	private String hadoopOutputDirectory;
	private Date modifiedDate;
	private int searchBy;
	private List<JobSearchParameter> searchParameters;

	public JobSearch(){
	}

	public String getOutputDirectory() {
		return outputDirectory;
	}
	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public String getOutputFileName() {
		return outputFileName;
	}
	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}
	public String getSearchOn() {
		return searchOn;
	}
	public void setSearchOn(String searchOn) {
		this.searchOn = searchOn;
	}

	public Date getSearchStartTime() {
		return searchStartTime;
	}

	public void setSearchStartTime(Date searchStartTime) {
		this.searchStartTime = searchStartTime;
	}

	public Date getSearchEndTime() {
		return searchEndTime;
	}

	public void setSearchEndTime(Date searchEndTime) {
		this.searchEndTime = searchEndTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getHadoopOutputDirectory() {
		return hadoopOutputDirectory;
	}

	public void setHadoopOutputDirectory(String hadoopOutputDirectory) {
		this.hadoopOutputDirectory = hadoopOutputDirectory;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public int getSearchBy() {
		return searchBy;
	}

	public void setSearchBy(int searchBy) {
		this.searchBy = searchBy;
	}

	public List<JobSearchParameter> getSearchParameters() {
		return searchParameters;
	}

	public void setSearchParameters(List<JobSearchParameter> searchParameters) {
		this.searchParameters = searchParameters;
	}

	public String toString() {
		return new ToStringBuilder(this)
		.append("id", id)
		.append("createdDate", createdDate)
		.append("outputFileName", outputFileName)
		.append("SearchOn", searchOn)
		.append("SearchParameters", searchParameters)
		.append("outputDirectory", outputDirectory)
		.append("searchStartTime", searchStartTime)
		.append("searchEndTime", searchEndTime)
		.append("status",status)
		.append("hadoopOutputDirectory",hadoopOutputDirectory)
		.toString();

	}

}
