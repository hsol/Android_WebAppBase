/*
* Copyright 2016 HansolLim
* Released under the MIT license
* http://hsol.github.io/
*/

package com.example.webappbase.common;

import android.net.Uri;
import android.widget.Toast;

import com.example.webappbase.R;
import com.example.webappbase.base.BaseActivity;
import com.example.webappbase.lib.Utils;

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
