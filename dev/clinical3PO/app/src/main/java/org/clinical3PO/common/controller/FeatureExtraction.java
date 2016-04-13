package org.clinical3PO.common.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.clinical3PO.common.environment.EnvironmentType;
import org.clinical3PO.common.form.FEMlFlexForm;
import org.clinical3PO.common.form.FEUgeneForm;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/FExtraction")
public class FeatureExtraction {

	@Autowired
	JobSearchService jobSearchService;
	private static final int BUFFER_SIZE = 4096;
	private @Autowired ServletContext servletContext;
	@Autowired
	private EnvironmentType envType;

	@RequestMapping(value = "/ML-FLEX", method = RequestMethod.POST)
	public String getMlFlexObservations(@Valid @ModelAttribute("mlflexForm") FEMlFlexForm mlflexform, BindingResult result, Model model) {

		if (result.hasErrors()) {
			return "FEMLFlex";
		}

		assert (jobSearchService != null);
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		assert (user != null);
		
		List<JobSearchParameter> searchParameters = new ArrayList<JobSearchParameter>(9);
		searchParameters.add(new JobSearchParameter(JobSearchConstants.CLASSPROPERTY.getSearchKey(), mlflexform.getClassProperty(), 1));
		searchParameters.add(new JobSearchParameter(JobSearchConstants.CLASSIFIER.getSearchKey(), mlflexform.getClassificationAlgorithm(),1));
		searchParameters.add(new JobSearchParameter(JobSearchConstants.FOLDS.getSearchKey(), mlflexform.getFolds().toString(),1));
		searchParameters.add(new JobSearchParameter(JobSearchConstants.NoITERATIONS.getSearchKey(), mlflexform.getNumberOfIterations().toString(),1));
		searchParameters.add(new JobSearchParameter(JobSearchConstants.CLASSTIME.getSearchKey(), mlflexform.getClassTime(),1));
		searchParameters.add(new JobSearchParameter(JobSearchConstants.STARTDATE.getSearchKey(), mlflexform.getStartDate(),1));
		searchParameters.add(new JobSearchParameter(JobSearchConstants.ENDDATE.getSearchKey(), mlflexform.getEndDate(),1));
		searchParameters.add(new JobSearchParameter(JobSearchConstants.STARTTIME.getSearchKey(), mlflexform.getStartTime(),1));
		searchParameters.add(new JobSearchParameter(JobSearchConstants.ENDTIME.getSearchKey(), mlflexform.getEndTime(),1));

		JobSearch jobSearch = new JobSearch();
		jobSearch.setSearchBy(user.getId());
		jobSearch.setSearchParameters(searchParameters);
		
		JobSearchDetails jobSearchDetails = new JobSearchDetails();
		jobSearchDetails.setSearchOn(SearchOn.FEMLFLEX.getSearchOn());
		jobSearchDetails.setSearchType("FEMlFlex");
		jobSearchDetails.setSearchParameters(jobSearch.getSearchParameters().get(0).getValue());
		jobSearchDetails.setScriptType(SearchScript.FEMLFLEX.getSearchScript());
		jobSearchDetails.setScriptParameters(jobSearchService.getScriptParameters(jobSearch));

		jobSearchService.searchJob(jobSearch, jobSearchDetails);

		return "redirect:/MySearch/";
	}

	@RequestMapping(value = "/ML-FLEX", method = RequestMethod.GET)
	public String setMlFlexObservations(ModelMap model) {
		model.addAttribute("mlflexForm", new FEMlFlexForm());
		return "FEMLFlex";
	}

	@RequestMapping(value = "/Ugene", method = RequestMethod.POST)
	public String getUgeneObservations(@Valid @ModelAttribute("feUgeneForm") FEUgeneForm feUgeneForm, 
			BindingResult result,Model model) {

		if (result.hasErrors()) {
			return "FEUgene";
		}

		assert (jobSearchService != null);
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		assert (user != null);
		
		List<JobSearchParameter> searchParameters = new ArrayList<JobSearchParameter>(9);
		searchParameters.add(new JobSearchParameter(JobSearchConstants.CLASSPROPERTY.getSearchKey(), feUgeneForm.getClassProperty(), 1));
		searchParameters.add(new JobSearchParameter(JobSearchConstants.CLASSTIME.getSearchKey(), feUgeneForm.getClassTime(),1));
		searchParameters.add(new JobSearchParameter(JobSearchConstants.STARTDATE.getSearchKey(), feUgeneForm.getStartDate(),1));
		searchParameters.add(new JobSearchParameter(JobSearchConstants.ENDDATE.getSearchKey(), feUgeneForm.getEndDate(),1));
		searchParameters.add(new JobSearchParameter(JobSearchConstants.STARTTIME.getSearchKey(), feUgeneForm.getStartTime(),1));
		searchParameters.add(new JobSearchParameter(JobSearchConstants.ENDTIME.getSearchKey(), feUgeneForm.getEndTime(),1));
		searchParameters.add(new JobSearchParameter(JobSearchConstants.CLASSIFIER.getSearchKey(), feUgeneForm.getClassificationAlgorithm(),1));
		searchParameters.add(new JobSearchParameter(JobSearchConstants.FOLDS.getSearchKey(), feUgeneForm.getFolds().toString(),1));
		searchParameters.add(new JobSearchParameter(JobSearchConstants.NoITERATIONS.getSearchKey(), feUgeneForm.getNumberOfIterations().toString(),1));
		searchParameters.add(new JobSearchParameter(JobSearchConstants.UGENETYPE.getSearchKey(), feUgeneForm.getUgeneAlgorithm(),1));
		
		JobSearch jobSearch = new JobSearch();
		jobSearch.setSearchBy(user.getId());
		jobSearch.setSearchParameters(searchParameters);
		
		JobSearchDetails jobSearchDetails = new JobSearchDetails();
		jobSearchDetails.setSearchOn(SearchOn.FEUGENE.getSearchOn());
		jobSearchDetails.setSearchType("FEUGENE");
		jobSearchDetails.setSearchParameters(jobSearch.getSearchParameters().get(0).getValue());
		jobSearchDetails.setScriptType(SearchScript.FEUGENE.getSearchScript());
		jobSearchDetails.setScriptParameters(jobSearchService.getScriptParameters(jobSearch));

		jobSearchService.searchJob(jobSearch, jobSearchDetails);
		return "redirect:/MySearch/";
	}

	@RequestMapping(value = "/Ugene", method = RequestMethod.GET)
	public String setUgeneObservations(ModelMap model) {
		model.addAttribute("feUgeneForm", new FEUgeneForm());
		return "FEUgene";
	}
	
	@RequestMapping(value="/DownloadUgeneReport/{id}", method = RequestMethod.GET)
    public void doDownload(@PathVariable("id") String reportID, HttpServletRequest request,
            HttpServletResponse response) throws IOException {
 
    	String fileName = "mlFlexReport"+ reportID + ".tar.gz";
    	String fullPath = jobSearchService.getAppDataDirectory() + File.separator + "ugene"+ File.separator + "output" + File.separator + fileName;
    	
    	File downloadFile = new File(fullPath);
        FileInputStream inputStream = new FileInputStream(downloadFile);
         
        String mimeType = servletContext.getMimeType(fullPath);
        if (mimeType == null) {
            // set to binary type if MIME mapping not found
            mimeType = "application/octet-stream";
        }
 
        response.setContentType(mimeType);
        response.setContentLength((int) downloadFile.length());
 
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
        response.setHeader(headerKey, headerValue);
 
        OutputStream outStream = response.getOutputStream();
 
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = -1;
 
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }
 
        inputStream.close();
        outStream.close();
    }
}
