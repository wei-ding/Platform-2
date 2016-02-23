package org.clinical3PO.hadoop.reducers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.clinical3PO.environment.EnvironmentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersonIdReducer
extends Reducer<Text, Text, NullWritable, Text> {

	private static final Logger logger = LoggerFactory.getLogger(PersonIdReducer.class);

	String envType = null;

	Map<Integer, String> conceptIds = new HashMap<Integer, String>();

	@Override
	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

		String line = null;
		String[] pidArr;
		String[] conceptidArr;
		String outValue = null;
		String tmpStr = null;
		Integer tmpNum = 0;
		
		for (Text value: values) {
			line = value.toString();
			pidArr = line.split(";");

			outValue = null;
			tmpNum = Integer.parseInt(pidArr[0]);
			tmpStr = conceptIds.get(tmpNum);
			conceptidArr = tmpStr.split(";");

			outValue = conceptidArr[0] + ";" + pidArr[1] + ";" + pidArr[2] + ";" + pidArr[3] + ";" + conceptidArr[1] + ";";
			context.write(NullWritable.get(),  new Text(outValue));

			if(envType != null && EnvironmentType.DEVELOPMENT == EnvironmentType.valueOf(envType)) {
				logger.info ("In Reduce Size of array : " + conceptidArr.length);
				logger.info ("In Reduce property_name : " + conceptidArr[0]);
				logger.info ("In Reduce value_units : " + conceptidArr[1]);
				logger.info ("Reducer Output : " + key.toString() + "	" + outValue);
			}

		}
	}

	@Override
	public void setup(Context context) throws IOException, InterruptedException {

		String line;
		String[] conceptIdArr;

		FileSystem fs = FileSystem.get(context.getConfiguration());

		envType = context.getConfiguration().get("envType");

		String conceptFileInHadoop = (String)context.getConfiguration().get("conceptFile");
		
		if(envType != null && EnvironmentType.DEVELOPMENT == EnvironmentType.valueOf(envType)) {
			logger.info ("PersonIdReduce:conceptFileInHadoop : " + conceptFileInHadoop);
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(new Path(conceptFileInHadoop))));
		try {
			line = br.readLine();
			while (line != null) {
				//System.out.println("Value of line : " + line);

				conceptIdArr = line.split(";");

				if (!conceptIdArr[0].trim().matches("src_concept_id")) { // skipping 1st line
					if (conceptIdArr[4].trim().length() == 0)
						conceptIdArr[4] = "No Unit";
					// Storing property name and units
					conceptIds.put(Integer.parseInt(conceptIdArr[0].trim()), conceptIdArr[1].trim() + ";" + conceptIdArr[4].trim());
				}

				if(envType != null && EnvironmentType.DEVELOPMENT == EnvironmentType.valueOf(envType)) {
					logger.info ("Size of array : " + conceptIdArr.length);
					logger.info ("concept_id : " + conceptIdArr[0].trim());
					logger.info ("property_name : " + conceptIdArr[1].trim());
					logger.info ("value_units : " + conceptIdArr[4].trim());
				}

				// read the next line
				line = br.readLine();
			}
		} catch (IOException e) {
			logger.error ("Error while reading concept file");
		} finally {
			br.close();
		}
	}
}

