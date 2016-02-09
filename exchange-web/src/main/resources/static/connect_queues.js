function connect_queues() {
    var snapshotsList = $("#snapshots");
    var privateTradeList = $("#privateTrades");

    // defined a connection to a new socket endpoint
    var socket = new SockJS('/exchange');

    var stompClient = Stomp.over(socket);

    stompClient.connect({ }, function(frame) {

        var suffix = frame.headers['queue-suffix'];
        snapshotsList.append("<li>" + suffix + "</li>");
        // subscribe to the /topic/message endpoint
        stompClient.subscribe("/topic/snapshot", function(data) {
            var message = data.body;
            snapshotsList.append("<li>" + message + "</li>");
        });

        stompClient.subscribe("/queue/private.trade" + suffix, function(data) {
            var message = data.body;
            privateTradeList.append("<li>" + message + "</li>");
        });
    });
}