#!/usr/bin/python

import csv
import sys
import os

"""
Read all patients data from  death.txt file
store them in key, value pair dict
compare this dict while constructing indexDataMap and class value at end
"""

deathInfoMap={}
indexDataMap={}
patientHeaderMap=[]

def readDeathdata():
    deathFile = open("/opt/ugene/input/death.txt", 'rt')
    try:
        reader = csv.reader(deathFile, delimiter='\t')
        for row in reader:
            if len(row) == 0:#row is a list type
               continue
            deathInfoMap[row[1]]=1
    except Exception as error:
        print "Exception while reading death data : %s" % (error)

    finally:
        deathFile.close()

def parseData(full_dir_path, sub_dir_name):
    f = open( ("%s/%s/outputDistance.csv" % (full_dir_path, sub_dir_name)), 'rt')
    #f = open("/opt/ugene/OligosDistMatrix/outputDistance.csv", 'rt')
    try:
        reader = csv.reader(f)
        for row in reader:
            if len(row) == 0:# this is to ignore empty lines in file
                continue
            for i in range(len(row)) :
                if (len(row[i]) > 2):#To ignore empty/space/commas lines
                    global indexDataMap
                    indexDataMap[i]=row[i].strip(' ') + ','
            break

        for row in reader:
            if len(row) == 0:#row is a list type
                continue

            for i in range(len(row)) :
                if i == 0:
                    global patientHeaderMap
                    patientHeaderMap.append(row[i])
                    continue
                if (len(row[i]) > 1):
                    global indexDataMap
                    indexDataMap[i]=indexDataMap[i] + row[i].strip(' ') + ','
    except Exception as error:
        print "Exception while parsing Distance.csv file : %s" % (error)

    finally:
        f.close()

    #This is used to fill with Death/Alive class feature at the end of each patient data
    global indexDataMap
    for i in indexDataMap:
        strData=indexDataMap[i].split(',')[0]
        #print "strData '%s' ==== " % (strData)
        if len(strData) > 2:#To avoid empty or spaces
            liveStatus=""
            if strData in deathInfoMap:
                liveStatus = "Death"
            else:
                liveStatus = "Alive"
            indexDataMap[i]=indexDataMap[i] + liveStatus

def writeToOutfile(ugene_path, sub_dir_name):
    outputArff="%s/ARFF_dump/%s.arff" % (ugene_path, sub_dir_name)
    #outputArff="%s/ARFF_dump/%s/output.arff" % (ugene_path, sub_dir_name)
    outfile=open(outputArff, 'w')
    outfile.write("@relation clinical3PO \n")
    outfile.write("@attribute PatientID NUMERIC\n")

    global patientHeaderMap
    for i in patientHeaderMap:
        if len(i) > 2:#to avoid empty/space/comma lines
            outfile.write("@attribute Oligo_%s NUMERIC\n" % (i))

    outfile.write("@attribute class {Death,Alive}\n")
    outfile.write("\n\n")
    outfile.write("@data\n")

    global indexDataMap
    for i in indexDataMap:
       outfile.write('%s' % indexDataMap[i])
       outfile.write("\n")

    print "ARFF generation completed for --> %s " % (sub_dir_name)

    outfile.close()


def main(argv):
    if(os.environ.has_key('UGENE_HOME')):
         ugene_path=os.environ['UGENE_HOME']
    else:
         ugene_path="/opt/ugene"

    try:
        readDeathdata()
        distance_dump_dir="%s/OligosDistMatrix" % (ugene_path)
        output_arff_dump="%s/ARFF_dump" % (ugene_path)
        for sub_dir_name in os.listdir(distance_dump_dir):
            #print sub_dir_name
            #print output_arff_dump
        #for dir in os.listdir("/opt/ugene/OligosDistMatrix/"):
            #--createDirectory("%s/%s" % (output_arff_dump, sub_dir_name))
            global indexDataMap
            indexDataMap={}
            global patientHeaderMap
            patientHeaderMap=[]
            parseData(distance_dump_dir, sub_dir_name)
            writeToOutfile(ugene_path, sub_dir_name)
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

if __name__ == "__main__":
    main(sys.argv[1:])
