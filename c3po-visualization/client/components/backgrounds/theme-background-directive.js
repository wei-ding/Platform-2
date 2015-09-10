'use strict';

/**
* @ngdoc directive
* @name themeBackground
* @restrict A
* @scope
*
* @description
*
* Adds a theme colour and contrast CSS to an element
*
* @usage
* ```html
* <div md-theme="cyan" theme-background="primary|accent|warn|background:default|hue-1|hue-2|hue-3">Coloured content</div>
* ```
* @author Wei Ding
* @version 0.1.0
*/

/*!
    This content is released under the (http://opensource.org/licenses/MIT) MIT License.

    The MIT License (MIT)

    Copyright (c) <2014> <>

    Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. * MIT 
*/
/*!
 * will remove this directive soon.
 */
angular.module('c-3po')
.directive('themeBackground', function ($mdTheming, cylonTheming) {
    return {
        restrict: 'A',
        link: function ($scope, $element, attrs) {
            $mdTheming($element);

            // make sure we have access to the theme
            var $mdTheme = $element.controller('mdTheme');
            if(undefined !== $mdTheme) {
                var intent = attrs.themeBackground;
                var hue = 'default';

                // check if we have a hue provided
                if(intent.indexOf(':') !== -1) {
                    var splitIntent = attrs.themeBackground.split(':');
                    intent = splitIntent[0];
                    hue = splitIntent[1];
                }
                // get the color and apply it to the element
                var color = cylonTheming.getThemeHue($mdTheme.$mdTheme, intent, hue);
                if(color !== undefined) {
                    $element.css({
                        'background-color': cylonTheming.rgba(color.value),
                        'border-color': cylonTheming.rgba(color.value),
                        'color': cylonTheming.rgba(color.contrast)
                    });
                }
            }
        }
    };
});
