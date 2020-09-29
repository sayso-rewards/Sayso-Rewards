package com.sayso.ui.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.sayso.R;
import com.sayso.retrofit.ApiClient;
import com.sayso.retrofit.ApiInterface;
import com.sayso.ui.model.SurveyAvailabilityModel;
import com.sayso.utils.SdkConstants;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SaySo extends Activity {

    private static String mPartnerId = "";
    private static String mRid;

    public Activity mInstance;

    private WebView mWebView;
    private ProgressBar progressBar;

    private static final String TAG = "RFGOfferWall";

    public SharedPreferences sharedpreferences;

    public SharedPreferences.Editor editor;

    public void isSurveyAvailable(Activity instance, String partnerId, String rid) {

        ProgressDialog progressDialog = new ProgressDialog(instance);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        ApiInterface mApiInterface = ApiClient.getClient().create(ApiInterface.class);
        mApiInterface.isSurveyAvailable(mPartnerId, mRid).enqueue(new Callback<SurveyAvailabilityModel>() {
            @Override
            public void onResponse(Call<SurveyAvailabilityModel> call, Response<SurveyAvailabilityModel> response) {
                progressDialog.dismiss();

                mInstance = instance;
                mPartnerId = partnerId;
                mRid = rid;

                if (response != null && response.body() != null) {
                    if (response.body().getSurveysAvailable()) {
                        sharedpreferences = mInstance.getSharedPreferences("SaySo", Context.MODE_PRIVATE);
                        editor = sharedpreferences.edit();

                        editor.putBoolean(SdkConstants.IsSurveyAvailable, true);
                        editor.apply();

                        Toast.makeText(mInstance, "Survey available", Toast.LENGTH_SHORT).show();
                    } else {
                        sharedpreferences = mInstance.getSharedPreferences("SaySo", Context.MODE_PRIVATE);
                        editor = sharedpreferences.edit();

                        editor.putBoolean(SdkConstants.IsSurveyAvailable, false);
                        editor.apply();

                        Toast.makeText(mInstance, "Survey not available", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<SurveyAvailabilityModel> call, Throwable t) {
                sharedpreferences = mInstance.getSharedPreferences("SaySo", Context.MODE_PRIVATE);
                editor = sharedpreferences.edit();

                editor.putBoolean(SdkConstants.IsSurveyAvailable, false);
                editor.apply();

                progressDialog.dismiss();
                Log.d("onFailure online", t.toString());
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sayso_webview);

        mWebView = findViewById(R.id.SaysoWebView);
        progressBar = findViewById(R.id.SaysoProgressBar);

        showSurveyInWebView(getContentUrl());
    }

    public void displaySurveyInWebView() {
        if (mPartnerId.equals("")) {
            Toast.makeText(mInstance, "Sdk not configured", Toast.LENGTH_SHORT).show();
            return;
        }

        mInstance.startActivity(new Intent(mInstance, SaySo.class));
    }

    public void showSurveyInWebView(String contentUrl) {
        mWebView.loadUrl(contentUrl);
    }

    public String getContentUrl() {
        ApiInterface mApiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<ResponseBody> serviceCall = mApiInterface.survey(mPartnerId, mRid);

        return serviceCall.request().url().toString();
    }

    private void setWebViewSettings(WebView mWebView) {
        mWebView.requestFocus();
        mWebView.getSettings().setLightTouchEnabled(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setSupportMultipleWindows(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.setSoundEffectsEnabled(true);

        //improve webView performance
        mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setSavePassword(true);
        mWebView.getSettings().setSaveFormData(true);
        mWebView.getSettings().setEnableSmoothTransition(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mWebView != null) {

            setWebViewSettings(mWebView);

            if (Build.VERSION.SDK_INT >= 19) {
                mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            } else {
                mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }

            mWebView.setWebViewClient(new WebViewClient());

            mWebView.setWebViewClient(new WebViewClient() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    Log.d(TAG, "shouldOverrideUrlLoading: "+String.valueOf(request.getUrl()));
                    return super.shouldOverrideUrlLoading(view, request);
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {

                }
            });

            mWebView.setWebChromeClient(new WebChromeClient() {
                WebView newView = new WebView(SaySo.this);

                @Override
                public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {

                    setWebViewSettings(newView);

                    newView.setWebViewClient(new WebViewClient() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                            Intent intent = new Intent(SaySo.this, NewTabActivity.class);
                            intent.putExtra("URL", String.valueOf(request.getUrl()));
                            startActivity(intent);

                            newView.destroy();

                            return super.shouldOverrideUrlLoading(view, request);
                        }

                        @Override
                        public void onPageStarted(WebView view, String url, Bitmap favicon) {

                        }
                    });

                    WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                    transport.setWebView(newView);
                    resultMsg.sendToTarget();
                    return true;
                }

                public void onProgressChanged(WebView view, int progress) {
                    if (progress < 100) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                    if (progress == 100) {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
      super.onBackPressed();
    }

    @SuppressLint("CommitPrefEdits")
    public boolean isSurveyAvailable(Activity activity) {
        sharedpreferences = activity.getSharedPreferences("SaySo", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        boolean isSurveyAvailable = sharedpreferences.getBoolean(SdkConstants.IsSurveyAvailable, false);
        editor.clear();
        editor.apply();

        return isSurveyAvailable;
    }

    @SuppressLint("CommitPrefEdits")
    public Float getRewardValue(Activity activity){
        sharedpreferences = activity.getSharedPreferences("SaySo", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        float rewardValue = sharedpreferences.getFloat(SdkConstants.PAYOUT,0);
        editor.clear();
        editor.apply();

        return rewardValue;
    }
}
