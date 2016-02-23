package accumulo.mapreduce.Drivers;

import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.BatchWriterConfig;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Instance;
import org.apache.accumulo.core.client.ZooKeeperInstance;
import org.apache.accumulo.core.client.security.tokens.PasswordToken;
import org.apache.log4j.Logger;

public class DeathDaoObject {

	private static DeathDaoObject obj = null;
	private static BatchWriter bw = null; 
	final static Logger logger = Logger.getLogger(DeathDaoObject.class);

	public static synchronized DeathDaoObject getInstance() {

		if(obj == null) {
			obj = new DeathDaoObject();
		} 
		return obj;
	}

	public void init(String[] args) {

		String tableNameInAccumulo = args[1];
		String accumuloInstance = args[2];
		String zookeeperServers = args[3];
		String accumuloUser = args[4];
		String accumuloPassword = args[5];
		Connector conn = null;

		// Connect
		Instance inst = new ZooKeeperInstance(accumuloInstance, zookeeperServers);
		try {
			conn = inst.getConnector(accumuloUser, new PasswordToken(accumuloPassword));
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!conn.tableOperations().exists(tableNameInAccumulo)) {
			logger.info("Creating table " + tableNameInAccumulo);
			try {
				conn.tableOperations().create(tableNameInAccumulo);
			} catch (Exception e) {
				logger.error(e);
			}
		}

		// BatchWriterConfig has reasonable defaults
		BatchWriterConfig config = new BatchWriterConfig();
		config.setMaxMemory(100000000L); // bytes available to batchwriter for buffering mutations

		// Use batch writer to write accumulo table data
		try {
			bw = conn.createBatchWriter(tableNameInAccumulo, config);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public BatchWriter getBw() {
		return bw;
	}
}
