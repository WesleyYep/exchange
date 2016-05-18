
import React from "react";

class OrderForm extends React.Component {

    constructor(props) {
        super(props);
        console.log("Created OrderForm for "+this.props.instrument)
    }

    submitForm(evt) {
        var payload = {};
        payload.price = this.refs.price.value;
        payload.quantity = this.refs.quantity.value;
        payload.side = this.refs.side.value;
        payload.instrument = this.props.instrument;

        console.log("Submitting "+payload.side+" order for "+this.props.instrument+" price="+payload.price+" qty="+payload.quantity );

        fetch('/orders', {
            method: 'POST',
            body: JSON.stringify(payload),
            credentials: 'same-origin',
            headers: new Headers({
                'Content-Type': 'application/json'
            })
        }).then((response) => {
            console.log("Order submitted");
        }, (error) => {
            console.log('save failed');
        });

        evt.preventDefault(); // stop the browser submitting the page
    }

    render() {
        return (
            <form className="form-inline" onSubmit={(evt) => this.submitForm(evt)}>
                <div>Instrument {this.props.instrument}</div>
                <div className="form-group">
                    <label forName="price" >Price</label>
                    <input className="form-control" type="text" ref="price" id="price"/>
                </div>
                <div className="form-group">
                    <label forName="quantity" >Quantity</label>
                    <input className="form-control" type="text" ref="quantity" id="quantity"/>
                </div>
                <div className="form-group">
                    <label forName="side" >Side</label>
                    <select ref="side" id="side">
                        <option value="BUY">Buy</option>
                        <option value="SELL">Sell</option>
                    </select>
                </div>

                <button className="btn btn-default" type="submit" >Submit</button>

            </form>
        )
    }
}

// Make sure that the instrument string is supplied
OrderForm.propTypes = { instrument : React.PropTypes.string.isRequired }

export default OrderForm;