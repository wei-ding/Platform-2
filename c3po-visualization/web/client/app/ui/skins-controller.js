'use strict';

/**
 * @ngdoc function
 * @name SkinsUIController
 * @module c-3poUI
 * @kind function
 *
 * @description
 *
 * Handles the toolbar ui page
 */
angular.module('c-3poUI').
controller('SkinsUIController', function ($scope, $cookies, $window, cylonSkins, cylonTheming) {
    $scope.skins = cylonSkins.getSkins();
    $scope.selectedSkin = cylonSkins.getCurrent();

    $scope.trySkin = function() {
        if($scope.selectedSkin !== cylonSkins.getCurrent()) {
            $cookies.put('cylon-skin',angular.toJson({
                skin: $scope.selectedSkin.id
            }));            
            $window.location.reload();
        }
    };

    $scope.elementColors = {
        logo: '',
        sidebar: '',
        content: '',
        toolbar: ''
    };

    $scope.updatePreview = function() {
        for(var element in $scope.elementColors) {
            var theme = cylonTheming.getTheme($scope.selectedSkin.elements[element]);
            var hue = theme.colors.primary.hues.default === undefined ? '500' : theme.colors.primary.hues.default;
            var color = cylonTheming.getPaletteColor(theme.colors.primary.name, hue);
            $scope.elementColors[element] = cylonTheming.rgba(color.value);
        }
    };

    $scope.updatePreview();
});
