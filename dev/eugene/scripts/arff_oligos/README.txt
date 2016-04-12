Following scripts are used while generating ARFF from FASTA files using Ugene.
   --> generateARFF.sh - This is called from the main c3po hadoop script 
   --> runUgeneAppOligos.py -  This will run Ugene individually for each patient in Oligos file and compared against all patients in fasta file
   --> calculateOligiosDistance.py - This will calculate distance matrix from the output files which are generated from Ugene.
   --> createARFF.py - This will generate ARFF files from distance matrices

Following are prerequisites to generate ARFF from FASTA files

* Oligo file has to be created for each attribute like glasgow.txt/glucose.txt 
* Create corresponding substitution matrix for each attribute and keep this file in "$UGENE_HOME/data/weight_matrix"
* Oligos files path and FASTA files path has to be given as input args to generateARFF.sh script

