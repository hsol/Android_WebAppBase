package kr.cnttech.webappbase.base;

import android.content.Context;
import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.util.List;

import kr.cnttech.webappbase.lib.Utils;

/**
 * Created by hansollim on 2016-08-04.
 */
public class BaseBridge {
    protected final Handler handler = new Handler();
    protected static WebView mWebView;
    protected static Context mContext;

    /**
     * 안드로이드 브릿지 생성자.
     *
     * @param Context context 부모 context
     * @param WebView view 호출될 웹뷰
     */
    public BaseBridge(Context context, WebView view) {
        mWebView = view;
        mContext = context;
    }

    /**
     * 파라미터가 없는 안드로이드 브릿지 웹 호출 클래스
     * @param String fName 호출할 함수명
     * @return String requestString javascript 프리픽스가 붙은 url
     */
    public static String call(String fName) {
        String requestString = "javascript:" + fName + "()";
        if(mWebView != null)
            mWebView.loadUrl(requestString);

        return requestString;
    }

    /**
     * 파라미터가 있는 안드로이드 브릿지 웹 호출 클래스
     * @param String fName 호출할 함수명
     * @param Utils.Pair params 파라미터 pair 배열. key에는 자료형이, value에는 값이 들어간다.
     * @return String requestString javascript 프리픽스가 붙은 url
     */
    public static String call(String fName, List<Utils.Pair> params) {
        String requestString = "javascript:" + fName + "(", splitter = "";
        for(Utils.Pair param : params) {
            String key = (param.getKey()).toUpperCase(), value = (param.getValue());
            switch(key) {
                case "STRING":
                case "STR":
                    value = "\"" + value + "\"";
                    break;
                case "INTEGER":
                case "INT":
                case "NUMBER":
                    value = value;
                    break;
                case "BOOL":
                case "BOOLEAN":
                    if(value.toUpperCase().contains("T"))
                        value = "true";
                    else
                        value = "false";
                    break;
                case "JSONOBJECT":
                case "JSON":
                    value = value;
                    break;
            }
            requestString += splitter + value;
            splitter = ", ";
        }
        requestString += ")";

        if(mWebView != null)
            mWebView.loadUrl(requestString);
        return requestString;
    }
}
