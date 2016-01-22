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
