import React from "react";

import Instrument from "./instrument";
import $ from "jquery";

class InstrumentSelector extends React.Component {

    constructor(props) {
        super(props);
        this.state = { instruments: ["AMZN"],
                       instrument: "AMZN" };
    }

    componentWillMount() {
        this.getInstrumentList();

    }

    getInstrumentList() {
        $.ajax({
            url: this.props.instrument_list_url,
            crossDomain: true,
            xhrFields: {
                withCredentials: true
            },
        }).then((instruments) => {
            console.log("Got instrument data "+instruments);
            this.setState({ instruments: instruments, instrument: instruments[0]});
        }),
        (error) => {
            console.log('failed to get instruments');
        };




        //var auth = 'Basic ' + window.btoa(unescape(encodeURIComponent("doug:password")))
        //var instrumentRequest = new Request(this.props.instrument_list_url, {
        //    method: 'GET',
        //    credentials: 'same-origin',
        //    headers: new Headers({
        //        'Content-Type': 'application/json',
        //        'Authorization': auth
        //    })
        //})
        //
        //fetch(instrumentRequest).then((response) => {
        //    console.log("got instruments");
        //    return response.json();
        //}).then((instruments) => {
        //    console.log("Got instrument data "+instruments);
        //    this.setState({ instruments: instruments,
        //        instrument: instruments[0]});
        //}),
        //(error) => {
        //    console.log('failed to get instruments');
        //};
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
                <Instrument instrument={this.state.instrument}
                            order_submit_url={this.props.order_submit_url}
                            snapshot_url={this.props.snapshot_url}
                            order_search_url={this.props.order_search_url}
                            snapshot_updates_url={this.props.snapshot_updates_url}
                            order_updates_url={this.props.snapshot_updates_url}
                            private_trade_updates_url={this.props.snapshot_updates_url}
                            public_trade_updates_url={this.props.snapshot_updates_url}
                    />
            </div>
        )
    }
}

InstrumentSelector.propTypes = { instrument_list_url : React.PropTypes.string.isRequired }
InstrumentSelector.propTypes = { order_submit_url : React.PropTypes.string.isRequired }
InstrumentSelector.propTypes = { order_search_url : React.PropTypes.string.isRequired }
InstrumentSelector.propTypes = { snapshot_url : React.PropTypes.string.isRequired }

InstrumentSelector.propTypes = { snapshot_updates_url : React.PropTypes.string.isRequired }
InstrumentSelector.propTypes = { order_updates_url : React.PropTypes.string.isRequired }
InstrumentSelector.propTypes = { private_trade_updates_url : React.PropTypes.string.isRequired }
InstrumentSelector.propTypes = { public_trade_updates_url : React.PropTypes.string.isRequired }



export default InstrumentSelector;