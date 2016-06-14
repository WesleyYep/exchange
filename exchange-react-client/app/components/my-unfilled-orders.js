
import React from "react";
import stompConnectTo from "../services/connection-service";
import _ from "lodash";
import $ from "jquery";

class MyUnfilledOrders extends React.Component {

    constructor(props) {
        super(props);
        this.state = {  openOrders: [] };
        this.subscription = null;
    }

    getOpenOrders(instrument) {

        console.log("Getting unfilled orders for "+instrument);


        $.ajax({
            url: this.props.order_search_url+"?instrument="+instrument,
            crossDomain: true,
            xhrFields: {
                withCredentials: true
            },
        }).then((openOrders) => {
            console.log("Got open orders data "+openOrders);
            this.setState({ openOrders });
        }),
        (error) => {
            console.log('failed to get open orders');
        };

        //var ordersRequest = new Request(this.props.order_search_url+"?instrument="+instrument, {
        //    method: 'GET',
        //    credentials: 'same-origin',
        //    headers: new Headers({
        //        'Content-Type': 'application/json'
        //    })
        //})
        //fetch(ordersRequest).then((response) => {
        //    console.log("got open orders");
        //    return response.json();
        //}).then((openOrders) => {
        //    console.log("Got open orders data "+openOrders);
        //    this.setState({ openOrders });
        //}),
        //(error) => {
        //    console.log('failed to get open orders');
        //};
    }

    receiveOrderUpdate(update){
        // Order status has changed.
        // Go through the openOrders and find a match
        // If match, use the new one (instead of the old version)
        // If no match, just add the updated order to the list
        console.log("Received order update "+JSON.stringify(update));
        var updatedOrders = [];
        var matched = false;
        for(var i = 0; i < this.state.openOrders.length ; i++) {
            var order = this.state.openOrders[i];
            if (order.orderId == update.orderId) {
                matched = true;
                updatedOrders.push(update);
                console.log("> "+JSON.stringify(update));
            } else {
                updatedOrders.push(order);
                console.log("+ "+JSON.stringify(order));
            }
        }

        if (matched == false) {
            console.log("Add order");
            updatedOrders.push(update);
            console.log("+ "+JSON.stringify(update));
        }

        this.setState( { openOrders: updatedOrders });
    }

    subscribeForUpdates(instrument) {
        if (this.subscription == null) {
            console.log("Subscribing to order updates for " + instrument);
            stompConnectTo(this.props.updates_url).then((client) => {
                this.subscription = client.subscribe(`/user/queue/order.updates/${this.props.instrument}`, (data) => {
                    this.receiveOrderUpdate(JSON.parse(data.body));
                });
            });
        }
    }

    unsubscribeCurrent() {
        if (this.subscription != null) {
            this.subscription.unsubscribe();
            this.subscription = null;
            console.log(`Unsubscribed from /user/queue/order.updates/${this.props.instrument}`);
        }
    }

    componentWillUnmount() {
        this.unsubscribeCurrent();
    }

    componentWillReceiveProps(nextProps) {
        console.log("getting open order props. Current Instrument: "+this.props.instrument+" New Instrument:"+nextProps.instrument);
        this.unsubscribeCurrent();
        this.getOpenOrders(nextProps.instrument);
        this.subscribeForUpdates(nextProps.instrument);
    }

    render() {
        console.log("Rendering open orders");
        return (
            <table className="table table-bordered">
                <thead><tr><th>Id</th><th>Price</th><th>Side</th><th>Quantity</th><th>Unfilled</th><th>Status</th><th> </th></tr></thead>
                <tbody>

                {this.state.openOrders.map((order, index) => {
                    return (
                        <tr key={index}><td>{order.orderId}</td><td>{order.price}</td><td>{order.side}</td><td>{order.quantity}</td><td>{order.unfilled}</td><td>{order.state}</td><td> </td></tr>
                    )
                })}

                </tbody>
            </table>
        )
    }
}


// Make sure that the instrument string and order search URL is supplied
MyUnfilledOrders.propTypes = { instrument : React.PropTypes.string.isRequired }
MyUnfilledOrders.propTypes = { order_search_url : React.PropTypes.string.isRequired }
MyUnfilledOrders.propTypes = { updates_url : React.PropTypes.string.isRequired }

export default MyUnfilledOrders;