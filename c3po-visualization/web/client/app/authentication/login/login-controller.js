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
 */
angular.module('c-3poAuthentication')
.controller('LoginController', function ($scope, $state, $mdToast) {
    // create blank user variable for login form
    $scope.user = {
        email: '',
        password: ''
    };

    $scope.uservalid = {
        email: 'info@clinical3po.org',
        password: 'd3m0'
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
        if ($scope.user.email == $scope.uservalid.email && $scope.uservalid.password == $scope.user.password ) {
            $state.go('admin-panel.default.risk-cluster');
        } else {
            $mdToast.show(toast).then(function() {
            });
        };
    };
});
