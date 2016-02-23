package org.clinical3PO.test.data.builder;
//This is the  Builder class to configure data for  JobSearchDetails Class in  Services.
import org.clinical3PO.model.JobSearchDetails;
import org.springframework.test.util.ReflectionTestUtils;


public class JobSearchDetailsBuilder {

	private JobSearchDetails jobSearchDetails;


	public JobSearchDetailsBuilder(){
		jobSearchDetails= new JobSearchDetails();
	}
	public JobSearchDetailsBuilder searchOn(String searchOn)
	{
		ReflectionTestUtils.setField(jobSearchDetails,"searchOn",searchOn);
		return this;
	}
	public JobSearchDetailsBuilder searchType(String searchType)
	{
		ReflectionTestUtils.setField(jobSearchDetails,"searchType",searchType);
		return this;
	}
	public JobSearchDetailsBuilder searchParameters(String searchParameters)
	{
		ReflectionTestUtils.setField(jobSearchDetails,"searchParameters",searchParameters);
		return this;
	}
	public JobSearchDetailsBuilder scriptParameters(String scriptParameters)
	{
		ReflectionTestUtils.setField(jobSearchDetails,"scriptParameters",scriptParameters);
		return this;
	}
	public JobSearchDetailsBuilder scriptType(String scriptType)
	{
		ReflectionTestUtils.setField(jobSearchDetails,"scriptType",scriptType);
		return this;
	}
	public JobSearchDetails Build(){
		return jobSearchDetails;
	}
}
