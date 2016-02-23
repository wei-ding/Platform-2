1. Following files are modified in Ugene source package to extend alphabets to support C3PO type
   --> src/corelibs/U2Core/src/datatype/DNAAlphabet.h - new type C3PO_ALPHABET added
   --> src/corelibs/U2Core/src/globals/global.h -  new enum type added for C3PO_ALPHABET
   --> src/corelibs/U2Core/src/datatype/BaseAlphabets.cpp - new c3po type alphabet object is created 
   --> src/corelibs/U2Core/src/datatype/DNAAlphabet.cpp - c3po type is added
   --> src/corelibs/U2Algorithm/src/registry/SubstMatrixRegistry.cpp - Checking for alphabet in c3po type or not

   Note: Only above few files are modified in Ugene source code package
   
2. Following scripts are useful to calculate distance matrices for FASTA files using Ugene.
   --> genDistance.sh - This is called from be main c3po hadoop script 
   --> runUgeneApp.py -  This will run Ugene individually for each patient and compared against all patients in fasta file
   --> calculateDistance.py - This will calculate distance matrix from the output files which are generated from Ugene.
