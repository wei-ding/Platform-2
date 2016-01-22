'use strict';

/**
* @ngdoc directive
* @name chartjsLineWidget
* @restrict A
* @scope
*
* @description
*
* Adds chartjs line chart data to widget
*
* @usage
* ```html
* <widget chartjs-line-widget>
* ```
*/
angular.module('c-3poDashboards')
.directive('chartjsLineWidget', ['$window', '$timeout', function ($window, $timeout, $interval) {
    return {
        require: 'widget',
        restrict: 'A',
        link: function ($scope, $element, attrs, widgetCtrl) {
            widgetCtrl.setLoading(true);

            $timeout(function() {
                widgetCtrl.setLoading(false);
                randomData();
            }, 1500);

            widgetCtrl.setMenu({
                icon: 'icon-more-vert',
                items: [{
                    icon: 'icon-refresh',
                    title: 'DASHBOARDS.WIDGETS.MENU.REFRESH',
                    click: function($event) {
                        $interval.cancel($scope.intervalPromise);
                        widgetCtrl.setLoading(true);
                        $timeout(function() {
                            widgetCtrl.setLoading(false);
                            randomData();
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

            $scope.lineChart = {
                labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
                series: ['Pageviews', 'Visits', 'Sign ups'],
                options: {
                    datasetFill: false
                },
                data: []
            };

            function randomData() {
                $scope.lineChart.data = [];
                for(var series = 0; series < $scope.lineChart.series.length; series++) {
                    var row = [];
                    for(var label = 0; label < $scope.lineChart.labels.length; label++) {
                        row.push(Math.floor((Math.random() * 100) + 1));
                    }
                    $scope.lineChart.data.push(row);
                }
            }

            // Simulate async data update
            $scope.intervalPromise = $interval(randomData, 5000);
        }
    };
}]);
