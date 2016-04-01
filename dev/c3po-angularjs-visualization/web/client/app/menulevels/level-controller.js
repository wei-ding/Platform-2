'use strict';

/**
 * @ngdoc function
 * @name LevelController
 * @module c-3po
 * @kind function
 *
 * @description
 *
 * Handles basic Menu Level Template
 */
angular.module('c-3po')
.controller('LevelController', function ($scope, $stateParams) {
    $scope.level = $stateParams.level;
});