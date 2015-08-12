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
