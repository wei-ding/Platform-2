package org.clinical3PO.hadoop.reducers;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.clinical3PO.environment.EnvironmentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObservationIdReducer
extends Reducer<Text, Text, Text, Text> {

	String observationIdUnit;
	String observationIdName;

	String envType = null;

	private static final Logger logger = LoggerFactory.getLogger(ObservationIdReducer.class);

	@Override
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

		String[] pidArr;
		String[] keyArr;
		String outKey = null;
		String outValue = null;

		Iterator<Text> iter = values.iterator();
		Text mortality = new Text(iter.next());
		
		while (iter.hasNext()) {

			keyArr = key.toString().split("~");
			outKey = keyArr[0];

			String line = iter.next().toString();
			pidArr = line.split(";");

			outValue = pidArr[0] + ";" + pidArr[1] + ";" + pidArr[2] + ";" + observationIdUnit + ";" + 
			observationIdName + ";" + mortality + ";";

			context.write(new Text(outKey), new Text(outValue));

			if(envType !=null && EnvironmentType.DEVELOPMENT == EnvironmentType.valueOf(envType)) {
				logger.info ("Reducer Output : " + key.toString() + "	" + outValue);
			}
		}
	}

	@Override
	public void setup(Context context)
			throws IOException, InterruptedException {

		observationIdUnit = context.getConfiguration().get("observationIdUnit");
		observationIdName = context.getConfiguration().get("observationIdName");

		envType = context.getConfiguration().get("envType");
	}
}