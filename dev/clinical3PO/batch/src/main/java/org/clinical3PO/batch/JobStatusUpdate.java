package org.clinical3PO.batch;

import java.io.File;
import java.sql.Timestamp;
import java.util.Date;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.clinical3PO.services.JobSearchService;
import org.clinical3PO.services.constants.JobStatus;
import org.clinical3PO.services.dao.model.JobSearch;

public class JobStatusUpdate {

	public static void main(String args[]) {

		if (args.length != 2) {
			System.err.println("Usage: JobStatusUpdate <job id> <job status>");
			System.exit(-1);
		}

		int jobId = Integer.parseInt(args[0]);
		String jobStatus = args[1];

		ClassPathXmlApplicationContext ctx = null;

		try {
			ctx = new ClassPathXmlApplicationContext(new String[] {
					"classpath*:clinical3PO-services-dao.xml",
					"classpath*:clinical3PO-services.xml" });

			JobSearchService jobSearchService = (JobSearchService) ctx
					.getBean("jobSearchService");
			
			assert (jobSearchService != null);

			JobSearch jobSearch = jobSearchService.getJob(jobId);
						
			assert (jobSearch != null);

			if (jobStatus.matches("SUCCESS")) {

				String outputFileName = jobSearch.getOutputDirectory()
						+ File.separator + jobSearch.getOutputFileName();
				File outputFile = new File(outputFileName);

				jobSearch.setStatus(JobStatus.FINISHED.getJobStatus());
				jobSearch.setSearchEndTime(new Date(outputFile.lastModified()));

				jobSearchService.updateJob(jobSearch);

			} else if (jobStatus.matches("FAIL")) {

				jobSearch.setStatus(JobStatus.FAILED.getJobStatus());
				jobSearch.setModifiedDate(new Timestamp(new Date().getTime()));

				jobSearchService.updateJob(jobSearch);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

}
