#!/bin/sh

#$1 Index of which search to run
#$2 Hadoop/Accumulo Output File (where hadoop/accumulo stores output files)
#$3 App Specific Local Directory, where hadoop files are copied.
#$4 App Specific local file (hadoop part-r-00000 is renamed).
#$5 Job Id of the scheduled job

#Index 1 - patientIdSearch
#$6 Person Search Id (Patient Id)

#Index 2 - observationIdSearch
#$6 Person Search Id (Patient Id)
#$7 Observation Search Id (Observation Id) 

#Index 3 - patientIdSearchInAccumulo
#$6 Person Search Id (Patient Id)
#$7 Accumulo Authorizations

#Index 4 - observationIdSearchInAccumulo
#$6 Person Search Id (Patient Id)
#$7 Observation Search Id (Observation Id)
#$8 Accumulo Authorizations

#Index 5 - multiObservationIdSearch
#$6 Input Parameters (Observation Id1~Patient Id1,Patient Id2~Color Scheme#Observation Id2~Patient Id3,Patient Id4~Color Scheme))

#Index 6 - batchSearchInAccumulo
#$6 Input file name (BatchFile20141105_122945)
#$7 Accumulo Authorizations

#Index 7 - mlCrossValidation
#$6 Input Parameters (Observation Id1~Patient Id1,Patient Id2~Number of Bins#Observation Id2~Patient Id3,Patient Id4~Number of Bins))
#$7 Classification Algorithm
#$8 Folds
#$9 Iterations

#Index 8 - patientIdSearchUsingHive
#$3 App Specific Local Directory, where hadoop files are copied.
#$4 App Specific local file (hadoop part-r-00000 is renamed)
#$6 Person Search Id (Patient Id)

#Index 9 - observationIdSearchUsingHive
#$3 App Specific Local Directory, where hadoop files are copied.
#$4 App Specific local file (hadoop part-r-00000 is renamed)
#$6 Person Search Id (Patient Id)
#$7 Observation Search Id (Observation Id)

#Index 10 - multiObservationIdSearchUsingHive
#$3 App Specific Local Directory, where hadoop files are copied.
#$4 App Specific local file (hadoop part-r-00000 is renamed)
#$6 Input Parameters (Observation Id1~Patient Id1,Patient Id2~Color Scheme#Observation Id2~Patient Id3,Patient Id4~Color Scheme))


#Index 14 - Feature Extraction
#$6 Input Parameters (Observation Id1~Patient Id1,Patient Id2~Number of Bins#Observation Id2~Patient Id3,Patient Id4~Number of Bins))
#$7 Classification Algorithm
#$8 Folds
#$9 Iterations


# Check the command line arguments
if [ $1 -eq 1 ]
then
   if [ $# -ne 6 ]
   then
       echo "Usage: $0 1 <Unique Output Dir> <LocalDirectory> <LocalFileName> <Job Id> <Patient Id>"
       exit
   fi

elif [ $1 -eq 2 ]
then
   if [ $# -ne 7 ]
   then
       echo "Usage: $0 2 <Unique Output Dir> <LocalDirectory> <LocalFileName> <Job Id> <Patient Ids> <Observation Id>"
       exit
   fi

elif [ $1 -eq 3 ]
then
   if [ $# -ne 7 ]
   then
       echo "Usage: $0 3 <Unique Output Dir> <LocalDirectory> <LocalFileName> <Job Id> <Patient Id> <Accumulo Authorizations> "
       exit
   fi

elif [ $1 -eq 4 ]
then
   if [ $# -ne 8 ]
   then
       echo "Usage: $0 4 <Unique Output Dir> <LocalDirectory> <LocalFileName> <Job Id> <Patient Ids> <Observation Id> <Accumulo Authorizations>"
       exit
   fi

elif [ $1 -eq 5 ]
then
   if [ $# -ne 6 ]
   then
       echo "Usage: $0 5 <Unique Output Dir> <LocalDirectory> <LocalFileName> <Job Id> <Input Params>"
       exit
   fi
   
elif [ $1 -eq 6 ]
then
   if [ $# -ne 7 ]
   then
       echo "Usage: $0 6 <Unique Output Dir> <LocalDirectory> <LocalFileName> <Job Id> <Input Params> <Accumulo Authorizations>"
       exit
   fi 
elif [ $1 -eq 7 ]
then
   if [ $# -ne 9 ]
   then
       echo "Usage: $0 7 <Unique Output Dir> <LocalDirectory> <LocalFileName> <Job Id> <Input Params> <Classification ALgorithm> <Folds> <Iterations>"
       exit
   fi

elif [ $1 -eq 14 ]
then
   if [ $# -ne 9 ]
   then
       echo "Usage: $0 14 <Unique Output Dir> <LocalDirectory> <LocalFileName> <Job Id> <Input Params> <Classification ALgorithm> <Folds> <Iterations>"
       exit
   fi        
fi


if [ $1 -eq 1 ]
then
   hadoop jar ${clinical3PO.hadoop.shellscripts.dir}/lib/${artifactId}-${version}.jar org.clinical3PO.hadoop.PersonId \
   ${clinical3PO.hadoop.opts} \
   ${hadoop.file.observationFile} $2 ${hadoop.file.conceptFile} $6

elif [ $1 -eq 2 ]
then
   hadoop jar ${clinical3PO.hadoop.shellscripts.dir}/lib/${artifactId}-${version}.jar org.clinical3PO.hadoop.ObservationId \
   ${clinical3PO.hadoop.opts} \
   ${hadoop.file.observationFile} $2 ${hadoop.file.conceptFile} $6 $7 ${hadoop.file.deathFile}

elif [ $1 -eq 3 ]
then
   accumulo jar ${clinical3PO.hadoop.shellscripts.dir}/lib/${artifactId}-${version}.jar org.clinical3PO.accumulo.PersonIdSearchInAccumulo /tmp/$2 $6 $7 ${clinical3PO.accumulo.instance} ${clinical3PO.accumulo.zookeeper} ${clinical3PO.accumulo.user} ${clinical3PO.accumulo.password} \
   ${clinical3PO.accumulo.table.concept} ${clinical3PO.accumulo.table.personIdSearch.personId} ${clinical3PO.accumulo.table.personIdSearch.observation}

elif [ $1 -eq 4 ]
then
   accumulo jar ${clinical3PO.hadoop.shellscripts.dir}/lib/${artifactId}-${version}.jar org.clinical3PO.accumulo.ObservationIdSearchInAccumulo /tmp/$2 $6 $8 ${clinical3PO.accumulo.instance} ${clinical3PO.accumulo.zookeeper} ${clinical3PO.accumulo.user} ${clinical3PO.accumulo.password} \
   $7 ${clinical3PO.accumulo.table.concept} ${clinical3PO.accumulo.table.observation.death} ${clinical3PO.accumulo.table.observationIdSearch.index} ${clinical3PO.accumulo.table.observationIdSearch.observation}

elif [ $1 -eq 5 ]
then
   hadoop jar ${clinical3PO.hadoop.shellscripts.dir}/lib/${artifactId}-${version}.jar org.clinical3PO.hadoop.MultiObservationId \
   ${clinical3PO.hadoop.opts} \
   ${hadoop.file.observationFile} $2 ${hadoop.file.conceptFile} $6 ${hadoop.file.deathFile}

 
elif [ $1 -eq 6 ]
then
   accumulo jar ${clinical3PO.hadoop.shellscripts.dir}/lib/${artifactId}-${version}.jar org.clinical3PO.accumulo.BatchSearchInAccumulo /tmp/$2 $6 $7 ${clinical3PO.accumulo.instance} ${clinical3PO.accumulo.zookeeper} ${clinical3PO.accumulo.user} ${clinical3PO.accumulo.password} \
   ${clinical3PO.accumulo.table.concept} ${clinical3PO.accumulo.table.observationIdSearch.index} ${clinical3PO.accumulo.table.observationIdSearch.observation}

elif [ $1 -eq 7 ]
then
   hadoop jar ${clinical3PO.hadoop.shellscripts.dir}/lib/${artifactId}-${version}.jar org.clinical3PO.hadoop.MultiObservationId \
   ${clinical3PO.hadoop.opts} \
   ${hadoop.file.observationFile} $2 ${hadoop.file.conceptFile} $6 ${hadoop.file.deathFile}
 
elif [ $1 -eq 8 ] || [ $1 -eq 10 ]
then
	cd ${clinical3PO.hadoop.shellscripts.dir}/lib
	java -cp "*" -Dclinical3PO.dataServices.logging.file=${clinical3PO.hadoop.localOutput.dir}/logs/$4.log4j.log \
	-Dlog4j.configuration=file:${clinical3PO.hadoop.shellscripts.dir}/log4j.xml \
    org.clinical3PO.services.hive.HiveSearchEngine $3 $4 $6
    
elif [ $1 -eq 9 ]
then
	cd ${clinical3PO.hadoop.shellscripts.dir}/lib
	java -cp "*" -Dclinical3PO.dataServices.logging.file=${clinical3PO.hadoop.localOutput.dir}/logs/$4.log4j.log \
	-Dlog4j.configuration=file:${clinical3PO.hadoop.shellscripts.dir}/log4j.xml \
    org.clinical3PO.services.hive.HiveSearchEngine $3 $4 $6 $7
	
elif [ $1 -eq 11 ]
then 
    Rscript ${clinical3PO.hadoop.shellscripts.dir}/PatientSearch.R $6 ${hadoop.file.conceptFile} ${hadoop.file.observationFile} /user/$LOGNAME/$2 $3/$4
elif [ $1 -eq 12 ]
then 
    Rscript ${clinical3PO.hadoop.shellscripts.dir}/ObservationSearch.R $6 $7 ${hadoop.file.conceptFile} ${hadoop.file.deathFile} ${hadoop.file.observationFile} /user/$LOGNAME/$2 $3/$4
elif [ $1 -eq 13 ]
then
    Rscript ${clinical3PO.hadoop.shellscripts.dir}/BatchSearchrmr.R $6 ${hadoop.file.conceptFile} ${hadoop.file.deathFile} ${hadoop.file.observationFile} /user/$LOGNAME/$2 $3/$4

elif [ $1 -eq 14 ]
then
	echo "Initiating Feature Extraction Module. Calling FExtract.sh. "
	
	################################################################
	#Since every thing is static and hardcoded, remove the output directory before another run. (Not required once program accept input as parameters) 
	hdfs dfs -rm -R /user/$LOGNAME/output
	${clinical3PO.hadoop.shellscripts.dir}/fextract.sh diasabp
fi


if [ $? -ne 0 ]
then
    echo "Search job failed. Exiting."
    cd ${clinical3PO.hadoop.shellscripts.dir}/lib
    java -ea -cp "*" org.clinical3PO.services.JobStatusUpdate $5 FAIL
  	exit
fi

echo "Search job finished"

###############################################################################################
# For Feature Extraction - Temporary (Remove this session and include in --> # For hadoop search, once Feature EXtraction program accept inputs as parameters) 
if [ $1 -eq 14 ]
then
	mkdir $3/$2
	hdfs dfs -get /user/$LOGNAME/output/* $3/$2
	
	if [ $? -ne 0 ]
    then
      	echo "Hadoop local file copy failure"
	    cd ${clinical3PO.hadoop.shellscripts.dir}/lib
	    java -ea -cp "*" org.clinical3PO.services.JobStatusUpdate $5 FAIL
      	exit
   fi
   echo "Copying hadoop output to local directory done"
   echo "Copying the local hadoop output to $3/$4"
   
   cp $3/$2/part-r-00000 $3/$4
   
   if [ $? -ne 0 ]
   then
      	echo "Hadoop output copy failed. Exiting."
	    cd ${clinical3PO.hadoop.shellscripts.dir}/lib
	    java -ea -cp "*" org.clinical3PO.services.JobStatusUpdate $5 FAIL
      	exit
   fi
   echo "copying the hadoop output from local directory to app specific file is done"
fi

# For hadoop search
if [ $1 -eq 1 ] || [ $1 -eq 2 ] || [ $1 -eq 5 ] || [ $1 -eq 7 ]
then
   hdfs dfs -D fs.defaultFS=${clinical3PO.hadoop.namenode} -copyToLocal /user/$LOGNAME/$2 $3

   if [ $? -ne 0 ]
   then
      	echo "Hadoop local file copy failure"
	    cd ${clinical3PO.hadoop.shellscripts.dir}/lib
	    java -ea -cp "*" org.clinical3PO.services.JobStatusUpdate $5 FAIL
      	exit
   fi

   echo "Copying hadoop output to local directory done"

   # copy and Rename the file part-r-00000 (considering the hadoop job ran properly)

   echo "Copying the local hadoop output to $3/$4"

   cp $3/$2/part-r-00000 $3/$4

   if [ $? -ne 0 ]
   then
      	echo "Hadoop output copy failed. Exiting."
	    cd ${clinical3PO.hadoop.shellscripts.dir}/lib
	    java -ea -cp "*" org.clinical3PO.services.JobStatusUpdate $5 FAIL
      	exit
   fi
   echo "copying the hadoop output from local directory to app specific file is done"
fi

# For accumulo search
if [ $1 -eq 3 ] || [ $1 -eq 4 ] || [ $1 -eq 6 ]
then
   echo "Copying the local accumulo output to $3/$4"

   cp -p /tmp/$2 $3/$4

   if [ $? -ne 0 ]
   then
      	echo "Accumulo search output copy failed. Exiting."
	    cd ${clinical3PO.hadoop.shellscripts.dir}/lib
	    java -ea -cp "*" org.clinical3PO.services.JobStatusUpdate $5 FAIL
      	exit
   fi
   rm -f /tmp/$2
   echo "Copying the Accumulo search output from local directory to app specific file is done"
fi

# ML-Flex
if [ $1 -eq 7 ]
then
	echo "Generating ARFF data"	
	cd ${clinical3PO.hadoop.shellscripts.dir}/lib
	java -cp "*" \
	org.clinical3PO.arff.ArffGeneration $3/$4 $6 ${clinical3PO.app.dataDirectory}/mlflex $4 $7 $8 $9
	
	cd ${clinical3PO.mlflex.directory}
	java -Xmx1g -jar mlflex.jar EXPERIMENT_FILE=${clinical3PO.app.dataDirectory}/mlflex/experiments/$4.txt ACTION=Process 
		
	mv ${clinical3PO.mlflex.directory}/Output/$4_Experiment ${clinical3PO.app.dataDirectory}/mlflex/output/$5
	cd ${clinical3PO.app.dataDirectory}/mlflex/output
	tar -cvf mlFlexReport$5.tar.gz $5/
		 
	if [ $? -ne 0 ]
    then
      	echo "Arff generation failed. Exiting."
	    cd ${clinical3PO.hadoop.shellscripts.dir}/lib
	    java -ea -cp "*" org.clinical3PO.services.JobStatusUpdate $5 FAIL
      	exit
    fi
fi

# FEATURE EXTRACTION
if [ $1 -eq 14 ]
then
	echo "Copying generated ARFF file to ${clinical3PO.app.dataDirectory}/mlflex/data"
	
	if [ -d "${clinical3PO.app.dataDirectory}/mlflex/data" ]
	then
		echo "Directory already exists"
	else
		mkdir ${clinical3PO.app.dataDirectory}/mlflex
		mkdir ${clinical3PO.app.dataDirectory}/mlflex/data
		echo "Directory doesn't exists. Created new directory"
	fi
	
	cp $3/$2/*.arff ${clinical3PO.app.dataDirectory}/mlflex/data/$4.arff
	
	##########################################################################
	# REMOVE THIS LINE, ONCE ARFF IS CORRECTED.
	sed -i "s/classAttribute_diasabp/class/g" "${clinical3PO.app.dataDirectory}/mlflex/data/$4.arff"
	
	if [ $? -ne 0 ]
    then
      	echo "Arff generation failed. Exiting."
	    cd ${clinical3PO.hadoop.shellscripts.dir}/lib
	    java -ea -cp "*" org.clinical3PO.services.JobStatusUpdate $5 FAIL
      	exit
    fi
	
	cd ${clinical3PO.hadoop.shellscripts.dir}/lib
	java -cp "*" \
	org.clinical3PO.fe.ExperimentFileGeneration ${clinical3PO.app.dataDirectory}/mlflex $4 $7 $8 $9
	
	cd ${clinical3PO.mlflex.directory}
	java -Xmx1g -jar mlflex.jar EXPERIMENT_FILE=${clinical3PO.app.dataDirectory}/mlflex/experiments/$4.txt ACTION=Process
	
	mv ${clinical3PO.mlflex.directory}/Output/$4_Experiment ${clinical3PO.app.dataDirectory}/mlflex/output/$5
	cd ${clinical3PO.app.dataDirectory}/mlflex/output
	tar -cvf mlFlexReport$5.tar.gz $5/
		 
	if [ $? -ne 0 ]
    then
        echo "Arff generation failed. Exiting."
	    cd ${clinical3PO.hadoop.shellscripts.dir}/lib
	    java -ea -cp "*" org.clinical3PO.services.JobStatusUpdate $5 FAIL
      	exit
    fi 
fi

# Update job status to success
cd ${clinical3PO.hadoop.shellscripts.dir}/lib
java -ea -cp "*" org.clinical3PO.services.JobStatusUpdate $5 SUCCESS
