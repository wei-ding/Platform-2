'use strict';

/**
 * @ngdoc module
 * @name c-3poElements
 * @description
 *
 * The `c-3poElements` module adds the example elements pages & menus
 */
angular.module('c-3poElements', [])
.config(function ($translatePartialLoaderProvider, $stateProvider) {
    $translatePartialLoaderProvider.addPart('app/elements');

    $stateProvider
    .state('admin-panel.default.elements-buttons', {
        url: '/elements/buttons',
        templateUrl: 'app/elements/buttons.tmpl.html',
        controller: 'ButtonsController'
    })
    .state('admin-panel.default.elements-icons', {
        url: '/elements/icons',
        templateUrl: 'app/elements/icons.tmpl.html',
        controller: 'IconsController',
        resolve: {
            icons: function($http, API_CONFIG) {
                return $http({
                    method: 'GET',
                    url: API_CONFIG.url + 'elements/icons',
                });
            },
            fa: function($http, API_CONFIG) {
                return $http({
                    method: 'GET',
                    url: API_CONFIG.url + 'elements/icons-fa',
                });
            }
        }
    })
    .state('admin-panel.default.elements-checkboxes', {
        url: '/elements/checkboxes',
        templateUrl: 'app/elements/checkboxes.tmpl.html'
    })
    .state('admin-panel.default.elements-radios', {
        url: '/elements/radios',
        templateUrl: 'app/elements/radios.tmpl.html',
    })
    .state('admin-panel.default.elements-toolbars', {
        url: '/elements/toolbars',
        templateUrl: 'app/elements/toolbars.tmpl.html'
    })
    .state('admin-panel.default.elements-tooltips', {
        url: '/elements/tooltips',
        templateUrl: 'app/elements/tooltips.tmpl.html'
    })
    .state('admin-panel.default.elements-whiteframes', {
        url: '/elements/whiteframes',
        templateUrl: 'app/elements/whiteframes.tmpl.html'
    })
    .state('admin-panel.default.elements-sliders', {
        url: '/elements/sliders',
        templateUrl: 'app/elements/sliders.tmpl.html'
    })
    .state('admin-panel.default.elements-toasts', {
        url: '/elements/toasts',
        templateUrl: 'app/elements/toasts.tmpl.html'
    })
    .state('admin-panel.default.elements-progress', {
        url: '/elements/progress',
        templateUrl: 'app/elements/progress.tmpl.html',
        controller: 'ProgressController'
    })
    .state('admin-panel.default.elements-switches', {
        url: '/elements/switches',
        templateUrl: 'app/elements/switches.tmpl.html',
        controller: 'SwitchesController'
    })
    .state('admin-panel.default.elements-dialogs', {
        url: '/elements/dialogs',
        templateUrl: 'app/elements/dialogs.tmpl.html',
        controller: 'DialogsController'
    })
    .state('admin-panel.default.menus', {
        url: '/elements/menus',
        templateUrl: 'app/elements/menus.tmpl.html',
    })
    .state('admin-panel.default.elements-tabs', {
        url: '/elements/tabs',
        templateUrl: 'app/elements/tabs.tmpl.html'
    })
    .state('admin-panel.default.elements-sidebars', {
        url: '/elements/sidebars',
        templateUrl: 'app/elements/sidebars.tmpl.html',
        controller: 'SidebarsController'
    })
    .state('admin-panel.default.elements-grids', {
        url: '/elements/grids',
        templateUrl: 'app/elements/grids.tmpl.html'
    })
    .state('admin-panel.default.fab-speed', {
        url: '/elements/fab-speed',
        templateUrl: 'app/elements/fab-speed.tmpl.html'
    })
    .state('admin-panel.default.fab-toolbar', {
        url: '/elements/fab-toolbar',
        templateUrl: 'app/elements/fab-toolbar.tmpl.html'
    })
    .state('admin-panel.default.elements-selects', {
        url: '/elements/selects',
        templateUrl: 'app/elements/selects.tmpl.html'
    })
    .state('admin-panel.default.elements-tables', {
        url: '/elements/tables',
        templateUrl: 'app/elements/tables.tmpl.html'
    })
    .state('admin-panel.default.elements-textangular', {
        url: '/elements/textangular',
        templateUrl: 'app/elements/textangular.tmpl.html'
    })
    .state('admin-panel.default.elements-lists', {
        url: '/elements/lists',
        templateUrl: 'app/elements/lists.tmpl.html',
        controller: 'ListsController',
        resolve: {
            emails: function($http, API_CONFIG) {
                return $http({
                    method: 'GET',
                    url: API_CONFIG.url + 'email/inbox',
                });
            },
        }
    })
    .state('admin-panel.default.elements-chips', {
        url: '/elements/chips',
        templateUrl: 'app/elements/chips.tmpl.html',
        controller: 'ChipsController',
        resolve: {
            contacts: function($http, API_CONFIG) {
                return $http({
                    method: 'GET',
                    url: API_CONFIG.url + 'email/contacts',
                });
            }
        }
    })
    .state('admin-panel.default.elements-cards', {
        url: '/elements/cards',
        templateUrl: 'app/elements/cards.tmpl.html'
    });
})
.run(function(SideMenu) {
    SideMenu.addMenu({
        name: 'MENU.ELEMENTS.ELEMENTS',
        icon: 'icon-school',
        type: 'dropdown',
        priority: 3.1,
        children: [{
            name: 'MENU.ELEMENTS.BUTTONS',
            type: 'link',
            state: 'admin-panel.default.elements-buttons'
        },{
            name: 'MENU.ELEMENTS.CARDS',
            type: 'link',
            state: 'admin-panel.default.elements-cards'
        },{
            name: 'MENU.ELEMENTS.CHECKBOXES',
            type: 'link',
            state: 'admin-panel.default.elements-checkboxes'
        },{
            name: 'MENU.ELEMENTS.CHIPS',
            type: 'link',
            state: 'admin-panel.default.elements-chips'
        },{
            name: 'MENU.ELEMENTS.DIALOGS',
            type: 'link',
            state: 'admin-panel.default.elements-dialogs'
        },{
            name: 'MENU.ELEMENTS.FAB-SPEED',
            type: 'link',
            state: 'admin-panel.default.fab-speed'
        },{
            name: 'MENU.ELEMENTS.FAB-TOOLBAR',
            type: 'link',
            state: 'admin-panel.default.fab-toolbar'
        },{
            name: 'MENU.ELEMENTS.GRIDS',
            type: 'link',
            state: 'admin-panel.default.elements-grids'
        },{
            name: 'MENU.ELEMENTS.ICONS',
            type: 'link',
            state: 'admin-panel.default.elements-icons'
        },{
            name: 'MENU.ELEMENTS.LISTS',
            type: 'link',
            state: 'admin-panel.default.elements-lists'
        },{
            name: 'MENU.ELEMENTS.MENUS',
            type: 'link',
            state: 'admin-panel.default.menus'
        },{
            name: 'MENU.ELEMENTS.PROGRESS',
            type: 'link',
            state: 'admin-panel.default.elements-progress'
        },{
            name: 'MENU.ELEMENTS.RADIOS',
            type: 'link',
            state: 'admin-panel.default.elements-radios'
        },{
            name: 'MENU.ELEMENTS.SELECTS',
            type: 'link',
            state: 'admin-panel.default.elements-selects'
        },{
            name: 'MENU.ELEMENTS.SIDEBARS',
            type: 'link',
            state: 'admin-panel.default.elements-sidebars'
        },{
            name: 'MENU.ELEMENTS.SLIDERS',
            type: 'link',
            state: 'admin-panel.default.elements-sliders'
        },{
            name: 'MENU.ELEMENTS.SWITCHES',
            type: 'link',
            state: 'admin-panel.default.elements-switches'
        },{
            name: 'MENU.ELEMENTS.TABLES',
            type: 'link',
            state: 'admin-panel.default.elements-tables'
        },{
            name: 'MENU.ELEMENTS.TABS',
            type: 'link',
            state: 'admin-panel.default.elements-tabs'
        },{
            name: 'MENU.ELEMENTS.TEXTANGULAR',
            type: 'link',
            state: 'admin-panel.default.elements-textangular'
        },{
            name: 'MENU.ELEMENTS.TOASTS',
            type: 'link',
            state: 'admin-panel.default.elements-toasts'
        },{
            name: 'MENU.ELEMENTS.TOOLBARS',
            type: 'link',
            state: 'admin-panel.default.elements-toolbars'
        },{
            name: 'MENU.ELEMENTS.TOOLTIPS',
            type: 'link',
            state: 'admin-panel.default.elements-tooltips'
        },{
            name: 'MENU.ELEMENTS.WHITEFRAMES',
            type: 'link',
            state: 'admin-panel.default.elements-whiteframes'
        }]
    });
});

