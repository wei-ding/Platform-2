package org.clinical3PO.test.data.builder;
//This is the  Builder class to configure data for  JobSearchParameter Class in  Services.
import org.clinical3PO.model.JobSearchParameter;
import org.springframework.test.util.ReflectionTestUtils;

public class JobSearchParameterBuilder {

	private JobSearchParameter jobSearchParameter;

	public JobSearchParameterBuilder(){
		jobSearchParameter= new JobSearchParameter();
	}
	public JobSearchParameterBuilder key(String key)
	{
		ReflectionTestUtils.setField(jobSearchParameter,"key",key);
		return this;
	}
	public JobSearchParameterBuilder value(String value)
	{
		ReflectionTestUtils.setField(jobSearchParameter,"value",value);
		return this;
	}
	public JobSearchParameterBuilder groupId(int groupId)
	{
		ReflectionTestUtils.setField(jobSearchParameter,"groupId",groupId);
		return this;
	}
	public JobSearchParameter Build(){
		return jobSearchParameter;
	}

}
