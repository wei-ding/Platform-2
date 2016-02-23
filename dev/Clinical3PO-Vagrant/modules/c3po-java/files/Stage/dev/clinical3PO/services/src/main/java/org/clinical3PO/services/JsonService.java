package org.clinical3PO.services;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.clinical3PO.common.environment.EnvironmentType;
import org.clinical3PO.services.json.ObservationDataToJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JsonService {

	private static final Logger logger = LoggerFactory.getLogger(JsonService.class);

	@Autowired
	private EnvironmentType envType;

	@Autowired
	ObservationDataToJson generateJSON;

	private String appDataDirectory;

	private static final String jsonDirectory = "JsonFiles";

	private boolean checkIfFileExists(String filePath) {
		File outputFileOnDisk = new File(filePath);

		// Get the file name from the path.

		String fileName = outputFileOnDisk.getName();
		
		String jsonFilePath = getAppDataDirectory()+File.separator+jsonDirectory+File.separator+fileName+".json";
		
		logger.info("Checking for json file: "+jsonFilePath+" existence");
		return new File(jsonFilePath).isFile();

	}

	private String getFileContents(String filePath){
		
		File outputFileOnDisk = new File(filePath);

		// Get the file name from the path.

		String fileName = outputFileOnDisk.getName();
		
		String jsonFilePath = getAppDataDirectory()+File.separator+jsonDirectory+File.separator+fileName+".json";
		
		try {
			return FileUtils.readFileToString(new File(jsonFilePath),StandardCharsets.UTF_8);
		} catch (IOException e) {
			logger.error("Error reading jons file :",e);
		} 
		return "";
	}

	private void saveFile(String filePath, String json) {
		// Save the json contents to a file.
		
		File outputFileOnDisk = new File(filePath);

		// Get the file name from the path.

		String fileName = outputFileOnDisk.getName();
		
		File dir = new File(getAppDataDirectory()+File.separator+jsonDirectory+File.separator);
		
		File jsonFile = new File(getAppDataDirectory()+File.separator+jsonDirectory+File.separator+fileName+".json");
		
		if (!dir.exists())
			dir.mkdirs();
		
		try {
			logger.info("Saving json contents to "+jsonFile);
			FileUtils.writeStringToFile(jsonFile, json, StandardCharsets.UTF_8);
		} catch (IOException e) {
			logger.error("Error writing jons files: ",e);
		}

	}

	public String generateObservationDataJSON (String filePath, String jsonParameters){

		// Check if the json is cached on the disk, if yes return, otherwise generate, store on the 
		// disk and return

		String json;

		if (checkIfFileExists(filePath)) {

			logger.info("Json file available on the disk :");

			json =  getFileContents(filePath);

			if (envType == EnvironmentType.DEVELOPMENT && logger.isDebugEnabled()){
				logger.debug("json fetched from file: " + json);
			}

			if (envType == EnvironmentType.PRODUCTION){
				logger.debug("json size: " + json.length());
			}

			return json;
		} else {

			// Convert to json, store the file contents.

			json = generateJSON.generateObservationDataJSON(filePath, jsonParameters);
			saveFile(filePath,json);

			return json;

		}

	}

	public String generateMultiObservationDataJSON (String filePath, String jsonParameters){
		// Check if the json is cached on the disk, if yes return, otherwise generate, store on the 
		// disk and return

		String json;
		
		if (checkIfFileExists(filePath)) {

			logger.info("Json file available on the disk :");

			json =  getFileContents(filePath);

			if (envType == EnvironmentType.DEVELOPMENT && logger.isDebugEnabled()){
				logger.debug("json fetched from file: " + json);
			}

			if (envType == EnvironmentType.PRODUCTION){
				logger.debug("json size: " + json.length());
			}

			return json;
		} else {

			// Convert to json, store the file contents.

			json = generateJSON.generateMultiObservationDataJSON(filePath, jsonParameters);
			saveFile(filePath,json);

			return json;

		}
	}

	public String generatePatientViewDataJson (String filePath, String patientID){

		// Check if the json is cached on the disk, if yes return, otherwise generate, store on the 
		// disk and return

		String json;

		if (checkIfFileExists(filePath)) {

			logger.info("Json file available on the disk :");

			json =  getFileContents(filePath);

			if (envType == EnvironmentType.DEVELOPMENT && logger.isDebugEnabled()){
				logger.debug("json fetched from file: " + json);
			}

			if (envType == EnvironmentType.PRODUCTION){
				logger.debug("json size: " + json.length());
			}

			return json;
		} else {

			// Convert to json, store the file contents.

			json = generateJSON.generatePatientViewDataJson(filePath, patientID);
			saveFile(filePath,json);

			return json;

		}

	}

	public String getAppDataDirectory() {
		return appDataDirectory;
	}

	public void setAppDataDirectory(String appDataDirectory) {
		this.appDataDirectory = appDataDirectory;
	}

}
