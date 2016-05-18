import React from "react";

import Instrument from "./instrument";

class InstrumentSelector extends React.Component {

    constructor(props) {
        super(props);
        this.state = { instruments: ["AMZN"],
                       instrument: "AMZN" };
    }

    componentWillMount() {

        var instrumentRequest = new Request(this.props.instruments_url, {
            method: 'GET',
            credentials: 'same-origin',
            headers: new Headers({
                'Content-Type': 'application/json'
            })
        })


        fetch(instrumentRequest).then((response) => {
            console.log("got instruments");
            return response.json();
        }).then((instruments) => {
            console.log("Got instrument data "+instruments);
            this.setState({ instruments: instruments,
                instrument: instruments[0]});
        }),
        (error) => {
            console.log('failed to get instruments');
        };
    }


    handleInstrumentChange(e) {
        console.log("Handling change to " + e.target.value)

        this.setState({ instruments: this.state.instruments,
                       instrument: e.target.value});
    }

    render() {

        var options = this.state.instruments.map(function(instrument) {
            return (
                <option value={instrument}>{instrument}</option>
            )
        });

        return(
            <div>
                <select value={this.state.instrument} onChange={this.handleInstrumentChange.bind(this)}>
                    {options}
                </select>
                <Instrument instrument={this.state.instrument} snapshot_url='/snapshot'/>
            </div>
        )
    }
}

InstrumentSelector.propTypes = { instruments_url : React.PropTypes.string.isRequired }

export default InstrumentSelector;