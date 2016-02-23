/* Compilation is by the following command
   javac -classpath $HADOOP_HOME/share/hadoop/common/hadoop-common-2.4.1.jar:$ACCUMULO_HOME/lib/accumulo-core.jar LoadObservation.java */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.BatchWriterConfig;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Instance;
import org.apache.accumulo.core.client.ZooKeeperInstance;
import org.apache.accumulo.core.client.security.tokens.PasswordToken;
import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.ColumnVisibility;

import org.apache.hadoop.io.Text;

public class LoadObservation {

   public static void main(String[] args) throws Exception {

      String[] columnQualifiers;
      String   inputFileInHadoop;
      String   tableNameInAccumulo, personIdTable, personObservationTable;
      String   accumuloInstance, zookeeperServers, accumuloUser, accumuloPassword;
      long     numRecords = 0;
      BatchWriterConfig config = null;
      BatchWriter bw = null, bw1 = null, bw2 = null;
      Connector conn = null;
      int i = 0, j = 0;
      String rowKey = null;
      String rowPersonId = null;
      String rowObservationId = null;
      String rowCQ = null;
      String[] words = null;
      String[] docSec = {"Doc1", "Doc2", "Doc3", "Doc4", "Doc5"};
      ColumnVisibility colVis = null;

      if (args.length != 8) {
         System.err.println("Usage: LoadObservation <input observation file> <accumulo table name> <accumulo instance> <zookeeper servers> <accumulo user> <accumulo password> <person id table> <person observation table>");
         System.exit(-1);
      }

      inputFileInHadoop = args[0];
      tableNameInAccumulo = args[1];
      accumuloInstance = args[2];
      zookeeperServers = args[3];
      accumuloUser = args[4];
      accumuloPassword = args[5];
      personIdTable = args[6];
      personObservationTable = args[7];

      System.out.println("Input file :" + inputFileInHadoop);
      File inputFile = new File(inputFileInHadoop);

      if (!inputFile.exists()) {
         System.err.println(inputFileInHadoop + " is not found");
         System.exit(-1);
      }

      if (inputFile.isDirectory()) {
         System.err.println(inputFileInHadoop + " is directory, not a file");
         System.exit(-1);
      }

      String line1 = "OBSERVATION;observation_id;person_id;observation_concept_id;observation_date;observation_time;value_as_number;value_as_string;value_as_concept_id;unit_concept_id;range_low;range_high;observation_type_concept_id;associated_provider_id;visit_occurrence_id;relevant_condition_concept_id;observation_source_value;units_source_value";
      columnQualifiers = line1.split(";");

      // Connect
      Instance inst = new ZooKeeperInstance(accumuloInstance, zookeeperServers);
      try {
         conn = inst.getConnector(accumuloUser, new PasswordToken(accumuloPassword));
      } catch (Exception e) {
         e.printStackTrace();
      }

      if (!conn.tableOperations().exists(tableNameInAccumulo)) {
         System.out.println("Creating table " + tableNameInAccumulo);
         try {
            conn.tableOperations().create(tableNameInAccumulo);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      // BatchWriterConfig has reasonable defaults
      config = new BatchWriterConfig();
      config.setMaxMemory(100000000L); // bytes available to batchwriter for buffering mutations

      // Use batch writer to write accumulo table data
      try {
         bw = conn.createBatchWriter(tableNameInAccumulo, config);
      } catch (Exception e) {
         e.printStackTrace();
      }

      if (!conn.tableOperations().exists(personIdTable)) {
         System.out.println("Creating table " + personIdTable);
         try {
            conn.tableOperations().create(personIdTable);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      // Use batch writer to write accumulo table data
      try {
         bw1 = conn.createBatchWriter(personIdTable, config);
      } catch (Exception e) {
         e.printStackTrace();
      }

      if (!conn.tableOperations().exists(personObservationTable)) {
         System.out.println("Creating table " + personObservationTable);
         try {
            conn.tableOperations().create(personObservationTable);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      // Use batch writer to write accumulo table data
      try {
         bw2 = conn.createBatchWriter(personObservationTable, config);
      } catch (Exception e) {
         e.printStackTrace();
      }

      BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));

      try {
         while ((line1 = br.readLine()) != null) {
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
                        bw.addMutation(mutation);
                        //System.out.println("Added mutation to tab1, rowKey : " + rowKey);
                     } catch (Exception e) {
                        e.printStackTrace();
                     }
                  }
                  i++;
               }

               try {
                  // Create new mutation and add rowID, colFam, colQual, value
                  Mutation mutation1 = new Mutation(new Text(rowPersonId));
                  mutation1.put(new Text("CF1"), new Text(rowKey), colVis, new Value(rowKey.getBytes()));

                  // Add the mutation to the batch writer
                  bw1.addMutation(mutation1);
                  //System.out.println("Added mutation to tab2, rowKey : " + rowPersonid);
               } catch (Exception e) {
                  e.printStackTrace();
               }

               try {
                  // Create new mutation and add rowID, colFam, colQual, value
                  Mutation mutation2 = new Mutation(new Text(rowPersonId + "~" + rowObservationId));
                  mutation2.put(new Text("CF1"), new Text(rowKey), colVis, new Value(rowKey.getBytes()));

                  // Add the mutation to the batch writer
                  bw2.addMutation(mutation2);
                  //System.out.println("Added mutation to tab2, rowKey : " + rowPersonid + "~" + rowObservationId);
               } catch (Exception e) {
                  e.printStackTrace();
               }

               numRecords++;
               if ((numRecords > 50000) && (numRecords%50000 == 0)) { // Commit every 10000 records
                  try {
                     // Flush the batch writer
                     bw.flush();
                     //System.out.println("Flushed tab1");
                  } catch (Exception e) {
                     e.printStackTrace();
                  }

                  try {
                     // Flush the batch writer
                     bw1.flush();
                     //System.out.println("Flushed tab2");
                  } catch (Exception e) {
                     e.printStackTrace();
                  }

                  try {
                     // Flush the batch writer
                     bw2.flush();
                     //System.out.println("Flushed tab3");
                  } catch (Exception e) {
                     e.printStackTrace();
                  }
               }
            }
         }
      } catch (IOException e) {
         System.out.println("Error while reading file " + inputFileInHadoop);
      }

      System.out.println("Number of records added : " + numRecords);

      br.close();

      try {
         // Close the batch writer
         bw.close();
         //System.out.println("Closed tab1");
      } catch (Exception e) {
         e.printStackTrace();
      }

      try {
         // Close the batch writer
         bw1.close();
         //System.out.println("Closed tab2");
      } catch (Exception e) {
         e.printStackTrace();
      }

      try {
         // Close the batch writer
         bw2.close();
         //System.out.println("Closed tab3");
      } catch (Exception e) {
         e.printStackTrace();
      }

      System.exit(0);
   }

}

