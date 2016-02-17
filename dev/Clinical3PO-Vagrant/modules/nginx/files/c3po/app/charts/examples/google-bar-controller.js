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
controller('GoogleChartsBarController', function ($scope) {
    $scope.barChart = {
        type: 'Bar',
        data: [
            ['Year', 'Sales', 'Expenses', 'Profit'],
            ['2014', 1000, 400, 200],
            ['2015', 1170, 460, 250],
            ['2016', 660, 1120, 300],
            ['2017', 1030, 540, 350]
        ],
        options: {
            chart: {
                title: 'Company Performance',
                subtitle: 'Sales, Expenses, and Profit: 2014-2017',
            },
            bars: 'vertical',
            width: 800,
            height: 600
        }
    };
});
