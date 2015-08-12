'use strict';

/**
* app.js 
* Defines the c-3po application module and initializes it
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

angular.module('c-3po', [
    // inject angular modules
    'ngAnimate', 'ngCookies', 'ngTouch', 'ngSanitize', 'ngMessages', 'ngMaterial', 'ngMdIcons',
    // inject extra 3rd party angular modules
    'ui.router', 'pascalprecht.translate', 'LocalStorageModule', 'linkify', 'ui.calendar', 'angularMoment', 'textAngular',  'hljs', 'objectTable',
    // inject our own c3po modules
    //'c-3poUI', 
    // replace it with plugin system soon.
    'c-3poAuthentication', 
    'c-3poDashboards', 
    'c-3poRisk'
])
.constant('APP', {
    // replace it soon
    name: 'Clinical3PO',
    // replace it soon
    logo: 'assets/images/c3pologo.png',
    version: '0.1.0',
    languages: [{
        name: 'LANGUAGES.ENGLISH',
        key: 'en'
    },{
        name: 'LANGUAGES.CHINESE',
        key: 'cn'
    }],
    defaultSkin: 'grey'
})
.constant('API_CONFIG', {
    // replace it soon
    url':  'http://c3po-api.localhost/api/rs'
})
/**
 *  SETUP TRANSLATIONS
 */
.config(function ($stateProvider, $urlRouterProvider, $translateProvider, $translatePartialLoaderProvider, localStorageServiceProvider, APP) {
    /**
     *  each module loads its own translation file - making it easier to create translations
     *  also translations are not loaded when they aren't needed
     *  each module will have a il8n folder that will contain its translations
     */
    $translateProvider.useLoader('$translatePartialLoader', {
        urlTemplate: '{part}/il8n/{lang}.json'
    });

    $translatePartialLoaderProvider.addPart('app');

    // make sure all values used in translate are sanitized for security
    $translateProvider.useSanitizeValueStrategy('sanitize');

    // cache translation files to save load on server
    $translateProvider.useLoaderCache(true);

    // get languages set in APP constant
    var languageKeys = [];
    for(var lang in APP.languages) {
        languageKeys.push(APP.languages[lang].key);
    }
    /**
     *  try to detect the users language by checking the following
     *      navigator.language
     *      navigator.browserLanguage
     *      navigator.systemLanguage
     *      navigator.userLanguage
     */
    $translateProvider
    .registerAvailableLanguageKeys(languageKeys, {
        'en_US': 'en',
        'en_UK': 'en'
    })
    .use('en');

    // store the users language preference in a cookie
    $translateProvider.useLocalStorage();

    // setup public states & routes
    $stateProvider
    .state('admin-panel', {
        abstract: true,
        templateUrl: 'app/layouts/admin-panel/admin-panel.tmpl.html',
        data: {
            toolbar: {
                extraClass: '',
                background: false,
                shrink: false 
            },
        }
    })

    .state('admin-panel.default', {
        abstract: true,
        views: {
            sidebarLeft: {
                templateUrl: 'components/sidebar-left/sidebar-left.tmpl.html',
                controller: 'SidebarLeftController'
            },
            sidebarRight: {
                templateUrl: 'components/sidebar-right/sidebar-right.tmpl.html',
                controller: 'SidebarRightController'
            },
            toolbar: {
                templateUrl: 'components/toolbars/default.tmpl.html',
                controller: 'DefaultToolbarController'
            },
            content: {
                template: '<div id="admin-panel-content-view" flex ui-view></div>'
            }
        },
    })

    .state('admin-panel-no-scroll', {
        abstract: true,
        templateUrl: 'app/layouts/no-scroll/no-scroll.tmpl.html',
        data: {
            toolbar: {
                extraClass: '',
                background: false,
                shrink: false
            },
        }
    })

    .state('admin-panel-no-scroll.default', {
        abstract: true,
        views: {
            sidebarLeft: {
                templateUrl: 'components/sidebar-left/sidebar-left.tmpl.html',
                controller: 'SidebarLeftController'
            },
            sidebarRight: {
                templateUrl: 'components/sidebar-right/sidebar-right.tmpl.html',
                controller: 'SidebarRightController'
            },
            toolbar: {
                templateUrl: 'components/toolbars/default.tmpl.html',
                controller: 'DefaultToolbarController'
            },
            content: {
                template: '<div flex ui-view layout="column"></div>'
            }
        },
    })

    .state('404', {
        url: '/404',
        templateUrl: '404.html',
        controller: function($scope, $state, APP) {
            $scope.app = APP;

            $scope.goHome = function() {
                $state.go('admin-panel.default.dashboard-analytics');
            };
        }
    })

    .state('500', {
        url: '/500',
        templateUrl: '500.html',
        controller: function($scope, $state, APP) {
            $scope.app = APP;

            $scope.goHome = function() {
                $state.go('admin-panel.default.dashboard-analytics');
            };
        }
    });

    // set default routes when no path specified
    //$urlRouterProvider.when('', '/dashboards/analytics');
    //$urlRouterProvider.when('/', '/dashboards/analytics');
    
    // replace it soon.
    $urlRouterProvider.when('', '/login');
    $urlRouterProvider.when('/', '/login');


    // always goto 404 if route not found
    $urlRouterProvider.otherwise('/404');

    // set prefix for local storage
    localStorageServiceProvider
    // replace it soon.
    .setPrefix('c-3po')
    .setStorageType('sessionStorage');
})
/**
 *  PALETTES & THEMES & SKINS oh my.....
 */
.config(function ($mdThemingProvider, cylonThemingProvider, cylonSkinsProvider, APP) {
    /**
     *  PALETTES
     */
    $mdThemingProvider.definePalette('white', {
        '50': 'ffffff',
        '100': 'ffffff',
        '200': 'ffffff',
        '300': 'ffffff',
        '400': 'ffffff',
        '500': 'ffffff',
        '600': 'ffffff',
        '700': 'ffffff',
        '800': 'ffffff',
        '900': 'ffffff',
        'A100': 'ffffff',
        'A200': 'ffffff',
        'A400': 'ffffff',
        'A700': 'ffffff',
        'contrastDefaultColor': 'dark',    // whether, by default, text (contrast)
    });

    $mdThemingProvider.definePalette('black', {
        '50': 'e1e1e1',
        '100': 'b6b6b6',
        '200': '8c8c8c',
        '300': '646464',
        '400': '4d4d4d',
        '500': '3a3a3a',
        '600': '2f2f2f',
        '700': '232323',
        '800': '1a1a1a',
        '900': '121212',
        'A100': 'ffffff',
        'A200': 'ffffff',
        'A400': 'ffffff',
        'A700': 'ffffff',
        'contrastDefaultColor': 'light',    // whether, by default, text (contrast)
    });

    /**
     *  SKIN
     */
    // GREY SKIN
    cylonThemingProvider.theme('blue-grey')
    .primaryPalette('blue-grey')
    .accentPalette('amber')
    .warnPalette('orange');

    /**
     *  ALLOW SKIN TO BE SAVED IN A COOKIE ( Only for demo or test )
     *  This overrides any skin set in a call to cylonSkinsProvider.setSkin if there is a cookie
     *  REMOVE LINE BELOW FOR PRODUCTION 
     */
    cylonSkinsProvider.useSkinCookie(true);

    /**
     *  SET DEFAULT SKIN
     */
    cylonSkinsProvider.setSkin(APP.defaultSkin);
})
// delete it soon
.config(function (ChartJsProvider) {
    // Configure all charts to use material design colors
    ChartJsProvider.setOptions({
        colours: [
            '#4285F4',    // blue
            '#DB4437',    // red
            '#F4B400',    // yellow
            '#0F9D58',    // green
            '#AB47BC',    // purple
            '#00ACC1',    // light blue
            '#FF7043',    // orange
            '#9E9D24',    // browny yellow
            '#5C6BC0'     // dark blue
        ],
        responsive: true,
    });
})
.run(function ($rootScope, $window) {
    // add a class to the body if we are on windows
    if($window.navigator.platform.indexOf('Win') !== -1) {
        $rootScope.bodyClasses = ['os-windows'];
    }
})
// setup google charts to use material charts
// delete it soon
.value('googleChartApiConfig', {
    version: '1.1',
    optionalSettings: {
        packages: ['line', 'bar', 'geochart', 'scatter'],
        language: 'en'
    }
});
