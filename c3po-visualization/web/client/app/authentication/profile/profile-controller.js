'use strict';

/**
 * @ngdoc function
 * @name ProfileController
 * @module c-3poAuthentication
 * @kind function
 *
 * @description
 *
 * Handles settings form fields
 */
angular.module('c-3poAuthentication')
.controller('ProfileController', function ($scope, $state) {
    // create blank user variable for login form
    $scope.user = {
        name: 'John',
        email: 'john@clinical3po.org',
        location: 'Charleston, SC',
        website: 'http://www.clinical3po.org',
        twitter: 'c-3po',
        bio: 'Big Data pilot finds hidden patterns in EHR',
        current: '',
        password: '',
        confirm: ''        
    };

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

    // controller to handle login check
    $scope.updateSettingsClick = function() {
        // TODO:probably display a toast here.
        $state.go('admin-panel.default.profile');
    };
});
