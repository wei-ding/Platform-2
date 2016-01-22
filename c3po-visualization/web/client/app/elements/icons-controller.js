'use strict';

/**
 * @ngdoc function
 * @name IconsController
 * @module c-3poElements
 * @kind function
 *
 * @description
 *
 * Handles icons element page
 */
angular.module('c-3poElements').
controller('IconsController', function ($scope, icons, fa) {
    $scope.icons = [];
    $scope.families = ['Material Icon Font', 'Font Awesome'];
    $scope.selectedIcon = null;

    // create filterable data structure for icons
    angular.forEach(icons.data, function(iconGroup) {
        angular.forEach(iconGroup, function(icon, iconName) {
            $scope.icons.push({
                name: iconName,
                family: 'Material Icon Font',
                className: icon
            });
        });
    });

    angular.forEach(fa.data, function(name, className) {
        $scope.icons.push({
            name: name,
            family: 'Font Awesome',
            className: className
        });
    });

    $scope.selectIcon = function(icon) {
        $scope.selectedIcon = icon;
    };
});