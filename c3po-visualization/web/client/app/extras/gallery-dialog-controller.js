'use strict';

/**
 * @ngdoc function
 * @name GalleryDialogController
 * @module c-3poExtras
 * @kind function
 *
 * Handles the gallery photo popup
 */
angular.module('c-3poExtras').
controller('GalleryDialogController', function ($scope, $mdDialog, day, image) {
    $scope.currentImage = image;

    $scope.closeDialog = function() {
        $mdDialog.cancel();
    };

    $scope.next = function() {
        var index = day.images.indexOf($scope.currentImage);
        index = index + 1 < day.images.length ? index + 1 : 0;
        $scope.currentImage = day.images[index];
    };

    $scope.prev = function() {
        var index = day.images.indexOf($scope.currentImage);
        index = index - 1 < 0 ? day.images.length -1 : index - 1;
        $scope.currentImage = day.images[index];
    };
});