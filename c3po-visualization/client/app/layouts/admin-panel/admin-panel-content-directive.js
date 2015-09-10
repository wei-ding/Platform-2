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
