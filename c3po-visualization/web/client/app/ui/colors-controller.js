'use strict';

/**
 * @ngdoc function
 * @name ColorsController
 * @module c-3poUI
 * @kind function
 *
 * @description
 *
 * Handles the colors ui page
 */
angular.module('c-3poUI').
controller('ColorsController', function ($scope, $mdDialog, cylonTheming) {
    $scope.palettes = cylonTheming.palettes;

    $scope.colourRGBA = function(value) {
        var rgba = cylonTheming.rgba(value);
        return {
            'background-color': rgba
        };
    };

    $scope.selectPalette = function($event, name, palette) {
        $mdDialog.show({
            controller: 'ColorDialogController',
            templateUrl: 'app/ui/color-dialog.tmpl.html',
            targetEvent: $event,
            locals: {
                name: name,
                palette: palette
            },
            clickOutsideToClose: true
        })
        .then(function(answer) {
            $scope.alert = 'You said the information was "' + answer + '".';
        }, function() {
            $scope.alert = 'You cancelled the dialog.';
        });
    };
});