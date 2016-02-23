#!/usr/bin/env python
'''
@author: sleipnir@walhalla
'''
import argparse
import os
from util_time import *

class ParseCmdLine:
    """
This module parses the command line.
DEFAULT values are represented in square brackets
Arguments:
  REQUIRED:
    --input_physionet / -ip      // WRC: To be changed later on??
    --input_mapreduce / -im      // WRC: To be changed later on?? 
    --output          / -o       Output Directory
  OPTIONAL:
    --user    / -u          [""] File with selected users which are to be withheld.
                                 If empty -> ALL users will be witheld.
    --config  / -conf / -c  [""] File with selected configs which are to be withheld.
                                 If empty, then ALL configs will be withheld.
    --start   / -s          [""] Time stamps to be withheld [start-end[]
    --end     / -e          [""]  
    --range   / -r          [""] File with SELECTED ranges which are to be withheld
                                 for specific configurations
    --delim   / -d          [,]  Delimiter between 2 fields
    --comment / -i          ["#"]
    --help    / -h
    --verbose / -v
    
    """

    def __init__(self):
        """
        Constructor of the ParseInput class
        """
        parser = argparse.ArgumentParser()
        parser.add_argument("--verbose", "-v", 
                            help="Increase verbosity",action="store")
        parser.add_argument("--input_physionet", "-ip", 
                            dest='inputFilePhysionet', required=True,
                            help="Name of the Physionet Input file")
        parser.add_argument("--input_mapreduce","-im",
                            dest='inputFileMapReduce', required=True,
                            help="Name of the Map Reduce Input File")
        parser.add_argument("--output","-o", dest='outputDir', required=True,
                            help="Name of the Output Directory (Dump List/Matrices)")
        parser.add_argument("--user","-u", action='store',
                            dest='userFile', default="", required=False,
                            help="Name of the User File")
        parser.add_argument("--config","-conf","-c", action='store',
                            dest='configFile', default="",
                            required=False, help="Name of the Config File")
        parser.add_argument("--start","-s", action='store',
                            dest='startTime', default="", required=False,
                            help="Global start time")
        parser.add_argument("--end","-e",action='store', dest='endTime', 
                            default="", required=False,
                            help="Global end time")
        parser.add_argument("--range","-r", action='store',
                            dest='rangeFile', default="", required=False,
                            help="Name of the Range File")
        parser.add_argument("--delim","-d", action='store',
                            dest='delim', default=",", required=False,
                            help="Delimiter for the CSV file")
        parser.add_argument("--comment","-i", action='store', dest='comment', 
                            default="#", required=False, help="Comment symbol")
        parser.add_argument("--version", action='version', 
                            version='%(prog)s 1.0')
        results=parser.parse_args()
        
        # Retrieve variables
        # WRC: What about verbosity????
        self.inputFilePhysionet = results.inputFilePhysionet
        self.inputFileMapReduce = results.inputFileMapReduce
        self.outputDir = results.outputDir
        self.userFile = results.userFile
        self.configFile = results.configFile
        self.globalStartTime = results.startTime
        self.globalEndTime = results.endTime
        self.rangeFile = results.rangeFile
        self.delim = results.delim
        self.comment = results.comment

    def checkCmdLineParameters(self):
        """
        Method which checks the command line parameters
        in casu:
            a. whether files exist
            b. if global start & end time exist =>
               convert them to integers 
        """
        if len(self.userFile) == 0:
            self.foundUserFile = False
        else:
            self.foundUserFile = True
         
        if len(self.configFile) == 0:
            self.foundConfigFile = False
        else:
            self.foundConfigFile = True
            
        if len(self.rangeFile) == 0:
            self.foundRangeFile = False
        else:
            self.foundRangeFile = True      
            
        # Check the existence of the files:
        if(not(os.path.isfile(self.inputFilePhysionet))):
            print("  ERROR: The file '{0}' does not exist".\
                  format(self.inputFilePhysionet))
            sys.exit()
        
        if(not(os.path.isfile(self.inputFileMapReduce))):
            print("  ERROR: The file '{0}' does not exist".\
                  format(self.inputFileMapReduce))
            sys.exit()      
            
        if(not(os.path.isdir(self.outputDir))):
            print("  Directory '{0}' does not exist -> create it!".\
                  format(self.outputDir))    
            os.mkdir(self.outputDir)
            
        if(self.foundUserFile):
            if(not(os.path.isfile(self.userFile))):
                print("  ERROR: The file '{0}' does not exist".\
                format(self.userFile))
                sys.exit()        
            
        if(self.foundConfigFile):
            if(not(os.path.isfile(self.configFile))):
                print("  ERROR: The file '{0}' does not exist".\
                format(self.configFile))
                sys.exit()
                
        if(self.foundRangeFile):
            if(not(os.path.isfile(self.rangeFile))):
                print("  ERROR: The file '{0}' does not exist".\
                format(self.rangeFile))
                sys.exit()  
        
        # Start-time    
        if len(self.globalStartTime) == 0:
            self.foundGlobalStartTime = False
        else:
            self.globalStartTimeMin = \
                 TimeUtil.convTimeString(self.globalStartTime)
            self.foundGlobalStartTime = True
            
        # End-Time    
        if len(self.globalEndTime) == 0:
            self.foundGlobalEndTime = False
        else:
            self.globalEndTimeMin=TimeUtil.convTimeString(self.endTime)
            self.foundGlobalEndTime = True
            
        # Check whether start & end time are both present    
        if self.foundGlobalStartTime != self.foundGlobalEndTime:
            print("  ERROR: The global start & end time must be:") 
            print("         either present or absent at the SAME time")
            sys.exit()   
        self.foundGlobalTime = self.foundGlobalStartTime   
                
        return
                      

    def printCmdLineParameters(self):
        """             
        Method which prints the Command Line parameters
        """
        print("  Command Line Parameters:")
        print("    Input File Physionet:'{0}'".format(self.inputFilePhysionet)) 
        print("    Input File MapReduce:'{0}'".format(self.inputFileMapReduce))
        print("    Output Directory    :'{0}'".format(self.outputDir)) 
          
        if self.foundUserFile:
            print("    User id. file:'{0}'".format(self.userFile))
        else:
            print("    NO user id. file")
                
        if self.foundConfigFile:
            print("    Config. file:'{0}'".\
                  format(self.configFile))
        else:
            print("    NO config. file")
            
        if self.foundRangeFile:
            print("    Range file:'{0}'".format(self.rangeFile))  
        else:
            print("    NO range file")    
            
        if self.foundGlobalTime:
            print("    Global Start Time:{0}  {1:4d}".\
                  format(self.globalStartTime,self.globalStartTimeMin))
            print("    Global End   Time:{0}  {1:4d}".\
                  format(self.globalEndTime,self.globalEndTimeMin))
        else:
            print("    NO Global Start/End Time")
             
        print("    CVS Delim:'{0}'".format(self.delim))
        print("    CVS Comment:'{0}'".format(self.comment))         
                