
import React from "react";
import stompConnectTo from "../services/connection-service";

class PrivateTrades extends React.Component {

    constructor(props) {
        super(props);
        this.state = { trades: [] };
    }

    receivePrivateTrade(trade){
        var trades = this.state.trades;
        trades.push(trade);
        this.setState({trades});
    }

    subscribeTradesForInstrument(instrument) {
        console.log("Subscribing to private trades for "+instrument);
        stompConnectTo(this.props.updates_url).then((client) => {
            this.subscription = client.subscribe(`/user/queue/private.trade/${this.props.instrument}`, (data) => {
                this.receivePrivateTrade(JSON.parse(data.body));
            });
        });
    }

    unsubscribeCurrent() {
        if (this.subscription != null) {
            this.subscription.unsubscribe();
            this.subscription = null;
            console.log("Unsubscribed from private trades")
        }
    }

    componentWillUnmount() {
        this.unsubscribeCurrent();
    }

    componentWillReceiveProps(nextProps) {
        console.log("getting private trade props. Current Instrument: "+this.props.instrument+" New Instrument:"+nextProps.instrument);
        this.unsubscribeCurrent();
        this.subscribeTradesForInstrument(nextProps.instrument);
    }

    render() {
        return (
            <table className="table table-bordered">
                <thead><tr><th>Price</th><th>Quantity</th><th>Trade Id</th><th>Order Id</th></tr></thead>
                <tbody>
                {this.state.trades.map((trade, index) => {
                  return (
                      <tr key={index}><td>{trade.price}</td><td>{trade.quantity}</td><td>{trade.tradeId}</td><td>{trade.orderId}</td></tr>
                  )
                })}
                </tbody>
            </table>
        )
    }
}

// Make sure that the instrument string is supplied
PrivateTrades.propTypes = { instrument : React.PropTypes.string.isRequired }
PrivateTrades.propTypes = { updates_url : React.PropTypes.string.isRequired }

export default PrivateTrades;