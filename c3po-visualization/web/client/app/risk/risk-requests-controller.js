'use strict';

/**
 * @ngdoc function
 * @name RiskServerController
 * @module c-3poRisk
 * @kind function
 *
 *
 */
angular.module('c-3poRisk').
controller('RiskRequestsController', function ($scope, $timeout, $mdToast, $rootScope) {        
    $timeout(function() {
        $rootScope.$broadcast('newMailNotification');
        $mdToast.show({
            template: '<md-toast><span flex>You have new email messages! View them <a href="" ng-click=viewUnread()>here</a></span></md-toast>',
            controller: 'ToastCtrl',
            position: 'bottom right',
            hideDelay: 5000
        });
    }, 10000);
})

.controller('ToastCtrl', function($scope, $mdToast, $state) {
    $scope.viewUnread = function() {        
        $state.go('admin-panel-no-scroll.email.inbox');
    };
});

