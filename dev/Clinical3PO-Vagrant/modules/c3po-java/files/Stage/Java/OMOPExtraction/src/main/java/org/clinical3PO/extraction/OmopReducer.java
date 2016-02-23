/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.clinical3PO.extraction;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author hadoop
 */
public class OmopReducer
       extends Reducer<Text,Text,Text,Text> 
{
   private char delimCSV;  // CSV Delimiter
 
   /**
    * Method to setup the OmopReducer
    * @param context 
    */
   @Override
   protected void setup(Context context)
   {        
       Configuration conf=context.getConfiguration(); 
       System.out.println(" *** Setup Reducer ***");
       String delim=conf.get("DelimCSV");
       this.delimCSV=delim.charAt(0);
       System.out.println("     Delimiter CSV:'" + this.delimCSV + "'");
       return;
   }    
    
   /**
    * Proper Reducer
    * Input Data: key: string
    * 
    * @param key
    * @param iterValues
    * @param context
    * @throws IOException
    * @throws InterruptedException 
    */
   @Override
   // Input: key: string
   //        value: record_id time val1
   // Output: key: recordid
   //         value: string [time1 val1 ...]
   protected void reduce(Text key, Iterable<Text> iterValues,
                         Context context)
                  throws IOException,InterruptedException                    
   {  
       // List containing the RecordIDs
       ArrayList<String> lstRecordID=new ArrayList<String>();
       // Lists of lists containing the results
       ArrayList<ArrayList<String>> lstRes = 
            new ArrayList<ArrayList<String>>(); 
      
       // Loop over ALL lines with SAME Key=<PROPERTY>
       CSVRecord csvRecord;
       String currKey=key.toString();
       for(Text val : iterValues)
       {
           // Split current value=(recordID,timeVal,measVal) into words
           // System.out.println("val:"+val.toString());
           Reader in = new StringReader(val.toString().trim());
           CSVParser parser= new CSVParser(in,CSVFormat.newFormat(this.delimCSV));
           List<CSVRecord> arrList= parser.getRecords();
           csvRecord=arrList.get(0);
           
           String recordID=csvRecord.get(0).trim();
           String timeVal=csvRecord.get(1).trim();
           String measVal=csvRecord.get(2).trim();
         
           // Test if recordID \in lstRecordID
           int ipos=lstRecordID.indexOf(recordID);
           if(ipos == -1)
           {
               // Add recordID to lstRecordID
              lstRecordID.add(recordID);
              ipos=lstRecordID.indexOf(recordID);
              lstRes.add(new ArrayList<String>());
           }
  
           // Add (timeVal,measVal)
           lstRes.get(ipos).add(timeVal);
           lstRes.get(ipos).add(measVal);
        } // End for loop
      
        // Loop over ALL lstRes
        for(int irec=0; irec<lstRecordID.size(); irec++)
        {
            String valStr=key.toString();
            for(int k=0; k< lstRes.get(irec).size();k++)
                valStr+=( this.delimCSV + lstRes.get(irec).get(k));
            context.write(new Text(lstRecordID.get(irec)),new Text(valStr));
        }      
        return;
   } // End of method     
}
