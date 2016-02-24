package org.clinical3PO.fe;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExperimentFileGeneration {

	private static final Logger logger = LoggerFactory.getLogger(ExperimentFileGeneration.class);

	private void generateArff (String[] args) throws IOException{

		String clinicalAppData_directory = args[0]; 
		String fileName = args[1];	//both arff, experiment file names are same.
		String classificationAlgorithm = args[2];
		String folds=args[3];
		String iterations=args[4];

		File experimentDirectory = new File(clinicalAppData_directory+File.separator+"experiments");
		if (!experimentDirectory.exists()) {
			experimentDirectory.mkdirs();
		}

		// Create output directory, where the output from mlflex is copied (in shell script)
		File outputDirectory = new File(clinicalAppData_directory+File.separator+"output");
		if (!outputDirectory.exists()) {
			outputDirectory.mkdirs();
		}		

		String outputARFFFile = clinicalAppData_directory+File.separator+"data"+File.separator+fileName+".arff";
		String outputExperimentFile = experimentDirectory.getAbsolutePath()+File.separator+fileName+".txt";

		// Write experiment file
		StringBuilder experimentFile = new StringBuilder();
		experimentFile.append("DATA_PROCESSORS=mlflex.dataprocessors.ArffDataProcessor(\"")
		.append(outputARFFFile).append("\")");
		experimentFile.append("\nCLASSIFICATION_ALGORITHMS=").append(classificationAlgorithm)
		.append("\nNUM_OUTER_CROSS_VALIDATION_FOLDS=").append(folds)
		.append("\nNUM_ITERATIONS=").append(iterations);

		// Create output file even if no data is fetched
		BufferedWriter bwExperiment = null;

		try {
			bwExperiment = new BufferedWriter(new FileWriter(outputExperimentFile));
			bwExperiment.write(experimentFile.toString());
			bwExperiment.flush();
		} catch(IOException e) {
			logger.error(e.toString());
			return;
		} finally {
			if(bwExperiment != null)
				bwExperiment.close();
		}
	}

	public static void main(String[] args){

		if (args.length != 5){
			System.out.println("Usage: <hadoop output file> <input parameters> <desired arff file path> <arff file name> <classification algorithm> <folds> <iterations>");			
			System.exit(-1);
		}
		BasicConfigurator.configure();

		ExperimentFileGeneration expGeneration = new ExperimentFileGeneration();
		try {
			expGeneration.generateArff(args);
		} catch(IOException ie) {
			logger.error(ie.toString());
		}
	}
}