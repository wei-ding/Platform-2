'use strict';

/**
 * @ngdoc function
 * @name BindingController
 * @module c-3poElements
 * @kind function
 *
 * @description
 *
 * Handles binding forms page
 */
angular.module('c-3poForms').
controller('Binding1Controller', function ($scope) {
    $scope.user = {
        username: 'Morris',
        password: '',
        description: '',
        favouriteColor: ''
    };
});