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

#Index 14 - Feature Extraction - MLFLEX
#$6 Class Property(Ex: diasabp or glucose)
#$7 Classification Algorithm
#$8 Folds (integer)
#$9 Iterations (integer)
#${10} ClassBinTime (String-HH:mm)
#${11} StartDate (String-MM/dd/yy)
#${12} EndDate (String-MM/dd/yy)
#${13} StartTime (String-HH:mm:ss)
#${14} EndTime (String-HH:mm:ss)

#Index 15 - NLP 
# No input properties - atleast for now.

#Index 16 - Feature Extraction - Ugene
#$6 Class Property(Ex: diasabp or glucose)
#$7 ClassBinTime (String-HH:mm)
#$8 StartDate (String-MM/dd/yy)
#$9 EndDate (String-MM/dd/yy)
#${10} StartTime (String-HH:mm:ss)
#${11} EndTime (String-HH:mm:ss)


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
   
elif [ $1 -eq 8 ]
then
   if [ $# -ne 6 ]
   then
       echo "Usage: $0 15 <Unique Output Dir> <LocalDirectory> <LocalFileName> <Job Id> <Patient Ids>"
       exit
   fi 

elif [ $1 -eq 9 ]
then
   if [ $# -ne 7 ]
   then
       echo "Usage: $0 15 <Unique Output Dir> <LocalDirectory> <LocalFileName> <Job Id> <Patient Ids> <Observation Id>"
       exit
   fi 
 
elif [ $1 -eq 10 ]
then
   if [ $# -ne 6 ]
   then
       echo "Usage: $0 15 <Unique Output Dir> <LocalDirectory> <LocalFileName> <Job Id> <Input Params>"
       exit
   fi 

elif [ $1 -eq 14 ]
then
   if [ $# -ne 14 ]
   then
       echo "Usage: $0 14 <Unique Output Dir> <LocalDirectory> <LocalFileName> <Job Id> <Class Property> <Classification ALgorithm> <Folds> <Iterations> <Class Time> <Start Date> <End Date> <Start Time> <End Time>"
       exit
   fi 
   
elif [ $1 -eq 15 ]
then
   if [ $# -ne 5 ]
   then
       echo "Usage: $0 15 <Unique Output Dir> <LocalDirectory> <LocalFileName> <Job Id> "
       exit
   fi 
 
elif [ $1 -eq 16 ]
then
   if [ $# -ne 11 ]
   then
       echo "Usage: $0 16 <Unique Output Dir> <LocalDirectory> <LocalFileName> <Job Id> <Class Property> <Class Time> <Start Date> <End Date> <Start Time> <End Time>"
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
	echo "Initiating Feature Extraction Module With Ml-Flex. "
	
	${clinical3PO.hadoop.shellscripts.dir}/fextract.sh $6 $2 ${10} ${11} ${12} ${13} ${14}
   
   	if [ $? -ne 0 ]
   	then
      	echo "Feature Extraction execution failed. Exiting."
	    cd ${clinical3PO.hadoop.shellscripts.dir}/lib
	    java -ea -cp "*" org.clinical3PO.services.JobStatusUpdate $5 FAIL
      	exit
   	fi

elif [ $1 -eq 16 ]
then
	echo "Initiating Feature Extraction Module With UGENE. "
	${clinical3PO.hadoop.shellscripts.dir}/fextract.sh $6 $2 $7 $8 $9 ${10} ${11}
	
	if [ $? -ne 0 ]
   	then
      	echo "Feature Extraction execution failed. Exiting."
	    cd ${clinical3PO.hadoop.shellscripts.dir}/lib
	    java -ea -cp "*" org.clinical3PO.services.JobStatusUpdate $5 FAIL
      	exit
   	fi
	
elif [ $1 -eq 15 ]
then

	################################################################
	# This integration of NLP isn't complete. Once the original code is integrated into NLP as per the design, we would configure xml path and remaining parameters.
	
	# SHELL ASSUME NLP JAR IS IN PLACE.
    cd ${nlp.codebase.jar}
    
    if [ $? -ne 0 ]
   	then
      	echo "Path ${nlp.codebase.jar} doesn't exists. Please provide path of NLP jar. Program Exiting."
	    cd ${clinical3PO.hadoop.shellscripts.dir}/lib
	    java -ea -cp "*" org.clinical3PO.services.JobStatusUpdate $5 FAIL
      	exit
   	fi
   	
    hdfs dfs -rm -R /home/hdfs/NLP/output; hadoop jar *-jar-with-dependencies.jar /home/hdfs/NLP/model/redex-pain.model pain /home/hdfs/NLP/input /home/hdfs/NLP/output
        
    if [ -d "${clinical3PO.app.dataDirectory}/NLP" ]
	then
		echo "Directory ${clinical3PO.app.dataDirectory}/NLP already exists"
	else
		mkdir ${clinical3PO.app.dataDirectory}/NLP
		echo "Directory ${clinical3PO.app.dataDirectory}/NLP doesn't exists. Created new directory"
	fi
    
    mkdir $3/$2NLP
    hdfs dfs -get /home/hdfs/NLP/output/* $3/$2NLP
    
    ##### COMMAND TO REANME EXTENSIONS OF ALL REDUCER FILES TO .XML #####
   	echo " ---------------------------------  "
	echo "RENAMING HADOOP OUTPUT FILES AS .XML EXTENSION"
	echo " ---------------------------------  "
   	cd $3/$2NLP/
   	mv *-r-* $4.xml
   	#for old in *-r-*; do mv $old `basename $old .txt`.xml; done
    mkdir ${clinical3PO.app.dataDirectory}/NLP/$4
   	cp $3/$2NLP/$4.xml ${clinical3PO.app.dataDirectory}/NLP/$4/
   
   	if [ $? -ne 0 ]
   	then
      	echo "Rename/copying files not succesfull . Exiting."
	    cd ${clinical3PO.hadoop.shellscripts.dir}/lib
	    java -ea -cp "*" org.clinical3PO.services.JobStatusUpdate $5 FAIL
      	exit
   	fi
    
	cd ${clinical3PO.hadoop.shellscripts.dir}/lib
	java -cp "*" -Dclinical3PO.dataServices.logging.file=${clinical3PO.hadoop.localOutput.dir}/logs/$4.log4j.log \
	-Dlog4j.configuration=file:${clinical3PO.hadoop.shellscripts.dir}/log4j.xml \
	org.clinical3PO.services.nlp.UpdateDataFilesFromNLP ${clinical3PO.app.dataDirectory}/NLP/$4/$4.xml
	
	if [ $? -ne 0 ]
   	then
      	echo "Running Java class to update Hive isn't succesfull . Exiting."
	    cd ${clinical3PO.hadoop.shellscripts.dir}/lib
	    java -ea -cp "*" org.clinical3PO.services.JobStatusUpdate $5 FAIL
      	exit
   	fi
	
	# This is required to update DB on completion of job.
    cp $3/$2NLP/*.xml $3/$4
fi


if [ $? -ne 0 ]
then
    echo "Search job failed. Exiting."
    cd ${clinical3PO.hadoop.shellscripts.dir}/lib
    java -ea -cp "*" org.clinical3PO.services.JobStatusUpdate $5 FAIL
  	exit
fi

echo "Search job finished"

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
	java -cp "*" org.clinical3PO.arff.ArffGeneration $3/$4 $6 ${clinical3PO.app.dataDirectory}/mlflex $4 $7 $8 $9
	
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
################################ FE is divided into 3-parts ####################################
################################ 1. FE -> ML-FLEX (14)		####################################
################################ 2. FE -> FASTA -> UGENE (16)	####################################
################################ 3. FE -> JSON -> VISUALIZATION (17) ####################################

##############################################################
################ ML-FLEX CALL ON ARFF ########################
##############################################################
if [ $1 -eq 14 ]
then

	hdfs dfs -D fs.defaultFS=${clinical3PO.hadoop.namenode} -copyToLocal /user/$LOGNAME/$2 $3
    echo "Copying the FE output from HDFS to LOCAL is done"
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
	
	if [ $? -ne 0 ]
    then
      	echo "Arff generation failed. Exiting."
	    cd ${clinical3PO.hadoop.shellscripts.dir}/lib
	    java -ea -cp "*" org.clinical3PO.services.JobStatusUpdate $5 FAIL
      	exit
    fi

	cd ${clinical3PO.hadoop.shellscripts.dir}/lib
	java -cp "*" org.clinical3PO.fe.ExperimentFileGeneration ${clinical3PO.app.dataDirectory}/mlflex $4 $7 $8 $9
	
	cd ${clinical3PO.mlflex.directory}
	java -Xmx1g -jar mlflex.jar EXPERIMENT_FILE=${clinical3PO.app.dataDirectory}/mlflex/experiments/$4.txt NUM_THREADS=2 THREAD_TIMEOUT_MINUTES=10 PAUSE_SECONDS=60 ACTION=Process DEBUG=true
	
	mv ${clinical3PO.mlflex.directory}/Output/$4_Experiment ${clinical3PO.app.dataDirectory}/mlflex/output/$5
	cd ${clinical3PO.app.dataDirectory}/mlflex/output
	tar -cvf mlFlexReport$5.tar.gz $5/
		 
	if [ $? -ne 0 ]
   	then
    	echo "Running ML-FLEX failed. Exiting."
	    cd ${clinical3PO.hadoop.shellscripts.dir}/lib
	    java -ea -cp "*" org.clinical3PO.services.JobStatusUpdate $5 FAIL
      	exit
    fi
    	
    ## This is required to update DB on completion of job.
   	cat $3/$2/*.arff > $3/$4
fi

if [ $1 -eq 16 ]
then
    ##############################################################
	########### INTEGRATING ARFF TO FASTA ########################
	##############################################################	
    echo " ---------------------------------  "
	echo "EXTRACTING FASTA FROM ARFF STARTED - HADOOP"
	echo " ---------------------------------  "
	cd ${clinical3PO.hadoop.shellscripts.dir}/lib
	hadoop jar clinical3PO-FExtract-1.0.0-SNAPSHOT-jar-with-dependencies.jar org.clinical3PO.learn.fasta.ArffToFastADriver  \
	-D c3fe.inputdir=$2/$2.arff -D c3fe.outputdir=$2/fasta \
	file://${clinical3PO.hadoop.shellscripts.dir}/fastaDescreteProperties.txt 
	
	if [ -d "${clinical3PO.app.dataDirectory}/ugene" ]
	then
		echo "Directory FASTA already exists"
		else
			mkdir ${clinical3PO.app.dataDirectory}/ugene
			echo "FASTA Directory doesn't exists. Created new."
	fi
	
	if [ -d "${clinical3PO.app.dataDirectory}/ugene/fasta" ]
	then
		echo "Directory FASTA already exists"
		else
			mkdir ${clinical3PO.app.dataDirectory}/ugene/fasta
			echo "FASTA Directory doesn't exists. Created new."
	fi
	
	echo " ---------------------------------------- "
	echo "GETTING HADOOP OUTPUT FILES TO LOCAL"
	echo " ----------------------------------------  "
	hdfs dfs -D fs.defaultFS=${clinical3PO.hadoop.namenode} -copyToLocal /user/$LOGNAME/$2 $3
	
	if [ $? -ne 0 ]
    then
    	echo "HDFS to LOCAL($3) Fasta files copy failure"
	   	cd ${clinical3PO.hadoop.shellscripts.dir}/lib
	   	java -ea -cp "*" org.clinical3PO.services.JobStatusUpdate $5 FAIL
    	exit
  	fi
   	
   	##### COMMAND TO REANME EXTENSIONS OF ALL REDUCER FILES TO .FA #####
   	echo " ---------------------------------  "
	echo "RENAMING HADOOP OUTPUT FILES AS .FA EXTENSION"
	echo " ---------------------------------  "
   	cd $3/$2/fasta/
   	for old in *-r-*; do mv $old `basename $old .txt`.fa; done
   	mkdir ${clinical3PO.app.dataDirectory}/ugene/fasta/$4
   	cp $3/$2/fasta/*.fa ${clinical3PO.app.dataDirectory}/ugene/fasta/$4/
   
   	if [ $? -ne 0 ]
   	then
    	echo "Renaming files to .FA is not succesfull. Exiting."
	 	cd ${clinical3PO.hadoop.shellscripts.dir}/lib
		java -ea -cp "*" org.clinical3PO.services.JobStatusUpdate $5 FAIL
    	exit
   	fi
	
	##############################################################
	########### INTEGRATING FASTA-UGENE ##########################
	##############################################################
	echo " ---------------------------------  "
	echo "STARTED GENERATING DISTANCE MATRICES FROM UGENE EXECUTABLE"
	echo " ---------------------------------  "
	sh ${clinical3PO.ugene.directory}/scripts/genDistance.sh ${clinical3PO.app.dataDirectory}/ugene/fasta/$4 > ${clinical3PO.ugene.directory}/logs/log_`date '+%Y-%m-%d_%H-%M-%S'`.txt
	
	if [ $? -ne 0 ]
   	then
    	echo "FAILED - GENERATING DISTANCE MATRICES FROM UGINE TOOL. Exiting."
	 	cd ${clinical3PO.hadoop.shellscripts.dir}/lib
		java -ea -cp "*" org.clinical3PO.services.JobStatusUpdate $5 FAIL
    	exit
    else
    	echo " ---------------------------------  "
		echo "COMPLETED GENERATING DISTANCE MATRICES FROM UGENE EXECUTABLE"
		echo " ---------------------------------  "
   	fi
   	
   	if [ -d "${clinical3PO.app.dataDirectory}/ugene/output" ]
	then
		echo "Directory ${clinical3PO.app.dataDirectory}/ugene/output already exists"
	else
		mkdir ${clinical3PO.app.dataDirectory}/ugene/output
		echo "Directory ${clinical3PO.app.dataDirectory}/ugene/output doesn't exists. Created new."
	fi
	
	mkdir ${clinical3PO.app.dataDirectory}/ugene/output/$5
   	mv ${clinical3PO.ugene.directory}/distanceMatrices ${clinical3PO.app.dataDirectory}/ugene/output/$5
	cd ${clinical3PO.app.dataDirectory}/ugene/output
	tar -cvf feUgeneReport$5.tar.gz $5/
	
	if [ $? -ne 0 ]
   	then
    	echo "FAILED - Could not copy UGENE output to ${clinical3PO.app.dataDirectory}/ugene/output/$5. Exiting."
	 	cd ${clinical3PO.hadoop.shellscripts.dir}/lib
		java -ea -cp "*" org.clinical3PO.services.JobStatusUpdate $5 FAIL
    	exit
   	fi
	
   	## This is required to update DB on completion of job.
   	cat $3/$2/fasta/*.fa > $3/$4
fi

# Update job status to success
cd ${clinical3PO.hadoop.shellscripts.dir}/lib
java -ea -cp "*" org.clinical3PO.services.JobStatusUpdate $5 SUCCESS
