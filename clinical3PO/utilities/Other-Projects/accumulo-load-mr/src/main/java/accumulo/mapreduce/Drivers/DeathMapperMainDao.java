package accumulo.mapreduce.Drivers;

import java.io.IOException;

import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.ColumnVisibility;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

public class DeathMapperMainDao extends Mapper<LongWritable, Text, Text, Text> {

	private long numRecords = 0;
	private String[] columnQualifiers = null;
	private DeathDaoObject obj = null;
	final static Logger logger = Logger.getLogger(DeathMapperMainDao.class);

	public DeathMapperMainDao() {

		String line1 = "DEATH	person_id	death_date	death_type_concept_id	cause_of_death_concept_id	cause_of_death_source_value$";
		columnQualifiers = line1.split("\\t");
	}

	@Override
	public void setup(Context context) throws IOException, InterruptedException {

		final String inputFile = context.getConfiguration().get("inputFile");
		final String accumuloTableName = context.getConfiguration().get("accumuloTableName");
		final String accumuloInstance = context.getConfiguration().get("accumuloInstance");
		final String zooKeepers = context.getConfiguration().get("zooKeepers");
		final String accumuloUser = context.getConfiguration().get("accumuloUser");
		final String accumuloPassword = context.getConfiguration().get("accumuloPassword");

		String[] args = {inputFile, accumuloTableName, accumuloInstance, zooKeepers, accumuloUser, accumuloPassword};
		obj = DeathDaoObject.getInstance();
		obj.init(args);
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
			logger.info("CLOSING CONNECTIONS : " + obj.getBw());
			obj.getBw().close();
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private void process(String line1) {

		BatchWriter bw = obj.getBw();
		int i = 0, j = 0;
		String rowKey = null;
		String rowCQ = null;
		String[] words = null;
		String[] docSec = {"Doc1", "Doc2", "Doc3", "Doc4", "Doc5"};
		ColumnVisibility colVis = null;

		words = line1.split("\\t");

		if (!words[1].trim().matches("person_id")) { // Skipping 1st line

			rowKey = words[1]; // Second field is key
			i = 0;
			j = (Integer.parseInt(rowKey) % docSec.length); // Map to a doctor security value based on person id
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
						bw.addMutation(mutation);
						if(logger.isDebugEnabled()) {
							logger.debug("Added mutation to tab1, rowKey : " + rowKey);
						}
					} catch (Exception e) {
						logger.error(e);
					}
				}
				i++;
			}

			numRecords++;
			if ((numRecords > 50000) && (numRecords%50000 == 0)) { // Commit every 10000 records
				try {
					// Flush the batch writer
					bw.flush();
					if(logger.isDebugEnabled()) {
						logger.debug("Flushed tab1");
					}
				} catch (Exception e) {
					logger.error(e);
				}
			}
		}
	}
}
