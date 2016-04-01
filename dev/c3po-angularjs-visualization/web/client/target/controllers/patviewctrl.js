var appv=angular.module('patview',['ngRoute','ui.bootstrap','ngMessages']);
appv.controller('patviewctrl', ['$scope',function($scope){

}]);
appv.constant('url','http://54.205.29.187:8080/clinical3PO/');
//appv.constant('url','http://localhost:8080/clinical3PO/');
appv.config(function($httpProvider) {
	//Enable cross domain calls
	$httpProvider.defaults.useXDomain = true;
	delete $httpProvider.defaults.headers.common['X-Requested-With'];
	$httpProvider.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded; charset=UTF-8';

});
appv.config(function($routeProvider){

	$routeProvider.
	when('/',{
		templateUrl:'blank.html'
	}).when('/:id',{
		templateUrl:'patientView.html',
		controller:'patviewctrl'
	}).when('/:id/:val',{
		templateUrl:'patientObservation.html',
		controller:'patobsctrl'
	}).
	otherwise({
		redirectTO:'/'

	});

});


appv.directive('myNodes', ['$compile','$http','$routeParams','url', function ($compile,$http,$routeParams,url) {
	return {
		restrict: 'A',
		link: function(scope, element, attrs) {
		var margin = {top: 20, right: 120, bottom: 20, left: 120},
				width = 960 - margin.right - margin.left,
				height = 500 - margin.top - margin.bottom;
		var treeData=$http.get(url+'ang/Visualization/Patient/json/'+$routeParams.id).success(function(data){
			var i = 0,
					duration = 750,                        //not discussed yet
					root;
			var duration1 = d3.event && d3.event.altKey ? 5000 : 500;
			console.log(duration1);

			var tree = d3.layout.tree()
					.size([height, width]);

			var diagonal = d3.svg.diagonal()
					.projection(function(d) { return  [d.y, d.x]; });

			var svg = d3.select("body").append("svg")
					.attr("width", width + margin.right + margin.left)
					.attr("height", height + margin.top + margin.bottom)
					.append("g")
					.attr("transform", "translate(" + margin.left + "," + margin.top + ")");

			root = data;
			root.x0 = height / 2;
			root.y0 = 0;
			function clickAll(d) {
				if (d.children) {
					d.children.forEach(clickAll);
					click(d);
				}
			}


			// Initialize the display to show a few nodes.
			root.children.forEach(clickAll);

			update(root);

			d3.select(self.frameElement).style("height", "0px");


			function update(source) {

				// Compute the new tree layout.
				var nodes = tree.nodes(root).reverse(),
						links = tree.links(nodes);

				// Normalize for fixed-depth.
				nodes.forEach(function(d) { d.y = d.depth * 180; });

				// Update the nodes…
				var node = svg.selectAll("g.node")
						.data(nodes, function(d) { return d.id || (d.id = ++i); });

				// Enter any new nodes at the parent's previous position.
				var nodeEnter = node.enter().append("g")
						.attr("class", "node")
						.attr("transform", function(d) { return "translate(" + source.y0 + "," + source.x0 + ")"; })
						.on("click", click);

				nodeEnter.append("circle")
				.attr("r", 1e-6)
				.style("fill", function(d) { return d._children ? "lightsteelblue" : "#fff"; });

				nodeEnter.append("text")
				.attr("x", function(d) { return d.children || d._children ? -13 : 13; })
				.attr("dy", ".35em")
				.attr("text-anchor", function(d) { return d.children || d._children ? "end" : "start"; })
				.text(function(d) { return d.name; })
				.style("fill-opacity", 1e-6);

				// Transition nodes to their new position.
				var nodeUpdate = node.transition()
						.duration(duration)
						.attr("transform", function(d) { return "translate(" + d.y + "," + d.x + ")"; });

				nodeUpdate.select("circle")
				.attr("r", 5)
				.style("fill", function(d) { return d._children ? "lightsteelblue" : "#fff"; });

				nodeUpdate.select("text")
				.style("fill-opacity", 1);

				// Transition exiting nodes to the parent's new position.
				var nodeExit = node.exit().transition()
						.duration(duration)
						.attr("transform", function(d) { return "translate(" + source.y + "," + source.x + ")"; })
						.remove();

				nodeExit.select("circle")
				.attr("r", 1e-6);

				nodeExit.select("text")
				.style("fill-opacity", 1e-6);

				// Update the links…
				var link = svg.selectAll("path.link")
						.data(links, function(d) { return d.target.id; });

				// Enter any new links at the parent's previous position.
				link.enter().insert("path", "g")
				.attr("class", "link")
				.attr("d", function(d) {
					var o = {x: source.x0, y: source.y0};
					return diagonal({source: o, target: o});
				});

				// Transition links to their new position.
				link.transition()
				.duration(duration)
				.attr("d", diagonal);

				// Transition exiting nodes to the parent's new position.
				link.exit().transition()
				.duration(duration)
				.attr("d", function(d) {
					var o = {x: source.x, y: source.y};
					return diagonal({source: o, target: o});
				})
				.remove();

				// Stash the old positions for transition.
				nodes.forEach(function(d) {
					d.x0 = d.x;
					d.y0 = d.y;
				});
			}


			// Toggle children on click.
			function click(d) {

				if (d.children) {
					d._children = d.children;
					d.children = null;
				} else {
					d.children = d._children;
					d._children = null;
				}
				update(d);
			}


			var mySvg = d3.select(element[0])
					.append("svg")
					.attr("width", 200)
					.attr("height", 100);

		}).error(function(data){
			console.log("failed to retrive");});

	}
	};
}]); 

