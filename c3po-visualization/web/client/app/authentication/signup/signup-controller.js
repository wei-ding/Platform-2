'use strict';

/**
 * @ngdoc function
 * @name SignupController
 * @module c-3poAuthentication
 * @kind function
 *
 * @description
 *
 * Handles sending of signup info to api and response
 */
angular.module('c-3poAuthentication')
.controller('SignupController', function ($scope, $state, $mdToast, $http, $filter, API_CONFIG) {
    // create blank user variable for login form
    $scope.user = {
        name: 'john',
        email: 'john@clinical3po.org',
        password: '123456789',
        confirm: '123456789'
    };

    $scope.signupClick = function() {
        $http({
            method: 'POST',
            url: API_CONFIG.url + 'signup',
            data: $scope.user
        }).
        success(function(data) {
            $mdToast.show(
                $mdToast.simple()
                .content($filter('translate')('SIGNUP.MESSAGES.CONFIRM_SENT') + ' ' + data.email)
                .position('bottom right')
                .action($filter('translate')('SIGNUP.MESSAGES.LOGIN_NOW'))
                .highlightAction(true)
                .hideDelay(0)
            ).then(function() {
                $state.go('public.auth.login');
            });
        }).
        error(function() {
            $mdToast.show(
                $mdToast.simple()
                .content($filter('translate')('SIGNUP.MESSAGES.NO_SIGNUP'))
                .position('bottom right')
                .hideDelay(5000)
            );
        });
    };
});
