package org.clinical3PO.learn.main;

import java.io.IOException;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.clinical3PO.learn.util.C3POTimeRange;

import com.google.gson.Gson;

/**
 * Mapper class.
 * This class emits the patient details for those who fall with in the given timeframe and classes.
 * @author 3129891
 *
 */
public class FECoreMapper extends Mapper<LongWritable, Text, FEDataObjects, NullWritable> {

	private Text key = null;
	private Text value = null;
	private Map<String, String> conceptDataMap = null;
	private long startTime = 0;
	private long endTime = 0;

	@Override
	public void setup(Context context) {

		key = new Text();
		value = new Text();
		Configuration conf = context.getConfiguration();
		String conceptData = conf.get("conceptData", "");
		
		/*
		 * Getting the JSON from the reference which is set from the driver.
		 */
		Gson gson = new Gson();
		FEUtils utils = gson.fromJson(conceptData, FEUtils.class);
		conceptDataMap = utils.getMap();

		/*
		 * Parsing the given time ranges.
		 * Our class supports only hh:mm:ss format. But if the input is only hh:mm, then the seconds part is added.
		 * Convert the given time and date formats into a long (which makes comparison easy)
		 */
		StringBuffer sb = new StringBuffer();
		String ts = conf.get("c3fe.starttime", "00:00:00");		// if the time is not available providing default time
		ts = C3POTimeRange.validateTimestamp(ts);				// method call to validate the time format
		sb.append(conf.get("c3fe.startdate", "")).append("\t").append(ts);
		System.err.println("INPUT: Start Date & Time : " + sb.toString());
		startTime = C3POTimeRange.parseDateToMillis(sb.toString());	// converting date & time to long
		sb.delete(0, sb.length());

		ts = conf.get("c3fe.endtime", "23:59:59");
		ts = C3POTimeRange.validateTimestamp(ts);
		sb.append(conf.get("c3fe.enddate", "")).append("\t").append(ts);
		System.err.println("INPUT: End Date & Time : " + sb.toString());
		endTime = C3POTimeRange.parseDateToMillis(sb.toString());
		sb.delete(0, sb.length());

		System.err.println("OUTPUT: Start Date & Time in milliseconds : " + startTime);
		System.err.println("OUTPUT: End Date & Time in milliseconds: " + endTime);
	}

	@Override
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

		String conceptLine = value.toString();

		if(conceptLine.length() > 0) {

			String[] tokens = conceptLine.split(";");
			String conceptId = tokens[3];
			
			/*
			 * Filter: From the input by user, check if the class/observation is valid.
			 * If not required skip.
			 * conceptDataMap- is a output of concept job. 
			 */
			if (conceptDataMap.containsKey(conceptId)) {

				if((startTime > 0 && endTime > 0)) {
					
					// Validate time range and convert into long format.
					String timeStamp = C3POTimeRange.validateTimestamp(tokens[5]);
					long millis = C3POTimeRange.parseDateToMillis(tokens[4] +"\t"+timeStamp);

					/*
					 * If the patient record time falls between the given time ranges, write it to the context.
					 */
					if((millis > 0) && (startTime <= millis && millis <= endTime)) {

						this.key.clear();
						this.value.clear();
						FEDataObjects feObject = new FEDataObjects((tokens[2]+"~"+tokens[3]), (String.valueOf(millis) +" "+tokens[6]));
						context.write(feObject, NullWritable.get());
					}
				} else {
					System.err.println("StartTime & EndTime are invalid parameters- ST: " + startTime + " ET: " + endTime);
				}
			}
		}
	}
}
