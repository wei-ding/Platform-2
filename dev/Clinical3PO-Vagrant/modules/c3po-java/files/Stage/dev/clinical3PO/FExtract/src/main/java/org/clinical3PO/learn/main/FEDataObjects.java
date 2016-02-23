package org.clinical3PO.learn.main;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableUtils;

public class FEDataObjects implements Writable, WritableComparable<FEDataObjects> {

	private String key = null;	// Combination of PatientID, ConceptID with ~ separator 
	private String value = null;	// Combination of date, time, value with \t as separator

	protected FEDataObjects() {	
	}
	
	protected FEDataObjects(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public void readFields(DataInput dataInput) throws IOException {
		key = WritableUtils.readString(dataInput);
		value = WritableUtils.readString(dataInput);
	}

	public void write(DataOutput dataOutput) throws IOException {
		WritableUtils.writeString(dataOutput, key);
		WritableUtils.writeString(dataOutput, value);
	}

	@Override
	public int compareTo(FEDataObjects o) {
		
		int result = key.compareTo(o.key);
		if(0 == result) {
			result = value.compareTo(o.value);
		}
		return result;
	}
	
	@Override
	public String toString() {
		
		String[] temp = value.split("\\s+");
		String dateAsString = parseMillisToDate(Long.valueOf(temp[0]));
		return new StringBuffer().append(key).append("\t").append(dateAsString).append("\t").append(temp[1]).toString();
	}
	
	private String parseMillisToDate(long millis)  {

		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy\tHH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		return (sdf.format(calendar.getTime())); 
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}
}
