'use strict';

/**
 * @ngdoc module
 * @name c3po.risk
 * @description
 *
 * The `c3po.risk` module handles the most common dashboard pages.
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
