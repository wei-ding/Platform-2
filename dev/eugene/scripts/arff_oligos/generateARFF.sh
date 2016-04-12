#!/bin/sh

echo "ARFF genration - Start"
echo " Using Ugene distance matrices - Start "
echo "args passed is $#"

if [ $# -ne 2 ]
then
    echo "<<< Please provide input FASTA path and input oligos dir path to process >>>"
    echo "Usage:"
    echo -e "\t./generateARFF.sh <FASTA_dir_path> <Oligos_dir_path"
    echo -e "\nEg:"
    echo -e "\t./generateARFF.sh /opt/ugene/fasta_dump /opt/ugene/oligos_dump"
    exit
fi

fasta_dir=$1
oligos_dir=$2

echo "Splitting input file into individual .gb file and Running Ugene for all patients against selected Oligos"
python /opt/ugene/scripts/arff_oligos/runUgeneAppOligos.py $fasta_dir $oligos_dir
rc=$?

echo "Return value after Split and run Ugene is === $rc"

returnCode=-1
if [ $rc -eq 0 ]
 then 
    echo "Calculating Distance Matrices"
    python /opt/ugene/scripts/arff_oligos/calculateOligiosDistance.py
    returnCode=$?
fi

if [ $returnCode -eq 0 ]
 then 
    echo " Creating ARFF file from distance matrix csv file "
    python /opt/ugene/scripts/arff_oligos/createARFF.py
fi


echo "Cleaning temporary directories..."

echo "... rm -rf /tmp/OligosDistance ..."
rm -rf /tmp/OligosDistance
echo "... rm -rf /tmp/OligosOutfiles ..."
rm -rf /tmp/OligosOutfiles

echo "<<< ARFF generation - End >>>"

