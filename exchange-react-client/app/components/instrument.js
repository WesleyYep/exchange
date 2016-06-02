
import React from "react";
import dom from "react-dom";
import OrderForm from "./order-form";
import PublicTrades from "./public-trades";
import PrivateTrades from "./private-trades";
import Snapshot from "./snapshot";
import MyUnfilledOrders from "./my-unfilled-orders";
//import '../bootstrap/dist/css/bootstrap.css';

class Instrument extends React.Component {

    constructor() {
        super();
        console.log("Created exchange")
    }

    render() {
        return (
            <div className="container-fluid">
                <div ><OrderForm  instrument={this.props.instrument} order_submit_url={this.props.order_submit_url} /></div>
                <div className="row">
                    <div className="col-md-1">&nbsp;</div>
                    <div className="col-md-5"><Snapshot instrument={this.props.instrument} snapshot_url={this.props.snapshot_url} updates_url={this.props.snapshot_updates_url} /></div>
                    <div className="col-md-5"><MyUnfilledOrders instrument={this.props.instrument} order_search_url={this.props.order_search_url} updates_url={this.props.order_updates_url} /></div>
                    <div className="col-md-1">&nbsp;</div>
                </div>
                <div className="row">
                    <div className="col-md-8"><PrivateTrades instrument={this.props.instrument} updates_url={this.props.private_trade_updates_url} /></div>
                    <div className="col-md-4"><PublicTrades instrument={this.props.instrument} updates_url={this.props.public_trade_updates_url} /></div>
                </div>
            </div>
        );
    }
}

Instrument.propTypes = { instrument : React.PropTypes.string.isRequired }
Instrument.propTypes = { snapshot_url : React.PropTypes.string.isRequired }
Instrument.propTypes = { order_submit_url : React.PropTypes.string.isRequired }
Instrument.propTypes = { order_search_url : React.PropTypes.string.isRequired }

Instrument.propTypes = { snapshot_updates_url : React.PropTypes.string.isRequired }
Instrument.propTypes = { order_updates_url : React.PropTypes.string.isRequired }
Instrument.propTypes = { private_trade_updates_url : React.PropTypes.string.isRequired }
Instrument.propTypes = { public_trade_updates_url : React.PropTypes.string.isRequired }

export default Instrument
