package kr.cnttech.webappbase.common;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import kr.cnttech.webappbase.R;
import kr.cnttech.webappbase.base.BaseFragment;
import kr.cnttech.webappbase.base.BaseURLConnection;
import kr.cnttech.webappbase.lib.Utils;

/**
 * Created by hansollim on 2016-07-26.
 */
public class PreloaderFragment extends BaseFragment {
    @Override
    protected int getBaseFragment() { return R.layout.fragment_splash; }

    @Override
    protected void onInit() {
    }

    @Override
    protected void onClick(int viewId) {

    }
}
