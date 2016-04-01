'use strict';

/**
 * @ngdoc module
 * @name c-3poMenuLevels
 * @description
 *
 * The `c-3poMenuLevels` module adds an UI pages
 */
angular.module('c-3poMenuLevels', [])
.config(function ($translatePartialLoaderProvider, $stateProvider) {
    $translatePartialLoaderProvider.addPart('app/menulevels');

    $stateProvider
    .state('admin-panel.default.menu-levels', {
        url: '/menu-levels/:level',
        controller: 'LevelController',
        templateUrl: 'app/menulevels/level.tmpl.html',
    });
})
.run(function(SideMenu) {
    SideMenu.addMenu({
        name: 'MENU.LEVELS.LEVELS',
        icon: 'icon-receipt',
        type: 'dropdown',
        priority: 6.1,
        children: [{
            name: 'MENU.LEVELS.1-1',
            type: 'dropdown',
            children: [{
                name: 'MENU.LEVELS.2-1',
                type: 'dropdown',
                children: [{
                    name: 'MENU.LEVELS.3-1',
                    type: 'dropdown',
                    children: [{
                        name: 'MENU.LEVELS.4-1',
                        type: 'link',
                        state: 'admin-panel.default.menu-levels',
                        params: {
                            level: 'Item1-1-1-1'
                        }
                    },{
                        name: 'MENU.LEVELS.4-2',
                        type: 'link',
                        state: 'admin-panel.default.menu-levels',
                        params: {
                            level: 'Item1-1-1-2'
                        }
                    },{
                        name: 'MENU.LEVELS.4-3',
                        type: 'link',
                        state: 'admin-panel.default.menu-levels',
                        params: {
                            level: 'Item1-1-1-3'
                        }
                    }]
                }]
            }]
        }]
    });
});
