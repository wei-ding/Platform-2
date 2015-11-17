FEATURE EXTRACTION(FE)

FE source(src) contain 2 packages. 
1) org.clinical3PO.learn.main
2) org.clinical3PO.learn.fasta

Consider these 2 packages as 2 modules.
MAIN:
The package main is all about generating ARFF from the given files. This is completed more or less with integration to web search.

FATA:
The package fasta contain code to generate FASTA file from ARFF. 

At present, there's no integration done between both the modules. But, can run the code by providing arff as input to get fasta.
NOTE: C3PO COMILE COMMANDS WOULD COMPILE THIS CODE AS WELL.

USE:
hadoop jar clinical3PO-FExtract-1.0.0-SNAPSHOT-jar-with-dependencies.jar org.clinical3PO.learn.fasta.ArffToFastADriver -D c3fe.inputdir=/user/hdfs/output/output.arff -D c3fe.outputdir=/user/hdfs/output/fasta file:///fastaDescreteProperties.txt

NOTE: copy the fastaDescreteProperties.txt(located in src/org/clinical3PO/learn/fasta/ArffToFastADriver) file to any local directory. Provide the path of same to above command.