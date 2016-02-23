package org.clinical3PO.services.data.dao;

import java.util.List;

import org.clinical3PO.model.ConceptTableDetails;
import org.clinical3PO.model.ConceptTableMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class NLPHiveTableUpdater {

	private static final Logger logger = LoggerFactory.getLogger(NLPHiveTableUpdater.class);

	@Autowired
	private JdbcTemplate hiveTemplate;

	public JdbcTemplate getHiveTemplate() {
		return hiveTemplate;
	}

	public void setHiveTemplate(JdbcTemplate hiveTemplate) {
		this.hiveTemplate = hiveTemplate;
	}

	public boolean checkForPropertyNameAvailabilityInConceptTable(String type, String hiveConceptTable) throws DataAccessException {

		logger.info("Check for the property_name- " +type + ", started.");
		String sql = "SELECT property_name FROM " + hiveConceptTable +" WHERE property_name='"+type+"'";
		logger.info("Query : " + sql);
		List<ConceptTableDetails> list = hiveTemplate.query(sql, new ConceptTableMapper());

		boolean flag = false;
		if(list != null && !list.isEmpty()) {

			logger.info("Concept table - property_name, list isn't empty. Size is: " + list.size());

			ConceptTableDetails details = list.get(0);
			if(type.equals(details.getPropertyName())) {
				flag = true;
			}
		}
		logger.info("Check for the property_name- " +type + ", Completed --> " +flag);
		return flag;
	}

	public int getConceptRowCount(String hiveConceptTable) throws DataAccessException{

		logger.info("Check for count(*) in concept table, started.");

		// Use this for fast query but only if the column 'src_concept_id' is int.
		// String sql = "SELECT MAX(src_concept_id) FROM " + hiveConceptTable;
		String sql = "SELECT MAX(CAST((src_concept_id) as int)) FROM "+ hiveConceptTable;
		logger.info("Query : " + sql);

		Number count = hiveTemplate.queryForObject(sql, Integer.class);

		logger.info("Check for count(src_concept_id) in concept table, completed. Result is " + count);
		return (count != null ? count.intValue() : 0);
	}

	public int getObservationRowCount(String hiveObservationTable) throws DataAccessException {

		logger.info("Check for count(*) in observation table, started.");

		// Use this for fast query but only if the column 'observation_id' is int.
		// String sql = "SELECT MAX(observation_id) FROM "+hiveObservationTable;
		String sql = "SELECT MAX(CAST((observation_id) as int)) FROM "+hiveObservationTable;
		logger.info("Query : " + sql);

		Number count = hiveTemplate.queryForObject(sql, Integer.class);

		logger.info("Check for count(observation_id) in observation table, completed. Result is " +count);
		return (count != null ? count.intValue() : 0);
	}

	public void insertIntoConceptTable(int src_concept_id, String property_type, String hiveConceptTable) {

		logger.info("Inserting into Concept table.");
		String sql = "insert into "
				+ hiveConceptTable
				+ " (src_concept_id, property_name, property_definition, value_type, value_units, value_min, value_max) values('"
				+ src_concept_id + "','" + property_type + "', 'NULL','NULL', 'NULL', 'NULL', 'NULL')";
		logger.info("QUERY: "+ sql);
		hiveTemplate.update(sql);
		logger.info("Done. Inserting into Concept table.");
	}

	public void insertIntoObservationTable(int observation_row_count, String patientId, int src_concept_id, 
			String date, String time, String value, String hiveObservationTable) {

		logger.info("Inserting into Observation table.");
		String sql = "insert into "
				+ hiveObservationTable
				+ " (observation_id, person_id, observation_concept_id, observation_date, "
				+ "observation_time, value_as_number, value_as_string, value_as_concept_id, "
				+ "unit_concept_id, range_low, range_high, observation_type_concept_id, "
				+ "associated_provider_id, visit_occurrence_id, relevant_condition_concept_id, "
				+ "observation_source_value, units_source_value) values('"
				+ observation_row_count + "','" + patientId + "','" + src_concept_id + "','" + 
				date + "','" + time + "','NULL','" + value
				+ "','NULL','NULL','NULL','NULL','NULL','NULL','NULL','NULL','NULL','NULL')";
		logger.info("QUERY: "+ sql);
		hiveTemplate.update(sql);
		logger.info("Done. Inserting into Observation table.");
	}
}