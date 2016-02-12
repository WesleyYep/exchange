
import React from "react";
import StompClient from "../services/connection-service";

class PrivateTrades extends React.Component {

    constructor(props) {
        super(props);
        this.state = { trades: [] };
    }

    componentWillMount() {
        StompClient.then((client) => {
            client.subscribe(`/user/queue/private.trade/${this.props.instrument}`, (data) => {
                var trade = JSON.parse(data.body);
                var trades = this.state.trades;
                trades.push(trade);
                this.setState({trades});
            });
        });
    }

    render() {
        return (
            <table>
                <thead><tr><td>Price</td><td>Quantity</td><td>Trade Id</td><td>Order Id</td></tr></thead>
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

export default PrivateTrades;