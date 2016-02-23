/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.clinical3PO.omopfilter;

/**
 *
 * @author hadoop
 */

public class OMOPTime {
    
    // Method to convert OMOP Time String "xx:yy" 
    // where xx :: hours
    //       yy :: min
    // into an integer minutes
    public static int convertOMOPTime(String timeStr)
    {
        int val;
        String[] words=timeStr.split(":");
        return val=Integer.parseInt(words[0])*60 + Integer.parseInt(words[1]);
    }       
    
}

