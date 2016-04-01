/**
 * Created by w.ding on 12/08/15.
 */
'use strict';

var AppDispatcher = require('../dispatcher/AppDispatcher.js');
var AppConstants = require('../constants/AppConstants.js');
var WebAPIUtils = require('../utils/WebAPIUtils.js');
var ActionTypes = AppConstants.ActionTypes;

module.exports = {

    setProductVariant: function(variant) {
        console.log('ProductActionCreator setProductVariant', variant);
        AppDispatcher.dispatch({
            type: ActionTypes.SET_PRODUCT_VARIANT,
            index: variant.index,
            variantIndex: variant.variantIndex
        });
    },
    /*
    removeOneFromInventory: function(product) {
        AppDispatcher.dispatch({
            type: ActionTypes.REMOVE_ONE_FROM_INVENTORY,
            product: product
        });
    },

    setInventory: function (productIndex, initialInventory, qty) {
        AppDispatcher.dispatch({
            type: ActionTypes.SET_PRODUCT_INVENTORY,
            productIndex: productIndex,
            initialInventory: initialInventory,
            qty: qty
        });
    },
    */

    loadCatalog: function() {
        AppDispatcher.dispatch({
            type: ActionTypes.LOAD_CATALOG
        });
        WebAPIUtils.loadCatalog();
    },

    selectCatalog: function(rid) {
        AppDispatcher.dispatch({
            type: ActionTypes.SELECT_CATALOG,
            rid: rid
        });
        WebAPIUtils.loadProducts(rid);
    }

};
