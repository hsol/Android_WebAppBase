package kr.cnttech.webappbase.base;

import android.content.Context;
import android.os.StrictMode;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import kr.cnttech.webappbase.lib.Utils;

/**
 * Created by hansollim on 2016-05-18.
 */
public class BaseURLConnection {
    public BaseURLConnection() {}
    public BaseURLConnection(boolean isStrict){
        if(isStrict)
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
    }

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
            Toast.makeText(context, "[getJsonFromHttpUrlConnection]: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return null;
    }

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
