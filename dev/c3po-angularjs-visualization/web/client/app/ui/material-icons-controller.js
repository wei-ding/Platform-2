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
controller('MaterialIconsController', function ($scope, $mdDialog, icons) {
    $scope.icons = [];
    $scope.groups = [];

    $scope.iconSource = 'Select icon below to see HTML';
    // create filterable data structure for icons
    angular.forEach(icons.data, function(iconGroup, groupName) {
        angular.forEach(iconGroup, function(icon, iconName) {
            $scope.icons.push({
                name: iconName,
                group: groupName,
                className: icon
            });
        });
        $scope.groups.push(groupName);
    });

    $scope.selectIcon = function($event, icon) {
        $mdDialog.show(
            $mdDialog.alert()
            .parent(angular.element(document.body))
            .title('Here\'s the code for that icon')
            .content('<md-icon md-font-icon="' + icon.className + '"></md-icon>')
            .ok('Thanks')
            .targetEvent($event)
        );
    };

});