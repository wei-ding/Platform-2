#!/bin/sh

if [ $# -ne 5 ]
then
    echo "Script is expecting 4 input parameters as follows... <path of mlflex directory> <output directory of accuracy files> <arff file path with Name> <number of iterations> <number of folds>"
    exit
fi

# use -A option declares associative array
declare -A array

array[One_Rule_Weka]=weka_one_r
array[Linear_SVM_Weka]=weka_svm_linear
array[Polynomial_SVM_Weka]=weka_svm_poly
array[SVM_using_RBF_kernel_Weka]=weka_svm_rbf
array[Naive_Bayes_Weka]=weka_naive_bayes
array[Conjuctive_Rule_Weka]=weka_conjunctive_rule
array[Decision_Tree_Weka]=weka_decision_tree
array[k_Nearest_Neighbours_Weka]=weka_knn
array[Bagging_Weka]=weka_bagging
array[Decision_Trees_learner_Quinlans_C.0]=c50
array[Naive_Bayes_Orange]=orange_naive_bayes
array[Decision_Tree_Orange]=orange_decision_tree
array[Linear_SVM_Orange]=orange_svm_linear
array[Polynomial_SVM_Orange]=orange_svm_poly
array[SVM_using_RBF_kernel_Orange]=orange_svm_rbf
array[SVM_using_RBF_kernel_R]=r_svm_rbf
array[Linear_SVM_R]=r_svm_linear
array[Polynomial_SVM_R]=r_svm_poly
array[Random]=random

echo "*************** ML-FLEX ALGORITHMS *****************"
echo " "
echo "         One_Rule_Weka"
echo "         Linear_SVM_Weka"
echo "         Polynomial_SVM_Weka"
echo "         SVM_using_RBF_kernel_Weka"
echo "         Naive_Bayes_Weka"
echo "         Conjuctive_Rule_Weka"
echo "         Decision_Tree_Weka"
echo "         k_Nearest_Neighbours_Weka"
echo "         Bagging_Weka"
echo "         Decision_Trees_learner_Quinlans_C.0"
echo "         Naive_Bayes_Orange"
echo "         Decision_Tree_Orange"
echo "         Linear_SVM_Orange"
echo "         Polynomial_SVM_Orange"
echo "         SVM_using_RBF_kernel_Orange"
echo "         SVM_using_RBF_kernel_R"
echo "         Linear_SVM_R"
echo "         Polynomial_SVM_R"
echo "         Random"
echo " "
echo "********************** END **************************"

# get inputs from console
echo " "
echo " Choose algorithm to run ML-FLEX -: " 
read algorithm

echo "Inputs provided are " 


echo "Inputs Provided are"
echo "          ML_FLEX Directory path-: " $1
echo "          Accuracy File O/P Directory-: " $2
echo "          Arff file path-: " $3
echo "          Number of Iterations-: " $4
echo "          Number of Folds-: " $5
echo "          Algorithm: " ${array[$algorithm]}
echo " "
# ml-flex jar path here
mlflex=$1

# output path of accuracy file
output=$2

# arff file path
arffPath=$3

# no of iterations
iterations=$4

# no of folds
folds=$5

# read arff file from console
#echo "Enter ARFF file path. example: /home/hdfs/filename.arff. "
#read arffPath

# creating experiment file with provided inputs
if [ -d "experimentfiles" ]
then
    echo "Directory already exists"
else
    mkdir experimentfiles
fi

timestamp(){
    date +%s
}

# writing into experiment file
timeSeconds=$( timestamp )
experiment="experiment_"$timeSeconds
experimentFile=$experiment".txt"
echo "DATA_PROCESSORS=mlflex.dataprocessors.ArffDataProcessor("\"$arffPath"\")" >>experimentfiles/$experimentFile
echo "CLASSIFICATION_ALGORITHMS="${array[$algorithm]} >> experimentfiles/$experimentFile
echo "NUM_OUTER_CROSS_VALIDATION_FOLDS="$folds >> experimentfiles/$experimentFile
echo "NUM_ITERATIONS="$iterations >> experimentfiles/$experimentFile

if [ $? -ne 0 ]
then
    echo "FAILED writing into experiment file."
    exit
fi

currentDir=$PWD

cd $mlflex
# executing ml-flex
java -Xmx1g -jar mlflex.jar EXPERIMENT_FILE=$currentDir/experimentfiles/$experimentFile NUM_THREADS=2 THREAD_TIMEOUT_MINUTES=10 PAUSE_SECONDS=60 ACTION=Process DEBUG=true

if [ $? -ne 0 ]
then
    echo "FAILED executing ml-flex."
    exit
fi

cd $currentDir
if [ -d "accuracyOutput" ]
then
    echo "folder exists"
else
    mkdir accuracyOutput
fi


# retrieving accuracy from the output generated
echo "" >> accuracyOutput/Accuracy_$timeSeconds
echo "Algorithm = "${array[$algorithm]} >> accuracyOutput/Accuracy_$timeSeconds
echo "Folds = "$folds >> accuracyOutput/Accuracy_$timeSeconds
echo "Iterations = "$iterations >> accuracyOutput/Accuracy_$timeSeconds
echo "" >> accuracyOutput/Accuracy_$timeSeconds
grep "Accuracy" $mlflex/Output/$experiment*/Results/Performance_Metrics.txt >> accuracyOutput/Accuracy_$timeSeconds

echo "" >> accuracyOutput/Accuracy_$timeSeconds
echo "" >> accuracyOutput/Accuracy_$timeSeconds
echo "Full path of output: "$mlflex/Output/$experiment* >> accuracyOutput/Accuracy_$timeSeconds
echo ""

if [ $? -ne 0 ]
then
    echo "FAILED grep command from $mlflex/Output/$experiment*/Results/Performance_Metrics.txt."
    exit
else 
    echo ""
    echo "####################################################"
    echo "#########      SUCCESFULLY COMPLETED       #########"
    echo "####################################################"
fi
