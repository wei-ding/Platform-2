package org.clinical3PO.learn.main;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;


/**
 * Is a Hadoop Mapper Code.
 * Previous Mapper(https://github.com/Clinical3PO/Stage/commit/e5acba642e1deefbdba5d556cf7c65bb5212ce25) intakes, Output of Wim's code.
 * To similirize every module with OMPOPv4 format, changes have been done to the mapper.
 * 
 *  Reads line by line and picks important information like patientId, observation(key with ~ as separator), time, observation value(value with space as separator).
 *  This is further written into context.
 *  
 *  NOTE: Previous Mapper code is put into Reducer. 
 *  
 * @author 3129891
 *
 */
public class FEMapper extends Mapper<LongWritable, Text, Text, Text>{

	private Text key_ = null;
	private Text value_ = null;
	private static final String regexp = "[\\s,;]+";
	
	public void setup(Context context) {
		
		key_ = new Text();
		value_ = new Text();
	}
	
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		
		String line = value.toString();
		String[] tokens = line.split(regexp);
		
		key_.clear();
		value_.clear();
		key_.set(tokens[2]+"~"+tokens[3]);
		value_.set(tokens[5] +" "+tokens[6]);
		context.write(key_, value_);
	}
}
