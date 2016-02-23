/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.clinical3PO.extraction;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.fieldsel.FieldSelectionMapper;

/**
 *
 * @author hadoop
 */
public class OmopMapper
      extends Mapper<Object, Text, Text, Text>
{
    private String referenceDate;                    // Reference Date
    private String referenceTime;                    // Reference Time
    private char delimCSV;                           // CSV Delimiter
    final private String PATTERN_DATE="dd-MMM-yyyy"; // Pattern Date
    private long timeInMsStart;
    private Hashtable<Integer,String> conceptIDString
     = new Hashtable<Integer,String>();
    
    /**
     * Method to setup/initialize the mapper.
     * @param context
     * @throws IOException
     * @throws InterruptedException 
     */
    @Override
    protected void setup(Context context) throws 
              IOException, InterruptedException {

        Configuration conf=context.getConfiguration();
        
        System.out.println(" *** Setup Mapper ***");
        // Calculate the Start Time
        calcStartTime(conf);
        System.out.println("     ReferenceDate::'" + referenceDate +"'");
        System.out.println("     ReferenceTime::'" + referenceTime +"'");
        System.out.println("     Start time/ms::" + timeInMsStart);
        
        // Retrieve the CONCEPT data
        retrieveConceptData(conf);
        System.out.println("     Concepts:");
        for(int keyVal: conceptIDString.keySet() )
            System.out.println("       " + keyVal + " :'" + conceptIDString.get(keyVal) +"'");
        
        // Extract the DELIMITER
        extractDelimCSV(conf);
        System.out.println("     Delimiter CSV:'" + this.delimCSV + "'"); 
        return;
    }
    
    /**
     * Method to retrieve the Start Time in ms
     * @param conf 
     */
    private void calcStartTime(Configuration conf)
    {
        
        // Get the OPTIONAL time variables
        referenceDate=conf.get("ReferenceDate");
        referenceTime=conf.get("ReferenceTime");
       
        // Determine Calculate longInMsStart
        timeInMsStart=0;
        
        //  1.Date PART
        try{
            Date date= new SimpleDateFormat(PATTERN_DATE,Locale.US).parse(referenceDate);
            timeInMsStart=date.getTime();
        } 
        catch(ParseException e){
            System.err.println(e);
            System.exit(-1);
        }      
        
        //  2.Time PART
        String[] wordsHMS=referenceTime.trim().split(":");
        if(wordsHMS.length!=3)
        {    
            System.err.print(" The string TMD '" + referenceTime + "'" +
                             " has the WRONG format.\n" +
                             " Req. Format:'HH:MM:SS' ");
            System.exit(-1);
        }
        // Convert HH:MM:SS into Ms
        long numMsTime=(Integer.parseInt(wordsHMS[0].trim()) * 3600 +
                        Integer.parseInt(wordsHMS[1].trim()) * 60 +
                        Integer.parseInt(wordsHMS[2].trim())
                       ) * 1000;
        timeInMsStart+=numMsTime;
        return;
    }        
    
    /**
     * Method to retrieve the data from the Configuration object
     * 
     * @param conf 
     */
    private void retrieveConceptData(Configuration conf)
    {
        
        String conceptIDs=conf.get("conceptIDs");
        String conceptNames=conf.get("conceptNames");
        String[] conceptIDsWords = null;
        String[] conceptNamesWords = null;
        
        // Split conceptIDs into words
        if(conceptIDs != null)    
           conceptIDsWords=conceptIDs.trim().split("\n");
        else
        {
           System.err.println("Mapper::setup conceptIDs is null");
           System.exit(1);
        }
        
        // Split conceptNames into words
        if(conceptNames != null) 
           conceptNamesWords=conceptNames.trim().split("\n");
        else
        {
           System.err.println("Mapper::setup conceptNames is null");
           System.exit(1);        
        }   
        
        // Add entries to HashTable -> lowercase
        for(int i=0; i< conceptIDsWords.length ; i++)
            conceptIDString.put(Integer.parseInt(conceptIDsWords[i]), 
                                conceptNamesWords[i].toLowerCase());
        
        return;
    }        
    
    /**
     * Extract the delimCSV from the Configuration
     * @param conf 
     */
    private void extractDelimCSV(Configuration conf)
    {
        String delim=conf.get("DelimCSV");
        this.delimCSV=delim.charAt(0);
        return;
    }
    
    /**
     * Mapper method
     * Input::
     *   OBSERVATION,12159675,132539,45,01-Jan-2013,00:00:00,54.000  
     *        0          1       2    3     4           5        6
     * Output::
     *   Key  : concept
     *   Value: person_id  time  val
     * 
     * @param key
     * @param value
     * @param context
     * @throws IOException
     * @throws InterruptedException 
     */
    @Override
    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException
    {
        CSVRecord csvRecord;
        Reader in = new StringReader(value.toString().trim());
        CSVParser parser= new CSVParser(in,CSVFormat.newFormat(this.delimCSV));
        List<CSVRecord> arrList= parser.getRecords();
        csvRecord=arrList.get(0);
        
        if(csvRecord.get(0).trim().equals("OBSERVATION"))
        {
           String timeVal;
           try {
                timeVal = TimeConversion.convertTimeToString(timeInMsStart, 
                              csvRecord.get(4).trim(), csvRecord.get(5).trim());
                String keyStr=conceptIDString.get(Integer.parseInt(csvRecord.get(3).trim()));
                String res = csvRecord.get(2).trim();
                res += this.delimCSV + timeVal + this.delimCSV+csvRecord.get(6).trim();
                context.write(new Text(keyStr), new Text(res));
                return;
            } catch (ParseException ex) {
                Logger.getLogger(OmopMapper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return;
    }
}
