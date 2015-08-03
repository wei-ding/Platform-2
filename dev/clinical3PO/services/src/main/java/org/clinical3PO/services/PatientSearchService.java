package org.clinical3PO.services;

import java.io.IOException;

import org.clinical3PO.services.data.dao.PatientSearchDAO;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class PatientSearchService {

	private String omopHiveDbName;
	private String omopHiveConceptTable;
	private String omopHiveDeathTable;
	private String omopHiveObservationTable;
	private PatientSearchDAO patientSearchDAO;
	private String hiveQueryFileLocation;

	public PatientSearchDAO getPatientSearchDAO() {
		return patientSearchDAO;
	}

	public void setPatientSearchDAO(PatientSearchDAO patientSearchDAO) {
		this.patientSearchDAO = patientSearchDAO;
	}

//	public void getPatientSearchOnHiveQL(String outputDir, String outputFile, String parameters) throws DataAccessException{
//
//		patientSearchDAO.printPatientDeatils(outputDir, outputFile, parameters, omopHiveDbName.trim(), 
//				omopHiveConceptTable.trim(), omopHiveObservationTable.trim());
//	}
	
	public void getPatientSearchOnHiveQL(String outputDir, String outputFile, String parameters) throws DataAccessException, IOException{

		patientSearchDAO.getPatientIdSearch(outputDir, outputFile, parameters,
				omopHiveConceptTable.trim(), omopHiveObservationTable.trim(), 
				hiveQueryFileLocation);
	}

//	public void getObservationSearchOnHiveQL(String outputDir, String outputFile, String patientId, String observationId) throws DataAccessException {
//
//		patientSearchDAO.getObservationDeatils(outputDir, outputFile, patientId, observationId, omopHiveDbName.trim(), 
//				omopHiveConceptTable.trim(), omopHiveObservationTable.trim(), omopHiveDeathTable.trim());
//	}
	
	public void getObservationSearchOnHiveQL(String outputDir, String outputFile, String patientId, String observationId) 
			throws DataAccessException, IOException {

		patientSearchDAO.getObservationSearch(outputDir, outputFile, patientId, observationId, 
				omopHiveDbName.trim(), omopHiveConceptTable.trim(), omopHiveObservationTable.trim(),
				omopHiveDeathTable.trim(), hiveQueryFileLocation);
	}
	
//	public void getBatchSearchOnHiveQL(String outputDir, String outputFile, String parameters) throws DataAccessException {
//
//		patientSearchDAO.getBatchSearchDeatils(outputDir, outputFile, parameters, omopHiveDbName.trim(), 
//				omopHiveConceptTable.trim(), omopHiveObservationTable.trim(), omopHiveDeathTable.trim());
//	}
	
	public void getBatchSearchOnHiveQL(String outputDir, String outputFile, String parameters) throws DataAccessException, IOException {

		patientSearchDAO.getBatchSearch(outputDir, outputFile, parameters, omopHiveDbName.trim(), 
				omopHiveConceptTable.trim(), omopHiveObservationTable.trim(), 
				omopHiveDeathTable.trim(), hiveQueryFileLocation);
	}

	public void setOmopHiveDbName(String omopHiveDbName) {
		this.omopHiveDbName = omopHiveDbName;
	}

	public void setOmopHiveConceptTable(String omopHiveConceptTable) {
		this.omopHiveConceptTable = omopHiveConceptTable;
	}

	public void setOmopHiveDeathTable(String omopHiveDeathTable) {
		this.omopHiveDeathTable = omopHiveDeathTable;
	}

	public void setOmopHiveObservationTable(String omopHiveObservationTable) {
		this.omopHiveObservationTable = omopHiveObservationTable;
	}

	public void setHiveQueryFileLocation(String hiveQueryFileLocation) {
		this.hiveQueryFileLocation = hiveQueryFileLocation;
	}
}