package org.clinical3PO.hadoop.mappers;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.clinical3PO.environment.EnvironmentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatientUniqueCountMapper extends Mapper<LongWritable, Text, Text, IntWritable> { //Mapper<KEYIN, VALUEIN, KEYOUT, VALUEOUT>

	private String envType = null;
	private int validLenOfObservationList = 0;
	private static final Logger logger = LoggerFactory.getLogger(PatientUniqueCountMapper.class);

	PatientUniqueCountMapper() {
		String temp = "OBSERVATION;observation_id;person_id;observation_concept_id;observation_date;"
				+ "observation_time;value_as_number;value_as_string;value_as_concept_id;unit_concept_id;"
				+ "range_low;range_high;observation_type_concept_id;associated_provider_id;visit_occurrence_id;"
				+ "relevant_condition_concept_id;observation_source_value;units_source_value";
		validLenOfObservationList = temp.split(";").length;
	}

	@Override
	public void setup(Context context) throws IOException, InterruptedException {
		envType = context.getConfiguration().get("envType");
	}

	@Override
	public void map(LongWritable key, Text value, Context context) 
			throws IOException, InterruptedException {

		if(value != null && !value.equals(null)) {
			String line = value.toString();

			if(envType != null && EnvironmentType.DEVELOPMENT == EnvironmentType.valueOf(envType)) {
				logger.info ("Value of line : " + line);
			}
			String[] pIdArray = line.split(";");

			// checking if the record is complete or not. 
			if(pIdArray.length == validLenOfObservationList) {

				if (!pIdArray[2].matches("person_id")) {  // skipping 1st line
					context.write(new Text(pIdArray[2]),  new IntWritable(0));
				}
			} else {
				logger.error("Parameters provided are incorect. ");
			}
		} else {
			logger.error("File provides ");
		}
	}
}