/**
#### Version %VERSION%

The entire dw.js library is scoped under the **dw** name space. It does not introduce anything else
into the global name space.

#### Function Chaining
Most dw functions are designed to allow function chaining, meaning they return the current chart
instance whenever it is appropriate. This way chart configuration can be written in the following
style:
```js
chart.width(300)
    .height(300)
    .filter("sunday")
```
The getter forms of functions do not participate in function chaining because they necessarily
return values that are not the chart.  (Although some, such as `.svg` and `.xAxis`, return values
that are chainable d3 objects.)

**/
var dw = {
    version: "%VERSION%",
    constants: {
        CHART_CLASS: "dw-chart",
        DEBUG_GROUP_CLASS: "debug",
        STACK_CLASS: "stack",
        DESELECTED_CLASS: "deselected",
        SELECTED_CLASS: "selected",
        NODE_INDEX_NAME: "__index__",
        GROUP_INDEX_NAME: "__group_index__",
        DEFAULT_CHART_GROUP: "__default_chart_group__",
        EVENT_DELAY: 40,
        NEGLIGIBLE_NUMBER: 1e-10
    },
    _renderlet: null
};

dw.chartRegistry = function() {
    // chartGroup:string => charts:array
    var _chartMap = {};

    function initializeChartGroup(group) {
        if (!group)
            group = dw.constants.DEFAULT_CHART_GROUP;

        if (!_chartMap[group])
            _chartMap[group] = [];

        return group;
    }

    return {
        has: function(chart) {
            for (var e in _chartMap) {
                if (_chartMap[e].indexOf(chart) >= 0)
                    return true;
            }
            return false;
        },

        register: function(chart, group) {
            group = initializeChartGroup(group);
            _chartMap[group].push(chart);
        },

        deregister: function (chart, group) {
            group = initializeChartGroup(group);
            for (var i = 0; i < _chartMap[group].length; i++) {
                if (_chartMap[group][i].anchorName() === chart.anchorName()) {
                    _chartMap[group].splice(i, 1);
                    break;
                }
            }
        },

        clear: function(group) {
            if (group) {
                delete _chartMap[group];
            } else {
                _chartMap = {};
            }
        },

        list: function(group) {
            group = initializeChartGroup(group);
            return _chartMap[group];
        }
    };
}();

dw.registerChart = function(chart, group) {
    dw.chartRegistry.register(chart, group);
};

dw.deregisterChart = function (chart, group) {
    dw.chartRegistry.deregister(chart, group);
};

dw.hasChart = function(chart) {
    return dw.chartRegistry.has(chart);
};

dw.deregisterAllCharts = function(group) {
    dw.chartRegistry.clear(group);
};

/**
## Utilities
**/

/**
#### dw.filterAll([chartGroup])
Clear all filters on all charts within the given chart group. If the chart group is not given then
only charts that belong to the default chart group will be reset.
**/
dw.filterAll = function(group) {
    var charts = dw.chartRegistry.list(group);
    for (var i = 0; i < charts.length; ++i) {
        charts[i].filterAll();
    }
};

/**
#### dw.refocusAll([chartGroup])
Reset zoom level / focus on all charts that belong to the given chart group. If the chart group is
not given then only charts that belong to the default chart group will be reset.
**/
dw.refocusAll = function(group) {
    var charts = dw.chartRegistry.list(group);
    for (var i = 0; i < charts.length; ++i) {
        if (charts[i].focus) charts[i].focus();
    }
};

/**
#### dw.renderAll([chartGroup])
Re-render all charts belong to the given chart group. If the chart group is not given then only
charts that belong to the default chart group will be re-rendered.
**/
dw.renderAll = function(group) {
    var charts = dw.chartRegistry.list(group);
    for (var i = 0; i < charts.length; ++i) {
        charts[i].render();
    }

    if(dw._renderlet !== null)
        dw._renderlet(group);
};

/**
#### dw.redrawAll([chartGroup])
Redraw all charts belong to the given chart group. If the chart group is not given then only charts
that belong to the default chart group will be re-drawn. Redraw is different from re-render since
when redrawing dw tries to update the graphic incrementally, using transitions, instead of starting
from scratch.
**/
dw.redrawAll = function(group) {
    var charts = dw.chartRegistry.list(group);
    for (var i = 0; i < charts.length; ++i) {
        charts[i].redraw();
    }

    if(dw._renderlet !== null)
        dw._renderlet(group);
};

/**
#### dw.disableTransitions
If this boolean is set truthy, all transitions will be disabled, and changes to the charts will happen
immediately.  Default: false
**/
dw.disableTransitions = false;

dw.transition = function(selections, duration, callback) {
    if (duration <= 0 || duration === undefined || dw.disableTransitions)
        return selections;

    var s = selections
        .transition()
        .duration(duration);

    if (typeof(callback) === 'function') {
        callback(s);
    }

    return s;
};

dw.units = {};

/**
#### dw.units.integers
`dw.units.integers` is the default value for `xUnits` for the [Coordinate Grid
Chart](#coordinate-grid-chart) and should be used when the x values are a sequence of integers.

It is a function that counts the number of integers in the range supplied in its start and end parameters.

```js
chart.xUnits(dw.units.integers) // already the default
```

**/
dw.units.integers = function(s, e) {
    return Math.abs(e - s);
};

/**
#### dw.units.ordinal
This argument can be passed to the `xUnits` function of the to specify ordinal units for the x
axis. Usually this parameter is used in combination with passing `d3.scale.ordinal()` to `.x`.

It just returns the domain passed to it, which for ordinal charts is an array of all values.

```js
chart.xUnits(dw.units.ordinal)
    .x(d3.scale.ordinal())
```

**/
dw.units.ordinal = function(s, e, domain){
    return domain;
};

/**
#### dw.units.fp.precision(precision)
This function generates an argument for the [Coordinate Grid Chart's](#coordinate-grid-chart)
`xUnits` function specifying that the x values are floating-point numbers with the given
precision.

The returned function determines how many values at the given precision will fit into the range
supplied in its start and end parameters.

```js
// specify values (and ticks) every 0.1 units
chart.xUnits(dw.units.fp.precision(0.1)
// there are 500 units between 0.5 and 1 if the precision is 0.001
var thousandths = dw.units.fp.precision(0.001);
thousandths(0.5, 1.0) // returns 500
```
**/
dw.units.fp = {};
dw.units.fp.precision = function(precision){
    var _f = function(s, e){
        var d = Math.abs((e-s)/_f.resolution);
        if(dw.utils.isNegligible(d - Math.floor(d)))
            return Math.floor(d);
        else
            return Math.ceil(d);
    };
    _f.resolution = precision;
    return _f;
};

dw.round = {};
dw.round.floor = function(n) {
    return Math.floor(n);
};
dw.round.ceil = function(n) {
    return Math.ceil(n);
};
dw.round.round = function(n) {
    return Math.round(n);
};

dw.override = function(obj, functionName, newFunction) {
    var existingFunction = obj[functionName];
    obj["_" + functionName] = existingFunction;
    obj[functionName] = newFunction;
};

dw.renderlet = function(_){
    if(!arguments.length) return dw._renderlet;
    dw._renderlet = _;
    return dw;
};

dw.instanceOfChart = function (o) {
    return o instanceof Object && o.__dw_flag__ && true;
};
