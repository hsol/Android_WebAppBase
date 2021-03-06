/*
* Copyright 2016 HansolLim
* Released under the MIT license
* http://hsol.github.io/
*/

package com.example.webappbase.common;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.webappbase.R;
import com.example.webappbase.base.BaseFragment;
import com.example.webappbase.lib.Utils;

/**
 * Created by hansollim on 2016-07-26.
 */
public class WebViewFragment extends BaseFragment {
    protected WebView webView = null;
    protected AndroidBridge mBridge = null;

    @Override
    protected int getBaseFragment() {
        return R.layout.fragment_webview;
    }

    @Override
    protected void onInit() {
        webView = (WebView) mView.findViewById(R.id.webView);
        webViewInit(getRootUrl());

        if(mValue.isDev())
            mView.findViewById(R.id.flag_test).setVisibility(View.VISIBLE);
    }

    @Override
    protected void onClick(int viewId) {

    }

    @Override
    protected void onBackKeyDown(KeyEvent event) {
        WebBackForwardList webBackForwardList = webView.copyBackForwardList();
        if (webBackForwardList.getCurrentIndex() > 0) {
            String historyUrl = webBackForwardList.getItemAtIndex(webBackForwardList.getCurrentIndex() - 1).getUrl();
            webView.goBack();
        } else {
            if(getBaseActivity().getVisibleFragment() == this){
                new AlertDialog.Builder(mContext)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle(getString(R.string.app_name))
                        .setMessage(getString(R.string.finish_question))
                        .setPositiveButton("예", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getBaseActivity().finish();
                            }
                        })
                        .setNegativeButton("아니요", null)
                        .show();
            }
        }
    }

    public void webViewInit(String urlString) {
        mBridge = new AndroidBridge(mContext, webView);

        webView.setVerticalScrollBarEnabled(false);
        webView.setVerticalFadingEdgeEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setHorizontalFadingEdgeEnabled(false);
        webView.addJavascriptInterface(mBridge, getString(R.string.app_prefix));
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                String[] fnm = url.split("/");
                String fileName = fnm[fnm.length - 1]; // 파일이름
                String fileHost = fnm[2]; // 도메인

                DownloadManager mDownloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(url);
                DownloadManager.Request mRequest = new DownloadManager.Request(uri);
                mRequest.setTitle(fileName);
                mRequest.setDescription(fileHost);
                mRequest.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                File pathExternalPublicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                pathExternalPublicDir.mkdirs();
                mRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                mRequest.setShowRunningNotification(true);
                mRequest.setVisibleInDownloadsUi(true);
                long downloadId = mDownloadManager.enqueue(mRequest);
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result) {
                AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                alert.setTitle(getString(R.string.app_name));
                alert.setMessage(message);
                alert.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                alert.setCancelable(false);
                alert.create();
                alert.show();
                return true;
            }

            @Override
            public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, Message resultMsg) {
                WebView newWebView = new WebView(getContext());
                WebView.WebViewTransport transport = (WebView.WebViewTransport)resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();
                return true;
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Utils.Logger(mContext, "D", "onPageStarted: " + url);

                if(mValue.isPreloaderOn())
                    mView.findViewById(R.id.web_preloader).setVisibility(View.VISIBLE);

                if(mValue.isSpinnerOn())
                    mView.findViewById(R.id.web_preloader_spinner).setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Utils.Logger(mContext, "D", "onPageFinished: " + url);

                if(mValue.isPreloaderOn())
                    mView.findViewById(R.id.web_preloader).setVisibility(View.GONE);

                if(mValue.isSplashOn())
                    mView.findViewById(R.id.web_preloader_splash).setVisibility(View.GONE);

                if(mValue.isSpinnerOn())
                    mView.findViewById(R.id.web_preloader_spinner).setVisibility(View.GONE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return whenUrlLoading(view, url);
            }
        });

        WebSettings webSettings = webView.getSettings();
        webSettings.setSupportMultipleWindows(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportZoom(false);
        webSettings.setDefaultTextEncodingName("utf-8");

        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(mContext);
        }
        cookieManager.setAcceptCookie(true);

        webView.loadUrl(urlString);

        if(mValue.isSplashOn())
            mView.findViewById(R.id.web_preloader_splash).setVisibility(View.VISIBLE);
    }

    public boolean whenUrlLoading(WebView view, String url) {
        String logHeader = "shouldOverrideUrlLoading";
        Intent intent;
        try {
            Utils.Logger(logHeader, mContext, "D", "intent url : " + url);
            intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);

            Utils.Logger(logHeader, mContext, "D", "intent getDataString : " + intent.getDataString());
            Utils.Logger(logHeader, mContext, "D", "intent getPackage : " + intent.getPackage());

        } catch (URISyntaxException ex) {
            Utils.Logger(logHeader, mContext, "E", "intent getPackage : " + "URI syntax error : " + url + ":" + ex.getMessage());
            return false;
        }

        Uri uri = Uri.parse(url);
        Uri uriDataString = Uri.parse(intent.getDataString());
        intent = new Intent(Intent.ACTION_VIEW, uri);

        if (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("javascript:")) {
            return false;
        } else if(url.contains("")) {
            return false;
        } else {

        }
        return true;
    }

    public String getParam(String urlString, String keyName) {
        Pattern p = Pattern.compile("(\\?|\\&)([^=]+)\\=([^&]+)");
        Matcher m = p.matcher(urlString);
        String paramString, paramKey, result = "";

        while (m.find()) {
            paramString = m.group();
            paramKey = paramString.replace("?", "").replace("&", "").split("=")[0];

            if(paramKey.equals(keyName))
                result = paramString.split(paramKey + "=")[1];
        }

        return result;
    }

    public String getRootUrl() { return !mValue.isDev() ? mValue.rootUrl() : mValue.rootUrl_dev(); }
}