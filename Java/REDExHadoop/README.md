## REDExHadoop
Hadoop MapReduce instrumentation for REDEx

### To build
* build the uber-jar:

> mvn assembly:single

resulting jar will be named something like REDExHadoop-<version>-jar-with-dependencies.jar

### To run
(examples are for the hortonworks sandbox)

* copy the uber-jar to the hadoop machine.

> scp -P 2222 REDExHadoop-<version>-jar-with-dependencies.jar root@127.0.0.1:

* login to the hadoop machine.

> ssh -p 2222 root@127.0.0.1

* execute the hadoop job. REDExHadoop expects 3 arguments, <redex-model-file> <input file directory> <output file directory>

> hadoop jar REDExHadoop-<version>-jar-with-dependencies.jar hdfs:/user/root/redex-pain.model in out




