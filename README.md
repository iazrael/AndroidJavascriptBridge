让Java跟Javascript交互变得更加容易!
=======================

众所周知, 目前Android的Java如果要调用Webview中的Javascript只能使用Webview.loadUrl("javascript:xxx(yyy)")的形式
使用URL来执行Javascript,将会遇到URL长度限制的问题(浏览器对URL的长度都是有限制的), 如果需要传递大数据, 是比较困难的.

而Javascript却能直接调用Java提供的方法, 并可取得返回值, 因此可以改成由Javascript去轮询Java接口, 取出需要执行的命令并把结果返回
如此, 就能实现Java对Javascript的调用, 并且支持传入大量数据.

Example
=======

Android端调用, 加入com.imatlas.jsb 和 com.imatlas.util包, 按如下步骤调用

    1. 创建JavascriptBridge实例  
    final JavascriptBridge jsb = new JavascriptBridge(webView); 

    2. 调用Javascript方法
    Bundle params = new Bundle();
    params.putString("asdfasdf", "123123");
    jsb.require("alert", params, new JavascriptBridge.Callback() {
        @Override
        public void onComplate(JSONObject response, String cmd, Bundle params) {
            Log.i("js response",response.toString());
        }
    });

    3. 提供Java方法给Javascript调用
    //添加个 messagebox 方法给js
    jsb.addJavaMethod("messagebox", new JavascriptBridge.Function() {
        @Override
        public Object execute(JSONObject params) {
            Toast.makeText(getApplicationContext(), params.toString(), Toast.LENGTH_LONG)
                    .show();
            return "{\"ret\":123}";
        }
    });

Javascript端的调用, 须先引入web/js/jsb.js, 之后按如下方式调用

    1. 调用Java方法
    jsb.require('messagebox', {'text': '你好, messagebox!'}, function(response){
        alert('调用messagebox回来啦\n' + JSON.stringify(response));
    });
		
    2. 提供Javascript方法给Java调用
    jsb.addJavascriptMethod('alert', function(params){
        alert( '------\n' + JSON.stringify(params) + '\n========\n');
        return {'text': 'alert ok'};
    });
