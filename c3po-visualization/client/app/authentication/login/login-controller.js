'use strict';

/**
 * @ngdoc function
 * @name LoginCtrl
 * @module c-3poAuthentication
 * @kind function
 *
 * @description
 *
 * Handles login form submission and response
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
.controller('LoginController', function ($scope, $state, $mdToast, API_CONFIG) {
    // create blank user variable for login form
    $scope.user = {
        email: '',
        password: ''
    };

    $scope.uservalid = {
        email: '',
        password: ''
    };

    $scope.socialLogins = [{
        icon: 'fa-twitter',
        color: '#5bc0de',
        url: '#'
    },{
        icon: 'fa-facebook',
        color: '#337ab7',
        url: '#'
    },{
        icon: 'fa-google-plus',
        color: '#e05d6f',
        url: '#'
    },{
        icon: 'fa-linkedin',
        color: '#337ab7',
        url: '#'
    }];
    $scope.toastPosition = {
        bottom: true,
        top: true,
        left: true,
        right:true 
    };
    $scope.getToastPosition = function() {
        return Object.keys($scope.toastPosition)
        .filter(function(pos) { return $scope.toastPosition[pos]; })
        .join(' ');
    };

    $scope.closeToast = function() {
     $mdToast.hide();
    };

    var toast = $mdToast.simple()
        .content("Server said your email/pass doesn't match existing users.")
        .highlightAction(false)
        .hideDelay(100)
        .position($scope.getToastPosition());

    // controller to handle login check
    $scope.loginClick = function() {
        $http({
            method: 'POST',
            url: API_CONFIG.url + 'login',
            data: $scope.user
        }).
        success(function(data) {
            $mdToast.show(
                $state.go('admin-panel.default.risk-cluster');
            ).then(function() {
                $mdToast.show(toast).then(function() {
                });
            });
        };
    };
});
