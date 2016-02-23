import csv
import pickle
import sys
import numpy as np
"""
@author: sleipnir@walhalla
"""

def readCSVFile(filename,separator=',',comment='#'):
    """
        Method to read a CSV input file.
        1.The lines starting with a comment (DEFAULT:'#') are skipped
        2.The remaining lines are split (using the delimiter) 
          into a list of words (:= csvRecord) 
          
        AND:    
        a.The Blanks around the words are removed.
        b.The Words within a csvRecord are lowered.

        @param: filename : Name of the CSV file to be read
                separator: CSV delimiter  
                comment  : Character to start a line with comments

        WARNING:
        The separator can only be 1 character long.
        e.g. ' ','\t',',',';' 
        Multiple white space can NOT be used.

    """
    try:
        f = open(filename,"r")
        reader = csv.reader(f, delimiter=separator)
        arrCSVRecords = [[s.strip().lower() for s in line]
                         for line in list(reader)
                             if not line[0].strip().startswith(comment)]
        f.close()
    except IOError:
        print("    ERROR: The file '{0}' can not be opened".\
              format(filename))
        sys.exit()
    return arrCSVRecords
 

def readFile(filename):                                       # To KEEP ?
    """
        Method to read input files 
        and dump the lines into an array
    """
    try:
        f = open(filename,"r")
        arrLines = f.readlines()
        f.close()
    except IOError:
        print("    ERROR: The file '{0}' can not be opened".\
                  format(filename)) 
        sys.exit()
    return arrLines
    

def writeOut(filename,arrLines):
    """
        Method to write content of arrLines
        into a file
    """
    try:
        f = open(filename,"w")
        f.writeLines(arrLines)
        f.close()
    except IOError:
        print("   The file '{0}' can not be opened".format(filename))
    return
            

def isInInterval(val,low,upper):
    """
        Method to check if the value val
           \in [low,upper[
        We require that:
           low>=0 and upper>low
        @param  val: value to be checked
                low  : lower boundary of interval (inclusive)
                upper: upper boundary of interval (exclusive)
        @return: True <=> if value lies in interval
                 else False
    """
    if (upper>low>=0) and (upper>val>=low):
        return True
    else:
        return False


def dumpListToFile(filename,lst):
    """
        Method to dump Python list to file 
        using pickle
        @param filename: filename
               lst     : name of the list
    """
    with open(filename,'wb') as f:
        pickle.dump(lst,f)
    return


def getListFromFile(filename):
    """
    Method to retrieve a List from file
    using pickle
    """
    with open(filename,'rb') as f:
        lst=pickle.load(f)
    return lst


def dumpMatrixToFile(filename,arr):
    """
        Method to dump a Numpy arr to file 
    """
    np.save(filename,arr)
    return


def getMatrixFromFile(filename):
    """
        Method to retrieve a Numpy Array from file
    """
    arr = np.load(filename)
    return arr