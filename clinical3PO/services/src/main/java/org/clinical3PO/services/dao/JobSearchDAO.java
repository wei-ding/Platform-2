package org.clinical3PO.services.dao;

import java.sql.Connection;
//import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import org.clinical3PO.model.JobSearchMapper;
import org.clinical3PO.model.JobSearchParameter;
import org.clinical3PO.model.JobSearchParameterMapper;
import org.clinical3PO.services.constants.JobStatus;
import org.clinical3PO.services.dao.model.JobSearch;



public class JobSearchDAO extends JdbcDaoSupport {

	public void insertJob(JobSearch jobSearch) {

		final String insertJob = "INSERT INTO searchrepository "
				+ "(createdDate, outputFileName,outputDirectory, searchOn, searchStartTime,status,hadoopOutputDirectory,searchBy) "
				+ "VALUES (?,?,?,?,?,?,?,?)";

		final JobSearch jobSearch1 = jobSearch;

		KeyHolder keyHolder = new GeneratedKeyHolder();

		getJdbcTemplate().update(
			new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps = connection.prepareStatement(insertJob, Statement.RETURN_GENERATED_KEYS);
					Timestamp t1 = new Timestamp(jobSearch1.getCreatedDate().getTime());
					t1.setNanos(0);
					ps.setTimestamp(1, t1);
					ps.setString(2, jobSearch1.getOutputFileName());
					ps.setString(3, jobSearch1.getOutputDirectory());
					ps.setString(4, jobSearch1.getSearchOn());
					Timestamp t2 = new Timestamp(jobSearch1.getSearchStartTime().getTime());
					t2.setNanos(0);
					ps.setTimestamp(5, t2);
					ps.setString(6, jobSearch1.getStatus());
					ps.setString(7, jobSearch1.getHadoopOutputDirectory());
					ps.setInt(8, jobSearch1.getSearchBy());
					return ps;
				}
			},
			keyHolder);

		Integer id = keyHolder.getKey().intValue();

		jobSearch.setId(id);
		insertJobParameters(jobSearch);
	}
	
	private void insertJobParameters(JobSearch jobSearch){
		
		final List<JobSearchParameter> searchParametersList = jobSearch.getSearchParameters();
		final int jobId=jobSearch.getId();
		
		String sql = "insert into searchparameters (jobId, `key`, `value`,groupId) VALUES (?, ?, ?,?)";
	 
		getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				JobSearchParameter jobSearchParamter = searchParametersList.get(i);
				ps.setInt(1, jobId);
				ps.setString(2, jobSearchParamter.getKey());
				ps.setString(3, jobSearchParamter.getValue());  
				ps.setInt(4, jobSearchParamter.getGroupId());
			}
		 
			@Override
			public int getBatchSize() {
				return searchParametersList.size();
			}
		});
	}
	
	public void updateJob(JobSearch jobSearch) {

		String updateJob = "update searchrepository set searchEndTime=?, status=?, modifiedDate=? where id=?";

		getJdbcTemplate().update(
				updateJob,
				new Object[] { 
						jobSearch.getSearchEndTime(),
						jobSearch.getStatus(),
						jobSearch.getModifiedDate(),
						jobSearch.getId()
				});

	}

	public List<JobSearch> getJobs(int id) {
		String allJobs = "select * from searchrepository where searchBy="+id+" order by createdDate desc";
		List<JobSearch> jobList=getJdbcTemplate().query(allJobs,new JobSearchMapper());
		
		for (JobSearch jobSearch : jobList) {
			jobSearch.setSearchParameters(getJobParameters(jobSearch.getId()));
		}
		return jobList;
	}
	
	public List<JobSearch> getJobs(JobStatus jobStatus) {
		String allJobsWithStatus = "select * from searchrepository where status=?";
		return getJdbcTemplate().query(allJobsWithStatus,new JobSearchMapper(),new Object[] {jobStatus.getJobStatus()});
	}

	public JobSearch getJob(int id) {
		String sql = "select * from searchrepository where id=?";

		JobSearch jobSearch = null;
		try {
			jobSearch=(JobSearch)getJdbcTemplate().queryForObject(sql, new Object[] { id }, new JobSearchMapper());
			jobSearch.setSearchParameters(getJobParameters(id));
		} catch (EmptyResultDataAccessException  exp){
		}
		
		return jobSearch;
	}
	
	private List<JobSearchParameter> getJobParameters(int id){
	
		String jobSearchParametersSql = "select * from (select * from searchparameters where jobId=? order by groupId) t order by id";
		return getJdbcTemplate().query(jobSearchParametersSql, new Object[] { id }, new JobSearchParameterMapper());

	}

}
