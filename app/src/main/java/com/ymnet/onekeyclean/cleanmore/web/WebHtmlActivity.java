package com.ymnet.onekeyclean.cleanmore.web;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.umeng.analytics.MobclickAgent;
import com.ymnet.onekeyclean.R;
import com.ymnet.onekeyclean.cleanmore.utils.OnekeyField;
import com.ymnet.onekeyclean.cleanmore.utils.StatisticMob;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jrm on 2017-5-5.
 * 加载html界面
 */

public class WebHtmlActivity extends Activity {

    private WebView   webView;
    private ImageView back;
    private int       flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.web_activity);
            webView = (WebView) findViewById(R.id.webview);
            back = (ImageView) findViewById(R.id.back);
            setConfig();
            loadWebView();
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (webView.canGoBack()) {
                        webView.goBack();//返回上个页面
                        return;
                    }
                    finish();
                }
            });

            //获取本地推广浏览器
            JumpUtil.getInstance().getWebAddresss(this);
            //统计
            String stringExtra = getIntent().getStringExtra(OnekeyField.CLEAN_NEWS);
            Log.d("WebHtmlActivity", stringExtra);
            Map<String, String> m = new HashMap<>();
            m.put(OnekeyField.CLEAN_NEWS, stringExtra);
            MobclickAgent.onEvent(this, StatisticMob.STATISTIC_NEWS_ID, m);
        } catch (Exception e) {
            MobclickAgent.reportError(this, "没有webview,"+e.fillInStackTrace());
            finish();
        }

    }

    private void loadWebView() {
        final String htmlUrl = getIntent().getStringExtra("html");
        flag = getIntent().getIntExtra("flag", -1);
        if (!TextUtils.isEmpty(htmlUrl)) {
            webView.loadUrl(htmlUrl);
            webView.setWebChromeClient(new WebChromeClient());
            // 对页面加载时，成功，失败时的处理,这里可以拓展；
            webView.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    String uri = Uri.parse(htmlUrl).getHost();
                    String uri2 = Uri.parse(url).getHost();
                    if (uri.equals(uri2)) {
                        return super.shouldOverrideUrlLoading(view, url);
                    } else {
                        if (flag != -1 && flag == 10) {
                            JumpUtil.getInstance().unJumpAddress(WebHtmlActivity.this, url, 10);
                        }/* else {
                            JumpUtil.getInstance().unJumpAddress(WebHtmlActivity.this, url,20);
                        }*/
                        return true;
                    }
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    if (!webView.getSettings().getLoadsImagesAutomatically()) {
                        webView.getSettings().setLoadsImagesAutomatically(true);
                    }
                }

                @Override
                public void onReceivedError(WebView view, int errorCode,
                                            String description, String failingUrl) {
                    super.onReceivedError(view, errorCode, description, failingUrl);
                    // 加载网页失败时处理 如：
                    view.loadDataWithBaseURL(null,
                            "<span style=\"color:#FF0000\">网页加载失败</span>",
                            "text/html", "utf-8", null);
                }

                @Override
                public void onReceivedSslError(WebView view,
                                               SslErrorHandler handler, SslError error) {
                    super.onReceivedSslError(view, handler, error);
                    handler.proceed();  // 接受所有网站的证书
                }
            });
        }
    }

    private void setConfig() {
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setHorizontalScrollbarOverlay(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setSupportZoom(true);
        // 适配不同分辨率
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int scale = dm.densityDpi;
        if (scale == 240) {
            webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        } else if (scale == 160) {
            webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        } else {
            webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
        }
        ints();
    }

    // 加快HTML网页加载完成速度 ,在不加载完网页的情况先不加载图片
    private void ints() {
        if (Build.VERSION.SDK_INT >= 19) {
            webView.getSettings().setLoadsImagesAutomatically(true);
        } else {
            webView.getSettings().setLoadsImagesAutomatically(false);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();//返回上个页面
            return true;
        }
        return super.onKeyDown(keyCode, event);//退出整个应用程序
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
