package com.lisn.loadinghtml;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

/**
 * 参考
 * http://blog.csdn.net/harvic880925/article/details/51464687
 * http://blog.csdn.net/harvic880925/article/details/51523983
 * http://blog.csdn.net/harvic880925/article/details/51583253
 */

public class MainActivity extends AppCompatActivity {


    private WebView mWebView;
    private Button mBtn;
    private Button mBtn1;
    private Button mBtn2;
    private String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TAG = "myLog";
        mWebView = (WebView) findViewById(R.id.webview);

        //设置WebView属性，能够执行Javascript脚本
        mWebView.getSettings().setJavaScriptEnabled(true);
        //这句的意思是把MyActivity对象注入到WebView中，在WebView中的对象别名叫android
        mWebView.addJavascriptInterface(new JSBridge(), "android");
        //加载需要显示的网页
        //  mWebView.loadUrl("http://www.baidu.com/");
        mWebView.loadUrl("file:///android_asset/map.html");

        /*//根据手机系统版本调用不同的方法  KITKAT==Android的版本4.4
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.evaluateJavascript(javascript, resultCallback);
        } else {
            mWebView.loadUrl(javascript);
        }*/

        //设置Web视图
        mWebView.setWebViewClient(new HelloWebViewClient());


        mBtn = (Button) findViewById(R.id.btn);

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.loadUrl("http://www.baidu.com");
            }
        });

        //调用js中的方法
        mBtn1 = (Button) findViewById(R.id.bt1);
        mBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.loadUrl("javascript:sum(3,8)");
            }
        });

        //调用JS函数获取返回值（4.4系统）
        mBtn2 = (Button) findViewById(R.id.bt2);
        mBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.evaluateJavascript("getGreetings()", new ValueCallback() {
                    @Override
                    public void onReceiveValue(Object o) {
                        Log.e("fanhui value====", "onReceiveValue value=" + (String) o);
                    }

                });
            }
        });


    }

    @Override
    //设置回退
    //覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
//            mWebView.goBack(); //goBack()表示返回WebView的上一页面
//            return true;
//        }
//        return false;
        //改写物理返回键的逻辑
        if(keyCode==KeyEvent.KEYCODE_BACK) {
            if(mWebView.canGoBack()) {
                mWebView.goBack();//返回上一页面
                return true;
            } else {
                System.exit(0);//退出程序
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    // 直接调用WebView.setWebViewClient方法即可设置WebViewClient回调，
    // 这里重写的两个函数，onPageStarted会在WebView开始加载网页时调用，onPageFinished会在加载结束时调用。
    private class HelloWebViewClient extends WebViewClient {

        //这个函数会在加载超链接时回调过来；所以通过重写shouldOverrideUrlLoading，可以实现对网页中超链接的拦截；
        //返回值是boolean类型，表示是否屏蔽WebView继续加载URL的默认行为，因为这个函数是WebView加载URL前回调的，
        //所以如果我们return true，则WebView接下来就不会再加载这个URL了，所有处理都需要在WebView中操作，包含加载。
        //如果我们return false，则系统就认为上层没有做处理，接下来还是会继续加载这个URL的。
        // WebViewClient默认就是return false的：
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //由于每次超链接在加载前都会先走shouldOverrideUrlLoading回调，所以我们如果想拦截某个URL，
            // 将其转换成其它URL可以在这里做。
            //比如，我们拦截所有包含“blog.csdn.net”的地址，将其替换成”www.baidu.com”：
            /*if (url.contains("blog.csdn.net")){
                view.loadUrl("http://www.baidu.com");
            }else {
                view.loadUrl(url);
            }*/

            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.e(TAG, "onPageStarted会在WebView开始加载网页时调用");
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.e(TAG, "onPageFinished会在加载结束时调用");
        }

        /*  WebView view:当前的WebView实例
            int errorCode：错误码
            String description：错误描述
            String failingUrl：当前出错的URL*/

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            //mWebView.loadUrl("file:///android_asset/error.html"); //加载错误页面
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }
    }

    public class JSBridge {
        @JavascriptInterface
        public void toastMessage(String message) {
            Toast.makeText(getApplicationContext(), "通过Natvie传递的Toast:" + message, Toast.LENGTH_LONG).show();
        }

        @JavascriptInterface
        public void onSumResult(int result) {
            Toast.makeText(getApplicationContext(), "received result:" + result, Toast.LENGTH_SHORT).show();
        }
    }
}
