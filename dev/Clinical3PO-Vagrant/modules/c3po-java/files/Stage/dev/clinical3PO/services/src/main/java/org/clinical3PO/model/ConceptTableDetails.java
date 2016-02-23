package org.clinical3PO.model;

public class ConceptTableDetails {
	
	private String property_name;
	private String src_concept_id;

	public String getPropertyName() {
	return property_name;
	}

	public void setPropertyName(String property_name) {
	this.property_name = property_name;
	}

	public String getSrcConceptId() {
		return src_concept_id;
	}

	public void setSrcConceptId(String src_concept_id) {
		this.src_concept_id = src_concept_id;
	}

	public String toString() {
		return property_name;
	}
}
