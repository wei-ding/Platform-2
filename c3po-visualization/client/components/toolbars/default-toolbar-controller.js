'use strict';

/**
 * @ngdoc function
 * @name EmailToolbarController
 * @module c-3poEmail
 * @kind function
 *
 * @description
 *
 * Handles all actions for email toolbar
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

angular.module('c-3po')
.controller('DefaultToolbarController', function ($scope, $translate, $state, $element, $mdUtil, $mdSidenav, $timeout, $mdToast, SideMenu, APP) {
    $scope.menu = SideMenu.getMenu();

    $scope.toastPosition = {
        bottom: false,
        top: true,
        left: false,
        right: true
    };
    $scope.getToastPosition = function() {
        return Object.keys($scope.toastPosition)
        .filter(function(pos) { return $scope.toastPosition[pos]; })
        .join(' ');
    };

    $scope.closeToast = function() {
     $mdToast.hide();
    };

    $scope.toolbarTypeClass = function() {
        return $scope.extraClass;
    };

    $scope.$on('$stateChangeStart', initToolbar);

    function initToolbar() {
        $element.css('background-image', '');

        if($state.current.data !== undefined) {
            if($state.current.data.toolbar !== undefined) {
                if($state.current.data.toolbar.extraClass !== false) {
                    $scope.extraClass = $state.current.data.toolbar.extraClass;
                }

                if($state.current.data.toolbar.background) {
                    $element.css('background-image', 'url(' + $state.current.data.toolbar.background + ')');
                }
            }
        }

        var activeLanguage = $translate.use() ||
            $translate.storage().get($translate.storageKey()) ||
            $translate.preferredLanguage();
        if ( activeLanguage == 'cn' ) {
            $scope.currentlangicon = "assets/images/flag/cn.png"
        } else {
            $scope.currentlangicon = "assets/images/flag/us.png"
        }
    };

    initToolbar();


    $scope.switchLanguage = function(languageCode) {
        var toast = $mdToast.simple()
            .content(languageCode)
            .highlightAction(false)
            .hideDelay(100)
            .position($scope.getToastPosition());

        $translate.use(languageCode).then(function() {
            if(languageCode == "cn") {
                $scope.currentlangicon = "assets/images/flag/cn.png"; 
            } else {
                $scope.currentlangicon = "assets/images/flag/us.png"; 
            }
        });
        $mdToast.show(toast).then(function() {
        });
            
    };


    $scope.openSideNav = function(navID) {
        $mdUtil.debounce(function(){
            $mdSidenav(navID).toggle();
        }, 300)();
    };

    $scope.toggleNotificationsTab = function(tab) {
        $scope.$parent.$broadcast('cylonSwitchNotificationTab', tab);
        $scope.openSideNav('notifications');
    };

    $scope.profile = function() {
        $state.go('admin-panel.default.profile');
    };

    $scope.logout = function() {
        $state.go('authentication.login');
    };

    $scope.$on('newMailNotification', function(){
        $scope.emailNew = true;
    });        

    // until we can get languages from angular-translate use APP constant
    $scope.languages = APP.languages;
});
