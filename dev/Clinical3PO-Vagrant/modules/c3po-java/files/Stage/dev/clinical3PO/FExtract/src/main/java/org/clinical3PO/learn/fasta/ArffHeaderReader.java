package org.clinical3PO.learn.fasta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This class is a util class.
 * Class is used to load Arff header and parse the properties like: 
 * 		1. number of bins
 * 		2. total list of attributes available in arff
 * 		3. type of class attribute
 * 
 * @author 3129891
 *
 */
public class ArffHeaderReader {

	private int bins = 0;
	private String relation = null;
	private boolean pidFlag = false;
	private Set<String> attributeSet = null;

	protected ArffHeaderReader() {
		attributeSet = new LinkedHashSet<String>();
	}

	/*
	 * This is for testing.
	 */
	public static void main(String[] args) throws IOException {

		ArffHeaderReader reader = new ArffHeaderReader();
		File initialFile = new File("D:\\Srikanth\\workspace\\projects\\MUSC-C3PO\\Clinical3PO\\Stage\\dev\\clinical3PO\\FExtract\\src\\test\\resources\\output1.arff");
		InputStream fsds = new FileInputStream(initialFile); 
		reader.readParseArffHeaders(fsds);
	}

	/**
	 * Reading(from hdfs) Arff file header to get count of unique number of attributes and relation type.
	 * While there's an information available(separated by '_') in each attribute, split accordingly and 
	 * 	store the in linked list.   
	 * @param fsds
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	protected void readParseArffHeaders(InputStream fsds) throws NumberFormatException, IOException{

		// Initializing reader to read arff file.
		BufferedReader br = new BufferedReader(new InputStreamReader(fsds));
		String arffHeader = null;
		StringBuilder sb = null;
		String bins = null;
		try {

			sb = new StringBuilder();
			arffHeader = br.readLine();	//line-by-line

			// Loop over the line for not null && not empty
			while(arffHeader != null) {

				if(!arffHeader.isEmpty()) {

					/*
					 * There are 3 string in each @attribute line.
					 * 1) @attribute itself
					 * 2) unique attribute string
					 * 3) type of unique attribute
					 * NOTE: we are not using 3rd string/value (type of unique attribute)
					 */
					String[] inputLine= arffHeader.split("\\s+");

					// very 1st line of arff file.
					if(inputLine[0].equals("@relation")) {
						relation = inputLine[1];
					} else if(inputLine[0].equals("@attribute")) {

						/*
						 * All the @attribute String values contain information(separated by '_') expect PatientID & class attributes.
						 * if helps to avoid PartientID and class attributes from insertion into set.
						 * 
						 * Only insert the unique name of the attribute instead of full name.
						 * REASON:
						 * n-unique attributes = n-fasta files with all patients of unique attributes.
						 * Program to process above statement, have to remember the order of attributes in arff header.
						 * Using LinkedSet to maintain the order. 
						 */
						String[] attribute = inputLine[1].split("_");
						if(attribute.length == 1 && attribute[0].equalsIgnoreCase("PatientID")) {
							pidFlag = true;
						} else if(!attribute[0].equals("class")) {

							sb.append(attribute[0]).append("_").append(attribute[1]).append("_").append(attribute[2]);
							if(!attributeSet.contains(sb.toString())) {
								attributeSet.add(sb.toString());
							}
							bins = attribute[5];	// attribute[5] contains keyword 'bin' with integer value of bin count. Example: bin20
						}
						sb.delete(0, sb.length());
					} else if(inputLine[0].equals("@data")) {	// hereafter arff data starts and header ends, so set 'conf' and break.
						break;
					} else {
						continue;
					}
				}
				arffHeader = br.readLine();		//read next line
			}
		} catch(IOException e) {
			e.printStackTrace();
		} finally {

			// Bins start from 0. So add 1 to make it exact number.
			this.bins = Integer.parseInt(bins.substring(3)) + 1;
			br.close();
		}
	}

	public String getRelation() {
		return relation;
	}

	public boolean isPidFlag() {
		return pidFlag;
	}

	public Set<String> getAttributeSet() {
		return attributeSet;
	}

	public int getBins(){
		return bins;
	}
}
