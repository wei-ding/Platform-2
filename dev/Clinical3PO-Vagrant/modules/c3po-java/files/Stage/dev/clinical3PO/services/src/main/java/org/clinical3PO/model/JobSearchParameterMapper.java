package org.clinical3PO.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class JobSearchParameterMapper implements RowMapper<JobSearchParameter> {
	@Override
	public JobSearchParameter mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		JobSearchParameter jobSearchParameter = new JobSearchParameter();
		
		jobSearchParameter.setGroupId(rs.getInt("groupId"));
		jobSearchParameter.setKey(rs.getString("key"));
		jobSearchParameter.setValue(rs.getString("value"));
		
		return jobSearchParameter;
	}
}
