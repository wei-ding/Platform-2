'use strict';

/**
 * @ngdoc function
 * @name SidebarsController
 * @module c-3poElements
 * @kind function
 *
 * @description
 *
 * Handles sidebar element page
 */
angular.module('c-3poElements').
controller('SidebarsController', function ($scope, $mdSidenav) {
    $scope.openSidebar = function(id) {
        $mdSidenav(id).toggle();
    };
});