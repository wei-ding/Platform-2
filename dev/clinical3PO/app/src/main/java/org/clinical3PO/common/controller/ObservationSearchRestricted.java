package org.clinical3PO.common.controller;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.validation.Valid;

import org.apache.commons.io.FileUtils;
import org.clinical3PO.common.environment.EnvironmentType;
import org.clinical3PO.common.form.ObservationSearchForm;
import org.clinical3PO.common.security.model.AccumuloRole;
import org.clinical3PO.common.security.model.User;
import org.clinical3PO.common.utils.WebAppUtils;
import org.clinical3PO.model.JobSearchDetails;
import org.clinical3PO.model.JobSearchParameter;
import org.clinical3PO.services.JobSearchService;
import org.clinical3PO.services.constants.JobSearchConstants;
import org.clinical3PO.services.constants.SearchOn;
import org.clinical3PO.services.constants.SearchScript;
import org.clinical3PO.services.dao.model.JobSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/ObservationSearchRestricted")
public class ObservationSearchRestricted {

	@Autowired
	JobSearchService jobSearchService;

	@Autowired
	private EnvironmentType envType;

	private @Autowired ServletContext servletContext;

	private static final String stringSeparator = "|";

	private static final Logger logger = LoggerFactory.getLogger(ObservationSearchRestricted.class);

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String observationSearch(ModelMap model) {

		model.addAttribute("observationIds",WebAppUtils.getObservationIds());
		model.addAttribute("observationSearchForm", new ObservationSearchForm());

		return "ObservationSearchView";
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	public String getObservation(
			@Valid @ModelAttribute("observationSearchForm") ObservationSearchForm observationForm,
			BindingResult result, Model model) {

		if (result.hasErrors()) {
			model.addAttribute("observationIds",WebAppUtils.getObservationIds());
			return "ObservationSearchView";
		}

		// Invoke service which inserts a record in the table, calls the accumulo search
		assert (jobSearchService != null);

		User user=(User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		assert (user != null);

		AccumuloRole loggedUserAccumuloRole = null;
		String loggedUserAccumuloRoleNames = "";
		int i = 0;
		for (AccumuloRole accumuloRole: user.getAccumuloRoles()) {
			loggedUserAccumuloRole = accumuloRole;
			if (i>0)
				loggedUserAccumuloRoleNames = loggedUserAccumuloRoleNames + ",";
			loggedUserAccumuloRoleNames = loggedUserAccumuloRoleNames + loggedUserAccumuloRole.getName();
			i++;
		}

		List<JobSearchParameter> searchParameters = new ArrayList<JobSearchParameter>(1);
		JobSearchParameter jobSearchParameter = new JobSearchParameter(JobSearchConstants.PATIENTID.getSearchKey(),observationForm.getPatientId(),1);

		searchParameters.add(jobSearchParameter);

		jobSearchParameter = new JobSearchParameter(JobSearchConstants.OBSERVATIONID.getSearchKey(),observationForm.getObservationId(),1);
		searchParameters.add(jobSearchParameter);

		JobSearch jobSearch = new JobSearch();

		jobSearch.setSearchBy(user.getId());
		jobSearch.setSearchParameters(searchParameters);

		JobSearchDetails jobSearchDetails = new JobSearchDetails();
		jobSearchDetails.setSearchOn(SearchOn.OBSERVATIONIDRESTRICTED.getSearchOn());
		jobSearchDetails.setSearchType("ObservationIdRestricted");
		jobSearchDetails.setSearchParameters(jobSearch.getSearchParameters().get(1).getValue());
		jobSearchDetails.setScriptType(SearchScript.OBSERVATIONIDRESTRICTED.getSearchScript());
		jobSearchDetails.setScriptParameters(jobSearchService.getScriptParameters(jobSearch));
		jobSearchDetails.setAccumuloRoles(loggedUserAccumuloRoleNames);

		jobSearchService.searchJob(jobSearch, jobSearchDetails);

		return "redirect:/MySearch/";
	}

	@RequestMapping(value = "/FileUpload", method = RequestMethod.POST)
	public String getPatient(@RequestParam("batchFile") MultipartFile batchFile, Model model) {
		// Invoke service which inserts a record in the table, calls the hadoop
		// job
		assert (jobSearchService != null);
		User user = (User) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		assert (user != null);

		AccumuloRole loggedUserAccumuloRole = null;
		String loggedUserAccumuloRoleNames = "";
		int i = 0;
		for (AccumuloRole accumuloRole: user.getAccumuloRoles()) {
			loggedUserAccumuloRole = accumuloRole;
			if (i>0)
				loggedUserAccumuloRoleNames = loggedUserAccumuloRoleNames + ",";
			loggedUserAccumuloRoleNames = loggedUserAccumuloRoleNames + loggedUserAccumuloRole.getName();
			i++;
		}

		if (batchFile.isEmpty()) {
			logger.info("Uploaded file is empty");
			return "redirect:/MySearch/";
		}

		BufferedOutputStream stream = null;
		BufferedReader reader = null;

		try {
			byte[] bytes = batchFile.getBytes();

			File dir = new File(jobSearchService.getAppDataDirectory()+File.separator+"batchUploads");

			JobSearch jobSearch = new JobSearch();
			jobSearch.setSearchBy(user.getId());

			if (!dir.exists())
				dir.mkdirs();

			// Create the file on server
			String name = "BatchFile"+ new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
			File savedBatchFile = new File(dir.getAbsolutePath() + File.separator + name);

			if (envType == EnvironmentType.DEVELOPMENT && logger.isDebugEnabled()){
				logger.debug("File uploaded path:"+savedBatchFile.getAbsolutePath());
			}

			try{
				stream = new BufferedOutputStream(new FileOutputStream(savedBatchFile));
				stream.write(bytes);
			} catch (Exception e) {
				logger.error("File upload directory not found");
			} finally{
				if (stream!=null) stream.close();
			}

			String regexSeparator = "(?<!\\\\)" + Pattern.quote(stringSeparator);

			List<JobSearchParameter> searchParameters = new ArrayList<JobSearchParameter>();
			try{
				reader = new BufferedReader(new FileReader(savedBatchFile));
				String patientInfo;
				int index = 1;

				while ((patientInfo = reader.readLine()) != null) {
					searchParameters.add(new JobSearchParameter(
							JobSearchConstants.PATIENTIDS.getSearchKey(),
							patientInfo.split(regexSeparator)[0], index));
					searchParameters.add(new JobSearchParameter(
							JobSearchConstants.OBSERVATIONIDS.getSearchKey(),
							patientInfo.split(regexSeparator)[1], index));
					searchParameters.add(new JobSearchParameter(
							JobSearchConstants.COLORCODE.getSearchKey(),
							patientInfo.split(regexSeparator)[2], index));
					index++;
				}
				searchParameters.add(new JobSearchParameter("FileName",savedBatchFile.getAbsolutePath(),index));
				jobSearch.setSearchParameters(searchParameters);
			} catch (Exception e) {
				logger.error("Unable to read uploaded file");
			} finally{
				if (reader!=null) reader.close();
			}

			JobSearchDetails jobSearchDetails = new JobSearchDetails();
			jobSearchDetails.setSearchOn(SearchOn.BATCHSEARCHRESTRICTED.getSearchOn());
			jobSearchDetails.setSearchType("BATCHSEARCHRESTRICTED");
			jobSearchDetails.setScriptType(SearchScript.BATCHSEARCHRESTRICTED.getSearchScript());
			jobSearchDetails.setScriptParameters(jobSearchService.getBatchSearchParameters(jobSearch));
			jobSearchDetails.setAccumuloRoles(loggedUserAccumuloRoleNames);

			jobSearchService.searchJob(jobSearch, jobSearchDetails);

			File newServerFile = new File(dir.getAbsolutePath() + File.separator + "UploadedFile-" + jobSearch.getId() + ".txt");
			FileUtils.copyFile(savedBatchFile, newServerFile);

			return "redirect:/MySearch/";

		} catch (Exception e) {
			logger.info("You failed to upload " + " => " + e.getMessage());
			return "redirect:/MySearch/";
		} 
	}

	@RequestMapping(value = "/FileUpload", method = RequestMethod.GET)
	public String patientSearchFromFile(ModelMap model) {
		// Allow the user to search for a patient
		return "PatientSearchBatch";
	}

}
