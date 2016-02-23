"""
@author sleipnir@walhalla
"""
import util_time as uttime


def filterUserIds(csvList,userArr):
    """
    Method to keep ONLY those users which have 
    been found in the userfile.
    """
    res=[]
    for iline in range(len(csvList)):
        if csvList[iline][0] in userArr:
            res.append(csvList[iline])
    return res   
    
    
def filterConfigs(csvList,configArr):
    """
    Method to keep ONLY those configs which have 
    been found in the configfile.
    """
    res=[]
    for iline in range(len(csvList)):
        if csvList[iline][1] in configArr:
            res.append(csvList[iline])
    return res           
    
    
def filterLocalTime(csvList,configArr,localStartTimeArr,localEndTimeArr):
    """
    Method to preserve ONLY those (time,val) tuples 
    for those configurations which:
      a. are specified in configFile
      b. where t \in [t0,t1[
      Local Time is linked to a CONFIGURATION
    """
    res=[]
    for iline in range(len(csvList)):
        if csvList[iline][1] in configArr:
            ipos = configArr.index(csvList[iline][1])
            timeVal = uttime.convTimeString(csvList[iline][2])
            lowTime = localStartTimeArr[ipos]
            upTime =  localEndTimeArr[ipos]
            if lowTime <= timeVal <upTime:
                #print("        {0}".format(csvList[iline]))
                res.append(csvList[iline])
    return res
    
    
def filterGlobalTime(csvList,lowTime,upTime):
    """
    Method to KEEP only those (time,val) tuples 
    where time \in [low,up[
    """
    res=[]
    for iline in range(len(csvList)):
        timeVal=uttime.convTimeString(csvList[iline][2])
        if lowTime <= timeVal <upTime:
            res.append(csvList[iline])
    return res
    
 
def filterRange(csvList,conceptRangeArr,lowRangeArr,upperRangeArr):
    """
    Method to KEEP only those configurations where 
    the meas. value \in [val1, val2[    
    """
    res=[]
    for iline in range(len(csvList)):
        if csvList[iline][1] in conceptRangeArr:
            ipos = conceptRangeArr.index(csvList[iline][1])
            measVal = float(csvList[iline][3])
            lowRange = lowRangeArr[ipos]
            upRange  = upperRangeArr[ipos]  
            if lowRange <= measVal <upRange:
                #print("        {0}".format(csvList[iline]))
                res.append(csvList[iline])       
    return res
            