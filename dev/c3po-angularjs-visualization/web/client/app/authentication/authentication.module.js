'use strict';

/**
 * @ngdoc module
 * @name c3po.authentication
 * @description
 *
 * The `c3po.authentication` module handles all the login and signup pages
 */
angular.module('c-3poAuthentication', [])
.config(function ($translatePartialLoaderProvider, $stateProvider) {
    $translatePartialLoaderProvider.addPart('app/authentication');

    $stateProvider
    .state('authentication', {
        abstract: true,
        templateUrl: 'app/authentication/layouts/authentication.tmpl.html',
    })
    .state('authentication.login', {
        url: '/login',
        templateUrl: 'app/authentication/login/login.tmpl.html',
        controller: 'LoginController'
    })
    .state('authentication.signup', {
        url: '/signup',
        templateUrl: 'app/authentication/signup/signup.tmpl.html',
        controller: 'SignupController'
    })
    .state('authentication.lock', {
        url: '/lock',
        templateUrl: 'app/authentication/lock/lock.tmpl.html',
        controller: 'LockController'
    })
    .state('authentication.forgot', {
        url: '/forgot',
        templateUrl: 'app/authentication/forgot/forgot.tmpl.html',
        controller: 'ForgotController'
    })
    .state('admin-panel.default.profile', {
        url: '/profile',
        templateUrl: 'app/authentication/profile/profile.tmpl.html',
        controller: 'ProfileController'
    });
})
.run(function(SideMenu) {
    SideMenu.addMenu({
        name: 'MENU.AUTH.AUTH',
        icon: 'icon-person',
        type: 'dropdown',
        priority: 4.1,
        children: [{
            name: 'MENU.AUTH.LOGIN',
            state: 'authentication.login',
            type: 'link',
        },{
            name: 'MENU.AUTH.SIGN_UP',
            state: 'authentication.signup',
            type: 'link',
        },{
            name: 'MENU.AUTH.FORGOT',
            state: 'authentication.forgot',
            type: 'link',
        },{
            name: 'MENU.AUTH.LOCK',
            state: 'authentication.lock',
            type: 'link',
        },{
            name: 'MENU.AUTH.PROFILE',
            state: 'admin-panel.default.profile',
            type: 'link',
        }]
    });
});

