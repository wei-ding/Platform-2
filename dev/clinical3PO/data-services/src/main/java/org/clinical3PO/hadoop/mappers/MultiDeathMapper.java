package org.clinical3PO.hadoop.mappers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.clinical3PO.environment.EnvironmentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * One of the Multiple Mapper's belongs to MultiObservationId.java (driver class)
 * Inputs : death.txt file
 * This get the list of patientIds from configuration, which is set in driver class.
 * 
 * Job : To compare whether the list of patients are available in input file.
 * 
 * Output : Key - ObservationType~PatientID, Value-Death OR Alive 
 *   
 * @author 3129891
 *	
 */
public class MultiDeathMapper extends Mapper<LongWritable, Text, Text, Text> {

	private String envType = null;
	private Map<String, String> deathPersonIdMap = null;
	private Map<String, List<String>> keyAsObservationId_valueAsPatientIdsList_Map = null;
	private static final Logger logger = LoggerFactory.getLogger(MultiDeathMapper.class);

	public MultiDeathMapper() {
		deathPersonIdMap = new HashMap<String, String>();
		keyAsObservationId_valueAsPatientIdsList_Map = new HashMap<String, List<String>>();
	}

	@Override
	public void setup(Context context) {

		envType = context.getConfiguration().get("envType");

		String inputedFileForProcessing = context.getConfiguration().get("inputParam");
		StringTokenizer inidvidualString_observationWithIdsAndColor = new StringTokenizer(inputedFileForProcessing, "#");

		while(inidvidualString_observationWithIdsAndColor.hasMoreTokens()) {

			String observationIdWiseInput = inidvidualString_observationWithIdsAndColor.nextToken();
			StringTokenizer tokens_Obs_ids_color = new StringTokenizer(observationIdWiseInput, "~");

			String observationType = tokens_Obs_ids_color.nextToken();
			String personIds = tokens_Obs_ids_color.nextToken();

			// Adding PersonId's as key and empty string into map.
			iterateArrayToInsertIntoMap(new StringTokenizer(personIds, ","));

			if(keyAsObservationId_valueAsPatientIdsList_Map.containsKey(observationType)) {

				List<String> values = keyAsObservationId_valueAsPatientIdsList_Map.get(observationType);
				StringTokenizer personId_st = new StringTokenizer(personIds, ",");
				while(personId_st.hasMoreTokens()) {
					values.add(personId_st.nextToken());
				}
				keyAsObservationId_valueAsPatientIdsList_Map.put(observationType, values);
			} else {
				List<String> patientList = new ArrayList<String>();
				StringTokenizer personId_st = new StringTokenizer(personIds, ",");
				while(personId_st.hasMoreTokens()) {
					patientList.add(personId_st.nextToken());
				}
				keyAsObservationId_valueAsPatientIdsList_Map.put(observationType, patientList);
			}

			if(envType !=null && EnvironmentType.DEVELOPMENT == EnvironmentType.valueOf(envType)) {
				logger.info ("observationIdWiseInput : " + observationIdWiseInput);
			}
			tokens_Obs_ids_color = null;
		}
	}

	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

		String line = value.toString();

		if(envType !=null && EnvironmentType.DEVELOPMENT == EnvironmentType.valueOf(envType)) {
			logger.info ("DeathMapper Value of line :" + line);
		}

		String[] pidArr = line.split("\\t");
		String personId_deathFile = pidArr[1];

		if(envType !=null && EnvironmentType.DEVELOPMENT == EnvironmentType.valueOf(envType)) {
			logger.info ("DeathMapper Size of array : " + pidArr.length);
			logger.info ("DeathMapper person_id : " + personId_deathFile);
		}

		/*
		 *  Check it the patientId read from Death file is available in map or not.
		 *  If present, replacing patientID-key, Empty-value with patientID-key, Death-value
		 */
		if (deathPersonIdMap.containsKey(personId_deathFile)) {
			deathPersonIdMap.put(personId_deathFile, "Death");
		}
	}

	@Override
	public void cleanup(Context context) throws IOException, InterruptedException {

		Set<Entry<String,List<String>>> entrySet = keyAsObservationId_valueAsPatientIdsList_Map.entrySet();

		// Looping status: n-patients = n-iterations
		for(Map.Entry<String, String> entry : deathPersonIdMap.entrySet()) {

			String patientId = entry.getKey();					// patientID
			String mortality_status = entry.getValue();			// Death or Empty("")
			if(mortality_status.isEmpty()) {
				mortality_status = "Alive";
			}

			Iterator<Entry<String, List<String>>> it = entrySet.iterator();
			while(it.hasNext()) {

				Map.Entry<String, List<String>> keyAsObservationIdList_valueAsPatientIds_EntryMap = it.next();
				if(keyAsObservationIdList_valueAsPatientIds_EntryMap.getValue().contains(patientId)) {

					String observationType = keyAsObservationIdList_valueAsPatientIds_EntryMap.getKey();

					// Hadoop String Construction
					Text outputKey = new Text(observationType + "~" + patientId);

					// Writing into context for Reducer
					context.write(new Text(outputKey), new Text(mortality_status));
				} // Do not use break. Reason being: patient(in a bunch with comma separated) can get repeated for different observations
			}
			it = null;
		}
		keyAsObservationId_valueAsPatientIdsList_Map.clear();
		deathPersonIdMap.clear();
		deathPersonIdMap = null;
		keyAsObservationId_valueAsPatientIdsList_Map = null;
	}

	/**
	 * Iterating over the tokenizer of personIds. Inserting personId's into map as key and empty string as value.
	 * This map is later used to compare with the death file.
	 * 
	 * @param StringTokenizer with ',' separated patientId's.
	 */
	private void iterateArrayToInsertIntoMap(StringTokenizer st) {

		while(st.hasMoreTokens()) {
			deathPersonIdMap.put(st.nextToken(), "");
		}
	}
}