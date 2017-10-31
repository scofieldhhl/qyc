package com.systemteam;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.systemteam.activity.BreakActivity;
import com.systemteam.activity.WalletActivity;
import com.systemteam.bean.Config;
import com.systemteam.bean.MyUser;
import com.systemteam.util.Constant;
import com.systemteam.util.LogTool;
import com.systemteam.util.Utils;
import com.systemteam.view.ProgressDialogHelper;
import com.systemteam.welcome.WelcomeActivity;

import java.lang.reflect.Field;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.systemteam.util.Constant.COST_BASE_DEFAULT;
import static com.systemteam.util.Constant.REQUEST_CODE;
import static com.systemteam.util.Constant.TIME_ONCE_ACTIVE_TEST;
import static com.systemteam.util.Utils.dp2px;


public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    protected final int REQUEST_CODE_SOME_FEATURES_PERMISSIONS = 101;
    public int statusBarHeight = 0, titleHeight;
    protected Toolbar mToolbar;
    protected TextView mToolbarTitle;
    protected Context mContext;
    protected SharedPreferences mSharedPre;
    protected ProgressDialogHelper mProgressHelper;
    protected boolean CheckNetwork = true;
    protected static boolean isOfflineResponse = false;
    protected InputMethodManager mImm;
    protected BikeApplication mApplication;
    protected MyUser mUser;
//    protected ImageView mIvMenu, mIvSearch;
//    protected TextView mTvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }*/
        statusBarHeight = getStatusBarHeight();
        titleHeight = dp2px(this, 50);
        mImm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        mProgressHelper = new ProgressDialogHelper(this);
        checkNetworkAvailable(this);
        MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
        if(myUser != null && myUser.getMobilePhoneNumber() != null){
            if(Utils.isSuperAdmin(myUser.getMobilePhoneNumber())){
                Constant.TIME_ONCE_ACTIVE = TIME_ONCE_ACTIVE_TEST;
            }
        }
    }

    @TargetApi(19)
    protected void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;        // a|=b的意思就是把a和b按位或然后赋值给a   按位或的意思就是先把a和b都换成2进制，然后用或操作，相当于a=a|b
        } else {
            winParams.flags &= ~bits;        //&是位运算里面，与运算  a&=b相当于 a = a&b  ~非运算符
        }
        win.setAttributes(winParams);
    }

    protected void setStatusBarLayout() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final ViewGroup linear_bar = (ViewGroup) findViewById(R.id.title_layout);
            final int statusHeight = getStatusBarHeight();
            linear_bar.post(new Runnable() {
                @Override
                public void run() {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) linear_bar.getLayoutParams();
                    params.height = statusHeight;
                    linear_bar.setLayoutParams(params);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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
    protected ListView mListview;
    protected BaseAdapter mAdapter;
    private CompositeSubscription mCompositeSubscription;

    /**
     * 解决Subscription内存泄露问题
     *
     * @param s
     */
    protected void addSubscription(Subscription s) {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }
        this.mCompositeSubscription.add(s);
    }

    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public static void log(String msg) {
        LogTool.d("===============================================================================");
        LogTool.d(msg);
    }

    public static void loge(Throwable e) {
        LogTool.d("===============================================================================");
        if (e instanceof BmobException) {
            LogTool.e("错误码：" + ((BmobException) e).getErrorCode() + ",错误描述：" + ((BmobException) e).getMessage());
        } else {
            LogTool.e("错误描述：" + e.getMessage());
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
            mToolbarTitle.setTextColor(ContextCompat.getColor(act, R.color.white));
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

    protected boolean checkUser(final Activity act) {
        if (((BikeApplication) act.getApplication()).getmUser() == null ||
                BmobUser.getCurrentUser(MyUser.class) == null) {
            AlertDialog alertDialog = new AlertDialog.Builder(act).create();
            alertDialog.setTitle(act.getString(R.string.tip));
            alertDialog.setMessage(act.getString(R.string.user_no));
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, act.getString(R.string.confirm),
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(act, WelcomeActivity.class));
                            if (act instanceof Main2Activity) {
                            } else {
                                act.finish();
                            }
                        }
                    });
            /*alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, act.getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });*/
            alertDialog.show();
            return false;
        }
        return true;
    }

    public void gotoBreak(View view) {
        Intent intentBreak = new Intent(mContext, BreakActivity.class);
        intentBreak.putExtra(Constant.BUNDLE_TYPE_MENU, Constant.BREAK_TYPE_BREAK);
        startActivityForResult(intentBreak, Constant.REQUEST_CODE_BREAK);
    }

    public void gotoBreakLock(View view) {
        Intent intent = new Intent(mContext, BreakActivity.class);
        intent.putExtra(Constant.BUNDLE_TYPE_MENU, Constant.BREAK_TYPE_LOCK);
        startActivityForResult(intent, Constant.REQUEST_CODE_BREAK);
    }

    public void loadAvatar(final Context context, String path, final ImageView imageView) {
        LogTool.d("Pohotopath:" + path);
        if (!TextUtils.isEmpty(path)) {
            Glide.with(context)
                    .load(path)
                    .asBitmap()
                    .placeholder(R.drawable.account_default_head_portrait)
                    .centerCrop()
                    .into(new BitmapImageViewTarget(imageView) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            imageView.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        }else {
            LogTool.e("path null");
        }
    }

    protected void registerBroadcast(String action, BroadcastReceiver receiver) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(action);  //添加要收到的广播
        registerReceiver(receiver, intentFilter);
    }

    protected void unRegisterBroadcast(BroadcastReceiver receiver) {
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
    }

    protected boolean checkBalance(MyUser user, final Activity activity) {
        if (user.getCoupon() != null && user.getCoupon().intValue() > 0) {
            return true;
        } else if (user.getBalance() != null && user.getBalance().floatValue() > COST_BASE_DEFAULT) {
            return true;
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
            alertDialog.setTitle(activity.getString(R.string.tip));
            alertDialog.setMessage(activity.getString(R.string.ballance_no));
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, activity.getString(R.string.confirm),
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivityForResult(new Intent(activity, WalletActivity.class), REQUEST_CODE);
                        }
                    });
            alertDialog.show();
            return false;
        }
    }

    /**
     * 判断网络是否可用
     *
     * @param context
     * @return 0:不可用，1移动网络，2WIFI
     */
    protected int checkNetworkAvailable(Context context) {
        ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mgr.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            String name = info.getTypeName();
            LogTool.d("Network is available :" + name);
            if (name.equals("WIFI")) {
                return Constant.NETWORK_STATUS_WIFI;
            } else {
                return Constant.NETWORK_STATUS_GRPS;
            }
        } else {
            LogTool.e("Network is not available !");
            Utils.showDialog(context, context.getString(R.string.tip), context.getString(R.string.net_none));
            return Constant.NETWORK_STATUS_NO;
        }
    }

    /**
     * 设置为全屏显示
     */
    protected void setFullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 退出全屏显示
     */
    protected void cancelFullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    private void requestSaveConfig(Config config){
        addSubscription(config.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){

                }else{
                    if (e instanceof BmobException) {
                        LogTool.e("错误码：" + ((BmobException) e).getErrorCode() + ",错误描述：" + ((BmobException) e).getMessage());
                    } else {
                        LogTool.e("错误描述：" + e.getMessage());
                    }
                }
            }
        }));
    }

    protected void requestInitConfig(){
        /*Config config1 = new Config();
        config1.setTag(Constant.ConfigEnum.EARN_RATE_DEFAULT.getTag());
        config1.setValue(Float.valueOf(Constant.EARN_RATE_DEFAULT));
        config1.setMax(Float.valueOf(Constant.EARN_RATE_DEFAULT_MAX));
        config1.setMin(Float.valueOf(Constant.EARN_RATE_DEFAULT_MIN));
        requestSaveConfig(config1);

        Config config2 = new Config();
        config2.setTag(Constant.ConfigEnum.COST_BASE_DEFAULT.getTag());
        config2.setValue(Float.valueOf(Constant.COST_BASE_DEFAULT));
        config2.setMax(Float.valueOf(Constant.COST_BASE_DEFAULT_MAX));
        config2.setMin(Float.valueOf(Constant.COST_BASE_DEFAULT_MIN));
        requestSaveConfig(config2);

        Config config3 = new Config();
        config3.setTag(Constant.ConfigEnum.TIME_ONCE_ACTIVE.getTag());
        config3.setValue(Float.valueOf(Constant.TIME_ONCE_ACTIVE));
        config3.setMax(Float.valueOf(Constant.TIME_ONCE_ACTIVE_MAX));
        config3.setMin(Float.valueOf(Constant.TIME_ONCE_ACTIVE_MIN));
        requestSaveConfig(config3);

        Config config4 = new Config();
        config4.setTag(Constant.ConfigEnum.COUPON_DEFAULT.getTag());
        config4.setValue(Float.valueOf(Constant.COUPON_DEFAULT));
        config4.setMax(Float.valueOf(Constant.COUPON_DEFAULT_MAX));
        config4.setMin(Float.valueOf(Constant.COUPON_DEFAULT_MIN));
        requestSaveConfig(config4);

        Config config5 = new Config();
        config5.setTag(Constant.ConfigEnum.COUPON_DEFAULT_pay_100.getTag());
        config5.setValue(Float.valueOf(Constant.COUPON_DEFAULT_pay_100));
        config5.setMax(Float.valueOf(Constant.COUPON_DEFAULT_pay_100_MAX));
        config5.setMin(Float.valueOf(Constant.COUPON_DEFAULT_pay_100_MIN));
        requestSaveConfig(config5);

        Config config6 = new Config();
        config6.setTag(Constant.ConfigEnum.COUPON_DEFAULT_pay_200.getTag());
        config6.setValue(Float.valueOf(Constant.COUPON_DEFAULT_pay_200));
        config6.setMax(Float.valueOf(Constant.COUPON_DEFAULT_pay_200_MAX));
        config6.setMin(Float.valueOf(Constant.COUPON_DEFAULT_pay_200_MIN));
        requestSaveConfig(config6);

        Config config7 = new Config();
        config7.setTag(Constant.ConfigEnum.WITHDRAW_DAYS_DEFAULT.getTag());
        config7.setValue(Float.valueOf(Constant.WITHDRAW_DAYS_DEFAULT));
        config7.setMax(Float.valueOf(Constant.WITHDRAW_DAYS_DEFAULT_MAX));
        config7.setMin(Float.valueOf(Constant.WITHDRAW_DAYS_DEFAULT_MIN));
        requestSaveConfig(config7);*/

        BmobQuery<Config> query = new BmobQuery<>();
        addSubscription(query.findObjects(new FindListener<Config>() {

            @Override
            public void done(List<Config> object, BmobException e) {
                if(e==null){
                    if(object != null && object.size() > 0){
                        for(Config config : object){
                            if(Constant.ConfigEnum.EARN_RATE_DEFAULT.getTag().equalsIgnoreCase(config.getTag())){
                                if(config.getValue() != null && config.getMax() != null && config.getMin() != null){
                                    float value = config.getValue();
                                    float max = config.getMax();
                                    float min = config.getMin();
                                    if(value >= min && value <= max &&
                                            value >= Constant.EARN_RATE_DEFAULT_MIN
                                            && value <= Constant.EARN_RATE_DEFAULT_MAX){
                                        Constant.EARN_RATE_DEFAULT = value;
                                    }
                                }
                            }else if(Constant.ConfigEnum.COST_BASE_DEFAULT.getTag().equalsIgnoreCase(config.getTag())){
                                if(config.getValue() != null && config.getMax() != null && config.getMin() != null){
                                    float value = config.getValue();
                                    float max = config.getMax();
                                    float min = config.getMin();
                                    if(value >= min && value <= max &&
                                            value >= Constant.COST_BASE_DEFAULT_MIN
                                            && value <= Constant.COST_BASE_DEFAULT_MAX){
                                        Constant.COST_BASE_DEFAULT = value;
                                    }
                                }
                            }else if(Constant.ConfigEnum.TIME_ONCE_ACTIVE.getTag().equalsIgnoreCase(config.getTag())){
                                if(config.getValue() != null && config.getMax() != null && config.getMin() != null){
                                    Float value = config.getValue();
                                    float max = config.getMax();
                                    float min = config.getMin();
                                    if(value >= min && value <= max &&
                                            value >= Constant.TIME_ONCE_ACTIVE_MIN
                                            && value <= Constant.TIME_ONCE_ACTIVE_MAX){
                                        Constant.TIME_ONCE_ACTIVE = value.intValue();
                                    }
                                }
                            }else if(Constant.ConfigEnum.COUPON_DEFAULT.getTag().equalsIgnoreCase(config.getTag())){
                                if(config.getValue() != null && config.getMax() != null && config.getMin() != null){
                                    Float value = config.getValue();
                                    float max = config.getMax();
                                    float min = config.getMin();
                                    if(value >= min && value <= max &&
                                            value >= Constant.COUPON_DEFAULT_MIN
                                            && value <= Constant.COUPON_DEFAULT_MAX){
                                        Constant.COUPON_DEFAULT = value.intValue();
                                    }
                                }
                            }else if(Constant.ConfigEnum.COUPON_DEFAULT_pay_100.getTag().equalsIgnoreCase(config.getTag())){
                                if(config.getValue() != null && config.getMax() != null && config.getMin() != null){
                                    Float value = config.getValue();
                                    float max = config.getMax();
                                    float min = config.getMin();
                                    if(value >= min && value <= max &&
                                            value >= Constant.COUPON_DEFAULT_pay_100_MIN
                                            && value <= Constant.COUPON_DEFAULT_pay_100_MAX){
                                        Constant.COUPON_DEFAULT_pay_100 = value.intValue();
                                    }
                                }
                            }else if(Constant.ConfigEnum.COUPON_DEFAULT_pay_200.getTag().equalsIgnoreCase(config.getTag())){
                                if(config.getValue() != null && config.getMax() != null && config.getMin() != null){
                                    Float value = config.getValue();
                                    float max = config.getMax();
                                    float min = config.getMin();
                                    if(value >= min && value <= max &&
                                            value >= Constant.COUPON_DEFAULT_pay_200_MIN
                                            && value <= Constant.COUPON_DEFAULT_pay_200_MAX){
                                        Constant.COUPON_DEFAULT_pay_200 = value.intValue();
                                    }
                                }
                            }else if(Constant.ConfigEnum.WITHDRAW_DAYS_DEFAULT.getTag().equalsIgnoreCase(config.getTag())){
                                if(config.getValue() != null && config.getMax() != null && config.getMin() != null){
                                    Float value = config.getValue();
                                    float max = config.getMax();
                                    float min = config.getMin();
                                    if(value >= min && value <= max &&
                                            value >= Constant.WITHDRAW_DAYS_DEFAULT_MIN
                                            && value <= Constant.WITHDRAW_DAYS_DEFAULT_MAX){
                                        Constant.WITHDRAW_DAYS_DEFAULT = value.intValue();
                                    }
                                }
                            }else if(Constant.ConfigEnum.VERSION_CODE_NEW.getTag().equalsIgnoreCase(config.getTag())){//升级检查
                                Utils.checkUpgrade(BaseActivity.this, config, false);
                            }
                        }
                    }
                }else{
                    if (e instanceof BmobException) {
                        LogTool.e("错误码：" + ((BmobException) e).getErrorCode() + ",错误描述：" + ((BmobException) e).getMessage());
                    } else {
                        LogTool.e("错误描述：" + e.getMessage());
                    }
                }
            }
        }));
    }

}