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