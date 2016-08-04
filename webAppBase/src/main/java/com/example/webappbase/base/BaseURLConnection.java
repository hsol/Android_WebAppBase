package com.example.webappbase.base;

import android.content.Context;
import android.os.StrictMode;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import com.example.webappbase.lib.Utils;

/**
 * Created by hansollim on 2016-05-18.
 */
public class BaseURLConnection {
    public BaseURLConnection() {}
    /**
     * [BaseURLConnection] strict 모드 설정용 메소드. 메인 스레드에서 호출 시 true로 설정한다.
     *
     * boolean isStrict strict 모드 허용 여부
     *
     */
    public BaseURLConnection(boolean isStrict){
        if(isStrict)
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
    }

    /**
     * [getResponseFromHttpUrlConnection] HttpURLConnection http 통신 모듈
     *
     * Context context 호출될 context
     * Utils.HttpObject httpObject 옵션 객체
     *
     */
    public static String getResponseFromHttpUrlConnection(Context context, Utils.HttpObject httpObject){
        try{
            URL url = new URL(httpObject.getUrlString());

            HttpURLConnection conn = null;

            OutputStream os = null;
            InputStream is = null;
            ByteArrayOutputStream baos = null;

            conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod(httpObject.getMethod());

            conn.setConnectTimeout(1 * 1000);
            conn.setReadTimeout(1 * 1000);

            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            if(httpObject.getHeaders() != null) {
                for (Utils.Pair pair : httpObject.getHeaders()) {
                    conn.setRequestProperty(pair.getKey(), pair.getValue());
                }
            }

            if(httpObject.getParams() != null) {
                os = conn.getOutputStream();
                //BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                //writer.write(getQuery(params));
                //writer.flush();
                os.write(getQuery(httpObject.getParams()).getBytes());
                os.flush();
                os.close();
            }
            String response;

            int responseCode = conn.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK) {

                is = conn.getInputStream();
                baos = new ByteArrayOutputStream();
                byte[] byteBuffer = new byte[1024];
                byte[] byteData = null;
                int nLength = 0;
                while((nLength = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                    baos.write(byteBuffer, 0, nLength);
                }
                byteData = baos.toByteArray();

                response = new String(byteData);
                return response;
            }
        } catch(Exception e){
            Utils.Logger("getJsonFromHttpUrlConnection", context, "E", e.getMessage());
        }
        return null;
    }

    /**
     * [getQuery] Utils.Pair List를 쿼리형태로 변환
     *
     * List<Utils.Pair> params 파라미터 리스트
     *
     * return String result URL 쿼리로 변환된 파라미터
     *
     */
    private static String getQuery(List<Utils.Pair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Utils.Pair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }
        return result.toString();
    }
}
