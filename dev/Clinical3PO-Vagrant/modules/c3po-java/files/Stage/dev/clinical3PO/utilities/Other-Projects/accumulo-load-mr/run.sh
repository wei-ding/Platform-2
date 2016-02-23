#!/bin/sh

# -----------------------------------------------------
# PARMAETRES TO BE INPUTED ACCORDING TO THE REQUIREMENT
# -----------------------------------------------------

# HDFS Input Directories
# The below shown format is for non-cluster mode. 
# To run in cluster env, you should configure something like this: "hdfs://ec2-m1.ec2.internal:9000/directory in hdfs" 
hdfsObservationDir=/c3po/datafiles/observation.txt
hdfsConceptDir=/c3po/datafiles/concept.txt
hdfsDeathDir=/c3po/datafiles/death.txt


# HDFS Output Directories
hdfsOutputDir=/user/sample


# INPUT PARMS
# 1 for Observation Table
# 2 for Concept Table
# 3 for Death Table
arg=2


# Accumulo Table Names TO BE
# OBSERVATION TABLE
accumuloObservationTable1=testObservation1
accumuloObservationTable2=testPersonIdTable1
accumuloObservationTable3=testPersonObservationTable1
# CONCEPT TABLE
accumuloConceptTable=testConcept1
# DEATH TABLE
accumuloDeathTable=testDeath1


# ACCUMULO Details
accumuloInstance=c3po
accumuloUser=root
accumuloPassword=c3po123
# The below shown format is for non-cluster mode. 
# To run in cluster env, you should configure something like this: "ec2-m1.ec2.internal:2181,ec2-m2.ec2.internal:2181,ec2-m3.ec2.internal:2181"
zookeeperInstance=localhost:2181


if [ $arg -ne 1 ] ||  [ $arg -ne 2 ] || [ $arg -ne 3 ]
then
	echo "Entered Wrong choise. Valid Input Args are 1 OR 2 OR 3."
	exit
fi

if [ $arg -eq 1 ]
then
	echo " 	Creating Observation Table... " 

	# Deleting Output File in Hadoop
	hdfs dfs -rm -R $hdfsOutputDir

	cd lib/

	# To RUN IN 168 MACHINE
	# Inputs: hdfs directory, tableName-1, accumuloInstance, ZookeeperInstance, accumuloUser, AccumuloPassword, tableName-2, tableName-3, partiationSize Of HDFS File, outputDirectory
	hadoop jar accumulo-load-mr-0.0.1-SNAPSHOT.jar accumulo.mapreduce.Drivers.ObservationDriverMain $hdfsObservationDir $accumuloObservationTable1 $accumuloInstance $zookeeperInstance $accumuloUser $accumuloPassword $accumuloObservationTable2 $accumuloObservationTable3 30000000 $hdfsOutputDir


	# TO RUN ON CLUSTER
	#hadoop jar accumulo-load-mr-0.0.1-SNAPSHOT.jar -D fs.defaultFS=hdfs://ec2-m1.ec2.internal:9000 -D yarn.resourcemanager.address=ec2-m1.ec2.internal:8032 -D yarn.resourcemanager.scheduler.address=ec2-m1.ec2.internal:8030 -D mapreduce.map.memory.mb=2056 $hdfsObservationDir $accumuloObservationTable1 $accumuloInstance $zookeeperInstance $accumuloUser $accumuloPassword $accumuloObservationTable2 $accumuloObservationTable3 130000000 $hdfsOutputDir	

	echo "Verify the table by accesing accumulo shell"
elif [ $arg -eq 2 ]
then	
	echo "Creating Concept Table... "

	# Deleting Output File in Hadoop
	hdfs dfs -rm -R $hdfsOutputDir

	cd lib/

	# To RUN IN 168 MACHINE
	# Inputs: hdfs directory, tableName-1, accumuloInstance, ZookeeperInstance, accumuloUser, AccumuloPassword, outputDirectory
	hadoop jar accumulo-load-mr-0.0.1-SNAPSHOT.jar accumulo.mapreduce.Drivers.ConceptDriverMain $hdfsConceptDir $accumuloConceptTable $accumuloInstance $zookeeperInstance $accumuloUser $accumuloPassword $hdfsOutputDir

	echo "Verify the table by accesing accumulo shell"
elif [ $arg -eq 3 ]
then 
	echo "Creating Death Table... "
	
	# Deleting Output File in Hadoop
	hdfs dfs -rm -R $hdfsOutputDir

	cd lib/

	# To RUN IN 168 MACHINE
	# Inputs: hdfs directory, tableName-1, accumuloInstance, ZookeeperInstance, accumuloUser, AccumuloPassword, outputDirectory
	hadoop jar accumulo-load-mr-0.0.1-SNAPSHOT.jar accumulo.mapreduce.Drivers.DeathDriverMain $hdfsDeathDir $accumuloDeathTable $accumuloInstance $zookeeperInstance $accumuloUser $accumuloPassword $hdfsOutputDir
	
	echo "Verify the table by accesing accumulo shell"
fi
