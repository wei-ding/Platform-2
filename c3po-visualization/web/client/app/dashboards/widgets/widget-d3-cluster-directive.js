'use strict';

/**
* @ngdoc directive
* @name chartjsLineWidget
* @restrict A
* @scope
*
* @description
*
* Adds chartjs line chart data to widget
*
* @usage
* ```html
* <widget chartjs-line-widget>
* ```
*/
angular.module('c-3poRisk')
.directive('d3ClusterWidget', ['$window', '$timeout', function ($window, $timeout, $interval) {
    return {
        require: 'widget',
        restrict: 'A',
        link: function ($scope, $element, attrs, widgetCtrl) {
            widgetCtrl.setLoading(true);

            $timeout(function() {
                widgetCtrl.setLoading(false);
            }, 1500);

            widgetCtrl.setMenu({
                icon: 'icon-more-vert',
                items: [{
                    icon: 'icon-refresh',
                    title: 'DASHBOARDS.WIDGETS.MENU.REFRESH',
                    click: function($event) {
                        $interval.cancel($scope.intervalPromise);
                        widgetCtrl.setLoading(true);
                        $timeout(function() {
                            widgetCtrl.setLoading(false);
                        }, 1500);
                    }
                },{
                    icon: 'icon-share',
                    title: 'DASHBOARDS.WIDGETS.MENU.SHARE',
                },{
                    icon: 'icon-print',
                    title: 'DASHBOARDS.WIDGETS.MENU.PRINT',
                }]
            });
        }
    };
}]);

angular.module('c-3poRisk')
.directive('d3Cluster', ['$window', '$timeout', function ($window, $timeout, $interval) {
    return {
        restrict: 'EA',
        replace: true,
        link: function ($scope, $element, attr) {
                               var el = $element[0];
                               var cw = el.clientWidth;
                               var ch = el.clientWidth;
                               var format = d3.format(",d");
                               var color = d3.scale.category10();
                               var margin = 4;

                                var pack = d3.layout.pack()
                                    .size([cw - margin, ch - margin])
                                    .value(function(d) { return d.size; });

                                var svg = d3.select(el).append("svg")
                                    .attr("width", cw)
                                    .attr("id","d3-cluster")
                                    .attr("height", ch)
                                    .append("g")
                                    .attr("transform", "translate(2,2)");

                                $scope.$watch(function(){
                                    cw = $element[0].clientWidth;
                                    ch = $element[0].clientWidth;
                                    return cw + ch;
                                }, resize);

                                function resize(){
                                    d3.select("#d3-cluster").remove();
                                    cw = $element[0].clientWidth;
                                    ch = $element[0].clientWidth;
                                    pack = d3.layout.pack()
                                        .size([cw - margin, ch - margin])
                                        .value(function(d) { return d.size; });

                                    svg = d3.select($element[0]).append("svg")
                                        .attr("width", cw)
                                        .attr("id","d3-cluster")
                                        .attr("height", ch)
                                        .append("g")
                                        .attr("transform", "translate(2,2)");
                                
                                    d3.json($scope.clusterurl, function(error, root) {
                                        var node = svg.datum(root).selectAll(".node")
                                            .data(pack.nodes)
                                            .enter().append("g")
                                            .attr("class", function(d) { return d.children ? "node" : "leaf node"; })
                                            .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });

                                        node.append("title")
                                            .text(function(d) { return d.name; })
                                        ;

                                        node.append("circle")
                                            .attr("r", function(d) { return d.kind == "title" ? 0 : d.r - 4; })
                                            .style("fill", function(d) { return d.parent ? d.children ? color(d.name) : color(d.parent.name) : d3.rgb(31, 119, 180) })
                                            .style("fill-opacity", function(d) { return d.parent ? d.children ? "0.4": "0.4" : "0.1"; });

                                        node.filter(function(d) { return !d.children && d.name != null; }).append("text")
                                            .attr("dy", ".3em")
                                            .style("text-anchor", "middle")
                                            .style("font-size", function(d) { return Math.max(8, d.r / 2.6) + "px"; })
                                            .style("fill", function(d) { return d.kind != "title" ? "#fff" : "#444"})
                                            .style("font", "'Helvetica Neue', Helvetica, Arial, sans-serif")
                                            .text(function(d) { return d.name.substring(0, d.r / 2.3); });

                                        node.filter(function(d) { return d.line1 != null; }).append("text")
                                            .attr("dy", "-0.3em")
                                            .style("text-anchor", "middle")
                                            .style("font-size", function(d) { return Math.max(8, d.r / 2.6) + "px"; })
                                            .style("font", "'Helvetica Neue', Helvetica, Arial, sans-serif")
                                            .style("fill", function(d) { return d.kind != "title" ? "#fff" : "#444"})
                                            .text(function(d) { return d.line1.substring(0, d.r / 2.3); });

                                        node.filter(function(d) { return d.line2 != null; }).append("text")
                                            .attr("dy", "0.9em")
                                            .style("text-anchor", "middle")
                                            .style("font-size", function(d) { return Math.max(8, d.r / 2.6) + "px"; })
                                            .style("font", "'Helvetica Neue', Helvetica, Arial, sans-serif")
                                            .style("fill", function(d) { return d.kind != "title" ? "#fff" : "#444"})
                                            .text(function(d) { return d.line2.substring(0, d.r / 2.3); });
                                    });
                                    d3.select(self.frameElement).style("width", cw+"px");
                                    d3.select(self.frameElement).style("height", cw+"px");
                                };



        }
    };
}]);


