#!/bin/sh

echo "Generating Distance Matrices-Start"
echo "args passed is $#"

if [ $# -ne 1 ]
then
    echo "<<< Please provide input Fasta dir path to process >>>"
    exit
fi

dir_to_read=$1

echo "Splitting FASTA files and Running Ugene for all patients"
python /opt/ugene/scripts/runUgeneApp.py $dir_to_read
rc=$?

echo "Return value after Split and run Ugene is === $rc"

if [ $rc -eq 0 ]
 then 
    echo "Calculating Distance Matrices"
    python /opt/ugene/scripts/calculateDistance.py
    fi
#fi

echo "Cleaning temporary directories..."

echo "... rm -rf /tmp/distance ..."
rm -rf /tmp/distance
echo "... rm -rf /tmp/outfiles ..."
rm -rf /tmp/outfiles

echo "End..."

