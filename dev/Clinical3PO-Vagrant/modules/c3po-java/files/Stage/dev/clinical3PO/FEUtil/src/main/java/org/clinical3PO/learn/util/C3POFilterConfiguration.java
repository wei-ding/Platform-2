package org.clinical3PO.learn.util;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * C3POFilterConfiguration
 * Represents the settings with which Wim's filtering program was run.
 * 
 * These consist, as of 10/18/13, of an optional start and end time
 * Where start time is inclusive and end time inclusive 
 * which I will represent with a C3POTimeRange
 * 
 * Plus a config file that mentions one per line the properties we care about
 * and optional [starttime endtime] for each (same kind of incl/excl range)
 * 
 * @author sean
 *
 */

public class C3POFilterConfiguration {
	
	C3POTimeRange globalTimeRange;
	
	//TODO: Default values for various things such as global time range.
	//note that some aren't final - so they'll be configurable and instantly reflected over
	//any objects of that type (within the same VM instance.)
	public static int defaultNumberOfHours = 48;
	
	//name for dummy property emitted by mapper when processing an uninteresting property
	public static final String dummyPropertyName = "*dummy*";
	
	/**
	 * propertiesUsed uses property names as the key and time range as the value.
	 * when deciding if we're interested in a given property/timestamp pair - 
	 * check to see if the property is among the keys (case-sensitive.) 
	 * if not, we don't want it.
	 * if so, check the time range.
	 *   if the time range is null, which it is by default, use the global time range
	 *   otherwise use the given time range.
	 * if the given timestamp is within whichever of those we use, then we're interested,
	 * otherwise not.
	 */
	TreeMap<String,C3POTimeRange> propertiesUsed;
	
	//and here they are in the order they were discovered so that we step through them in
	//the same order they were specified in the filter config, once it's time to generate the
	//arff vectors / headers etc.
	ArrayList<String> propertiesInOrder;
	
	
	
	public C3POTimeRange getGlobalTimeRange() {
		return globalTimeRange;
	}

	public void setGlobalTimeRange(C3POTimeRange globalTimeRange) {
		this.globalTimeRange = globalTimeRange;
	}
	
	public void setGlobalTimeRange(String sts, String ets) throws Exception {
		this.globalTimeRange = new C3POTimeRange(sts,ets);
	}

	public TreeMap<String, C3POTimeRange> getPropertiesUsed() {
		return propertiesUsed;
	}
	
	public ArrayList<String> getPropertiesInOrder() {
		return propertiesInOrder;
	}
	
	//returns null if there isn't one, caller should know to use global?
	public C3POTimeRange getTimeRangeForProperty(String prop) {
		if(!propertiesUsed.containsKey(prop)) return null;
		return propertiesUsed.get(prop);
	}

	//default ctor
	public C3POFilterConfiguration() {
		globalTimeRange = new C3POTimeRange();
		propertiesUsed = new TreeMap<String,C3POTimeRange>();
		propertiesInOrder = new ArrayList<String>();
	}
	
	/**
	 * usesProperty returns true if the given property is a key in propertiesUsed.
	 * if it isn't, we don't emit any features for it.
	 */
	
	public boolean usesProperty(String attr) {
		return (propertiesUsed!= null && propertiesUsed.containsKey(attr));
	}
	
	/**
	 * interesting returns true if the given property is in the list of properties we care about
	 * and within the relevant time range.
	 * @param attr
	 * @param timestamp
	 * @return
	 */
	public boolean interesting(String attr, String timestamp) throws Exception {
		//if the property isn't in our list, we don't want it
		if(!propertiesUsed.containsKey(attr)) {
			return false;
		} else {
			if(propertiesUsed.get(attr) == null) {
				//use global time range
				return globalTimeRange.inRange(timestamp);
			} else {
				//use property's own time range
				return propertiesUsed.get(attr).inRange(timestamp);
			}
		}
	}
	
	/**
	 * handleLine parses a single line from a configuration input stream.
	 * currently consists of:
	 * property plus optional [starttime endtime] (incl/excl range)
	 * @param theLine
	 * @return
	 * @throws Exception
	 */
	protected boolean handleLine(String theLineAsRead) throws Exception {
		//chuck leading/trailing ws
		String theLine = theLineAsRead.trim();
		String theProp = null;
		C3POTimeRange range = null;
		
		//so - to accommodate spaces-or-not bt [ and timestamp, timestamp and ] -
		//split on whitespace to separate off attr name
		String[] toks = theLine.split("\\s+",2);
		//presumably toks[0] is our property. yell if we already know about it? Let's say warn.
		//check to see that it's not blank -
		theProp = toks[0];
		//if there are further tokens, see if there's a time range
		if(toks.length > 1) {
			String[] rangetoks = toks[1].trim().split("[\\s\\[\\]]+");
			//debug
			//System.err.println("in line |" + theLine + "| Found these addl tokens:");
			//for(String tk:rangetoks) System.err.println("\"" + tk + "\"");
			
			//find out if there are exactly two usable timestamps
			//clumsy, but well, it's early and it's java
			String sts = null;
			String ets = null;
			
			for(String tk:rangetoks) {
				if(C3POTimeRange.isTimestamp(tk)) {
					if(sts ==  null) {
						//no start timestamp yet set, use this one
						sts = tk;
					} else if(ets == null) {
						//no end timestamp yet set, use this one
						ets = tk;
					} else {
						System.err.println("WARNING: extra timestamp found on line \"" + theLine + "\"");
					}
				}
			}
			
			if(sts != null && ets != null) {
				//yay found a range
				range = new C3POTimeRange(sts,ets);
				//debug System.err.println("-- found time range " + range);
			} else {
				System.err.println("WARNING: extra data (not apparent time range) in configuration line: \"" + theLine + "\" - assumed property \"" + theProp + "\"");
			}
		}

		//now we know the property and time range
		if(theProp.isEmpty() || theProp.matches("^\\s+$")) {
			throw new Exception("Badly-formed configuration line:\n" + theLineAsRead);
		}
		if(propertiesUsed.containsKey(theProp)) {
			System.err.println("WARNING: duplicate property given in configuration file: \"" + theProp + "\" - skipping");
		} else {
			//genuinely new property! add our new entry
			propertiesInOrder.add(theProp);
			propertiesUsed.put(theProp, range);
		}
		
		return true;
	}
	
	/**
	 * readPropertyConfiguration reads a configuration from an input stream
	 * that mentions one per line the properties we care about
	 * and optional [starttime endtime] for each (incl/excl range) 
	 * @param isr
	 * @return
	 * @throws Exception
	 */
	public boolean readPropertyConfiguration(InputStreamReader isr) throws Exception {
		LineNumberReader lnr = new LineNumberReader(isr);
		
		String theLine = lnr.readLine();
		
		while (theLine != null) {
			
			//ignore blank lines
			if(!theLine.isEmpty()) {
				if(!handleLine(theLine)) {
					//ERROR?
					//return null;
				}
			}
			
			theLine = lnr.readLine();
			if(theLine!=null) theLine = theLine.trim();
		}
		
		
		return true;
	}
	
	/** 
	 * test main - first argument is a config file
	 * @param args
	 */
	public static void main(String[] args) {
		
		if(args.length < 1) {
			System.err.println("Test usage: C3POFilterConfiguration (filter configuration file) [global time start global time end]");
			System.err.println("Where config file mentions one per line the properties we care about");
			System.err.println("and optional [starttime endtime] for each (incl/excl range)"); 
			System.err.println("Where global time start and global time end are (currently) hh:mm");
			System.exit(0);
		}
		
		
		C3POFilterConfiguration cf = new C3POFilterConfiguration();

		
		try {
			//do global time span, if any given
			if(args.length >= 3) {
				System.err.println("-- attempting to set global time range from \"" + args[1] + "\" to \"" + args[2] + "\"");
				cf.setGlobalTimeRange(args[1],args[2]);
			}

			if(!cf.readPropertyConfiguration(new InputStreamReader(new FileInputStream(args[0])))) {
				System.err.println("Failed to read filter config file \"" + args[0] + "\"");
			}
			
			//print it out
			System.out.println("Read configuration file " + args[0] + ":");
			for(String attr:cf.getPropertiesUsed().keySet()) {
				System.out.print(attr);
				if(cf.getPropertiesUsed().get(attr) != null) {
					//print out time range
					System.out.println(" " + cf.getPropertiesUsed().get(attr));
				} else {
					//blank, assume global time range
					System.out.println();
				}
				
			}
		} catch (Exception e) {
			System.err.println("Failed to read filter config file \"" + args[0] + "\"");
			e.printStackTrace();
		}
	}
	
}
