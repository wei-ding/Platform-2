'use strict';

/**
 * @ngdoc module
 * @name triSeedModule
 * @description
 *
 * Start up demo module to show how modules work
 */
angular.module('triSeedModule', [])
.config(function ($translatePartialLoaderProvider, $stateProvider) {

    // Tell the translate module that we want translations to be loaded from the il8n folder in this module
    $translatePartialLoaderProvider.addPart('app/seed-module');

    // Create a state for our seed test page
    $stateProvider
    .state('admin-panel.default.seed-page', {
        // set the url of this page
        url: '/seed/seed-test',
        // set the html template to show on this page
        templateUrl: 'app/seed-module/seed-page.tmpl.html',
        // set the controller to load for this page
        controller: 'SeedPageController'
    });
})
.run(function(SideMenu) {
    // add a menu for the seed page we created in the $stateProvider above
    SideMenu.addMenu({
        // give the menu a name to show (should be translatable and in the il8n folder json)
        name: 'MENU.SEED.SEED-PAGE',
        // point this menu to the state we created in the $stateProvider above
        state: 'admin-panel.default.seed-page',
        // set the menu type to a link
        type: 'link',
        // set an icon for this menu
        icon: 'icon-cake',
        // set a proirity for this menu item, menu is sorted by priority
        priority: 1.1
    });
});
