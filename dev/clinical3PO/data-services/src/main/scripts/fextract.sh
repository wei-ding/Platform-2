#!/usr/bin/env bash
# run with first argument = class
# THIS VERSION USES THE NEW HADOOP CONFIGURATION METHOD INSTEAD OF THE OLD COMMAND LINE ARGUMENTS

echo "FExtract.sh execution started."
echo using class: \"$1\"
echo Arff Write Path: \"$2\"
echo Class Time: \"$3\"
echo Start Time: \"$4\"
echo End Time: \"$5\"

cd ${clinical3PO.hadoop.shellscripts.dir}/lib/
hadoop jar clinical3PO-FExtract-1.0.0-SNAPSHOT-jar-with-dependencies.jar org.clinical3PO.learn.main.FEMain \
-D c3fe.inputdir=${fe.file.data} -D c3fe.outputdir=output -D c3fe.incpid=true \
-D c3fe.starttime=$4 -D c3fe.endtime=$5 -D c3fe.classproperty=$1 -D c3fe.classtime=$3 \
-D c3fe.filterconfig=${fe.file.filter.config} -D c3fe.feconfig=${fe.file.config} \
-D c3fe.noOfReducers=${fe.number.reducers} -D c3fe.conceptFile=${hadoop.file.conceptFile} \
-D c3fe.outputdir=$2 -D c3fe.arffname=$2

exit $?