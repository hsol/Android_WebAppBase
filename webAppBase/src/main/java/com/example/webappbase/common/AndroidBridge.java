/*
* Copyright 2016 HansolLim
* Released under the MIT license
* http://hsol.github.io/
*/

package com.example.webappbase.common;
import android.content.Context;
import android.content.SharedPreferences;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.example.webappbase.R;
import com.example.webappbase.base.BaseBridge;
import com.example.webappbase.lib.Utils;
import com.fingerpush.android.FingerPushManager;
import com.fingerpush.android.NetworkUtility;
import com.fingerpush.android.dataset.TagList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hansollim on 2016-08-04.
 */
public class AndroidBridge extends BaseBridge {
    public AndroidBridge(Context context, WebView view) {
        super(context, view);
    }

    @JavascriptInterface
    public void getDeviceSetting() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = mContext.getSharedPreferences(mContext.getString(R.string.SETTINGS), 0);

                List<Utils.Pair> params = new ArrayList<>();
                params.add(new Utils.Pair("String", pref.getString(mContext.getString(R.string.ATTR_PUSH), "N")));
                call("setDeviceSetting", params);

                Utils.Logger(mContext, "D", "getDeviceSetting: " + params.get(0).getValue());
            }
        });
    }

    @JavascriptInterface
    public void putDeviceSetting(final String isAccept) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref = mContext.getSharedPreferences(mContext.getString(R.string.SETTINGS), 0);
                pref.edit().putString(mContext.getString(R.string.ATTR_PUSH), isAccept).apply();

                FingerPushManager pushManager = FingerPushManager.getInstance(mContext);
                pushManager.setPushAlive(
                        isAccept.equals("Y"),
                        new NetworkUtility.ObjectListener() {
                            @Override
                            public void onComplete(String s, String s1, JSONObject jsonObject) {
                                Utils.Logger(mContext, "D", "putDeviceSetting: " + isAccept);
                            }

                            @Override
                            public void onError(String s, String s1) {

                            }
                        }
                );
            }
        });
    }

    @JavascriptInterface
    public void setDeviceTag(final String deviceTag) {
        handler.post(new Runnable() {
            public void run() {
                setDeviceTag(deviceTag, "");
            }
        });
    }

    @JavascriptInterface
    public void setDeviceTag(final String deviceTagKey, final String deviceTagData) {
        handler.post(new Runnable() {
            public void run() {
                Utils.Logger("setDeviceTag", mContext, "D", "key: " + deviceTagKey + "data: " + deviceTagData);

                FingerPushManager.getInstance(mContext).getDeviceTag(
                        new NetworkUtility.ObjectListener() { // 비동기 이벤트 리스너

                            @Override
                            public void onError(String code, String message) {
                                // TODO Auto-generated method stub
                                Utils.Logger("FingerPush", mContext, "E", "onError: code : " + code + ", message : " + message);
                            }

                            @Override
                            public void onComplete(String code, String message, JSONObject data) {
                                // TODO Auto-generated method stub
                                Utils.Logger("FingerPush", mContext, "D", "onComplete: code : " + code + ", message : " + message);
                                Boolean isExist = false;

                                try {
                                    JSONArray ArrayData = data.getJSONArray(TagList.TAGLIST);
                                    if(ArrayData != null) {
                                        for (int i = 0; i < ArrayData.length(); i++) {
                                            if (ArrayData.getJSONObject(i).optString("tag").equals(deviceTagKey + (!deviceTagData.equals("") ? "|" + deviceTagData : "")))
                                                isExist = true;
                                        }
                                    }
                                } catch (JSONException e) {
                                    //e.printStackTrace();
                                    Utils.Logger("FingerPush", mContext, "D", "NoData");
                                }

                                if(!isExist) {
                                    FingerPushManager.getInstance(mContext).setTag(
                                            deviceTagKey + (!deviceTagData.equals("") ? "|" + deviceTagData : ""), // 태그값
                                            new NetworkUtility.ObjectListener() { // 비동기 이벤트 리스너
                                                @Override
                                                public void onError(String code, String message) {
                                                    // TODO Auto-generated method stub
                                                    Utils.Logger("FingerPush", mContext, "E", "onError: code : " + code + ", message : " + message);
                                                }

                                                @Override
                                                public void onComplete(String code, String message, JSONObject data) {
                                                    // TODO Auto-generated method stub
                                                    Utils.Logger("FingerPush", mContext, "D", "onComplete: code : " + code + ", message : " + message);

                                                    List<Utils.Pair> params = new ArrayList<>();
                                                    params.add(new Utils.Pair("String", "setDeviceTag"));
                                                    params.add(new Utils.Pair("String", deviceTagKey + (!deviceTagData.equals("") ? "|" + deviceTagData : "")));
                                                    call("fingerPush", params);
                                                }
                                            }
                                    );
                                } else {
                                    List<Utils.Pair> params = new ArrayList<>();
                                    params.add(new Utils.Pair("String", "setDeviceTag"));
                                    params.add(new Utils.Pair("String", deviceTagKey + (!deviceTagData.equals("") ? "|" + deviceTagData : "")));
                                    call("fingerPush", params);
                                }
                            }
                        }
                );
            }
        });
    }

    @JavascriptInterface
    public void removeDeviceTag(final String deviceTag) {
        handler.post(new Runnable() {
            public void run() {
                removeDeviceTag(deviceTag, "");
            }
        });
    }

    @JavascriptInterface
    public void removeDeviceTag(final String deviceTagKey, final String deviceTagData) {
        handler.post(new Runnable() {
            public void run() {
                Utils.Logger("removeDeviceTag", mContext, "D", "key: " + deviceTagKey + "data: " + deviceTagData);

                FingerPushManager.getInstance(mContext).getDeviceTag(
                        new NetworkUtility.ObjectListener() { // 비동기 이벤트 리스너
                            @Override
                            public void onError(String code, String message) {
                                // TODO Auto-generated method stub
                                Utils.Logger("FingerPush", mContext, "E", "onError: code : " + code + ", message : " + message);
                            }

                            @Override
                            public void onComplete(String code, String message, JSONObject data) {
                                // TODO Auto-generated method stub
                                Utils.Logger("FingerPush", mContext, "D", "onComplete: code : " + code + ", message : " + message);
                                Boolean isExist = false;

                                try {
                                    JSONArray ArrayData = data.getJSONArray(TagList.TAGLIST);
                                    if(ArrayData != null) {
                                        for (int i = 0; i < ArrayData.length(); i++) {
                                            if(ArrayData.getJSONObject(i).optString("tag").equals(deviceTagKey + (!deviceTagData.equals("") ? "|" + deviceTagData : "")))
                                                isExist = true;
                                        }
                                    }

                                } catch (JSONException e) {
                                    //e.printStackTrace();
                                    Utils.Logger("FingerPush", mContext, "D", "NoData");
                                }

                                if(isExist) {
                                    FingerPushManager.getInstance(mContext).removeTag(
                                            deviceTagKey + (!deviceTagData.equals("") ? "|" + deviceTagData : ""), // 태그값
                                            new NetworkUtility.ObjectListener() { // 비동기 이벤트 리스너
                                                @Override
                                                public void onError(String code, String message) {
                                                    // TODO Auto-generated method stub
                                                    Utils.Logger("FingerPush", mContext, "E", "onError: code : " + code + ", message : " + message);
                                                }

                                                @Override
                                                public void onComplete(String code, String message, JSONObject data) {
                                                    // TODO Auto-generated method stub
                                                    Utils.Logger("FingerPush", mContext, "D", "onComplete: code : " + code + ", message : " + message);

                                                    List<Utils.Pair> params = new ArrayList<>();
                                                    params.add(new Utils.Pair("String", "removeDeviceTag"));
                                                    params.add(new Utils.Pair("String", deviceTagKey + (!deviceTagData.equals("") ? "|" + deviceTagData : "")));
                                                    call("fingerPush", params);
                                                }
                                            }
                                    );
                                } else {
                                    List<Utils.Pair> params = new ArrayList<>();
                                    params.add(new Utils.Pair("String", "removeDeviceTag"));
                                    params.add(new Utils.Pair("String", deviceTagKey + (!deviceTagData.equals("") ? "|" + deviceTagData : "")));
                                    call("fingerPush", params);
                                }
                            }
                        }
                );
            }
        });
    }

    @JavascriptInterface
    public void removeDeviceTagByKey(final String deviceTagKey) {
        handler.post(new Runnable() {
            public void run() {
                FingerPushManager.getInstance(mContext).getDeviceTag(
                        new NetworkUtility.ObjectListener() { // 비동기 이벤트 리스너
                            @Override
                            public void onError(String code, String message) {
                                // TODO Auto-generated method stub
                                Utils.Logger("FingerPush", mContext, "E", "onError: code : " + code + ", message : " + message);
                            }

                            @Override
                            public void onComplete(String code, String message, JSONObject data) {
                                // TODO Auto-generated method stub
                                Utils.Logger("FingerPush", mContext, "D", "onComplete: code : " + code + ", message : " + message);
                                Boolean isExist = false;

                                try {
                                    JSONArray ArrayData = data.getJSONArray(TagList.TAGLIST);
                                    ArrayList<String> tagList = new ArrayList<>();

                                    if(ArrayData != null) {
                                        for (int i = 0; i < ArrayData.length(); i++) {
                                            if(ArrayData.getJSONObject(i).optString("tag").contains(deviceTagKey + "|"))
                                                tagList.add(ArrayData.getJSONObject(i).optString("tag"));
                                        }
                                    }

                                    if(tagList.size() > 0) {
                                        final int[] listSize = {tagList.size()};

                                        for(String deviceTag : tagList) {
                                            FingerPushManager.getInstance(mContext).removeTag(
                                                    deviceTag, // 태그값
                                                    new NetworkUtility.ObjectListener() { // 비동기 이벤트 리스너
                                                        @Override
                                                        public void onError(String code, String message) {
                                                            // TODO Auto-generated method stub
                                                            Utils.Logger("FingerPush", mContext, "E", "onError: code : " + code + ", message : " + message);
                                                        }

                                                        @Override
                                                        public void onComplete(String code, String message, JSONObject data) {
                                                            // TODO Auto-generated method stub
                                                            Utils.Logger("FingerPush", mContext, "D", "onComplete: code : " + code + ", message : " + message);
                                                            if(--listSize[0] <= 0) {

                                                                List<Utils.Pair> params = new ArrayList<>();
                                                                params.add(new Utils.Pair("String", "removeDeviceTagByKey"));
                                                                params.add(new Utils.Pair("String", deviceTagKey));
                                                                call("fingerPush", params);
                                                            }
                                                        }
                                                    }
                                            );
                                        }
                                    } else {
                                        List<Utils.Pair> params = new ArrayList<>();
                                        params.add(new Utils.Pair("String", "removeDeviceTagByKey"));
                                        params.add(new Utils.Pair("String", deviceTagKey));
                                        call("fingerPush", params);
                                    }
                                } catch (JSONException e) {
                                    //e.printStackTrace();
                                    Utils.Logger("FingerPush", mContext, "D", "NoData");

                                    List<Utils.Pair> params = new ArrayList<>();
                                    params.add(new Utils.Pair("String", "removeDeviceTagByKey"));
                                    params.add(new Utils.Pair("String", deviceTagKey));
                                    call("fingerPush", params);
                                }
                            }
                        }
                );
            }
        });
    }

    @JavascriptInterface
    public void setDeviceIdentity(final String identity) {
        handler.post(new Runnable() {
            public void run() {
                if(identity != null && !identity.equals(""))
                    FingerPushManager.getInstance(mContext).setIdentity(
                            identity,
                            new NetworkUtility.ObjectListener() {
                                @Override
                                public void onComplete(String code, String message, JSONObject jsonObject) {
                                    Utils.Logger("FingerPush", mContext, "D", "setIdentity : " + code + " | mesg : " + message);

                                    List<Utils.Pair> params = new ArrayList<>();
                                    params.add(new Utils.Pair("String", "setDeviceIdentity"));
                                    params.add(new Utils.Pair("String", identity));
                                    call("fingerPush", params);
                                }
                                @Override
                                public void onError(String code, String message) {
                                    Utils.Logger("FingerPush", mContext, "E", "setIdentity : " + code + " | mesg : " + message);
                                }
                            }
                    );
            }
        });
    }
}
