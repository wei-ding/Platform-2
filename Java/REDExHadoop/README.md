## REDExHadoop
Hadoop MapReduce instrumentation for REDEx

### To build
* build the uber-jar:

> mvn clean install

resulting jar will be named something like REDExHadoop-<version>-jar-with-dependencies.jar

### File format
* Input files are expected to be clinical notes, one clinical note per file. The first line must be in the format <patient ID>|<document ID>|<document date>. The remainder of the file contains the body of the clinical note.
* 
### To run
(examples are for the hortonworks sandbox)

* copy the uber-jar to the hadoop machine.

> scp -P 2222 REDExHadoop-<version>-jar-with-dependencies.jar root@127.0.0.1:

* copy the REDEx model file to the hadoop machine.

> scp -P 2222 redex-pain.model root@127.0.0.1:

* login to the hadoop machine.

> ssh -p 2222 root@127.0.0.1

* copy the REDEx model file into hdfs.

> hdfs dfs -copyFromLocal redex-pain.model

* execute the hadoop job. REDExHadoop expects 4 arguments, <redex-model-file> <annotation type> <input file directory> <output file directory>

> hadoop jar REDExHadoop-<version>-jar-with-dependencies.jar hdfs:/user/root/redex-pain.model pain in out




