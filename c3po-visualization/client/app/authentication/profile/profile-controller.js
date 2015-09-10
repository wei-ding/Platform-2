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
.controller('ProfileController', function ($scope, $state) {
    // create blank user variable for login form
    $scope.user = {
        name: '',
        email: '',
        location: '',
        website: '',
        twitter: '',
        bio: '',
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
