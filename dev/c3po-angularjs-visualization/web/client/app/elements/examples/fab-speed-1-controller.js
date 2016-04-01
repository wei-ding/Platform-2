'use strict';

/**
 * @ngdoc function
 * @name ChipsController
 * @module c-3poElements
 * @kind function
 *
 * @description
 *
 * Handles buttons element page
 */
angular.module('c-3poElements').
controller('Fab1Controller', function ($scope, $mdToast, $element) {
    $scope.fabDirections = ['up', 'down', 'left', 'right'];
    $scope.fabDirection = $scope.fabDirections[0];

    $scope.fabAnimations = ['md-fling', 'md-scale'];
    $scope.fabAnimation = $scope.fabAnimations[0];

    $scope.fabStatuses = [false, true];
    $scope.fabStatus = $scope.fabStatuses[0];

    $scope.share = function(message) {
        $mdToast.show({
            template: '<md-toast><span flex>' + message + '</span></md-toast>',
            position: 'top right',
            hideDelay: 3000,
            parent: $element
        });
    };
});