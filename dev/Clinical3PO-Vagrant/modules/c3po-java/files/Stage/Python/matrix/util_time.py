import sys
'''
@author: sleipnir@walhalla
'''
def convTimeString(timeString):
    """
        Method to convert a Time String (aa:bb) 
        where aa are hours & bb are minutes        
        into minutes
    """
    try:
        arrTime = timeString.strip().split(":")
        res = int(arrTime[0])*60 + int(arrTime[1])
    except IndexError:
        print("  ERROR: TimeUtil::convTimeString -> TimeString:'{0}' has an invalid format".\
              format(timeString))
        sys.exit();
    return res
        
        