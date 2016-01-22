'use strict';

/**
* @ngdoc directive
* @name contactsWidget
* @restrict E
* @scope
*
* @description
*
* Creates a dashboard weather widget
*
* @usage
* ```html
* <widget contacts-widget></widget>
* ```
*/
angular.module('c-3poDashboards')
.directive('contactsWidget', function() {
    return {
        require: 'widget',
        restrict: 'A',
        link: function($scope) {
            $scope.contacts = [{
                name: 'John',
                image: 'assets/images/avatars/avatar-2.png'
            },{
                name: 'Newton Welch',
                image: 'assets/images/avatars/avatar-5.png',
            },{
                name: 'Kelly Koelpin',
                image: 'assets/images/avatars/avatar-1.png',
            },{
                name: 'Rowland Emard',
                image: 'assets/images/avatars/avatar-2.png',
            },{
                name: 'Kailee Johnston',
                image: 'assets/images/avatars/avatar-3.png',
            },{
                name: 'Roberto Grimes',
                image: 'assets/images/avatars/avatar-4.png',
            }];
        }
    };
});