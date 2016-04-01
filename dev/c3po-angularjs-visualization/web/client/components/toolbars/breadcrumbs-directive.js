'use strict';

/**
* @ngdoc directive
* @name breadcrumb
* @restrict A
* @scope
*
* @description
*
* Handles the default toolbar breadcrumbs - works together with the breadcrumb directive recusi
*
* @usage
* ```html
* <h2 breadcrumbs="menu"></h2>
* ```
*/
angular.module('c-3po')
.directive('breadcrumbs', function () {
    return {
        restrict: 'A',
        require: '^toolbar',
        replace: true,
        scope: {
            breadcrumbs: '='
        },
        template: '<span class="breadcrumb" hide-sm ng-repeat="breadcrumb in breadcrumbs" ng-show="breadcrumb.active" breadcrumb="breadcrumb"></span>',
    };
});
