package org.clinical3PO.common.controller;

import java.util.List;

import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.clinical3PO.common.environment.EnvironmentType;
import org.clinical3PO.common.security.model.User;
import org.clinical3PO.services.JobSearchService;
import org.clinical3PO.services.dao.model.JobSearch;

@Controller
@RequestMapping("/MySearch")
public class MySearch {

	private static final Logger logger = LoggerFactory.getLogger(MySearch.class);
	
	@Autowired
	JobSearchService jobSearch;
	
	@Autowired
	private EnvironmentType envType;

	@Profiled (tag = "Retrieving the jobs")
	@RequestMapping(value="/", method = RequestMethod.GET)
	public String patientSearch(ModelMap model) {
		// Allow the user to search for a patient
		
		User user=(User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		assert (user != null);
		
		List<JobSearch> jobs = jobSearch.getJobs(user.getId());
		
		if (envType == EnvironmentType.DEVELOPMENT && logger.isDebugEnabled()){
			logger.debug("List of Jobs: "+jobs);
		}
		
		if (envType == EnvironmentType.PRODUCTION){
			logger.info("Jobs size "+jobs.size());
		}
		
		model.addAttribute("jobs", jobs);
		return "MysearchesView";
	}

}