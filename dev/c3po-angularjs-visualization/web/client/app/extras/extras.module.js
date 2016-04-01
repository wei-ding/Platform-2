'use strict';

/**
 * @ngdoc module
 * @name c3po.authentication
 * @description
 *
 * The `c3po.extras` module handles all the extra pages
 */
angular.module('c-3poExtras', [])
.config(function ($translatePartialLoaderProvider, $stateProvider) {
    $translatePartialLoaderProvider.addPart('app/extras');

    $stateProvider
    .state('admin-panel-no-scroll.default.extra-gallery', {
        url: '/extras/gallery',
        templateUrl: 'app/extras/gallery.tmpl.html',
        controller: 'GalleryController'
    })
    .state('admin-panel.default.extra-avatars', {
        url: '/extras/avatars',
        templateUrl: 'app/extras/avatars.tmpl.html',
        controller: 'AvatarsController'
    })
    .state('admin-panel.default.extra-blank', {
        url: '/extras/blank',
        templateUrl: 'app/extras/blank.tmpl.html',
    })
    .state('admin-panel.default.extra-timeline', {
        url: '/extras/timeline',
        templateUrl: 'app/extras/timeline.tmpl.html',
        controller: 'TimelineController'
    });
})
.run(function(SideMenu) {
    SideMenu.addMenu({
        name: 'MENU.EXTRAS.EXTRAS',
        icon: 'icon-list',
        type: 'dropdown',
        priority: 8.1,
        children: [{
            name: 'MENU.EXTRAS.GALLERY',
            state: 'admin-panel-no-scroll.default.extra-gallery',
            type: 'link',
        },{
            name: 'MENU.EXTRAS.AVATARS',
            state: 'admin-panel.default.extra-avatars',
            type: 'link',
        },{
            name: 'MENU.EXTRAS.404',
            state: '404',
            type: 'link',
        },{
            name: 'MENU.EXTRAS.500',
            state: '500',
            type: 'link',
        },{
            name: 'MENU.EXTRAS.BLANK',
            state: 'admin-panel.default.extra-blank',
            type: 'link',
        },{
            name: 'MENU.EXTRAS.TIMELINE',
            state: 'admin-panel.default.extra-timeline',
            type: 'link',
        }]
    });
});
