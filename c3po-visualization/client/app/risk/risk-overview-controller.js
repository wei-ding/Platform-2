'use strict';

/**
 * @ngdoc controller 
 * @name RiskOverviewController
 * @module c-3poRisk
 * @kind function
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
/*
    replace it soon.
*/

angular.module('c-3poRisk').
controller('RiskOverviewController', function ($scope, $timeout, $mdToast) {
    $scope.disks = [{
        icon: 'icon-storage',
        name: 'HDFS',
        enabled: true
    },{
        icon: 'icon-settings-input-component',
        name: 'Local'
        enabled: false
    },{
        icon: 'icon-storage',
        name: 'GraphDB',
        enabled: true
    }];

    $scope.jobs = [{
        job: 'MR Job(Cluster)',
        time: '',
        complete: true
    },{
        job: 'MR Job(Rank)',
        time: '',
        complete: false
    },{
        job: 'Sync Job(Cluster)',
        time: '',
        complete: false
    }];

    $scope.serverChart = {
        labels: ['Cluster1'],
        data: [15]
    };

    $timeout(function() {
        $mdToast.show(
            $mdToast.simple()
            .content('Server CPU at 100%!')
            .position('bottom right')
            .hideDelay(3000)
        );
    }, 5000);
});
