
import React from "react";

class OrderForm extends React.Component {

    constructor(props) {
        super(props);
    }

    submitForm(evt) {
        var payload = {};
        payload.price = this.refs.price.value;
        payload.quantity = this.refs.quantity.value;
        payload.side = this.refs.side.value;
        payload.instrument = this.props.instrument;

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

                <form onSubmit={(evt) => this.submitForm(evt)}>
                    <fieldset>
                        <legend>Order</legend>
                        Price <input type="text" ref="price" /><br/>
                        Quantity <input type="text" ref="quantity" /><br/>
                        Side <select ref="side">
                            <option value="BUY">Buy</option>
                            <option value="SELL">Sell</option>
                        </select> <br/>
                        <input type="submit" value="Submit" />
                    </fieldset>
                </form>
        )
    }
}

// Make sure that the instrument string is supplied
OrderForm.propTypes = { instrument : React.PropTypes.string.isRequired }

export default OrderForm;