var app=angular.module('visualization',[]);
app.directive('visHist', ['$compile',function ($compile) {
	return {
		restrict: 'A',
		link: function(scope, element, attrs) {
		var panel={height:20};
		var margin = {top: 20, right: 20, bottom: 100, left: 40},
			width = 960 - margin.left - margin.right,
			height = 350- margin.top - margin.bottom - panel.height;
		d3.json("cohortjson2.json", function(error, data) {
			var len=Object.keys(data.innercolumns[0]).length;
				d3.selectAll(".main").remove();
			var svg2 = d3.selectAll("body").select("#main").append("div").attr("class","main").append("svg")
						.attr("width",(width+ margin.left + margin.right)*3)
						.attr("height", height + margin.top + margin.bottom+margin.left);
			var svg=svg2.append("g")
						.attr("transform","translate(" + (margin.bottom+margin.left) + "," + margin.top + ")");
			var svg1=svg2.append("g")
						.attr("transform","translate(" + (width+margin.left+margin.bottom) + "," + margin.top + ")");
			
			var input=svg2.append("g")
						  .attr("transform","translate(0,"+margin.top+")");
			input.selectAll('div').data(data.time_Series).enter()
				 .append("text")
				 .attr("y",function(d,i){return i*20;})
				 .attr('class','key_line')
				 .text(function(d,i){return d.concept;});
			d3.selectAll('.key_line').on('click',change);
			svg.append("text")
				.attr("class", "labelaxis")
				.attr("transform", "rotate(-90) translate(-90,-45)")
				.attr("y", 6)
				.attr("dy", ".71em")
				.style("text-anchor", "middle");			
			var xScale = d3.scale.linear().range([0, width]).domain([-365,0]);					
			var  yScale = d3.scale.linear().range([height,0]).domain([data.time_Series[0].min,data.time_Series[0].max]);	
			var xAxis = d3.svg.axis()
					.scale(xScale);
			var yAxis = d3.svg.axis()
					.scale(yScale)
					.orient("left");
			svg.append("svg:g")
				.attr("class", "x axis")
				.attr("transform", "translate(0," + height+ ")")
				.call(xAxis);
			svg.append('text')
				.attr('class','labelaxis')
				.attr("transform", "translate("+(width/2)+"," + (height+margin.left)+ ")")
				.style("text-anchor", "middle")
				.text(data.time_Series[0].concept);
			svg.append("svg:g")
				.attr("class", "yaxis")
				.call(yAxis);
			svg.select(".labelaxis")
			   .text(data.time_Series[0].concept);
				
			var g1=  svg.selectAll("circle");
			var lineGen = d3.svg.line()
					.x(function(d) {while(d.days<=0){
					
						return xScale(d.days);}
					})
					.y(function(d) {while(d.days<=0){
						return yScale(d.value);}
					});
				
			(data.time_Series[0].time_series).forEach(function(d, i) {
				
				svg.append('svg:path')
					.attr("id","id")
					.attr('d', lineGen(d.values))
					.attr('stroke-width', 2)
					.style('stroke',d.color);
				g1.data(d.values)
					.enter()
					.append("circle")
					.attr('cx', function(d) {while(d.days<=0){return xScale(d.days);} })
					.attr('cy', function(d) { while(d.days<=0){return yScale(d.value);} })
					.attr('r', function(d) {while(d.days<=0){return 3;}});
			});  
					
			var  yScale = d3.scale.linear().range([height,0]);			
			function change(d){
				d3.selectAll("circle").remove();
				d3.selectAll("#id").remove();
				d3.selectAll(".yaxis").remove();
				d3.selectAll(".labelaxis").remove();
				yScale.domain([d.min,d.max]);
				yAxis.scale(yScale)
					 .orient("left");
				svg.append('text')
					.attr('class','labelaxis')
					.attr("transform", "translate("+(width/2)+"," + (height+margin.left)+ ")")
					.style("text-anchor", "middle")
					.text(d.concept);
				svg.append("svg:g")
					.attr("class", "yaxis")
					.call(yAxis);
				svg.append("text")
					.attr("class", "labelaxis")
					.attr("transform", "rotate(-90) translate(-90,-45)")
					.attr("y", 6)
					.attr("dy", ".71em")
					.style("text-anchor", "middle")
					.text(d.concept);
				var g1=  svg.selectAll("circle");
				var lineGen = d3.svg.line()
								.x(function(d) {while(d.days<=0){
								return xScale(d.days);}
								})
								.y(function(d) {while(d.days<=0){
								return yScale(d.value);}
								});
				(d.time_series).forEach(function(d, i) {
					svg.append('svg:path')
						.attr("id","id")
						.attr('d', lineGen(d.values))
						.attr('stroke-width', 2)
						.style('stroke',d.color);
					g1.data(d.values)
						.enter()
						.append("circle")
						.attr('cx', function(d) {while(d.days<=0){return xScale(d.days);} })
						.attr('cy', function(d) { while(d.days<=0){return yScale(d.value);} })
						.attr('r', function(d) {while(d.days<=0){return 3;}});
				});  
			}
			var x0 = d3.scale.ordinal()
					   .rangeRoundBands([0, width], 0.1);
			var x1 = d3.scale.ordinal();
			var y = d3.scale.linear()
					        .range([height, 0]);
			var xAxis = d3.svg.axis()
							.scale(x0)
							.orient("bottom");
			var yAxis = d3.svg.axis()
							.scale(y)
							.orient("right")
							.tickFormat(d3.format(".2s"));
			var color = d3.scale.ordinal()
							.range(["red","green","black"]);
			var yBegin;
			var innerColumns = data.innercolumns[0]
			var data1=data.cohorts;
			var columnHeaders = d3.keys(data1[0]).filter(function(key) { return key !== "State"; });
			color.domain(d3.keys(data1[0]).filter(function(key) { return key !== "State"; }));
			data1.forEach(function(d) {
				var yColumn = new Array();
				d.columnDetails = columnHeaders.map(function(name) {
					for (ic in innerColumns) {
						if($.inArray(name, innerColumns[ic]) >= 0){
							if (!yColumn[ic]){
								yColumn[ic] = 0;
							}
							var col= (Object.keys(data));
							var val3;
							var val4;
							var label;
							var na= name.length;
							var ost=name.substring(0,na-1);
							col.map(function(val){
								if (ic == val ){
									var val1=data[val];
									if( d.State == '1-30'){
										var column=val1[0];
										val3 = column.column;
										val4=column.innercolumns;
									}
									if( d.State == '30-60'){
										var column=val1[1];
										val3 = column.column;
										val4=column.innercolumns;
									}
									if( d.State == '60-90'){
										var column=val1[2];
										val3 = column.column;
										val4=column.innercolumns;
									}
								}	
							});
							yBegin = yColumn[ic];
							yColumn[ic] += +d[name];
							return {name: name, column: ic, yBegin: yBegin, yEnd: +d[name] + yBegin,month:d.State,cd:val3,inc:val4,label:ost};
						}
					}
				});
				d.total = d3.max(d.columnDetails, function(d) { 
				return d.yEnd; 
				});
			});
			x0.domain(data1.map(function(d) { return d.State; }));
			x1.domain(d3.keys(innerColumns)).rangeRoundBands([0, x0.rangeBand()]);
			y.domain([0, d3.max(data1, function(d) { 
				return d.total; 
			})]);
			//start of svg1
			svg1.append("g")
				.attr("class", "x axis")
				.attr("transform", "translate(0," + height+ ")")
				.call(xAxis);
			svg1.append('text')
				.attr('class','label')
				.attr("transform", "translate("+width/2+",0)")
				.style("text-anchor", "middle")
				.text('Summary of Post Events');
			var y1 = d3.scale.linear()
				.range([height, 0])
				.domain([0,0]);
			var yAxis1 = d3.svg.axis()
				.scale(y1)
				.orient("right");
			svg1.append("g")				
				.attr("class", "y axis")
				.call(yAxis1);
			svg1.append("g")				
				.attr("class", "y axis")	
				.attr("transform", "translate(" + width + " ,0)")	
				.style("fill", "red")		
				.call(yAxis);
			svg1.append("text")
				.attr("class", "label")
				.attr("transform", "rotate(-90) translate(-90," + (width+margin.right) + " )")
				//.attr('transform', 'translate(0,0)')
				.attr("y", 6)
				.attr("dy", ".71em")
				.style("text-anchor", "middle")
				.text('# no.of patients');
			var project_stackedbar = svg1.selectAll(".project_stackedbar")
										.data(data1)
										.enter().append("g")
										.attr("class", "g")
										.attr("transform", function(d) { return "translate(" + x0(d.State) + ",0)"; });
										project_stackedbar.selectAll("rect")
										.data(function(d) {  return d.columnDetails; })
										.enter().append("rect")
										.attr("width", x1.rangeBand())
										.attr("x", function(d) {	
										return x1(d.column);
										})
										.attr("y", function(d) { 
										return y(d.yEnd); 
										})
										.attr("height", function(d) { 
										return y(d.yBegin) - y(d.yEnd); 
										})
										.style("fill", function(d) { return color(d.name); })  
										.on("click",function(d){
										var ts= d3.select('.main1');
										sub(d.cd,d.inc,d.label);
										});
			project_stackedbar.selectAll("text")
								.data(function (d) { return d.columnDetails; })
								.enter()
								.append("text")
								.attr("class","label")
								.attr("y",  function (d) { return height+margin.left;})
								.attr("transform",  function (d,i) {if(d.yBegin == 0){return 'rotate(90) translate('+(height+margin.top)+',-'+(x1.rangeBand()+x1(d.column)+height+margin.top+10)+')';}})
								.attr("dy", ".71em")
								.text(function (d) {if(d.yBegin == 0){return d.label;}} );
			function sub(data1,inc,label){
				var width= 200;
				d3.selectAll(".main1").remove();
				var x0 = d3.scale.ordinal()
							.rangeRoundBands([0, width], 0.1);
				var innerColumns = inc[0];
				var x1 = d3.scale.ordinal();
				var y = d3.scale.linear()
							.range([height, 0]);
				var xAxis = d3.svg.axis()
								.scale(x0)
								.orient("bottom");
				var yAxis = d3.svg.axis()
								.scale(y)
								.orient("left")
								.tickFormat(d3.format(".2s"));
				var color = d3.scale.ordinal()
								.range(["red","green","black"]);
				var svg1= d3.select("body").select("#main").append("div").attr("class","main1").append("svg")
							.attr("width",960)
							.attr("height", height + margin.top + margin.bottom+margin.left);
				var svg=svg1.append("g")
							.attr("transform", "translate(" +(margin.left+margin.bottom) + "," + margin.top + ")");
				var yBegin;
				var columnHeaders = d3.keys(data1[0]).filter(function(key) { return key !== "State"; });
				color.domain(d3.keys(data1[0]).filter(function(key) { return key !== "State"; }));
				data1.forEach(function(d) {
					var yColumn = new Array();
					d.columnDetails = columnHeaders.map(function(name) {
							for (ic in innerColumns) {
								if($.inArray(name, innerColumns[ic]) >= 0){
									if (!yColumn[ic]){
										yColumn[ic] = 0;
									}
									yBegin = yColumn[ic];
									yColumn[ic] += +d[name];
									return {name: name, column: ic, yBegin: yBegin, yEnd: +d[name] + yBegin};
								}
							}
						});
						d.total = d3.max(d.columnDetails, function(d) { 
						return d.yEnd; 
					});
				});
				x0.domain(data1.map(function(d) { return d.State; }));
				x1.domain(d3.keys(innerColumns)).rangeRoundBands([0, x0.rangeBand()]);
				y.domain([0, d3.max(data1, function(d) { 
					return d.total; 
				})]);
				svg.append("g")
					.attr("class", "x axis")
					.attr("transform", "translate(0," + height + ")")
					.call(xAxis);
				svg.append('text')
						.attr('class','label')
						.attr("transform", "translate("+width/2+"," + (height+margin.left)+ ")")
						.style("text-anchor", "middle")
						.text(label);
				svg.append("g")
					.attr("class", "y axis")
					.call(yAxis)
					.append("text")
					.attr("transform", "rotate(-90)")
					.attr("y", 6)
					.attr("dy", "-3.7em")
					.style("text-anchor", "end")
					.text("#no.of population");
				var project_stackedbar = svg.selectAll(".project_stackedbar")
											.data(data1)
											.enter().append("g")
											.attr("class", "g")
											.attr("transform", function(d) { return "translate(" + x0(d.State) + ",0)"; });
				project_stackedbar.selectAll("rect")
									.data(function(d) { return d.columnDetails; })
									.enter().append("rect")
									.attr("width", x1.rangeBand())
									.attr("x", function(d) { 
									return x1(d.column);
									})
									.attr("y", function(d) { 
									return y(d.yEnd); 
									})
									.attr("height", function(d) { 
									return y(d.yBegin) - y(d.yEnd); 
									})
									.style("fill", function(d) { return color(d.name); });
									data1.forEach(function(d) {
									delete d.columnDetails;
									delete d.total;
									});
			}
		});
		//end of link 
		}
	}
}]);