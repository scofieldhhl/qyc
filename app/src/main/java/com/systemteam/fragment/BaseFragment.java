package com.systemteam.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.systemteam.bean.MyUser;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/11/15 11:46
 */

public abstract class BaseFragment extends Fragment implements View.OnClickListener{
    protected MyUser mUser;
    protected Context mContext;
    protected InputMethodManager mImm;
    private CompositeSubscription mCompositeSubscription;


    /**
     * 解决Subscription内存泄露问题
     * @param s
     */
    protected void addSubscription(Subscription s) {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }
        this.mCompositeSubscription.add(s);
    }

    public void toast(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    protected abstract void initView(View view);
    protected abstract void initData();
}
