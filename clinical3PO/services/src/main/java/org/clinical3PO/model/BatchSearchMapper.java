package org.clinical3PO.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class BatchSearchMapper implements RowMapper<BatchSearchObservationDetails> {

	private String daeth = null;
	private String concept = null;
	private String observation = null;
	
	public BatchSearchMapper(String death, String concept, String observation) {

		this.daeth = death;
		this.concept = concept;
		this.observation = observation;
	}
	
	@Override
	public BatchSearchObservationDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		BatchSearchObservationDetails details = new BatchSearchObservationDetails();
		
		details.setPropertyName(rs.getString(concept+".property_name"));
		details.setPatientId(rs.getString(observation+".person_id"));
		details.setObservationDate(rs.getString(observation+".observation_date"));
		details.setObservationTime(rs.getString(observation+".observation_time"));
		details.setValueAsNumber(String.valueOf(rs.getFloat(observation+".value_as_number")));
		details.setValueUnits(rs.getString(concept+".value_units"));
		details.setPropertyDefinition(rs.getString(concept+".property_definition"));
		
		String death = rs.getString(daeth+".person_id");
		if(death == null || death.equals("NULL")) {
			death = "Alive";
		} else {
			death = "Death";
		}
		details.setDeath(death);
		
		return details;
	}
}