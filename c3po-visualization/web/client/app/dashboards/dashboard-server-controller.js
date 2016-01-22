'use strict';

/**
 * @ngdoc function
 * @name DashboardServerController
 * @module c-3poDashboards
 * @kind function
 *
 *
 */
angular.module('c-3poDashboards').
controller('DashboardServerController', function ($scope, $timeout, $mdToast) {
    $scope.disks = [{
        icon: 'icon-storage',
        name: 'Ubuntu 10.04 LTS Disk Image (10000 MB, ext3)',
        enabled: true
    },{
        icon: 'icon-settings-input-component',
        name: 'Ubuntu 11.10 SSD Image (10224 MB, ext3)',
        enabled: false
    },{
        icon: 'icon-storage',
        name: '256MB Swap Image (256 MB, swap)',
        enabled: true
    }];

    $scope.jobs = [{
        job: 'Host initiated restart',
        time: 'Took: 10 seconds',
        complete: true
    },{
        job: 'Snapshot ',
        time: 'Took: 6minutes 26 seconds',
        complete: false
    }];

    $scope.serverChart = {
        labels: ['Swap space', 'Kernel', 'OS', 'Free space'],
        data: [15, 5, 35, 45]
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