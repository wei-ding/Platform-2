'use strict';

/**
 * @ngdoc function
 * @name RiskServerController
 * @module c-3poRisk
 * @kind function
 * @
 *
 *
 */
angular.module('c-3poRisk').
controller('RiskRanksController', function ($scope, $timeout, $mdToast, $rootScope, $http) {        
    /*
    $timeout(function() {
        $rootScope.$broadcast('newMailNotification');
        $mdToast.show({
            template: '<md-toast><span flex>You have new email messages! View them <a href="" ng-click=viewUnread()>here</a></span></md-toast>',
            controller: 'ToastCtrl',
            position: 'bottom right',
            hideDelay: 5000
        });
    }, 10000);
    */

    /* ### Simple server paging ### */
    $scope.rankPaging = {
        data:null,
        display:0,
        currentPage:0,
        total:0,
        pages:0
    };

    var rankurl = 'app/risk/data/rank.json';
    $scope.loadRankData = function(){
        //load data
        $http.get(rankurl).
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

