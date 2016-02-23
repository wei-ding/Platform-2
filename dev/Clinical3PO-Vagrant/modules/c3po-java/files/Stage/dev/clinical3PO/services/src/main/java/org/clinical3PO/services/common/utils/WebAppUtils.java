package org.clinical3PO.services.common.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is referred from app package.
 * 
 * Load's content menu from a file to display over multiple observation search.
 * @author 3129891
 *
 */
public class WebAppUtils {

	private String observationFile;
	private static Map<String,String> observationIdsMap = null;
	private static final Logger logger = LoggerFactory.getLogger(WebAppUtils.class);

	public WebAppUtils(String observationFile) {

		observationIdsMap = new LinkedHashMap<String,String>();
		
		//method call
		loadObservationListFromFile(observationFile);
	}

	/**
	 * Method loads observation list of key,value pairs(comma separated) from a file.
	 * Put into Map for further processing.
	 * 
	 * @param observationFile
	 */
	private void loadObservationListFromFile(String observationFile) {

		BufferedReader br = null;

		try {

			String line;
			logger.info("PREPARING TO READ LIST OF OBSERVATION'S FROM A FILE.");
			br = new BufferedReader(new FileReader(observationFile));

			while ((line = br.readLine()) != null) {

				String conceptArr[] = line.split(",");
				if(conceptArr.length == 2) {
					observationIdsMap.put(conceptArr[0].trim(), conceptArr[1].trim());
				} else {
					logger.error("THE FOLLOWING OBSERVATION IN THE FILE, IS NOT DECLARED PROPERLY:- "+conceptArr[0]);
				}
			}
			logger.info("COMPLETED READING LIST OF OBSERVATION'S FROM A FILE.");
		} catch(FileNotFoundException e) {
			logger.error("concept file is not in the specified path",e);
		}catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public String getObservationFile() {
		return observationFile;
	}

	public void setObservationFile(String observationFile) {
		this.observationFile = observationFile;
	}

	public static Map<String,String> getObservationIds() {
		return observationIdsMap;
	}
}
