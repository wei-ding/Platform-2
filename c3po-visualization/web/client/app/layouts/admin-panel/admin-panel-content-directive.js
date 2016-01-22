'use strict';

/**
* @ngdoc directive
* @name adminPanelContent
* @restrict E
* @scope
*
* @description
*
* Handles injection of the footer into the admin panel content
*
* @usage
* ```html
* <div ui-view="content" admin-panel-content></div>
* ```
*/
angular.module('c-3po')
.directive('adminPanelContent', function($compile, $templateRequest) {
    return {
        restrict: 'A',
        link: function($scope, $element) {
            $scope.$on('$stateChangeStart', function() {
                var mdContentElement = $element.parent();
                // scroll page to the top when content is loaded (stops pages keeping scroll position even when they have changed url)
                mdContentElement.scrollTop(0);
            });

            $scope.$on('$viewContentLoaded', function() {
                var contentView = $element.find('#admin-panel-content-view');

                // add footer to the content view
                $templateRequest('components/footer/footer.tmpl.html')
                .then(function(template) {
                    // compile template with current scope and add to the content
                    var linkFn = $compile(template);
                    var content = linkFn($scope);
                    contentView.append(content);
                }, function() {
                    console.error('Could not load footer tempalate');
                });
            });
        }
    };
});