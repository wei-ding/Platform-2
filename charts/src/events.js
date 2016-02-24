dw.events = {
    current: null
};

/**
#### dw.events.trigger(function[, delay])
This function triggers a throttled event function with a specified delay (in milli-seconds).  Events
that are triggered repetitively due to user interaction such brush dragging might flood the library
and invoke more renders than can be executed in time. Using this function to wrap your event
function allows the library to smooth out the rendering by throttling events and only responding to
the most recent event.

```js
    chart.renderlet(function(chart){
        // smooth the rendering through event throttling
        dw.events.trigger(function(){
            // focus some other chart to the range selected by user on this chart
            someOtherChart.focus(chart.filter());
        });
    })
```
**/
dw.events.trigger = function(closure, delay) {
    if (!delay){
        closure();
        return;
    }

    dw.events.current = closure;

    setTimeout(function() {
        if (closure == dw.events.current)
            closure();
    }, delay);
};
