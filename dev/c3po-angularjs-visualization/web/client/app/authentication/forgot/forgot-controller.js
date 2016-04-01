'use strict';

/**
 * @ngdoc function
 * @name ForgotController
 * @module c-3poAuthentication
 * @kind function
 *
 * @description
 *
 * Handles forgot password form submission and response
 */
angular.module('c-3poAuthentication')
.controller('ForgotController', function ($scope, $state, $mdToast, $filter, $http, API_CONFIG) {
    // create blank user variable for login form
    $scope.user = {
        email: 'info@clinical3po.org',
    };

    // controller to handle login check
    $scope.resetClick = function() {
        $http({
            method: 'POST',
            url: API_CONFIG.url + 'reset',
            data: $scope.user
        }).
        success(function(data) {
            $mdToast.show(
                $mdToast.simple()
                .content($filter('translate')('FORGOT.MESSAGES.RESET_SENT') + ' ' + data.email)
                .position('bottom right')
                .action($filter('translate')('FORGOT.MESSAGES.LOGIN_NOW'))
                .highlightAction(true)
                .hideDelay(0)
            ).then(function() {
                $state.go('public.auth.login');
            });
        }).
        error(function(data) {
            $mdToast.show(
                $mdToast.simple()
                .content($filter('translate')('FORGOT.MESSAGES.NO_RESET') + ' ' + data.email)
                .position('bottom right')
                .hideDelay(5000)
            );
        });
    };
});