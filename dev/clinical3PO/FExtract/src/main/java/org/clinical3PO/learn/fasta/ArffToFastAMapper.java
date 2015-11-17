package org.clinical3PO.learn.fasta;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import com.google.gson.Gson;

public class ArffToFastAMapper extends Mapper<LongWritable, Text, Text, Text>{

	private String[] attributes = null;
	private int length = 17;
	private StringBuilder sb = null;
	private boolean pidFlag = false;

	@Override
	public void setup(Context context) {

		Configuration conf = context.getConfiguration();
		attributes = conf.get("attribute").split(",");
		sb = new StringBuilder();

		// Following steps are part of De-serializing the ArffToFastAProperties class object using Gson API.
		String deserObject = conf.get("properties");
		Gson gson = new Gson();
		ArffToFastAProperties object = gson.fromJson(deserObject, ArffToFastAProperties.class);
		pidFlag = object.getPidFlag();
	}

	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

		String line = value.toString();
		
		// Allow to parse only if the line doesn't start with the following declared strings.
		if(!line.isEmpty() && !line.startsWith("@relation") && !line.startsWith("@attribute") 
				&& !line.startsWith("%") && !line.startsWith("@data")) {

			String[] data = line.split(",");

			int j = 0;
			int k = 0;
			int i = 0;
			
			/*
			 * if flag, means there is patientId available with ARFF file so, set i=1(which means i=0 is patientID).
			 * example: pid,?,?,?,?,?,low
			 * else, means there isn't patientId available with ARFF file so, i=0(without patientID)
			 * example: ?,?,?,?,?,low 
			 */
			if(pidFlag) {
				i = 1;
			} 
			
			/*
			 * Arff contains header, data along with some others.
			 * All Vertical Header's represent each Horizontal data. I.e. In every line of data, all the headers were present.
			 * 
			 * This function gives data from arff file. 
			 * Based on the pre-processed headers available in attributes[] and also each attribute hold 17-bins from the arff data, 
			 * 	iterate accordingly to write attribute as key and 17-bin values as value to the reducer.
			 */
			int len = data.length -1;
			for(; i < len; ) {

				if(pidFlag) {
					sb.append(data[0]).append(",");
				}
				j=i+length;
				while(i < j) {
					sb.append(data[i]).append(",");
					i++;
				}
				i = j;
				sb.append(data[len]);
				context.write(new Text(attributes[k]), new Text(sb.toString()));
				k++;
				sb.delete(0, sb.length());
			}
		}
	}
}
