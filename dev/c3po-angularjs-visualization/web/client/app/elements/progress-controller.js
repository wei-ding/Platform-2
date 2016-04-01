'use strict';

/**
 * @ngdoc function
 * @name ProgressController
 * @module c-3poElements
 * @kind function
 *
 * @description
 *
 * Handles progress element page
 */
angular.module('c-3poElements').
controller('ProgressController', function ($scope, $interval) {
    $scope.determinateValue = 0;
    $scope.bufferValue = 0;
    $interval(function() {
        $scope.determinateValue += 1;
        $scope.bufferValue += 1.5;
        if($scope.determinateValue > 100) {
            $scope.determinateValue = 0;
            $scope.bufferValue = 0;
        }
    }, 100, 0, true);
});