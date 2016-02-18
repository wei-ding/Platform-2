package org.clinical3PO.learn.main;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class FEConceptMapper extends Mapper<LongWritable, Text, Text, NullWritable> {

	private String[] observationArray = null;
	private Text key = new Text();
	private NullWritable value = NullWritable.get();
	private StringBuffer sb = new StringBuffer();

	@Override
	public void setup(Context context) {

		String lineSeparator = System.getProperty("line.separator");
		Configuration conf = context.getConfiguration();

		// If the 'classProperty' is not set in Driver class, empty string is returned instead of exception.
		String classProperty = conf.get("classProperty", "");

		// If the 'filtconfContents' property is not set in Driver class, then the method return 'classProperty' instead of exception  
		observationArray = conf.get("filtconfContents", classProperty).split(lineSeparator);
	}

	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

		String conceptLine = value.toString();

		if(conceptLine.length() > 0) {
			String[] lineArray = conceptLine.split(";");
			
			for(String observation : observationArray) {
				if(lineArray[1].equalsIgnoreCase(observation)) {
					this.key.clear();

					// key would be the conceptID (commnon key related in observation file) and class/observation name.
					sb.append(lineArray[0]).append("\t").append(lineArray[1]);
					this.key.set(sb.toString());	// set value to key
					context.write(this.key, this.value);	// write into output
					System.err.println(sb.toString());	// logg  
					sb.delete(0, sb.length());		//clear the buffer
				}
			}
		}
	}
}
