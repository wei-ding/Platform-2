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

## mimic2 to omop using Kettle/Pentaho ## ----

About Pentaho:
	Pentaho's Big Data story revolves around Pentaho Data Integration AKA Kettle. Kettle is a powerful Extraction, Transformation and Loading (ETL) engine that uses a metadata-driven approach. The kettle engine provides data services for, and is embedded in, most of the applications within the Pentaho BI suite. 
		- Spoon is a kettle desktop visual design tool used to create and edit ETL ransformations and jobs. http://wiki.pentaho.com/display/EAI/.01+Introduction+to+Spoon#.01IntroductiontoSpoon-UserInterfaceOverview

Spoon Installation:   
    - Install the Sun Microsystems Java Runtime Environment version 1.5 or higher. You can download a JRE for free at http://www.javasoft.com/.
    - Download the pentaho Data Integration - pdi-ce-6.0.1.0-stable from : http://community.pentaho.com/
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
	
Look over the files: ETLfrommimic2toomopforGit.ktr and 

## Execute R script through ETL penatho/Kettle -- ETLRscriptJobForGit.kjb

--There is step called "R script executor" to excute an R script from within a PDI transformation. The R script Executor step is already installed in PDI(Pentaho Data Integration). The following is the link which gives instructions to configure the environment to use the step. This step is only available in Pentaho Enterprise Edition of PDI. It is not available in Community edition.

http://wiki.pentaho.com/display/EAI/R+script+executor

--Here we are using Community Edition to run the transformations because it is open source. 

There is an alternative step to execute the R script in ETL through job entry named called "Shell".

To view the "Shell" step we need to create the Job through spoon File -> New -> Job.

--The following are the steps to execute the R script and keep the output in Hadoop environment.

1. To start the Job we need to use the START from General -> START in design section.

2. After that our actual job starts. Drag the Shell from Scripting -> Shell. Right click on the shell step and then go to Edit option. We can see the window called Execute shell script with general and script tabs. In the General section there is an option called Insert script- if we check that option we can use the script tab to write the script with in the Execute shell script window and the script file name option will disable. Here I checked the insert script option. In the Working directory option we need to give the path where our R code is resides. Here I used the R code which is available in https://github.com/Clinical3PO/Stage/blob/master/dev/clinical3PO/utilities/Other-Projects/R_Package/ObservationFileCreation/createObservation.R. In the script tab I added following line of code Rscript createObservation.R. Here I used windows machine to run it. That is the reason I installed Cygwin. It provides the linux like environment for windows to run the shell scripts. Linux users can directly to run it. Connect the hop between START and Shell. Here is the Link for Shell Documentation http://wiki.pentaho.com/display/EAI/Shell

3. Step 1 and step 2 will generate the output from R script according to location specified in the Rscript. 

4. Copy Files step is used to copy files or folders from source to destination environment. Drag the copy files step after the Shell. Connect hop between Shell and Copy Files. Right on the Copy Files. Click on Edit to set the sourde and destination environments and File/Folder names. Here is the Link for Copy Files Documentation http://wiki.pentaho.com/display/EAI/Copy+Files

5. Move Files step is used to move the Files/Folder to alternate locations. Drag the Move Files after the Copy Files. When we click on the Edit in Move Files it will give Move Files window with General, Destination File and Advanced tabs. In the General tab we need to specify the File/Folder source and destination source. In the Destination File tab we need to check the Destination is a File, Specify Date time format and Add date before extension options. In the date time format I chose yyyyMMddHHmmss and in the destination file exists I chose Overwrite the destination file option. Connect the hop between Copy Files and Move Files step. Here is the Link for Move Files Documentation http://wiki.pentaho.com/display/EAI/Move+files

6. Hadoop Copy Files is used to copy files in a Hadoop cluster from one location to another. Drag the Hadoop Copy Files after the Move Files. Connect the Hop between Move Files and Hadoop Copy Files. When we click on the Edit in Move Files it will give Hadoop Copy Files window with Files and settings tabs. In the Files tab we need to give the source,destination environment and source, destination Files/Folder location. In the settings tab I checked the  remove source Files option. Here is the documentation for http://wiki.pentaho.com/display/EAI/Hadoop+Copy+Files.







