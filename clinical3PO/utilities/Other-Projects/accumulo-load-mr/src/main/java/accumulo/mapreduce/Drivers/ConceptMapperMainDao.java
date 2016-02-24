package accumulo.mapreduce.Drivers;

import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Value;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

public class ConceptMapperMainDao extends Mapper<LongWritable, Text, Text, Text> {

	private long numRecords = 0;
	private ConceptDaoObject obj = null;
	private String[] columnQualifiers = null;
	final static Logger logger = Logger.getLogger(ConceptMapperMainDao.class);

	public ConceptMapperMainDao() {

		String line1 = "src_concept_id;property_name;property_definition;value_type;value_units;value_min;value_max";
		columnQualifiers = line1.split(";");
	}

	@Override
	public void setup(Context context) {

		final String inputFile = context.getConfiguration().get("inputFile");
		final String accumuloTableName = context.getConfiguration().get("accumuloTableName");
		final String accumuloInstance = context.getConfiguration().get("accumuloInstance");
		final String zooKeepers = context.getConfiguration().get("zooKeepers");
		final String accumuloUser = context.getConfiguration().get("accumuloUser");
		final String accumuloPassword = context.getConfiguration().get("accumuloPassword");

		String[] args = {inputFile, accumuloTableName, accumuloInstance, zooKeepers, accumuloUser, accumuloPassword};
		obj = ConceptDaoObject.getInstance();
		obj.init(args);
	}

	public void map(LongWritable key, Text value, Context context) {

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
		int i = 0;
		String rowKey = null;
		String rowCQ = null;
		String[] words = null;

		words = line1.split(";");

		if (!words[0].trim().equals("src_concept_id")) { // Skipping 1st line

			rowKey = words[0]; // First field is key
			i = 0;

			for (String word : words) {

				if (i == 0) // Skipping the first field
					; // No processing
				else {
					rowCQ = columnQualifiers[i];

					try {
						// Create new mutation and add rowID, colFam, colQual, value
						Mutation mutation = new Mutation(new Text(rowKey));
						mutation.put(new Text("CF1"), new Text(rowCQ), new Value(word.getBytes()));

						// Add the mutation to the batch writer
						bw.addMutation(mutation);
						if(logger.isDebugEnabled()) {
							logger.debug("Added mutation to tab1, rowKey : " + rowKey);
						}
					} catch (Exception e) {
						e.printStackTrace();
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