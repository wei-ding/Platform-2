'use strict';

/**
 * @ngdoc function
 * @name TablesController
 * @module c-3poElements
 * @kind function
 *
 * @description
 *
 * Handles table elements page
 */
angular.module('c-3poElements').
controller('Tables1Controller', function ($scope) {
    $scope.columns = [{
        title: '',
        field: 'thumb',
        sortable: false,
        filter: 'tableImage'
    },{
        title: 'Name',
        field: 'name',
        sortable: true,
    },{
        title: 'Description',
        field: 'description',
        sortable: true,
    },{
        title: 'Date of Birth',
        field: 'birth',
        sortable: true,
    }];

    $scope.contents = [{
        thumb:'assets/images/avatars/avatar-1.png',
        name: 'Chris Doe',
        description: 'Developer',
        birth: 'Jun 5, 1994'
    },{
        thumb:'assets/images/avatars/avatar-2.png',
        name: 'Ann Doe',
        description: 'Commerce',
        birth: 'Jul 15, 1993'
    },{
        thumb:'assets/images/avatars/avatar-3.png',
        name: 'Mark Ronson',
        description: 'Designer',
        birth: 'Jan 27, 1984'
    },{
        thumb:'assets/images/avatars/avatar-4.png',
        name: 'Eric Doe',
        description: 'Human Resources',
        birth: 'Feb 3, 1985'
    },{
        thumb:'assets/images/avatars/avatar-5.png',
        name: 'John Doe',
        description: 'Commerce',
        birth: 'Sep 5, 1978'
    },{
        thumb:'assets/images/avatars/avatar-1.png',
        name: 'George Doe',
        description: 'Media',
        birth: 'Jun 23, 1996'
    },{
        thumb:'assets/images/avatars/avatar-2.png',
        name: 'Ann Ronson',
        description: 'Commerce',
        birth: 'Aug 16, 1995'
    },{
        thumb:'assets/images/avatars/avatar-3.png',
        name: 'Adam Ronson',
        description: 'Developer',
        birth: 'Jan 7, 1987'
    },{
        thumb:'assets/images/avatars/avatar-4.png',
        name: 'Hansel Doe',
        description: 'Social Media',
        birth: 'Feb 13, 1977'
    },{
        thumb:'assets/images/avatars/avatar-5.png',
        name: 'Tony Doe',
        description: 'CEO',
        birth: 'Sep 29, 1970'
    }];
});