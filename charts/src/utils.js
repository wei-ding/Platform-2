dw.dateFormat = d3.time.format("%m/%d/%Y");

dw.printers = {};

dw.printers.filters = function (filters) {
    var s = "";

    for (var i = 0; i < filters.length; ++i) {
        if (i > 0) s += ", ";
        s += dw.printers.filter(filters[i]);
    }

    return s;
};

dw.printers.filter = function (filter) {
    var s = "";

    if (filter) {
        if (filter instanceof Array) {
            if (filter.length >= 2)
                s = "[" + dw.utils.printSingleValue(filter[0]) + " -> " + dw.utils.printSingleValue(filter[1]) + "]";
            else if (filter.length >= 1)
                s = dw.utils.printSingleValue(filter[0]);
        } else {
            s = dw.utils.printSingleValue(filter);
        }
    }

    return s;
};

dw.pluck = function(n,f) {
    if (!f) return function(d) { return d[n]; };
    return function(d,i) { return f.call(d,d[n],i); };
};

dw.utils = {};

dw.utils.printSingleValue = function (filter) {
    var s = "" + filter;

    if (filter instanceof Date)
        s = dw.dateFormat(filter);
    else if (typeof(filter) == "string")
        s = filter;
    else if (dw.utils.isFloat(filter))
        s = dw.utils.printSingleValue.fformat(filter);
    else if (dw.utils.isInteger(filter))
        s = Math.round(filter);

    return s;
};
dw.utils.printSingleValue.fformat = d3.format(".2f");

// FIXME: these assume than any string r is a percentage (whether or not it
// includes %). They also generate strange results if l is a string.
dw.utils.add = function (l, r) {
    if (typeof r === "string")
        r = r.replace("%", "");

    if (l instanceof Date) {
        if (typeof r === "string") r = +r;
        var d = new Date();
        d.setTime(l.getTime());
        d.setDate(l.getDate() + r);
        return d;
    } else if (typeof r === "string") {
        var percentage = (+r / 100);
        return l > 0 ? l * (1 + percentage) : l * (1 - percentage);
    } else {
        return l + r;
    }
};

dw.utils.subtract = function (l, r) {
    if (typeof r === "string")
        r = r.replace("%", "");

    if (l instanceof Date) {
        if (typeof r === "string") r = +r;
        var d = new Date();
        d.setTime(l.getTime());
        d.setDate(l.getDate() - r);
        return d;
    } else if (typeof r === "string") {
        var percentage = (+r / 100);
        return l < 0 ? l * (1 + percentage) : l * (1 - percentage);
    } else {
        return l - r;
    }
};

dw.utils.isNumber = function(n) {
    return n===+n;
};

dw.utils.isFloat = function (n) {
    return n===+n && n!==(n|0);
};

dw.utils.isInteger = function (n) {
    return n===+n && n===(n|0);
};

dw.utils.isNegligible = function (n) {
    return !dw.utils.isNumber(n) || (n < dw.constants.NEGLIGIBLE_NUMBER && n > -dw.constants.NEGLIGIBLE_NUMBER);
};

dw.utils.clamp = function (val, min, max) {
    return val < min ? min : (val > max ? max : val);
};

var _idwounter = 0;
dw.utils.uniqueId = function () {
    return ++_idwounter;
};

dw.utils.nameToId = function (name) {
    return name.toLowerCase().replace(/[\s]/g, "_").replace(/[\.']/g, "");
};

dw.utils.appendOrSelect = function (parent, name) {
    var element = parent.select(name);
    if (element.empty()) element = parent.append(name);
    return element;
};

dw.utils.safeNumber = function(n){return dw.utils.isNumber(+n)?+n:0;};
