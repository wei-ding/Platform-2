package org.clinical3PO.common.controller.visualization;

import java.io.File;

import org.clinical3PO.common.environment.EnvironmentType;
import org.clinical3PO.services.JobSearchService;
import org.clinical3PO.services.JsonService;
import org.clinical3PO.services.dao.model.JobSearch;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("Visualization/Patient")
public class PatientView {

	@Autowired
	JobSearchService jobSearchService;

	@Autowired
	JsonService jsonService;

	@Autowired
	private EnvironmentType envType;

	private static final Logger logger = LoggerFactory.getLogger(PatientView.class);

	@RequestMapping(value="/{id}", method = RequestMethod.GET)
	public String getPatientView(@PathVariable Long id, Model model) {
		// The id should be passed to the jsp, jsp inturn would pass it to the service.
		model.addAttribute("id", id);
		return "visualization/PatientView";
	}

	@Profiled (tag = "Preparing the Patient Search Json")
	@RequestMapping(value = "/json/{id}", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String getSearchedPatient(@PathVariable("id") String id, Model model) {

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
		String patientID = jobSearch.getSearchParameters().get(0).getValue();

		String filePath = outDir + File.separator  + outFile; // Not having any file extension
		String jsonBuffer = null;

		jsonBuffer = jsonService.generatePatientViewDataJson(filePath, patientID);
		
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