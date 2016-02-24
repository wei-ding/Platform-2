// Renamed functions

dw.abstractBubbleChart = dw.bubbleMixin;
dw.baseChart = dw.baseMixin;
dw.capped = dw.capMixin;
dw.colorChart = dw.colorMixin;
dw.coordinateGridwhart = dw.coordinateGridMixin;
dw.marginable = dw.marginMixin;
dw.stackableChart = dw.stackMixin;

return dw;}
if(typeof define === "function" && define.amd) {
  define(["d3"], _dw);
} else if(typeof module === "object" && module.exports) {
  module.exports = _dw(d3);
} else {
  this.dw = _dw(d3);
}
}
)();
