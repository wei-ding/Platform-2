/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.clinical3PO.omopfilter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import static org.clinical3PO.omopfilter.OMOPTime.convertOMOPTime;

/**
 * @author hadoop
 */
public class OMOPFilterMapper
       extends Mapper<Object, Text, Text, Text>
{
    private Boolean foundConfigFile=false;
    private Boolean foundUserFile=false;
    private Boolean foundRangeFile=false;
    private Boolean foundGlobalTime=false;
    private Boolean foundLocalTime=false;
    
    private String userString="";
    private String configString="";
    private String localStartTimeString="";
    private String localEndTimeString="";
    private String conceptRangeString="";
    private String lowRangeString="";
    private String upperRangeString="";
    private int  globalStartTime=0;
    private int  globalEndTime=0;
    private char delimCSV;
    
    // Constraints on the Users
    private ArrayList<String> userArr=new ArrayList<String>();
    
    // Constraints on the Configurations (i.e. Concepts)
    private ArrayList<String> configArr=new ArrayList<String>();
    private ArrayList<Integer> localStartTimeArr=new ArrayList<Integer>();
    private ArrayList<Integer> localEndTimeArr=new ArrayList<Integer>();
    
    // Constraints on the measured values
    private ArrayList<String> conceptRangeArr=new ArrayList<String>();
    private ArrayList<Double> lowRangeArr=new ArrayList<Double>();
    private ArrayList<Double> upperRangeArr=new ArrayList<Double>();
    
           
    @Override
    protected void setup(Context context) 
               throws IOException, InterruptedException {
        Configuration conf=context.getConfiguration();
    
        System.out.println(" *** Setup Mapper ***");
        // Get the Content of the files if they exist
        retrieveUserIDs(conf);
        retrieveConfigs(conf);
        retrieveRanges(conf);
        
        // Get the Global StartTime & endTime
        retrieveTimes(conf);
        
        // Extract the DELIMITER
        extractDelimCSV(conf);
        
        // Print-out parameters
        printSelCriteria(); 
        return;
    }
    
     /**
     * Method to extract the userids from a string 
     * if they have been provided previously
     * @param conf 
     */
    private void retrieveUserIDs(Configuration conf)
    {
        foundUserFile=conf.getBoolean("foundUserFile",false);
        if(foundUserFile)
        {    
            userString=conf.get("userArray","");
            String[] propWords=userString.trim().split("\\s+");
            for(int i=0; i<propWords.length; i++)
                this.userArr.add(propWords[i]);
        } 
        return;
    }
    
    /**
     * Method to extract to extract the configurations 
     * and (if the local times have been provided) 
     * the local times as well.
     * @param conf 
     */
    private void  retrieveConfigs(Configuration conf)
    {
        foundConfigFile=conf.getBoolean("foundConfigFile",false);
        foundLocalTime=conf.getBoolean("foundLocalTime", false);
        if(foundConfigFile)
        {   
            configString=conf.get("configArray");
            String[] propWords=configString.trim().split("\\s+");
            for(int i=0; i<propWords.length; i++)
                this.configArr.add(propWords[i]);
        }    
        if(foundLocalTime)
        {
            String[] timeWords;
            localStartTimeString=conf.get("localStartTimeArray","");
            timeWords=localStartTimeString.trim().split("\\s+");
            for(int i=0; i<timeWords.length; i++)
                localStartTimeArr.add(Integer.valueOf(timeWords[i]));
            
            localEndTimeString=conf.get("localEndTimeArray","");
            timeWords=localEndTimeString.trim().split("\\s+");
            for(int i=0; i<timeWords.length; i++)
                localEndTimeArr.add(Integer.valueOf(timeWords[i]));
        }       
        return;
    }        
    
    /**
     * Method to extract the ranges from a string
     * @param conf 
     */
    private void retrieveRanges(Configuration conf)
    {
        foundRangeFile=conf.getBoolean("foundRangeFile",false);
        if(foundRangeFile)
        {
            String[] propWords;
            conceptRangeString=conf.get("rangeArray","");
            propWords=conceptRangeString.trim().split("\\s+");
            for(int i=0; i<propWords.length; i++)
                this.conceptRangeArr.add(propWords[i]);
            
            lowRangeString=conf.get("lowRangeArray","");
            propWords=lowRangeString.trim().split("\\s+");
            for(int i=0; i<propWords.length; i++)
                this.lowRangeArr.add(Double.valueOf(propWords[i]));
            
            upperRangeString=conf.get("upperRangeArray","");
            propWords=upperRangeString.trim().split("\\s+");
            for(int i=0; i<propWords.length; i++)
                this.upperRangeArr.add(Double.valueOf(propWords[i]));
        }     
        return;
    }
    
    /**
     * Method to extract the global start & end time from a string
     * @param conf 
     */
    private void retrieveTimes(Configuration conf)
    {
        foundGlobalTime=conf.getBoolean("foundGlobalTime", false);
        if(foundGlobalTime)
        {    
            globalStartTime=conf.getInt("globalStartTime",0);
            globalEndTime=conf.getInt("globalEndTime",0);
        }
        return;
    }        
    
    private void extractDelimCSV(Configuration conf)
    {
        String delim=conf.get("DelimCSV");
        this.delimCSV=delim.charAt(0);
        return;
    }
    
    /**
     * Print-out the selection criteria
     */
    private void printSelCriteria()
    {
        // Apply USERS CONSTRAINTS
        if(foundUserFile)
        {    
           System.out.printf("     Keep the following %d userids:\n",
                                               this.userArr.size());
           System.out.println("       " + userArr); 
        }   
        else
           System.out.printf("     All USERS will be withheld:\n");
        
        
        // Apply CONFIG CONSTRAINTS
        if(foundConfigFile)
        {  
           System.out.printf("     Keep the following %d config.:\n",
                                               this.configArr.size() );
           System.out.println("       " + configArr);  
        } 
        else
           System.out.printf("     All CONFIGURATIONS will be kept\n");
        
        
        // Apply TIME CONSTRAINTS 
        if(foundLocalTime)
        {
           System.out.printf("     Keep the following %d config. iff \\in TIME interval:\n",
                                   this.configArr.size() );
           for(int i=0; i< this.configArr.size(); i++)
               System.out.printf("       %15s  [%d -- %d[\n",this.configArr.get(i),
                                                           this.localStartTimeArr.get(i),
                                                           this.localEndTimeArr.get(i)); 
        }    
        else if(foundGlobalTime)
           System.out.printf("     Restrict config. iff \\in TIME interval:[%d -- %d[\n",
                                   globalStartTime,globalEndTime);
        else
           System.out.printf("     No TIME Constraints\n");
          
        
        // Apply RANGE CONSTRAINTS
        if(foundRangeFile)
        {    
           System.out.printf("     Keep the following %d props within these ranges:\n",
                                   this.conceptRangeArr.size());
           for(int i=0; i< this.conceptRangeArr.size(); i++)
               System.out.printf("       %15s   [%12.4f -- %12.4f[\n",
                       this.conceptRangeArr.get(i),
                       this.lowRangeArr.get(i),
                       this.upperRangeArr.get(i)); 
        }   
        else
           System.out.println("     No RANGE Constraints\n");
        return;
    }  
    
    /**
     * Method to copy a CSVRecord to an ArrayList
     * @param s
     * @return 
     */
    private static ArrayList<String> copyCSVRecordToArrayList(CSVRecord s)
    {
        ArrayList<String> res=new ArrayList<String>();
        for(int i=0; i< s.size(); i++)
            res.add(s.get(i).trim());
        return res;
    }  
    
    
    /**
     * Method to preserve ONLY those users which have 
     * been found in the userfile.
     * All the other users are discarded.
     * @param words 
     */
    private void filterUser(ArrayList<String> words)
    {      
        if( userArr.indexOf(words.get(0)) == -1)
            words.clear();
        return;
    }
    
    /**
     * Method to keep ONLY those configs which have 
     * been found in the configfile
     * All the OTHER configs will be discarded.  
     * @param words 
     */
    private void filterConfig(ArrayList<String> words)
    {
        if( this.configArr.indexOf(words.get(1).toLowerCase()) == -1)
            words.clear();
    }        
        
    /**
     * Method to preserve ONLY those (time,val) tuples 
     * for those configurations which:
     * a. are specified in configFile
     * b. where t \in [t0,t1[
     * @param words 
     */   
    private void filterLocalTime(ArrayList<String> words)
    {
        int ipos= this.configArr.indexOf(words.get(1).toLowerCase());
        if(ipos == -1)
        {
            System.out.printf("filterLocalTime: Concept '%s' not found!\n",
                               words.get(1));
            System.out.println("                Line:" + words.toString());
            System.exit(-1);
        }    
        // Find the Interval Limits for the concept
        int lowTime=localStartTimeArr.get(ipos);
        int upTime=localEndTimeArr.get(ipos);
        
        int startpos=words.size()-2;
        for(int i=startpos; i >=2 ; i-=2)
        {
            int timeVal=convertOMOPTime(words.get(i));
            if( (timeVal<lowTime) || (timeVal >= upTime))
            {
                words.remove(i+1);
                words.remove(i);
            }
        }
        
        // Remove the whole array if only the userid and concept are remaining
        if(words.size() == 2)
        { 
           //System.out.println("       ### DISCARD ### "+ words.get(0) + " "+words.get(1)); 
           words.clear();
        }
        return;
    }    
    
    /**
     * Method to KEEP only those (time,val) tuples 
     * where time \in [low,up[
     * @param words 
     */
    private void filterGlobalTime(ArrayList<String> words)
    {
        // Find the Interval Boundaries 
        int lowTime=this.globalStartTime;
        int upTime=this.globalEndTime;
        
        // Delete tuples outside the interval
        int startpos=words.size()-2;
        for(int i=startpos; i >=2 ; i-=2)
        {
            int timeVal=convertOMOPTime(words.get(i));
            if( (timeVal<lowTime) || (timeVal >= upTime))
            {
                words.remove(i+1);
                words.remove(i);
            }
        }
        
        // Delete Array iff no (time,val) tuples are left
        if(words.size() == 2)
           words.clear();
       
        return;
    }  
    
    
    /** 
     * Restrict the (time,values) tuples ONLY for those 
     * concepts \in rangeFile.
     * @param words 
     */
    private void filterRange(ArrayList<String> words)
    {
        int ipos=this.conceptRangeArr.indexOf(words.get(1).toLowerCase());
        if(ipos == -1)
            return;
        else
        {    
            double lowRange=this.lowRangeArr.get(ipos);
            double upperRange=this.upperRangeArr.get(ipos);
            
            // Delete ALL tuples outside the interval
            int startpos=words.size()-2;
            for(int i=startpos; i>=2 ; i-=2)
            {
                double measVal=Double.valueOf(words.get(i+1));
                if( (measVal< lowRange ) || (measVal >= upperRange))
                {
                   words.remove(i+1);
                   words.remove(i);
                }
            }
            
            // Delete Array iff no (time,val) tuples are left
            if(words.size() == 2)
                words.clear();
            
            return;
        }    
    }  
    
    
    @Override
    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException
    {
        CSVRecord csvRecord;
        Reader in = new StringReader(value.toString().trim());
        CSVParser parser= new CSVParser(in,CSVFormat.newFormat(this.delimCSV));
        List<CSVRecord> arrList= parser.getRecords();
        ArrayList<String> words;
        words=copyCSVRecordToArrayList(arrList.get(0));
        
        if(foundUserFile)
        {
            filterUser(words);
            if(words.size() == 0)
                return;
        }
        if(foundConfigFile)
        {
            filterConfig(words);
            if(words.size() == 0)
                return; 
        } 
        if(foundLocalTime)
        {
            filterLocalTime(words);
            if(words.size() == 0)
                return;
        } 
        else if(foundGlobalTime)
        {
            filterGlobalTime(words);
            if(words.size()==0)
                return;
        }
        if(foundRangeFile)
        {
            filterRange(words);
            if(words.size()==0)
                return;
        }    
            
        // Write-out
        if(words.size()>2)
        {   
            String res=words.get(1);
            for(int i=2 ; i<words.size();i+=2)
                res +=(this.delimCSV + words.get(i) + this.delimCSV + words.get(i+1) );
            context.write(new Text(words.get(0)),new Text(res));
        } 
        else
        {
            System.err.println("ERROR:: words >2 -> " + words.toString());
            System.exit(-1);
        }
        return;
    
    }
}
