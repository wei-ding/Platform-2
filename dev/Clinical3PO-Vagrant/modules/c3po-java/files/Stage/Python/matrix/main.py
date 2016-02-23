import numpy as np
import sys
import matrix as mat
import parsecmdline as parsecmd
import physionetconstraints as physcon
import physionetdriver as physdriver
import physionet as physnet
import physionetfilter as physfilt
import util_io as utio


# Parse Cmd Line & Check it.
print("Parse Command Line::")
p = parsecmd.ParseCmdLine()
p.checkCmdLineParameters()
p.printCmdLineParameters()


# Physionet Data
print("\nPhysionet Data Block::")
physio    = physdriver.readData(p.inputFilePhysionet, p.delim, p.comment)
physiocon = physdriver.retrievePhysnetConstraints(
                          p.foundUserFile, p.foundConfigFile, p.foundRangeFile,
                          p.userFile, p.configFile, p.rangeFile,
                          p.foundGlobalTime, p.foundGlobalStartTime, p.globalEndTime,
                          p.delim, p.comment)

physdriver.applyPhysnetConstraints(physio,physiocon)
print("  Creating Physnet 3D-matrices::")
(matA, matB, matC) = mat.createPCHMatrix(physio.arrCSVRecords,
                                         physio.lstPatientIDs,
                                         physio.lstConcepts,
                                         physio.minTime, physio.maxTime)
print("    #Entries in matA:{0}".format(  int(np.sum(matA[:,:,:]))))
physdriver.dumpPhysnetData(physio,matA, matB, matC,p.outputDir)


# Mapreduce Data
print("\nMapReduce Data Block::")
print("  Read file '{0}' (MapReduce Output)".format(p.inputFileMapReduce))
csvMRFiltered = utio.readCSVFile(p.inputFileMapReduce, p.delim, p.comment)
print("  Creating MapReduce 3D-matrices:")
(matA2, matB2, matC2) = mat.createPCHMatrix(csvMRFiltered,
                                            physio.lstPatientIDs,
                                            physio.lstConcepts,
                                            physio.minTime, physio.maxTime)
print("    #Entries in matA2:{0}".format( int(np.sum(matA2[:,:,:]))))


# Compare results
print("\nCompare Results::")
print("    |DOCC|  = {0:20d}".format(mat.checkSumDiffInt(matA, matA2)))
print("    |DTIME| = {0:20d}".format(mat.checkSumDiffInt(matB, matB2)))
print("    |DVALUE|={0:20.12f}".format(mat.checkSumDiffDouble(matC,matC2)))