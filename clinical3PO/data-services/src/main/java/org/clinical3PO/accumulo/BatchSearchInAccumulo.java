package org.clinical3PO.accumulo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.regex.Pattern;

import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.BatchScanner;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Instance;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.client.ZooKeeperInstance;
import org.apache.accumulo.core.client.security.tokens.PasswordToken;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.Authorizations;
import org.apache.hadoop.io.Text;
import org.clinical3PO.environment.AppUtil;
import org.clinical3PO.environment.EnvironmentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatchSearchInAccumulo {

	private final String familyName = "CF1";
	private final String stringSeparator = "|";

	// In the input batch file, patients, observations and colors are separated by "|" symbol 
	private final String regexSeparator = "(?<!\\\\)" + Pattern.quote(stringSeparator);
	private static final Logger logger = LoggerFactory.getLogger(BatchSearchInAccumulo.class);
	private String envType = AppUtil.getProperty("environment.type");

	public static void main(String[] args) {

		if (args.length != 10) {
			logger.error ("Usage: BatchSearchInAccumulo <output file> <search parameters> <authorizations> <accumulo instance> <zookeeper servers> <accumulo user> <accumulo password>");
			System.exit(-1);
		}

		BatchSearchInAccumulo batchSearch = new BatchSearchInAccumulo();
		batchSearch.searchBatchInAccumulo(args);

		System.exit(0);
	}

	@SuppressWarnings("unchecked")
	private void searchBatchInAccumulo(String[] args) {

		String outputFile = args[0];
		String batchFile = args[1];   // "/home/c3po/c3po-app-data/UploadedFile-139.txt"
		String authorizations = args[2];
		String instanceName = args[3];
		String zkServers = args[4];
		String userName = args[5];
		String password = args[6];

		// Accumulo related tables, passed as arguments.
		String conceptTable = args[7];
		String personObservationIndexTable = args[8];
		String observationTable = args[9];

		Connector conn = null;
		BufferedWriter bw = null;
		List<Range> range_list = null;
		ConcurrentSkipListSet<String> outputFileData = new ConcurrentSkipListSet<String>();
		Map<String, String> conceptId = null;
		Map<String, String> conceptNames = null;
		Map<String, String> conceptUnits = null;
		Map<String, String> conceptIdObservation = null;
		Map<String, String> observationPatientsPair = null;
		BatchScanner personIDBatchScan = null, observationBatchScan = null;

		try {
			// Calling Method
			observationPatientsPair = readingInputFile(batchFile);
			if(observationPatientsPair != null) {

				if(envType !=null && EnvironmentType.DEVELOPMENT == EnvironmentType.valueOf(envType)) {
					logger.info ("Observation Patient pair: " +observationPatientsPair.toString());
				}

				// Calling Method
				conn = createOrGetConnection(instanceName, zkServers, userName, password);
				if(conn != null) {
					logger.info("Connection Established with accumulo.");
				} else { 
					return;
				}
			}

			// List of authorizations are comma separated
			Authorizations auths = new Authorizations(authorizations.split(","));

			// Calling Method
			Object[] arr = getConceptInfo(conn, conceptTable, auths, observationPatientsPair);
			conceptId = (Map<String, String>) arr[0];
			conceptIdObservation = (Map<String, String>) arr[1];

			if(conceptId != null && conceptIdObservation!= null) {

				range_list = new ArrayList<Range>();

				/*
				 * Setting Range
				 */
				for (String keys : conceptIdObservation.keySet()) {
					range_list.add(new Range(keys));
				}

				Object[] map_arr = getBatchScanOnConceptTable(conn, conceptTable, auths, observationPatientsPair, range_list);
				conceptNames = (Map<String, String>) map_arr[0];
				conceptUnits = (Map<String, String>) map_arr[1];
				range_list.clear();

				/*
				 * Setting Range
				 */
				for(String observationIDKey:observationPatientsPair.keySet()) {
					for(String observationIDValue:observationPatientsPair.get(observationIDKey).split(",")) {
						range_list.add(new Range(observationIDValue+"~"+conceptId.get(observationIDKey)));
					}					
				}
				conceptId.clear();
				conceptId = null;

				/*
				 * Scanning Table
				 */
				personIDBatchScan = conn.createBatchScanner(personObservationIndexTable, auths, range_list.size());
				personIDBatchScan.fetchColumnFamily(new Text(familyName));
				personIDBatchScan.setRanges(range_list);

				observationPatientsPair.clear();
				observationPatientsPair = null;	
				range_list.clear();


				List<String> personObservationIds = new ArrayList<String>();
				for(Entry<Key,Value> entry1 : personIDBatchScan) {
					personObservationIds.add(entry1.getValue().toString());   // get the unique counter of observation record
				}
				personIDBatchScan.close();

				int personObsIdSize = personObservationIds.size();

				/*
				 * Checking if the size of the list contains personIds is not empty.
				 */
				if (personObsIdSize > 0) {

					// Calling a method to create BatchScanner
					observationBatchScan = creatingObservationBatchScan(observationTable, auths, personObsIdSize, conn);
					bw = new BufferedWriter(new FileWriter(outputFile)); // Open output file

					int final_count = 0;

					/*
					 * On checking null on BatchScanner:
					 * 
					 * Situation: 
					 * 1. There's a scenario where, BatchScanner couldn't process because of insufficient RAM.
					 * 2. BatchScanners are meant for huge ranges-less data or less ranges-huge data.
					 * 	Not for huge ranges-huge data.
					 * Solution:
					 * Get a fixed range to break and process huge data sets, say x. In this program x is 80000.
					 * Data sets could be huge but, this program process only x-ranges at a time.   
					 */
					if(observationBatchScan != null) {

						float limit_each_cycle = 80000.0f;
						int limit_each_cycle_int = (int) limit_each_cycle;
						float iterations = personObsIdSize / limit_each_cycle;

                        /*
						 * This is a condition to check if the search size is less than the limit set manually. I.e. 80000.
						 * So, to satisfy and program not to throw indexOutOfBoundException, this condition evolved.
						 *
						 *  If the search size is less than the manual limit size, set the limt ot search size.
						 */
						if(personObsIdSize < limit_each_cycle_int) {
							limit_each_cycle_int = personObsIdSize;
						}

						/*
						 * Dividing size of data set with cycle-limit may give decimal value.
						 * To make it numeric adding 1. Else, some portion of data set will be missed. 
						 */
						int a = (((int)iterations) + 1);
						for(int i = 0; i < a; i++) {

							while(final_count < limit_each_cycle_int) {

								range_list.add(new Range(personObservationIds.get(final_count)));
								final_count++;
							}

							/*
							 * Setting the ranges to BatchScanner.
							 * Passing BatchScanner to a method where it iterates over BatchScanner.
							 * 
							 * In return, the method gives a set of data from observation Table.
							 * 
							 * Iterating over the set to hadoop output file. 
							 */
							if(!range_list.isEmpty()) {
								observationBatchScan.fetchColumnFamily(new Text(familyName));
								observationBatchScan.setRanges(range_list);

								range_list.clear();
								outputFileData = getObservationBatchScanInfo(observationBatchScan, conceptNames, 
										conceptUnits, conceptIdObservation, personObsIdSize, outputFileData);
								observationBatchScan.clearScanIterators();
								observationBatchScan.clearColumns();

								creatingOutPutFile(bw, outputFileData);
								outputFileData.clear();
							}

							/*
							 * On every iteration, it necessary to increase the scan range,
							 * Eg: First iteration - 0 to 80000
							 *     Second iteration - 80000 to 160000 and so on.
							 * If the range is not so much in the next iteration, limiting the range by some arithmetic operation.
							 */
							limit_each_cycle_int += (int) limit_each_cycle;
							if(limit_each_cycle_int > personObsIdSize) {

								long temp = limit_each_cycle_int - personObsIdSize;
								limit_each_cycle_int -=  temp;
							}
						}
						range_list = null;
						conceptNames.clear();
						conceptNames = null;
						conceptUnits.clear();
						conceptUnits = null;
						conceptIdObservation.clear();
						conceptIdObservation = null;
						observationBatchScan.close();
					}
				}
			}
		} catch (AccumuloException e) {
			logger.error(e.toString());
		} catch (AccumuloSecurityException e) {
			logger.error (e.toString());
		} catch (IOException e1) {
			logger.error(e1.toString());
		} catch (TableNotFoundException e) {
			logger.error(e.toString());
		}  finally {
			if(bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					logger.error(e.toString());
				}
			}
			if(outputFile != null) {
				outputFileData.clear();
				outputFileData = null;
			}
		}
	}

	/**
	 * Creating a new BatchScanner instance by specifying table name and other parmas.
	 *   
	 * @param observationTable
	 * @param auths
	 * @param personObsIdSize
	 * @param conn
	 * @return BatchScanner instance
	 */
	private BatchScanner creatingObservationBatchScan(String observationTable, Authorizations auths, 
			int personObsIdSize, Connector conn) {

		/*
		 * Scanning Observation Table 
		 */
		BatchScanner observationBatchScan = null;
		try {
			observationBatchScan = conn.createBatchScanner(observationTable, auths, personObsIdSize);
		} catch(TableNotFoundException e) {
			logger.error (e.toString());
		} 
		return observationBatchScan;
	}

	/**
	 * Fetching the data from the 'ObservationTable' using accumulo batchScanner.
	 * Iterating over the scanner to get the details.
	 * 
	 * Constructing a string (pre-defined), which JSON can understand.
	 * Writing the string to a set (concurrent). 
	 *  
	 * @param observationBatchScan
	 * @param conceptNames
	 * @param conceptUnits
	 * @param conceptIdObservation
	 * @param personObsIdSize
	 * @param outputFileData
	 * @return concurrent set contains patients-observation data (string format)
	 */
	private ConcurrentSkipListSet<String> getObservationBatchScanInfo(BatchScanner observationBatchScan,
			Map<String, String> conceptNames, Map<String, String> conceptUnits,
			Map<String, String> conceptIdObservation, int personObsIdSize, 
			ConcurrentSkipListSet<String> outputFileData) {

		int count = 0;
		String observationId = null;
		String observationPersonId = null;
		String observationDate = null;
		String observationTime = null;
		String observationValue = null;
		String observationName = null;
		String observationUnit = null;
		StringBuilder sb = new StringBuilder();

		for(Entry<Key,Value> entry3 : observationBatchScan) {

			if(entry3 != null) {
				String key_entry = entry3.getKey().getColumnQualifier().toString();
				String value_entry = entry3.getValue().toString().trim();
				if (key_entry.equals("observation_concept_id")) {
					String key = value_entry;
					observationId = conceptIdObservation.get(key);
					observationName = conceptNames.get(key);
					observationUnit = conceptUnits.get(key);
				} else if (key_entry.equals("person_id")) {
					observationPersonId = value_entry;
				} else if (key_entry.equals("observation_date")) {
					observationDate = value_entry;
				} else if (key_entry.equals("observation_time")) {
					observationTime = value_entry;
				}  

				if (key_entry.equals("value_as_number")) {
					observationValue = value_entry;

					// write line in the end
					String writeLine = sb.append(observationId).append("~").append(observationPersonId)
							.append("\t").append(observationDate).append(";").append(observationTime)
							.append(";").append(observationValue).append(";").append(observationUnit)
							.append(";").append(observationName).append(";").toString();
					sb.delete(0, sb.length());
					outputFileData.add(writeLine);
					writeLine = null;
					count++;
					logger.debug(Integer.toString(count)+", "+ personObsIdSize);
				}
			} else {
				logger.error("The Search returned null.");
			}
		}
		sb = null;
		return outputFileData;
	}

	/**
	 * Reading input file as inputed.
	 * Converting first position of String array as List by breaking the string, using '|' as delimiter.
	 * Inserting the personId as value and attributes as key into map.
	 *  
	 * @param batchFile
	 * @return map of key,value pairs
	 * @throws IOException
	 */
	private Map<String, String> readingInputFile(String batchFile) throws IOException {

		BufferedReader reader = null;
		Map<String, String> observationPatientsPair = null;
		try {
			reader = new BufferedReader(new FileReader(new File(batchFile)));
			observationPatientsPair = new HashMap<String, String>();
			String line;
			while ((line = reader.readLine()) != null) {				

				String[] seperator = line.split(regexSeparator);
				List<String> temp = Arrays.asList(seperator[1].split(","));  // get comma separated observation ids in allSearchParameters

				for (String keyLocal:temp){
					String valueLocal = seperator[0];

					if (observationPatientsPair.containsKey(keyLocal)) {
						valueLocal = observationPatientsPair.get(keyLocal)+","+valueLocal;
						observationPatientsPair.put(keyLocal,valueLocal);
					}
					else{    		  
						observationPatientsPair.put(keyLocal,valueLocal);
					}
				}
			}
		} catch (IOException e) {	
			logger.error (e.toString());
		} finally {
			if (reader!=null){
				reader.close();
			}
		}
		return observationPatientsPair;
	}

	/**
	 * Connecting to Accumulo using Zookeeper instance.
	 * 
	 * @param instanceName
	 * @param zkServers
	 * @param userName
	 * @param password
	 * @return connection object of accumulo
	 * @throws AccumuloException
	 * @throws AccumuloSecurityException
	 */
	private Connector createOrGetConnection(String instanceName, String zkServers, String userName, 
			String password) throws AccumuloException, AccumuloSecurityException {

		// Connect
		Instance inst = new ZooKeeperInstance(instanceName, zkServers);
		return inst.getConnector(userName, new PasswordToken(password));
	}

	/**
	 * Scanning Concept Table for name, definition and units for each observation
	 * 
	 * @param conn
	 * @param conceptTable
	 * @param auths
	 * @param observationPatientsPair
	 * @return array with two map instances.
	 */
	private Object[] getConceptInfo(Connector conn, String conceptTable, Authorizations auths, 
			Map<String, String> observationPatientsPair) {

		Scanner concept_scan = null;
		Map<String, String> conceptId = null;
		Map<String, String> conceptIDObservation = null;

		// Get concept name, definition and units for each observation
		try {
			concept_scan = conn.createScanner(conceptTable, auths);
			concept_scan.enableIsolation();
			concept_scan.fetchColumnFamily(new Text(familyName));

			conceptId = new HashMap<String,String>();
			conceptIDObservation = new HashMap<String,String>();

			for(Entry<Key,Value> entry2 : concept_scan) {

				Key key = entry2.getKey();
				Value value = entry2.getValue();
				String key_row = key.getRow().toString();
				String col_qualifier = key.getColumnQualifier().toString();

				if (col_qualifier.equals("property_name")) {
					for(String observationID : observationPatientsPair.keySet()){

						if (value.toString().equals(observationID)) {
							conceptId.put(observationID, key_row);
							conceptIDObservation.put(key_row, observationID);					
						}
					}
				}
			}
		} catch(TableNotFoundException e) {
			logger.error (e.toString());
		} finally {
			concept_scan.close();	
		}
		Object[] temp = {conceptId, conceptIDObservation};
		return temp;
	}

	/**
	 * 
	 * @param conn
	 * @param conceptTable
	 * @param auths
	 * @param observationPatientsPair
	 * @param range_list
	 * @return object contains maps
	 */
	private Object[] getBatchScanOnConceptTable(Connector conn, String conceptTable, Authorizations auths, 
			Map<String, String> observationPatientsPair, List<Range> range_list) {

		/*
		 * Scanning Concept Table with specified Ranges
		 */
		BatchScanner conceptBatchScan = null;
		Map<String, String> conceptNames = null;
		Map<String, String> conceptUnits = null;
		try {
			conceptBatchScan = conn.createBatchScanner(conceptTable, auths, observationPatientsPair.size());
			conceptBatchScan.fetchColumnFamily(new Text(familyName));
			conceptBatchScan.setRanges(range_list);

			conceptNames = new HashMap<String,String>();
			conceptUnits = new HashMap<String,String>();

			for(Entry<Key,Value> entry2 : conceptBatchScan) {
				String temp = entry2.getKey().getRow().toString().trim();
				String key = entry2.getKey().getColumnQualifier().toString();
				String value = entry2.getValue().toString().trim();

				if (key.contains("src_concept_id")) {
					temp = value;
					conceptNames.put(temp, null);
				}

				if (key.contains("property_definition")) {
					conceptNames.put(temp, value);
				}

				if (key.contains("value_units")) {
					if (value.length() == 0)
						conceptUnits.put(temp,"No Units");
				} else {
					conceptUnits.put(temp, value);
				}
			}	
		} catch(TableNotFoundException e) {
			logger.error (e.toString());
		} finally {
			if(conceptBatchScan != null) {
				conceptBatchScan.close();
			}
		}
		Object[] temp = {conceptNames, conceptUnits};
		return temp;
	}

	/**
	 * Iterating the set to write the generated output into file.
	 * 
	 * @param bw
	 * @param outputFileData
	 * @throws IOException
	 */
	private void creatingOutPutFile(BufferedWriter bw, ConcurrentSkipListSet<String> outputFileData) throws IOException {

		try {
			if (outputFileData != null && (outputFileData.size() > 0)) {

				Iterator<String> iterator = outputFileData.descendingIterator();
				while (iterator.hasNext()) {
					bw.write(iterator.next());
					bw.newLine();
					bw.flush();
				}
			}
		} catch(IOException e) {
			logger.error(e.toString());
		}
	}
}