/**
 * @author azrael
 * @date 2013-2-6
 */
package com.imatlas.jsb;

import android.os.Bundle;
import android.webkit.WebView;

import com.imatlas.util.JSONUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author azrael
 * AndroidJavascriptBridge
 * 连接 Java 和 Javascript 的桥梁
 */
public class JavascriptBridge {

    /**
     * 调用js方法后的回调
     * @author azrael
     *
     */
    public interface Callback{
        /**
         * js方法执行后会调用该方法回调
         * @param response
         * @param cmd
         * @param params
         */
        public void onComplate(JSONObject response, String cmd, Bundle params);
    }
    
    /**
     * 提供给js的java方法
     * @author azrael
     *
     */
    public interface Function{
        /**
         * 被js调用是执行的java
         * @param params
         * @return
         */
        public Object execute(JSONObject params);
    }
    
    private static long seed = 0;
    
    private static long getSerial(){
        return ++seed;
    }
    
    /**
     * java对js的调用命令封装
     * @author azrael
     *
     */
    class Command{
        long serial;
        String cmd;
        Bundle params;
        Callback callback;
        
        public Command() {
            this.serial = getSerial();
        }

        /**
         * @param cmd
         * @param params
         * @param callback
         */
        public Command(String cmd, Bundle params, Callback callback) {
            this();
            this.cmd = cmd;
            this.params = params;
            this.callback = callback;
        }
        /**
         * 把命令的内容序列化成json字符串
         */
        @Override
        public String toString(){
            JSONObject json = new JSONObject();
            try {
                json.put("cmd", this.cmd);
                json.put("serial", this.serial);
                json.put("params", JSONUtil.bundleToJSON(this.params));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return json.toString();
        }
        /**
         * 释放该命令保存的内容, 防止被再次触发
         */
        public void release(){
            this.serial = 0;
            this.cmd = null;
            this.params = null;
            this.callback = null;
        }
        
    }
    /**
     * 提供给js调用的接口
     * @author azrael
     *
     */
    class JavascriptInterface{
        /**
         * 获取需要处理的命令
         * @return 命令数组
         */
        @JavascriptInterface
        public String getCommands(){
            ArrayList<Command> temp = commandQueue;
            commandQueue = new ArrayList<Command>();
            String cmds = temp.toString();
            temp.clear();
            return cmds;
        }
        /**
         * js执行java cmd之后返回的接口通过这个接口设置
         * @param serial
         * @param jsonResult
         */
         @JavascriptInterface
        public void setResult(long serial, String jsonResult){
            Command command = commandMap.remove(serial);
            if(command == null){
                return;
            }
            JSONObject json = null;
            if(command.callback != null && jsonResult != null && !jsonResult.isEmpty()){
                try {
                    json = new JSONObject(jsonResult);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                command.callback.onComplate(json, command.cmd, command.params);
            }
            command.release();
        }
        /**
         * js使用该方法请求java接口
         * @param cmd
         * @param params
         * @return java方法执行的返回值
         */
         @JavascriptInterface
        public Object require(String cmd, String params){
            Function function = javaMethodMap.get(cmd);
            if(function != null){
                try {
                    JSONObject json = new JSONObject(params);
                    return function.execute(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
    
    /**
     * js方法的命名空间, js里的使用 window.__JavascriptBridge__.xxx 调用java提供给js的接口
     */
    private static final String API_NAMESPACE = "__JavascriptBridge__";
    
    private HashMap<Long, Command> commandMap;
    
    private ArrayList<Command> commandQueue;
    
    /**
     * 保存java提供给js的接口列表
     */
    private HashMap<String, Function> javaMethodMap;
    
    public JavascriptBridge(WebView mWebView) {
        commandMap = new HashMap<Long, Command>();
        commandQueue = new ArrayList<Command>();
        javaMethodMap = new HashMap<String, Function>();
        
        mWebView.addJavascriptInterface(new JavascriptInterface(), API_NAMESPACE);
        
    }
    
    /**
     * 请求调用js方法
     * @param cmd
     * @param params
     * @param callback
     */
    public void require(String cmd, Bundle params, Callback callback){
        Command command = new Command(cmd, params, callback);
        commandMap.put(command.serial, command);
        commandQueue.add(command);
    }
    
    /**
     * 添加一个java方法给js调用
     * @param method
     * @param function
     */
    public void addJavaMethod(String method, Function function){
        javaMethodMap.put(method, function);
    }
    
    
}
