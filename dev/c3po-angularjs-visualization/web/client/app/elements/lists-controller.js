'use strict';

/**
 * @ngdoc function
 * @name ListsController
 * @module c-3poElements
 * @kind function
 *
 * @description
 *
 * Handles dialog element page
 */
angular.module('c-3poElements').
controller('ListsController', function ($scope, emails) {
    $scope.emails = emails.data.splice(0, 5);
});