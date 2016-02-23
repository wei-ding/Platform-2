
import numpy as np
import matrix as mat
import physionet as physnet
import physionetconstraints as physcon
import physionetdriver as physnetdriver
import physionetfilter as physfilt
import util_io as utio


def readData(inputFilePhysionet, delim, comment):
    """
    Read the Complete Physionet Data
    :param inputFilePhysionet
    :param delim:
    :param comment:
    :return: physionet object
             i.e. lstPatientIds, lstConcepts, minTime, maxTime, records
    """

    physio = physnet.Physionet()
    physio.readCSVFile(inputFilePhysionet,delim,comment)
    physio.extractFeature(0)
    physio.extractFeature(1)
    physio.extractMinMaxTime()
    print("  Physionet Data (COMPLETE) Summary:")
    print("    #Patients:{0:4d}".format(len(physio.lstPatientIDs)))
    print("    #Concepts:{0:4d}".format(len(physio.lstConcepts)))
    print("    Min/Max. Time:{0:d}/{1:d}".format(physio.minTime,physio.maxTime))
    return physio



def retrievePhysnetConstraints(foundUserFile, foundConfigFile, foundRangeFile,
                       userFile, configFile, rangeFile,
                       foundGlobalTime, globalStartTime, globalEndTime,
                       delim, comment):
    """
    Method which retrieves the Physionet Constraints

    :param foundUserFile:
    :param foundConfigFile:
    :param foundRangeFile:
    :param userFile:
    :param configFile:
    :param rangeFile:
    :param foundGlobalTime:
    :param globalStartTime:
    :param globalEndTime:
    :param delim:
    :param comment:
    :return: PhysionetConstraints object
    """
    print("  Retrieve Constraints for Physnet::")
    physiocon = physcon.PhysionetConstraits(foundGlobalTime,
                                        globalStartTime, globalEndTime)

    if foundUserFile:
        csvUser = utio.readCSVFile(userFile, delim, comment)
        physiocon.getSelUserIds(csvUser)

    if foundConfigFile:
        csvConfig = utio.readCSVFile(configFile, delim, comment)
        physiocon.getSelConfigs(csvConfig)

    if foundRangeFile:
        csvRange = utio.readCSVFile(rangeFile, delim, comment)
        physiocon.getSelRanges(csvRange)

    # Print constraints
    physiocon.printConstraints()

    return physiocon

def applyPhysnetConstraints(physio, physiocon):
    """
    Method which applies constraints to the physionet Data
    :param physio:
    :param physiocon:
    :return:
    """

    print("  Apply Constraints to Physnet::")
    if len(physiocon.userArr) > 0:
        print("    #Lines (BEFORE Filt. users)     :{0:8d}".\
              format(len(physio.arrCSVRecords)))
        physio.arrCSVRecords = \
               physfilt.filterUserIds(physio.arrCSVRecords, physiocon.userArr)
        print("    #Lines (AFTER Filt. users)      :{0:8d}".\
              format(len(physio.arrCSVRecords)))

    if len(physiocon.configArr) > 0:
        print("    #Lines (BEFORE filt. conf.)     :{0:8d}".\
              format(len(physio.arrCSVRecords)))
        physio.arrCSVRecords = \
              physfilt.filterConfigs(physio.arrCSVRecords, physiocon.configArr)
        print("    #Lines (AFTER filt. conf.)      :{0:8d}".\
              format(len(physio.arrCSVRecords)))

    # Local Time :: (configuration, tlow, tup)
    if physiocon.foundLocalTime:
        print("    #Lines (BEFORE filt. loc. time) :{0:8d}".\
              format(len(physio.arrCSVRecords)))
        physio.arrCSVRecords = \
              physfilt.filterLocalTime(physio.arrCSVRecords,
                                    physiocon.configArr,
                                    physiocon.localStartTimeArr,
                                    physiocon.localEndTimeArr)
        print("    #Lines (AFTER filt. loc. time)  :{0:8d}".\
              format(len(physio.arrCSVRecords)))

    elif physiocon.foundGlobalTime:
        print("    #Lines (BEFORE filt. glob. time):{0:8d}".\
              format(len(physio.arrCSVRecords)))
        physio.arrCSVRecords = \
               physfilt.filterGlobalTime(physio.arrCSVRecords,
                                     physiocon.globalStartTime,
                                     physiocon.globalEndTime)
        print("    #Lines (AFTER filt. global time):{0:8d}".\
              format(len(physio.arrCSVRecords)))

    if len(physiocon.conceptRangeArr) > 0:
        print("    #Lines (BEFORE filt. range)     :{0:8d}".\
              format(len(physio.arrCSVRecords)))
        physio.arrCSVRecords = \
               physfilt.filterRange(physio.arrCSVRecords,
                                physiocon.conceptRangeArr,
                                physiocon.lowRangeArr,
                                physiocon.upperRangeArr)
        print("    #Lines (AFTER filt. range)      :{0:8d}".\
              format(len(physio.arrCSVRecords)))

    return


def dumpPhysnetData(physio,matAccOcc,
                    matAccTime, matAccVal, outputDir):
    """
    Method which dumps the physionet data to a file
    :param physio:
    :param matAccOcc:
    :param matAccTime:
    :param matAccVal:
    :param outputDir:
    :return:
    """

    xArr = physio.lstPatientIDs
    yArr = physio.lstConcepts

    filePatientIDs = outputDir + "/PatientIDs.txt" # Pickle
    fileConcepts   = outputDir + "/Concepts.txt"   # Pickle
    fileMatAccOcc  = outputDir + "/matOcc"         # Numpy
    fileMatAccTime = outputDir + "/matAccTime"     # Numpy
    fileMatAccVal  = outputDir + "/matAccVal"      # Numpy

    # Dump arrays and lists to file
    utio.dumpListToFile(filePatientIDs, xArr)
    utio.dumpListToFile(fileConcepts, yArr)
    utio.dumpMatrixToFile(fileMatAccOcc,matAccOcc)
    utio.dumpMatrixToFile(fileMatAccTime,matAccTime)
    utio.dumpMatrixToFile(fileMatAccVal,matAccVal)
    return










