'use strict';

/**
* @ngdoc directive
* @name chatWidget
* @restrict E
* @scope
*
* @description
*
* Adds some chat data
*
* @usage
* ```html
* <widget chat-widget></widget>
* ```
*/
angular.module('c-3poDashboards')
.directive('chatWidget', function() {
    return {
        require: 'widget',
        restrict: 'A',
        link: function($scope) {
            $scope.conversation = [{
                name: 'John',
                image: 'assets/images/avatars/avatar-6.png',
                messages: ['Hi there how are you?', 'Hello?'],
            },{
                name: 'Danny Ings',
                image: 'assets/images/avatars/avatar-3.png',
                messages: ['Howsitgowin?'],
            },{
                name: 'John',
                image: 'assets/images/avatars/avatar-6.png',
                messages: ['We need those images ASAP!', 'Client asked about them.'],
            },{
                name: 'Danny Ings',
                image: 'assets/images/avatars/avatar-3.png',
                messages: ['OK, will send them over'],
            }];

            $scope.userClass = function($even) {
                return $even ? 'user-left' : 'user-right';
            };
        }
    };
});