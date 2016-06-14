
import React from "react";
import stompConnectTo from "../services/connection-service";
import _ from "lodash";
import $ from "jquery";

class Snapshot extends React.Component {

    constructor(props) {
        super(props);
        this.state = {  buy: [], sell: [] };
        this.subscription = null;
    }

    getSnapshot(instrument) {

        console.log("Getting order snapshot for "+instrument);

        $.ajax({
            url: this.props.snapshot_url+"?instrument="+instrument,
            crossDomain: true,
            xhrFields: {
                withCredentials: true
            },
        }).then((snapshot) => {
            console.log("Got snapshot data "+snapshot);
            this.receiveSnapshot(snapshot);
        }),
        (error) => {
            console.log('failed to get snapshot');
        };


        //var snapshotRequest = new Request(this.props.snapshot_url+"?instrument="+instrument, {
        //    method: 'GET',
        //    credentials: 'same-origin',
        //    mode: "no-cors",
        //    cache: "no-cache",
        //    headers: new Headers({
        //        'Content-Type': 'application/json'
        //    })
        //})
        //
        //fetch(snapshotRequest).then((response) => {
        //    console.log("got snapshot");
        //    return response.json();
        //}).then((snapshot) => {
        //    console.log("Got snapshot data "+snapshot);
        //    this.receiveSnapshot(snapshot);
        //}),
        //(error) => {
        //    console.log('failed to get snapshot');
        //};
    }

    receiveSnapshot(snapshot){
        var buy = _.sortBy(snapshot.buy, "price");
        var sell = _.sortBy(snapshot.sell, "price");
        console.log("Snapshot with buys: "+buy+" sells: "+sell);
        this.setState({buy, sell});
    }

    subscribeInstrument(instrument) {
        console.log("Subscribing to snapshots for "+instrument+" on url "+this.props.updates_url);
        stompConnectTo(this.props.updates_url).then((client) => {
            this.subscription = client.subscribe(`/topic/snapshot/${instrument}`, (data) => {
                this.receiveSnapshot(JSON.parse(data.body));
            });
        });
    }

    unsubscribeCurrent() {
        if (this.subscription != null) {
            this.subscription.unsubscribe();
            this.subscription = null;
            console.log("Unsubscribed")
        }
    }

    componentWillUnmount() {
        this.unsubscribeCurrent();
    }

    componentWillReceiveProps(nextProps) {
        console.log("getting snapshot props. Current Instrument: "+this.props.instrument+" New Instrument:"+nextProps.instrument);
        this.unsubscribeCurrent();
        this.getSnapshot(nextProps.instrument);
        this.subscribeInstrument(nextProps.instrument);
    }

    render() {
        console.log("Rendering snapshot");
        return (
            <table className="table table-bordered">
                <thead><tr><th>Sell</th><th>Price</th><th>Buy</th></tr></thead>
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
Snapshot.propTypes = { snapshot_url : React.PropTypes.string.isRequired }
Snapshot.propTypes = { updates_url : React.PropTypes.string.isRequired }

export default Snapshot;