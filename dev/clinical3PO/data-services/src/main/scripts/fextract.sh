#!/usr/bin/env bash
# run with first argument = class
# THIS VERSION USES THE NEW HADOOP CONFIGURATION METHOD INSTEAD OF THE OLD COMMAND LINE ARGUMENTS

echo "FExtract.sh execution started."
echo using class: \"$1\"
echo Arff Write Path: \"$2\"
echo Class Bin Time: \"$3\"
echo Start Date: \"$4\"
echo End Date: \"$5\"
echo Start Time: \"$6\"
echo End Time: \"$7\"

cd ${clinical3PO.hadoop.shellscripts.dir}/lib/
hadoop jar clinical3PO-FExtract-1.0.0-SNAPSHOT-jar-with-dependencies.jar org.clinical3PO.learn.main.FEMain \
-D c3fe.inputdir=${fe.file.data} -D c3fe.outputdir=$2 -D c3fe.incpid=true \
-D c3fe.classproperty=$1 -D c3fe.arffname=$2 -D c3fe.classtime=$3 -D c3fe.startdate=$4 \
-D c3fe.starttime=$6 -D c3fe.enddate=$5 -D c3fe.endtime=$7 \
-D c3fe.filterconfig=file://${clinical3PO.hadoop.shellscripts.dir}/filterconfig1.txt \
-D c3fe.feconfig=file://${clinical3PO.hadoop.shellscripts.dir}/basicFEConfig.txt \
-D c3fe.noOfReducers=${fe.number.reducers} -D c3fe.conceptFile=${hadoop.file.conceptFile}

exit $?