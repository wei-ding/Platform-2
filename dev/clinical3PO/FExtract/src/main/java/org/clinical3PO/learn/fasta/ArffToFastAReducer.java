package org.clinical3PO.learn.fasta;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

import com.google.gson.Gson;

public class ArffToFastAReducer extends Reducer<Text, Text, Text, NullWritable>{

	private Text text = null;
	private String relation = null;
	private boolean pidFlag = false;
	private StringBuilder builder = null;
	private Map<String, HashMap<String, String>> propertiesMap = null; 
	private MultipleOutputs<Text, NullWritable> mos = null;

	@Override
	public void setup(Context context) throws IOException {

		Configuration conf = context.getConfiguration();
		relation = conf.get("relation");

		// Following steps are part of De-serializing the ArffToFastAProperties class object using Gson API.
		String deserObject = conf.get("properties");
		Gson gson = new Gson();
		ArffToFastAProperties object = gson.fromJson(deserObject, ArffToFastAProperties.class);
		propertiesMap = object.getPropertiesMap();
		pidFlag = object.getPidFlag();
		builder = new StringBuilder();
		text = new Text();
		
		mos = new MultipleOutputs<Text, NullWritable>(context);
	}

	@Override
	public void reduce(Text key, Iterable<Text> values, Context context) 
			throws IOException, InterruptedException {

		String in_key = key.toString();
		String[] key_array = in_key.split("_");
		String attribute = key_array[0];

		String[] tokens = null;

		// Iterate over all the values for each unique key.
		for(Text val : values) {

			tokens = val.toString().split(",");
			int lengthOfToken = tokens.length;
			int i = 0;

			/*
			 * if flag, means there is patientId available with ARFF file so, enable sequence with patientID, set i=1(which means i=0 is patientID).
			 * example: pid,?,?,?,?,?,low
			 * else, means there isn't patientId available with ARFF file so, enable sequence without patientID, i=0(without patientID)
			 * example: ?,?,?,?,?,low 
			 */
			if(pidFlag) {
				i = 1;
				String pid = tokens[0];
				builder.append(">pid "+pid+" |class "+relation + " |attribute "+key.toString()+"\n");
			} else {
				builder.append(">class "+relation + " |attribute "+key.toString()+"\n");
			}

			/*
			 * for every individual value, iterate based on its length(after separating with ',').
			 * Each character contains some value(say it a '?' or 'low' or 'high' or any datatype).
			 * For each value there's a parameter declared and will be replaced to construct a new string(FASTA) 
			 */
			for(; i < lengthOfToken; i++) {

				String ch = tokens[i];
				if(ch.equals("?")) {
					builder.append("N");				
				} else {
					if(ch.equalsIgnoreCase("low")) {
						builder.append("Z");
					} else if(ch.equalsIgnoreCase("verylow")) {
						builder.append("Y");
					} else if(ch.equalsIgnoreCase("normal")) {
						builder.append("X");
					} else if(ch.equalsIgnoreCase("borderline")) {
						builder.append("W");
					} else if(ch.equalsIgnoreCase("high")) {
						builder.append("V");
					} 

					/*
					 * If program reach this level of condition, this could be a value instead of any kind of string.
					 * Convert the value in string to Float for descritization.
					 */
					else {
						try {
							float value = Float.parseFloat(ch);

							//method call
							builder.append(descritize(value, attribute));
						} catch(NumberFormatException ne) {
							System.err.println(ne);
						}
					}
				}
			}
			text.set(builder.toString());
			
			//DO NOT REMOVE THIS TO UNDERSTAND THE BELOW CODE.
			//context.write(text, NullWritable.get());
			
			/*
			 * Writing to context would reflect in part-r-0000 series(1-to-n).
			 * Writing to mos(MultipleOutputs) would reflect in filename-r-0000 series.
			 * I.e. While n-reducers process n-unique data-sets, each reducer writes its data to a output file
			 * 	start wiht part-r-0000. MultipleOutputs(mos) would help to set the name of the reducer output.
			 * Instead of part-r-0000, using mos we could name reducer file to anyName-r-0000.
			 * 
			 * In this program, key of reducer would reflect as reducer filenames.
			 */
			mos.write(text, NullWritable.get(), in_key.replace("_", "-"));			
			builder.delete(0, builder.length());
			text.clear();
			tokens = null;
		}
	}
	
	@Override
	public void cleanup(Context context) throws IOException, InterruptedException {
		mos.close();
	}

	/**
	 * This method perform descritization of string/values.
	 * Descritize values are available in propertiesMap. Based on the keys, get the appropriate descritized values.
	 * 
	 * @param value
	 * @param attribute
	 * @return descritized string (EX: ADVGDSESSEC)
	 */
	private String descritize(float value, String attribute) {

		String discreteValue = null;
		Map<String, String> map = propertiesMap.get(attribute);
		if(map != null) {

			if(!map.get("min").isEmpty() && !map.get("max").isEmpty() 
					&& !map.get("descritize_in").isEmpty() && !map.get("descritize_out").isEmpty()) {

				float min = Float.parseFloat(map.get("min"));
				float max = Float.parseFloat(map.get("max"));

				if(value >= min && value <= max) {
					discreteValue = map.get("descritize_in");
				} else {
					discreteValue = map.get("descritize_out");
				}				
			}
		}
		return discreteValue;
	}
}