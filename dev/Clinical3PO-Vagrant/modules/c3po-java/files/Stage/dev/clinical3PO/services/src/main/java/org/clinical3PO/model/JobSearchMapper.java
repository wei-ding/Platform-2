package org.clinical3PO.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import org.clinical3PO.services.dao.model.JobSearch;

public class JobSearchMapper implements RowMapper<JobSearch> {

	@Override
	public JobSearch mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		JobSearch jobSearch = new JobSearch();
		
		jobSearch.setId(rs.getInt("id"));
		jobSearch.setOutputFileName(rs.getString("outputFileName"));
		jobSearch.setOutputDirectory(rs.getString("outputDirectory"));
		jobSearch.setStatus(rs.getString("status"));
		jobSearch.setHadoopOutputDirectory(rs.getString("hadoopOutputDirectory"));
		jobSearch.setSearchBy(rs.getInt("searchBy"));
		jobSearch.setSearchOn(rs.getString("searchOn"));
		
		jobSearch.setCreatedDate(rs.getTimestamp("createdDate"));
		jobSearch.setModifiedDate(rs.getTimestamp("modifiedDate"));
		jobSearch.setSearchStartTime(rs.getTimestamp("searchStartTime"));
		jobSearch.setSearchEndTime(rs.getTimestamp("searchEndTime"));
		
		return jobSearch;
	}
}
