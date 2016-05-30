
import React from "react";
import StompClient from "../services/connection-service";
import _ from "lodash";

class MyUnfilledOrders extends React.Component {

    constructor(props) {
        super(props);
        this.state = {  openOrders: [] };
        this.subscription = null;
    }

    getOpenOrders(instrument) {

        console.log("Getting unfilled orders for "+instrument);
        var ordersRequest = new Request(this.props.order_search_url+"?instrument="+instrument, {
            method: 'GET',
            credentials: 'same-origin',
            headers: new Headers({
                'Content-Type': 'application/json'
            })
        })


        fetch(ordersRequest).then((response) => {
            console.log("got open orders");
            return response.json();
        }).then((openOrders) => {
            console.log("Got open orders data "+openOrders);
            this.setState({ openOrders });
        }),
        (error) => {
            console.log('failed to get open orders');
        };
    }

    receiveOrderUpdate(order){
        // Order status has changed.
        // Go through the openOrders and find a match
        // If match and new state is not OPEN or PARTIAL then remove from list
        // If no match, and new stat is OPEN or PARTIAL then add to the list
    }

    subscribeInstrument(instrument) {
        console.log("Subscribing to order updates for "+instrument);
        //StompClient.then((client) => {
        //    this.subscription = client.subscribe(`/topic/snapshot/${instrument}`, (data) => {
        //        this.receiveSnapshot(JSON.parse(data.body));
        //    });
        //});
    }

    unsubscribeCurrent() {
        if (this.subscription != null) {
            this.subscription.unsubscribe();
            this.subscription = null;
            console.log("Unsubscribed")
        }
    }

    componentWillMount() {
        this.getOpenOrders(this.props.instrument);
        this.subscribeInstrument(this.props.instrument);
    }

    componentWillUnmount() {
        this.unsubscribeCurrent();
    }

    componentWillReceiveProps(nextProps) {
        console.log("getting open order props. Current Instrument: "+this.props.instrument+" New Instrument:"+nextProps.instrument);
        this.unsubscribeCurrent();
        this.getOpenOrders(nextProps.instrument);
        this.subscribeInstrument(nextProps.instrument);
    }

    render() {
        console.log("Rendering open orders");
        return (
            <table className="table table-bordered">
                <thead><tr><th>Id</th><th>Price</th><th>Side</th><th>Unfilled</th><th> </th></tr></thead>
                <tbody>

                {this.state.openOrders.map((order, index) => {
                    return (
                        <tr key={index}><td>{order.orderId}</td><td>{order.price}</td><td>{order.side}</td><td>{order.unfilled}</td><td> </td></tr>
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

export default MyUnfilledOrders;