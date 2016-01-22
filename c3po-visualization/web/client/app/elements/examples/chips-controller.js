'use strict';

/**
 * @ngdoc function
 * @name ChipsController
 * @module c-3poElements
 * @kind function
 *
 * @description
 *
 * Handles buttons element page
 */
angular.module('c-3poElements').
controller('ChipsController', function ($scope, contacts) {
    $scope.contacts = contacts.data;
    $scope.email = {
        to: [],
        cc: [],
        bcc: [],
    };

    $scope.queryContacts = function($query) {
        var lowercaseQuery = angular.lowercase($query);
        return $scope.contacts.filter(function(contact) {
            var lowercaseName = angular.lowercase(contact.name);
            if (lowercaseName.indexOf(lowercaseQuery) !== -1) {
                return contact;
            }
        });
    };
});