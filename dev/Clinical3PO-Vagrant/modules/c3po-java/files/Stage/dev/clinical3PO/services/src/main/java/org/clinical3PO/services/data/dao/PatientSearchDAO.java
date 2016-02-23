package org.clinical3PO.services.data.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.clinical3PO.common.environment.EnvironmentType;
import org.clinical3PO.model.BatchSearchMapper;
import org.clinical3PO.model.BatchSearchObservationDetails;
import org.clinical3PO.model.ObservationSearchMapper;
import org.clinical3PO.model.ObservationSearchObservationDetails;
import org.clinical3PO.model.PatientSearchMapper;
import org.clinical3PO.model.PatientSearchObservationDeatils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * This class is used for querying hive stuff for patientId, ObsrvationId, BatchSearch.
 * Uses hiveQueries.properties file for dynamic query building, according to the user search criteria.
 *  
 * @author 3129891
 *
 */
public class PatientSearchDAO {

	@Autowired
	private JdbcTemplate hiveTemplate;

	@Autowired
	private EnvironmentType envType;

	private static final Logger logger = LoggerFactory.getLogger(PatientSearchDAO.class);

	public JdbcTemplate getHiveTemplate() {
		return hiveTemplate;
	}

	public void setHiveTemplate(JdbcTemplate hiveTemplate) {
		this.hiveTemplate = hiveTemplate;
	}

	/**
	 * Reading properties file by initializing Properties. Closing the connection once the 
	 * file is loaded into properties. Get the required property from the file.
	 * The $ variable is replaced by the patientId, to build complete query.
	 * 
	 * HiveTemplate will process the query for results. 
	 *  
	 * @param outputDir
	 * @param outputFile
	 * @param patientId
	 * @param omopHiveConceptTable
	 * @param omopHiveObservationTable
	 * @param hiveQueryFileName
	 * @param hiveQueryFileLocation
	 * @throws IOException
	 */
	public void getPatientIdSearch(String outputDir, String outputFile, String patientId, 
			String omopHiveConceptTable, String omopHiveObservationTable,
			String hiveQueryFileLocation) throws IOException {

		// method calling
		Properties props = readHiveQueryFromFile(hiveQueryFileLocation);

		if(props != null) {
			// fetching required property
			String sql = props.getProperty("hive.query.patientSearch").trim();
			if(sql != null && !sql.isEmpty()) {

				// replacing $ variable with patientId
				sql = sql.replaceAll("\\$\\{personId\\}", patientId);
				logger.info("Query to be executed: " + sql +"\n");
			} else {
				logger.info("Query is empty or null, ensure there's a KEY with name 'hive.query.patientSearch' available and VALUE respectively");
				return;
			}
			if (envType == EnvironmentType.DEVELOPMENT && logger.isDebugEnabled()){ 
				logger.debug("Script for Patient Search:- " + sql);
			}

			// HiveTemplate processing query for results with the help of ResultMapper.
			List<PatientSearchObservationDeatils> patientObservations = hiveTemplate.query(
					sql, new PatientSearchMapper(omopHiveConceptTable, omopHiveObservationTable));

			Iterator<PatientSearchObservationDeatils> it = patientObservations.iterator();

			// method calling
			writeHiveResultsToFile(it, outputDir, outputFile);
		} else {
			logger.info("hiveQueries.proeprties files not found. Make sure the file is in the specified location");
			return;
		}
	}

	/**
	 * Reading properties file by initializing Properties. Closing the connection once the 
	 * file is loaded into properties. Get the required property from the file.
	 * The $ variable is replaced by the patientId, to build complete query.
	 * 
	 * HiveTemplate will process the query for results.
	 * 
	 * @param outputDir
	 * @param outputFile
	 * @param patientId
	 * @param observationId
	 * @param omopHiveDbName
	 * @param omopHiveConceptTable
	 * @param omopHiveObservationTable
	 * @param omompHiveDeathTable
	 * @param hiveQueryFileName
	 * @param hiveQueryFileLocation
	 * @throws IOException
	 */
	public void getObservationSearch(String outputDir, String outputFile, String patientId, 
			String observationId, String omopHiveDbName, String omopHiveConceptTable, 
			String omopHiveObservationTable, String omompHiveDeathTable, 
			String hiveQueryFileLocation) throws IOException {

		// method calling
		Properties props = readHiveQueryFromFile(hiveQueryFileLocation);

		if(props != null) {

			// fetching required property
			String query = props.getProperty("hive.query.observationSearch").trim();
			if(query != null && !query.isEmpty()) {

				query = query.replaceAll("\\$\\{personId\\}", patientId);
				query = query.replaceAll("\\$\\{propertyName\\}", "'"+observationId+"'");
				logger.info("Query to be executed: " + query +"\n");
			} else {
				logger.info("Query is empty or null, ensure there's a KEY with name 'hive.query.observationSearch' available and VALUE respectively");
				return;
			}
			if (envType == EnvironmentType.DEVELOPMENT && logger.isDebugEnabled()){
				logger.debug("Script for Observation Search:- " + query);
			}

			// HiveTemplate processing query for results with the help of ResultMapper.
			List<ObservationSearchObservationDetails> observationSearch = hiveTemplate.query(
					query, new ObservationSearchMapper(omompHiveDeathTable, omopHiveConceptTable, omopHiveObservationTable));

			Iterator<ObservationSearchObservationDetails> it = observationSearch.iterator();

			// method calling
			writeHiveResultsToFile(it, outputDir, outputFile);
		} else {
			logger.info("hiveQueries.proeprties files not found. Make sure the file is in the specified location");
			return;
		}		
	}

	/**
	 * Reading properties file by initializing Properties. Closing the connection once the 
	 * file is loaded into properties. Get the required property from the file.
	 * The $ variable is replaced by the patientId, to build complete query.
	 * 
	 * HiveTemplate will process the query for results.
	 * 
	 * @param outputDir
	 * @param outputFile
	 * @param parameters
	 * @param omopHiveDbName
	 * @param omopHiveConceptTable
	 * @param omopHiveObservationTable
	 * @param omompHiveDeathTable
	 * @param hiveQueryFileName
	 * @param hiveQueryFileLocation
	 * @throws IOException
	 */
	public void getBatchSearch(String outputDir, String outputFile, String parameters, 
			String omopHiveDbName, String omopHiveConceptTable, String omopHiveObservationTable,
			String omompHiveDeathTable, String hiveQueryFileLocation) throws IOException {

		// method calling
		Properties props = readHiveQueryFromFile(hiveQueryFileLocation);

		if(props != null) {

			// fetching required property
			String query = props.getProperty("hive.query.batchSearch.main").trim();
			String query_sub1 = props.getProperty("hive.query.batchSearch.sub1").trim();
			String query_sub2 = props.getProperty("hive.query.batchSearch.sub2").trim();

			if((query != null && !query.isEmpty()) && (query_sub1 != null && !query_sub1.isEmpty()) && (query_sub2 != null && !query_sub2.isEmpty())) {

				/*
				 * Since this method deals with file/batch uploads, the input file is expected to be
				 * large with multiple patient's and observations.
				 * #- Every single search string details are separated with
				 * ~- category separation within a single search string 
				 * 3 Token from ~ separation are available. 
				 * 1st Token-patientId's
				 * 2nd Token-Observation ID
				 * 3rd Token-color code
				 * Into a hash map observationID is key and patientId's are value. 
				 */
				StringTokenizer inputSplits = new StringTokenizer(parameters, "#");
				Map<String, String> map = new HashMap<String, String>();
				while(inputSplits.hasMoreTokens()) {

					StringTokenizer tokens_Obs_ids_color = new StringTokenizer(inputSplits.nextToken(), "~");

					String observation = tokens_Obs_ids_color.nextToken().trim();
					String patientIds = tokens_Obs_ids_color.nextToken().trim();

					if (envType == EnvironmentType.DEVELOPMENT && logger.isDebugEnabled()){
						logger.info(" -----------------------> " + observation);
						logger.info(" -----------------------> " + patientIds);
					}

					/*
					 * There is a possibility to search different observation's with same patientId's 
					 * & same observation for different patinetId's.
					 * Since we are using observationID as key, dynamic query is prepared based on
					 * "same observation for different patinetId's".
					 * I.e. If an observationID is already available in the map, append the latest 
					 * patientId's to the existing patinetId's.
					 */
					if(map.containsKey(observation)) {

						String val = map.get(observation);
						if(val != patientIds || !val.contains(patientIds) || !patientIds.contains(val)) {

							val = val.concat(",").concat(patientIds);
							map.put(observation, val);
						}
					} else {
						map.put(observation, patientIds);
					}
				}
				inputSplits = null;

				Set<String> keys = map.keySet();
				Set<String> values = new HashSet<String>(map.values());
				int key_size = keys.size();
				int values_size = keys.size();

				if (envType == EnvironmentType.DEVELOPMENT && logger.isDebugEnabled()){
					logger.info("----------------------------------");
					logger.info("Size of Keys: " + key_size);
					logger.info(keys.toString());
					logger.info("Size of Keys: " + values_size);
					logger.info(values.toString());
					logger.info("----------------------------------");
				}

				/*
				 * As for the condition in if, the search is for n-observations with single and 
				 * same set of patientId's.
				 * $-varibale's are replaced by patinetId's and observationID's 
				 */
				if(key_size >= 1 && values_size == 1) {

					String observationId = keys.toString();
					observationId = observationId.substring(1, observationId.length()-1);
					observationId = observationId.replaceAll("\\s", "");
					observationId = observationId.replaceAll(",", "','");

					String patientId = values.toString();
					patientId = patientId.replaceAll("\\s", "");
					patientId = patientId.substring(1, patientId.length() -1);

					query = query + " " +query_sub1;
					query = query.replaceAll("\\$\\{personId\\}", patientId);
					query = query.replaceAll("\\$\\{propertyName\\}", "'"+observationId+"'");
					logger.info("Query to be executed: " + query +"\n");
				}

				/*
				 * As for the condition in else if, the search is particular on observations with 
				 * different set's of patientId's.
				 * $-varibale's are replaced by patinetId's and observationID's
				 * Multiple levels of query is build. 
				 */
				else if(values_size >= 1 && key_size >= 1) {

					Set<Map.Entry<String, String>> set = map.entrySet();
					Iterator<Map.Entry<String, String>> it = set.iterator();

					String temp = " ";
					boolean flag = false;
					while(it.hasNext()) {

						if(flag) {
							temp = temp.concat(" ").concat(query_sub2).concat(" ");
						}

						Map.Entry<String, String> me = it.next();
						String k = me.getKey();
						String val = me.getValue();

						temp = temp.concat(query_sub1);

						temp = temp.replaceAll("\\$\\{personId\\}", val);
						temp = temp.replaceAll("\\$\\{propertyName\\}", "'"+k+"'");
						flag = true;
					}

					query = query.concat(" ").concat(temp);
					logger.info("Query to be executed: " + query +"\n");
				}
			} else {
				logger.info("Query is empty or null, ensure there's a KEY with name 'hive.query.batchSearch.*' available and VALUE respectively");
				return;
			}

			// HiveTemplate processing query for results with the help of ResultMapper.
			List<BatchSearchObservationDetails> batchSearch = hiveTemplate.query(
					query, new BatchSearchMapper(omompHiveDeathTable, omopHiveConceptTable, omopHiveObservationTable));

			Iterator<BatchSearchObservationDetails> it = batchSearch.iterator();

			// method calling
			writeHiveResultsToFile(it, outputDir, outputFile);
		} else {
			logger.info("hiveQueries.proeprties files not found. Make sure the file is in the specified location");
			return;
		}
	}

	/**
	 * Reading properties files using FileInputStream. 
	 * Load the file into properties and close connection of stream.
	 *  
	 * @param hiveQueryFileLocation
	 * @param hiveQueryFileName
	 * @return properties with file loaded
	 * @throws IOException
	 */
	private Properties readHiveQueryFromFile(String hiveQueryFileLocation) throws IOException {

		Properties props = null;
		String filename = hiveQueryFileLocation.concat(File.separator).concat("hiveQueries.properties");
		InputStream input = null;

		// reading properties file and loading then closing.
		try {
			input = new FileInputStream(filename);
			props = new Properties();
			props.load(input);
		} catch (IOException e) {
			logger.error("", e);
		} finally {
			if(input != null) {
				input.close();
			}
		}
		return props;
	}

	/**
	 * Writing Hive Results into file.
	 *  
	 * @param it
	 * @param outputDir
	 * @param outputFile
	 */
	private void writeHiveResultsToFile(Iterator<?> it, String outputDir, String outputFile) {

		// Creating file and writing results.
		String OutPutDirecory = outputDir + File.separator + outputFile;
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(OutPutDirecory, "UTF-8");
			while(it.hasNext()) {

				writer.write(it.next().toString() + "\n");
			}
		} catch (FileNotFoundException e) {
			logger.error("Writing Hive Results into file failed: ", e);
		} catch (UnsupportedEncodingException e) {
			logger.error("Writing Hive Results into file failed: ", e);
		} finally {
			if(writer != null) {
				writer.flush();
				writer.close();
			}
		}
	}
}