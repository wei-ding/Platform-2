	package org.clinical3PO.arff;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.clinical3PO.environment.AppUtil;
import org.clinical3PO.environment.EnvironmentType;
import org.clinical3PO.services.json.TimeSeries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;


public class ArffGeneration {

	private static final Logger logger = LoggerFactory.getLogger(ArffGeneration.class);
	private static String envType = AppUtil.getProperty("environment.type");

	public void generateArff (String[] args){
		
		String hadoopOutputFile = args[0];
		
		String inputParameters = args[1];
		
		
		// Check for the directories and create if not existing
		
		File dataDir = new File(args[2]+File.separator+"data");
		
		if (!dataDir.exists())
			dataDir.mkdirs();
		
		File experimentDirectory = new File(args[2]+File.separator+"experiments");
		
		if (!experimentDirectory.exists())
			experimentDirectory.mkdirs();
		
		// Create output directory, where the output from mlflex is copied (in shell script)
		
		File outputDirectory = new File(args[2]+File.separator+"output");
		
		if (!outputDirectory.exists())
			outputDirectory.mkdirs();
		
		String outputARFFFile = dataDir.getAbsolutePath()+File.separator+args[3]+".arff";
		String outputExperimentFile = experimentDirectory.getAbsolutePath()+File.separator+args[3]+".txt";
		
		
		String classificationAlgorithm = args[4];
		String folds=args[5];
		String iterations=args[6];

		Map<String,String> bins = new HashMap<String,String>();
		List<String> patientList = new ArrayList<String>();

		Map<String,String> allPatients = new HashMap<String,String>();
		Map<String,String> patientObservationValue = new HashMap<String,String>();

		FastVector c3poAttributes = new FastVector();
		FastVector classValues =new FastVector();
		Instances c3poData;
		
		Map<String,List<TimeSeries>> timeValue = new HashMap<String,List<TimeSeries>>(); 

		double[] c3poVals;

		patientList.addAll(Arrays.asList(inputParameters.split("~")[1].split(",")));	//patients

		String[] records = inputParameters.split("#");
		for (String record : records){
			String[] parts = record.split("~");
			bins.put(parts[0],parts[2]);				
		}

		for (String patient : patientList){
			allPatients.put(patient, null);
		}

		for (String bin : bins.keySet()){
			for(int j=0; j<Integer.parseInt(bins.get(bin)); j++){
				int tmp = j+1;
				c3poAttributes.addElement(new Attribute(bin+tmp));
			}			
		}

		classValues.addElement("Death");
		classValues.addElement("Alive");
		c3poAttributes.addElement(new Attribute("class",classValues));			

		// Create instances object
		c3poData = new Instances("clinical3PO", c3poAttributes, 0);

		BufferedReader reader=null;

		try {
			reader = new BufferedReader(new FileReader(hadoopOutputFile));
			String line;
			long startTime = new SimpleDateFormat("MM/dd/yy;HH:mm").parse("01/01/14;00:00").getTime();         // Starting time = 01/01/14;00:00
			
			while ((line=reader.readLine())!=null){
				String key, outcome;
				StringBuffer value;
				key = line.split("\t")[0];
				value = new StringBuffer(line.split(";")[2]);
				outcome = line.split(";")[5];					// Uncomment this when the hadoop output file contains the outcome
				
				List<TimeSeries> timeValueList = new ArrayList<TimeSeries>();
				
				String dateAndTime = (line.split("\\s+")[1]).split(";")[0]+";"+line.split(";")[1];
				long currentTime = new SimpleDateFormat("MM/dd/yy;HH:mm").parse(dateAndTime).getTime();
				float diffTimeInHours = (float) (currentTime-startTime)/(1000*60*60);
				String timeInHrs = Float.toString(diffTimeInHours);	
				
				if (patientObservationValue.containsKey(key)){
					value.append(",").append(patientObservationValue.get(key));
					patientObservationValue.put(key,value.toString());
				}
				else {
					patientObservationValue.put(key,value.toString());
				}
				
				TimeSeries individualTimeSeries = new TimeSeries();
				individualTimeSeries.setTime(timeInHrs);
				individualTimeSeries.setValue(value.toString());
				
				if (timeValue.containsKey(key)){
					timeValueList = timeValue.get(key);
					timeValueList.add(individualTimeSeries);
				}
				else {
					timeValueList.add(individualTimeSeries);					
				}
				
				timeValue.put(key, timeValueList);
				
				String pid = key.split("~")[1];
				allPatients.put(pid,outcome);

			}

			// 				Create Instances
			
			logger.info("Number of Instances = " +allPatients.size());
			
			int count=0;
			for (String patient : allPatients.keySet()){
				c3poVals = new double[c3poData.numAttributes()];
				count = 0;
				for (String bin : bins.keySet()){
					for(int j=0; j<Integer.parseInt(bins.get(bin)); j++){
						double tmp=0;							
						String key;
						String[] value;
						key = bin+"~"+patient;
						if (patientObservationValue.get(key)!=null) {
							value = patientObservationValue.get(key).split(",");
							for (String eachValue : value){
								tmp+=Double.parseDouble(eachValue);
							}
							c3poVals[count]=tmp/value.length;
						}
						else {
							c3poVals[count] = -1;
						}
						count++;
					}			
				}
				
				c3poVals[count]=classValues.indexOf(allPatients.get(patient));
				c3poData.add(new Instance(1.0, c3poVals));
				
			}
			
			logger.info("Number of Independent Attributes = " +count);
			
		} catch (FileNotFoundException e) {			
			logger.error(e.toString()); 
		} catch (IOException e) {
			logger.error(e.toString());
		} catch (ParseException e) {
			logger.error(e.toString());
		} finally {
			if(reader!=null)
				try {
					reader.close();
				} catch (IOException e) {
					logger.error(e.toString());
				}
		}
		
		// Write experiment file
		StringBuilder experimentFile = new StringBuilder();
		experimentFile.append("DATA_PROCESSORS=mlflex.dataprocessors.ArffDataProcessor(\"").append(outputARFFFile).append("\")");
		experimentFile.append("\nCLASSIFICATION_ALGORITHMS=").append(classificationAlgorithm).append("\nNUM_OUTER_CROSS_VALIDATION_FOLDS=").append(folds)
		.append("\nNUM_ITERATIONS=").append(iterations);
		
		
		// Create output file even if no data is fetched
		BufferedWriter bwARFF = null;
		BufferedWriter bwExperiment = null;
		
		try {
			bwARFF = new BufferedWriter(new FileWriter(outputARFFFile)); // Open output file
			bwARFF.write(c3poData.toString());
			bwARFF.flush();
			
			bwExperiment = new BufferedWriter(new FileWriter(outputExperimentFile));
			bwExperiment.write(experimentFile.toString());
			bwExperiment.flush();
			
		} catch(IOException e) {
			logger.error(e.toString());
			return;
		} finally {
			try {
				bwARFF.close();
				bwExperiment.close();
			} catch(IOException e) {
				logger.info("Exception :"+e);
				logger.error(e.toString());
				return;
			}
		}
		return;
	}
	
	public static void main(String[] args){

		if (args.length!=7){
			System.out.println("Usage: <hadoop output file> <input parameters> <desired arff file path> <arff file name> <classification algorithm> <folds> <iterations>");			
			System.exit(-1);
		} else {
			if(envType !=null && EnvironmentType.DEVELOPMENT == EnvironmentType.valueOf(envType)) {
				logger.info ("Input Param : " + args);
			}
		}
		BasicConfigurator.configure();
		ArffGeneration arffGeneration = new ArffGeneration();
		arffGeneration.generateArff(args);
	}

}
