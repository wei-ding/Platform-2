#!/bin/sh

# Provide the input here
localFilePath=/home/hadoop/Desktop/Clinical3PO-Docs/InputFiles
hdfsFilePath=/home/hadoop/logs
hdfsFileName=testFile3.txt
hdfsRoot=$hdfsFilePath/$hdfsFileName

#Creating a file in hdfs destination
hdfs dfs -touchz $hdfsRoot

# Change the type of file, if not .txt
for i in $localFilePath/*.txt
   do
		echo "uploading file "$i" started at " `date`

		# Upload & Append command into hadoop
		hdfs dfs -appendToFile $i $hdfsRoot

		if [ $? -eq 0 ]
			then
			echo "Uploading file "$i" completed at " `date`

			# Removing file after upload
			/bin/rm "$i"
		else
			echo "Uploading of file $i failed at " `date`
		fi
done
