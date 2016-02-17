'use strict';

/**
 * @ngdoc function
 * @name GoogleBarController
 * @module c-3poElements
 * @kind function
 *
 * @description
 *
 * Handles bar chart
 */
angular.module('c-3poElements').
controller('ChartJsLineController', function ($scope, $interval) {
    $scope.labels = ['January', 'February', 'March', 'April', 'May', 'June', 'July'];
    $scope.series = ['Series A', 'Series B', 'Series C'];
    $scope.options = {
        datasetFill: false
    };

    function randomData() {
        $scope.data = [];
        for(var series = 0; series < $scope.series.length; series++) {
            var row = [];
            for(var label = 0; label < $scope.labels.length; label++) {
                row.push(Math.floor((Math.random() * 100) + 1));
            }
            $scope.data.push(row);
        }
    }

    randomData();

    // Simulate async data update
    $interval(randomData, 5000);
});
