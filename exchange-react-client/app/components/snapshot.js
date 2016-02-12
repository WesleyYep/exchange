
import React from "react";
import StompClient from "../services/connection-service";
import _ from "lodash";

class Snapshot extends React.Component {

    constructor(props) {
        super(props);
        this.state = {  buy: [], sell: [] };
    }

    componentWillMount() {
        StompClient.then((client) => {
            client.subscribe(`/topic/snapshot/${this.props.instrument}`, (data) => {
                var snapshot = JSON.parse(data.body);
                var buy = _.sortBy(snapshot[this.props.instrument].buy, "price");
                var sell = _.sortBy(snapshot[this.props.instrument].sell, "price");
                sell = _.reverse(sell);
                this.setState({buy, sell});
            });
        });
    }

    render() {
        return (
            <table>
                <thead><tr><td>Sell</td><td>Price</td><td>Buy</td></tr></thead>
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