'use strict';

/**
 * @ngdoc function
 * @name LoginCtrl
 * @module c3po.authentication
 * @kind function
 *
 * @description
 *
 * Handles lock screen login
 *
 */
angular.module('c-3poAuthentication')
.controller('LockController', function ($scope, $state) {
    $scope.user = {
        name: 'John',
        email: 'info@clinical3po.org',
        password: 'demo'
    };

    // controller to handle login check
    $scope.loginClick = function() {        
        // user logged in ok so goto the dashboard
        $state.go('admin-panel.default.dashboard-general');        
    };


    $scope.logoutClick = function() {        
        // go back to login screen
        $state.go('public.auth.login');
    };
});