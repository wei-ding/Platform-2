'use strict';

/**
* @ngdoc directive
* @name cylonLoader
* @restrict E
* @scope
*
* @description
*
* Adds a loader screen that takes up 100%
*
* @usage
* ```html
* <cylon-loader></cylon-loader>
* ```
*/
angular.module('c-3po')
.directive('cylonLoader', function($rootScope, APP) {
    return {
        restrict: 'E',
        replace: true,
        scope: {},
        template: '<md-content flex class="loader" ng-show="active" layout="column" layout-fill layout-align="center center"><div class="loader-inner"><md-progress-circular md-mode="indeterminate"></md-progress-circular></div><h3 class="md-headline">{{appName}}</h3></md-content>',
        link: function($scope) {
            $scope.appName = APP.name;

            $scope.active = true;

            $rootScope.$on('$viewContentLoading', function() {
                $scope.active = true;
            });

            $rootScope.$on('$viewContentLoaded', function() {
                $scope.active = false;
            });
        }
    };
});
