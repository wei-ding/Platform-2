'use strict';

/**
 * @ngdoc function
 * @name SwitchesController
 * @module c-3poElements
 * @kind function
 *
 * @description
 *
 * Handles switches element page
 */
angular.module('c-3poElements').
controller('SwitchesController', function ($scope) {
    $scope.toggleAll = function(data, value) {
        for(var x in data) {
            data[x] = value;
        }
    };
});