package com.example.webappbase.lib;

import android.content.Context;

import com.example.webappbase.R;

/**
 * Created by hansollim on 2016-07-26.
 */
public class Const {
    Context mContext = null;

    /**
     * 생성자
     * @param Context 해당 컨텍스트
     */
    public Const(Context context) { mContext = context; }

    /**
     * getResources 구현
     * @param int id
     * @return 각 resources value
     */
    private int getInteger(int id) { return mContext.getResources().getInteger(id); }
    private String getString(int id) { return mContext.getResources().getString(id); }
    private boolean getBoolean(int id) { return mContext.getResources().getBoolean(id); }

    public boolean isDev() { return getBoolean(R.bool.is_develop); }
    public boolean fingerPush() { return getBoolean(R.bool.is_contain_fingerpush); }

    public String rootUrl() { return getString(R.string.root_url); }
    public String rootUrl_dev() { return getString(R.string.root_url_test); }

    public boolean isPreloaderOn() { return getBoolean(R.bool.is_preloader_on); }
    public boolean isSplashOn() { return getBoolean(R.bool.is_splash_on); }
    public boolean isSpinnerOn() { return getBoolean(R.bool.is_spinner_on); }
}
