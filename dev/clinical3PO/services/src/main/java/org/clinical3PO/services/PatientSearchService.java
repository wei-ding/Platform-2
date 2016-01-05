package org.clinical3PO.services;

import java.io.IOException;

import org.clinical3PO.services.data.dao.NLPHiveTableUpdater;
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
	private NLPHiveTableUpdater nlpHiveTableUpdater;
	
	public void setNlpHiveTableUpdater(NLPHiveTableUpdater nlpHiveTableUpdater) {
		this.nlpHiveTableUpdater = nlpHiveTableUpdater;
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
	
	public NLPHiveTableUpdater getNlpHiveTableUpdater() {
		return nlpHiveTableUpdater;
	}

	public PatientSearchDAO getPatientSearchDAO() {
		return patientSearchDAO;
	}

	public void setPatientSearchDAO(PatientSearchDAO patientSearchDAO) {
		this.patientSearchDAO = patientSearchDAO;
	}

	public void getPatientSearchOnHiveQL(String outputDir, String outputFile, String parameters) throws DataAccessException, IOException{

		patientSearchDAO.getPatientIdSearch(outputDir, outputFile, parameters,
				omopHiveConceptTable.trim(), omopHiveObservationTable.trim(), 
				hiveQueryFileLocation);
	}

	public void getObservationSearchOnHiveQL(String outputDir, String outputFile, String patientId, String observationId) 
			throws DataAccessException, IOException {

		patientSearchDAO.getObservationSearch(outputDir, outputFile, patientId, observationId, 
				omopHiveDbName.trim(), omopHiveConceptTable.trim(), omopHiveObservationTable.trim(),
				omopHiveDeathTable.trim(), hiveQueryFileLocation);
	}
	
	public void getBatchSearchOnHiveQL(String outputDir, String outputFile, String parameters) throws DataAccessException, IOException {

		patientSearchDAO.getBatchSearch(outputDir, outputFile, parameters, omopHiveDbName.trim(), 
				omopHiveConceptTable.trim(), omopHiveObservationTable.trim(), 
				omopHiveDeathTable.trim(), hiveQueryFileLocation);
	}
	
	public boolean getPropertyNameAvailable(String type) throws DataAccessException {
		return nlpHiveTableUpdater.checkForPropertyNameAvailabilityInConceptTable(type, omopHiveConceptTable);
	}
	
	public int getConceptIdCount() {
		return nlpHiveTableUpdater.getConceptRowCount(omopHiveConceptTable);
	}
	
	public int getObservationIdCount() {
		return nlpHiveTableUpdater.getObservationRowCount(omopHiveObservationTable);
	}
	
	public void insertIntoHiveConcept(int k, String type) {
		nlpHiveTableUpdater.insertIntoConceptTable(k, type, omopHiveConceptTable);
	}
	
	public void insertIntoHiveObservation(int m, String patientId, int p, String date, String time,String value) {
		nlpHiveTableUpdater.insertIntoObservationTable(m,patientId,p,date,time,value,omopHiveObservationTable);
	}
}