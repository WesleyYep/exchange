
import React from "react";
import StompClient from "../services/connection-service";
import _ from "lodash";

class Snapshot extends React.Component {

    constructor(props) {
        super(props);
        this.state = {  buy: [], sell: [] };
        this.subscription = null;
    }

    receiveSnapshot(snapshot){
        var buy = _.sortBy(snapshot.buy, "price");
        var sell = _.sortBy(snapshot.sell, "price");
        this.setState({buy, sell});
    }

    subscribeInstrument(instrument) {
        console.log("Subscribing to "+instrument);
        StompClient.then((client) => {
            this.subscription = client.subscribe(`/topic/snapshot/${instrument}`, (data) => {
                this.receiveSnapshot(JSON.parse(data.body));
            });
        });
    }

    unsubscribeCurrent() {
        if (this.subscription != null) {
            this.subscription.unsubscribe();
            this.subscription = null;
            console.log("Unsubscribed")
        }
    }

    componentWillMount() {
        this.subscribeInstrument(this.props.instrument);
    }

    componentWillUnmount() {
        this.unsubscribeCurrent();
    }

    componentWillReceiveProps(nextProps) {
        console.log("getting props. Current Instrument: "+this.props.instrument+" New Instrument:"+nextProps.instrument);
        this.unsubscribeCurrent();
        this.subscribeInstrument(nextProps.instrument);
    }

    render() {
        return (
            <table className="table table-bordered">
                <thead><tr><th>Sell</th><th>Price</th><th>Buy</th></tr></thead>
                <tbody>

                {this.state.buy.map((order, index) => {
                  return (
                      <tr key={index}><td> </td><td>{order.price}</td><td>{order.quantity}</td></tr>
                  )
                })}
                {this.state.sell.map((order, index) => {
                    return (
                        <tr key={index}><td>{order.quantity}</td><td>{order.price}</td><td> </td></tr>
                    )
                })}
                </tbody>
            </table>
        )
    }
}


// Make sure that the instrument string is supplied
Snapshot.propTypes = { instrument : React.PropTypes.string.isRequired }

export default Snapshot;