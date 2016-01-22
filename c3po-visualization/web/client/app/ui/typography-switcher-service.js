'use strict';

angular.module('c-3poUI')
.service('TypographySwitcher', function($window, $cookies, UI_FONTS) {
    var service = {
        init: function() {
            // if we arent using the default font then change it
            var currentFont = service.getCurrentFont();
            if(currentFont.name !== 'Roboto Draft') {
                service.changeFont(currentFont);
            }
        },
        getCurrentFont: function() {
            // if we have no current font set, set it to first font (Roboto)
            var fontCookie = $cookies.get('cylon-typography-font');
            if(undefined === fontCookie) {
                $cookies.put('cylon-typography-font', angular.toJson(UI_FONTS[0]));
            }
                        
            return angular.fromJson($cookies.get('cylon-typography-font'));
        },
        changeFont: function(font) {
            $window.WebFont.load({
                google: {
                  families: [font.google]
                },
                active: function() {
                    $('button,select,html,textarea,input').css({'font-family': font.family});
                    $cookies.put('cylon-typography-font', angular.toJson(font));
                },
                inactive: function() {
                    console.error('Font ' + font.name + ' could not be loaded');
                }
            });
        }
    };

    return service;
});
