'use strict';

/**
 * @ngdoc controller
 * @name RiskClusterController
 * @module c-3poRisk
 * @kind controller
 *
 * @description
 *
 * Display cluster and cluster rank charts
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

angular.module('c-3poRisk')
// create a controller for the seed page and inject the scope directive
.controller('RiskClusterController', function ($scope, $window, $interval, $http, API_CONFIG)  {
    // add some test data to the scope
    $scope.clusterdata = [];
    $scope.url = API_CONFIG.url + 'cluster'; 
    $http.get(scope.url).
        success(function(data) {
            $scope.clusterdata = data;
        });


    angular.element($window).on('resize', $scope.$apply.bind($scope));


});
