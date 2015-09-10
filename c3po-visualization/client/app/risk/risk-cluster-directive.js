'use strict';

/**
* @ngdoc directive
* @name risk-cluster-scatterPlot
* @restrict E
* @scope
*
* @description
*
* Adds a cluster d3 chart into cluster page
*
* @usage
* ```html
* <risk-cluster-scatterPlot></risk-cluster-scatterPlot>
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
angular.module('c-3poRisk')
.directive('risk-cluster-scatterPlot', function($window, APP) {
    return {
        restrict: 'E',
        replace: true,
        scope: {
            scatterdata: '='
        },
        link: function(scope, element, array) {

        var data = scope.scatterdata;
        var el = element[0];
        var w = 400;
        var h = 400;
        var padding = 40;

        //Create scale functions
        var xScale = d3.scale.linear()
            .domain([0, d3.max(data, function(d) {
            return d[0];
            })])
            .range([padding, w - padding * 2]);

        var yScale = d3.scale.linear()
            .domain([0, d3.max(data, function(d) {
            return d[1];
            })])
            .range([h - padding, padding]);

        var rScale = d3.scale.linear()
            .domain([0, d3.max(data, function(d) {
            return d[1];
            })])
            .range([4, 14]);

        // Create axes
        var xAxis = d3.svg.axis()
            .scale(xScale)
            .orient('bottom')
            .ticks(5);

        var yAxis = d3.svg.axis()
            .scale(yScale)
            .orient('left')
            .ticks(5);

        //Create SVG element
        var svg = d3.select(el)
            .append("svg")
            .attr('id', 'scatter-plot')
            .attr("width", w)
            .attr("height", h)
            .attr('viewBox', '0 0 400 400')
            .attr('preserveAspectRatio', 'xMinYMin');

        svg.append("g") // create new g
            .attr('id', 'circles') // assign id of "circles"
            .attr('clip-path', 'url(#chart-area)') // add reference to clipPath
            .selectAll('circle')
            .data(data)
            .enter()
            .append("circle")
            .attr("cx", function(d) {
            return xScale(d[0]);
            })
            .attr("cy", function(d) {
            return yScale(d[1]);
            })
            .attr("r", function(d) {
            return rScale(d[1]);
            });

        svg.append('g')
                        .attr('class', 'axis')
                        .attr('transform', 'translate(0,' + (h - padding) + ')') // sends axis to bottom
                        .call(xAxis);
        
                svg.append('g')
                        .attr('class', 'axis')
                        .attr('transform', 'translate(' + padding + ',0)')
                        .call(yAxis);

        svg.append('clipPath')
            .attr('id', 'chart-area')
            .append('rect')
            .attr('x', padding)
            .attr('y', padding)
            .attr('width', w - padding * 3)
            .attr('height', h - padding * 2);

        /* ------ Render Specs ----- */
        scope.render = function(newData, oldData) {
            var data = newData || data;

            svg.selectAll("circle")
            .data(data)
            .transition()
            .duration(400) // milliseconds, so 1 second duration
            .each('start', function() {
                d3.select(this)
                .attr('fill', '#EA5A5A')
            })
            .attr("cx", function(d) {
                return xScale(d[0]);
            })
            .attr("cy", function(d) {
                return yScale(d[1]);
            })
            .attr("r", function(d) {
                return rScale(d[1]);
            })
            .each('end', function() {
                d3.select(this)
                .attr('fill', '#28BD8B')
            });
            
            svg.append("g") // create new g
            .attr('id', 'circles') // assign id of "circles"
            .attr('clip-path', 'url(#chart-area)') // add reference to clipPath
    

        svg.append('g')
            .attr('class', 'axis')
            .attr('transform', 'translate(0,' + (h - padding) + ')') // sends axis to bottom
            .call(xAxis);

        svg.append('g')
            .attr('class', 'axis')
            .attr('transform', 'translate(' + padding + ',0)')
            .call(yAxis);

        };
        
        

        function updateWindow() {
            var scatter = d3.select('#scatter-plot');
            scatter.attr("width", '100%');
            scatter.attr("height", '100%');
        }

        angular.element($window).bind('resize', function() {
            updateWindow();
        })
        
        scope.$watch('scatterdata', function(newData, oldData) {
            console.log('watch fired');
            scope.render(newData, oldData);
        }, true);

        }
    };
});
