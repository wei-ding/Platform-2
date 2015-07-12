package org.clinical3PO.hadoop.mappers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.clinical3PO.environment.EnvironmentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiObservationIdMapper extends Mapper<LongWritable, Text, Text, Text> {

	private String envType = null;
	private StringBuilder builder = null;
	private Map<String, String> conceptId_observationId_map = null;
	private Map<String, List<String>> keyAsObservationIdList_valueAsPatientIds_EntryMap = null;
	private static final Logger logger = LoggerFactory.getLogger(MultiObservationIdMapper.class);

	public MultiObservationIdMapper() {

		builder = new StringBuilder();
		conceptId_observationId_map = new HashMap<String, String>();
		keyAsObservationIdList_valueAsPatientIds_EntryMap = new HashMap<String, List<String>>();
	}

	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

		String line = value.toString();

		if(envType !=null && EnvironmentType.DEVELOPMENT == EnvironmentType.valueOf(envType)) {
			logger.info ("Value of line : " + line);
		}

		StringTokenizer tokens = new StringTokenizer(line, ";");

		tokens.nextToken(); // not used
		tokens.nextToken(); // not used
		final String patientId_observationFile = tokens.nextToken();
		final String observationConceptId = tokens.nextToken();
		final String observationDate = tokens.nextToken();
		final String observationTime = tokens.nextToken();
		final String observationValue = tokens.nextToken();

		if(envType !=null && EnvironmentType.DEVELOPMENT == EnvironmentType.valueOf(envType)) {
			logger.info ("Size of array : " + tokens.countTokens());
			logger.info ("person_id : " + patientId_observationFile);
			logger.info("observation_id : " + observationConceptId);
		}

		String observationId = null;
		List<String> personIdList = null;
		for (Map.Entry<String, List<String>> entry : keyAsObservationIdList_valueAsPatientIds_EntryMap.entrySet()) {

			observationId = entry.getKey();
			if (observationId.equals(observationConceptId)) { // matching Observation_Concept_Id
				personIdList = entry.getValue();
				for (String patientId : personIdList) {
					if (patientId.equals(patientId_observationFile)) { // matching Person Id
						String outValue = builder.append(observationDate).append(";").append(observationTime).append(";")
								.append(observationValue).append(";").toString();
						builder.delete(0, builder.length());
						context.write(new Text(conceptId_observationId_map.get(observationId) + "~" + patientId),  new Text(outValue));
						break; // Line will contain only one observation id + person id detail
					}
				}
				break; // Line will contain only one observation id + person id detail
			}
		}
	}

	@Override
	public void setup(Context context) throws IOException, InterruptedException {

		envType = context.getConfiguration().get("envType");
		try {

			/*
			 * Parsing 'observationDetails' arguments
			 */
			String observationDetails = context.getConfiguration().get("observationDetails");
			String[] observationIdWiseDetails = observationDetails.split("#");
			Map<String, String>  observationId_conceptId_map = new HashMap<String, String>();

			String[] tmpStr = null;
			for (String observationIdWiseDetail : observationIdWiseDetails) {
				tmpStr = observationIdWiseDetail.split("~");
				observationId_conceptId_map.put(tmpStr[0], tmpStr[1]);
				conceptId_observationId_map.put(tmpStr[1], tmpStr[0]);
				tmpStr = null;
			}

			/*
			 * Parsing 'inputParam' arguments
			 */
			String observationIdWithRespectiveToConceptFile = null;
			String inputedFileForProcessing = context.getConfiguration().get("inputParam");
			StringTokenizer inidvidualString_observationWithIdsAndColor = new StringTokenizer(inputedFileForProcessing, "#");

			while(inidvidualString_observationWithIdsAndColor.hasMoreTokens()) {

				String observationIdWiseInput = inidvidualString_observationWithIdsAndColor.nextToken();
				StringTokenizer tokens_Obs_ids_color = new StringTokenizer(observationIdWiseInput, "~");

				observationIdWithRespectiveToConceptFile = observationId_conceptId_map.get(tokens_Obs_ids_color.nextToken());
				StringTokenizer personIds = new StringTokenizer(tokens_Obs_ids_color.nextToken(), ",");

				if (keyAsObservationIdList_valueAsPatientIds_EntryMap.containsKey(observationIdWithRespectiveToConceptFile)) {

					List<String> value_personId_list = keyAsObservationIdList_valueAsPatientIds_EntryMap.get(observationIdWithRespectiveToConceptFile);
					while(personIds.hasMoreTokens()) {
						value_personId_list.add(personIds.nextToken());
					}
					keyAsObservationIdList_valueAsPatientIds_EntryMap.put(observationIdWithRespectiveToConceptFile, value_personId_list);
				}
				else {
					List<String> personIdList = new ArrayList<String>();
					while(personIds.hasMoreTokens()) {
						personIdList.add(personIds.nextToken());
					}
					keyAsObservationIdList_valueAsPatientIds_EntryMap.put(observationIdWithRespectiveToConceptFile, personIdList);
				}
			}
		} catch (ArrayIndexOutOfBoundsException ae) {
			logger.error("Setup method had encountered error, Iterating over an array which doesn't have enough range");
		}
	}
}