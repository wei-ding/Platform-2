package org.clinical3PO.common.angular.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.validation.Valid;

import org.clinical3PO.common.form.PatientSearchForm;
import org.clinical3PO.model.JobSearchDetails;
import org.clinical3PO.model.JobSearchParameter;
import org.clinical3PO.services.JobSearchService;
import org.clinical3PO.services.constants.JobSearchConstants;
import org.clinical3PO.services.constants.SearchOn;
import org.clinical3PO.services.constants.SearchScript;
import org.clinical3PO.services.dao.model.JobSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
@Controller
@RequestMapping("/ang/PatientSearch")
@Component("PatientSearchAng")
public class PatientSearch {
	@Autowired
	JobSearchService jobSearchService;


	private @Autowired ServletContext servletContext;
	/*Angular controllers for patientSearch*/
	@RequestMapping(value="/patientSearch",method=RequestMethod.POST,headers="Content-Type=application/x-www-form-urlencoded")
	public @ResponseBody String getAngularPid(@Valid @ModelAttribute("patientSearchForm") PatientSearchForm patientForm, BindingResult result) {
		if (result.hasErrors()) {
			return "{\"url\":\"/PatHad\"}";
		}
		assert (jobSearchService != null);
		
		List<JobSearchParameter> searchParameters = new ArrayList<JobSearchParameter>(1);
		JobSearchParameter jobSearchParameter = new JobSearchParameter(JobSearchConstants.PATIENTID.getSearchKey(),patientForm.getPatientId(),1);
		searchParameters.add(jobSearchParameter);
		
		JobSearch jobSearch = new JobSearch();
		jobSearch.setSearchBy(1);
		jobSearch.setSearchParameters(searchParameters);
		
		JobSearchDetails jobSearchDetails = new JobSearchDetails();
		jobSearchDetails.setSearchOn(SearchOn.PATIENTID.getSearchOn());
		jobSearchDetails.setSearchType("PatientId");
		jobSearchDetails.setSearchParameters(jobSearch.getSearchParameters().get(0).getValue());
		jobSearchDetails.setScriptType(SearchScript.PATIENTID.getSearchScript());
		jobSearchDetails.setScriptParameters(jobSearchService.getScriptParameters(jobSearch));
		
		jobSearchService.searchJob(jobSearch, jobSearchDetails);
		
		return "{\"url\":\"/MySearches\"}";
	}

}
