package org.clinical3PO.hadoop.reducers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * Is a Reducer class for Multiple Observation search on Hadoop.
 * Inputs: Outputs from 1. MultiObservationIdMapper.java
 * 						2. MultiDeathMapper.java
 * 
 * Job : Combining the output's of Mappers and produce the required information of patientIds 
 * 		with given observations.
 * 
 * OutPut : Text written into a file with combined output from mappers.
 * 
 * @author 3129891
 *
 */
public class MultiObservationIdReducer extends Reducer<Text, Text, Text, Text> {

	private List<String> list = null;
	private StringBuilder sb = null;
	private Map<String, String> observationIdsName = null;
	private Map<String, String> observationIdsUnit = null;

	public MultiObservationIdReducer() {

		sb = new StringBuilder();
		list = new ArrayList<String>();
		observationIdsName = new HashMap<String, String>();
		observationIdsUnit = new HashMap<String, String>();
	}

	/**
	 * Is a overwritten method of Reducer.
	 * Called only once at the beginning of the class initialization.
	 * 
	 */
	@Override
	public void setup(Context context) throws IOException, InterruptedException {

		String[] observationIdWiseDetails = null;
		String observationDetails = context.getConfiguration().get("observationDetails");

		observationIdWiseDetails = observationDetails.split("#");
		if(observationIdWiseDetails.length <= 0) {
			observationIdWiseDetails = new String[1];
			observationIdWiseDetails[0] = observationDetails;
		}
		for (String observationIdWiseDetail : observationIdWiseDetails) {

			StringTokenizer st = new StringTokenizer(observationIdWiseDetail, "~");
			String token0 = st.nextToken();
			@SuppressWarnings("unused")
			String token1 = st.nextToken();
			String token2 = st.nextToken();
			String token3 = st.nextToken();
			observationIdsName.put(token0, token2);
			observationIdsUnit.put(token0, token3);
		}
	}

	/**
	 * Is a overwritten method of Reducer.
	 * Gets called as many times of unique keys availability.
	 * 
	 * Receives 2 different inputs from 2 different Mappers.
	 * 1 - Give mortality status (a single word)
	 * 2 - Gives the details of the patient with provided observation. (multiple words with delimiter)
	 * 
	 * As reducer gives all the values of a key in an order :
	 * 	Breaking the values using ';' and checking for more than 1 word, would differentiate the Mapper Outputs.
	 * 	Keep adding the Mortality status to each value. 
	 * 
	 */
	@Override
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		
		String outValue = null;
		String observationIdName = null;
		String observationIdUnit = null;
		String death_status = "";
		String key_details = key.toString();
		String[] keyArr = key_details.split("~");

		for(Text value_details : values) {

			String value_details_str = value_details.toString();
			StringTokenizer st = new StringTokenizer(value_details_str, ";");
			if(st.countTokens() > 1) {

				String token0 = st.nextToken();
				String token1 = st.nextToken();
				String token2 = st.nextToken();

				observationIdName = observationIdsName.get(keyArr[0]);
				observationIdUnit = observationIdsUnit.get(keyArr[0]);

				outValue = sb.append(token0).append(";").append(token1).append(";").append(token2).append(";")
						.append(observationIdUnit).append(";").append(observationIdName).append(";").toString();
				sb.delete(0, sb.length());
				list.add(outValue);
			} else {
				if(!value_details_str.isEmpty()) {
					death_status = value_details_str;
				}
			}
		}

		/*
		 * For every iteration on the values of each key, mortality status is appended.
		 * Writing the string to context and clearing the buffer for next iteration on reduce method.
		 */
		for(String temp : list) {

			temp = sb.append(temp).append(death_status).append(";").toString();
			context.write(new Text(key_details), new Text(temp));
			sb.delete(0, sb.length());
		}
		list.clear();
	}
}