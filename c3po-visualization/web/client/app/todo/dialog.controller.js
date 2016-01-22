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
.controller('DialogController', function ($scope, $state, $mdDialog) {    
    
    $scope.item = {
        description: '',
        priority: '',
        selected: false
    };
    
    $scope.hide = function() {
        $mdDialog.hide($scope.item);
    };
    
    $scope.cancel = function() {
        $mdDialog.cancel();
    };
    
});