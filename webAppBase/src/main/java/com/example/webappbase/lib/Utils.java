package com.example.webappbase.lib;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.webappbase.R;

/**
 * Created by hansollim on 2016-07-26.
 */
public class Utils {
    private static SharedPreferences prefs;
    private static Intent intent;

    public static void Logger(Context context, String type, String message) {
        Logger(null, context, type, message);
    }
    public static void Logger(String header, Context context, String type, String message) {
        String logHead = (context.getString(R.string.app_name)) + ":";
        header = header == null ? "" : (" " + header);
        message = message == null ? "" : message;
        switch(type) {
            case "D":
                if(new Const(context).isDev())
                    Log.d(logHead + context.getResources().getString(R.string.log_develop) + header, message);
                break;
            case "I":
                if(new Const(context).isDev())
                    Log.i(logHead + context.getResources().getString(R.string.log_info) + header, message);
                break;
            case "E":
                Log.e(logHead + context.getResources().getString(R.string.log_error) + header, message);
                break;
            case "W":
                Log.w(logHead + context.getResources().getString(R.string.log_warning) + header, message);
                break;
            case "V":
                Log.v(logHead + context.getResources().getString(R.string.log_verbose) + header, message);
                break;
        }
    }

    public static class Pair{
        private String key = null;
        private String value = null;
        public Pair(){

        }
        public Pair(String key, String value){
            this.key = key;
            this.value = value;
        }
        public String getKey(){
            return this.key;
        }
        public String getValue(){
            return this.value;
        }
        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class HttpObject {
        private String _method = null;
        private String _urlString = null;
        private List<Pair> _headers = new ArrayList<>();
        private List<Pair> _params = new ArrayList<>();

        public String getMethod() { return _method; }
        public String getUrlString() { return _urlString; }
        public Uri getUri() { return Uri.parse(_urlString); }
        public List<Pair> getHeaders() { return _headers; }
        public List<Pair> getParams() { return _params; }

        public void setMethod(String method) { _method = method; }
        public void setUrlString(String urlString) { _urlString = urlString; }
        public void setUri(Uri url) { _urlString = url.toString(); }
        public void setHeaders(List<Pair> headers) { _headers = headers; }
        public void addHeader(Pair header) { _headers.add(header); }
        public void addHeaders(List<Pair> headers) {
            for(Pair header : headers) {
                _headers.add(header);
            }
        }
        public void setParam(List<Pair> params) { _params = params; }
        public void addParam(Pair param) { _params.add(param); }
        public void addParams(List<Pair> params) {
            for(Pair param : params) {
                _headers.add(param);
            }
        }
    }

    public static String getPhoneNumber(Context context) {
        String phoneNumber = "";
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_DENIED) {
            TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            phoneNumber = tMgr.getLine1Number();
        }
        return phoneNumber;
    }

    public static String getDeviceId(Context context) {
        String deviceId = "";
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_DENIED) {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            final String tmDevice, wmMacAddr;

            tmDevice = "" + tm.getDeviceId();
            wmMacAddr = "" + wm.getConnectionInfo().getMacAddress();

            UUID deviceUuid = new UUID(tmDevice.hashCode(), ((long) tmDevice.hashCode() << 32) | wmMacAddr.hashCode());
            deviceId = deviceUuid.toString();
        }
        return deviceId;
    }

    public static String getDeviceShortId(Context context) {
        String deviceId = "";
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_DENIED) {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            final String tmDevice, wmMacAddr;

            tmDevice = "" + tm.getDeviceId();
            wmMacAddr = "" + wm.getConnectionInfo().getMacAddress();

            UUID deviceUuid = new UUID(tmDevice.hashCode(), ((long) tmDevice.hashCode() << 32) | wmMacAddr.hashCode());
            deviceId = deviceUuid.toString();
            deviceId = deviceId.substring(deviceId.length() - 20);
        }
        return deviceId;
    }

    public static String getVersion(Context context) {
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return pInfo.versionName;
    }

    public static boolean compareVersion(String localVer, String latestVer) {
        float local = Float.parseFloat(localVer);
        float latest = Float.parseFloat(latestVer);

        if (local >= latest)
            return true;
        else
            return false;
    }

    public static boolean getWifiConnected(Context context) {
        ConnectivityManager cManager;
        NetworkInfo wifi;

        cManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        wifi = cManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return wifi.isConnected() ? true : false;
    }

    private static boolean isInstalledApp(Context context, String packageName) {
        List<ApplicationInfo> appList = context.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo appInfo : appList) {
            if (appInfo.packageName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    private static void initPrefs(Context context) {
        prefs = context.getSharedPreferences(context.getPackageName(),
                Context.MODE_PRIVATE);
    }

    public static void setIconBadgeCount(Context context, int count) {
        initPrefs(context);

        prefs.edit().putInt(context.getString(R.string.BADGE_COUNT), count)
                .commit();

        if (getDeviceName().toUpperCase().contains("SONY")) {
            intent = new Intent("com.sonyericsson.home.action.UPDATE_BADGE");
            intent.putExtra(
                    "com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME",
                    getLauncherClassName(context));
            intent.putExtra(
                    "com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE",
                    true);
            intent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE",
                    String.valueOf(count));
            intent.putExtra(
                    "com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME",
                    context.getPackageName());
        } else {
            intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
            intent.putExtra("badge_count_package_name",
                    context.getPackageName());
            intent.putExtra("badge_count_class_name",
                    getLauncherClassName(context));
            intent.putExtra("badge_count", count);
        }

        // Version이 3.1이상일 경우에는 Flags를 설정하여 준다.
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            intent.setFlags(0x00000020);
        }

        // send
        context.sendBroadcast(intent);
    }

    public static int getIconBadgeCount(Context context) {
        initPrefs(context);
        return prefs.getInt(context.getString(R.string.BADGE_COUNT), 0);
    }

    public static void addIconBadgeCount(Context context) {
        int preCount = getIconBadgeCount(context);
        setIconBadgeCount(context, preCount + 1);
    }

    public static void addIconBadgeCount(Context context, int count) {
        int preCount = getIconBadgeCount(context);
        setIconBadgeCount(context, preCount + count);
    }

    public static void subIconBadgeCount(Context context) {
        int preCount = getIconBadgeCount(context);
        setIconBadgeCount(context, (preCount - 1 > 0 ? preCount - 1 : 0));
    }

    public static void subIconBadgeCount(Context context, int count) {
        int preCount = getIconBadgeCount(context);
        setIconBadgeCount(context, (preCount - count > 0 ? preCount - count : 0));
    }

    private static String getLauncherClassName(Context context) {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setPackage(context.getPackageName());

        List<ResolveInfo> resolveInfoList = context.getPackageManager()
                .queryIntentActivities(intent, 0);
        if (resolveInfoList != null && resolveInfoList.size() > 0) {
            return resolveInfoList.get(0).activityInfo.name;
        }
        return "";
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
}
