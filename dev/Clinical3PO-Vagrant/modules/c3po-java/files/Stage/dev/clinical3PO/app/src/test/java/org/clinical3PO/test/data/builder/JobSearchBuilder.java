package org.clinical3PO.test.data.builder;

// This is the  Builder class to configure data for  JobSearch Class in  Services.

import java.util.Date;
import java.util.List;

import org.clinical3PO.services.dao.model.JobSearch;
import org.clinical3PO.model.JobSearchParameter;
import org.springframework.test.util.ReflectionTestUtils;

public class JobSearchBuilder {

	private JobSearch jobSearch;

	public JobSearchBuilder(){
		jobSearch= new JobSearch();
	}
	public JobSearchBuilder id(int id)
	{
		ReflectionTestUtils.setField(jobSearch,"id",id);
		return this;
	}
	public JobSearchBuilder createdDate(Date createdDate)
	{
		ReflectionTestUtils.setField(jobSearch,"createdDate",createdDate);
		return this;
	}
	public JobSearchBuilder searchStartTime(Date searchStartTime)
	{
		ReflectionTestUtils.setField(jobSearch,"searchStartTime",searchStartTime);
		return this;
	}
	public JobSearchBuilder searchBy(int searchBy)
	{
		ReflectionTestUtils.setField(jobSearch,"searchBy",searchBy);
		return this;
	}
	public JobSearchBuilder outputFileName(String outputFileName)
	{
		ReflectionTestUtils.setField(jobSearch,"outputFileName",outputFileName);
		return this;
	}
	public JobSearchBuilder searchOn(String searchOn)
	{
		ReflectionTestUtils.setField(jobSearch,"searchOn",searchOn);
		return this;
	}
	public JobSearchBuilder outputDirectory(String outputDirectory)
	{
		ReflectionTestUtils.setField(jobSearch,"outputDirectory",outputDirectory);
		return this;
	}

	public JobSearchBuilder status(String status)
	{
		ReflectionTestUtils.setField(jobSearch,"status",status);
		return this;
	}
	public JobSearchBuilder hadoopOutputDirectory(String hadoopOutputDirectory)
	{
		ReflectionTestUtils.setField(jobSearch,"hadoopOutputDirectory",hadoopOutputDirectory);
		return this;
	}

	public JobSearchBuilder searchParameters(List<JobSearchParameter> searchParameters)
	{
		ReflectionTestUtils.setField(jobSearch,"searchParameters",searchParameters);
		return this;
	}
	public JobSearch Build(){
		return jobSearch;
	}



}
