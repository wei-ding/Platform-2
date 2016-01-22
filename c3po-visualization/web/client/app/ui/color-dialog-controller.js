'use strict';

/**
 * @ngdoc function
 * @name ColorDialogController
 * @module c-3poUI
 * @kind function
 *
 * @description
 *
 * Handles the colors popup dialog
 */
angular.module('c-3poUI').
controller('ColorDialogController', function ($scope, name, palette, cylonTheming) {
    $scope.name = name;
    $scope.palette = [];

    $scope.itemStyle = function(palette) {
        return {
            'background-color': cylonTheming.rgba(palette.value),
            'color': cylonTheming.rgba(palette.contrast)
        };
    };

    for(var code in palette) {
        $scope.palette.push({
            code: code,
            palette: palette[code]
        });
    }
});