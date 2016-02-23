package org.clinical3PO.common.angular.controller;


import java.io.File;

import javax.servlet.ServletContext;

import org.clinical3PO.common.environment.EnvironmentType;
import org.clinical3PO.services.JobSearchService;
import org.clinical3PO.services.JsonService;
import org.clinical3PO.services.dao.model.JobSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("ang/Visualization/Observation")
@Component("PatientObservationViewAng")
public class PatientObservationView {
	
	
	@Autowired
	JobSearchService jobSearchService;
	
	@Autowired
	JsonService jsonService;

	private @Autowired ServletContext servletContext;
	
	@Autowired
	private EnvironmentType envType;
	
	private static final Logger logger = LoggerFactory.getLogger(PatientObservationView.class);

	

	@RequestMapping(value = "/json/{id}", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody		
	public String getMultiplePatients(@PathVariable("id") String id, Model model) 
	{
		/*
		 * Steps to be performed
		 * 
		 * 1. Get the jobsearch data from DB. 2. Use the filename, destination
		 * directory (from jobsearchbean) to fetch hadoop job output. 3.
		 * Construct json representation. 4. Send back the json.
		 */

		// 1. Get the jobsearch data from DB.
		JobSearch jobSearch = jobSearchService.getJob(new Integer(id)
				.intValue());

		String outDir = jobSearch.getOutputDirectory();
		String outFile = jobSearch.getOutputFileName();
		
		String filePath = outDir + File.separator  + outFile; // Not having any file extension
		String jsonBuffer = null;
		
		String patientIds = jobSearch.getSearchParameters().get(0).getValue();
		String observation = jobSearch.getSearchParameters().get(1).getValue();
		
		String parameters = observation+"~"+patientIds;

		jsonBuffer = jsonService.generateObservationDataJSON(filePath, parameters);
		
		if (envType == EnvironmentType.PRODUCTION){
			logger.debug("job Search: "+jobSearch);
		}
		
		if (envType == EnvironmentType.DEVELOPMENT && logger.isDebugEnabled()){
			logger.debug("json buffer "+jsonBuffer);
		}
		
		return(jsonBuffer);
	}

	public JobSearchService getJobSearchService() {
		return jobSearchService;
	}

	public void setJobSearchService(JobSearchService jobSearchService) {
		this.jobSearchService = jobSearchService;
	}

}