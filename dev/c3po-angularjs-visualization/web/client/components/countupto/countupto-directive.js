'use strict';

/**
* @ngdoc directive
* @name countupto
* @restrict A
* @scope
*
* @description
*
* Animated counting number
*
* @usage
* ```html
* <h1 countupto="100"></h1>
* ```
*
*
* Full options here http://inorganik.github.io/countUp.js/
*/
angular.module('c-3po')
.directive('countupto', function($timeout) {
    return {
        restrict: 'A',
        scope: {
            'countupto': '=',
            'options': '='
        },
        link: function($scope, $element, attrs) {
            var options = {
                useEasing: true,
                useGrouping: true,
                separator: ',',
                decimal: '.',
                prefix: '',
                suffix: ''
            };

            // override default options?
            if ($scope.options) {
                for(var option in options) {
                    if($scope.options[option] !== undefined) {
                        options[option] = $scope.options[option];
                    }
                }
            }

            attrs.from = attrs.from === undefined ? 0 : parseInt(attrs.from);
            attrs.decimals = attrs.decimals === undefined ? 2 : parseFloat(attrs.decimals);
            attrs.duration = attrs.duration === undefined ? 5 : parseFloat(attrs.duration);

            $timeout(function() {
                var numAnim = new CountUp($element[0], attrs.from, $scope.countupto, attrs.decimals, attrs.duration, options);
                numAnim.start();
            }, 500);
        }

    };
});