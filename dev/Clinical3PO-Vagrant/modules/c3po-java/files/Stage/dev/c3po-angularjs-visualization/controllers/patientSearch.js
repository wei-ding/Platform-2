app.controller('patientSearch',function($scope,$http,$location,url){
	$scope.reset=function(){
		$scope.patientid=null;
	};
	$scope.patientSearch=function(){
		var data2 = 'patientId=' + encodeURIComponent($scope.patientid);
		$http.post(url+'ang/PatientSearch/patientSearch',data2).success(function(data){

			$location.path(data.url);
		});
	};
});