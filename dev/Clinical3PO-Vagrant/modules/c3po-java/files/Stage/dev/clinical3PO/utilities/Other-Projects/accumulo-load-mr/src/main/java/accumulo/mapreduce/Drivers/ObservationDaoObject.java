package accumulo.mapreduce.Drivers;

import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.BatchWriterConfig;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Instance;
import org.apache.accumulo.core.client.ZooKeeperInstance;
import org.apache.accumulo.core.client.security.tokens.PasswordToken;
import org.apache.log4j.Logger;

public class ObservationDaoObject {

	private static ObservationDaoObject obj = null;
	private static BatchWriter bw = null; 
	private static BatchWriter bw1 = null;
	private static BatchWriter bw2 = null;
	final static Logger logger = Logger.getLogger(ObservationDaoObject.class);

	public static synchronized ObservationDaoObject getInstance() {

		if(obj == null) {
			obj = new ObservationDaoObject();
		} 
		return obj;
	}
	
	public void init(String[] args) {

		String tableNameInAccumulo = args[1];
		String accumuloInstance = args[2];
		String zookeeperServers = args[3];
		String accumuloUser = args[4];
		String accumuloPassword = args[5];
		String personIdTable = args[6];
		String personObservationTable = args[7];

		Connector conn = null;
		// Connect
		Instance inst = new ZooKeeperInstance(accumuloInstance, zookeeperServers);
		try {
			conn = inst.getConnector(accumuloUser, new PasswordToken(accumuloPassword));

			// BatchWriterConfig has reasonable defaults
			BatchWriterConfig config = new BatchWriterConfig();
			config.setMaxMemory(100000000L); // bytes available to batchwriter for buffering mutations

			if (!conn.tableOperations().exists(tableNameInAccumulo)) {
				logger.info("Creating table " + tableNameInAccumulo);
				conn.tableOperations().create(tableNameInAccumulo);
			}

			// Use batch writer to write accumulo table data
			bw = conn.createBatchWriter(tableNameInAccumulo, config);

			if (!conn.tableOperations().exists(personIdTable)) {
				logger.info("Creating table " + personIdTable);
				conn.tableOperations().create(personIdTable);
			}

			// Use batch writer to write accumulo table data
			bw1 = conn.createBatchWriter(personIdTable, config);


			if (!conn.tableOperations().exists(personObservationTable)) {
				logger.info("Creating table " + personObservationTable);
				conn.tableOperations().create(personObservationTable);
			}

			// Use batch writer to write accumulo table data
			bw2 = conn.createBatchWriter(personObservationTable, config);

			logger.info("IN INIT METHOD.");
			logger.info("BATCH WRITER" +bw);
			logger.info("BATCH WRITER" +bw1);
			logger.info("BATCH WRITER" +bw2);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public BatchWriter getBw() {
		return bw;
	}

	public BatchWriter getBw1() {
		return bw1;
	}

	public BatchWriter getBw2() {
		return bw2;
	}
}