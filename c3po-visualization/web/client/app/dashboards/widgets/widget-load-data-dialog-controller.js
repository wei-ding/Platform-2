'use strict';

/**
 * @ngdoc function
 * @name WidgetLoadDataDialogController
 * @module c-3poDashboards
 * @kind function
 *
 * @description
 *
 * Handles actions in compose dialog
 */

angular.module('c-3poDashboards')
.controller('WidgetLoadDataDialogController', function ($scope, $mdDialog, data) {
    $scope.data = data;

    $scope.closeDialog = function() {
        $mdDialog.cancel();
    };
});