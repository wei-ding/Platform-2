'use strict';

/**
* @ngdoc directive
* @name md-table
* @restrict E
* @scope
*
* @description
*
* Creates a material design table
*
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
/*
  will replace it soon.
*/
angular.module('c-3poDashboards')
.directive('mdTable', function($filter) {
    return {
        restrict: 'E',
        scope: {
            columns: '=',
            contents: '=',
            filters: '=',
        },
        link: function($scope, $element, attrs) {
            var sortableColumns = [];
            var activeSortColumn = null;
            var activeSortOrder = false;

            // init page size if not set to default
            $scope.pageSize = attrs.pageSize === undefined ? 0 : attrs.pageSize;

            // init page if not set to default
            $scope.page = attrs.page === undefined ? 0 : attrs.page;

            // make an array of all sortable columns
            angular.forEach($scope.columns, function(column) {
                if(column.sortable) {
                    sortableColumns.push(column.field);
                }
            });

            $scope.refresh = function(resetPage) {
                if(resetPage === true) {
                    $scope.page = 0;
                }
                $scope.contents = $filter('orderBy')($scope.contents, activeSortColumn, activeSortOrder);
            };

            // if we have sortable columns sort by first by default
            if(sortableColumns.length > 0) {
                // sort first column by default
                activeSortOrder = false;
                activeSortColumn = sortableColumns[0];
                $scope.refresh();
            }

            $scope.sortClick = function(field) {
                if(sortableColumns.indexOf(field) !== -1) {
                    if(field === activeSortColumn) {
                        activeSortOrder = !activeSortOrder;
                    }
                    activeSortColumn = field;
                    $scope.refresh();
                }
            };

            $scope.showSortOrder = function(field, orderDown) {
                return field === activeSortColumn && activeSortOrder === orderDown;
            };

            $scope.headerClass = function(field) {
                var classes = [];
                if(sortableColumns.indexOf(field) !== -1) {
                    classes.push('sortable');
                }
                if(field === activeSortColumn) {
                    classes.push('sorted');
                }
                return classes;
            };

            $scope.cellContents = function(column, content) {
                if(column.filter !== undefined) {
                    return $filter(column.filter)(content[column.field]);
                }
                else {
                    return content[column.field];
                }
            };

            $scope.numberOfPages = function() {
                return Math.ceil($scope.contents.length / $scope.pageSize);
            };

            $scope.pageStart = function() {
                return ($scope.page * $scope.pageSize) + 1;
            };

            $scope.pageEnd = function() {
                var end = (($scope.page + 1) * $scope.pageSize);
                if(end > $scope.contents.length) {
                    end = $scope.contents.length;
                }
                return end;
            };

            $scope.goToPage = function (page) {
                $scope.page = page;
            };
        },
        templateUrl: 'components/tables/table-directive.tmpl.html',
    };
});
