'use strict';

/**
 * @ngdoc module
 * @name c3po.dashboards
 * @description
 *
 * The `c3po.dashboards` module handles the most common dashboard pages.
 */
angular.module('c-3poDashboards', [])
.config(function ($translatePartialLoaderProvider, $stateProvider, $urlRouterProvider) {
    $translatePartialLoaderProvider.addPart('app/dashboards');

    /*
    $stateProvider
    .state('admin-panel.default.dashboard-general', {
        url: '/dashboards/general',
        templateUrl: 'app/dashboards/dashboard-general.tmpl.html',
        authenticate: true,
    })
    .state('admin-panel.default.dashboard-analytics', {
        url: '/dashboards/analytics',
        templateUrl: 'app/dashboards/dashboard-analytics.tmpl.html',
        controller: 'DashboardAnalyticsController',
        authenticate: true,
    })
    .state('admin-panel.default.dashboard-server', {
        url: '/dashboards/server',
        controller: 'DashboardServerController',
        templateUrl: 'app/dashboards/dashboard-server.tmpl.html',
        authenticate: true,
    })
    .state('admin-panel.default.dashboard-widgets', {
        url: '/dashboards/widgets',
        templateUrl: 'app/dashboards/widgets.tmpl.html',
        authenticate: true,
    })
    .state('admin-panel.default.dashboard-social', {
        url: '/dashboards/social',
        templateUrl: 'app/dashboards/dashboard-social.tmpl.html',
        controller: 'DashboardSocialController',
        authenticate: true
    });
    */
    //Send to login if the URL was not found
    $urlRouterProvider.otherwise("/login");
})
.run(function(SideMenu) {
    SideMenu.addMenu({
        name: 'MENU.DASHBOARDS.DASHBOARDS',
        icon: 'icon-home',
        type: 'dropdown',
        priority: 2.1,
        children: [/*{
            name: 'MENU.DASHBOARDS.GENERAL',
            state: 'admin-panel.default.dashboard-general',
            type: 'link',
        },{
            name: 'MENU.DASHBOARDS.ANALYTICS',
            state: 'admin-panel.default.dashboard-analytics',
            type: 'link',
        },{
            name: 'MENU.DASHBOARDS.SERVER',
            state: 'admin-panel.default.dashboard-server',
            type: 'link',
        }
        ,{
            name: 'MENU.DASHBOARDS.WIDGETS',
            state: 'admin-panel.default.dashboard-widgets',
            type: 'link',
        },{
            name: 'MENU.DASHBOARDS.SOCIAL',
            state: 'admin-panel.default.dashboard-social',
            type: 'link',
        }
        **/
        ]
    });
});
