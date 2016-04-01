'use strict';

/**
 * @ngdoc module
 * @name c-3poCharts
 * @description
 *
 * The `c-3poCharts` module handles shows off the charts available
 */
angular.module('c-3poCharts', [])
.config(function ($translatePartialLoaderProvider, $stateProvider) {
    $translatePartialLoaderProvider.addPart('app/charts');

    $stateProvider
    .state('admin-panel.default.charts-google-bar', {
        url: '/charts/google/bar',
        templateUrl: 'app/charts/google-bar.tmpl.html'
    })
    .state('admin-panel.default.charts-google-scatter', {
        url: '/charts/google/scatter',
        templateUrl: 'app/charts/google-scatter.tmpl.html'
    })
    .state('admin-panel.default.charts-google-line', {
        url: '/charts/google/line',
        templateUrl: 'app/charts/google-line.tmpl.html'
    })
    .state('admin-panel.default.charts-chartjs-bar', {
        url: '/charts/chartjs/bar',
        templateUrl: 'app/charts/chartjs-bar.tmpl.html'
    })
    .state('admin-panel.default.charts-chartjs-pie', {
        url: '/charts/chartjs/pie',
        templateUrl: 'app/charts/chartjs-pie.tmpl.html'
    })
    .state('admin-panel.default.charts-chartjs-ticker', {
        url: '/charts/chartjs/ticker',
        templateUrl: 'app/charts/chartjs-ticker.tmpl.html'
    })
    .state('admin-panel.default.charts-chartjs-line', {
        url: '/charts/chartjs/line',
        templateUrl: 'app/charts/chartjs-line.tmpl.html'
    });
})
.run(function(SideMenu) {
    SideMenu.addMenu({
        name: 'MENU.CHARTS.CHARTS',
        icon: 'icon-insert-chart',
        type: 'dropdown',
        priority: 5.1,
        children: [{
            name: 'MENU.CHARTS.GOOGLE',
            type: 'dropdown',
            children: [{
                name: 'MENU.CHARTS.BAR',
                state: 'admin-panel.default.charts-google-bar',
                type: 'link',
            },{
                name: 'MENU.CHARTS.SCATTER',
                state: 'admin-panel.default.charts-google-scatter',
                type: 'link',
            },{
                name: 'MENU.CHARTS.LINE',
                state: 'admin-panel.default.charts-google-line',
                type: 'link',
            }]
        },{
            name: 'MENU.CHARTS.CHARTJS',
            type: 'dropdown',
            children: [{
                name: 'MENU.CHARTS.BAR',
                state: 'admin-panel.default.charts-chartjs-bar',
                type: 'link',
            },{
                name: 'MENU.CHARTS.LINE',
                state: 'admin-panel.default.charts-chartjs-line',
                type: 'link',
            },{
                name: 'MENU.CHARTS.PIE',
                state: 'admin-panel.default.charts-chartjs-pie',
                type: 'link',
            },{
                name: 'MENU.CHARTS.TICKER',
                state: 'admin-panel.default.charts-chartjs-ticker',
                type: 'link',
            }]
        }]
    });
});
