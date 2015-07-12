#!/usr/bin/env bash
# run with first argument = class
# THIS VERSION USES THE NEW HADOOP CONFIGURATION METHOD INSTEAD OF THE OLD COMMAND LINE ARGUMENTS
echo using class \"$1\"
cd ${clinical3PO.hadoop.shellscripts.dir}/lib/
hadoop jar FExtract.jar -D c3fe.inputdir=input -D c3fe.outputdir=output -D c3fe.starttime=00:00 -D c3fe.endtime=17:00 -D c3fe.classproperty=$1 -D c3fe.classtime=33:00 -D c3fe.filterconfig=filterconfig1.txt -D c3fe.feconfig=basicFEConfig.txt

exit $?