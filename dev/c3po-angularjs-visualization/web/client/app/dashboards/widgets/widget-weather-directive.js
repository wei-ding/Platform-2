'use strict';

/**
* @ngdoc directive
* @name widget
* @restrict E
* @scope
*
* @description
*
* Creates a dashboard weather widget
*
* @usage
* ```html
* <widget weather-widget="London"></widget>
* ```
*/
angular.module('c-3poDashboards')
.directive('weatherWidget', function($http) {
    return {
        require: 'widget',
        restrict: 'A',
        link: function($scope, $element, attrs, widgetCtrl) {
            widgetCtrl.setLoading(true);

            var query = 'select item.condition from weather.forecast where woeid in (select woeid from geo.places(1) where text="' + attrs.weatherWidget + '")';
            var url = 'https://query.yahooapis.com/v1/public/yql?q=' + encodeURIComponent(query) + '&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys';

            $http.get(url).
            success(function(data) {
                if(data.query.count > 0) {                    
                    widgetCtrl.setLoading(false);
                    $scope.weather = {
                        iconClass: 'wi-yahoo-' + data.query.results.channel.item.condition.code,
                        date: new Date(data.query.created),
                        temp: data.query.results.channel.item.condition.temp,
                        text: data.query.results.channel.item.condition.text,
                        location: attrs.weatherWidget
                    };
                }
            }).
            error(function(data) {
                console.error('Could not get location weather', data);
            });
        }
    };
});