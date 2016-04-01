'use strict';

/**
* @ngdoc directive
* @name todoWidget
* @restrict E
* @scope
*
* @description
*
* Adds some todo data
*
* @usage
* ```html
* <widget todo-widget></widget>
* ```
*/
angular.module('c-3poDashboards')
.directive('todoWidget', function() {
    return {
        require: 'widget',
        restrict: 'A',
        link: function($scope) {
            $scope.todos = [{
                name: 'Buy Milk',
                done: false
            },{
                name: 'Fix Server',
                done: true
            },{
                name: 'Walk the dog',
                done: false
            },{
                name: 'Upload files',
                done: false
            }];
        }
    };
});