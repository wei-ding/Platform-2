1) Copy the .settings.xml from /opt/apache-maven-3.1.1/conf to .m2 directory under user's (logged in) home directory.
2) Edit the settings.xml, add the below snippet in profiles section. 
   Appropriately modifying db.url, db.username, db.password, hadoop.output.desitnationDirectory

    <profile>
      <id>env-properties</id>
      <activation>
               <activeByDefault>true</activeByDefault>
      </activation>
      <properties>
               <db.driverClassName>com.mysql.jdbc.Driver</db.driverClassName>
               <db.url>jdbc:mysql://localhost:3306/clinical3PO</db.url>
               <db.username>root</db.username>
               <db.password>@adminpassword</db.password>

               <omop.hive.db>c3pohivedemo</omop.hive.db>
               <omop.hive.death>death_t</omop.hive.death>
               <omop.hive.concept>concept_t</omop.hive.concept>
               <omop.hive.observation>observation_t</omop.hive.observation>

			   <!-- Application lookup location for the hiveQueries.properties file-->
			   <hive.query.file.location>/home/hdfs/c3po-dist/clinical3PO-hadoop-scripts</hive.query.file.location>
			   
			   <clinical3PO.hive.host>10.147.128.12</clinical3PO.hive.host>
			   <clinical3PO.hive.port>10000</clinical3PO.hive.port>
			   <clinical3PO.hive.db>default</clinical3PO.hive.db>
			   <clinical3PO.hive.user>hive</clinical3PO.hive.user>
			   <clinical3PO.hive.password>c3po123</clinical3PO.hive.password>			   

               <!-- Environment related (could be DEVELOPMENT, PRODUCTION)-->
               <clinical3PO.environment.type>DEVELOPMENT</clinical3PO.environment.type>
               <clinical3PO.logging.level>DEBUG</clinical3PO.logging.level>

               <!-- Application log, output directory-->
               <clinical3PO.logging.file>/home/c3po/clinical3PO-logs/clinical3PO.log</clinical3PO.logging.file>
               <clinical3PO.perfLogging.file>/home/c3po/clinical3PO-logs/clinical3POPerf.log</clinical3PO.perfLogging.file>
               
               <!-- Application related batch upload files, output directory-->
               <clinical3PO.app.dataDirectory>/home/c3po/clinical3PO-app-data</clinical3PO.app.dataDirectory>

               <!-- Directory where shell scripts related to hadoop search are stored -->
               <clinical3PO.hadoop.shellscripts.dir>/home/c3po/clinical3PO-hadoop-scripts</clinical3PO.hadoop.shellscripts.dir>
               <clinical3PO.hadoop.shellscripts.commonSearchScript>commonSearch.sh</clinical3PO.hadoop.shellscripts.commonSearchScript>

               <!-- Local Directory where hadoop output(s) are present -->
               <clinical3PO.hadoop.localOutput.dir>/home/c3po/clinical3PO-hadoop-output</clinical3PO.hadoop.localOutput.dir>
				
			   <!-- Directory to save uploaded files -->
			   <clinical3PO.mlflex.directory>/home/c3po/ML-Flex</clinical3PO.mlflex.directory>
               
			   <!-- Hadoop related local files -->
               <hadoop.file.conceptFile>/c3po/datafiles/concept.txt</hadoop.file.conceptFile>
               <hadoop.file.observationFile>/c3po/datafiles/observation.txt</hadoop.file.observationFile>
               <hadoop.file.deathFile>/c3po/datafiles/death.txt</hadoop.file.deathFile>

               <!-- Uncomment the relevant one depending on compilation local or remote -->
               <!-- Hadoop local execution -->
               <!--clinical3PO.hadoop.opts>-D fs.defaultFS=hdfs://localhost:9000 -D yarn.resourcemanager.address=localhost:8032 -D yarn.resourcemanager.scheduler.address=localhost:8030</clinical3PO.hadoop.opts-->
               <!--clinical3PO.hadoop.namenode>hdfs://localhost:9000</clinical3PO.hadoop.namenode-->

               <!-- Hadoop remote execution -->
               <!--clinical3PO.hadoop.opts>-D fs.defaultFS=hdfs://ec2-m1.ec2.internal:9000 -D yarn.resourcemanager.address=ec2-m1.ec2.internal:8032 -D yarn.resourcemanager.scheduler.address=ec2-m1.ec2.internal:8030 -D mapreduce.input.fileinputformat.split.minsize=18253611008 -D mapreduce.map.memory.mb=3072 -D mapreduce.job.reduce.slowstart.completedmaps=0.80 -D mapreduce.map.speculative=false</clinical3PO.hadoop.opts-->
               <!--clinical3PO.hadoop.namenode>hdfs://ec2-m1.ec2.internal:9000</clinical3PO.hadoop.namenode-->

               <!-- Accumulo related fields -->
               <!--clinical3PO.accumulo.instance>c3po</clinical3PO.accumulo.instance-->

               <!-- Uncomment the relevant one depending on compilation local or remote -->
               <!-- Accumulo local execution -->
               <!--clinical3PO.accumulo.zookeeper>localhost:2181</clinical3PO.accumulo.zookeeper-->
               <!-- Accumulo remote execution -->
               <!--clinical3PO.accumulo.zookeeper>ec2-m1.ec2.internal:2181,ec2-m2.ec2.internal:2181,ec2-m3.ec2.internal:2181</clinical3PO.accumulo.zookeeper-->

               <clinical3PO.accumulo.user>root</clinical3PO.accumulo.user>
               <clinical3PO.accumulo.password>c3po123</clinical3PO.accumulo.password>

               <!-- Accumulo related application table names -->
               <clinical3PO.accumulo.table.concept>concept</clinical3PO.accumulo.table.concept>

               <clinical3PO.accumulo.table.personIdSearch.personId>personid</clinical3PO.accumulo.table.personIdSearch.personId>
               <clinical3PO.accumulo.table.personIdSearch.observation>observation</clinical3PO.accumulo.table.personIdSearch.observation>

               <clinical3PO.accumulo.table.observationIdSearch.observation>observation</clinical3PO.accumulo.table.observationIdSearch.observation>
               <clinical3PO.accumulo.table.observationIdSearch.index>personobservationid</clinical3PO.accumulo.table.observationIdSearch.index>
               <clinical3PO.accumulo.table.observation.death>death</clinical3PO.accumulo.table.observation.death>

      </properties>
    </profile>


3) To build the app give command - mvn clean package, the war would be created in app/target
