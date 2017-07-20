var http = require("http");
var url = require("url");

function start(route,handle) {
	function onRequest(request, response) {

		var pathname = url.parse(request.url).pathname;

		if(pathname == '/favicon.ico') {
			return;
		}
 		console.log("Request for " + pathname + " received.");	

        var content = route(handle,pathname,request,response);  
	}
	http.createServer(onRequest).listen(8888);
	console.log("server has started");
}

exports.start = start;