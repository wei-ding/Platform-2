package org.clinical3PO.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class ConceptTableMapper implements RowMapper<ConceptTableDetails>{

	@Override
	public ConceptTableDetails mapRow(ResultSet rs, int arg) throws SQLException {

		ConceptTableDetails cs = new ConceptTableDetails();
		cs.setPropertyName(rs.getString("property_name"));
		return cs;
	}
}
