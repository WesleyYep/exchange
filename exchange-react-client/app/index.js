
import React from "react";
import dom from "react-dom";
import OrderForm from "./components/order-form";
import PublicTrades from "./components/public-trades";
import PrivateTrades from "./components/private-trades";
import Snapshot from "./components/snapshot";
import '../bootstrap/dist/css/bootstrap.css';

class Exchange extends React.Component {

    constructor() {
        super();
        this.instruments = [ "AMZN" ];
    }

    render() {
        return (
            <div>
                {
                    this.instruments.map(instrument => {
                        return (
                            <div className="container-fluid">
                                <div ><OrderForm  instrument={instrument}/></div>
                                <div className="row">
                                    <div className="col-md-1">&nbsp;</div>
                                    <div className="col-md-10"><Snapshot instrument={instrument}/></div>
                                    <div className="col-md-1">&nbsp;</div>
                                </div>
                                <div className="row">
                                    <div className="col-md-8"><PrivateTrades instrument={instrument}/></div>
                                    <div className="col-md-4"><PublicTrades instrument={instrument}/></div>
                                </div>
                            </div>

                        )
                    })
                }
            </div>
        )
    }


}

dom.render(<Exchange/>, document.getElementById("app"));
