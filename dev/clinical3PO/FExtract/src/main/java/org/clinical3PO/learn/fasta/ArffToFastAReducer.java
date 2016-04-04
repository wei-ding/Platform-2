package org.clinical3PO.learn.fasta;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.clinical3PO.learn.util.FEConfiguration;
import org.clinical3PO.learn.util.FEEvaluatorBase;
import org.clinical3PO.learn.util.FEStrategyBase;

public class ArffToFastAReducer extends Reducer<Text, Text, Text, NullWritable>{

	private Text text = null;
	private String relation = null;
	private boolean pidFlag = false;
	private StringBuilder builder = null;
	private FEEvaluatorBase feBase_default = null;
	private FEEvaluatorBase feBase_classProperty = null;
	private MultipleOutputs<Text, NullWritable> mos = null;

	@Override
	public void setup(Context context) throws IOException {

		Configuration conf = context.getConfiguration();
		relation = conf.get("relation");		
		pidFlag = Boolean.parseBoolean(conf.get("patientIdBoolen"));
		builder = new StringBuilder();
		text = new Text();

		// method call to get objects required for descritization.
		try {
			parseConfigObjectToGetDescreteProperties(conf.get("configFileAsString"), conf.get("c3fe.classproperty"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		mos = new MultipleOutputs<Text, NullWritable>(context);
		System.err.println("------------------- END OF SETUP-------------------");
	}

	@Override
	public void reduce(Text key, Iterable<Text> values, Context context) 
			throws IOException, InterruptedException {

		String in_key = key.toString();
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
					builder.append(feBase_default.getDescreteValue(ch));
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
							builder.append(feBase_classProperty.getDescreteValue(ch));
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
	 * Method is used to parse the config object received from the driver.
	 * Getting 'FEEvaluatorBase' objects to extract descrete values out of them to perform descritization.
	 * 
	 * @param feConfigObjet
	 * @param classProperty
	 * @throws Exception 
	 * @throws IOException 
	 */
	private void parseConfigObjectToGetDescreteProperties(String configFileAsString, String classProperty) throws IOException, Exception {

		FEConfiguration feConfig = new FEConfiguration();
		InputStream is = new ByteArrayInputStream(configFileAsString.getBytes(Charset.forName("UTF-8")));

		if(feConfig.accumulateFromTextConfigFile(is, classProperty)) {
			System.err.println("--- feature ext. configuration read successfully!");
			System.err.println("--- size of Map: " +feConfig.getStrategies());
		} else {
			is.close();
			throw new Exception("ERROR: unable to read feature extraction config file");
		}
		
		is.close();
		
		// Since map contains 'class' as prefix for the class property, adding class using Builder.		 
		classProperty = new StringBuilder().append("class").append(classProperty).toString();
		System.err.println("CLASS PROPERTY " + classProperty);
		
		// Getting the Map with properties of FEconfig file.
		final TreeMap<String,ArrayList<FEStrategyBase>> mapOfConfiguration = feConfig.getStrategies();

		if(mapOfConfiguration.containsKey(classProperty)) {

			List<String> keyList = new ArrayList<String>(mapOfConfiguration.keySet());
			System.err.println("Keys from the Map: "+ keyList);
			
			ArrayList<FEStrategyBase> list = mapOfConfiguration.get(classProperty);
			if (list.size() == 1) {
				FEStrategyBase object = list.get(0);
				System.err.println("---------- class " +object);
				feBase_classProperty = object.getEvaluator();
				System.err.println("Class Object: " + feBase_classProperty);
			} else {
				System.err.println("+++++++++++ " + list);
			}

			// After this line, list is left with only one key.
			keyList.remove(classProperty);

			ArrayList<FEStrategyBase> defaultList = mapOfConfiguration.get(keyList.get(0));
			if (defaultList.size() == 1) {
				FEStrategyBase object = defaultList.get(0);
				System.err.println("---------- default " +object);
				feBase_default = object.getEvaluator();
				System.err.println("Default Object: " + feBase_classProperty);
			} else {
				System.err.println("+++++++++++ " + list);
			}
		} else {
			System.err.println("Map from FEConfiguration doesn't have the key of class property. I.e."+classProperty);
		}
	}
}