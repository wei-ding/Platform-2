/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.clinical3PO.omopfilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Date;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import static org.clinical3PO.omopfilter.OMOPTime.convertOMOPTime;

/**
 *
 * @author hadoop
 */
public class OMOPFilter extends Configured implements Tool {
    
    // REQUIRED parameters
    private String  c3poInp="";   
    private boolean c3poFoundInp=false;
    private String  c3poOut="";
    private boolean c3poFoundOut=false;
    
    // OPTIONAL parameters
    private String  c3poConfigFile="";
    private boolean c3poFoundConfigFile=false;
    private String  c3poUserFile="";
    private boolean c3poFoundUserFile=false;
    private String  c3poRangeFile="";
    private boolean c3poFoundRangeFile=false;
    
    private String  c3poGlobalStartTime="";
    private boolean c3poFoundGlobalStartTime=false;
    private String  c3poGlobalEndTime="";
    private boolean c3poFoundGlobalEndTime=false;
    private String  c3poDelimCSV="";
    private boolean c3poFoundDelimCSV=false;
    
    
    @Override
    public int run(String[] args) throws Exception{
        
        // Determine + print Start Job
        Date dateStart=new Date();
        System.out.println(" MapReduce Filtering started at " + 
                            dateStart.toString());
        
        // Parse the Command Line INPUT
        parseCmdLine(args);
        
        // Parse INPUT added by the -D option
        Configuration conf=getConf();
        parseConfig(conf);
        
        // Print Summary of INPUT Variables
        printInputParam();
        
        // Add OPTIONAL Variables to the configuration
        setConfParameters(conf);
        
        // Check if inp, out & concept files exist
        FileSystem fs = FileSystem.get(conf);
        checkExistenceOfFiles(fs);
        
        // Retrieve Data from config, user or range file
        if(c3poFoundConfigFile)
           retrieveDataFromConfigFile(conf);
        if(c3poFoundUserFile)
           retrieveDataFromUserFile(conf);
        if(c3poFoundRangeFile)
           retrieveDataFromRangeFile(conf);
        
        
        // Create a Job object
        Job job = new Job(conf);
        job.setJobName("OMOP Filter");
        job.setJarByClass(OMOPFilter.class);
        job.setMapperClass(OMOPFilterMapper.class);
       
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        // Define the input and output dirs
        FileInputFormat.addInputPath(job, new Path(this.c3poInp));
        FileOutputFormat.setOutputPath(job, new Path(this.c3poOut));
 
        // Determine + print End Job       
        Date dateEnd=new Date();
        System.out.println(" OMOP Filter finished at " + 
                            dateEnd.toString());
        return job.waitForCompletion(true) ? 0 : 1;
  
    }
    
    /**
     * Private method to parse the command line
     * @param args 
     */
    private void parseCmdLine(String[] args)
    {
        // Print-out command Line      
        for(int i=0; i < args.length; i++)
        {
            if(i==0)
                System.out.print(" Command Line Args:"+ args[0]+" ");
            else
                System.out.print(args[i]+ " ");
        }    
        System.out.print("\n");
        
        
        System.out.println(" *** Parsing the command line ***");
        if(args.length==0)
           System.out.println("     No input arguments");    
        
        int i=0;
        while(i < args.length)
        {
            
            if(args[i].equals("-inp")) {
                System.out.printf("     Keyword '-inp' has been found\n");
                i++;
                if(i<args.length)
                {   
                    c3poInp=args[i].trim();
                    System.out.printf("     Input file/dir:'%s'\n",c3poInp);
                    c3poFoundInp=true;
                    i++;
                }
                else
                {    
                    System.err.print("     Input file/dir::missing");
                    printInputOptions();
                    System.exit(-1);
                }         
            }
            else if(args[i].equals("-out")) {
                System.out.printf("     Keyword '-out' has been found\n");
                i++;
                if(i<args.length)
                {   
                    c3poOut=args[i].trim();
                    c3poFoundOut=true;
                    System.out.printf("     Output dir:'%s'\n",c3poOut);
                    i++;
                }
                else
                {    
                    System.err.print("     Output dir:: missing");
                    printInputOptions();
                    System.exit(-1);
                }    
            }
            else if(args[i].equals("-config")) {
                System.out.printf("     Keyword '-config' has been found\n");
                i++;
                if(i<args.length)
                {   
                    c3poConfigFile=args[i].trim();
                    c3poFoundConfigFile=true;
                    System.out.printf("     Restrict to config. from::'%s'\n",c3poConfigFile);
                    i++;
                }
                else
                {    
                    System.err.print("     File w. configs::missing");
                    printInputOptions();
                    System.exit(-1);
                }    
            } 
            else if(args[i].equals("-user")) {
                System.out.printf("     Keyword '-user' has been found\n");
                i++;
                if(i<args.length)
                {   
                    c3poUserFile=args[i].trim();
                    c3poFoundUserFile=true;
                    System.out.printf("     Restrict to userids from:'%s'\n",c3poUserFile);
                    i++;
                }
                else
                {    
                    System.err.print("     File w. userids::missing");
                    printInputOptions();
                    System.exit(-1);
                }    
            }
            else if(args[i].equals("-range")) {
                System.out.printf("     Keyword '-range' has been found\n");
                i++;
                if(i<args.length)
                {   
                    c3poRangeFile=args[i].trim();
                    c3poFoundRangeFile=true;
                    System.out.printf("     Restrict to ranges from:'%s'\n",c3poRangeFile);
                    i++;
                }
                else
                {    
                    System.err.print("     File w. ranges::missing");
                    printInputOptions();
                    System.exit(-1);
                }    
            }
            else if(args[i].equals("-start")) {
                System.out.printf("     Keyword '-start' has been found\n");
                i++;
                if(i<args.length)
                {   
                    c3poGlobalStartTime=args[i].trim();
                    c3poFoundGlobalStartTime=true;
                    System.out.printf("     Global Start Time (config.):'%s'\n",c3poGlobalStartTime);
                    i++;
                }
                else
                {    
                    System.err.print("     Global Start Time::missing");
                    printInputOptions();
                    System.exit(-1);
                }    
            }
            else if(args[i].equals("-end")) {
                System.out.printf("     Keyword '-end' has been found\n");
                i++;
                if(i<args.length)
                {   
                    c3poGlobalEndTime=args[i].trim();
                    c3poFoundGlobalEndTime=true;
                    System.out.printf("     Global End Time:'%s'\n",c3poGlobalEndTime);
                    i++;
                }
                else
                {    
                    System.err.print("     Global End Time::missing");
                    printInputOptions();
                    System.exit(-1);
                }    
            }
           
            else if(args[i].equals("-delimcsv")) {
                System.out.printf("     Keyword '-delimcsv' has been found\n");
                i++;
                if(i<args.length)
                {   
                    c3poDelimCSV=args[i].trim();
                    c3poFoundDelimCSV=true;
                    System.out.printf("     DelimCSV:'%s'\n",c3poDelimCSV);
                    i++;
                }
                else 
                {    
                    System.err.print("     Delimiter CSV character::missing");
                    printInputOptions();
                    System.exit(-1);
                }    
            }
            else if(args[i].equals("-help") || args[i].equals("-h"))
            {
                System.out.printf("     Invoking the 'help' command\n");
                printInputOptions();
                System.exit(-1);
 
            }    
            else {
                System.out.printf("     Unknown keyword:'%s'\n",args[i].trim());
                printInputOptions();
                System.exit(-1);
            }
        }    
        return; 
    } 
    
    // Private method to parse the Command Line
    private void parseConfig(Configuration conf)
    {
        System.out.println(" *** Parsing the configuration ***");
        //for (Entry<String,String> entry : conf)
        //    System.out.printf("%s=%s\n",entry.getKey(),entry.getValue());
       
        // Check if the REQUIRED variables are defined
        if(c3poFoundInp==false)
        {    
            c3poInp=conf.get("c3poInp");
            if(c3poInp != null)
            {
                System.out.printf("     Input file/dir:'%s'\n", c3poInp);
                c3poFoundInp=true; 
            } 
            else {
                System.err.println("     The input file/dir has:\n" + 
                "       neither been specified on the command line " +
                "using the option '-inp <input>'\n" +
                "       nor by using the configuration parameter: " +
                "'-D c3poInp=<input>'");
                printInputOptions();
                System.exit(-1);
            }
        } 
        
        if(c3poFoundOut==false)
        {    
            this.c3poOut=conf.get("c3poOut");
            if(c3poOut != null)
            {
                System.out.printf("     Output dir:'%s'\n",c3poOut);
                c3poFoundOut=true; 
            } 
            else {
                System.err.println("     The output dir has:\n" + 
                "       neither been specified on the command line " +
                "using the option '-out <outputdir>'\n" +
                "       nor by using the configuration parameter: " +
                "'-D c3poOut=<outputdir>'");
                printInputOptions();
                System.exit(-1);
            }
        } 
              
        // Check if the OPTIONAL variables are redefined
        if(c3poFoundConfigFile==false)
        {
            c3poConfigFile=conf.get("c3poConfig");
            if(c3poConfigFile != null)
            {
                System.out.printf("     Restrict to config. from:'%s'\n",c3poConfigFile);
                c3poFoundConfigFile=true;
            }
            else
                c3poConfigFile="";
        }
        if(c3poFoundUserFile==false)
        {
            c3poUserFile=conf.get("c3poUser");
            if(c3poUserFile != null)
            {
                System.out.printf("     Restrict to userids from:'%s'\n",c3poUserFile);
                c3poFoundUserFile=true;
            }
            else
                c3poUserFile="";
        }    
        if(c3poFoundRangeFile==false)
        {
            c3poRangeFile=conf.get("c3poRange");
            if(c3poRangeFile != null)
            {
                    
                System.out.printf("     Restrict to ranges from:'%s'\n",c3poRangeFile);
                c3poFoundRangeFile=true;
            }
            else
                c3poRangeFile="";
        }
        if(c3poFoundGlobalStartTime==false)
        {    
           c3poGlobalStartTime=conf.get("c3poStart");
           if(c3poGlobalStartTime != null)
            {
                    
                System.out.printf("     Global Start Time:'%s'\n",c3poGlobalStartTime);
                c3poFoundGlobalStartTime=true;
            }
            else
                c3poGlobalStartTime="";
        }
        if(c3poFoundGlobalEndTime==false)
        {    
           c3poGlobalEndTime=conf.get("c3poEnd");
           if(c3poGlobalEndTime != null)
            {
                    
                System.out.printf("     Global End Time:'%s'\n",c3poGlobalEndTime);
                c3poFoundGlobalEndTime=true;
            }
            else
                c3poGlobalEndTime="";
        }
        if(this.c3poFoundDelimCSV==false)
        {
            this.c3poDelimCSV=conf.get("c3poDelimCSV",",");
            System.out.printf("     CSV Delimiter:'%s'\n",this.c3poDelimCSV);
            this.c3poFoundDelimCSV=true;
        }  
        return;
    }  
    
    /**
     * Method to print out the input options
     *    either on the command line 
     *    or by setting the configuration parameters
     */
    private void printInputOptions()
    {
        System.out.println("\n     THEREFORE:");
        System.out.println("     One can use a combination of command line");
        System.out.println("         AND configuration options");
        System.out.println("     The input, output file/dir are REQUIRED;");
        System.out.println("         the remaining options are OPTIONAL");
        System.out.println("     The OPTIONAL parameters have DEFAULT ([]) values\n");
        
        System.out.println("     COMMAND LINE options ***");
        System.out.println("       -inp <inputfile>");
        System.out.println("       -out <outputdir>");
        System.out.println("       -config <configfile> [ALL CONFIGS]");
        System.out.println("          -start <global_start_time>  [none]");
        System.out.println("          -end   <global_end_time>    [none]");
        System.out.println("       -user <userfile>     [ALL USERS]");
        System.out.println("       -range <rangefile>   [ALL VALUES]");
        System.out.println("       -delimcsv <char>     [,]");
        
        System.out.println("     CONFIGURATIONS options ***");
        System.out.println("       -D c3poInp=<inputfile/inpudir>");
        System.out.println("       -D c3poOut=<outputdir>");
        System.out.println("       -D c3poConfig=<configfile> [ALL VALUES]");
        System.out.println("           -D c3poStart=<global_start_time>   [none]");
        System.out.println("           -D c3poEnd=<global_end_time>       [none]");
        System.out.println("       -D c3poUser=<userfile>     [ALL VALUES]");
        System.out.println("       -D c3poRange=<rangefile>   [ALL VALUES]");
        System.out.println("       -D c3poDelimCSV=\"<char>\" [\",\"]\n");
        
        return;
    }   
    
    /**
     * Method to print the input parameters
     */
    private void printInputParam()
    {
        System.out.printf(" *** Print out the start options ***\n");
        System.out.printf("     Input file/dir:'%s'\n",c3poInp);
        System.out.printf("     Output dir    :'%s'\n",c3poOut);
        if(c3poFoundConfigFile)
            System.out.printf("     File w. configurations:'%s'\n",c3poConfigFile);
        else
            System.out.printf("     NO restriction imposed on the configurations\n");
        
        if(c3poFoundUserFile)
            System.out.printf("     File w. userids:'%s'\n",c3poUserFile);
        else
            System.out.printf("     NO restriction imposed on the userids\n");
        
        if(c3poFoundRangeFile)
            System.out.printf("     File w. meas. ranges:'%s'\n",c3poRangeFile);
        else
            System.out.printf("     N0 restriction imposed on the range values\n");
            
        if(c3poFoundGlobalStartTime && c3poFoundGlobalEndTime)
            System.out.printf("     Global Time Constraints: keep only iff \\in [%s -- %s[\n", 
                                    c3poGlobalStartTime,c3poGlobalEndTime);
        else if(!(c3poFoundGlobalStartTime) && !(c3poFoundGlobalEndTime))
            System.out.printf("     No Global Time Constraints imposed\n");
        else
        {
            System.out.printf("     The global start time & global end time must be:\n");
            System.out.printf("         -either provided at the same time\n");
            System.out.printf("         -or omitted at the same time\n");
            
        }
        System.out.printf("     CSV delimiter :'%s'\n",this.c3poDelimCSV);
        return;
    }
    
    /**
     * Method to set the O
     * to transfer to the Mapper class
     * i.e. StartTime
     *      EndTime
     *      CSV Delimiter
     * 
    */
    private void setConfParameters(Configuration conf)
    {
        // Set OPTIONAL Variables 
        conf.setBoolean("foundConfigFile", c3poFoundConfigFile);
        conf.setBoolean("foundUserFile", c3poFoundUserFile);
        conf.setBoolean("foundRangeFile", c3poFoundRangeFile);
        
        if (c3poFoundGlobalStartTime && c3poFoundGlobalEndTime)
        {    
            conf.setBoolean("foundGlobalTime", c3poFoundGlobalStartTime);
            conf.setInt("globalStartTime",convertOMOPTime(c3poGlobalStartTime));
            conf.setInt("globalEndTime",convertOMOPTime(c3poGlobalEndTime));
        }    
        
        // CSV
        conf.set("DelimCSV",this.c3poDelimCSV);
        
        // Sets the delimiter to the above for the key/value write out
        conf.set("mapreduce.output.textoutputformat.separator",this.c3poDelimCSV);
        return;
    }
    
    /**
     * Method to check if:
     *   a.the input file/dir exists
     *   b.the output directory exists.
     *     In case the output directory exists, it will be removed
     *   c.the file with the concepts
     */
    private void checkExistenceOfFiles(FileSystem fs) throws IOException
    {
       
       System.out.println(" *** Check the existence of files ***");
       // Check if Input file/dir exists
       if(!fs.exists(new Path(c3poInp) )) 
       {    
          System.out.println("     Input file/dir '" + c3poInp + "' doesn't exist");
          System.exit(1);
       }
       System.out.println("     Input file/dir '" + c3poInp + "' exist");
       
       
       // Remove OUTPUT directory if it exists. 
       if(fs.exists(new Path(c3poOut)))
       {
          System.out.println("     Output dir '" + c3poOut + "' already exists");
          fs.delete(new Path(c3poOut),true);
          System.out.println("     Output dir '" + c3poOut + "' has been removed");
       }
       
       if(c3poFoundConfigFile)
       {
          if(!fs.exists(new Path(c3poConfigFile)))
          {    
             System.out.println("     Config file '" + c3poConfigFile + "' doesn't exist");
             System.exit(1);
          }
          System.out.println("     Config file '" + c3poConfigFile + "' exist");  
       }    
       
       if(c3poFoundUserFile)
       {
          if(!fs.exists(new Path(c3poUserFile)))
          {    
             System.out.println("     User file '" + c3poUserFile + "' doesn't exist");
             System.exit(1);
          }
          System.out.println("     User file '" + c3poUserFile + "' exist");  
       }
       
       if(c3poFoundRangeFile)
       {
          if(!fs.exists(new Path(c3poRangeFile)))
          {    
             System.out.println("     Range file '" + c3poRangeFile + "' doesn't exist");
             System.exit(1);
          }
          System.out.println("     Range file '" + c3poRangeFile + "' exist");  
       }
       return;   
    }
    
    /**
     * Method to extract data from the configFile
     * The config file contains lines
     *     either lines with ONE element i.e. the concept_name
     *     or lines with THREE elements: i.e. concept_name,start_time,end_time
     *
     * Each line is parsed using the Apache CSV module.
     * @param  conf: Configuration
     * @return void
     */
    private void retrieveDataFromConfigFile(Configuration conf) throws IOException
    {
        FileSystem fs = FileSystem.get(conf);
        FSDataInputStream inFSDataStream=null;
        
        try{
            
            inFSDataStream=fs.open(new Path(c3poConfigFile));
            BufferedReader br=new BufferedReader(new InputStreamReader(inFSDataStream));
            
            String line;
            String configString="";
            boolean foundLocalTime=false;
            String localStartTimeString="";
            String localEndTimeString="";
            
            CSVRecord csvRecord;
            char charDelimCSV=this.c3poDelimCSV.charAt(0);
            
            System.out.printf("\n     Start reading %s\n",this.c3poConfigFile);
            // Read file
            while ((line = br.readLine()) != null) {
          
                Reader in = new StringReader(line);
                CSVParser parser= new CSVParser(in,CSVFormat.newFormat(charDelimCSV));
                List<CSVRecord> arrList= parser.getRecords();
                
                csvRecord=arrList.get(0);
                
                if(csvRecord.size() == 1)
                   configString += (csvRecord.get(0).trim() +"\n");
                else if(csvRecord.size() == 3 ) {
                   foundLocalTime=true;
                   configString += (csvRecord.get(0).trim().toLowerCase() +"\n");
                   localStartTimeString += (Integer.toString(convertOMOPTime(csvRecord.get(1))) + "\n");
                   localEndTimeString += (Integer.toString(convertOMOPTime(csvRecord.get(2))) + "\n");
                }   
                else
                {
                   System.out.printf("The config file '%s' requires lines with:\n",
                                     this.c3poConfigFile);
                   System.out.println("    either 1 parameter i.e. concept_name");
                   System.out.println("    or 3 parameters i.e. concept_name,start_time,end_time");
                   System.exit(-1);
                }
            }
            inFSDataStream.close(); // Close input FS Data Stream
            conf.set("configArray",configString);
            conf.setBoolean("foundLocalTime", foundLocalTime);
            conf.set("localStartTimeArray",localStartTimeString);
            conf.set("localEndTimeArray",localEndTimeString);
        }
        catch(IOException e)
        {
              System.err.println("Caught IOException: " + e.getMessage());
              System.exit(1);
        }   
        System.out.printf("     End reading %s\n", this.c3poConfigFile);
        return; 
    }   
    
    /**
     * Method to extract from the userFile 
     * those concepts which are to be withheld
     * Each line is parsed using the Apache CSV module.
     * @param  conf: Configuration
     * @return void
     */
    private void retrieveDataFromUserFile(Configuration conf) throws IOException
    {
        FileSystem fs = FileSystem.get(conf);
        FSDataInputStream inFSDataStream=null;
        
        try{
            
            inFSDataStream=fs.open(new Path(c3poUserFile));
            BufferedReader br=new BufferedReader(new InputStreamReader(inFSDataStream));
            
            String line;
            String userString="";
            
            CSVRecord csvRecord;
            char charDelimCSV=this.c3poDelimCSV.charAt(0);
            
            System.out.printf("\n     Start reading '%s'\n",c3poUserFile);
            // Read file
            while ((line = br.readLine()) != null) {
          
                Reader in = new StringReader(line);
                CSVParser parser= new CSVParser(in,CSVFormat.newFormat(charDelimCSV));
                List<CSVRecord> arrList= parser.getRecords();
                csvRecord=arrList.get(0);
                
                userString += (csvRecord.get(0).trim() +"\n");
                
            }
            inFSDataStream.close(); // Close input FS Data Stream
            conf.set("userArray",userString);   
        }
        catch(IOException e)
        {
              System.err.println("Caught IOException: " + e.getMessage());
              System.exit(1);
        }   
        System.out.printf("     End reading '%s'\n",c3poUserFile);
        return; 
    }   
    
    /**
     * Method to extract from the userFile 
     * those concepts which are to be withheld
     * Each line is parsed using the Apache CSV module.
     * @param  conf: Configuration
     * @return void
     */
    private void retrieveDataFromRangeFile(Configuration conf) throws IOException
    {
        FileSystem fs = FileSystem.get(conf);
        FSDataInputStream inFSDataStream=null;
        
        try{
            
            inFSDataStream=fs.open(new Path(c3poRangeFile));
            BufferedReader br=new BufferedReader(new InputStreamReader(inFSDataStream));
            
            String line;
            String rangeString="";
            String lowRangeString="";
            String upperRangeString="";
            
            CSVRecord csvRecord;
            char charDelimCSV=this.c3poDelimCSV.charAt(0);
            
            System.out.printf("\n     Start reading '%s'\n",this.c3poRangeFile);
            // Read file
            while ((line = br.readLine()) != null) {
          
                Reader in = new StringReader(line);
                CSVParser parser= new CSVParser(in,CSVFormat.newFormat(charDelimCSV));
                List<CSVRecord> arrList= parser.getRecords();
                csvRecord=arrList.get(0);
                
                rangeString += (csvRecord.get(0).trim() +"\n");
                lowRangeString += (csvRecord.get(1).trim() +"\n");
                upperRangeString +=  (csvRecord.get(2).trim() +"\n");
            }
            inFSDataStream.close(); // Close input FS Data Stream
            conf.set("rangeArray",rangeString);
            conf.set("lowRangeArray",lowRangeString);
            conf.set("upperRangeArray",upperRangeString);
        }
        catch(IOException e)
        {
              System.err.println("Caught IOException: " + e.getMessage());
              System.exit(1);
        }   
        System.out.printf("     Stop reading '%s'\n",this.c3poRangeFile);
        return; 
    }    
    
    public static void main (String[] args) throws Exception {
        
        // Generic command-line options are handled by ToolRunner
        int exitCode=ToolRunner.run(new Configuration(), new OMOPFilter(),args);
        System.exit(exitCode);
    }
}      

