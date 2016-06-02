'use strict'

import Stomp from "Stomp";
import SockJS from "SockJS";
import Promise from 'bluebird';


export default function stompConnectTo(url) {
    var socket = new SockJS(url);
    var stompClient = Stomp.over(socket);

    return new Promise((resolver, reject) => {
        stompClient.connect({} , () => {
            resolver(stompClient);
        });
    });

}
