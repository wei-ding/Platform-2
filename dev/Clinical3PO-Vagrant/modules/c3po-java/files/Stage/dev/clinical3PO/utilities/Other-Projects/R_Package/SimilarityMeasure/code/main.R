
similarity_measure<-function(working_directory,observation_file,index_patient_id,index_attribute,Starttime){
	setwd(working_directory)
	#source("code/0_Decide_number_of_bins.R")
	source("code/1_Binning.R")
	source("code/2_Filtering.R")
	source("code/3_Euclidean_Distance.R")

	#decide_number_of_bins(working_directory,observation_file)              # This function is not a good measure for all attributes. Please use default "suggested_number_of_bins"
	create_bins(working_directory,observation_file,index_attribute,Starttime)         # Run only once for a particular attribute
	filter_patients(working_directory,index_patient_id,index_attribute)
	compute_euclidean_distance(working_directory,index_patient_id,index_attribute)
}

