package org.clinical3PO.common.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.validation.Valid;

import org.clinical3PO.common.form.PatientSearchForm;
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
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/RPatientSearch")
public class RPatientSearch {
	
	@Autowired
	JobSearchService jobSearchService;


	private @Autowired ServletContext servletContext;
	

	@RequestMapping(value = "/", method = RequestMethod.POST)
	public String getPatient(@Valid @ModelAttribute("patientSearchForm") PatientSearchForm patientForm, BindingResult result, Model model) {
		
		if (result.hasErrors()) {
			return "PatientSearchView";
		}
		
		// Invoke service which inserts a record in the table, calls the hadoop
		// job
		assert (jobSearchService != null);
		User user=(User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		assert (user != null);
		
		List<JobSearchParameter> searchParameters = new ArrayList<JobSearchParameter>(1);
		JobSearchParameter jobSearchParameter = new JobSearchParameter(JobSearchConstants.PATIENTID.getSearchKey(),patientForm.getPatientId(),1);
		searchParameters.add(jobSearchParameter);
		
		JobSearch jobSearch = new JobSearch();
		jobSearch.setSearchBy(user.getId());
		jobSearch.setSearchParameters(searchParameters);
		
		JobSearchDetails jobSearchDetails = new JobSearchDetails();
		jobSearchDetails.setSearchOn(SearchOn.PATIENTIDR.getSearchOn());
		jobSearchDetails.setSearchType("PatientIdR");
		jobSearchDetails.setSearchParameters(jobSearch.getSearchParameters().get(0).getValue());
		jobSearchDetails.setScriptType(SearchScript.PATIENTIDR.getSearchScript());
		jobSearchDetails.setScriptParameters(jobSearchService.getScriptParameters(jobSearch));
		
		jobSearchService.searchJob(jobSearch, jobSearchDetails);
		
		return "redirect:/MySearch/";
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String patientSearch(ModelMap model) {
		// Allow the user to search for a patient
		model.addAttribute("patientSearchForm", new PatientSearchForm());
		
		return "PatientSearchView";
	}
}


