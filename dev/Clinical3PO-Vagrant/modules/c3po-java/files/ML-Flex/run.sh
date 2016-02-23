#!/bin/sh

java -Xmx1g -jar ml-flex-original-0.0.1-SNAPSHOT-jar-with-dependencies.jar EXPERIMENT_FILE=/home/hdfs/c3po-dist/clinical3PO-app-data/mlflex/experiments/FeatureExtractionTime20150929014453271Output.txt NUM_THREADS=2 THREAD_TIMEOUT_MINUTES=10 PAUSE_SECONDS=60 ACTION=Process DEBUG=true
