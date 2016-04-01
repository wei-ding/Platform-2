'use strict';

/**
* @ngdoc directive
* @name chatWidget
* @restrict E
* @scope
*
* @description
*
* Adds some chat data
*
* @usage
* ```html
* <widget chat-widget></widget>
* ```
*/
angular.module('c-3poDashboards')
.directive('animateElements', function($timeout) {
    return {
        restrict: 'A',
        link: function($scope, $element, attrs) {
            var $widgets  = [];
            var $dividers = [];

            // using interval checking since window load event does not work on some machines
            var widgetsLoaded = setInterval(function() {
                $widgets = $element.find('.timeline-widget');

                if($widgets.length > 0 && $($widgets[0]).height() > 1) {
                    $dividers = $element.find('.timeline-x-axis');
                    onScrollCallback();
                    clearInterval(widgetsLoaded);
                }
            }, 100);

            var onScrollCallback =  function() {
                for(var i=0; i<=$widgets.length-1; i++){
                   if ( $($widgets[i]).offset().top <= $(window).scrollTop() + $(window).height() * 0.80 && $($widgets[i]).height() > 1) {
                        var dir = ( i % 2 === 0 ) ? 'left':'right';
                        $($dividers[i]).addClass('timeline-content-animated '+ dir);
                        $($widgets[i]).addClass('timeline-content-animated '+ dir);
                   }
                }
            };

            angular.element('md-content').bind('scroll', onScrollCallback).scroll();
        }
    };
});