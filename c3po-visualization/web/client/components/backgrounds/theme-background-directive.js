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
