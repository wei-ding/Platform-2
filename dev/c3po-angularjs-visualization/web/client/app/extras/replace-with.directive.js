'use strict';

/**
* @ngdoc directive
* @name replaceWith
* @restrict E
* @scope
*
* @description
*
* Replaces the DOM element that was applied onto with the content in the attribute once interpolated
*
* @usage
* ```html
* <div replace-with='{{yourModel.prop}}'></div>
* ```
*/
angular.module('c-3poDashboards')
.directive('replaceWith', function($timeout) {
    return {        
        restrict: 'A',
        link: function (scope, element, attrs) {           
            attrs.$observe('replaceWith', function(value) {
                if (value) {
                    element.replaceWith(value == undefined ? '' : value);
                }  
            }); 
        }
    };
});