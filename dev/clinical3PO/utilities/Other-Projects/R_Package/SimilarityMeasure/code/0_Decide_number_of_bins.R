# Documentation

#
#This is a one time execution which decides the number of bins to be used
#for each of the 37 attributes based on the number of observations recorded
#for that attribute across all patients.

#The output here is just a suggestion on the bin size to be used based on the data.

#One can manually enter the "number of bins" required for each attribute in the file "Number_of_bins.csv".

#One can also use "Suggested_Number_of_bins.csv" where we have given some thought
#to each attribute and suggested the number of bins to be used based on our judgement. 
#Make sure to rename this file to "Number_of_bins.csv"

decide_number_of_bins <- function(working_directory,observation_file){

	setwd(working_directory)

	observation_Data <- read.csv(observation_file, header=TRUE, dec=".",sep=";", row.names=NULL, stringsAsFactors = FALSE)

	patient_list=unique(observation_Data$person_id)

	#-----Get the number of records of each attribute for each patient--------#

	records <- table(observation_Data$person_id, observation_Data$observation_concept_id)
	records_df <- as.data.frame.matrix(records)
	records_output_file <- cbind(person_id = rownames(records_df), records_df)

	dir.create("Binning_Data", showWarnings = FALSE)
	write.csv(records_output_file,file="Binning_Data/Number_of_records.csv",row.names=FALSE) 

	#------Decide the number of bins to be used based on the distribution-----#
	#records_new<-read.csv("Binning_Data/Number_of_records.csv", header=TRUE, dec=".",sep=",", row.names=NULL, stringsAsFactors = FALSE,na.strings="0")

	number_of_bins<-array(0,c(37,2))
	number_of_bins[,1]<-c(1:37)
	mu<-NULL
	sigma<-NULL
	max_r<-NULL
	min_r<-NULL

	ptm_apply<-proc.time()

	mu<-apply(records, 2, function(x) {
		y<-x[which(x>0)]
		mean(y)
	  })
	proc.time()-ptm_apply
	sigma<-apply(records, 2, function(x) {
	  y<-x[which(x>0)]
	  sd(y)
	})
	max_r<-apply(records, 2, function(x) {
	  y<-x[which(x>0)]
	  max(y)
	})
	min_r<-apply(records, 2, function(x) {
	  y<-x[which(x>0)]
	  min(y)
	})

	bins_required<-NULL
	bins_required$estimate<-mu-3*sigma

	bins_required$Number_of_bins<-cut(bins_required$estimate,c(-Inf,2,3,6,8,12,16,24,Inf),labels=c(2,3,6,8,12,16,24,48))

	number_of_bins[,2]<-as.numeric(as.character(bins_required$Number_of_bins))
	no_bins_df<-as.data.frame(number_of_bins)
	colnames(no_bins_df)<-c("Attribute_ID","Number_of_bins")
	write.csv(no_bins_df,file="Binning_Data/Number_of_Bins.csv",row.names=FALSE)
}
