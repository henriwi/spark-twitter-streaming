//$(function () {
    //ws = new WebSocket('ws://localhost:8080/ws');
    //ws.onmessage = function (msg) {
    //    console.log(msg);
    //}
//});
$(function() {
    
    ws = new WebSocket('ws://localhost:8080/ws');
    ws.onmessage = function (msg) {
        var data = JSON.parse(msg.data);

        var hashTagDiv = $('#hashtags');
        hashTagDiv.empty();
        console.log(data);
        
        data.hashTags.forEach(function(elem) {
            hashTagDiv.append('<li>' + elem.hashTag + elem.count + '</li>');

        });
    }
    
    console.log( "ready!" );
});

