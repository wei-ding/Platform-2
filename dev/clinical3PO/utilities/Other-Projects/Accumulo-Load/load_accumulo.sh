# Loading of concept table
accumulo LoadConcept concept.txt concept c3po localhost:2181 root c3po123

# Loading of death table
accumulo LoadDeath death.txt death c3po localhost:2181 root c3po123

# Loading of observation table
for i in Observation*/obs*txt
do

   echo "Loading of file $i started at " `date`
   accumulo LoadObservation $i observation c3po localhost:2181 root c3po123 personid personobservationid
   if [ $? -eq 0 ]
   then
      echo "Loading of file $i completed at " `date`
      /bin/rm $i
   else
      echo "Loading of file $i/$j failed at " `date`
   fi
   echo
   echo

done

