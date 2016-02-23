package org.clinical3PO.common.controller;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.io.FileUtils;
import org.clinical3PO.common.environment.EnvironmentType;
import org.clinical3PO.common.form.CrossValidationForm;
import org.clinical3PO.common.security.model.User;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/MLCrossValidation")
public class MLCrossValidation {

	@Autowired
	JobSearchService jobSearchService;
	private static final int BUFFER_SIZE = 4096;

	private @Autowired ServletContext servletContext;

	private static final Logger logger = LoggerFactory.getLogger(MLCrossValidation.class);
	
	private static final String stringSeparator = "|";
	
	@Autowired
	private EnvironmentType envType;
	
	@RequestMapping(value = "/", method = RequestMethod.POST)
	public String getPatient(
			@RequestParam("batchFile") MultipartFile batchFile,@Valid @ModelAttribute("crossValidationForm") CrossValidationForm crossValidationForm, 
			BindingResult result,Model model) {
		
		if (result.hasErrors()) {
			return "MLCrossValidationView";
		}
				
		assert (jobSearchService != null);
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		assert (user != null);

		if (batchFile.isEmpty()) {
			logger.error("Uploaded file is empty");
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
				logger.info("File upload directory not found");
			} finally{
				if (stream!=null) stream.close();
			}
						
			String regexSeparator = "(?<!\\\\)" + Pattern.quote(stringSeparator);


			List<JobSearchParameter> searchParameters = new ArrayList<JobSearchParameter>(1);
			try{
				reader = new BufferedReader(new FileReader(savedBatchFile));
				String patientInfo = reader.readLine();

				searchParameters.add(new JobSearchParameter(
						JobSearchConstants.PATIENTIDS.getSearchKey(),
						patientInfo.split(regexSeparator)[0], 1));
				searchParameters.add(new JobSearchParameter(
						JobSearchConstants.OBSERVATIONIDS.getSearchKey(),
						patientInfo.split(regexSeparator)[1], 1));
				searchParameters.add(new JobSearchParameter(
						JobSearchConstants.BINS.getSearchKey(),
						patientInfo.split(regexSeparator)[2], 1));

				searchParameters.add(new JobSearchParameter(JobSearchConstants.CLASSIFIER.getSearchKey(),crossValidationForm.getClassificationAlgorithm(),1));
				searchParameters.add(new JobSearchParameter(JobSearchConstants.FOLDS.getSearchKey(),crossValidationForm.getFolds().toString(),1));
				searchParameters.add(new JobSearchParameter(JobSearchConstants.NoITERATIONS.getSearchKey(),crossValidationForm.getNumberOfIterations().toString(),1));
				
				jobSearch.setSearchParameters(searchParameters);

			} catch (Exception e) {
				logger.error("Unable to read uploaded file");
			} finally{
				if (reader!=null) reader.close();
			}

			JobSearchDetails jobSearchDetails = new JobSearchDetails();
			jobSearchDetails.setSearchOn(SearchOn.CROSSVALIDATION.getSearchOn());
			jobSearchDetails.setSearchType("CrossValidation");
			jobSearchDetails.setScriptType(SearchScript.CROSSVALIDATION.getSearchScript());
			jobSearchDetails.setScriptParameters(jobSearchService.getCrossValidationParameters(jobSearch));

			jobSearchService.searchJob(jobSearch, jobSearchDetails);
			
			File newServerFile = new File(dir.getAbsolutePath() + File.separator + "UploadedFile-" + jobSearch.getId() + ".txt");
			FileUtils.copyFile(savedBatchFile, newServerFile);

			return "redirect:/MySearch/";

		} catch (Exception e) {
			logger.error(e.getMessage());
			return "redirect:/MySearch/";
		} 
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String patientSearchFromFile(ModelMap model) {
		model.addAttribute("crossValidationForm", new CrossValidationForm());
		
		return "MLCrossValidationView";
	}
	
	@RequestMapping(value="/DownloadReport/{id}", method = RequestMethod.GET)
    public void doDownload(@PathVariable("id") String reportID, HttpServletRequest request,
            HttpServletResponse response) throws IOException {
 
    	String fileName = "mlFlexReport"+ reportID + ".tar.gz";
    	String fullPath = jobSearchService.getAppDataDirectory() + File.separator + "mlflex"+ File.separator + "output" + File.separator + fileName;
    	
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