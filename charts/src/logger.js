dw.logger = {};

dw.logger.enableDebugLog = false;

dw.logger.warn = function (msg) {
    if (console) {
        if (console.warn) {
            console.warn(msg);
        } else if (console.log) {
            console.log(msg);
        }
    }

    return dw.logger;
};

dw.logger.debug = function (msg) {
    if (dw.logger.enableDebugLog && console) {
        if (console.debug) {
            console.debug(msg);
        } else if (console.log) {
            console.log(msg);
        }
    }

    return dw.logger;
};
