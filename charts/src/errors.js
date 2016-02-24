dw.errors = {};

dw.errors.Exception = function(msg) {
    var _msg = msg || "Unexpected internal error";

    this.message = _msg;

    this.toString = function(){
        return _msg;
    };
};

dw.errors.InvalidStateException = function() {
    dw.errors.Exception.apply(this, arguments);
};
