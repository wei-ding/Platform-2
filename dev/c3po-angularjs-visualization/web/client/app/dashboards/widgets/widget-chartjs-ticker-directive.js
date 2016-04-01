'use strict';

/**
* @ngdoc directive
* @name chartjsTickerWidget
* @restrict A
* @scope
*
* @description
*
* Adds chartjs line ticker data to widget
*
* @usage
* ```html
* <widget chartjs-ticker-widget>
* ```
*/
angular.module('c-3poDashboards')
.directive('chartjsTickerWidget', function ($timeout, $interval) {
    return {
        require: 'widget',
        restrict: 'A',
        link: function ($scope) {
            var maximum = 100;
            $scope.tickerChart = {
                data: [[]],
                labels: [],
                options: {
                    animation: false,
                    showScale: false,
                    showTooltips: false,
                    pointDot: false,
                    datasetStrokeWidth: 0.5,
                    maintainAspectRatio: false,
                }
            };

            // Update the dataset at 25FPS for a smoothly-animating chart
            $interval(function () {
                getLiveChartData();
            }, 1000);

            function getLiveChartData () {
                if ($scope.tickerChart.data[0].length) {
                    $scope.tickerChart.labels = $scope.tickerChart.labels.slice(1);
                    $scope.tickerChart.data[0] = $scope.tickerChart.data[0].slice(1);
                }

                while ($scope.tickerChart.data[0].length < maximum) {
                    $scope.tickerChart.labels.push('');
                    $scope.tickerChart.data[0].push(getRandomValue($scope.tickerChart.data[0]));
                }
            }

            function getRandomValue (data) {
                var l = data.length, previous = l ? data[l - 1] : 50;
                var y = previous + Math.random() * 10 - 5;
                return y < 0 ? 0 : y > 100 ? 100 : y;
            }
        }
    };
});