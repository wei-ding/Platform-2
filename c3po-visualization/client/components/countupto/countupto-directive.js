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
* @author Wei Ding
* @version 0.1.0
**/

/*!
    This content is released under the (http://opensource.org/licenses/MIT) MIT License.

    The MIT License (MIT)

    Copyright (c) <2014> <>

    Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. * MIT 
*/
/*
 * will remove this directive soon 
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
