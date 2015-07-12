package org.clinical3PO.accumulo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Instance;
import org.apache.accumulo.core.client.Scanner;
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

public class ObservationIdSearchInAccumulo {

	private static final Logger logger = LoggerFactory.getLogger(ObservationIdSearchInAccumulo.class);

	String envType = AppUtil.getProperty("environment.type");

	private void searchObservationId(String[] args) {

		String outputFile = args[0];
		String listOfPersonIds = args[1];
		String authorizations = args[2];
		String instanceName = args[3];
		String zkServers = args[4];
		String userName = args[5];
		String password = args[6];
		String observationId = args[7];

		// Accumulo related tables, passed as arguments.

		String conceptTable = args[8];
		String deathTable = args[9];
		String personObservationIndexTable = args[10];
		String observationTable = args[11];

		Connector conn = null;
		BufferedWriter bw = null;
		String conceptId = null;
		String conceptName = null;
		String conceptUnit = null;
		boolean conceptIdFound = false;
		String observationPersonId = null;
		String observationName = null;
		String observationDate = null;
		String observationTime = null;
		String observationValue = null;
		String observationUnit = null;
		String personIdMortality = null;
		String writeLine = null;
		String[] authorizationArr;
		String[] personIds;

		TreeSet<String> outputFileData = new TreeSet<String>();
		List<String> personObservationIds = new ArrayList<String>();
		Map<String, String> personIdDeaths = new HashMap<String, String>();
		Scanner personid_scan = null, observation_scan = null, concept_scan = null, death_scan = null;

		// Initailize all persons as no death
		personIds = listOfPersonIds.split(",");  // List of person ids are comma separated
		for (String personId: personIds) {
			personIdDeaths.put(personId, "Alive");
		}

		// Connect
		Instance inst = new ZooKeeperInstance(instanceName, zkServers);
		try {
			conn = inst.getConnector(userName, new PasswordToken(password));
		} catch(Exception e) {
			logger.error (e.toString());
			return;
		}

		authorizationArr = authorizations.split(",");  // List of authorizations are comma separated
		Authorizations auths = new Authorizations(authorizationArr);

		try {
			concept_scan = conn.createScanner(conceptTable, auths);
			concept_scan.enableIsolation();
			concept_scan.fetchColumnFamily(new Text("CF1"));

			for(Entry<Key,Value> entry2 : concept_scan) {
				if (entry2.getKey().getColumnQualifier().toString().matches("property_name")) {
					if (entry2.getValue().toString().trim().matches(observationId)) {
						conceptId = entry2.getKey().getRow().toString().trim();
						conceptIdFound = true;
						break;
					}
				}
			}

			if (conceptIdFound) {
				concept_scan.setRange(new Range(conceptId));

				for(Entry<Key,Value> entry2 : concept_scan) {
					if (entry2.getKey().getColumnQualifier().toString().matches("property_definition"))
						conceptName = entry2.getValue().toString().trim();

					if (entry2.getKey().getColumnQualifier().toString().matches("value_units"))
						if (entry2.getValue().toString().trim().length() == 0)
							conceptUnit = "No Units";
						else
							conceptUnit = entry2.getValue().toString();
				}

				personid_scan = conn.createScanner(personObservationIndexTable, auths);
				personid_scan.enableIsolation();
				personid_scan.fetchColumnFamily(new Text("CF1"));

				personIds = listOfPersonIds.split(",");  // List of person ids are comma separated
				for (String personId: personIds) {
					personid_scan.setRange(new Range(personId + "~" + conceptId));

					for(Entry<Key,Value> entry1 : personid_scan) {
						personObservationIds.add(entry1.getValue().toString());
					}
				}

			}
		} catch(Exception e) {
			logger.error (e.toString());
			return;
		} finally {
			if(concept_scan != null) {
				concept_scan.close();
			}
			if(personid_scan != null){
				personid_scan.close();
			}
		}

		if (personObservationIds.size() > 0) {

			try {
				death_scan = conn.createScanner(deathTable, auths);
				death_scan.enableIsolation();
				death_scan.fetchColumnFamily(new Text("CF1"));

				personIds = listOfPersonIds.split(",");  // List of person ids are comma separated
				for (String personId: personIds) {
					death_scan.setRange(new Range(personId));

					for(@SuppressWarnings("unused") Entry<Key,Value> entry1 : death_scan) {
						personIdDeaths.put(personId, "Death");
					}
				}
			} catch(Exception e) {
				logger.error (e.toString());
				return;
			} finally {
				if(death_scan != null) {
					death_scan.close();
				}
			}

			try {
				observation_scan = conn.createScanner(observationTable, auths);
				observation_scan.enableIsolation();
				observation_scan.fetchColumnFamily(new Text("CF1"));

				for (String personObservationId: personObservationIds) {
					writeLine = "";

					observation_scan.setRange(new Range(personObservationId));

					for(Entry<Key,Value> entry3 : observation_scan) {
						if (entry3.getKey().getColumnQualifier().toString().matches("observation_concept_id")) {
							observationName = conceptName;
							observationUnit = conceptUnit;
						}

						if (entry3.getKey().getColumnQualifier().toString().matches("person_id"))
							observationPersonId = entry3.getValue().toString().trim();

						if (entry3.getKey().getColumnQualifier().toString().matches("observation_date"))
							observationDate = entry3.getValue().toString().trim();

						if (entry3.getKey().getColumnQualifier().toString().matches("observation_time"))
							observationTime = entry3.getValue().toString().trim();

						if (entry3.getKey().getColumnQualifier().toString().matches("value_as_number"))
							observationValue = entry3.getValue().toString().trim();
					}

					personIdMortality = personIdDeaths.get(observationPersonId);

					writeLine = observationPersonId + "\t" + observationDate + ";" + observationTime + ";" + observationValue + ";" + observationUnit + ";" + observationName + ";" + personIdMortality + ";";
					outputFileData.add(writeLine);
				}
			} catch(Exception e) {
				logger.error (e.toString());
				return;
			} finally {
				if(observation_scan != null) {
					observation_scan.close();
				}
			}
		}

		// Create output file even if no data is fetched
		try {
			bw = new BufferedWriter(new FileWriter(outputFile)); // Open output file
		} catch(IOException e) {
			logger.error (e.toString());
			return;
		}

		if (outputFileData.size() > 0) {

			Iterator<String> iterator = outputFileData.descendingIterator();

			try {
				while (iterator.hasNext()) {

					writeLine = (String)iterator.next();
					bw.write(writeLine);
					bw.newLine();
				}
			} catch(IOException e) {
				logger.error (e.toString());
			} finally {
				try {
					if (bw != null)
						bw.close();
				} catch(IOException f) {
					logger.error (f.toString());
				}
			}
		}
	}

	public static void main(String[] args) {

		if (args.length != 12) {
			logger.error ("Usage: ObservationIdSearchInAccumulo <output file> <person ids> <authorizations> <accumulo instance> <zookeeper servers> <accumulo user> <accumulo password> <observation id>");
			System.exit(-1);
		}

		ObservationIdSearchInAccumulo observationIdSearch = new ObservationIdSearchInAccumulo();
		observationIdSearch.searchObservationId(args);

		System.exit(0);
	}
}