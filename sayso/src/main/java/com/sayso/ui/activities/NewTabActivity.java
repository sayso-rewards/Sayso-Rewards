package com.sayso.ui.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.sayso.R;
import com.sayso.utils.SdkConstants;

import cz.msebera.android.httpclient.Header;

public class NewTabActivity extends Activity {

    public SharedPreferences sharedpreferences;

    public SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sayso_webview);

        sharedpreferences = getSharedPreferences("SaySo", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        WebView webView = findViewById(R.id.SaysoWebView);
        ProgressBar progressBar = findViewById(R.id.SaysoProgressBar);
        ImageView home = findViewById(R.id.Saysohome);

        home.setVisibility(View.VISIBLE);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        String url = intent.getStringExtra("URL");

        //improve webView performance
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setAppCacheEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setSavePassword(true);
        webView.getSettings().setSaveFormData(true);
        webView.getSettings().setEnableSmoothTransition(true);

        webView.getSettings().setLightTouchEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (request.getUrl().toString().contains(SdkConstants.CLIENT_URL)) {

                    progressBar.setVisibility(View.VISIBLE);

                    AsyncHttpClient client = new AsyncHttpClient();
                    client.get(request.getUrl().toString(), new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            progressBar.setVisibility(View.GONE);
                            finish();
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
                            progressBar.setVisibility(View.GONE);
                            for (Header header : headers) {
                                if (header.getName().toLowerCase().equals(SdkConstants.PAYOUT)) {
                                    try {
                                        float payout = Float.parseFloat(header.getValue());

                                        if (sharedpreferences.contains(SdkConstants.PAYOUT)) {
                                            payout = payout + sharedpreferences.getFloat(SdkConstants.PAYOUT, 0);
                                        }

                                        editor.putFloat(SdkConstants.PAYOUT, payout);
                                        editor.apply();
                                        finish();
                                    } catch (NumberFormatException e) {
                                        finish();
                                    }
                                }
                            }
                        }
                    });
                }

                return false;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                if (progress == 100) {
                    progressBar.setVisibility(View.GONE);
                }
            }

        });

        webView.loadUrl(url);
    }
}
