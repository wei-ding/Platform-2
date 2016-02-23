#Documentation

#This is a one time execution for a particular attribute. This creates the bins and stores the median as its bin value

#Input: observation file, index attribute and Number of bins for that attribute
#Output: Bins.csv under the folder "<index attribute>_bins"

#Once this is created, any query with a patient ID can be intiated for this particular attribute. In case the "Number_of_bins.csv" is modified, then this script should be executed again


create_bins<-function(working_directory,observation_file,index_attribute,Starttime){
	setwd(working_directory)

	check_file<-paste(index_attribute,"_bins/Bins.csv",sep="")
	if(file.exists(check_file)){
	  print("Binning data exists for this attribute. Skipping binning process")
	}
	else{
		observation_Data <- read.csv(observation_file, header=TRUE, dec=".",sep=";", row.names=NULL, stringsAsFactors = FALSE)
		Binning_Data <- read.csv("Binning_Data/Number_of_Bins.csv", header=TRUE, dec=".",sep=",", row.names=NULL, stringsAsFactors = FALSE)

		#index_attribute<-15
		number_of_bins<-Binning_Data[which(Binning_Data[,1]==index_attribute),2]       #Must be a factor of 48

		patient_list<-unique(observation_Data$person_id)

		observation_Data<-subset(observation_Data, observation_concept_id==index_attribute)

		observation_Data$date_time <- as.POSIXct(paste(observation_Data$observation_date, observation_Data$observation_time), format = "%m/%d/%y %H:%M")
		start_time <- unclass(as.POSIXct(Starttime))
		observation_Data$time <- (unclass(observation_Data$date_time) - start_time)/3600


		initialize<-rep(-10,length(patient_list)*number_of_bins)
		bins<-array(initialize,c(length(patient_list),number_of_bins))


		bins<-sapply(seq_along(patient_list), function (j) {
		  
		  current_patient<-observation_Data[which(observation_Data$person_id==patient_list[j]),]
		  
			
			index_to_bin<-NULL                   #map index to bins and later use to compute median
			bin_values<-NULL
			index_to_bin$index<-c(1:length(current_patient$observation_id)) 
			start<-48/number_of_bins
			bin_size<-start
			index_to_bin$bin_number<-cut(as.numeric(current_patient$time),c(-Inf,-0.1,seq(start,48,bin_size),Inf),labels=c(-10,1:number_of_bins,-20)) 
			
			#Assign bin values
			unique_bin_numbers<-sort(unique(index_to_bin$bin_number))
			bin_values<- sapply(1:number_of_bins, function (x) {
			  sub_data<-current_patient$value_as_number[which(index_to_bin$bin_number==x)]
			  median(sub_data)
			})
			return (bin_values)

		})
		bins_transpose<-t(bins)
		bins_df<-as.data.frame(bins_transpose)
		bins_df$person_id<-patient_list
		bins_df<-bins_df[,c(number_of_bins+1,1:number_of_bins)]
		out_dir<-paste(index_attribute,"bins",sep="_")
		dir.create(out_dir, showWarnings = FALSE)
		outfile<-paste(out_dir,"/Bins.csv",sep="")
		write.csv(bins_df,file=outfile,row.names=FALSE)
	}
}



