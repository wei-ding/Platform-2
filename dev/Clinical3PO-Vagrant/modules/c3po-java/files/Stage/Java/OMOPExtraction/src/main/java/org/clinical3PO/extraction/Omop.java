/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.clinical3PO.extraction;
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


/**
 *  Note: 
 *   a. What has to be changed in the Short Term
 *      Command Line: -DreferenceDate (default 01-Jan-2013)
 *                    -DreferenceTime (default 00:00:00 )
 *                    -DdelimiterCVS     (default ",") 
 *                    --help
 *      Use a Apache CVS function to Parse the Strings
 * 
 *   b. In the next revision use a MapReduce Join to 
 *      treat the concept file.
 * 
 * @author hadoop
 */

public class Omop extends Configured implements Tool {
    
    private String  c3poInput="";   
    private boolean c3poFoundInput=false;
    private String  c3poOutput="";
    private boolean c3poFoundOutput=false;
    private String  c3poConceptFile="";
    private boolean c3poFoundConcept=false;
    private String  c3poReferenceDate="";
    private boolean c3poFoundReferenceDate=false;
    private String  c3poReferenceTime="";
    private boolean c3poFoundReferenceTime=false;
    private String  c3poDelimCSV="";
    private boolean c3poFoundDelimCSV=false;
    
    @Override
    public int run(String[] args) throws Exception{
        
        // Determine + print Start Job
        Date dateStart=new Date();
        System.out.println(" MapReduce on OMOP data started at " + 
                            dateStart.toString());
        
        // Parse the Command Line INPUT
        parseCmdLine(args);
        
        // Parse INPUT added by the -D option
        Configuration conf=getConf();
        parseConfig(conf);
        
        // Print Summary of INPUT Variables
        printInputParam();
        
        // Add OPTIONAL Variables to the configuration
        setConfiguration(conf);
        
        // Check if inp, out & concept files exist
        FileSystem fs = FileSystem.get(conf);
        checkExistenceOfFiles(fs);
        
        // Retrieve Data from the concept file 
        // && add them to the Configuration
        retrieveDataFromConceptFile(conf);
        
        // Create a Job object
        Job job = new Job(conf);
        job.setJobName("Omop Extraction");
        job.setJarByClass(Omop.class);
        job.setMapperClass(OmopMapper.class);
        job.setReducerClass(OmopReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        // Define the input and output dirs
        FileInputFormat.addInputPath(job, new Path(this.c3poInput));
        FileOutputFormat.setOutputPath(job, new Path(this.c3poOutput));
 
        // Determine + print End Job       
        Date dateEnd=new Date();
        System.out.println(" MapReduce on OMOP data finished at " + 
                            dateEnd.toString());
        return job.waitForCompletion(true) ? 0 : 1;
    }
    
    // Private method to parse the Command Line
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
                    c3poInput=args[i].trim();
                    System.out.printf("     Input file/dir:'%s'\n",c3poInput);
                    c3poFoundInput=true;
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
                    c3poOutput=args[i].trim();
                    c3poFoundOutput=true;
                    System.out.printf("     Output dir:'%s'\n",c3poOutput);
                    i++;
                }
                else
                {    
                    System.err.print("     Output dir:: missing");
                    printInputOptions();
                    System.exit(-1);
                }    
            }
            else if(args[i].equals("-concept")) {
                System.out.printf("     Keyword '-concept' has been found\n");
                i++;
                if(i<args.length)
                {   
                    c3poConceptFile=args[i].trim();
                    c3poFoundConcept=true;
                    System.out.printf("     Concept File:'%s'\n",c3poConceptFile);
                    i++;
                }
                else
                {    
                    System.err.print("     Concept File::missing");
                    printInputOptions();
                    System.exit(-1);
                }    
            } 
            else if(args[i].equals("-refdate")) {
                System.out.printf("     Keyword '-refDate' has been found\n");
                i++;
                if(i<args.length)
                {   
                    c3poReferenceDate=args[i].trim();
                    c3poFoundReferenceDate=true;
                    System.out.printf("     ReferenceDate:'%s'\n",c3poReferenceDate);
                    i++;
                }
                else
                {    
                    System.err.print("     Reference Date::missing");
                    printInputOptions();
                    System.exit(-1);
                }    
            }
            else if(args[i].equals("-reftime")) {
                System.out.printf("     Keyword '-reftime' has been found\n");
                i++;
                if(i<args.length)
                {   
                    c3poReferenceTime=args[i].trim();
                    c3poFoundReferenceTime=true;
                    System.out.printf("     ReferenceTime:'%s'\n",c3poReferenceTime);
                    i++;
                }
                else
                {    
                    System.err.print("     Reference Time::missing");
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
        if(c3poFoundInput==false)
        {    
            c3poInput=conf.get("c3poInput");
            if(this.c3poInput != null)
            {
                System.out.printf("     Input file/dir:'%s'\n", c3poInput);
                c3poFoundInput=true; 
            } 
            else {
                System.err.println("     The input file/dir has:\n" + 
                "       neither been specified on the command line " +
                "using the option '-inp <input>'\n" +
                "       nor by using the configuration parameter: " +
                "'-D c3poInput=<input>'");
                printInputOptions();
                System.exit(-1);
            }
        } 
        
        if(c3poFoundOutput==false)
        {    
            this.c3poOutput=conf.get("c3poOutput");
            if(c3poOutput != null)
            {
                System.out.printf("     Output dir:'%s'\n",c3poOutput);
                c3poFoundOutput=true; 
            } 
            else {
                System.err.println("     The output dir has:\n" + 
                "       neither been specified on the command line " +
                "using the option '-out <outputdir>'\n" +
                "       nor by using the configuration parameter: " +
                "'-D c3poOutput=<outputdir>'");
                printInputOptions();
                System.exit(-1);
            }
        } 
        
        if(c3poFoundConcept==false)
        {    
            c3poConceptFile=conf.get("c3poConcept");
            if(c3poConceptFile != null)
            {
                System.out.printf("     Concept File:'%s'\n",c3poConceptFile);
                c3poFoundConcept=true; 
            } 
            else {
                System.err.println("     The concept file has:\n" + 
                "       neither been specified on the command line " +
                "using the option '-concept <conceptFile>'\n" +
                "       nor by using the configuration parameter: " +
                "'-D c3poConcept=<conceptFile>'");
                printInputOptions();
                System.exit(-1);
            }
        } 
        
        // Check if the OPTIONAL variables are redefined
        if(c3poFoundReferenceDate==false)
        {
            c3poReferenceDate=conf.get("c3poRefDate","01-Jan-2013");
            System.out.printf("     Reference Date:'%s'\n",c3poReferenceDate);
            c3poFoundReferenceDate=true;
        }    
        
        if(this.c3poFoundReferenceTime==false)
        {
            this.c3poReferenceTime=conf.get("c3poRefTime","00:00:00");
            System.out.printf("     Reference Time:'%s'\n",this.c3poReferenceTime);
            this.c3poFoundReferenceTime=true;
        }  
        
        if(this.c3poFoundDelimCSV==false)
        {
            this.c3poDelimCSV=conf.get("c3poDelimCSV",",");
            System.out.printf("     CSV Delimiter:'%s'\n",this.c3poDelimCSV);
            this.c3poFoundDelimCSV=true;
        }  
        return;
    }  
    
    private void printInputOptions()
    {
        System.out.println("\n     THEREFORE:");
        System.out.println("     One can use a combination of configuration");
        System.out.println("         AND command line options");
        System.out.println("     The input, output and concept file are REQUIRED;");
        System.out.println("         the remaining options are OPTIONAL");
        System.out.println("     The OPTIONAL parameters have DEFAULT ([]) values\n");
        
        System.out.println("     CONFIGURATIONS options ***");
        System.out.println("       -D c3poInput=<input>");
        System.out.println("       -D c3poOutput=<output>");
        System.out.println("       -D c3poConcept=<concept>");
        System.out.println("       -D c3poRefDate=<refdate> [01-Jan-2013]");
        System.out.println("       -D c3poRefTime=<reftime> [00:00:00]");
        System.out.println("       -D c3poDelimCSV=\"<char>\" [\",\"]\n");
        
        System.out.println("     COMMAND LINE options ***");
        System.out.println("       -inp <input>");
        System.out.println("       -out <output>");
        System.out.println("       -concept <conceptfile>");
        System.out.println("       -refdate <refdate> [01-Jan-2013]");
        System.out.println("       -reftime <reftime> [00:00:00]");
        System.out.println("       -delimcsv <char>   [,]");
        
        return;
    }        
    
    private void printInputParam()
    {
        System.out.printf(" *** Print out the start options ***\n");
        System.out.printf("     Input file/dir:'%s'\n",this.c3poInput);
        System.out.printf("     Output dir    :'%s'\n",this.c3poOutput);
        System.out.printf("     Concept file  :'%s'\n",this.c3poConceptFile);
        System.out.printf("     Reference date:'%s'\n",this.c3poReferenceDate);
        System.out.printf("     Reference time:'%s'\n", this.c3poReferenceTime);
        System.out.printf("     CSV delimiter :'%s'\n",this.c3poDelimCSV);
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
       // Check whether INPUT dir exists; otherwise => exit
       Path inputDir, outputDir, conceptFile;
       
       System.out.println(" *** Check the existence of files ***");
       inputDir=new Path(this.c3poInput);
       if(!fs.exists(inputDir)) 
       {    
          System.out.println("     Input dir '" + inputDir + "' doesn't exist");
          System.exit(1);
       }
       System.out.println("     Input dir '" + inputDir + "' exist");
       
        // Remove OUTPUT directory if it exists.
       outputDir=new Path(this.c3poOutput); 
       if(fs.exists(outputDir))
       {
          System.out.println("     Output dir '" + outputDir + "' already exists");
          fs.delete(outputDir,true);
          System.out.println("     Output dir '" + outputDir + "' has been removed");
       }
       
       // Check whether CONCEPT file exists; otherwise => exit
       conceptFile=new Path(this.c3poConceptFile);
       if((!fs.exists(conceptFile)) || (!fs.isFile(conceptFile)))
       {   
          System.out.println("     The file with the concepts '" + conceptFile + 
                  "' either doesn't exist or is a directory ");
          System.exit(1);
       }
       System.out.println("     Concept file '" + conceptFile + "' exist");
       return;   
    }
    
    /**
     * Method to extract the concept Names & concept IDs 
     * from the conceptFile
     * Each line is parsed using the Apache CSV module.
     * @return null
     */
    private void retrieveDataFromConceptFile(Configuration conf) throws IOException
    {
        FileSystem fs = FileSystem.get(conf);
        FSDataInputStream inFSDataStream=null;
        
        try{
            
            inFSDataStream=fs.open(new Path(this.c3poConceptFile));
            BufferedReader br=new BufferedReader(new InputStreamReader(inFSDataStream));
            
            String line;
            String conceptIDs="";
            String conceptNames="";
            
            CSVRecord csvRecord;
            char charDelimCSV=this.c3poDelimCSV.charAt(0);
            
            while ((line = br.readLine()) != null) {
          
                Reader in = new StringReader(line);
                CSVParser parser= new CSVParser(in,CSVFormat.newFormat(charDelimCSV));
                List<CSVRecord> arrList= parser.getRecords();
                csvRecord=arrList.get(0);
                
                if(csvRecord.get(0).trim().equals("CONCEPT"))
                {    
                   conceptIDs   += (csvRecord.get(1).trim() + "\n");
                   conceptNames += (csvRecord.get(2).trim().toLowerCase() + "\n");
                }     
            }
            inFSDataStream.close(); // Close input FS Data Stream
            conf.set("conceptIDs",conceptIDs);
            conf.set("conceptNames",conceptNames);   
        }
        catch(IOException e)
        {
              System.err.println("Caught IOException: " + e.getMessage());
              System.exit(1);
        }   
        return; 
    }    
    
    private void setConfiguration(Configuration conf)
    {
        // Set OPTIONAL Variables
        conf.set("ReferenceDate", this.c3poReferenceDate);
        conf.set("ReferenceTime",this.c3poReferenceTime);
        conf.set("DelimCSV",this.c3poDelimCSV);
        // Sets the delimiter to the above for the key/value write out
        conf.set("mapreduce.output.textoutputformat.separator",this.c3poDelimCSV);
        return;
    }
    
    public static void main (String[] args) throws Exception {
        
        // Generic command-line options are handled by ToolRunner
        int exitCode=ToolRunner.run(new Configuration(), new Omop(),args);
        System.exit(exitCode);
    }
            
}
