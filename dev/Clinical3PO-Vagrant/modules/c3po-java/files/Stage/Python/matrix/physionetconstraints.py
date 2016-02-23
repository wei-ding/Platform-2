import sys
import util_time as uttime

class PhysionetConstraits:
    
    def __init__(self, foundGlobalTime, globalStartTime, globalEndTime):
        """
        PhysionetConstraints constructor
        """
        self.userArr = []
        self.configArr = []
        self.foundLocalTime = False
        self.localStartTimeArr = []
        self.localEndTimeArr = []
        self.foundGlobalTime = foundGlobalTime
        self.globalStartTime = globalStartTime
        self.globalEndTime = globalEndTime
        self.conceptRangeArr = []
        self.lowRangeArr = []
        self.upperRangeArr = []
        
    def getSelUserIds(self, csvLst):
        """
        Method to extract the user ids in a csv list 
        """
        for iconf in range(len(csvLst)):    
            if len(csvLst[iconf])==1:
                self.userArr.append(csvLst[iconf][0])
            else:
                print("  ERROR: Constraint::getSelUserIds -> line has > 1 el")
                print("         '{0}'".format(csvLst[iconf]))
                sys.exit()      
        return 

    
    def getSelConfigs(self, csvLst):
        """
        Method to extract the CONFIGURATION anmes
        and 
        (IF THEY EXIST) the lower and upper TIME stamps        
        """
        for iconf in range(len(csvLst)):    
            if len(csvLst[iconf])==1:
                self.configArr.append(csvLst[iconf][0])
            elif len(csvLst[iconf]) == 3:
                self.foundLocalTime = True
                self.configArr.append(csvLst[iconf][0])
                self.localStartTimeArr.append(
                     uttime.convTimeString(csvLst[iconf][1]))
                self.localEndTimeArr.append(
                     uttime.convTimeString(csvLst[iconf][2]))
            else:
                print("  ERROR: Constraint::getSelConfigs -> " +
                      "line has neither 1 el. nor 3 el.")
                print("         '{0}'".format(csvLst[iconf]))
                sys.exit()  
                
        # Check that #Local times == #Configs
        if self.foundLocalTime and \
           (len(self.configArr) != len(self.localStartTimeArr)):
            print("  ERROR::getselfConfigs: len(configArr) !=" +
                            " len(self.localStartTimeArr)")
        return

    def getSelRanges(self, csvLst):
        """
        Method to extract the CONFIGURATION names 
        and
        the upper and lower RANGES
        """
        for iconf in range(len(csvLst)):    
            if len(csvLst[iconf])==3:
                self.conceptRangeArr.append(csvLst[iconf][0])
                self.lowRangeArr.append(float(csvLst[iconf][1]))
                self.upperRangeArr.append(float(csvLst[iconf][2]))
            else:
                print("  ERROR: Constraint::getSelRanges -> " +
                      "line has neither 1 el. nor 3 el.")
                print("         '{0}'".format(csvLst[iconf]))
                sys.exit()     
        return   
        
    def printConstraints(self):
        """
        Method to print ALL the constraints that will be applied
        """
        if len(self.userArr) >0:
            print("    The following {0} user ids will be withheld".\
                  format(len(self.userArr)))
            for user in self.userArr:
                print("        {0}".format(user))
                
        if len(self.configArr) > 0:
            print("    The following {0} configs will be withheld".\
                  format(len(self.configArr)))
            if self.foundLocalTime :
                for (iconf,config) in enumerate(self.configArr):
                    print("        {0:<15}  [{1} -- {2} [".\
                          format(config,
                                 self.localStartTimeArr[iconf],
                                 self.localEndTimeArr[iconf]))
            else:
                for config in self.configArr:
                    print("        {0:<15}".format(config))
        
        if len(self.conceptRangeArr) > 0:
            print("    The following {0} configs will be withheld".\
                  format(len(self.conceptRangeArr)))
            for (irange,el) in enumerate(self.conceptRangeArr):
                print("        {0:<15}  [{1} -- {2} [".\
                      format(el,
                             self.lowRangeArr[irange],
                             self.upperRangeArr[irange]))   
                             
        if self.foundGlobalTime:
            print("    Global time constraint:")
            print("        {0:<15}  [{1} -- {2} [".\
                          format(config,
                                 self.globalStartTime,
                                 self.globalEndTime))     
        else:
            print("    No Global time constraint")                         
        return         
            
            