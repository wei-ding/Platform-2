#!/usr/bin/python
import re
import os
import sys
import datetime

### Input is FASTA files directory path passed as command line argument
### Each FASTA file is divided into individual .gb file based on patient Id
### This is step is required because each patient has to be compared with all other patients using Smith Waterman algorithm from Ugene
### This will run Ugene application for a FASTA file and output files are created accordingly
### 

def main(argv):
    try:
        fasta_dump_dir=argv[0]
        splitMethod(fasta_dump_dir)
        runUgene(fasta_dump_dir)
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

def splitMethod(fasta_dump_dir):
    current = None 
    parts = []
    dirs = os.listdir( fasta_dump_dir )
    todays_date=(datetime.datetime.now().strftime("%d-%b-%Y")).upper()

    ## Intermediate individual files based on patient id for each FASTA file will be created in this directory
    createDirectory("/tmp/distance")

    for file in dirs:
        #Create directory for each 
        dir_name=file.split('-')[0]
        createDirectory("/tmp/distance/%s" % (dir_name))

        complete_file_path="%s/%s" % (fasta_dump_dir, file)
        with open(complete_file_path, 'r') as f:#Reading data from .fa file and splitting into individual files
         for line in f:
             if line.startswith('>'):
                 current = [line.strip()]
                 parts.append(current)
             elif current is not None:
                 current.append(line.strip())

        # temp_split is a temporary file
        with open('/tmp/temp_split', 'w+b') as f:
            f.write('\n'.join(('|'.join(part) for part in parts)))

        file = open('/tmp/temp_split','r')
        for line in file:
            ouputStrings=""
            data=line.split('|')[0].strip('>')
            pid=re.sub(' ', '_', data.strip())
            obsrv=line.split('|')[1]
            text=line.split('|')[3].strip()
            createFile="/tmp/distance/%s/%s.gb" % (dir_name,pid)
            outfile=open(createFile,'w')
            ouputStrings ="LOCUS       %s                       5 hr                         %s\nUNIMARK     %s\nORIGIN\n        1 %s\n//" % (pid,todays_date, pid, text)
            #ouputStrings ="LOCUS       %s                       5 hr                         20-NOV-2015\nUNIMARK     %s\nORIGIN\n        1 %s\n//" % (pid, pid, text)
 
            outfile.write(ouputStrings)
            outfile.close()
        file.close()

def runUgene(fasta_dump_dir):
    createDirectory("/tmp/outfiles") #temporary output files wil be stored here

    dir_fasta_dump=fasta_dump_dir
    
    # Open a file
    for fasta_file_name in os.listdir(dir_fasta_dump):
        complete_file_path="%s/%s" % (dir_fasta_dump, fasta_file_name)
        temp_dir_name=fasta_file_name.split('-')[0]
        createDirectory("/tmp/outfiles/%s" % (temp_dir_name))

        path = "/tmp/distance/%s" % (temp_dir_name)
        dirs = os.listdir( path )

        # This would print all the files and directories
        for file in dirs:
            #Following is with score=90 default
            #command_to_run="ugene find-sw --ref=%s --ptrn=/tmp/distance/%s/%s --out=/tmp/outfiles/%s/%s --filter=none --matrix=cpppo --log-level-details " % (complete_file_path, temp_dir_name, file, temp_dir_name, file)
            command_to_run="ugene find-sw --ref=%s --ptrn=/tmp/distance/%s/%s --out=/tmp/outfiles/%s/%s --filter=none --matrix=cpppo --log-level-details > /dev/null " % (complete_file_path, temp_dir_name, file, temp_dir_name, file)

            print command_to_run
            os.system(command_to_run)
            #break
            #sys.exit()

if __name__ == "__main__":
       main(sys.argv[1:])

