package kr.cnttech.webappbase.lib;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import kr.cnttech.webappbase.R;

/**
 * Created by hansollim on 2016-07-26.
 */
public class Const {
    public static boolean isDev = true;
    public static boolean fingerPush = true;

    public static String rootUrl = "http://cntt.co.kr/";
    public static String rootUrl_dev = "http://cntt.co.kr/";
}
