This project is to insert Data into Accumulo tables (Observation, Death, Concept) from HDFS:

Two steps to compile and build
-------------------------------
1. Edit run.sh(project root), input all the parameters(ipaddress, passwords, input/output directories) according to the destination machine and configuration.
NOTE: All the existing values configured are for testing, may not work in your case.
2. Execute mvn clean assembly:assembly -DskipTests from the root folder.

Steps to run the program
------------------------
1) Copy accumulo-load-mr-0.0.1-SNAPSHOT-bin.tar from target folder to the hdfs machine.
2) Untar the tar file.
3) Execute run.sh.
