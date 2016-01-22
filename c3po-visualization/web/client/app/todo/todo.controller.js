'use strict';

/**
 * @ngdoc function
 * @name TodoController
 * @module c-3poTodo
 * @kind function
 *
 * @description
 *
 * Handles the todo app model and controls
 */
angular.module('c-3poTodo')
.controller('TodoController', function ($scope, $state, $mdDialog) {    
    
    $scope.messages = [
	    {description: 'Material Design', priority: 'high', selected: true},	   
	    {description: 'Install espresso machine', priority: 'high', selected: false}, 
	    {description: 'Deploy to Server', priority: 'medium', selected: true},
	    {description: 'Cloud Sync', priority: 'medium', selected: false},
	    {description: 'Test Configurations', priority: 'low', selected: false},	    
	    {description: 'Validate markup', priority: 'low', selected: false},
	    {description: 'Debug javascript', priority: 'low', selected: true},
	    {description: 'Arrange meeting', priority: 'low', selected: true},
	];

	$scope.orderTasks = function(task) {
		switch(task.priority){
			case 'high':
				return 1;
			case 'medium':
				return 2;
			case 'low':
				return 3;
			default: // no priority set
				return 4;
		}
	};

	$scope.addTodo = function( ev ){			
	   $mdDialog.show({
            templateUrl: 'app/todo/add-todo-dialog.tmpl.html',
            targetEvent: ev,
            controller: 'DialogController'
        })
        .then(function(answer) {                           
            $scope.messages.push(answer);      
        });
	}; 

	$scope.removeTodo = function( msg ){	
	   	for(var i = $scope.messages.length - 1; i >= 0; i--) {
		    if($scope.messages[i] === msg) {
		    	$scope.messages.splice(i, 1);
		    }
		}
	}; 
});