#Documentation

# Compute the Euclidean Distance between the index patient and every patient in the "filtered list of patients".

	compute_euclidean_distance <- function(working_directory,index_patient_id,index_attribute){
	setwd(working_directory)


	bins_file<-paste(index_attribute,"_bins/Bins.csv",sep="")
	binning_data<-read.csv(bins_file, header=TRUE, dec=".",sep=",", row.names=NULL, stringsAsFactors = FALSE)
	number_of_bins<-ncol(binning_data)-1

	index_patient<-binning_data[which(binning_data$person_id==index_patient_id),]
	non_empty_index_patient<-which(is.na(index_patient[2:ncol(index_patient)])==FALSE)
	distance<-array(0,c(4000,2))

	patient_dir<-paste(index_patient_id,index_attribute,"analysis",sep="_")
	filtered_file<-paste(patient_dir,"/filtered_patients.csv",sep="")

	filtered_patients<-read.csv(filtered_file, header=TRUE, dec=".",sep=",", row.names=NULL, stringsAsFactors = FALSE)

	distance[,1]<-binning_data[,1]


	distance[,2]<-sapply(seq_along(binning_data$person_id),function(j) {
	  
	  compare_patient<-binning_data[j,]
	  
	  if(length(which(filtered_patients==distance[j,1]))==1){
		
		non_empty_compare_patient<-which(is.na(compare_patient[2:ncol(compare_patient)])==FALSE)
		non_empty<-intersect(non_empty_index_patient,non_empty_compare_patient)
		distance_temp<-(index_patient[non_empty+1]-compare_patient[non_empty+1])^2
		as.numeric(mean(distance_temp))
		
	  }  
	  else{
		Inf
	  }

	})

	distance_sorted<-distance[order(distance[,2]),]
	colnames(distance_sorted)<-c("person_id","Euclidean_distance")
	write.csv(distance_sorted,file=file.path(patient_dir,"Euclidean_Distance.csv"),row.names=FALSE)
}