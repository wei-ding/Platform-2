'use strict';

/**
 * @ngdoc module
 * @name c3po.risk
 * @description
 *
 * The `c3po.risk` module handles the most common dashboard pages.
 */
angular.module('c-3poRisk', [])
.config(function ($translatePartialLoaderProvider, $stateProvider, $urlRouterProvider) {
    $translatePartialLoaderProvider.addPart('app/risk');

    $stateProvider
    .state('admin-panel.default.risk-setup', {
        url: '/risk/setup',
        templateUrl: 'app/risk/risk-setup',
        controller: 'RiskRequestsController',
        authenticate: true,
    })
    .state('admin-panel.default.risk-rank', {
        url: '/risk/rank',
        templateUrl: 'app/risk/risk-rank.html',
        controller: 'RiskRanksController',
        authenticate: true,
    })
    .state('admin-panel.default.risk-cluster', {
        url: '/risk/cluster',
        controller: 'RiskClusterController',
        templateUrl: 'app/risk/risk-cluster.html',
        authenticate: true,
    })
    .state('admin-panel.default.risk-overview', {
        url: '/risk/overview',
        templateUrl: 'app/risk/risk-overview.html',
        controller: 'RiskOverviewController',
        authenticate: true
    });
    // Send to login if the URL was not found
    $urlRouterProvider.otherwise("/login");
})
.run(function(SideMenu) {
    SideMenu.addMenu({
        name: 'MENU.RISK.RISK',
        icon: 'icon-quick-contacts-dialer',
        type: 'dropdown',
        priority: 2.1,
        children: [{
            name: 'MENU.RISK.REQUESTS',
            state: 'admin-panel.default.risk-setup',
            type: 'link',
        },{
            name: 'MENU.RISK.OVERVIEW',
            state: 'admin-panel.default.risk-overview',
            type: 'link',
        },{
            name: 'MENU.RISK.CLUSTERS',
            state: 'admin-panel.default.risk-cluster',
            type: 'link',
        },{
            name: 'MENU.RISK.RANK',
            state: 'admin-panel.default.risk-rank',
            type: 'link',
        }]
    });
});
