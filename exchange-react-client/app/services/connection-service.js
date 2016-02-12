'use strict'

import Stomp from "Stomp";
import SockJS from "SockJS";
import Promise from 'bluebird';

var socket = new SockJS('/exchange');
var stompClient = Stomp.over(socket);

function connect() {
    return new Promise((resolver, reject) => {
        stompClient.connect({} , () => {
            resolver(stompClient);
        });
    });

}

// Make stomp client available to other modules
export default connect();