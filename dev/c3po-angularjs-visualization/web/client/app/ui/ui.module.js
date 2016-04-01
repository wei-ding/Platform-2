'use strict';

/**
 * @ngdoc module
 * @name c-3poUI
 * @description
 *
 * The `c-3poUI` module adds an UI pages
 */
angular.module('c-3poUI', ['ngCookies', 'webfont-loader'])
.constant('UI_FONTS', [{
    name: 'Roboto Draft',
    family: 'RobotoDraft',
    google: 'RobotoDraft:300,400,500,700,400italic'
},{
    name: 'Noto Sans',
    family: 'Noto Sans',
    google: 'Noto+Sans:400,700,400italic'
},{
    name: 'Open Sans',
    family: 'Open Sans',
    google: 'Open+Sans:300,400,500,700,400italic'
},{
    name: 'Lato',
    family: 'Lato',
    google: 'Lato:300,400,500,700,400italic'
},{
    name: 'Ubuntu',
    family: 'Ubuntu',
    google: 'Ubuntu:300,400,500,700,400italic'
},{
    name: 'Source Sans Pro',
    family: 'Source Sans Pro',
    google: 'Source+Sans+Pro:300,400,500,700,400italic'
}])
.config(function ($translatePartialLoaderProvider, $stateProvider) {
    $translatePartialLoaderProvider.addPart('app/ui');

    $stateProvider
    .state('admin-panel.default.ui-typography', {
        url: '/ui/typography',
        controller: 'TypographyController',
        templateUrl: 'app/ui/typography.tmpl.html',
    })
    .state('admin-panel.default.ui-colors', {
        url: '/ui/colors',
        controller: 'ColorsController',
        templateUrl: 'app/ui/colors.tmpl.html',
    })
    .state('admin-panel.default.ui-material-icons', {
        url: '/ui/material-icons',
        controller: 'MaterialIconsController',
        templateUrl: 'app/ui/material-icons.tmpl.html',
        resolve: {
            icons: function($http, API_CONFIG) {
                return $http({
                    method: 'GET',
                    url: API_CONFIG.url + 'elements/icons',
                });
            }
        }
    })
    .state('admin-panel.default.ui-weather-icons', {
        url: '/ui/weather-icons',
        controller: 'WeatherIconsController',
        templateUrl: 'app/ui/weather-icons.tmpl.html'
    })
    .state('admin-panel.default.ui-fa-icons', {
        url: '/ui/fa-icons',
        controller: 'FaIconsController',
        templateUrl: 'app/ui/fa-icons.tmpl.html',
        resolve: {
            icons: function($http, API_CONFIG) {
                return $http({
                    method: 'GET',
                    url: API_CONFIG.url + 'elements/icons-fa',
                });
            }
        }
    })

    .state('admin-panel.default.ui-toolbar', {
        url: '/ui/toolbars/:extraClass/:background/:shrink',
        controller: 'ToolbarsUIController',
        templateUrl: 'app/ui/toolbars.tmpl.html'
    })

    .state('admin-panel.default.ui-skins', {
        url: '/ui/skins',
        controller: 'SkinsUIController',
        templateUrl: 'app/ui/skins.tmpl.html'
    });
})
.run(function(SideMenu, TypographySwitcher, $rootScope) {
    $rootScope.$on('$stateChangeStart', function(event, toState, toParams, fromState) {
        if(toState.name === 'admin-panel.default.ui-toolbar') {
            // override data in route to change the toolbar
            if(undefined !== toParams.extraClass) {
                toState.data.toolbar.extraClass = toParams.extraClass === 'default' ? '' : toParams.extraClass;
            }

            if(undefined !== toParams.background) {
                toState.data.toolbar.background = toParams.background === 'off' ? false : 'assets/images/backgrounds/bg-toolbar.jpg';
            }

            if(undefined !== toParams.shrink) {
                toState.data.toolbar.shrink = toParams.shrink === 'off' ? false : true;
            }
        }

        if(fromState.name === 'admin-panel.default.ui-toolbar' && toState.name !== 'admin-panel.default.ui-toolbar') {
            toState.data.toolbar.extraClass = '';
            toState.data.toolbar.background = false;
            toState.data.toolbar.shrink = false;
        }
    });


    // load up the webfont loader to allow loading google fonts in the demo
    jQuery.ajax({
        url: '//ajax.googleapis.com/ajax/libs/webfont/1.5.10/webfont.js',
        dataType: 'script',
        async: true,
        success: function() {
            // initialise typography switcher (make sure correct font is loaded if one has been selected)
            TypographySwitcher.init();
        }
    });

    // setup ui
    SideMenu.addMenu({
        name: 'MENU.UI.UI',
        icon: 'icon-straighten',
        type: 'dropdown',
        priority: 3.2,
        children: [{
            name: 'MENU.UI.COLORS',
            state: 'admin-panel.default.ui-colors',
            type: 'link',
        },{
            name: 'MENU.UI.FONT_AWESOME',
            state: 'admin-panel.default.ui-fa-icons',
            type: 'link',
        },{
            name: 'MENU.UI.MATERIAL_ICONS',
            state: 'admin-panel.default.ui-material-icons',
            type: 'link',
        },{
            name: 'MENU.UI.SKINS',
            state: 'admin-panel.default.ui-skins',
            type: 'link',
        },{
            name: 'MENU.UI.TOOLBAR',
            state: 'admin-panel.default.ui-toolbar',
            params: {
                extraClass: 'default',
                background: 'off',
                shrink: 'off'
            },
            type: 'link',
        },{
            name: 'MENU.UI.TYPOGRAPHY',
            state: 'admin-panel.default.ui-typography',
            type: 'link',
        },{
            name: 'MENU.UI.WEATHER_ICONS',
            state: 'admin-panel.default.ui-weather-icons',
            type: 'link',
        }]
    });
});
