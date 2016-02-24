package org.clinical3PO.accumulo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersonIdSearchInAccumulo {

	private static final Logger logger = LoggerFactory.getLogger(PersonIdSearchInAccumulo.class);
	@SuppressWarnings("unused")
	private final String envType = AppUtil.getProperty("environment.type");

	public static void main(String[] args) {

		if (args.length != 10) {
			logger.error ("Usage: PersonIdSearchInAccumulo <output file> <person id> <authorizations> <accumulo instance> <zookeeper servers> <accumulo user> <accumulo password>");
			System.exit(-1);
		}

		PersonIdSearchInAccumulo personIdSearch = new PersonIdSearchInAccumulo();
		personIdSearch.searchPersonId(args);
		System.exit(0);
	}

	/**
	 * 
	 * @param args
	 */
	private void searchPersonId(String[] args) {

		final String outputFile = args[0];
		final String personId = args[1];
		final String authorizations = args[2];
		final String instanceName = args[3];
		final String zkServers = args[4];
		final String userName = args[5];
		final String password = args[6];

		// Accumulo related tables, passed as arguments.
		final String conceptTable = args[7];
		final String personTable = args[8];
		final String observationTable = args[9];

		Connector conn = null;
		String[] authorizationArr = null;
		Map<String, String> conceptIds = null;
		TreeSet<String> outputFileData = null;

		// Creating a Connection instance to access Accumulo
		try {
			conn = createOrGetConnection(instanceName, zkServers, userName, password);
		} catch (AccumuloException e) {
			logger.error (e.toString());
		} catch (AccumuloSecurityException e) {
			logger.error (e.toString());
		} finally {

			// If connection is null, no execution will take place further.
			if(conn == null) {  
				return;
			}
		}

		// Calling a method for tokens
		authorizationArr = getTokens(authorizations);

		Authorizations auths = new Authorizations(authorizationArr);

		// Calling a method to query person table.
		List<String> personObservationIds = scanPersonTable(conn, personTable, auths, personId);

		conceptIds = scanConceptTable(conn, conceptTable, auths);
		outputFileData = scanObservationTable(conn, observationTable, auths, conceptIds, personObservationIds);

		// Create output file even if no data is fetched
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(outputFile)); // Open output file

			if (outputFileData.size() > 0) {

				Iterator<String> iterator = outputFileData.descendingIterator();
				String writeLine = null;
				while (iterator.hasNext()) {

					writeLine = (String)iterator.next();
					bw.write(writeLine);
					bw.newLine();
				}
			}
		} catch(IOException e) {
			logger.error (e.toString());
		} finally {
			try {
				if(bw != null) {
					bw.close();
				}
			} catch (IOException e) {
				logger.error (e.toString());
			}
		}
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
	private Connector createOrGetConnection(String instanceName, String zkServers, String userName, String password) 
			throws AccumuloException, AccumuloSecurityException {

		// Connect
		Instance inst = new ZooKeeperInstance(instanceName, zkServers);
		return inst.getConnector(userName, new PasswordToken(password));
	}

	/**
	 * Parsing the string contains ',' as delimiter.
	 * 
	 * @param authorizations
	 * @return array of strings
	 */
	private String[] getTokens(String authorizations) {

		StringTokenizer tokens = new StringTokenizer(authorizations, ","); // List of authorizations are comma separated
		String[] authorizationArr = new String[tokens.countTokens()];
		byte count = 0;
		while(tokens.hasMoreElements()) {
			authorizationArr[count] = tokens.nextToken();
			count++;
		}
		return authorizationArr;
	}

	/**
	 * Piece of code to read Person Details from Accumulo DataBase
	 * 
	 * @param conn
	 * @param personTable
	 * @param auths
	 * @param personObservationIds
	 * @param personId
	 * @return list of entries from the table
	 */
	private List<String> scanPersonTable(Connector conn, String personTable, Authorizations auths, String personId) {

		Scanner personid_scan = null;
		List<String> personObservationIds = null;
		try {
			personid_scan = conn.createScanner(personTable, auths);
			personid_scan.enableIsolation();
			personid_scan.fetchColumnFamily(new Text("CF1"));
			personid_scan.setRange(new Range(personId));
			personObservationIds = new ArrayList<String>();

			for(Entry<Key,Value> entry1 : personid_scan) {
				personObservationIds.add(entry1.getValue().toString());
			}
		} catch(TableNotFoundException e) {
			logger.error (e.toString());
		} catch(NullPointerException ne) {
			logger.error (ne.toString());
		} finally {
			if(personid_scan != null) {
				personid_scan.close();
			}
		}
		return personObservationIds;
	}

	/**
	 * Piece of code to scan conceptTable from Accumulo DataBase
	 * 
	 * @param conn
	 * @param conceptTable
	 * @param auths
	 * @param conceptIds
	 * @return map of conceptId's
	 */
	private Map<String, String> scanConceptTable(Connector conn, String conceptTable, Authorizations auths) {

		String conceptId = null;
		String conceptName = null;
		String conceptUnit = null;
		Scanner concept_scan = null;
		Map<String, String> conceptIds = null;
		try {
			concept_scan = conn.createScanner(conceptTable, auths);
			concept_scan.enableIsolation();
			concept_scan.fetchColumnFamily(new Text("CF1"));
			conceptIds = new HashMap<String, String>();

			for(Entry<Key,Value> entry2 : concept_scan) {
				Key key = entry2.getKey();
				Value value = entry2.getValue();
				String conceptId_value = value.toString().trim();
				conceptId = key.getRow().toString().trim();

				if (entry2.getKey().getColumnQualifier().toString().equals("property_name")) {
					conceptName = conceptId_value;
				}

				if (entry2.getKey().getColumnQualifier().toString().equals("value_units")) { 
					if (conceptId_value.length() == 0) { 
						conceptUnit = "No Units";
					} else {
						conceptUnit = entry2.getValue().toString();
					}
				}
				conceptIds.put(conceptId, conceptName + ";" + conceptUnit);
			}
		} catch(TableNotFoundException e) {
			logger.error (e.toString());
		} catch(NullPointerException ne) {
			logger.error (ne.toString());
		} finally {
			if(concept_scan != null) {
				concept_scan.close();
			}
		}
		return conceptIds;
	}

	/**
	 * Piece of code to scan Observation's from Accumulo DataBase
	 * 
	 * @param conn
	 * @param observationTable
	 * @param auths
	 * @param conceptIds
	 * @param outputFileData
	 * @param personObservationIds
	 * @return TreeSet of elements
	 */
	private TreeSet<String> scanObservationTable(Connector conn, String observationTable, Authorizations auths,
			Map<String, String> conceptIds, List<String> personObservationIds ) {

		String observationName = null;
		String observationDate = null;
		String observationTime = null;
		String observationValue = null;
		String observationUnit = null;
		String tmpStr = null;
		String writeLine = null;
		Scanner observation_scan = null;
		TreeSet<String> outputFileData = null;
		try {
			observation_scan = conn.createScanner(observationTable, auths);
			observation_scan.enableIsolation();
			observation_scan.fetchColumnFamily(new Text("CF1"));
			outputFileData = new TreeSet<String>();

			for (String personObservationId: personObservationIds) {

				observation_scan.setRange(new Range(personObservationId));

				for(Entry<Key,Value> entry3 : observation_scan) {

					Key key = entry3.getKey();
					Value value = entry3.getValue();
					String obsId_value = value.toString().trim();

					if (key.getColumnQualifier().toString().equals("observation_concept_id")) {
						tmpStr = conceptIds.get(obsId_value);
						StringTokenizer tokens = new StringTokenizer(tmpStr, ";");
						observationName = tokens.nextToken();
						observationUnit = tokens.nextToken();
					}

					if (key.getColumnQualifier().toString().equals("observation_date"))
						observationDate = obsId_value;

					if (key.getColumnQualifier().toString().equals("observation_time"))
						observationTime = obsId_value;

					if (key.getColumnQualifier().toString().equals("value_as_number"))
						observationValue = obsId_value;
				}
				writeLine = observationName + ";" + observationDate + ";" + observationTime + ";" + observationValue 
						+ ";" + observationUnit + ";";
				outputFileData.add(writeLine);
				writeLine = null;
			}
		} catch(NoSuchElementException e) {
			logger.error (e.toString());
		} catch (TableNotFoundException e) {
			logger.error (e.toString());
		} catch(NullPointerException ne) {
			logger.error (ne.toString());
		} finally {
			if(observation_scan != null) {
				observation_scan.close();
			}
		}
		return outputFileData;
	}
}