function route(handle,pathname,request,response) {
	if(pathname == '/favicon.ico') {
		return;
	}
    console.log("About to route a request for " + pathname);  
     if (typeof handle[pathname] === 'function') {  
        return handle[pathname](request,response);
    } else {  
        console.log("No request handler found for " + pathname);  
        return "404 Not found";
    }  
}  

exports.route = route;