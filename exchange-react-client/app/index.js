
import React from "react";
import dom from "react-dom";
import Instrument from "./components/instrument";
import InstrumentSelector from "./components/instrument-selector";
//import '../bootstrap/dist/css/bootstrap.css';

class Exchange extends React.Component {

    constructor() {
        super();
        console.log("Created exchange")
    }



    render() {
        return (
            <InstrumentSelector  instruments_url='/instruments' />
        );
    }

}

dom.render(<Exchange/>, document.getElementById("app"));
