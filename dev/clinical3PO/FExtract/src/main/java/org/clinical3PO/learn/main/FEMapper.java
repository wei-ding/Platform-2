package org.clinical3PO.learn.main;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;


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
