app1.constant('url','http://54.205.29.187:8080/clinical3PO/');
//app1.constant('url','http://localhost:8080/clinical3PO/');
app1.controller('cntrl',function($rootScope,$scope,$http,$location,$window,url,$cookieStore){
	$scope.data=null;

	$scope.login=function(){
		console.log($scope.credentials.username);

		var data1={
				"name":$scope.credentials.username,
				"password":$scope.credentials.password,
				"Submit":"Submit"};
				
		var data2 = 'name=' + encodeURIComponent($scope.credentials.username) +
				'&password=' + encodeURIComponent($scope.credentials.password);
		$http.post(url+'ang/MySearch/user2',data2).success(function(data){
			if(data.name==$scope.credentials.username & data.password==$scope.credentials.password){
			
				$window.location.replace("./homePage.html");
			}else{

				$scope.credentials.sub='true';

				$scope.data="Your login attempt was not successful,try again";
			}
		}).error(function(){
			alert("error");
		});


	};
	$scope.val=function(){
		$scope.credentials.username=null;
		$scope.credentials.password=null;
		$scope.credentials.sub='false';
		$scope.data=null;
	};
});