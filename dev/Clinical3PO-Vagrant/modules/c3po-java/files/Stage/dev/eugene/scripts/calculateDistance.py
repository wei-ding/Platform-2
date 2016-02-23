#!/usr/bin/python
import os, sys
import re


### This is to calculate distance matrices. 
### Input to this program is --> output files which are generated after running Ugene application
### Each patient is compared against all other patients and results are stored in dictionary as (key, value) pair
### Final output from this program is creating distance matrix files



def main(argv):
    if(os.environ.has_key('UGENE_HOME')):
        ugene_path=os.environ['UGENE_HOME']
    else:
        ugene_path="/opt/ugene"

    try:
        remove_old_dir="rm -r %s/distanceMatrices/*" % ugene_path
        os.system(remove_old_dir)
    except:
        pass

    try:
        os.mkdir("%s/distanceMatrices" % (ugene_path))
    except OSError:
        pass
    except:
        print "Unexpected error while creating directories"

    # Open a file
    path = "/tmp/outfiles/"
    for op_dir_list in os.listdir(path):
        temp_dir_name=op_dir_list
        try:
            os.mkdir("%s/distanceMatrices/%s" % (ugene_path,temp_dir_name))
        except OSError:
            #print "passing..."
            pass
        except:
            print "Unexpected error while creating directories"

    dirs = os.listdir( path )

    for dir in dirs:
        fileDict={}
        path_full = "/tmp/outfiles/%s/" % (dir)
        list_dir=os.listdir( path_full )
        # This would print all the files and directories
        for file in list_dir:
           parts = [] 
           current = None 
           d={}
           with open( path_full+file, 'r') as f:
             for line in f:
                if line.startswith('LOCUS'):
                    text=line.strip().split('|')[0]
                    temp=re.sub('LOCUS ', ' ', text)
                    pid=re.sub('pid_', '', temp.strip())
                    current=[pid.strip(' ')]
                    parts.append(current)
                elif line.startswith('UNIMARK'):
                    pass
                    #print "UNIMARK-ignore"
                elif line.startswith('ORIGIN'):
                    pass
                    #print "ORIGIN-ignore"
                elif line.find('score') != -1:
                    score=re.sub('\/score\=', ' ', line.strip())
                    current.append(int(score.strip(' ')))
             f.close()

             for i in parts:
               k=i[1:]
               #d[i[0].strip('_')]=max(k) if k else 0
               d[i[0].strip('_')]=max(k) if k else -1
    
             key=(re.sub('pid_', '', file.strip('.gb'))).strip('_')
             fileDict[key]=d
             #fileDict[key.strip('_')]=d

        master_list=[]
    
        for key in fileDict:
           #print (fileDict[key])
           master_list.append('%s'%key)
    
        master_list.sort()
    
        duplicate_master_list=master_list
        duplicate_master_list.sort()
        outputStrings=""
        for i in master_list:
            row=" "
            for j in duplicate_master_list:
                  score=str((fileDict['%s' % i])[j])
                  row +=  ("%02s" % score) + " "
            outputStrings += i + row + "\n" 
    
        outMatrix="%s/distanceMatrices/%s/distanceMatrix.txt" % (ugene_path, dir)
        outfile=open(outMatrix, 'w')
        outfile.write("#  Distance Matrix Sample \n")
        outfile.write("#  -1 is for Not matching \n\n")
        outfile.write("#  All Patients Data on X-axis\n")
        outfile.write("#  All Patients Data on Y-axis\n\n\n")
        outfile.write("      ")
        for i in master_list:
                outfile.write('%s ' % i)
    
        outfile.write("\n\n")
        outfile.write(outputStrings)
        outfile.close()
        print "DONE = %s" % dir


if __name__ == "__main__":
    main(sys.argv[1:])
