package org.clinical3PO.extraction;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author hadoop
 */
public class TimeConversion {
 /**
 * Method which returns a time given as two strings i.e.
 *      date (DD-MMM-YYYY) 
 *      time (HH:MM:SS)
 * wrt to a starting time given in ms.
 * 
 * and returns the difference between the time and the starting time 
 * as a string of the format HH:MM
 * @param  timeInMsStart  Start Time (01-Jan-2013 00:00:00 
 *                        wrt to a certain point in Ms
 * @param  timeDayMonthYear DD-MMM-YYYY
 * @param  timeHourMinSec   HH:MM:SS
 * @return                  HH:MM
 */
    public static String convertTimeToString(long timeInMsStart,
                                             String timeDayMonthYear,
                                             String timeHourMinSec) throws
            ParseException                                      
    {
        final double MS_TO_MIN=60000.;          // ms into min.
        final long MIN_TO_HOURS=60;             // min. into hours
        final String pattern = "dd-MMM-yyyy";   // Date pattern
        
        Date date;
        long numMsDate=0;
        // A. Get the #Millisecond of DD-MMM-YYYY since xxxx (1970)
        try {
              date=new SimpleDateFormat(pattern,Locale.US).parse(timeDayMonthYear.trim());
              numMsDate=date.getTime();                        
        }
        catch(ParseException e)  {
            System.err.println(e);
            System.exit(-1);    
        }
        
        // B. Get the #Milliseconds due to the Time
        String[] wordsHMS=timeHourMinSec.trim().split(":");
        if(wordsHMS.length!=3)
        {    
            System.err.print(" The string TMD '" + timeHourMinSec + "'" +
                             " has the WRONG format.\n" +
                             " Req. Format:'HH:MM:SS' ");
            System.exit(-1);
        }
        // Convert HH:MM:SS into Ms
        long numMsTime=(Integer.parseInt(wordsHMS[0].trim()) * 3600 +
                        Integer.parseInt(wordsHMS[1].trim()) * 60 +
                        Integer.parseInt(wordsHMS[2].trim())
                       ) * 1000;
       
        long timeInMs=(numMsDate + numMsTime);
        long diffMs=(timeInMs - timeInMsStart);
        long diffMin=Math.round(diffMs/MS_TO_MIN);
        long nhours=  (diffMin/MIN_TO_HOURS);
        long nmin=   (diffMin % MIN_TO_HOURS);
        return String.format("%02d:%02d",nhours, nmin);
    }  
      
}