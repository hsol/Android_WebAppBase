package kr.cnttech.webappbase.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.View;

import kr.cnttech.webappbase.R;

/**
 * Created by hansollim on 2016-07-29.
 */
public class BasePreLoader extends BaseFragment{
    ProgressDialog progDialog = null;

    @Override
    protected int getBaseFragment() {
        return R.layout.progress_circle;
    }

    @Override
    protected void onInit() { }

    @Override
    protected void onClick(int viewId) {

    }

    public void show() {
        mView.findViewById(R.id.progress_body).setVisibility(View.VISIBLE);
        progDialog = new ProgressDialog(getActivity());
        progDialog.setMessage("Loading...");
        progDialog.setIndeterminate(false);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setCancelable(true);
        progDialog.show();
    }
    public void dismiss() {
        mView.findViewById(R.id.progress_body).setVisibility(View.GONE);
        progDialog.dismiss();
    }
}
