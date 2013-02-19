package com.example;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.imatlas.jsb.JavascriptBridge;

import org.json.JSONObject;

public class MainActivity extends Activity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        
        Button btn = (Button) findViewById(R.id.button1);
        Button btn2 = (Button) findViewById(R.id.button2);
        
        final WebView webView = (WebView)findViewById(R.id.webView1);
        webView.setWebChromeClient(new WebChromeClient());
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        
        final JavascriptBridge jsb = new JavascriptBridge(webView);
        
        //添加个 messagebox 方法给js
        jsb.addJavaMethod("messagebox", new JavascriptBridge.Function() {
            
            @Override
            public Object execute(JSONObject params) {
                Toast.makeText(getApplicationContext(), params.toString(), Toast.LENGTH_LONG)
                        .show();
                return "{\"ret\":123}";
            }
        });
        
        webView.loadUrl("http://www.imatlas.com/test.html");
        btn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                webView.loadUrl("http://www.imatlas.com/test.html");
            }
        });
        
        btn2.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Bundle params = new Bundle();
                params.putString("asdfasdf", "123123");
                //调用js提供的alert方法
                jsb.require("alert", params, new JavascriptBridge.Callback() {
                    @Override
                    public void onComplate(JSONObject response, String cmd, Bundle params) {
                        Log.i("jsb",response.toString());
                    }
                });
                
            }
        });
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
}
