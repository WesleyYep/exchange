
import React from "react";
import StompClient from "../services/connection-service";

class PublicTrades extends React.Component {

    constructor(props) {
        super(props);
        this.state = { trades: [] };
    }

    componentWillMount() {
        StompClient.then((client) => {
            client.subscribe(`/topic/public.trade/${this.props.instrument}`, (data) => {
                var trade = JSON.parse(data.body);
                var trades = this.state.trades;
                trades.push(trade);
                this.setState({trades});
            });
        });
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