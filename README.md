## Clinical Personalized Pragmatic Predictions of Outcomes (Clinical3PO)


### Platform - Visualization

The current **Visualization** component of Clinical Personalized Pragmatic Predictions of Outcomes (Clinical3PO) provides functionality to search electronic health records (EHR) and visually display the searched data using data drive documents (D3JS). A query browser interface is provided so that the user can search for patients and their attributes.

The application interfaces with Hadoop (hdfs), Accumulo, Hive, and R. Machine learning is through [ML-Flex framework] (http://mlflex.sourceforge.net/)

The document briefly describes the setup instructions

### Architecture

![Alt Text](/dev/clinical3PO/utilities/build/markdown/images/Architecture.png?raw=true "Architecture")

### Installation Instructions

#### Software Installations

1. Install java/jdk, set appropriate variables (JAVA_HOME, PATH), 1.6 is the least version that is required.
2. Set PATH to have hadoop binaries (so that hadoop related commands can be executed).
3. Install apache maven, for installation instructions look at the MAVEN section.
4. Install apache tomcat, for installation instructions look at the Tomcat section.
5. Requires Firefox browser.
6. CLASSPATH variable should NOT be set (at least anything related to clinical3po project).

##### Maven

1. Download apache [maven](http://maven.apache.org/download.cgi) , 3.0 is the least version that is required.
2. Extract the binary to a folder, update the PATH variable to include bin directory from the extracted folder. Create an environment variable M2_HOME which points to the extracted folder.

##### Tomcat

1. Download the [tomcat installer] (http://www.bizdirusa.com/mirrors/apache/tomcat/tomcat-7/v7.0.53/bin/apache-tomcat-7.0.53.tar.gz)
2. Extract to a folder, update the PATH variable to include bin directory from the extracted folder.
3. Create an environment variable CATALINA_HOME which points to the extracted folder.
4. catalina.sh should be used to start/stop the server.

#### Database Setup

1. Create the database.
2. Execute the scripts located in utilities/db-objects. Read Readme.txt to know the order of execution of  the sql scripts.
3. The same database entries (username,password…should be entered in settings.xml).

### Application Setup

1. Check out the source.
2. Copy the profile segment from README.txt which is under utilities/build/maven (project source) to settings.xml which is present under conf (apache maven).
3. Modify the values appropriately to suite the target environment.
4. Compile and Build the source using mvn clean install -DskipTests –Penv-properties (at the project root folder). The clinical3PO war will be created under app/target
5. Copy the clinical3PO.war to webapps (under apache tomcat).
6. Start the catalina, catalina.sh start (it is assumed that bin directory of apache tomcat is in PATH).Check logs (under apache tomcat) for any issues.
7. Application can be accessed through http://<targetmachine>:8080/clinical3PO.
8. Username/Password can be found in utilities/db-objects/data.sql.
9. Create respective directories for the entries given in <clinical3PO.logging.file>,<clinical3PO.app.dataDirectory>,<clinical3PO.hadoop.shellscripts.dir>,<clinical3PO.hadoop.localOutput.dir>,<clinical3PO.mlflex.directory>.

#### Data Services

To invoke search (patient, observation), the data services should be built accordingly. 

1. When any patient id is given for search, application in turn calls a shell script that executes a hadoop job, this project deals with the source code pertaining to this activity.
2. Change directory to data-services, execute mvn clean assembly:assembly -Dbinary=true -DskipTests -Penv-properties, a binary with a name clinical3PO-data-services-1.0.0-SNAPSHOT-bin.tar is created under target directory.
3. Extract this tar archive under a specified directory, whatever the directory that was given against **&lt;clinical3PO.hadoop.shellscripts.dir&gt;** in settings.xml.

    > for example we are saying the relevant scripts (hadoop related) and data services are stored under /home/c3po/c3po-hadoop-scripts.<br>
        <!-- Directory where shell scripts related to hadoop search are stored --> <br>
        &lt;clinical3PO.hadoop.shellscripts.dir&gt;**/home/c3po/clinical3PO-hadoop-scripts**&lt;/clinical3PO.hadoop.shellscripts.dir&gt;
        
#### ML Flex

1. Download the [ML-Flex framework] (http://mlflex.sourceforge.net/).
2. Ensure to extract to the directory that is configured under &lt;clinical3PO.mlflex.directory&gt;&lt;/clinical3PO.mlflex.directory&gt;
<br><br>
