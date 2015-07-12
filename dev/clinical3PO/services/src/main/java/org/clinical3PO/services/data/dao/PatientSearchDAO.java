package org.clinical3PO.services.data.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

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

	public void printPatientDeatils(String outputDir, String outputFile, String patientId, String omopHiveDbName,
			String omopHiveConceptTable, String omopHiveObservationTable) throws DataAccessException{
		
		String concept = omopHiveDbName.concat(".").concat(omopHiveConceptTable);//"c3pohivedemo.concept";
		String observation = omopHiveDbName.concat(".").concat(omopHiveObservationTable);//"c3pohivedemo.observation";
		
		String sql = "SELECT "+omopHiveConceptTable+".property_name, "
					+omopHiveObservationTable+".observation_date, "
					+ omopHiveObservationTable+".observation_time, "
					+ omopHiveObservationTable+".value_as_number, "
					+ omopHiveConceptTable+".value_units "
					+ "FROM "+ observation +" "
					+ "JOIN "+concept+" ON "
					+ "("+omopHiveObservationTable+".observation_concept_id = "+omopHiveConceptTable+".src_concept_id) "
					+ "WHERE "+omopHiveObservationTable+".person_id=" +patientId;

		if (envType == EnvironmentType.DEVELOPMENT && logger.isDebugEnabled()){ 
			logger.debug("Script for Patient Search:- " + sql);
		}

		List<PatientSearchObservationDeatils> patientObservations = hiveTemplate.query(
				sql, new PatientSearchMapper(omopHiveConceptTable, omopHiveObservationTable));
		
		if (envType == EnvironmentType.DEVELOPMENT && logger.isDebugEnabled()){ 
			logger.debug("Patient Search Result Size:- " + patientObservations.size());
		}

		Iterator<PatientSearchObservationDeatils> it = patientObservations.iterator();

		String OutPutDirecory = outputDir + File.separator + outputFile;
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(OutPutDirecory, "UTF-8");
			while(it.hasNext()) {

				writer.write(it.next().toString() + "\n");
			}
		} catch (FileNotFoundException e) {
			logger.error("", e);
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		} finally {
			if(writer != null) {
				writer.flush();
				writer.close();
			}
		}
	}

	public void getObservationDeatils(String outputDir, String outputFile, String patientId, 
			String observationId, String omopHiveDbName, 
			String omopHiveConceptTable, String omopHiveObservationTable,
			String omompHiveDeathTable) throws DataAccessException{
		
		String death = omopHiveDbName.concat(".").concat(omompHiveDeathTable); //"c3pohivedemo.death";
		String concept = omopHiveDbName.concat(".").concat(omopHiveConceptTable);//"c3pohivedemo.concept";
		String observation = omopHiveDbName.concat(".").concat(omopHiveObservationTable);//"c3pohivedemo.observation";

		String[] patients = patientId.split(",");
		int sizeOfPatients = patients.length;
		String expression = " OR ";
		String expression1 = omopHiveObservationTable+".person_id=";
		String sql = "";
		boolean flag = false;
		for(int i = 0; i < sizeOfPatients; i++) {

			if(flag){
				sql = sql.concat(expression);
			}
			expression1 = expression1.concat(patients[i].trim()) ;
			sql = sql.concat(expression1);
			expression1 = omopHiveObservationTable+".person_id=";
			flag = true;
		}
		String query = " SELECT "+omopHiveObservationTable+".person_id, "
				+omopHiveObservationTable+".observation_date, "
				+omopHiveObservationTable+".observation_time, "
				+omopHiveObservationTable+".value_as_number, "
				+omopHiveConceptTable+".value_units, "
				+omopHiveConceptTable+".property_definition, "
				+omompHiveDeathTable+".person_id FROM "+ observation +" "
				+ "JOIN "+ concept +" ON ("
				+omopHiveObservationTable+".observation_concept_id = "+omopHiveConceptTable+".src_concept_id) "
				+ "LEFT OUTER JOIN "+ death +" ON ("
				+omopHiveObservationTable+".person_id = "+omompHiveDeathTable+".person_id) "
				+ "WHERE (" + sql +") AND "+omopHiveConceptTable+".property_name='"+observationId+"'";

		if (envType == EnvironmentType.DEVELOPMENT && logger.isDebugEnabled()){
			logger.debug("Script for Observation Search:- " + query);
		}

		List<ObservationSearchObservationDetails> observationSearch = hiveTemplate.query(
				query, new ObservationSearchMapper(omompHiveDeathTable, omopHiveConceptTable, omopHiveObservationTable));

		Iterator<ObservationSearchObservationDetails> it = observationSearch.iterator();
		String OutPutDirecory = outputDir + File.separator + outputFile;
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(OutPutDirecory, "UTF-8");
			while(it.hasNext()) {

				writer.write(it.next().toString() + "\n");
			}
		} catch (FileNotFoundException e) {
			logger.error("", e);
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		} finally {
			if(writer != null) {
				writer.flush();
				writer.close();
			}
		}
	}
	
	public void getBatchSearchDeatils(String outputDir, String outputFile, String parameters, String omopHiveDbName, 
			String omopHiveConceptTable, String omopHiveObservationTable,
			String omompHiveDeathTable) throws DataAccessException{
		
		String death = omopHiveDbName.concat(".").concat(omompHiveDeathTable); //"c3pohivedemo.death";
		String concept = omopHiveDbName.concat(".").concat(omopHiveConceptTable);//"c3pohivedemo.concept";
		String observation = omopHiveDbName.concat(".").concat(omopHiveObservationTable);//"c3pohivedemo.observation";
		
		// calling method to parse input string.
		String temp = parsingInputOfBatchSearchDeatils(parameters, omopHiveConceptTable, omopHiveObservationTable);
		
		String query = "SELECT "+omopHiveConceptTable+".property_name, "
		+omopHiveObservationTable+".person_id, "+omopHiveObservationTable+".observation_date, "
				+omopHiveObservationTable+".observation_time, "
		+omopHiveObservationTable+".value_as_number, "+omopHiveConceptTable+".value_units, "
				+omopHiveConceptTable+".property_definition, "
		+omompHiveDeathTable+".person_id FROM "+ observation +" "
				+ "JOIN "+ concept +" ON ("
		+omopHiveObservationTable+".observation_concept_id = "+omopHiveConceptTable+".src_concept_id) "
				+ "LEFT OUTER JOIN "+ death +" ON ("
		+omopHiveObservationTable+".person_id = "+omompHiveDeathTable+".person_id) "
				+ "WHERE ("+ temp +")";
	
		List<BatchSearchObservationDetails> batchSearch = hiveTemplate.query(
				query, new BatchSearchMapper(omompHiveDeathTable, omopHiveConceptTable, omopHiveObservationTable));

		Iterator<BatchSearchObservationDetails> it = batchSearch.iterator();
		String OutPutDirecory = outputDir + File.separator + outputFile;
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(OutPutDirecory, "UTF-8");
			while(it.hasNext()) {

				writer.write(it.next().toString() + "\n");
			}
		} catch (FileNotFoundException e) {
			logger.error("", e);
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		} finally {
			if(writer != null) {
				writer.flush();
				writer.close();
			}
		}
	}
	
	private String parsingInputOfBatchSearchDeatils(String inputedFileForProcessing, 
			String omopHiveConceptTable, String omopHiveObservationTable) {

		String query = "";
		boolean flag = false;
		String expression = ",";
		String obs_expression1 = "("+omopHiveObservationTable+".person_id IN (";
		String con_expression = " AND ";
		String con_expression1 = omopHiveConceptTable+".property_name IN (";

		StringTokenizer inputSplits = new StringTokenizer(inputedFileForProcessing, "#");
		Map<String, String> map = new HashMap<String, String>();
		while(inputSplits.hasMoreTokens()) {

			StringTokenizer tokens_Obs_ids_color = new StringTokenizer(inputSplits.nextToken(), "~");

			String observation = tokens_Obs_ids_color.nextToken().trim();
			String patientIds = tokens_Obs_ids_color.nextToken().trim();
			if(map.containsKey(observation)) {

				String val = map.get(observation);
				if(val != patientIds) {

					val = val.concat(",").concat(patientIds);
					map.put(observation, val);
				}
			} else {
				map.put(observation, patientIds);
			}
		}

		Set<String> keys = map.keySet();
		Set<String> values = new HashSet<String>(map.values());

		int key_size = keys.size();
		int values_size = values.size();
		if(values_size == 1 && key_size >= 1) { 

			for(String ids : values) {
				query = obs_expression1.concat(ids).concat(")");
			}
			query = query.concat(con_expression).concat(con_expression1);
			Iterator<String> it_keys = keys.iterator();
			String temp = "";
			while(it_keys.hasNext()) {

				if(flag) {
					temp = temp.concat(expression);
				}
				temp = temp.concat("'").concat(it_keys.next()).concat("'");
				flag = true;
			}
			query = query.concat(temp).concat("))");

		} else if(values_size >= 1 && key_size >= 1) {

			Set<Map.Entry<String, String>> set = map.entrySet();
			Iterator<Map.Entry<String, String>> it = set.iterator();

			String temp = "";
			while(it.hasNext()) {

				if(flag) {
					query = query + " OR ";
				}
				Map.Entry<String, String> me = it.next();
				String k = me.getKey();
				String val = me.getValue();
				temp = temp + obs_expression1.concat(val).concat(")");
				temp = temp.concat(con_expression);
				temp = temp.concat(con_expression1).concat("'").concat(k).concat("'").concat("))");
				query = query + temp;
				temp = "";
				flag = true;
			}
		}
		return query;	
	}
}