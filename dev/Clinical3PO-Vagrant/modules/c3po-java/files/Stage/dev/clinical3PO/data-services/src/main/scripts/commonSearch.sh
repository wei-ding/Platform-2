#!/bin/sh

if [ ! -d "${clinical3PO.hadoop.localOutput.dir}/logs" ]; then
  mkdir -p ${clinical3PO.hadoop.localOutput.dir}/logs
fi

${clinical3PO.hadoop.shellscripts.dir}/commonSearchWithRedirectedIO.sh $* > ${clinical3PO.hadoop.localOutput.dir}/logs/$4.log 2>&1

