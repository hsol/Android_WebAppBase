package kr.cnttech.webappbase.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import kr.cnttech.webappbase.lib.Utils;

public abstract class BaseFragment extends Fragment implements OnClickListener {
    public View mView = null;
    public Context mContext = null;

    protected abstract int getBaseFragment();
    protected abstract void onInit();
    protected abstract void onClick(int viewId);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(getBaseFragment(), container, false);
        mContext = getBaseActivity().mContext;
        mView = layout;

        onInit();

        return layout;
    }

    @Override
    public void onClick(View v) {
        onClick(v.getId());
    }

    public void finish() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.remove(this);
        fragmentTransaction.commit();
    }
    public BaseActivity getBaseActivity() {
        return ((BaseActivity) getActivity());
    }
    public FragmentManager getBaseFragmentManager() {
        return getBaseActivity().getSupportFragmentManager();
    }
    public void addFragment(String fragmentTag, android.support.v4.app.Fragment newFragment, boolean isBack) {
        getBaseActivity().addFragment(fragmentTag, newFragment, isBack);
    }
    public void replaceFragment(String fragmentTag, android.support.v4.app.Fragment newFragment, boolean isBack) {
        getBaseActivity().replaceFragment(fragmentTag, newFragment, isBack);
    }
    public void removeFragment(String fragmentTag) {
        getBaseActivity().removeFragment(fragmentTag);
    }
    protected void onBackKeyDown(KeyEvent event) { Utils.Logger(mContext, "D", "KeyDown"); }

    public class BaseAsyncTask extends AsyncTask {
        @Override
        protected void onPreExecute() {}
        @Override
        protected Object doInBackground(Object[] params) { return null; }
    }
}
