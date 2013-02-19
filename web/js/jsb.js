/**
 * AndroidJavascriptBridge
 * 连接 Java 和 Javascript 的桥梁
 * @author azrael
 */
!function(undefined){
	var NAMESPACE = 'jsb';
	var API_NAMESPACE = '__JavascriptBridge__';
	var API_FREQUENCY = 200;

	var context = window[NAMESPACE] = {};
	var api = window[API_NAMESPACE] || null;
	if(!api){
		return alert('发生错误, 未找到 api 对象!');
	}
	/**
	 * 保存提供给java调用的js方法列表
	 * @type {Object}
	 */
	var jsMethodMap = {};

	/**
	 * 添加提供给java调用的js方法
	 * @param {[type]} method [description]
	 * @param {[type]} func   [description]
	 */
	context.addJavascriptMethod = function(method, func){
		jsMethodMap[method] = func;
	}

	/**
	 * 请求调用java方法
	 * @param  {[type]}   cmd      [description]
	 * @param  {[type]}   params   [description]
	 * @param  {Function} callback [description]
	 * @return {[type]}
	 */
	context.require = function(cmd, params, callback){
		params = params || '{}';
		var result = api.require(cmd, JSON.stringify(params));
		if(callback && result){
			result = JSON.parse(result);
		}
		callback && callback(result);
	}

	var executeCommand = function(cmdObj){
		var method = jsMethodMap[cmdObj.cmd];
		if(method instanceof Function){
			var result = method.call(window, cmdObj.params);
			if(result){
				api.setResult(cmdObj.serial, JSON.stringify(result));
			}
		}
	}

	var listener = {
		_timer: 0,
		_timerFunc: function(){
			var cmdsStr = api.getCommands();
			var cmdList = JSON.parse(cmdsStr);
			if(cmdList.length){
				for(var i in cmdList){
					executeCommand(cmdList[i]);
				}
			}
			listener.run();
		},
		run: function(){
			this._timer = setTimeout(this._timerFunc, API_FREQUENCY);
		},
		stop: function(){
			clearTimeout(this._timer);
			this._timer = 0;
		}
	};

	listener.run();
}();