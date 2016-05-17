
import React from "react";
import dom from "react-dom";
import OrderForm from "./order-form";
import PublicTrades from "./public-trades";
import PrivateTrades from "./private-trades";
import Snapshot from "./snapshot";
//import '../bootstrap/dist/css/bootstrap.css';

class Instrument extends React.Component {

    constructor() {
        super();
        console.log("Created exchange")
    }

    render() {
        return (
            <div className="container-fluid">
                <div ><OrderForm  instrument={this.props.instrument}/></div>
                <div className="row">
                    <div className="col-md-1">&nbsp;</div>
                    <div className="col-md-10"><Snapshot instrument={this.props.instrument}/></div>
                    <div className="col-md-1">&nbsp;</div>
                </div>
                <div className="row">
                    <div className="col-md-8"><PrivateTrades instrument={this.props.instrument}/></div>
                    <div className="col-md-4"><PublicTrades instrument={this.props.instrument}/></div>
                </div>
            </div>
        );
    }
}

export default Instrument
