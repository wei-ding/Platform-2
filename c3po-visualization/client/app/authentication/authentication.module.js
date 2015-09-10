'use strict';

/**
 * @ngdoc module
 * @name c3po.authentication
 * @description
 *
 * The `c3po.authentication` module handles all the login and signup pages
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
/*
    replace it soon.
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
            name: 'MENU.AUTH.PROFILE',
            state: 'admin-panel.default.profile',
            type: 'link',
        }]
    });
});

