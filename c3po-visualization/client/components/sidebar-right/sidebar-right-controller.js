'use strict';

/**
 * @ngdoc function
 * @name SidebarRightController
 * @module c-3po
 * @kind function
 *
 * @description
 *
 * Handles the admin view
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
angular.module('c-3po').
controller('SidebarRightController', function ($scope, $http, $mdSidenav, $state, API_CONFIG) {
    // sets the current active tab
    $scope.notificationsTab = 0;

    // add an event to switch tabs (used when user clicks a menu item before sidebar opens)
    $scope.$on('cylonSwitchNotificationTab', function($event, tab) {
        $scope.notificationsTab = tab;
    });

    // fetch some dummy emails from the API
    $http({
        method: 'GET',
        url: API_CONFIG.url + 'email/inbox',
    }).success(function(data) {
        $scope.emails = data;
    }).
    error(function() {
        console.error('Cant get email list');
    });

    // create some dummy notification Groups and notifications
    $scope.notificationGroups = [{
        name: 'Query sync',
        notifications: [{
            title: 'Query 1: Updated',
            icon: 'fa fa-tasks',
            iconColor: '#55acee',
            date: moment().startOf('hour'),
        },{
            title: 'Query 2: Started syncing',
            icon: 'fa fa-tasks',
            iconColor: '#55acee',
            date: moment().startOf('hour'),
        },{
            title: 'Query 3: Failed',
            icon: 'fa fa-tasks',
            iconColor: '#55acee',
            date: moment().startOf('hour'),
        },{
            title: 'Query 4: Initializing data',
            icon: 'fa fa-tasks',
            iconColor: '#55acee',
            date: moment().startOf('hour'),
        }]
    },{
        name: 'P',
        notifications: [{
            title: 'Server Down',
            icon: 'icon-error',
            iconColor: 'rgb(244, 67, 54)',
            date: moment().startOf('hour'),
        },{
            title: 'Slow Response Time',
            icon: 'icon-warning',
            iconColor: 'rgb(255, 152, 0)',
            date: moment().startOf('hour'),
        },{
            title: 'Server Down',
            icon: 'icon-error',
            iconColor: 'rgb(244, 67, 54)',
            date: moment().startOf('hour'),
        }]
    },{
        name: 'Background Jobs',
        notifications: [{
            title: 'Query1: Data sync',
            icon: 'fa fa-exchange',
            iconColor: 'rgb(255, 51, 51)',
            date: moment().startOf('hour'),
        },{
            title: 'Query1: Data Summary',
            icon: 'fa fa-exchange',
            iconColor: 'rgb(255, 152, 0)',
            date: moment().startOf('hour'),
        },{
            title: 'Query 1: Data feedback',
            icon: 'fa fa-exchange',
            iconColor: 'rgb(76, 175, 80)',
            date: moment().startOf('hour'),
        },{
            title: 'Query 2: Data sync',
            icon: 'fa fa-exchange',
            iconColor: 'rgb(76, 175, 80)',
            date: moment().startOf('hour'),
        }]
    }];

    // create some dummy user settings
    $scope.settingsGroups = [{
        name: 'ADMIN.NOTIFICATIONS.ACCOUNT_SETTINGS',
        settings: [{
            title: 'ADMIN.NOTIFICATIONS.SHOW_LOCATION',
            icon: 'icon-location-on',
            enabled: true
        },{
            title: 'ADMIN.NOTIFICATIONS.SHOW_AVATAR',
            icon: 'icon-face-unlock',
            enabled: false
        },{
            title: 'ADMIN.NOTIFICATIONS.SEND_NOTIFICATIONS',
            icon: 'icon-notifications-on',
            enabled: true
        }]
    },{
        name: 'ADMIN.NOTIFICATIONS.CHAT_SETTINGS',
        settings: [{
            title: 'ADMIN.NOTIFICATIONS.SHOW_USERNAME',
            icon: 'icon-person',
            enabled: true
        },{
            title: 'ADMIN.NOTIFICATIONS.SHOW_PROFILE',
            icon: 'icon-account-box',
            enabled: false
        },{
            title: 'ADMIN.NOTIFICATIONS.ALLOW_BACKUPS',
            icon: 'icon-backup',
            enabled: true
        }]
    }];

    // create some dummy user stats
    $scope.statisticsGroups = [{
        name: 'ADMIN.NOTIFICATIONS.USER_STATS',
        stats: [{
            title: 'ADMIN.NOTIFICATIONS.STORAGE_SPACE',
            mdClass: 'md-primary',
            value: 60
        },{
            title: 'ADMIN.NOTIFICATIONS.BANDWIDTH_USAGAE',
            mdClass: 'md-accent',
            value: 10
        },{
            title: 'ADMIN.NOTIFICATIONS.MEMORY_USAGAE',
            mdClass: 'md-warn',
            value: 100
        }]
    },{
        name: 'ADMIN.NOTIFICATIONS.SERVER_STATS',
        stats: [{
            title: 'ADMIN.NOTIFICATIONS.STORAGE_SPACE',
            mdClass: 'md-primary',
            value: 60
        },{
            title: 'ADMIN.NOTIFICATIONS.BANDWIDTH_USAGAE',
            mdClass: 'md-accent',
            value: 10
        },{
            title: 'ADMIN.NOTIFICATIONS.MEMORY_USAGAE',
            mdClass: 'md-warn',
            value: 100
        }]
    }];

    $scope.openMail = function() {
        $state.go('private.admin.toolbar.inbox');
        $scope.close();
    };

    $scope.close = function () {
        $mdSidenav('notifications').close();
    };
});
