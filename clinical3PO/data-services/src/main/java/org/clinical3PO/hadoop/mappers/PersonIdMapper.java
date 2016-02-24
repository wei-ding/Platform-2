package org.clinical3PO.hadoop.mappers;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.clinical3PO.environment.EnvironmentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersonIdMapper extends Mapper<LongWritable, Text, Text, Text> {

	private String personId = null;
	private String envType = null;
	private StringBuilder builder = null;

	private static final Logger logger = LoggerFactory.getLogger(PersonIdMapper.class);

	public PersonIdMapper() {
		builder = new StringBuilder();
	}

	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

		String line = value.toString();

		if(envType != null && EnvironmentType.DEVELOPMENT == EnvironmentType.valueOf(envType)) {
			logger.info ("Value of line : " + line);
		}

		StringTokenizer tokens = new StringTokenizer(line, ";");
		@SuppressWarnings("unused")
		final String token_0 = tokens.nextToken();
		@SuppressWarnings("unused")
		final String token_1 = tokens.nextToken();
		final String personId_2 = tokens.nextToken();
		final String token_3 = tokens.nextToken();
		final String token_4 = tokens.nextToken();
		final String token_5 = tokens.nextToken();
		final String token_6 = tokens.nextToken();

		if(envType != null && EnvironmentType.DEVELOPMENT == EnvironmentType.valueOf(envType)) {
			logger.info ("Size of array : " + tokens.countTokens());
			logger.info ("person_id : " + personId_2);
		}

		if(personId != null && personId.equals(personId_2)) { // matching Person Id

			String outValue = builder.append(token_3).append(";").append(token_4).append(";")
					.append(token_5).append(";").append(token_6).append(";").toString();
			context.write(new Text(personId),  new Text(outValue));
			builder.delete(0, builder.length());
		}
	}

	@Override
	public void setup(Context context) throws IOException, InterruptedException {

		personId = context.getConfiguration().get("personId");
		envType = context.getConfiguration().get("envType");
	}
}