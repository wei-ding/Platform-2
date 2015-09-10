#!/usr/bin/env bash
# run with first argument = class
# THIS VERSION USES THE NEW HADOOP CONFIGURATION METHOD INSTEAD OF THE OLD COMMAND LINE ARGUMENTS
echo using class \"$1\"
cd ${clinical3PO.hadoop.shellscripts.dir}/lib/
hadoop jar clinical3PO-FExtract-1.0.0-SNAPSHOT-jar-with-dependencies.jar org.clinical3PO.learn.main.FEMain \
-D c3fe.inputdir=${hadoop.file.observationFile} -D c3fe.outputdir=output \
-D c3fe.starttime=00:00 -D c3fe.endtime=17:00 -D c3fe.classproperty=$1 -D c3fe.classtime=33:00 \
-D c3fe.filterconfig=${fe.file.filter.config} -D c3fe.feconfig=${fe.file.config} \
-D c3fe.noOfReducers=${fe.number.reducers} -D c3fe.conceptFile=${hadoop.file.conceptFile}

exit $?