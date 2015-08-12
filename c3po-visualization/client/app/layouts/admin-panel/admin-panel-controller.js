'use strict';

/**
 * @ngdoc function
 * @name AdminController
 * @module c-3po
 * @kind function
 *
 * @description
 *
 * Handles the admin view
 * @author Wei Ding
 * @version 0.1.0
**/

/*!
    This content is released under the (http://opensource.org/licenses/MIT) MIT License.

    The MIT License (MIT)

    Copyright (c) <2015> <>

    Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. * MIT 
 */
angular.module('c-3po').
controller('AdminController', function ($scope, $element, $timeout, $mdSidenav, $mdUtil, $state) {
    $scope.toolbarShrink = undefined;
    $scope.isMenuLocked = true; 
    $scope.isMenuCollapsing = false; 
    $scope.isHovering = false;  

    if($state.current.data !== undefined) {
        if($state.current.data.toolbar !== undefined) {
            if($state.current.data.toolbar.shrink === true) {
                $scope.toolbarShrink = true;
            }
        }
    }

    // we need different event handlers for mouse over / leave, can not toggle the variable.
    $scope.activateHover = function() {      
        $scope.isHovering = true;
    };

    $scope.removeHover = function(){        
        $scope.isHovering = false;
    };

    $scope.toggleMenuLock = function() {    
    	$scope.isMenuLocked = !$scope.isMenuLocked;        
    	$scope.isMenuCollapsing = !$scope.isMenuLocked;
    	
    	if($scope.isMenuCollapsing === true){  
            // manually remove the hover class in order to prevent the menu from growing back
            $scope.isHovering = false;            
    		$timeout(function() {                
    			$scope.isMenuCollapsing = false;                  
    		}, 400);
    	}    	    
    };

    $scope.menuClass = function() {        
    	return  $scope.isMenuLocked === true ? '' :($scope.isMenuCollapsing === true ? 'is-collapsing' : ($scope.isHovering == true ? 'admin-sidebar-collapsed hover': 'admin-sidebar-collapsed' ));
    };
});
