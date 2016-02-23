package org.clinical3PO.common.controller;

import java.util.ArrayList;
import java.util.List;

import org.clinical3PO.common.security.model.User;
import org.clinical3PO.model.JobSearchDetails;
import org.clinical3PO.model.JobSearchParameter;
import org.clinical3PO.services.JobSearchService;
import org.clinical3PO.services.constants.JobSearchConstants;
import org.clinical3PO.services.constants.SearchOn;
import org.clinical3PO.services.constants.SearchScript;
import org.clinical3PO.services.dao.model.JobSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/NLP")
public class NLPUpdatesProcesser {
	
	@Autowired
	JobSearchService jobSearchService;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String displayStatus() {
		return "nlpview";
		
	}
	
	@RequestMapping(value="/",method = RequestMethod.POST)
	public String getNlpUpdate(Model model) {
		
		assert (jobSearchService != null);
		User user=(User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		assert (user != null);
		
		
		List<JobSearchParameter> searchParameters = new ArrayList<JobSearchParameter>(1);
		JobSearchParameter jobSearchParameter = new JobSearchParameter(JobSearchConstants.XML.getSearchKey(),"",1);
		searchParameters.add(jobSearchParameter);
		
		JobSearch jobSearch = new JobSearch();
		jobSearch.setSearchBy(user.getId());
		jobSearch.setSearchParameters(searchParameters);
		
		JobSearchDetails jobSearchDetails = new JobSearchDetails();
		jobSearchDetails.setSearchOn(SearchOn.NLPse.getSearchOn());
		jobSearchDetails.setSearchType("NLP To Hive Update");
		jobSearchDetails.setSearchParameters("NLP To Hive Update");
		jobSearchDetails.setScriptType(SearchScript.NLPSEARCH.getSearchScript());
		jobSearchDetails.setScriptParameters(jobSearchService.getScriptParameters(jobSearch));
		
		jobSearchService.searchJob(jobSearch, jobSearchDetails);
		
		return "redirect:/MySearch/";
	}
}
