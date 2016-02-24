
import React from "react";
import dom from "react-dom";
import OrderForm from "./components/order-form";
import PublicTrades from "./components/public-trades";
import PrivateTrades from "./components/private-trades";
import Snapshot from "./components/snapshot";

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
                                <div ><Snapshot instrument={instrument}/></div>
                                <div className="col-md-8"><PublicTrades instrument={instrument}/></div>
                                <div className="col-md-4"><PrivateTrades instrument={instrument}/></div>
                            </div>

                        )
                    })
                }
            </div>
        )
    }


}

dom.render(<Exchange/>, document.getElementById("app"));
