package com.systemteam;

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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.systemteam.activity.BreakActivity;
import com.systemteam.activity.WalletActivity;
import com.systemteam.bean.Car;
import com.systemteam.bean.MyUser;
import com.systemteam.provider.ProtocolEncode;
import com.systemteam.service.RouteService;
import com.systemteam.util.Constant;
import com.systemteam.util.LogTool;
import com.systemteam.util.Utils;
import com.systemteam.view.ProgressDialogHelper;
import com.systemteam.welcome.WelcomeActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.exception.BmobException;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.systemteam.provider.ProtocolEncode.getRandomString;
import static com.systemteam.util.Constant.BUNDLE_CAR;
import static com.systemteam.util.Constant.COST_BASE_DEFAULT;
import static com.systemteam.util.Constant.REQUEST_CODE;
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
        if (((BikeApplication) act.getApplication()).getmUser() == null) {
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
    RequestQueue mQueue;
    protected void startRouteService(final Context context, final Car car) {
        //TODO 测试使用
        if (car.getCarNo().startsWith("1878")) {
            Intent intent = new Intent(context, RouteService.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(BUNDLE_CAR, car);
            intent.putExtras(bundle);
            startService(intent);
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            final String out_trade_no = formatter.format(new Date()) + getRandomString(16);
            final String url = ProtocolEncode.encodeUnlockUrl(car.getCarNo(), out_trade_no);
            LogTool.d("out_trade_no: " +out_trade_no);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            /**
                             * 200：机器启动成功
                             401：appid错误
                             402：签名验证错误
                             403：机器号有误
                             404：机器不在线
                             405：机器正在使用中
                             406：响应超时
                             * */
                            LogTool.d(response);
                            /*if (response.contains("300")) {
                            } else {
                                String msg = "";
                                if (response.contains("4000")) {
                                    msg = getString(R.string.error_lock_4000);
                                } else if (response.contains("4001")) {
                                    msg = getString(R.string.error_lock_4001);
                                } else if (response.contains("4002")) {
                                    msg = getString(R.string.error_lock_4002);
                                } else if (response.contains("4003")) {
                                    msg = getString(R.string.error_lock_4003);
                                } else if (response.contains("401")) {
                                    msg = getString(R.string.error_lock_401);
                                } else if (response.contains("402")) {
                                    msg = getString(R.string.error_lock_402);
                                } else if (response.contains("403")) {
                                    msg = getString(R.string.error_lock_403);
                                } else if (response.contains("404")) {
                                    msg = getString(R.string.error_lock_404);
                                } else if (response.contains("405")) {
                                    msg = getString(R.string.error_lock_405);
                                } else if (response.contains("406")) {
                                    msg = getString(R.string.error_lock_406);
                                }
                                Utils.showDialog(context, getString(R.string.error_lock_failed), msg);
                                LogTool.e("response error!");
                            }*/
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    loadCarStatus(context, car, url);
                                }
                            }, 1000);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    LogTool.e("Error: " + error.getMessage());
                }
            });
            mQueue = Volley.newRequestQueue(mContext);
            mQueue.add(stringRequest);
        }
    }
    /**
     * 10-12 00:00:40.931 22381-22381/? D/[HHL]com.systemteam.BaseActivity: a L346--Network is available :MOBILE--
     10-12 00:00:41.091 22381-22381/? I/[HHL]com.systemteam.Main2Activity: onStart L944--Main2Activity------------onStart--------------------
     10-12 00:00:41.091 22381-22381/? D/[HHL]com.systemteam.Main2Activity: onResume L414--onResume--
     10-12 00:00:41.091 22381-22381/? I/[HHL]com.systemteam.Main2Activity: onResume L418--MainActivity------------onRestart--------------------
     10-12 00:00:41.151 22381-22381/? D/[HHL]com.systemteam.Main2Activity: onNewIntent L935--onNewIntent--
     10-12 00:00:41.161 22381-22381/? D/[HHL]com.systemteam.activity.BaseActiveActivity: a L37--checkCarExist :21591--
     10-12 00:00:41.191 22381-22381/? D/[HHL]com.systemteam.Main2Activity: onResume L414--onResume--
     10-12 00:00:41.191 22381-22381/? I/[HHL]com.systemteam.Main2Activity: onResume L418--MainActivity------------onRestart--------------------
     10-12 00:00:41.611 22381-22381/? D/[HHL]com.systemteam.provider.d: a L71--appid=5pGH5pGH6L2m&device_id=21591&nonce_str=dpuiept1txiyy0n3bn8xjgtf3o70dra9--
     10-12 00:00:41.611 22381-22381/? D/[HHL]com.systemteam.provider.d: a L73--appid=5pGH5pGH6L2m&device_id=21591&nonce_str=dpuiept1txiyy0n3bn8xjgtf3o70dra9&key=65696e5e3624af4e287bee8559b494d5--
     10-12 00:00:41.611 22381-22381/? D/[HHL]com.systemteam.provider.d: a L87--strMD5: F4BC844672EE8300773CD9D8254385FE--
     10-12 00:00:41.611 22381-22381/? I/[HHL]com.systemteam.provider.c: a L46--encodeUnlockUrl = http://yyc.yiqiniubi.com:20022/start?appid=5pGH5pGH6L2m&device_id=21591&nonce_str=dpuiept1txiyy0n3bn8xjgtf3o70dra9&sign=F4BC844672EE8300773CD9D8254385FE--
     10-12 00:00:42.031 22381-22381/? D/[HHL]com.systemteam.BaseActivity$6: a L382--{"code":"300","msg":"订单提交成功!"}--
     10-12 00:00:44.041 22381-22381/? D/[HHL]com.systemteam.provider.d: a L71--appid=5pGH5pGH6L2m&device_id=21591&nonce_str=0s9njxwt30bvywbnmj60pms44zcp1frr--
     10-12 00:00:44.041 22381-22381/? D/[HHL]com.systemteam.provider.d: a L73--appid=5pGH5pGH6L2m&device_id=21591&nonce_str=0s9njxwt30bvywbnmj60pms44zcp1frr&key=65696e5e3624af4e287bee8559b494d5--
     10-12 00:00:44.051 22381-22381/? D/[HHL]com.systemteam.provider.d: a L87--strMD5: 88FF792B68B79323980296F3BE8688DB--
     10-12 00:00:44.051 22381-22381/? I/[HHL]com.systemteam.provider.c: b L73--encodeUnlockUrl = http://yyc.yiqiniubi.com:20022/query?appid=5pGH5pGH6L2m&device_id=21591&nonce_str=0s9njxwt30bvywbnmj60pms44zcp1frr&sign=88FF792B68B79323980296F3BE8688DB--
     10-12 00:00:44.111 22381-22381/? D/[HHL]com.systemteam.BaseActivity$8: a L445--{"code":"200","msg":"消费成功!"}--
     10-12 00:00:44.151 22381-22381/? D/[HHL]com.systemteam.service.RouteService: onStartCommand L109--RouteService--------onStartCommand-----------------
     10-12 00:00:44.441 22381-22381/? E/[HHL]com.systemteam.service.RouteService$3: done L356--错误码：206,错误描述：User cannot be altered without sessionToken Error.--
     10-12 00:00:44.691 22381-22381/? D/[HHL]com.systemteam.Main2Activity: onResume L414--onResume--
     10-12 00:00:44.701 22381-22381/? I/[HHL]com.systemteam.Main2Activity: onResume L418--MainActivity------------onRestart--------------------

     //TODO 开锁4s
     * */
    private void loadCarStatus(final Context context, final Car car, String url){
        String queryUrl = url.replace("start", "query");
        LogTool.d("queryUrl : " + queryUrl);
        /*StringRequest stringRequest = new StringRequest(Request.Method.GET, queryUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        *//**
                         * 200：机器启动成功
                         401：appid错误
                         402：签名验证错误
                         403：机器号有误
                         404：机器不在线
                         405：机器正在使用中
                         406：响应超时
                         * *//*
                        LogTool.d(response);
                        if (response.contains("1000") || response.contains("200")) {
                            Intent intent = new Intent(context, RouteService.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(BUNDLE_CAR, car);
                            intent.putExtras(bundle);
                            startService(intent);
                        } else {
                            String msg = "";
                            if (response.contains("4000")) {
                                msg = getString(R.string.error_lock_4000);
                            } else if (response.contains("4001")) {
                                msg = getString(R.string.error_lock_4001);
                            } else if (response.contains("4002")) {
                                msg = getString(R.string.error_lock_4002);
                            } else if (response.contains("4003")) {
                                msg = getString(R.string.error_lock_4003);
                            } else if (response.contains("401")) {
                                msg = getString(R.string.error_lock_401);
                            } else if (response.contains("402")) {
                                msg = getString(R.string.error_lock_402);
                            } else if (response.contains("403")) {
                                msg = getString(R.string.error_lock_403);
                            } else if (response.contains("404")) {
                                msg = getString(R.string.error_lock_404);
                            } else if (response.contains("405")) {
                                msg = getString(R.string.error_lock_405);
                            } else if (response.contains("406")) {
                                msg = getString(R.string.error_lock_406);
                            }
                            Utils.showDialog(context, getString(R.string.error_lock_failed), msg);
                            LogTool.e("response error!");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                LogTool.e("Error: " + error.toString());
            }
        });
        mQueue = Volley.newRequestQueue(mContext);
        mQueue.add(stringRequest);*/

        new AsyncTask<String, Void, String>(){

            @Override
            protected String doInBackground(String... params) {
                try {
                    URL url=new URL(params[0]);
                    HttpURLConnection urlConnection=(HttpURLConnection) url.openConnection();
                    urlConnection.setConnectTimeout(30000);
                    urlConnection.setReadTimeout(30000);
                    urlConnection.setRequestMethod("GET");
                    //设置请求头header
                    urlConnection.setRequestProperty("test-header","get-header-value");
                    urlConnection.connect();
                    int code=urlConnection.getResponseCode();
                    if (code==200) {
                        InputStream inputStream=urlConnection.getInputStream();
                        BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
                        String readerline;
                        StringBuffer buffer=new StringBuffer();
                        while ((readerline=reader.readLine())!=null) {
                            buffer.append(readerline);

                        }
                        String str=buffer.toString();
                        return str;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;

            }
            protected void onPostExecute(String response) {
                LogTool.d("response: " + response);
                if(response == null){
                    return;
                }
                if (response.contains("200")) {
                    Intent intent = new Intent(context, RouteService.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(BUNDLE_CAR, car);
                    intent.putExtras(bundle);
                    startService(intent);
                } else {
                    String msg = "";
                    if (response.contains("4000")) {
                        msg = getString(R.string.error_lock_4000);
                    } else if (response.contains("4001")) {
                        msg = getString(R.string.error_lock_4001);
                    } else if (response.contains("4002")) {
                        msg = getString(R.string.error_lock_4002);
                    } else if (response.contains("4003")) {
                        msg = getString(R.string.error_lock_4003);
                    } else if (response.contains("401")) {
                        msg = getString(R.string.error_lock_401);
                    } else if (response.contains("402")) {
                        msg = getString(R.string.error_lock_402);
                    } else if (response.contains("403")) {
                        msg = getString(R.string.error_lock_403);
                    } else if (response.contains("404")) {
                        msg = getString(R.string.error_lock_404);
                    } else if (response.contains("405")) {
                        msg = getString(R.string.error_lock_405);
                    } else if (response.contains("406")) {
                        msg = getString(R.string.error_lock_406);
                    }
                    Utils.showDialog(context, getString(R.string.error_lock_failed), msg);
                    LogTool.e("response error!");
                }


            };


        }.execute(queryUrl);//urlpath为网址
    }

    /**
     * 设置为全屏显示
     */
    protected void setFullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 退出全屏显示
     */
    protected void cancelFullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}