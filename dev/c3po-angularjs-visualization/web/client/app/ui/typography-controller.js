'use strict';

/**
 * @ngdoc function
 * @name TypographyController
 * @module c-3poUI
 * @kind function
 *
 * @description
 *
 * Handles the typography ui page
 */
angular.module('c-3poUI').
controller('TypographyController', function ($scope, TypographySwitcher, UI_FONTS) {
    $scope.fonts = UI_FONTS;
    $scope.currentFont = TypographySwitcher.getCurrentFont();

    angular.forEach($scope.fonts, function(font) {
        if($scope.currentFont.name === font.name) {
            $scope.currentFont = font;
        }
    });
    $scope.changeFont = function() {
        TypographySwitcher.changeFont($scope.currentFont);
    };
});