'use strict';

/**
 * @ngdoc function
 * @name MapLabelDemoController
 * @module c-3poMaps
 * @kind function
 *
 *
 */
angular.module('c-3poMaps').
controller('MapLabelDemoController', function ($scope, uiGmapGoogleMapApi) {
    uiGmapGoogleMapApi.then(function(maps) {
        $scope.labeledMap = {
            center: {
                latitude: 35.027469,
                longitude: -111.022753
            },
            zoom: 4,
            marker: {
                id:0,                
                coords: {
                    latitude: 35.027469,
                    longitude: -111.022753
                },
                options: {
                    icon: {
                        anchor: new maps.Point(36,36),
                        origin: new maps.Point(0,0),                        
                        url: 'assets/images/maps/blue_marker.png'
                    }
                }
            },
            options:{
                scrollwheel:false,             
            }
        };

        $scope.labelTitle = 'Hello from Arizona!';
    });   
});