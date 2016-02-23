var app=angular.module('SearchCtrl',['ngRoute','ui.bootstrap','ngMessages','ngCookies']);
app.constant('url','http://54.205.29.187:8080/clinical3PO/');
//app.constant('url','http://localhost:8080/clinical3PO/');
app.config(function($routeProvider){
	$routeProvider.
	when('/',{
		templateUrl:'blank.html'
	}).when('/MySearches',{
		templateUrl:'MySearches.html',
		controller:'search'
	}).when('/PatHad',{
		templateUrl:'patientSearchHad.html',
		controller:'patientSearch'
	}).otherwise({
		redirectTO:'/'
	});
});
app.config(function($httpProvider) {
	//Enable cross domain calls
	$httpProvider.defaults.useXDomain = true;
	delete $httpProvider.defaults.headers.common['X-Requested-With'];
	$httpProvider.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded; charset=UTF-8';
});
app.controller('con',function($scope,$window,$location,$modal,$http,$cookieStore){

	$scope.logout=function(){
		$window.location.replace("./IndexPage.html");
	};
	$scope.varl=function(id){
	
		$window.open('./patview.html#/'+id);
	};
	$scope.varlo=function(id,val){
	
		$window.open('./patview.html#/'+id+'/'+val);
	};
	
	
});