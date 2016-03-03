purpose: is to run ml-flex application & Check the accuracy levels. Will run from SHELL SCRIPT.

Replace < > with appropriate paths & values

How to run:
	-- ./run.sh <ml-flex directory path> <O/P directory to place Output (Accuray) file> <.arff file path> <no of iterations in numeric> <no of folds in numeric>
	
	Example: ./run.sh /home/hdfs/ML-Flex /home/hdfs/temp/ml-flex-shell-script-execution /home/hdfs/c3po-dist/clinical3PO-app-data/mlflex/data/CrossValidationTime20160127061348224Output.arff 5 3