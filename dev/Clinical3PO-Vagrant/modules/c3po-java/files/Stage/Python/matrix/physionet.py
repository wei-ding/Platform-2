q'''
@author: sleipnir@walhalla
'''
import copy
import numpy as np
import sys
import util_io as utio
import util_time as uttime

class Physionet:
    
    def __init__(self):
        """
        Physionet constructor
        """                  
        self.lstPatientIDs = [] # List with Patient IDs
        self.lstConcepts = []   # List with Concepts
        self.minTime = -100
        self.maxTime = -100
        self.arrCSVRecords = []
        
    def readCSVFile(self,filename, delim, comment):
        """
        Read CSV File
        @param filename: name of the file
        @param delim:    delimiter between 2 fields
        @param comment:  comment
        """
        self.arrCSVRecords = utio.readCSVFile(filename, delim, comment)
        return 
    
    def extractFeature(self, ifeature):  
        """
        Method to extract a feature 
               e.g. patientID, concept
        from an array of CSV Records && add these features to a list.

        @param arrCSVRecords: Array with CSV Records 
               ifeature:      Index of the feature in the CSV Record
        
        CSV Records have the following form: 
        ['142145','sysabp','00:50','94']
        0        1        2      3   
        """
        arrFeatures = [x[ifeature] for x in self.arrCSVRecords] 
        arrFeatures = list(set(arrFeatures))
        arrFeatures.sort()
        if ifeature == 0:
            self.lstPatientIDs = copy.deepcopy(arrFeatures)
        elif ifeature == 1:
            self.lstConcepts = copy.deepcopy(arrFeatures)
        return
        
        
    def extractMinMaxTime(self):
        """
        Method to extract the min & max time from 
        an array of CVS Records
        The CSV Records have the following format: 
            ['142145','sysabp','00:50','94']
       
        @param arrCVSRecords: Array with the CSV Records
        @return (minTime.maxTime)
                minTime: min. of all Time Values
                maxTime: max. of all Time Values
        """
        timeArr = [uttime.convTimeString(x[2]) for x in self.arrCSVRecords]
        self.minTime , self.maxTime = min(timeArr), max(timeArr)
        return    
   
   
