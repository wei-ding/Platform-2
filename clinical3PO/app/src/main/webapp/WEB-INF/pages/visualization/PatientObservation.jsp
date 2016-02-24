<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<style>
body {
	font-family: sans-serif;
}

.axis path,line {
	stroke: black;
}

.timeseries path {
	stroke-width: 3px;
}

.timeseries circle {
	stroke: white;
}

.timeseries text {
	fill: white;
	stroke: none;
	font-size: 12px;
	font-weight: bold;
}

.line {
	float: left;
}

.line_container {
	width: 150px;
	height: 20px;
}

path {
	fill: none;
}

.key {
	float: right;
}

.key_line {
	font-size: 17px;
	width: 60%;
	float: left;
}

.key_square {
    float: left;
    height: 10px;
    margin-left: 75px;
    margin-right: -100px;
    margin-top: 6px;
    outline: 1px solid black;
    width: 15px;
}

.legend {
	float: right;
}

.legend_line {
	font-size: 22px;
	width: 500px;
	color: blue;
}
/* 
#timeseries {
	position: fixed;
	top: 15%;
	left: 15%;
}

#key {
	float: left;
	position: fixed;
	top: 17%;
	left: 7%;
	width: 10em;
	height: 4em;
}

#legend {
	position: absolute;
	top: 10%;
	left: 75%;
	width: 30em;
	height: 4em;
	margin-top: -5em;
	margin-left: -25em;
	border: 0px solid #ccc;
	background-color: #ffffff;
} */
</style>

 <script>
    
    var time_scale;
    var percent_scale, percent_scale_local;
	var zz=-1;
		 var container_dimensions = {width: 900, height: 400},
            margins = {top: 10, right: 20, bottom: 40, left: 60},
            chart_dimensions = {
                width: container_dimensions.width - margins.left - margins.right,
                height: container_dimensions.height - margins.top - margins.bottom
            };    
		
	function get_timeseries_data(d,i){

		key_id = this.parentNode.id;
		index=key_id.split("_")[1];
		
		draw_timeseries1(d,key_id,index);
    }
	
	function draw_timeseries1(data,id,index,notClick) {
		
	// get the id of the current element
        
		var id = data.patient_number;
		var j=id.split("_")[1]-1;
		
		if(typeof(data.death)!="undefined"){
			zz=d.death;
		}	

        // see if we have an associated time series
        var ts = d3.select('#'+id);
		
		if (patients[j].time_series.length==0 && notClick == undefined){
			alert("Patient data not found");
		}
		else{
			// toggle
			if (ts.empty()){
					draw_timeseries(patients[j],id,zz,index,data.color);  
			} else {
				ts.remove();
			}
		}
	}
    
    function add_label(circle, d, patient_number){
        
		
		d3.select(circle)
            .transition()
            .attr('r', 9);
        
        d3.select('#' + patient_number).append('text')
            .text(patient_number.split('_')[1])
            .attr('text-anchor','middle')
            .style("dominant-baseline","central")
            .attr('x', time_scale(d.time))
            .attr('y', percent_scale_local(d.value))
            .attr('class','linelabel')
            .style('opacity',0)
            .style('fill','white')
            .transition()
                .style('opacity',1);       
    }
    
    function draw_timeseries(data,id,zz,index,input_color){
        
        percent_scale_local = d3.scale.linear()
            .range([chart_dimensions.height, 0])
            .domain([0.5*min[index], 1.3*max[index]]);
			
        var line = d3.svg.line()
            .x(function(d){return time_scale(d.time);})
            .y(function(d){return percent_scale_local(d.value);})
            //.interpolate("linear")
			.interpolate("cardinal");
        if(zz==-1){
			color_id=id.split("_")[1];
			color_id=color_id%10;
		}
		else {
			color_id=zz;
		}		

        var g = d3.select('#chart_'+index)
            .append('g')
            .attr('id', id)
            .attr('class', 'timeseries Line_1' + color_id )  // color or line (stroke) + circle
			.style("background-color","#"+input_color)
			.style("fill","#"+input_color)
			.style("stroke","#"+input_color);
			

        
        g.append('path')
            .attr('d', line(data.time_series));
        
        g.selectAll('circle')
            .data(data.time_series)
            .enter()
            .append("circle")
            .attr('cx', function(d) {return time_scale(d.time);})
            .attr('cy', function(d) {return percent_scale_local(d.value);})
            .attr('r',0);
        
		var check_data_ts = data.time_series;
        var enter_duration = 1000;
        
        g.selectAll('circle')
            .transition()
            .delay(function(d, i) { return i / data.time_series.length * enter_duration; })
            .attr('r', 5)
            .each('end',function(d,i){
                if (i === data.time_series.length-1){
                    add_label(this,d,data.patient_number);
                }
            });
            
        
        g.selectAll('circle')
            .on('mouseover', function(d){
                d3.select(this)
                    .transition().attr('r', 9);
            })
            .on('mouseout', function(d,i){
                if (i !== data.length-1) {
                    d3.select(this).transition().attr('r', 5);
                }
            });
        
        g.selectAll('circle')
            .on('mouseover.tooltip', function(d){
        if(zz==-1){
			color_id=id.split("_")[1];
			color_id=color_id%10;
		}
		else {
			color_id=zz;
		}
        percent_scale_mouse = d3.scale.linear()
            .range([chart_dimensions.height, 0])
            .domain([0.5*min[index], 1.3*max[index]]);		

                d3.select("text.Line_1" + color_id).remove();     //mouse over color
                d3.select('#chart_'+index)
                    .append('text')
                    .text(d.value)
                    .attr('x', time_scale(d.time) + 10)
                    .attr('y', percent_scale_mouse(d.value) - 10)
                    .attr('class', 'Line_1'+color_id)             //mouse over color
					.style("background-color","#"+input_color)
					.style("fill","#"+input_color)
					.style("stroke","#"+input_color);					
				
            })
            .on('mouseout.tooltip', function(d){
                d3.select("text.Line_1" + color_id)            //mouse over color
                    .transition()
                    .duration(500)
                    .style('opacity',0)
                    .attr('transform','translate(10, -10)')
                    .remove();
            });
    }
    
   
    function makelabel(data,index)
	{
	//draw legend
		"use strict";

		d3.select('#legend_'+index)
		    .append('div')
			.attr('class','legend_line')
			.text(data);
	}
	
    function draw(data,index) {
        "use strict";
        
        var key_items = d3.select('#key_'+index)
          .selectAll('div')
          .data(data)
          .enter()
          .append('div')
            .attr('class','key_line')
            .attr('id',function(d){return d.patient_number+"_key";});
        
		
        key_items.append('div')
            .attr('id', function(d){return 'key_square_' + d.patient_number;})
            .attr('class', function(d){
            var color_id;
			if(d.death){
				color_id=d.death; 
			}
			else{
			var color_id=d.patient_number.split("_")[1];color_id=color_id%10;
			}
			return 'key_square Patient_1' + color_id;
			
			}) 
			.style("background-color",function(d){return "#"+d.color;})
			.style("fill",function(d){return "#"+d.color;})
			.style("stroke",function(d){return "#"+d.color;});
        
        key_items.append('div')
            .attr('class','key_label')
            .text(function(d){return d.patient_id;});
        
        d3.selectAll('.key_line')
            .on('click', get_timeseries_data);
       
    }
	function create_divs(data,index){
		"use strict";
		
        var attributes_items = d3.select('#main')
          .selectAll('div')
          .data(data)
          .enter()
          .append('div')
            .attr('class','main_attributes')
            .attr('id',function(d,i){return 'container_'+i;});

		attributes_items.append('div')
		    .attr('id', function(d,i){return 'timeseries_'+i;});
		attributes_items.append('div')
		    .attr('id', function(d,i){return 'key_'+i;});
		attributes_items.append('div')
		    .attr('id', function(d,i){return 'legend_'+i;});
	

		var timeseries_shift=100+(index*500);
		d3.select("#timeseries_"+index)
			.style("left","150px")
			.style("position","absolute")
			.style("top",timeseries_shift+"px");

		var key_shift=timeseries_shift+4;
		d3.select("#key_"+index)
			.style("left","50px")
			.style("position","absolute")
			.style("top",key_shift+"px")
			.style("float","left")
            .style("width","7em")
            .style("overflow-y","auto")
            .style("height","450px");


		var legend_shift=timeseries_shift-30;
			
		d3.select("#legend_"+index)
			.style("position","absolute")
			.style("top","15px")
			.style("left","120px")
			.style("width","10em")
			.style("height","4em")
			.style("margin-top",legend_shift+"px")
			.style("margin-left","400px")
			.style("border","0px solid #ccc")
			.style("background-color","#ffffff");
			
			          	
        time_scale = d3.scale.linear()
            .range([0, chart_dimensions.width])
            .domain([0,48]);
            
        
        percent_scale = d3.scale.linear()
            .range([chart_dimensions.height, 0])
            .domain([0.5*min[index], 1.3*max[index]]);

        var time_axis = d3.svg.axis()
            .scale(time_scale);

        
        var count_axis = d3.svg.axis()
            .scale(percent_scale)
            .orient("left");
        
		var g = d3.select('#timeseries_'+index)
          .append('svg')
            .attr("width", container_dimensions.width)
            .attr("height", container_dimensions.height)
          .append("g")
            .attr("transform", "translate(" + margins.left + "," + margins.top + ")")
            .attr("id","chart_"+index);
        
        g.append("g")
          .attr("class", "x axis")
          .attr("transform", "translate(0," + chart_dimensions.height + ")")
          .call(time_axis);
         
        g.append("g")
          .attr("class", "y axis")
          .call(count_axis);
        
      // draw the y-axis label
        
        d3.select('.y.axis')
            .append('text')
            .text('value')
            .attr('transform', "rotate (-270, 0, 0)")
            .attr('x', 100)
            .attr('y', 50);
		
	//	draw x-axis label
	
		d3.select('.x.axis')
            .append('text')
            .text('Time')
            .attr('x', 320)
            .attr('y', 39);


	}
    </script>
    
<div id='main' />
<script>			
			$(document).ready(function(){
			     d3.xhr("<c:url value = '/Visualization/${type}/json/${id}'/>", function(error, json) {					
						if (error) {return console.warn(error);}
						data = JSON.parse(json.response);
							
						
						patient_data=data;
						
						attributes_all = new Array();
						filtered_data = new Array();
						patients = new Array();
						max=new Array();
						min=new Array();			
						
						for (var i=0; i<patient_data.length;i++){
							attributes_all[i] = patient_data[i].attribute;
							patients[i] = patient_data[i];	
						}
						var attributes=attributes_all.filter(function(itm,i,attributes_all){
							return i==attributes_all.indexOf(itm);
						});


						for (var i = 0; i<attributes.length; i++){
							filtered_data[i] = data.filter(function(d){return d.attribute==attributes[i];});
							min[i]=100000;
							max[i]=0;
							for (var j = 0; j<filtered_data[i].length;j++){
								min_temp=d3.min(filtered_data[i][j].time_series, function(d) { return +d.value;} );
								max_temp=d3.max(filtered_data[i][j].time_series, function(d) { return +d.value;} );
								if (min_temp<min[i]){
									min[i]=min_temp;
								}
								if (max_temp>max[i]){
									max[i]=max_temp;
								}
							}
						}
						
						for (var i=0;i<attributes.length; i++){
							create_divs(attributes,i);
						}
						for (var i=0; i<attributes.length; i++){       
							draw(filtered_data[i],i);
							makelabel(attributes[i],i);
						}
						
						for (var i=0; i<attributes.length; i++){       
							// Run across patients and show the timeseries graph
							for (var j=0;j<filtered_data[i].length;j++) {
								//get_timeseries_data(filtered_data[i][j]);
								
								parentKey = $("#"+filtered_data[i][j].patient_number+"_key")[0].parentNode.id;
								draw_timeseries1(filtered_data[i][j],parentKey,parentKey.split("_")[1],1)
								
							}
						}
						
					});
		     });
	
	     
</script>
