
import React from "react";
import dom from "react-dom";
import Instrument from "./components/instrument";
import InstrumentSelector from "./components/instrument-selector";
import * as config from "./config/default.js"
//import '../bootstrap/dist/css/bootstrap.css';

class Exchange extends React.Component {

    constructor() {
        super();
        console.log("Created exchange")
    }



    render() {
        return (
            <InstrumentSelector  instrument_list_url={config.instrument_list_url}
                                 order_submit_url={config.order_submit_url}
                                 snapshot_url={config.snapshot_url}
                                 order_search_url={config.order_search_url}
                                 snapshot_updates_url={config.snapshot_updates_url}
                                 order_updates_url={config.order_updates_url}
                                 private_trade_updates_url={config.private_trade_updates_url}
                                 public_trade_updates_url={config.public_trade_updates_url}
                />
        );
    }

}

dom.render(<Exchange/>, document.getElementById("app"));
