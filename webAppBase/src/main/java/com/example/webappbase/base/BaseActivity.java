/*
* Copyright 2016 HansolLim
* Released under the MIT license
* http://hsol.github.io/
*/

package com.example.webappbase.base;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.Toast;

import com.fingerpush.android.FingerPushManager;
import com.fingerpush.android.NetworkUtility;

import org.json.JSONObject;

import java.util.List;

import com.example.webappbase.R;
import com.example.webappbase.lib.AndroidBug5497Workaround;
import com.example.webappbase.lib.Const;
import com.example.webappbase.lib.Utils;

public abstract class BaseActivity extends FragmentActivity {
    protected int baseContentView = R.layout.activity_base;
    public Const mValue = null;

    protected FingerPushManager pushManager;
    protected Context mContext = null;
    protected Intent mIntent = null;
    protected abstract void onFirstLaunch();
    protected abstract void onNetworkOff();
    protected abstract void onNetworkOn();
    protected abstract void onScheme(Uri uri);

    /**
     * [onCreate] Activity 의 onCreate
     * Const, Context, Intent 초기화, 푸시에 의한 호출일 경우 onInitByPushMessage 호출
     *
     * 호출 메소드: onInit(), onInitByPushMessage()
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mValue = new Const(this);
        mContext = this;
        mIntent = getIntent();

        onInit();

        if(mIntent.getBooleanExtra(getString(R.string.boot_by_push_message), false)) onInitByPushMessage();

        setContentView(baseContentView);

        // 안드로이드 키보드 레이아웃 버그를 해결해주는 라이브러리 입니다.
        AndroidBug5497Workaround.assistActivity(this);
    }

    public void onInitByPushMessage() {
        Utils.Logger(mContext, "D", getString(R.string.boot_by_push_message));
    }

    /**
     * [onInit] Activity 의 onCreate 에서 호출하는 메소드.
     * BaseActivity 에서는 첫 실행 체크, 네트워크 체크, 스키마 체크를 기본으로 진행합니다.
     *
     * 호출 메소드: onFirstLaunch(), onNetworkOn(), onNetworkOff(), onScheme(Uri uri)
     */
    public void onInit() {
        try {
            SharedPreferences pref = getSharedPreferences(getString(R.string.VER), MODE_PRIVATE);
            PackageManager pm = mContext.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), MODE_PRIVATE);
            int latestVersion = packageInfo.versionCode;
            int currentVersion = pref.getInt(getString(R.string.VER_CURRENT), MODE_PRIVATE);
            if (currentVersion < latestVersion) {
                SharedPreferences.Editor edit = pref.edit();
                edit.putInt(getString(R.string.VER_CURRENT), latestVersion);
                edit.commit();

                Utils.Logger(mContext, "D", "First Launch.");
                onFirstLaunch();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Utils.Logger(mContext, "D", "networkType: " + (getNetworkState() != null ? getNetworkState() : "none"));
        if (getNetworkState() == null) {
            onNetworkOff();
        } else {
            onNetworkOn();
            if(mValue.fingerPush()) {
                pushManager = FingerPushManager.getInstance(this);
                if(mValue.isFingerUseAuth())
                    checkPermission();
                else
                    setDevice();
            }
        }

        if(getIntent() != null) {
            Uri uri = getIntent().getData();
            if(uri != null)
                onScheme(uri);
        }
    }

    /**
     * [onKeyDown] keydown 이벤트.
     * BaseActivity 에서는 visible Fragment 로 onBackKeyDown 이벤트를 보냅니다.
     *
     * 호출 메소드: onBackKeyDown(Event event)
     * return super
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(this.getVisibleFragment() != null)
                ((BaseFragment) this.getVisibleFragment()).onBackKeyDown(event);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * [getNetworkState] 네트워크 상태 반환 메소드.
     * 데이터로 연결할 때는 mobile, wifi로 연결할 때는 wifi, 그 외는 null 을 보낸다.
     *
     * return String typeName
     */
    public String getNetworkState() {
        String typeName = null;
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                typeName = activeNetwork.getTypeName();
            }
        }
        return typeName;
    }

    /**
     * [addFragment] 프레그먼트 추가 메소드.
     * 액티비티 프레그먼트 매니저에 새로운 프레그먼트 추가. addToBackStack 여부를 조정 가능
     *
     * String fragmentTag fragment 의 tag에 들어갈 문구
     * Fragment newFragment 새로 추가될 fragment
     * boolean isBack addToBackStack 여부
     *
     */
    public void addFragment(String fragmentTag, android.support.v4.app.Fragment newFragment, boolean isBack) {
        try {
            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.baseFragment, newFragment, fragmentTag);
            if(isBack)
                transaction.addToBackStack(fragmentTag);
            transaction.commitAllowingStateLoss();

            Utils.Logger(mContext, "D", getString(R.string.fragment_add_complete) + ":" + fragmentTag);
        } catch (Error e) {
            Utils.Logger(mContext, "E", e.getMessage());
        }
    }

    /**
     * [replaceFragment] 프레그먼트 교체 메소드.
     * 액티비티 프레그먼트 매니저에 visible fragment를 교체. addToBackStack 여부를 조정 가능
     *
     * String fragmentTag fragment 의 tag에 들어갈 문구
     * Fragment newFragment 새로 추가될 fragment
     * boolean isBack addToBackStack 여부
     *
     */
    public void replaceFragment(String fragmentTag, android.support.v4.app.Fragment newFragment, boolean isBack) {
        try {
            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (getVisibleFragment() != null) {
                Utils.Logger(mContext, "D", getString(R.string.fragment_replace_complete) + ":" + fragmentTag);
                transaction.replace(R.id.baseFragment, newFragment, fragmentTag);
                if(isBack)
                    transaction.addToBackStack(fragmentTag);
                transaction.commitAllowingStateLoss();
            } else {
                Utils.Logger(mContext, "W", getString(R.string.fragment_replace_warning));
                addFragment(fragmentTag, newFragment, isBack);
            }
        } catch (Error e) {
            Utils.Logger(mContext, "E", e.getMessage());
        }
    }

    /**
     * [removeFragment] 프레그먼트 삭제 메소드.
     * 액티비티 프레그먼트 매니저에 해당하는 fragment 를 삭제
     *
     * String fragmentTag 삭제될 fragment 의 tag
     *
     */
    public void removeFragment(String fragmentTag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if(fragment != null) {
            transaction.remove(fragment);
            transaction.commitAllowingStateLoss();
            Utils.Logger(mContext, "D", getString(R.string.fragment_remove_complete) + ":" + fragmentTag);
        }
    }

    /**
     * [getFragment] 프레그먼트 호출 메소드.
     * 액티비티 프레그먼트 매니저에 해당하는 fragment 를 반환
     *
     * String fragmentTag 반환할 fragment 의 태그
     * return Fragment fragment
     *
     */
    public Fragment getFragment(String fragmentTag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
        return fragment;
    }

    /**
     * [getVisibleFragment] 활성프레그먼트 호출 메소드.
     * 활성화되어있는 Fragment 를 반환
     *
     * return Fragment topFragment 활성화된 Fragment
     *
     */
    public Fragment getVisibleFragment(){
        Fragment topFragment = null;
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if(fragments != null){
            for(Fragment fragment : fragments){
                if(fragment != null && fragment.isVisible())
                    topFragment = fragment;
            }
        }
        return topFragment;
    }

    /**
     * FingerPush Methods
     */
    private void setDevice() {
        pushManager.setDevice(new NetworkUtility.ObjectListener() {
            @Override
            public void onComplete(String code, String message, JSONObject jsonObject) {
                pushManager.getInstance(mContext).setIdentity(
                        Utils.getUniqueId(mContext), // 식별자 값으로 Application 개발자가 임의 지정하여 사용한다. 예) 유저 아이디, 이메일 등
                        new NetworkUtility.ObjectListener() {
                            @Override
                            public void onComplete(String code, String message, JSONObject jsonObject) {
                                Utils.Logger("FingerPush", mContext, "D", "setdevice : " + code + " | mesg : " + message);
                            }
                            @Override
                            public void onError(String code, String message) {
                                Utils.Logger("FingerPush", mContext, "E", "setdevice : " + code + " | mesg : " + message);
                            }
                        }
                );
            }

            @Override
            public void onError(String code, String message) {
                Utils.Logger("FingerPush", mContext, "E", "setdevice : " + code + " | mesg : " + message);
            }
        });
    }
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // Explain to the user why we need to write the permission.
                    Toast.makeText(this, "Read/Write external storage", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
                // MY_PERMISSION_REQUEST_STORAGE is an
                // app-defined int constant

            } else {
                // 다음 부분은 항상 허용일 경우에 해당이 됩니다.
                setDevice();
            }
        } else {
            setDevice();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1001:
                if(grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        setDevice();
                        // permission was granted, yay! do the
                        // calendar task you need to do.
                    } else {
                        Utils.Logger("FingerPush", mContext, "E", "Permission always deny");
                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                    }
                }
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }
}
