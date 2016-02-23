# -*- coding: utf-8 -*-
"""
Created on Sun Jul  5 07:43:20 2015
@author: sleipnir
"""
import numpy as np
import util_time as uttime

def createPCHMatrix(arrCSVRecords, arrX, arrY,
                    minTime, maxTime):
    """
        Method to generate the 3D PCH Matrices
        where PCH stands for:
                  P: Patient Index
                  C: Concept Index
                  H: Hourly TimeStamp Index
        @param: arrCVSRecords: Array with the CVS Records
                arrX:  arr. with the Patient IDs
                arrY:  arr. with the Concepts
                Z-axis:
                  minTime: min. Time of ALL Physionet observations
                  maxTime  max. Time of ALL Physionet observations
        @return: matOcc   = f(patientID,conceptID, Hourly Time Stamp)
                 matTime  = f(patientID,conceptID, Hourly Time Stamp)
                 matVal   = f(patientID,conceptID, Hourly Time Stamp)
        where:
            Matrix Acc. Occupancy:
                  matOccPCH=f(patientID,conceptID,timeStamp)
            Matrix Acc. Time stamps:

                   matAccTimePCH=f(patientID,conceptID,timeStamp) 
        The matrix matAccMeasValPCH contains the acc. abs values of the measured values 
                   matAccMeasValPCH=f(patientID,conceptID,timeStamp) 
           
        Each CVS Record/Line has the following form:
        ['141808','albumin','28:38','3.400','46:13','3.100','39:09','3.100', ... ]
            0         1        2       3       4      5        6      7
    """
    numXCoord = len(arrX)
    numYCoord = len(arrY)
    numZCoord = ((maxTime-minTime)/60 + 1)
    print("    Min. Time:{0}".format(minTime))
    print("    Max. Time:{0}".format(maxTime))
    print("    Number of Time Coords:{0}".format(numZCoord))
    print("    #Lines to process:{0}".format(len(arrCSVRecords)))

    matOcc  = np.zeros([numXCoord, numYCoord, numZCoord], dtype=np.int32)
    matTime = np.zeros([numXCoord, numYCoord, numZCoord], dtype=np.int32)
    matVal  = np.zeros([numXCoord, numYCoord, numZCoord], dtype=np.float64)
        
    for iline in range(len(arrCSVRecords)):

        indXCoord = arrX.index(arrCSVRecords[iline][0])
        indYCoord = arrY.index(arrCSVRecords[iline][1])

        # Loop over ALL tuples (time,val)
        for i in range(2,len(arrCSVRecords[iline]),2):

            timeMinutes = uttime.convTimeString(arrCSVRecords[iline][i])
            val     = float(arrCSVRecords[iline][i+1])

            # Find time index
            indZCoord = timeMinutes/60

            # Updated the matrices
            matOcc[indXCoord, indYCoord, indZCoord] +=1
            matTime[indXCoord, indYCoord, indZCoord] +=timeMinutes
            matVal[indXCoord, indYCoord, indZCoord] +=val

    return (matOcc, matTime, matVal)
    
def checkSumDiffInt(matA, matA2):
    """
    Calculate the sum of differences of 2 matrices (type:integer)
    :param matA:
    :param matA2:
    :return:
    """
    return np.sum(matA-matA2)

def checkSumDiffDouble(matA,matA2):
    """
    Calculate the sum of difference of 2 matrices (type: double)
    :param matA:
    :param matA2:
    :return:
    """
    return np.fabs(np.sum(np.fabs(matA)-np.fabs(matA2)))


