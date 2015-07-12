package org.clinical3PO.hadoop.reducers;

import java.io.IOException;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatientUniqueCountReducer extends Reducer<Text, Text, Text, NullWritable> {

	private static final Logger logger = LoggerFactory.getLogger(PatientUniqueCountReducer.class);

	@Override
	public void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {

		if(key != null) {
			context.write(key, NullWritable.get());
		}
	}
}