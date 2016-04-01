# mimic2 to omop v4 etl #

## mimic2 demo dataset download webpage ##
https://physionet.org/mimic2/demo/
download: mimic2_flatfiles.tar.gz
https://physionet.org/mimic2/demo/mimic2_flatfiles.tar.gz

``` sh
$ for i in *.tar.gz; do echo working on $i; tar xvzf $i ; done
```

merge all the small files

## Build & run ##

```sh
$ cd mimic2
$ chmod u+x sbt
$ ./sbt
> +run
```

## Package ##

```sh
$ ./sbt package
```

## Contact ##

- Clinical3PO

## mimic2 to omop using Kettle/Pentaho ##

About Pentaho:
	Pentaho's Big Data story revolves around Pentaho Data Integration AKA Kettle. Kettle is a powerful Extraction, Transformation and Loading (ETL) engine that uses a metadata-driven approach. The kettle engine provides data services for, and is embedded in, most of the applications within the Pentaho BI suite. 
		- Spoon is a kettle desktop visual design tool used to create and edit ETL ransformations and jobs. http://wiki.pentaho.com/display/EAI/.01+Introduction+to+Spoon#.01IntroductiontoSpoon-UserInterfaceOverview

Spoon Installation:   
    - Install the Sun Microsystems Java Runtime Environment version 1.5 or higher. You can download a JRE for free at http://www.javasoft.com/.
    - Download the pentaho Data Integration - pdi-ce-5.0.1.A-stable from : https://sourceforge.net/projects/pentaho/files/Data%20Integration/5.0.1-stable/
	- Inorder to start the spoon tool we need to unzip the data integration folder which is downloaded and start the spoon.bat for windows and spoon.sh for Unix like platforms like Linux, Apple OSX, Solaris.
 
User Interface Overview:
     The Main tree in the upper-left panel of Spoon allows you to browse connections associated with the jobs and transformations you have open. When designing a transformation, the Core Objects palate in the lower left-panel contains the available steps used to build your transformation including input, output, lookup, transform, joins, scripting steps and more.
	 
Here we are transforming into mimic2 dataset to omop .
	 
Create the Tranformation and step:
    - Once tool is started, we need to create the tansformation for our mimic2 to omop conversion. Go to File -> New -> Transformation. We need to named it has by double click the drag and drop area. It will give Tanformation    properties window to change the transformation name.
	
    - Once the transformation is created, drag the table input from the Design -> Input -> table input into the tranformation area. When right click on the step got to edit option. It will give Table input window to set the step name and connection name. In the step name we need to give the name related to table name. For the connection name, if we dont have any other connection then we need to create new connection by clicking on the New button besides the connection textbox. It will open the database connection window. In this window we nned to give connection name, select PostgreSQL for connection Type, Native(JDBC) for Access. In the settings section we need to give hostname where the mimic2 dataset is in. Give the Database Name, User Name and password.Once we entered all the information, we can check the connection is working or not by clicking test button. Once we created the connection is succesfull click Ok, it will bring back to the table input area. Copy the SQL statement related to particular table by using https://github.com/Clinical3PO/Stage/tree/master/dev/etl/mimic2. If we want to check the data the table by clicking preview button in the table input window.
	
    - Here We have millions of records in tables, that is the reason we are using hadoop file output stpe in the Big Data section in the left panel. Drag and Drop the Hadoop file output step into the tranformation area. Right click on the hadoop file output it will give hadoop file output window. Here we can see File, Content, Fields tabs. In the File tab we need to assign the step name, at the Hadoop Cluster click on New it will give Hadoop cluster window. In the Hadoop cluster window given cluster name according to available clusters. In the HDFS section we need to give hostname, username and password. In the Job tracker section we need to give Jobtracker hostname. In the ZooKeeper section we need to give hostname. In the Oozie section we need to give URL of the Oozie. Once we entered everything we can check the connetion by click on text button.Once connection is sucesfull click Ok to go back to the Hadoop File output window. In the Folder/File textbox we need to enter where we wants to store the transforming table.
	
	- We need to create hop between the Table input and Hadoop file outpt inorder connect the two steps by use the left mouse while pressing the SHIFT key from input to output.
	
	- We need to do the above steps for all the tables.
	
	- Once we are done with above steps click Run to run this tranformation.
