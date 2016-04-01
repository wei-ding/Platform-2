'use strict';

/**
 * @ngdoc provider
 * @name cylonThemingProvider
 * @module c-3po
 * @kind provider
 *
 * @description
 *
 * Wrapper for material themes
 */
angular.module('c-3po')
.provider('cylonTheming', ThemingProvider);

function ThemingProvider($mdThemingProvider) {
    var themes = {};

    return {
        theme: function(name) {
            if(themes[name] !== undefined ) {
                return themes[name];
            }

            var theme = new Theme(name);

            themes[name] = theme;

            return themes[name];

        },
        $get: function() {
            return {
                getTheme: function(themeName) {
                    return themes[themeName];
                },
                getThemeHue: function(themeName, intentName, hue) {
                    if(undefined !== $mdThemingProvider._THEMES[themeName] && undefined !== $mdThemingProvider._THEMES[themeName].colors[intentName]) {
                        var palette = $mdThemingProvider._THEMES[themeName].colors[intentName];
                        if(undefined !== $mdThemingProvider._PALETTES[palette.name] && undefined !== $mdThemingProvider._PALETTES[palette.name][palette.hues[hue]]) {
                            return $mdThemingProvider._PALETTES[palette.name][palette.hues[hue]];
                        }
                    }
                },
                getPalette: function(name) {
                    return $mdThemingProvider._PALETTES[name];
                },
                getPaletteColor: function(paletteName, hue) {
                    if(undefined !== $mdThemingProvider._PALETTES[paletteName] && undefined !== $mdThemingProvider._PALETTES[paletteName][hue]) {
                        return $mdThemingProvider._PALETTES[paletteName][hue];
                    }
                },
                rgba: $mdThemingProvider._rgba,
                palettes: $mdThemingProvider._PALETTES,
                themes: $mdThemingProvider._THEMES,
                parseRules: $mdThemingProvider._parseRules,
            };
        }
    };
}

function Theme(name) {
    var THEME_COLOR_TYPES = ['primary', 'accent', 'warn', 'background'];
    var self = this;
    self.name = name;
    self.colors = {};
    self.isDark = false;

    THEME_COLOR_TYPES.forEach(function(colorType) {
        self[colorType + 'Palette'] = function setPaletteType(paletteName, hues) {
            self.colors[colorType] = {
                name: paletteName,
                hues: {}
            };
            if(undefined !== hues) {
                self.colors[colorType].hues = hues;
            }
            return self;
        };
    });

    self.dark = function(isDark) {
        // default setting when dark() is called is true
        self.isDark = isDark === undefined ? true : isDark;
    };
}
