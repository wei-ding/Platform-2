package accumulo.mapreduce.Drivers;

import java.io.IOException;

import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.ColumnVisibility;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

public class ObservationMapperMainDao extends Mapper<LongWritable, Text, Text, Text> {

	private long numRecords = 0;
	private ObservationDaoObject dao = null;
	private String[] columnQualifiers = null;
	final static Logger logger = Logger.getLogger(ObservationDriverMain.class);

	public ObservationMapperMainDao() {	

		String line1 = "OBSERVATION;observation_id;person_id;observation_concept_id;observation_date;observation_time;"
				+ "value_as_number;value_as_string;value_as_concept_id;unit_concept_id;range_low;range_high;"
				+ "observation_type_concept_id;associated_provider_id;visit_occurrence_id;relevant_condition_concept_id;"
				+ "observation_source_value;units_source_value";
		columnQualifiers = line1.split(";");
	}

	@Override
	public void setup(Context context) throws IOException, InterruptedException {

		final String inputFile = context.getConfiguration().get("inputFile");
		final String accumuloTableName = context.getConfiguration().get("accumuloTableName");
		final String accumuloInstance = context.getConfiguration().get("accumuloInstance");
		final String zooKeepers = context.getConfiguration().get("zooKeepers");
		final String accumuloUser = context.getConfiguration().get("accumuloUser");
		final String accumuloPassword = context.getConfiguration().get("accumuloPassword");
		final String accumuloTableName1 = context.getConfiguration().get("accumuloTableName1");
		final String accumuloTableName2 = context.getConfiguration().get("accumuloTableName2");

		String[] args = {inputFile, accumuloTableName, accumuloInstance, zooKeepers, accumuloUser, accumuloPassword,
				accumuloTableName1, accumuloTableName2};
		dao = ObservationDaoObject.getInstance();
		dao.init(args);
		logger.info("IN SETUP METHOD.");
		logger.info(Thread.currentThread());
	}

	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

		try {
			process(value.toString());
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Override
	public void cleanup(Context context) {

		try {
			dao.getBw().flush();
			dao.getBw1().flush();
			dao.getBw2().flush();
			dao.getBw().close();
			dao.getBw1().close();
			dao.getBw2().close();
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private void process(String line1) throws Exception {

		int i = 0, j = 0;
		String rowKey = null;
		String rowPersonId = null;
		String rowObservationId = null;
		String rowCQ = null;
		String[] words = null;
		String[] docSec = {"Doc1", "Doc2", "Doc3", "Doc4", "Doc5"};
		ColumnVisibility colVis = null;

		words = line1.split(";");

		if (!words[1].trim().matches("observation_id")) { // Skipping 1st line

			rowKey = words[1]; // Second field is key
			rowPersonId = words[2]; // Third field is person id
			rowObservationId = words[3]; // Fourth field is observation id
			i = 0;
			j = (Integer.parseInt(rowPersonId) % docSec.length); // Map to a doctor security value based on person id
			colVis = new ColumnVisibility(docSec[j]);

			for (String word : words) {

				if (i == 0 || i == 1) // Skipping the first 2 fields
					; // No processing
				else {
					rowCQ = columnQualifiers[i];

					try {
						// Create new mutation and add rowID, colFam, colQual, value
						Mutation mutation = new Mutation(new Text(rowKey));
						mutation.put(new Text("CF1"), new Text(rowCQ), colVis, new Value(word.getBytes()));

						// Add the mutation to the batch writer
						dao.getBw().addMutation(mutation);
						if(logger.isDebugEnabled()) {
							logger.debug("Added mutation to tab1, rowKey : " + rowKey);
						}
					} catch (Exception e) {
						logger.error(e);
					}
				}
				i++;
			}

			try {
				// Create new mutation and add rowID, colFam, colQual, value
				Mutation mutation1 = new Mutation(new Text(rowPersonId));
				mutation1.put(new Text("CF1"), new Text(rowKey), colVis, new Value(rowKey.getBytes()));

				// Add the mutation to the batch writer
				dao.getBw1().addMutation(mutation1);
				if(logger.isDebugEnabled()) {
					logger.debug("Added mutation to tab2, rowKey : " + rowPersonId);
				}
			} catch (Exception e) {
				logger.error(e);
			}

			try {
				// Create new mutation and add rowID, colFam, colQual, value
				Mutation mutation2 = new Mutation(new Text(rowPersonId + "~" + rowObservationId));
				mutation2.put(new Text("CF1"), new Text(rowKey), colVis, new Value(rowKey.getBytes()));

				// Add the mutation to the batch writer
				dao.getBw2().addMutation(mutation2);
				if(logger.isDebugEnabled()) {
					logger.debug("Added mutation to tab2, rowKey : " + rowPersonId + "~" + rowObservationId);
				}
			} catch (Exception e) {
				logger.error(e);
			}

			numRecords++;
			if ((numRecords > 5000) && (numRecords%5000 == 0)) { // Commit every 10000 records
				try {
					// Flush the batch writer
					dao.getBw().flush();
					dao.getBw1().flush();
					dao.getBw2().flush();
					if(logger.isDebugEnabled()) {
						logger.debug("Flushed tab1");
						logger.debug("Flushed tab2");
						logger.debug("Flushed tab3");
					}
				} catch (Exception e) {
					logger.error(e);
				}
			}
		}
	}
}