'use strict';

/**
* @ngdoc directive
* @name sideMenuItem
* @restrict E
* @scope
*
* @description
*
* Simple menu link item
*
* @usage
* ```html
* <side-menu-item ng-repeat="item in menu" item="item"></side-menu-item>
* ```
* @author Wei Ding
* @version 0.1.0
*/

/*!
    This content is released under the (http://opensource.org/licenses/MIT) MIT License.

    The MIT License (MIT)

    Copyright (c) <2014> <>

    Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. * MIT 
*/
/*
 * will remove this directive soon 
 */
angular.module('c-3po')
.directive('sideMenuItem', function($state) {
    return {
        restrict: 'E',
        require: '^sideMenu',
        scope: {
            item: '='
        },
        template: '<div ng-include="itemTemplate"></div>',
        link: function($scope) {
            // load a template for this directive based on the type ( link | dropdown )
            $scope.itemTemplate = 'components/side-menu/side-menu-' + $scope.item.type + '.tmpl.html';
            $scope.item.url = $state.href($scope.item.state);
            /***
            * Menu Click Handlers
            ***/
            $scope.toggleMenu = function() {
                // send message down the menu from the parent, item is toggled
                // this will close any sibling menus
                $scope.$parent.$parent.$broadcast('toggleMenu', $scope.item, !$scope.item.open);
            };

            // this event ensures that any sibling menu items are closed when menu item is opened
            $scope.$on('toggleMenu', function(event, item, open) {
                // if this is the item we are looking for
                if($scope.item === item) {
                    $scope.item.open = open;
                }
                else {
                    $scope.item.open = false;
                }
            });

            /***
            * URL Change Handlers
            ***/

            function isActive() {
                var params = $scope.item.params === undefined ? {} : $scope.item.params;
                return $state.includes($scope.item.state, params);
            }

            // on first init check if we are the current menu item
            if(isActive()) {
                openMenu();
            }

            // opens the menu then calls its parents to also open
            function openMenu() {
                $scope.item.active = true;
                $scope.item.open = true;
                $scope.$emit('openParents');
            }

            // add a watch for when the url location changes
            $scope.$on('$locationChangeSuccess', function() {
                // location has changed so update the menu
                $scope.item.active = false;
                $scope.item.open = false;
                if(isActive()) {
                    openMenu();
                }
            });

            // adds an extra hue class if the item is active
            $scope.activeClass = function() {
                return isActive() ? 'md-hue-3' : '';
            };

            $scope.openLink = function() {
                // if we dont have any default params for this state just use empty object
                var params = $scope.item.params === undefined ? {} : $scope.item.params;

                $state.go($scope.item.state, params);
            };

            // this event is emitted up the tree to open parent menus
            $scope.$on('openParents', function() {
                // openParents event so open the parent item
                $scope.item.active = true;
                $scope.item.open = true;
            });
        }
    };
});
