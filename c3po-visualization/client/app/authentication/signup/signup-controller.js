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
 * @author Wei Ding
 * @version 0.1.0
**/

/*!
    This content is released under the (http://opensource.org/licenses/MIT) MIT License.

    The MIT License (MIT)

    Copyright (c) <2015> <>

    Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. * MIT 
*/
/*
    replace it soon.
 */
angular.module('c-3poAuthentication')
.controller('SignupController', function ($scope, $state, $mdToast, $http, $filter, API_CONFIG) {
    // create blank user variable for login form
    $scope.user = {
        name: '',
        email: '',
        password: '',
        confirm: ''
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
