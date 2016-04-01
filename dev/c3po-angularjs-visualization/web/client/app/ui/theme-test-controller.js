'use strict';

/**
 * @ngdoc function
 * @name ThemeTestController
 * @module c-3poUI
 * @kind function
 *
 * @description
 *
 * Handles the colors popup dialog
 */
angular.module('c-3poUI').
controller('ThemeTestController', function ($scope, cylonTheming) {
    $scope.themes = cylonTheming.themes;
    $scope.getPalette = cylonTheming.getPalette;
    $scope.rgba = cylonTheming.rgba;
    $scope.intentions = ['md-primary', 'md-accent', 'md-warn'];
    $scope.hues = ['', 'md-hue-1', 'md-hue-2', 'md-hue-3'];

    $scope.materialCSS = 'md-bottom-sheet.md-THEME_NAME-theme {\n    background-color: \'{{background-50}}\';\n    border-top-color: \'{{background-300}}\';\n}';
    $scope.compiledCSS = '';

    for(var t in $scope.themes) {
        $scope.selectedTheme = $scope.themes[t];
        break;
    }

    $scope.parseCSS = function() {
        $scope.compiledCSS = cylonTheming.parseRules($scope.selectedTheme, 'primary', $scope.materialCSS);
    };
});