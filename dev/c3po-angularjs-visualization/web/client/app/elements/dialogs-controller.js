'use strict';

/**
 * @ngdoc function
 * @name DialogsController
 * @module c-3poElements
 * @kind function
 *
 * @description
 *
 * Handles dialog element page
 */
angular.module('c-3poElements').
controller('DialogsController', function ($scope, $mdDialog) {
    $scope.newDialog = {
        title: 'Are you sure?',
        content: 'This will wipe your whole computer!',
        ok: 'Agree',
        cancel: 'Disagree'
    };

    $scope.createDialog = function($event, dialog) {
        $mdDialog.show(
            $mdDialog.confirm()
            .title(dialog.title)
            .content(dialog.content)
            .ok(dialog.ok)
            .cancel(dialog.cancel)
            .targetEvent($event)
        );
    };
});