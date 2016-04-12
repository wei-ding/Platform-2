#!/usr/bin/python
import re
import os
import sys
import datetime

### Input is Glasgow coma patients who are dead as command line argument.
### Data is collected 48 hours before their death occurance for some random patients
### From patients 48 hours data, Oligos file is created
### This file is splitted into individual .gb file based on patient Id
### This step is required because each patient has to be compared with all other patients using Smith Waterman algorithm from Ugene
### This will run Ugene application for input file and output files are created accordingly
###

def main(argv):
    if len(argv)!=2:
        print "input FASTA file path | Oligos path missing to process...Exiting"
        sys.exit()
    fasta_path=argv[0]
    oligos_path=argv[1]
    if(os.environ.has_key('UGENE_HOME')):
        ugene_path=os.environ['UGENE_HOME']
    else:
        ugene_path="/opt/ugene"

    try:
        splitMethod(ugene_path, oligos_path)
        runUgene(ugene_path, fasta_path, oligos_path)
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

### This method will split lines from input file
### Patient data is available in 2 lines as
###      >pid 132929|observation Glasgow Coma
###      AAAAAAAACCDD
### temp fle is created to process above data and
### finally .gb files is created for each patient

def splitMethod(ugene_path, oligos_dump_dir):
    dirs = os.listdir( oligos_dump_dir )
    todays_date=(datetime.datetime.now().strftime("%d-%b-%Y")).upper()
    ## Intermediate individual files based on patient id for each FASTA file will be created in this directory
    createDirectory("/tmp/OligosDistance")

    for file in dirs:
        current = None
        parts = []
        #Create directory for each
        dir_name=file.split('.')[0]
        createDirectory("/tmp/OligosDistance/%s" % (dir_name))
        complete_file_path="%s/%s" % (oligos_dump_dir, file)
        with open(complete_file_path, 'r') as f:#Reading data from .fa file and splitting into individual files
            #input_fasta="%s/scripts/GlasgowAlgo.txt" % ugene_path
            for line in f:
                if line.startswith('>'):
                    current = [line.strip()]
                    parts.append(current)
                elif current is not None:
                    current.append(line.strip())
        with open('/tmp/temp_split_oligos', 'wb+') as f:
            f.write('\n'.join(('|'.join(part) for part in parts)))

        temp_file = open('/tmp/temp_split_oligos','r')
        for line in temp_file:
            ouputStrings=""
            data=line.split('|')[0].strip('>')
            pid=re.sub(' ', '_', data)
            obsrv=line.split('|')[1]
            text=line.split('|')[2].strip()
            createFile="/tmp/OligosDistance/%s/%s.gb" % (dir_name, pid)
            outfile=open(createFile,'w')
            ouputStrings ="LOCUS       %s                       5 hr                         %s\nUNIMARK     %s\nORIGIN\n        1 %s\n//" % (pid,todays_date, pid, text)
            #ouputStrings ="LOCUS       %s                       5 hr                         16-OCT-2015\nUNIMARK     %s\nORIGIN\n        1 %s\n//" % (pid, pid, text)
            outfile.write(ouputStrings)
            outfile.close()
        temp_file.close()

### This method will run Ugene application
### It will run Ugene for each patient (.gb file) and
### Compare data with all other patients using Smith Waterman algorithm
### Outfiles are generated for each run and stored temporarily
### These outfiles are used to calculate distance matrix

def runUgene(ugene_path, fasta_dump_dir, oligos_dump_dir):
    createDirectory("/tmp/OligosOutfiles") #temporary output files will be stored here

    # Open a file
    for fasta_file_name in os.listdir(fasta_dump_dir):
        complete_file_path="%s/%s" % (fasta_dump_dir, fasta_file_name)
        temp_dir_name=fasta_file_name.split('-')[0]

        print os.listdir(oligos_dump_dir)
        if ("%s.txt" % temp_dir_name) in os.listdir(oligos_dump_dir):
            print "Processing started for fasta ---> %s " % (fasta_file_name)
        else:
            print "Oligos file does not exists for file << %s >>" % (fasta_file_name)
            continue

        createDirectory("/tmp/OligosOutfiles/%s" % (temp_dir_name))
        path = "/tmp/OligosDistance/%s" % (temp_dir_name)
        dirs = os.listdir( path )
        # This would print all the files and directories
        for file_name in dirs:
            #Following commands to run for FASTA
            #Following is with score=90 default
            #command_to_run="ugene find-sw --ref=%s --ptrn=/tmp/OligosDistance/%s --out=/tmp/OligosOutfiles/%s --filter=none --matrix=cpppo --log-level-details > /dev/null " % (fasta_dump_dir, fasta_file_name,  file_name, file_name)
            command_to_run="ugene find-sw --ref=%s --ptrn=/tmp/OligosDistance/%s/%s --out=/tmp/OligosOutfiles/%s/%s --filter=none --matrix=%s --log-level-details > /dev/null " % (complete_file_path, temp_dir_name, file_name, temp_dir_name, file_name, temp_dir_name)
            print command_to_run
            os.system(command_to_run)


if __name__ == "__main__":
       main(sys.argv[1:])
