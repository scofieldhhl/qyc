package com.systemteam;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.systemteam.bean.MyUser;
import com.systemteam.util.LogTool;
import com.systemteam.view.ProgressDialogHelper;

import java.lang.reflect.Field;

import cn.bmob.v3.exception.BmobException;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.systemteam.util.Utils.dp2px;


public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener{

    public int statusBarHeight = 0,titleHeight;
    protected Toolbar mToolbar;
    protected TextView mToolbarTitle;
    protected Context mContext;
    protected SharedPreferences mSharedPre;
    protected String params;
    public RequestQueue mQueue;
    protected ProgressDialogHelper mProgressHelper;
    protected boolean CheckNetwork = true;
    protected static boolean isOfflineResponse = false;
    protected InputMethodManager mImm;
    protected BikeApplication mApplication;
    protected MyUser mUser;
    protected ImageView mIvMenu, mIvSearch;
    protected TextView mTvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }*/
        statusBarHeight = getStatusBarHeight();
        titleHeight=dp2px(this,50);
    }

    protected void setStatusBarLayout() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final ViewGroup linear_bar = (ViewGroup) findViewById(R.id.title_layout);
            final int statusHeight = getStatusBarHeight();
            linear_bar.post(new Runnable() {
                @Override
                public void run() {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) linear_bar.getLayoutParams();
                    params.height = statusHeight ;
                    linear_bar.setLayoutParams(params);
                }
            });
        }
    }

    /**
     * 获取状态栏的高度
     *
     * @return
     */
    protected int getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void finishActivity(View view) {
        finish();
    }

    //bmob
    public static String TAG = "bmob";
    protected ListView mListview;
    protected BaseAdapter mAdapter;

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

    public void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    Toast mToast;

    public void showToast(String text) {
        if (!TextUtils.isEmpty(text)) {
            if (mToast == null) {
                mToast = Toast.makeText(getApplicationContext(), text,
                        Toast.LENGTH_SHORT);
            } else {
                mToast.setText(text);
            }
            mToast.show();
        }
    }

    public void showToast(int resId) {
        if (mToast == null) {
            mToast = Toast.makeText(getApplicationContext(), resId,
                    Toast.LENGTH_SHORT);
        } else {
            mToast.setText(resId);
        }
        mToast.show();
    }

    public static void log(String msg) {
        LogTool.d("===============================================================================");
        LogTool.d(msg);
    }

    public static void loge(Throwable e) {
        LogTool.d("===============================================================================");
        if(e instanceof BmobException){
            LogTool.e("错误码："+((BmobException)e).getErrorCode()+",错误描述："+((BmobException)e).getMessage());
        }else{
            LogTool.e("错误描述："+e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.mCompositeSubscription != null) {
            this.mCompositeSubscription.unsubscribe();
        }
    }

    protected void initToolBar(Activity act, int titleId) {
        mToolbar = (Toolbar) act.findViewById(R.id.toolbar);
        mToolbarTitle = (TextView) act.findViewById(R.id.toolbar_title);
        mToolbar.getVisibility();
        mToolbar.setTitle("");
        if (titleId == 0) {
            mToolbarTitle.setText("");
        } else {
            int titleColor = act.getResources().getColor(R.color.white);
            mToolbarTitle.setTextColor(titleColor);
            mToolbarTitle.setText(titleId);
        }


        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationIcon(R.mipmap.return_icon);
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected abstract void initView();

    protected abstract void initData();

    protected void initTitle(Activity act, int titleId, int menuRes, int searchRes) {
        setStatusBar();
        mIvMenu = (ImageView) act.findViewById(R.id.menu_icon);
        mIvSearch = (ImageView) act.findViewById(R.id.search_icon);
        mTvTitle = (TextView) act.findViewById(R.id.title);
        mIvMenu.setOnClickListener(this);
        mIvSearch.setOnClickListener(this);
        if (titleId == 0) {
            mTvTitle.setText("");
        } else {
            mTvTitle.setText(titleId);
        }
        if (menuRes == 0) {
            mIvMenu.setVisibility(View.GONE);
        } else {
            mIvMenu.setVisibility(View.VISIBLE);
            mIvMenu.setImageResource(menuRes);
        }
        if (searchRes == 0) {
            mIvSearch.setVisibility(View.GONE);
        } else {
            mIvSearch.setVisibility(View.VISIBLE);
            mIvSearch.setImageResource(searchRes);
        }
    }

    /**
     * 设置沉浸式状态栏
     */
    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final ViewGroup linear_bar = (ViewGroup) findViewById(R.id.title_layout);
            final int statusHeight = getStatusBarHeight();
            linear_bar.post(new Runnable() {
                @Override
                public void run() {
//                    int titleHeight = linear_bar.getHeight();
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) linear_bar.getLayoutParams();
                    params.height = statusHeight + titleHeight;
                    linear_bar.setLayoutParams(params);
                }
            });
        }
    }

}
