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
