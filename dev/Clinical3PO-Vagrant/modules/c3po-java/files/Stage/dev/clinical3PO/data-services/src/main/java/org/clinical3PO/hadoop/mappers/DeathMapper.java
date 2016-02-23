package org.clinical3PO.hadoop.mappers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.clinical3PO.environment.EnvironmentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeathMapper extends Mapper<LongWritable, Text, Text, Text> {

	private String envType = null;
	private Map<String, String> deathPersonIdMap = null;
	private static final Logger logger = LoggerFactory.getLogger(DeathMapper.class);

	public DeathMapper() {
		deathPersonIdMap = new HashMap<String, String>();
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

		if (deathPersonIdMap.containsKey(personId_deathFile)) // matching Person Id
			deathPersonIdMap.put(personId_deathFile, "Death");
	}

	@Override
	public void setup(Context context) throws IOException, InterruptedException {

		String personId_config = context.getConfiguration().get("personId");
		StringTokenizer st = new StringTokenizer(personId_config, ",");
		while(st.hasMoreElements()) {
			deathPersonIdMap.put(st.nextToken(), "Alive");
		}
	}

	@Override
	public void cleanup(Context context) throws IOException, InterruptedException {

		for (Map.Entry<String, String> entry : deathPersonIdMap.entrySet()) {
			context.write(new Text(entry.getKey() + "~0"),  new Text(entry.getValue()));
		}
		deathPersonIdMap.clear();
		deathPersonIdMap = null;

		envType = context.getConfiguration().get("envType");
		logger.info("EnvType: "+envType);
	}
}