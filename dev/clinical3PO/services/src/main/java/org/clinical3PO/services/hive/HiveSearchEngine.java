package org.clinical3PO.services.hive;

import org.clinical3PO.services.JobSearchService;
import org.clinical3PO.services.PatientSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;

public class HiveSearchEngine {

	private static final Logger logger = LoggerFactory.getLogger(HiveSearchEngine.class);

	public static void main(String[] args) {

		int argsLength = args.length;
		PatientSearchService searchService = null;

		if(argsLength == 3 || argsLength == 4) {

			ClassPathXmlApplicationContext ctx = null;

			try {
				ctx = new ClassPathXmlApplicationContext(new String[] {
						"classpath*:clinical3PO-services-dao.xml", "classpath*:clinical3PO-services.xml" });

				JobSearchService jobSearchService = (JobSearchService) ctx.getBean("jobSearchService");

				assert (jobSearchService != null);
				searchService = jobSearchService.getPatientSearchService();
			} catch (BeansException e) {
				logger.error("", e);
			} finally {
				if (ctx != null)
					ctx.close();
			}

			try {

				if(argsLength == 4) {

					// Params: outputDir, outputFile, personId, observation
					searchService.getObservationSearchOnHiveQL(args[0], args[1], args[2], args[3]);
				} else if(args[2].contains("~") || args[2].contains("#")) {

					// Params: outputDir, outputFile, 'observation~personId#observation~personId'
					searchService.getBatchSearchOnHiveQL(args[0], args[1], args[2]);
				} else {

					// Params: outputDir, outputFile, personId
					searchService.getPatientSearchOnHiveQL(args[0], args[1], args[2]);
				}
			} catch(DataAccessException ae) {
				logger.error("", ae);
			}
		} else {

			logger.error("Usage: HIVE SEARCH <output path> <output file name> <arguments> ");
			System.exit(1);
		}
	}
}