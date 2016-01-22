'use strict';

/**
 * @ngdoc filter
 * @name tableImage
 * @module c-3po
 * @kind filter
 *
 * Creates a div with an image backround
 */
 angular.module('c-3po')
 .filter('tableImage', function ($sce) {
    return function (value) {
        return $sce.trustAsHtml('<div style=\"background-image: url(\'' + value + '\')\"/>');
    };
});