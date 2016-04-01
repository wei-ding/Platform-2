'use strict';

/**
* @ngdoc directive
* @name paletteBackground
* @restrict A
* @scope
*
* @description
*
* Adds a palette colour and contrast CSS to an element
*
* @usage
* ```html
* <div palette-background="green:500">Coloured content</div>
* ```
*/
angular.module('c-3po')
.directive('paletteBackground', function (cylonTheming) {
    return {
        restrict: 'A',
        link: function ($scope, $element, attrs) {
            var splitColor = attrs.paletteBackground.split(':');
            var color = cylonTheming.getPaletteColor(splitColor[0], splitColor[1]);

            if(color !== undefined) {
                $element.css({
                    'background-color': cylonTheming.rgba(color.value),
                    'border-color': cylonTheming.rgba(color.value),
                    'color': cylonTheming.rgba(color.contrast)
                });
            }
        }
    };
});
