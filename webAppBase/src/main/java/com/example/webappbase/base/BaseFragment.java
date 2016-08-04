package com.example.webappbase.base;

import android.app.Activity;
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

import com.example.webappbase.lib.Const;
import com.example.webappbase.lib.Utils;

public abstract class BaseFragment extends Fragment implements OnClickListener {
    public View mView = null;
    public Context mContext = null;
    public Const mValue = null;

    protected abstract int getBaseFragment();
    protected abstract void onInit();
    protected abstract void onClick(int viewId);

    /**
     * [onCreateView] 프레그먼트 첫 실행 메소드
     * BaseFragment 에서는 fragment 에 해당하는 view와 context를 지정해준다.
     *
     * return View layout fragment 가 적용되는 View
     *
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(getBaseFragment(), container, false);
        mContext = getBaseActivity().mContext;
        mView = layout;
        mValue = getBaseActivity().mValue;

        onInit();

        return layout;
    }

    /**
     * [onClick] 프레그먼트 onClick 메소드
     * 기본 onClick 메소드 상속
     */
    @Override
    public void onClick(View v) {
        onClick(v.getId());
    }

    /**
     * [finish] 프레그먼트 종료 메소드
     * activity 의 finish 와 동일한 기능 수행. 메소드 호출된 부모 프레그먼트 종료.
     */
    public void finish() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.remove(this);
        fragmentTransaction.commit();
    }

    /**
     * [getBaseActivity] 프레그먼트가 해당되는 activity 반환
     *
     * return BaseActivity
     *
     */
    public BaseActivity getBaseActivity() {
        return ((BaseActivity) (mContext != null ? (Activity) mContext : getActivity()));
    }

    /**
     * [getBaseFragmentManager] 프레그먼트가 해당되는 FragmentManager 반환
     *
     * return BaseActivity
     *
     */
    public FragmentManager getBaseFragmentManager() {
        return getBaseActivity().getSupportFragmentManager();
    }

    /**
     * [addFragment] 프레그먼트의 activity 에 fragment 추가
     */
    public void addFragment(String fragmentTag, android.support.v4.app.Fragment newFragment, boolean isBack) {
        getBaseActivity().addFragment(fragmentTag, newFragment, isBack);
    }
    /**
     * [replaceFragment] 프레그먼트의 activity 에 fragment 교체
     */
    public void replaceFragment(String fragmentTag, android.support.v4.app.Fragment newFragment, boolean isBack) {
        getBaseActivity().replaceFragment(fragmentTag, newFragment, isBack);
    }
    /**
     * [removeFragment] 프레그먼트의 activity 에 fragment 삭제
     */
    public void removeFragment(String fragmentTag) {
        getBaseActivity().removeFragment(fragmentTag);
    }
    /**
     * [onBackKeyDown] activity onKeyDown 에서 호출하는 메소드
     * 물리 back 버튼 클릭 시 호출된다.
     */
    protected void onBackKeyDown(KeyEvent event) { Utils.Logger(mContext, "D", "KeyDown"); }

    public class BaseAsyncTask extends AsyncTask {
        @Override
        protected void onPreExecute() {}
        @Override
        protected Object doInBackground(Object[] params) { return null; }
    }
}
