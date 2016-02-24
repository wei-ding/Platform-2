//# 
'use strict';

/* jshint globalstrict: true */
/* global d3,crossfilter,colorbrewer */

// ### Create Chart Objects
// Create chart objects assocated with the container elements identified by the css selector.
// Note: It is often a good idea to have these objects accessible at the global scope so that they can be modified or filtered by other page controls.
var genderChart = dw.pieChart("#gender-chart");
var ICUTypeChart = dw.pieChart("#ICUType-chart");

// ### Anchor Div for Charts
/*
// A div anchor that can be identified by id
    <div id="your-chart"></div>
// Title or anything you want to add above the chart
    <div id="chart"><span>Days by Gain or Loss</span></div>
// ##### .turnOnControls()
// If a link with css class "reset" is present then the chart
// will automatically turn it on/off based on whether there is filter
// set on this chart (slice selection for pie chart and brush
// selection for bar chart). Enable this with `chart.turnOnControls(true)`
     <div id="chart">
       <a class="reset" href="javascript:myChart.filterAll();dw.redrawAll();" style="display: none;">reset</a>
     </div>
// dw.js will also automatically inject applied current filter value into
// any html element with css class set to "filter"
    <div id="chart">
        <span class="reset" style="display: none;">Current filter: <span class="filter"></span></span>
    </div>
*/

//### Load your data
//Data can be loaded through regular means with your
//favorite javascript library
//
//```javascript
//```
d3.csv("data/ptsummary.csv", function (data) {
    /* since its a csv file we need to format the data a bit */
    var numberFormat = d3.format(".2f");

    //### Create Crossfilter Dimensions and Groups
    //See the [crossfilter API](https://github.com/square/crossfilter/wiki/API-Reference) for reference.
    var ptsummary= crossfilter(data);
    var all = ptsummary.groupAll();

    // dimension by full date
    var ptidDimension = ptsummary.dimension(function (d) {
        return d.pt_id;
    });

    // create gender dimension
    var maleOrFemale = ptsummary.dimension(function (d) {
        return d.gender == "1" ? "Male" : "Female";
    });
    // produce counts records in the dimension
    var maleOrFemaleGroup = maleOrFemale.group();


    // summerize volume by ICUType 
    // ICUType (1: Coronary Care Unit, 2: Cardiac Surgery Recovery Unit, 3: Medical ICU, or 4: Surgical ICU) 
    var icu = 0;
    var ICUType = ptsummary.dimension(function (d) {
        icu = d.icu_type;
        if ( icu == "1" )
            return "CCU";
        else if ( icu == "2" )
            return "CSRU";
        else if ( icu == "3" )
            return "Medical";
        else if ( icu == "4" )
            return "Surgical";
        else 
            return "Other";
    });
    var ICUTypeGroup = ICUType.group();




    // #### Pie/Donut Chart
    // Create a pie chart and use the given css selector as anchor. You can also specify
    // an optional chart group for this chart to be scoped within. When a chart belongs
    // to a specific group then any interaction with such chart will only trigger redraw
    // on other charts within the same chart group.

    genderChart
        .width(180) // (optional) define chart width, :default = 200
        .height(180) // (optional) define chart height, :default = 200
        .radius(80) // define pie radius
        .dimension(maleOrFemale) // set dimension
        .group(maleOrFemaleGroup) // set group
        /* (optional) by default pie chart will use group.key as its label
         * but you can overwrite it with a closure */
        // (optional) whether chart should render labels, :default = true
        .label(function (d) {
            if (genderChart.hasFilter() && !genderChart.hasFilter(d.key))
                return d.key + "(0%)";
            var label = d.key;
            if(all.value())
                label += "(" + Math.floor(d.value / all.value() * 100) + "%)";
            return label;
        });

    ICUTypeChart
        .width(180)
        .height(180)
        .radius(80)
        .innerRadius(30)
        .dimension(ICUType)
        .group(ICUTypeGroup);



    dw.dataCount(".dw-data-count")
        .dimension(ptsummary)
        .group(all)
        // (optional) html, for setting different html for some records and all records.
        // .html replaces everything in the anchor with the html given using the following function.
        // %filter-count and %total-count are replaced with the values obtained.
        .html({
            some:"<strong>%filter-count</strong> selected out of <strong>%total-count</strong> records | <a href='javascript:dw.filterAll(); dw.renderAll();''>Reset All</a>",
            all:"All records selected. Please click on the graph to apply filters."
        });
    
    //#### Data Table
    dw.dataTable(".dw-data-table")
        .dimension(ptidDimension)
        // data table does not use crossfilter group but rather a closure
        // as a grouping function
        .size(50) // (optional) max number of records to be shown, :default = 25
        // dynamic columns creation using an array of closures
        .columns([
            function (d) {
                return d.pt_id;
            }
        ]);


    //#### Rendering
    //simply call renderAll() to render all charts on the page
    dw.renderAll();
    /*
    // or you can render charts belong to a specific chart group
    dw.renderAll("group");
    // once rendered you can call redrawAll to update charts incrementally when data
    // change without re-rendering everything
    dw.redrawAll();
    // or you can choose to redraw only those charts associated with a specific chart group
    dw.redrawAll("group");
    */
});
//#### Version
//Determine the current version of dw with `dw.version`
d3.selectAll("#version").text(dw.version);
