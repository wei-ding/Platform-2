'use strict';

/**
 * @ngdoc module
 * @name c-3poForms
 * @description
 *
 * The `c-3poForms` module adds the example form pages & menus
 */
angular.module('c-3poForms', [])
.config(function ($translatePartialLoaderProvider, $stateProvider) {
    $translatePartialLoaderProvider.addPart('app/forms');

    $stateProvider
    .state('admin-panel.default.forms-inputs', {
        url: '/forms/inputs',
        templateUrl: 'app/forms/inputs.tmpl.html'
    })
    .state('admin-panel.default.forms-binding', {
        url: '/forms/binding',
        templateUrl: 'app/forms/binding.tmpl.html',
    })
    .state('admin-panel.default.forms-autocomplete', {
        url: '/forms/autocomplete',
        templateUrl: 'app/forms/autocomplete.tmpl.html'
    })
    .state('admin-panel.default.forms-validation', {
        url: '/forms/validation',
        templateUrl: 'app/forms/validation.tmpl.html'
    });

})
.run(function(SideMenu) {
    SideMenu.addMenu({
        name: 'MENU.FORMS.FORMS',
        icon: 'icon-event-available',
        type: 'dropdown',
        priority: 3.3,
        children: [{
            name: 'MENU.FORMS.AUTOCOMPLETE',
            type: 'link',
            state: 'admin-panel.default.forms-autocomplete'
        },{
            name: 'MENU.FORMS.BINDING',
            type: 'link',
            state: 'admin-panel.default.forms-binding'
        },{
            name: 'MENU.FORMS.INPUTS',
            type: 'link',
            state: 'admin-panel.default.forms-inputs'
        },{
            name: 'MENU.FORMS.VALIDATION',
            type: 'link',
            state: 'admin-panel.default.forms-validation'
        }]
    });
    SideMenu.addMenu({
        type: 'divider',
        priority: 3.4
    });
});

