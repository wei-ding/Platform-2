'use strict';

/**
* @ngdoc directive
* @name chartjsPieWidget
* @restrict A
* @scope
*
* @description
*
* Adds chartjs pie chart data to widget
*
* @usage
* ```html
* <widget chartjs-pie-widget>
* ```
*/
angular.module('c-3poDashboards')
.directive('chartjsPieWidget', function ($timeout) {
    return {
        require: 'widget',
        restrict: 'A',
        link: function ($scope, $element, attrs, widgetCtrl) {
            widgetCtrl.setLoading(true);

            $timeout(function() {
                widgetCtrl.setLoading(false);
            }, 1500);

            widgetCtrl.setMenu({
                icon: 'icon-more-vert',
                items: [{
                    icon: 'icon-refresh',
                    title: 'DASHBOARDS.WIDGETS.MENU.REFRESH',
                    click: function($event) {
                        widgetCtrl.setLoading(true);
                        $timeout(function() {
                            widgetCtrl.setLoading(false);
                        }, 1500);
                    }
                },{
                    icon: 'icon-share',
                    title: 'DASHBOARDS.WIDGETS.MENU.SHARE',
                },{
                    icon: 'icon-print',
                    title: 'DASHBOARDS.WIDGETS.MENU.PRINT',
                }]
            });

            $scope.pieChart = {
                labels: ['Facebook', 'Twitter', 'Google+', 'Others'],
                data: [300, 500, 100, 50]
            };
        }
    };
});