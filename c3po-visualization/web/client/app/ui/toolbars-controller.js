'use strict';

/**
 * @ngdoc function
 * @name ToolbarsUIController
 * @module c-3poUI
 * @kind function
 *
 * @description
 *
 * Handles the toolbar ui page
 */
angular.module('c-3poUI').
controller('ToolbarsUIController', function ($scope, $state) {
    $scope.toolbarSizes = [{
        name: 'Default',
        value: 'default'
    },{
        name: 'Medium',
        value: 'md-medium-tall'
    },{
        name: 'Tall',
        value: 'md-tall'
    }];

    $scope.toolbarSize = $scope.toolbarSizes[0];

    for (var i = $scope.toolbarSizes.length - 1; i >= 0; i--) {
        if($scope.toolbarSizes[i].value === $state.params.extraClass) {
            $scope.toolbarSize = $scope.toolbarSizes[i];
        }
    }

    $scope.toolbarBackground = $state.params.background;
    $scope.toolbarShrink = $state.params.shrink;

    $scope.tryIt = function(reload) {
        $state.go('admin-panel.default.ui-toolbar', {
            extraClass: $scope.toolbarSize.value,
            background: $scope.toolbarBackground,
            shrink: $scope.toolbarShrink
        },{
            reload: true === reload
        });

        updateVariables();
    };

    $scope.codeVariables = {
        extraClass: '',
        background: false,
        shrink: false,
    };

    function updateVariables() {
        $scope.codeVariables = {
            extraClass: $scope.toolbarSize.value === 'default' ? '' : $scope.toolbarSize.value,
            background: $scope.toolbarBackground === 'on' ? 'assets/images/backgrounds/bg-toolbar.jpg' : false,
            shrink: $scope.toolbarShrink === 'on' ? true : false,
        };
    }

    updateVariables();
});
