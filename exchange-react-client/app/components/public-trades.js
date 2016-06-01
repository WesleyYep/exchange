
import React from "react";
import StompClient from "../services/connection-service";

class PublicTrades extends React.Component {

    constructor(props) {
        super(props);
        this.state = { trades: [] };
    }

    receivePublicTrade(trade){
        var trades = this.state.trades;
        trades.push(trade);
        this.setState({trades});
    }

    subscribeTradesForInstrument(instrument) {
        console.log("Subscribing to public trades for "+instrument);
        StompClient.then((client) => {
            this.subscription = client.subscribe(`/topic/public.trade/${this.props.instrument}`, (data) => {
                this.receivePublicTrade(JSON.parse(data.body));
            });
        });
    }

    unsubscribeCurrent() {
        if (this.subscription != null) {
            this.subscription.unsubscribe();
            this.subscription = null;
            console.log("Unsubscribed from public trades")
        }
    }

    componentWillUnmount() {
        this.unsubscribeCurrent();
    }

    componentWillReceiveProps(nextProps) {
        console.log("getting public trade props. Current Instrument: "+this.props.instrument+" New Instrument:"+nextProps.instrument);
        this.unsubscribeCurrent();
        this.subscribeTradesForInstrument(nextProps.instrument);
    }

    render() {
        return (
            <table className="table table-bordered">
                <thead><tr><th>Price</th><th>Quantity</th></tr></thead>
                <tbody>
                {this.state.trades.map((trade, index) => {
                  return (
                      <tr key={index}><td>{trade.price}</td><td>{trade.quantity}</td></tr>
                  )
                })}
                </tbody>
            </table>
        )
    }
}

// Make sure that the instrument string is supplied
PublicTrades.propTypes = { instrument : React.PropTypes.string.isRequired }


export default PublicTrades;