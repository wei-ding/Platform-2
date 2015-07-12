package org.clinical3PO.common.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.clinical3PO.common.environment.EnvironmentType;
import org.clinical3PO.common.form.beans.PatientSearch;
import org.clinical3PO.common.form.beans.PatientSearchList;
import org.clinical3PO.common.security.model.User;
import org.clinical3PO.model.JobSearchDetails;
import org.clinical3PO.model.JobSearchParameter;
import org.clinical3PO.services.JobSearchService;
import org.clinical3PO.services.constants.JobSearchConstants;
import org.clinical3PO.services.constants.SearchOn;
import org.clinical3PO.services.constants.SearchScript;
import org.clinical3PO.services.dao.model.JobSearch;

@Controller
@RequestMapping("/PatientSearchUserPrefs")
public class PatientSearchListUserPrefs {

	private static final Logger logger = LoggerFactory.getLogger(PatientSearchListUserPrefs.class);

	@Autowired
	private EnvironmentType envType;

	@Autowired
	JobSearchService jobSearchService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(ModelMap model) {
		return "PatientSearchUserPrefsView";
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	public String save(@ModelAttribute("patientSearchListForm") PatientSearchList patientList) {

		assert (jobSearchService != null);
		User user = (User) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		assert (user != null);

		List<PatientSearch> patientSearchList = patientList.getPersonList();

		if (envType == EnvironmentType.DEVELOPMENT && logger.isDebugEnabled()) {
			logger.debug("Search size " + patientList.getPersonList().size());
			for (PatientSearch patientSearch : patientSearchList) {
				logger.debug(patientSearch.toString());
			}
		}

		if (envType == EnvironmentType.PRODUCTION) {
			logger.info("Search size" + patientList.getPersonList().size());
		}

		if (patientList.getPersonList().size() > 0) {

			List<JobSearchParameter> searchParameters = new ArrayList<JobSearchParameter>();
			JobSearch jobSearch = new JobSearch();
			jobSearch.setSearchBy(user.getId());

			// Ignore the records if any one of the value is null.
			int index = 1;
			for (PatientSearch patientSearch : patientSearchList) {
				
				if (StringUtils.isNotBlank(patientSearch.getPatientIds())
						&& StringUtils
								.isNotEmpty(patientSearch.getPatientIds())
						&& StringUtils.isNotBlank(patientSearch
								.getObservationIds())
						&& StringUtils.isNotEmpty(patientSearch
								.getObservationIds())
						&& StringUtils.isNotBlank(patientSearch.getColorCode())
						&& StringUtils.isNotEmpty(patientSearch.getColorCode())) {

					searchParameters.add(new JobSearchParameter(
							JobSearchConstants.PATIENTIDS.getSearchKey(),
							patientSearch.getPatientIds(), index));
					searchParameters.add(new JobSearchParameter(
							JobSearchConstants.OBSERVATIONIDS.getSearchKey(),
							patientSearch.getObservationIds(), index));
					searchParameters.add(new JobSearchParameter(
							JobSearchConstants.COLORCODE.getSearchKey(),
							patientSearch.getColorCode(), index));

					jobSearch.setSearchParameters(searchParameters);

					index++;
				}
			}
			
			if(jobSearch.getSearchParameters() !=null && jobSearch.getSearchParameters().size() >0) {
				JobSearchDetails jobSearchDetails = new JobSearchDetails();
				jobSearchDetails.setSearchOn(SearchOn.PATIENTIDUSERPREFS.getSearchOn());
				jobSearchDetails.setSearchType("PatientIdUserPrefs");
				jobSearchDetails.setScriptType(SearchScript.PATIENTIDUSERPREFS.getSearchScript());
				jobSearchDetails.setScriptParameters(jobSearchService.getPatientSearchWithUserPrefsParameters(jobSearch));
				
				jobSearchService.searchJob(jobSearch, jobSearchDetails);
			}
		}

		return "redirect:/MySearch/";
	}

}
