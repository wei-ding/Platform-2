'use strict';

/**
 * @ngdoc function
 * @name SeedPageController
 * @module SeedModule
 * @kind function
 *
 * @description
 *
 * Creates some dummy test data for the test seed page
 */

// get the c3po seed module
angular.module('c-3poRisk')
// create a controller for the seed page and inject the scope directive
.controller('RiskClusterController', function ($scope, $window, $interval) {
    // add some test data to the scope
    $scope.testData = ['c3po', 'is', 'great'];

    $scope.clusterurl = "app/dashboards/data/cluster.json";

    //angular.element($window).on('resize', $scope.$apply.bind($scope));

    angular.element($window).on('resize', function(){ $scope.$apply() }) 

    $scope.employers = [
        { value: 3.2, name: 'United States Department of Defense' }
        , { value: 2.3, name: 'People\'s Liberation Army' }
        , { value: 2.1, name: 'Walmart' }
        , { value: 1.9, name: 'McDonald\'s' }
        , { value: 1.7, name: 'National Health Service' }
        , { value: 1.6, name: 'China National Petroleum Corporation' }
        , { value: 1.5, name: 'State Grid Corporation of China' }
        , { value: 1.4, name: 'Indian Railways' }
        , { value: 1.3, name: 'Indian Armed Forces' }
        , { value: 1.2, name: 'Hon Hai Precision Industry (Foxconn)' }
    ]

    $scope.data = $scope.employers;
    $scope.piedata = [5, 10, 20, 45, 6];
    $scope.updatePieData = function() {
        $scope.piedata = [];
        var numDataPoints = 5;
        var range = Math.random() * 10;
        for (var i = 0; i < numDataPoints; i++) {
        var newNumber = Math.floor(Math.random() * range);
        if (newNumber == 0) {
            $scope.piedata.push(1);
        } else {
            $scope.piedata.push(newNumber);
        }

        }
    };

    $scope.bardata = [5, 8, 10, 2, 4, 2, 9];

    $scope.updateBarData = function() {
        $scope.bardata = [];
        var numDataPoints = 7;
        var range = Math.random() * 10;
        for (var i = 0; i < numDataPoints; i++) {
        var newNumber = Math.floor(Math.random() * range);
        $scope.bardata.push(newNumber);
        }
    };

    $scope.scatterdata = [
        [83, 104],
        [15, 200],
        [92, 100],
        [55, 55],
        [20, 143]
    ];

    $scope.updateScatterData = function() {
        $scope.scatterdata = [];
        var numDataPoints = 20;
        var xRange = Math.random() * 120;
        var yRange = Math.random() * 250;
        for (var i = 0; i < numDataPoints; i++) {
        var newNumber1 = Math.floor(Math.random() * xRange);
        var newNumber2 = Math.floor(Math.random() * yRange);
        $scope.scatterdata.push([newNumber1, newNumber2]);
        }
    }

});
