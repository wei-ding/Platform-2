'use strict';

/**
 * @ngdoc controller
 * @name RiskRanksController
 * @module c-3poRisk
 * @kind controller
 * @
 *
 *
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
 */
angular.module('c-3poRisk').
controller('RiskRanksController', function ($scope, $timeout, $mdToast, $rootScope, $http, API_CONFIG) {        
    /* ### Simple server paging ### */
    $scope.rankPaging = {
        data:null,
        display:0,
        currentPage:0,
        total:0,
        pages:0
    };

    $scope.rankurl = API_CONFIG.url + 'clusterrank'; 

    $scope.loadRankData = function(){
        //load data
        $http.get($scope.rankurl).
            success(function(data, status, headers, config) {
                $scope.rankPaging.data = data;
            }).
            error(function(data, status, headers, config) {
                $scope.rankPaging.data = null;
                // log error
            });
    };


    // load first page
    $scope.loadRankData();    
    $scope.userCluster = '';
    $scope.clusters = (' ,CV Risk/Obesity,Healthy,Hyperlipidemia,Multi-Chronic,Obesity,COPD/Asthma,' +
        'Renal Disease').split(',').map(function (cluster) { return { abbrev: cluster }; });
})

.controller('ToastCtrl', function($scope, $mdToast, $state) {
    $scope.viewUnread = function() {        
        $state.go('admin-panel-no-scroll.email.inbox');
    };
});

