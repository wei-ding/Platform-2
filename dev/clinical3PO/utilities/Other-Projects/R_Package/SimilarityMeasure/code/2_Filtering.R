#Documentation

#Remove patients who have very few regardings for a particular attribute


filter_patients <- function(working_directory,index_patient_id,index_attribute){
	setwd(working_directory)


	bins_file<-paste(index_attribute,"_bins/Bins.csv",sep="")
	binning_data<-read.csv(bins_file, header=TRUE, dec=".",sep=",", row.names=NULL, stringsAsFactors = FALSE)
	number_of_bins<-ncol(binning_data)-1

	index_patient<-binning_data[which(binning_data$person_id==index_patient_id),]
	min_non_empty_bins<-floor(length(which(index_patient[2:ncol(binning_data)]>-10))/2)

	filtered_patients<-NULL

	na_count<-NULL
	na_count<-apply(binning_data, 1, function(x){
	  length(which(is.na(x[2:(number_of_bins+1)])==TRUE))
	})

	filtered_patients<-binning_data$person_id[which(na_count<=(number_of_bins-min_non_empty_bins))] #change focus to non NA
	filtered_patients_df<-as.data.frame(filtered_patients)

	patient_dir<-paste(index_patient_id,index_attribute,"analysis",sep="_")
	dir.create(patient_dir,showWarnings = FALSE)
	out_file<-paste(patient_dir,"/filtered_patients.csv",sep="")
	colnames(filtered_patients_df)<-"person_id"
	write.csv(filtered_patients_df,file=out_file,row.names=FALSE)
}

