package kr.cnttech.webappbase.common;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.fingerpush.android.FingerPushManager;

import kr.cnttech.webappbase.R;
import kr.cnttech.webappbase.base.BaseActivity;
import kr.cnttech.webappbase.lib.Const;
import kr.cnttech.webappbase.lib.Utils;

/**
 * Created by hansollim on 2016-07-26.
 */
public class MainActivity extends BaseActivity {
    @Override
    public void onInit() {
        super.onInit();

    }

    @Override
    protected void onFirstLaunch() {
        Utils.Logger(mContext, "D", getString(R.string.when_first_load));
    }

    @Override
    protected void onNetworkOff() {
        Toast.makeText(mContext, getString(R.string.app_name) + " : " + getString(R.string.error_no_network), Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    protected void onNetworkOn() {
        addFragment("SplashFragment", new SplashFragment(), false);
    }

    @Override
    protected void onScheme(Uri uri) {

    }
}
