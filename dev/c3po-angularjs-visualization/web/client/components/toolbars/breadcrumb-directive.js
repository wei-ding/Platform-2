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
