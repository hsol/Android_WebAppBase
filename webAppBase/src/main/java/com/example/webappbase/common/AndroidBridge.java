package com.example.webappbase.common;
import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.example.webappbase.base.BaseBridge;

/**
 * Created by hansollim on 2016-08-04.
 */
public class AndroidBridge extends BaseBridge {
    public AndroidBridge(Context context, WebView view) {
        super(context, view);
    }

    @JavascriptInterface
    public void sendMessage() { //sendMessage() -> sendMessage(final String arg)
        handler.post(new Runnable() {
            public void run() {

            }
        });
    }
}
