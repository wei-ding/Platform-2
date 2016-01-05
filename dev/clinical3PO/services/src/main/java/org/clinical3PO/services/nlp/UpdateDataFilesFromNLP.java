package org.clinical3PO.services.nlp;

import java.io.File;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.clinical3PO.services.JobSearchService;
import org.clinical3PO.services.PatientSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class UpdateDataFilesFromNLP {

	private static final Logger logger = LoggerFactory.getLogger(UpdateDataFilesFromNLP.class);

	public static void main(String[] args) {

		if (args.length == 1) {

			UpdateDataFilesFromNLP xmlFileProcessorIntoHive = new UpdateDataFilesFromNLP();
			PatientSearchService beans = xmlFileProcessorIntoHive.getBeans();
			xmlFileProcessorIntoHive.processXmlData(args[0], beans);
		}
	}

	/**
	 * Instantiating xml files for beans Initialization.
	 * 
	 * @return PatientSearchService bean instance
	 */
	private PatientSearchService getBeans() {

		ClassPathXmlApplicationContext ctx = null;
		PatientSearchService searchService = null;

		try {
			ctx = new ClassPathXmlApplicationContext(new String[] {
					"classpath*:clinical3PO-services-dao.xml", "classpath*:clinical3PO-services.xml" });

			JobSearchService jobSearchService = (JobSearchService) ctx.getBean("jobSearchService");

			assert (jobSearchService != null);
			searchService = jobSearchService.getPatientSearchService();
		} catch (BeansException e) {
			logger.error("Error in reading services xml file", e);
			System.exit(0);
		} finally {
			if (ctx != null)
				ctx.close();
		}
		return searchService;
	}

	/**
	 * 
	 * @param pathOfXmlFileProducedByNlp
	 * @param searchService
	 */
	private void processXmlData(String pathOfXmlFileProducedByNlp, PatientSearchService searchService) {

		try {
			logger.info("ARGUMENT: XML FILE PATH: "+ pathOfXmlFileProducedByNlp);
			File xml = new File(pathOfXmlFileProducedByNlp);
			if(!xml.exists()) {
				logger.error("xml file is not located in specified path: " +pathOfXmlFileProducedByNlp);
				System.exit(0);
			}
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			factory.setNamespaceAware(true);
			factory.setFeature("http://xml.org/sax/features/namespaces", false);
			factory.setFeature("http://xml.org/sax/features/validation", false);
			factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			DocumentBuilder dBuilder = factory.newDocumentBuilder();
			Document doc = dBuilder.parse(xml);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("document");

			/*
			 * METHOD CALL
			 * 
			 * Getting concept and observation tables row count.
			 * This helps in inserting the data by adding 1 to the existing count.
			 */
			int concept_row_count = searchService.getConceptIdCount();
			int observation_row_count = searchService.getObservationIdCount();

			int length = nList.getLength();
			logger.info("Parsing and extracting values from the XML file. Headers size is: "+length+", which means will iterate for "+length+" times" );
			for (int temp = 0; temp < length; temp++) {

				Node nNode = nList.item(temp);
				logger.info("Iteration- " +temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;
					String patientId = eElement.getElementsByTagName("infon").item(1).getTextContent();
					logger.debug("PatientId " + patientId);

					String dateTime = eElement.getElementsByTagName("infon").item(2).getTextContent();
					logger.debug(" dateTime " + dateTime);

					StringTokenizer tokens = new StringTokenizer(dateTime, "T");
					final String date = tokens.nextToken();
					final String time = tokens.nextToken();

					String value = eElement.getElementsByTagName("infon").item(3).getTextContent();
					String type = eElement.getElementsByTagName("infon").item(4).getTextContent();

					/*
					 * METHOD CALL
					 * 
					 * Check for, if the property is available in concept table.
					 */
					boolean flag = searchService.getPropertyNameAvailable(type);

					/*
					 * The code inside condition is executed only if the flag=false.
					 * I.e. property is not available in concept table.  
					 */
					if(!flag) {

						logger.info("The property "+type + " is not available in Hive Concept Table. Proceeding to insert.");
						concept_row_count = concept_row_count + temp + 1;

						/*
						 * METHOD CALL
						 * 
						 * Based on the count, insert/update concept table with available properties. 
						 */
						searchService.insertIntoHiveConcept(concept_row_count, type);
					} else {
						logger.info("The property "+type + " is available in Hive Concept Table. No insertion's into Concept table." );
					}

					observation_row_count = observation_row_count + temp + 1;
					searchService.insertIntoHiveObservation(observation_row_count, patientId, concept_row_count, date, time, value);
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}
}