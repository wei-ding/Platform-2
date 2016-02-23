package org.clinical3PO.services;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.clinical3PO.common.environment.EnvironmentType;
import org.clinical3PO.model.JobSearchDetails;
import org.clinical3PO.model.JobSearchParameter;
import org.clinical3PO.services.common.utils.WebAppUtils;
import org.clinical3PO.services.constants.JobStatus;
import org.clinical3PO.services.constants.SearchOn;
import org.clinical3PO.services.dao.JobSearchDAO;
import org.clinical3PO.services.dao.model.JobSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobSearchService {

	private JobSearchDAO jobSearchDAO;
	private String hadoopShellScriptDirectory;
	private String commonSearchScript;
	private String hadoopLocalOutputDirectory;
	private String appDataDirectory;
	private String mlflexDirectory;

	@Autowired
	private EnvironmentType envType;
	
	@Autowired
	private WebAppUtils webAppUtils;

	private PatientSearchService patientSearchService;

	private static final Logger logger = LoggerFactory.getLogger(JobSearchService.class);

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)	
	public void searchJob(JobSearch jobSearch, JobSearchDetails jobSearchDetails){
		// This is directory under hadoop (part of bigger tree), but same would become file name locally
		String hadoopDirectory=(jobSearchDetails.getSearchType()+jobSearchDetails.getSearchParameters()
				+"Time"+new Timestamp(new Date().getTime())).replaceAll("[-:. \t]","");
		jobSearch.setSearchOn(jobSearchDetails.getSearchOn());
		jobSearch.setStatus(JobStatus.INPROGRESS.getJobStatus());
		jobSearch.setSearchStartTime(new Timestamp(new Date().getTime()));
		jobSearch.setCreatedDate(new Timestamp(new Date().getTime()));
		jobSearch.setOutputDirectory(getHadoopLocalOutputDirectory());
		jobSearch.setHadoopOutputDirectory(hadoopDirectory);
		jobSearch.setOutputFileName(hadoopDirectory+"Output");

		jobSearchDAO.insertJob(jobSearch);
		runJob(jobSearch,jobSearchDetails);
	}

	private void runJob(JobSearch jobSearch, JobSearchDetails jobSearchDetails) {
		
		if (envType == EnvironmentType.DEVELOPMENT && logger.isDebugEnabled()) {
			logger.debug("Calling hadoop/hive/accumulo script");
		}
		
		// Call hadoop/hive/accumulo job process
		String script=getHadoopShellScriptDirectory() + 
				File.separator+getCommonSearchScript() + " " + jobSearchDetails.getScriptType() + " "
				+ jobSearch.getHadoopOutputDirectory() + " "
				+ jobSearch.getOutputDirectory() + " "
				+ jobSearch.getOutputFileName() + " "
				+ jobSearch.getId() + " "
				+ getParameters(jobSearchDetails);

		if (envType == EnvironmentType.DEVELOPMENT && logger.isDebugEnabled()){
			logger.debug("Script Execution Details searchPatient : " + script);
		}

		try {
			Runtime.getRuntime().exec(script);
		} catch (IOException exp) {
			logger.error("Error executing the shell script :"+exp);
		}
	}

	public String getParameters(JobSearchDetails jobSearchDetails) {
		StringBuffer sbuff = new StringBuffer();

		if(jobSearchDetails.getSearchOn()==SearchOn.BATCHSEARCHRESTRICTED.getSearchOn()){			
			if(StringUtils.isNotBlank(jobSearchDetails.getScriptParameters())) {
				sbuff.append(StringUtils.strip(jobSearchDetails.getScriptParameters()).split("\\|")[1]);
				sbuff.append(" ");
			}
		}

		else {
			if(StringUtils.isNotBlank(jobSearchDetails.getScriptParameters())) {
				sbuff.append(StringUtils.strip(jobSearchDetails.getScriptParameters()));
				sbuff.append(" ");
			}
		}

		if(StringUtils.isNotBlank(jobSearchDetails.getAccumuloRoles())) {
			sbuff.append(StringUtils.strip(jobSearchDetails.getAccumuloRoles()));
		}

		if (envType == EnvironmentType.DEVELOPMENT && logger.isDebugEnabled()){ 
			logger.info("Parameters Sbuff: "+sbuff);
		}

		return sbuff.toString();
	}

	public String getScriptParameters(JobSearch jobSearch) {
		List<JobSearchParameter> searchParameters = jobSearch.getSearchParameters();

		int parametersSize = searchParameters.size();

		StringBuffer sbuff = new StringBuffer();

		for(int i=0;i<parametersSize;i++) {
			sbuff.append(StringUtils.strip(searchParameters.get(i).getValue()));
			sbuff.append(" ");
		}

		sbuff.setLength(sbuff.length() - 1);

		if (envType == EnvironmentType.DEVELOPMENT && logger.isDebugEnabled()){ 
			logger.info("Script Parameters Sbuff: "+sbuff);
		}

		return sbuff.toString();
	}

	public String getPatientSearchWithUserPrefsParameters(JobSearch jobSearch) {

		//Observation Id1~Patient Id1,Patient Id2~Color Scheme||Observation Id2~Patient Id3,Patient Id4~Color Scheme

		List<JobSearchParameter> searchParameters = jobSearch.getSearchParameters();

		// The order of insertion (db) patient ids, observation ids and color code.

		int index=0;
		String observationIds=null;
		String observations[] = null;
		String patientIds=null;
		String colorCode=null;

		int noOfParameters = searchParameters.size();
		StringBuffer sbuff = new StringBuffer();

		logger.info("Total number of parameters: "+noOfParameters);

		while (index < (noOfParameters-1)){

			observationIds = ((JobSearchParameter)searchParameters.get(index+1)).getValue();

			// Split the observation ids and repeat the below loop
			observations = observationIds.split(",");
			// Patient ids at 0, observation ids at 1, color code at 2, this pattern would repeat.

			patientIds=((JobSearchParameter)searchParameters.get(index)).getValue();
			colorCode=((JobSearchParameter)searchParameters.get(index+2)).getValue();

			for (String observation : observations) {
				sbuff.append(StringUtils.strip(observation)).append("~").append(StringUtils.strip(patientIds))
				.append("~").append(StringUtils.strip(colorCode)).append("#");				
			}

			index+=3;

			if(index >= noOfParameters) {
				// Remove the trailing pipes symbol.
				sbuff.setLength(sbuff.length() - 1);
			}
		}

		if (envType == EnvironmentType.DEVELOPMENT && logger.isDebugEnabled()){ 
			logger.info("Sbuff: "+sbuff);
		}

		return sbuff.toString();
	}

	public String getBatchSearchParameters(JobSearch jobSearch) {

		List<JobSearchParameter> searchParameters = jobSearch.getSearchParameters();
		int noOfParameters = searchParameters.size();

		StringBuffer sbuff = new StringBuffer();
		String tempSbuff;
		tempSbuff = getPatientSearchWithUserPrefsParameters(jobSearch);

		sbuff.append(tempSbuff).append("|").append(searchParameters.get(noOfParameters-1).getValue());  // Filename

		if (envType == EnvironmentType.DEVELOPMENT && logger.isDebugEnabled()){ 
			logger.info("Sbuff: "+sbuff);
		}

		return sbuff.toString();
	}

	public String getCrossValidationParameters(JobSearch jobSearch) {

		//sbuff = ObservationID1~PatientID1,PatientID2~NumberOfBins#ObservationID2~PatientID3,PatientID4~NumberOfBins ClassficationAlgorithm NumberOfFolds NumberOfIterations

		List<JobSearchParameter> searchParameters = jobSearch.getSearchParameters();

		String observationIds=null;
		String observations[] = null;
		String patientIds=null;
		String listOfBins=null;
		String classificationAlgorithm=null;
		String folds=null;
		String noIterations=null;
		String bins[]=null;
		int i=0;


		//		int noOfParameters = searchParameters.size();
		StringBuffer sbuff = new StringBuffer();

		//		logger.info("Total number of parameters: "+noOfParameters);

		observationIds = ((JobSearchParameter)searchParameters.get(1)).getValue();

		// Split the observation ids and repeat the below loop
		observations = observationIds.split(",");
		// Patient ids at 0, observation ids at 1, color code at 2, this pattern would repeat.

		patientIds = ((JobSearchParameter)searchParameters.get(0)).getValue();			
		listOfBins = ((JobSearchParameter)searchParameters.get(2)).getValue();
		classificationAlgorithm = ((JobSearchParameter)searchParameters.get(3)).getValue();
		folds = ((JobSearchParameter)searchParameters.get(4)).getValue();
		noIterations = ((JobSearchParameter)searchParameters.get(5)).getValue();

		bins = listOfBins.split(",");

		if (observations.length!=bins.length){
			logger.error("Mismatch between observation IDs and number of bins");
		}

		for (String observation : observations) {
			sbuff.append(StringUtils.strip(observation)).append("~").append(StringUtils.strip(patientIds))
			.append("~").append(StringUtils.strip(bins[i])).append("#");
			i++;
		}

		sbuff.setLength(sbuff.length() - 1);  // Remove the trailing '#'
		sbuff.append(" ").append(classificationAlgorithm);
		sbuff.append(" ").append(folds);
		sbuff.append(" ").append(noIterations);

		if (envType == EnvironmentType.DEVELOPMENT && logger.isDebugEnabled()){ 
			logger.info("Sbuff: "+sbuff);
		}

		logger.info("Sbuff: "+sbuff);

		return sbuff.toString();
	}

	public List<JobSearch> getJobs(int id) {
		return jobSearchDAO.getJobs(id);
	}

	public JobSearch getJob(int id) {
		return jobSearchDAO.getJob(id);
	}

	public List<JobSearch> getJobs(JobStatus jobStatus) {
		return jobSearchDAO.getJobs(jobStatus);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void updateJob(JobSearch jobSearch){
		jobSearchDAO.updateJob(jobSearch);
	}

	public JobSearchDAO getJobSearchDAO() {
		return jobSearchDAO;
	}

	public void setJobSearchDAO(JobSearchDAO jobSearchDAO) {
		this.jobSearchDAO = jobSearchDAO;
	}

	public String getHadoopShellScriptDirectory() {
		return hadoopShellScriptDirectory;
	}

	public void setHadoopShellScriptDirectory(String hadoopShellScriptDirectory) {
		this.hadoopShellScriptDirectory = hadoopShellScriptDirectory;
	}

	public String getCommonSearchScript() {
		return commonSearchScript;
	}

	public void setCommonSearchScript(String commonSearchScript) {
		this.commonSearchScript = commonSearchScript;
	}

	public String getHadoopLocalOutputDirectory() {
		return hadoopLocalOutputDirectory;
	}

	public void setHadoopLocalOutputDirectory(String hadoopLocalOutputDirectory) {
		this.hadoopLocalOutputDirectory = hadoopLocalOutputDirectory;
	}

	public String getMlflexDirectory() {
		return mlflexDirectory;
	}

	public void setMlflexDirectory(String mlflexDirectory) {
		this.mlflexDirectory = mlflexDirectory;
	}

	public PatientSearchService getPatientSearchService() {
		return patientSearchService;
	}

	public void setPatientSearchService(PatientSearchService patientSearchService) {
		this.patientSearchService = patientSearchService;
	}

	public String getAppDataDirectory() {
		return appDataDirectory;
	}

	public void setAppDataDirectory(String appDataDirectory) {
		this.appDataDirectory = appDataDirectory;
	}
}