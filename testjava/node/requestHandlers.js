var exec = require("child_process").exec; 
var MyBmob = require('./bmob');
var url = require("url");
var Bmob = MyBmob.Bmob;
Bmob.initialize("2a2af664ae1930ed9856320ae7257d96", "b0755af19edba83a5a8be26922728dab", "bcb1c76abe663895e0fce7f8ff178470");

function start(request,response) {   
	console.log("Request handler 'start' was called."); 
    var content = "Hello Start";
    exec("find /",  
        { timeout: 10000, maxBuffer: 20000*1024 },  
        function (error, stdout, stderr) {  
            response.writeHead(200, {"Content-Type": "text/plain"});  
            response.write(stdout);  
            response.end();  
        }  
    ); 
}  

function upload(request,response) {
	var query = new Bmob.Query(Bmob.User);
	var arg = url.parse(request.url,true).query;   
    console.log("objectId = " + arg.objectId);  

	query.get(arg.objectId, {
	  success: function(user) {
	    // 查询成功，调用get方法获取对应属性的值
	     console.log("查询成功");  

	    var username = user.get("username");
	    var appVersion = user.get("appVersion");
	    var userType = user.get("userType");

		 response.writeHead(200, {"Content-Type": "json"}); 
	    if(userType >= 1) {
	    	if(dataObject.appVersion < 430){
	        	response.write(JSON.stringify({ status:"failed",message:"当前版本过低，请更新到最新版本4.3.0（去群共享下载，不要卸载旧的，覆盖安装就行。更新之后还不行的需要重启下手机)"}));  
	    	}else {
	        	response.write(JSON.stringify({ status:"ok",message:"验证成功"}));  	
        	}
	    }else {
	        response.write(JSON.stringify({ status:"failed",message:"当前用户暂无授权，请联系软件作者购买授权"}));  
	    }
	    response.end(); 

	  },
	  error: function(object, error) {
	    // 查询失败
	     console.log("查询失败");  
	    response.writeHead(200, {"Content-Type": "json"});  
        response.write(JSON.stringify({status:"failed",message:"user not exist"}));  
        response.end(); 
	  }
	});
}  

exports.start = start;  
exports.upload = upload;