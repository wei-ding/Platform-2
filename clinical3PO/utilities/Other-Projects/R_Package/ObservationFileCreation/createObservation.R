
concept_Data <- read.csv("concept.txt", header=TRUE, dec=".",sep="\t", row.names=NULL, stringsAsFactors = FALSE)
filenames <- list.files("set-a", pattern="*.txt")
filenames <- paste("set-a", filenames, sep="/")

observation<-NULL

#Some value to start with
obs_id=1776118

for (i in 1:length(filenames))
{
  df<-NULL
  Person_Id<-NULL
  concept_description<-NULL
  concept_id<-NULL
  time<-NULL
  value<-NULL
  date<-NULL
  
  df <- read.csv(filenames[i], header=TRUE, dec=".",sep=",", row.names=NULL, stringsAsFactors = FALSE)
  Person_Id = df$Value[1]
  concept_description = df$Parameter[7:length(df$Parameter)]
  time=df$Time[7:length(df$Parameter)]
  value=df$Value[7:length(df$Parameter)]
  time_list<-strsplit(time,":")
  
 for (j in 1:length(value))
 {
 try((concept_id[j]=which(concept_Data$property_name==concept_description[j])))
   #concept_id[j]=which(concept_Data$property_name==concept_description[j])
   if(time_list[[j]][1]<24){
     date[j]="01/01/14"
   }
   if(time_list[[j]][1]>23){
     date[j]="01/02/14"
   }
 }

 df2<-data.frame(OBSERVATION="OBSERVATION",observation_id=obs_id,person_id=Person_Id, observation_concept_id=concept_id,observation_date=date,observation_time=time, value_as_number=value,value_as_string="NULL",value_as_concept_id="NULL",unit_concept_id="NULL",range_low="NULL",range_high="NULL",observation_type_concept_id="NULL",associated_provider_id="NULL",visit_occurrence_id="NULL",relevant_condition_concept_id="NULL",observation_source_value="NULL",units_source_value="NULL")
 rbind(observation, df2)->observation
 obs_id=obs_id+1
 print(i)
}

# Write in to observation file
write.csv(observation, file="observation.csv",row.names=FALSE)
