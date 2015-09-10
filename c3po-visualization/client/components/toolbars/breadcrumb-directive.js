'use strict';

/**
* @ngdoc directive
* @name breadcrumb
* @restrict A
* @scope
*
* @description
*
* Handles the default toolbar breadcrumbs - works together with the breadcrumb directive recusively
*
* @usage
* ```html
* <span breadcrumb="breadcrumb">
* ```
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
angular.module('c-3po')
.directive('breadcrumb', function ($compile) {
    return {
        restrict: 'A',
        replace: true,
        scope: {
            breadcrumb: '='
        },
        template: '<span><span>{{breadcrumb.name | translate}}<md-icon md-font-icon="icon-chevron-right" ng-show="breadcrumb.children.length > 0"></md-icon></span></span>',
        link: function ($scope, $element) {
            if($scope.breadcrumb.children !== undefined) {
                $element.find('span').attr('hide-sm', '');
            }
            var collectionSt = '<span breadcrumbs="breadcrumb.children"></span>';
            if (angular.isArray($scope.breadcrumb.children)) {
                $compile(collectionSt)($scope, function(cloned) {
                    $element.append(cloned);
                });
            }
        }
    };
});
