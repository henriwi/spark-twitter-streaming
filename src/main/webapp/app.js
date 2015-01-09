$(function () {
    ws = new WebSocket('ws://localhost:8080/ws');
    ws.onmessage = function (msg) {
        console.log(msg);
    }
});