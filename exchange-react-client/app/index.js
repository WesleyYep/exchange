
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
                            <div>
                                <OrderForm  instrument={instrument}/>
                                <hr/>
                                <Snapshot instrument={instrument}/>
                                <hr/>
                                <PublicTrades instrument={instrument}/>
                                <hr/>
                                <PrivateTrades instrument={instrument}/>
                            </div>

                        )
                    })
                }
            </div>
        )
    }


}

dom.render(<Exchange/>, document.getElementById("app"));
