'use strict';

/**
 * @ngdoc filter
 * @name tableImage
 * @module c-3po
 * @kind filter
 *
 * Used for table pagination
 */
angular.module('c-3po')
.filter('startFrom',function () {
    return function (input, start) {
        start = +start;
        return input.slice(start);
    };
});