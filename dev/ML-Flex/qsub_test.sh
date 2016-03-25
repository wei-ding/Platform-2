#!/usr/bin/env bash
#/*
# *	 Copyright 2016 Clinical3PO/dingw
# *
# *   Licensed under the Apache License, Version 2.0 (the "License");
# *   you may not use this file except in compliance with the License.
# *   You may obtain a copy of the License at
# *
# *       http://www.apache.org/licenses/LICENSE-2.0
# *
# *   Unless required by applicable law or agreed to in writing, software
# *   distributed under the License is distributed on an "AS IS" BASIS,
# *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# *   See the License for the specific language governing permissions and
# *   limitations under the License.
# */
#PBS -j oe
# OPENMPI will use torque-native mechainsms to launch and kill processes
# This will run the 2 MPI processes on the nodes that were allocated by Torque
# shell$ qsub -I -lnodes=2
# shell$ mpirun java ...
# or
# qsub -l nodes=2 qsub_test.sh
# or 
#  echo "export PBS_O_WORKDIR=/home/hdfs/codebase/Stage/dev/ML-Flex && java -jar $project_home/mlflex.jar EXPERIMENT_FILE=$project_home/FEMFlexdiasabp.txt LEARNER_TEMPLATES_FILE=$project_home/Config/Learner_Templates.txt CLASSIFICATION_ALGORITHMS_FILE=$project_home/Config/Classification_Algorithms.txt FEATURE_SELECTION_ALGORITHMS_FILE=$project_home/Config/Feature_Selection_Algorithms.txt NUM_THREADS=2 THREAD_TIMEOUT_MINUTES=10 PAUSE_SECONDS=60 DEBUG=true ACTION=Process" | qsub -l nodes=2

#export PBS_O_WORKDIR=/home/hdfs/codebase/Stage/dev/ML-Flex
# If PBS_O_WORKDIR not set, set it to $PWD
if [[ -z "$PBS_O_WORKDIR" ]]; then
    export PBS_O_WORKDIR=$PWD
fi

echo "Running on: " 
cat ${PBS_NODEFILE} 

echo 
echo "Program Output begins: " 

# Fix PBS_O_WORKDIR path to absolute path and cd to it
export PBS_O_WORKDIR=$(readlink -f $PBS_O_WORKDIR)
cd $PBS_O_WORKDIR

echo "WORK Dir is: " 
echo ${PBS_O_WORKDIR} 

#
# Run the tests
#

project_home=/home/hdfs/codebase/Stage/dev/ML-Flex
echo "Project home is: " 
echo ${project_home} 


java -jar $project_home/mlflex.jar EXPERIMENT_FILE=$project_home/FEMFlexdiasabp.txt NUM_THREADS=2 THREAD_TIMEOUT_MINUTES=10 PAUSE_SECONDS=60 DEBUG=true ACTION=Reset 

#java -jar $project_home/mlflex.jar LEARNER_TEMPLATES_FILE=$project_home/Config/Learner_Templates.txt CLASSIFICATION_ALGORITHMS_FILE=$project_home/Config/Classification_Algorithms.txt FEATURE_SELECTION_ALGORITHMS_FILE=$project_home/Config/Feature_Selection_Algorithms.txt EXPERIMENT_FILE=$project_home/FEMFlexdiasabp.txt NUM_THREADS=2 THREAD_TIMEOUT_MINUTES=10 PAUSE_SECONDS=10 DEBUG=true EXPERIMENT_FILE=$project_home/FEMFlexdiasabp.txt ACTION=Process > $project_home/qsub_mpi_test.log 2>&1 &

#mpirun java  -jar $project_home/mlflex.jar -cp $project_home/weka.jar weka.classifiers.bayes.NaiveBayes LEARNER_TEMPLATES_FILE=$project_home/Config/Learner_Templates.txt CLASSIFICATION_ALGORITHMS_FILE=$project_home/Config/Classification_Algorithms.txt FEATURE_SELECTION_ALGORITHMS_FILE=$project_home/Config/Feature_Selection_Algorithms.txt NUM_THREADS=2 THREAD_TIMEOUT_MINUTES=10 PAUSE_SECONDS=60 DEBUG=true EXPERIMENT_FILE=$project_home/FEMFlexdiasabp.txt  ACTION=Process


java  -jar $project_home/mlflex.jar -cp $project_home/weka.jar weka.classifiers.bayes.NaiveBayes LEARNER_TEMPLATES_FILE=$project_home/Config/Learner_Templates.txt CLASSIFICATION_ALGORITHMS_FILE=$project_home/Config/Classification_Algorithms.txt FEATURE_SELECTION_ALGORITHMS_FILE=$project_home/Config/Feature_Selection_Algorithms.txt NUM_THREADS=2 THREAD_TIMEOUT_MINUTES=10 PAUSE_SECONDS=60 DEBUG=true EXPERIMENT_FILE=$project_home/FEMFlexdiasabp.txt  ACTION=Process


