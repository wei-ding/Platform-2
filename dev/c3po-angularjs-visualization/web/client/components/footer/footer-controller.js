'use strict';

/**
 * @ngdoc function
 * @name FooterController
 * @module c-3po
 * @kind function
 *
 * @description
 *
 * Handles the footer view
 */
angular.module('c-3po').
controller('FooterController', function ($scope, APP) {
    $scope.footerInfo = {
        appName: APP.name,
        appLogo: APP.logo,
        date: new Date(),
        version: APP.version
    };
});