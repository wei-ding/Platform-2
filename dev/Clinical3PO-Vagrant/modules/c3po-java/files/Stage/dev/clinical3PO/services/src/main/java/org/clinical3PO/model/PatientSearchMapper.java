package org.clinical3PO.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class PatientSearchMapper implements RowMapper<PatientSearchObservationDeatils>{
	
	private String concept = null;
	private String observation = null;
	
	public PatientSearchMapper(String concept, String observation) {

		this.concept = concept;
		this.observation = observation;
	}

	@Override
	public PatientSearchObservationDeatils mapRow(ResultSet rs, int arg1) throws SQLException {
		
		PatientSearchObservationDeatils deatils = new PatientSearchObservationDeatils();
		
		deatils.setPropertyName(rs.getString(concept+".property_name"));
		deatils.setObservationDate(rs.getString(observation+".observation_date"));
		deatils.setObservationTime(rs.getString(observation+".observation_time"));
		deatils.setValueAsNumber(String.valueOf(rs.getFloat(observation+".value_as_number")));
		
		String valueUnits = rs.getString(concept+".value_units");
		if(valueUnits.isEmpty()) {
			valueUnits = "No Unit";
		}
		deatils.setValueUnits(valueUnits);
		return deatils;
	}
}
