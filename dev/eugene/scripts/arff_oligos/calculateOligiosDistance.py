#!/usr/bin/python
import os, sys
import re


fileDataMap={}
printDataMap={}
columnTitles=None

### This is to calculate distance matrices.
### Input to this program is --> output files which are generated after running Ugene application
### Each patient is compared against all other patients in Oligos file and results are stored in dictionary as (key, value) pair
### Final output from this program is creating distance matrix files

def createDistanceMatrix(ugene_path, dir_name):
    path = "/tmp/OligosOutfiles/%s/" % dir_name
    dirs = os.listdir( path )

    print "Processing for dir = %s" % (dir_name)
    # This would print all the files and directories
    for file in dirs:
       print "\t%s" % (path+file)
       parts = []
       current = None
       d={}
       with open( path+file, 'r' ) as f:
            #print "File path and Name is ==== ", (path+file)
            for line in f:
                if line.startswith('LOCUS'):
                    #print "LOCUS"
                    text=line.strip().split('|')[0]
                    temp=re.sub('LOCUS ', ' ', text)
                    pid=re.sub('pid_', '', temp.strip())
                    temp_pid=pid.strip('_')#new step required here
                    current=[temp_pid.strip(' ')]
                    #current=[pid.strip(' ')]
                    parts.append(current)
                elif line.startswith('UNIMARK'):#ignore this line
                    pass
                elif line.startswith('ORIGIN'):#ignore this line
                    pass
                elif line.find('score') != -1:
                    score=re.sub('\/score\=', ' ', line.strip())
                    current.append(int(score.strip(' ')))
            f.close()

            for i in parts:
                k=i[1:]
                d[i[0]]=max(k) if k else -1

            key=re.sub('pid_', '', file.strip('.gb'))
            global fileDataMap
            fileDataMap[key]=d
    #global fileDataMap
    for key in fileDataMap:
        global fileDataMap
        subDict=fileDataMap[key]
        values=""
        templist=[]#This list is used for sorting purpose, dictionary does not support sorting
        for k in subDict:
            templist.append('%s'%k)
        templist.sort()
        global columnTitles
        if columnTitles is None:
            columnTitles=templist
        for i in templist:
            values += str(subDict[i]) + ","
            if i == key:
                print "sub i is = %s and master key is == %s and subDict[i]---- %s" % (i, key, subDict[i])
        global printDataMap
        printDataMap[key]=values

def writeToFile(ugene_path, dir_name):
    try:
        outputLines=","
        global columnTitles
        for col in columnTitles:
            outputLines += col + ', '
        outputLines += '\n'

        global printDataMap
        for key in printDataMap:
            outputLines += key + ',' + printDataMap[key] + '\n'

        outMatrix="%s/OligosDistMatrix/%s/outputDistance.csv" % (ugene_path, dir_name)
        outfile=open(outMatrix, 'w')
        outfile.write("\n\n")
        outfile.write('%s ' % outputLines)
        outfile.close()
    except Exception as error:
        print "Exception: %s" % (error)

def createDirectory(dir_name):
    try:
        os.mkdir(dir_name)
    except OSError:
        #may get this exception if dir already exists in that path
        pass
    except Exception as e:
        print "Unexpected error while creating directory" % (dir_name)
        print "Exception: %" % (e)


def main(argv):
    if(os.environ.has_key('UGENE_HOME')):
        ugene_path=os.environ['UGENE_HOME']
    else:
        ugene_path="/opt/ugene"

    try:
        remove_old_dir="rm -rf %s/OligosDistMatrix/* %s/ARFF_dump/*" % (ugene_path, ugene_path)
        os.system(remove_old_dir)
    except:
        pass

    createDirectory("%s/OligosDistMatrix" % (ugene_path))
    createDirectory("%s/ARFF_dump" % (ugene_path))
    # Open a file
    path = "/tmp/OligosOutfiles/"
    for op_dir_list in os.listdir(path):
        temp_dir_name=op_dir_list
        createDirectory("%s/OligosDistMatrix/%s" % (ugene_path,temp_dir_name))

    try:
        dirs = os.listdir( path )
        for dir in dirs:
            createDistanceMatrix(ugene_path, dir)
            writeToFile(ugene_path, dir)
            global fileDataMap
            fileDataMap={}
            global printDataMap
            printDataMap={}
            global columnTitles
            columnTitles=None

    except Exception as error:
        print "Exception: %s" % (error)

    print "DONE"

if __name__ == "__main__":
    main(sys.argv[1:])
