'use strict';

/**
 * @ngdoc function
 * @name MapTerrainDemoController
 * @module c-3poMaps
 * @kind function
 *
 *
 */
angular.module('c-3poMaps').
controller('MapTerrainDemoController', function ($scope, uiGmapGoogleMapApi) {
    uiGmapGoogleMapApi.then(function(maps) {
        $scope.terrainMap = {
            center: {
                latitude: 51.219053,
                longitude: 4.404418
            },
            zoom: 10,
            marker: {
                id:0,                
                coords: {
                    latitude: 51.219053,
                    longitude: 4.404418
                },
                options: {
                    icon: {
                        anchor: new maps.Point(36,36),
                        origin: new maps.Point(0,0),                        
                        url: 'assets/images/maps/blue_marker.png'
                    },                                      
                }
            },
            options:{
                scrollwheel:false,
                mapTypeId:maps.MapTypeId.TERRAIN 
            }            
        };              
    });   
});