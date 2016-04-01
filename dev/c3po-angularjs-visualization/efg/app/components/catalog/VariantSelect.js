/**
 * Created by w.ding on 12/04/15.
 */
var React = require('react');
var ProductActionCreator = require('../../actions/ProductActionCreators');
var ReactPropTypes = React.PropTypes;


var VariantSelect = React.createClass({

    propTypes: {
        variants: React.PropTypes.array.isRequired,
        index: React.PropTypes.number.isRequired
    },

    render: function() {
        var options = this.props.variants.map(function(variant, index) {
            return <option key={index} value={index}>{variant.type} ${variant.price.toFixed(2)}</option>;
        });
        return (
            <select onChange={this._setProductVariant}>
                {options}
            </select>
        );
    },

    _setProductVariant: function(e) {
        var index = this.props.index;
        var variantIndex = Number(e.target.value);
        ProductActionCreator.setProductVariant({index, variantIndex});
    }

});

module.exports = VariantSelect;
