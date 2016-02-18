package org.clinical3PO.learn.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class C3POTimeRange {
	
	//Where start time is inclusive and end time inclusive 
	long startTime;
	long endTime;
	
	

	public long getStartTime() {
		return startTime;
	}
	
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	//by default, time range covers any conceivable time (except long.MAX_VALUE, since endTime is exclusive.)
	public C3POTimeRange() {
		startTime = java.lang.Long.MIN_VALUE;
		endTime = java.lang.Long.MAX_VALUE;
	}
	
	public C3POTimeRange(long s, long e) {
		startTime = s;
		endTime = e; 
	}
	
	//where start and end are given as timestamps
	public C3POTimeRange(String sts, String ets) throws Exception {
		startTime = interpretTimeStamp(sts);
		endTime = interpretTimeStamp(ets);
	}
	
	/**
	 * checks to see if given string is legit timestamp; currently hh:mm is all we allow
	 * Now allowing hh:mm:ss
	 * @param ts
	 * @return
	 * @throws Exception
	 */
	public static boolean isTimestamp(String ts) throws Exception {
		return (ts.matches("[0-9]+:[0-5][0-9]") || ts.matches("[0-9]+:[0-5][0-9]:[0-5][0-9]"));
	}
	
	public static String validateTimestamp(String ts) {
		if(ts.matches("[0-9]+:[0-5][0-9]")) {
			ts =  ts.concat(":00");
		} else if(ts.matches("[0-9]+:[0-5][0-9]:[0-5][0-9]")) {
			return ts;
		}
		return ts;
	}
	
	/*
	 * Time format may change so encapsulate; why not represent as a long such
	 * that <=> comparison makes sense, whatever the units are (let's say seconds for now.)
	 * 
	 * ORIGINALLY ASSUMING hr:min where hour can be any number and mins must be 00-59
	 * and seconds are 00
	 * Now allowing for 3-field with seconds 
	 * 
	 * returns -1 on error (or throws exception)
	 */
	public static long interpretTimeStamp(String ts) throws Exception {

		//sanity check timestamp
		if(ts == null || ts.isEmpty()) {
			throw(new Exception("null or empty timestamp"));
		}
		else if(!isTimestamp(ts)) {
			throw(new Exception("Improperly formatted timestamp \"" + ts + "\""));
		}
		
		String[] tstoks = ts.split(":");		//format is hour:minute[:second], so split on colon
		long hour = Long.valueOf(tstoks[0]).longValue();
		long min = Long.valueOf(tstoks[1]).longValue();
		//if there is a third value, it's seconds; else assume 0
		long sec = 0;							
		if(tstoks.length > 2) {
			sec = Long.valueOf(tstoks[2]).longValue();
		}
		
		return (hour * 3600) + (min * 60) + sec;
	}
	
	//then the function that really matters: is a given value in range?
	public boolean inRange(long t) {
		return (t >= startTime && t < endTime);
	}
	
	public boolean inRange(String ts) throws Exception {
		return inRange(interpretTimeStamp(ts));
	}
	
	//utilities for converting back to h:m:s
	public static long getHourFromTime(long t) {
		return t/3600;
	}
	
	public static long getMinuteFromTime(long t) {
		long sh = t / 3600;
		return (t - (sh*3600)) / 60;
	}
	
	public static long getSecondFromTime(long t) {
		return t % 60;
	}
	
	public static String timeToTimestamp(long t) {
		String str;
		if(t == java.lang.Long.MIN_VALUE) {
			str = "-infinity";
		} else if(t == java.lang.Long.MAX_VALUE) {
			str = "+infinity";
		} else {
			long sh = getHourFromTime(t);
			long sm = getMinuteFromTime(t);
			long ss = getSecondFromTime(t);
			str = String.format("%02d:%02d:%02d", sh,sm,ss);
		}		
		
		return str;
	}
	
	public static long parseDateToMillis(String dateString)  {

		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy\tHH:mm:ss");
		Date date = null;
		try {
			date = sdf.parse(dateString);
			return date.getTime();
		} catch (ParseException e) {
			System.err.print("Unparsable Date: " + dateString);
			System.exit(1);
		}
		return 0;
	}
	
	public String toString() {
		
		String startstring, endstring;
		
		//use -infinity as start time if start time is min long
		if(startTime == java.lang.Long.MIN_VALUE) {
			startstring = "-infinity";
		} else {
			long sh = getHourFromTime(startTime);
			long sm = getMinuteFromTime(startTime);
			long ss = getSecondFromTime(startTime);
			startstring = String.format("%02d:%02d:%02d", sh,sm,ss);
		}		
		
		//use +infinity as end time if end time is max long
		if(endTime == java.lang.Long.MAX_VALUE) {
			endstring = "+infinity";
		} else {
			long eh = getHourFromTime(endTime);
			long em = getMinuteFromTime(endTime);
			long es = getSecondFromTime(endTime);
			endstring = String.format("%02d:%02d:%02d", eh,em,es);
		}		
		
		return "[" + startstring + " " + endstring + ")";
	}

}
