package com.systemteam.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.systemteam.BaseActivity;
import com.systemteam.BuildConfig;
import com.systemteam.R;
import com.systemteam.adapter.ChargeAmountAdapter;
import com.systemteam.adapter.ChargeAmountDividerDecoration;
import com.systemteam.bean.AuthResult;
import com.systemteam.bean.Car;
import com.systemteam.bean.CashRecord;
import com.systemteam.bean.MyUser;
import com.systemteam.bean.OrderWx;
import com.systemteam.bean.OrderWxResult;
import com.systemteam.bean.PayResult;
import com.systemteam.bean.UseRecord;
import com.systemteam.bean.Withdraw;
import com.systemteam.provider.OrderInfoUtil2_0;
import com.systemteam.util.Constant;
import com.systemteam.util.LogTool;
import com.systemteam.util.ProtocolPreferences;
import com.systemteam.util.Utils;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import static com.systemteam.provider.WXpayManager.getRandomString;
import static com.systemteam.util.Constant.BUNDLE_KEY_ALL_EARN;
import static com.systemteam.util.Constant.BUNDLE_KEY_ALL_WITHDRAW;
import static com.systemteam.util.Constant.BUNDLE_KEY_AMOUNT;
import static com.systemteam.util.Constant.BUNDLE_KEY_BLANACE;
import static com.systemteam.util.Constant.MSG_ORDER_SUCCESS_WX;
import static com.systemteam.util.Constant.MSG_UPDATE_UI;
import static com.systemteam.util.Constant.PAY_AMOUNT_DEFAULT;
import static com.systemteam.util.Constant.REQUEST_CODE;
import static com.systemteam.util.Constant.REQUEST_KEY_BY_USER;
import static com.systemteam.util.Constant.WX_APP_ID;

//TODO float数值增加精度运算
public class WalletActivity extends BaseActivity implements ChargeAmountAdapter.OnItemClickListener{

    RecyclerView recyclerview_acount;
    ChargeAmountAdapter adapter;
    TextView ballance;
    ImageView wechat, alipay;
    RelativeLayout wechat_layout, alipay_layout;
    Button mBtnBook;
    boolean isPayByWechat = true;
    private float mAmout = 0f, mAllEarn, mAllWithDraw, mBalance,mAllCost;
    private int mAmountPay = PAY_AMOUNT_DEFAULT;
    IWXAPI mWXApi;
    private boolean isWithdrawBalance = false;
    RequestQueue mQueue;
    private String mWXTradeNo;//微信订单号

    private static class MyHandler extends Handler {
        private WeakReference<WalletActivity> mActivity;

        public MyHandler(WalletActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final WalletActivity theActivity = mActivity.get();
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_UPDATE_UI:
                    theActivity.updateBalance();
                    break;
                case MSG_ORDER_SUCCESS_WX:
                    OrderWx.Data data = (OrderWx.Data) msg.obj;
                    if(data != null){
                        theActivity.mWXTradeNo = data.getTradeNo();
                        theActivity.mWXApi.registerApp(Constant.WX_APP_ID);
                        PayReq req = new PayReq();
                        req.appId = data.getAppid();
                        LogTool.d("appId : " + data.getAppid());
                        req.partnerId = data.getPartnerid();
                        LogTool.d("partnerId : " + data.getPartnerid());
                        req.prepayId = data.getPrepayid();
                        LogTool.d("prepayId : " + data.getPrepayid());
                        req.packageValue = data.getPackagevalue();
                        LogTool.d("packageValue : " + data.getPackagevalue());
                        req.nonceStr = data.getNoncestr();
                        LogTool.d("nonceStr : " + data.getNoncestr());
                        req.timeStamp = data.getTimestamp();
                        LogTool.d("timeStamp : " + data.getTimestamp());
                        req.sign = data.getSign();
                        LogTool.d("sign : " + data.getSign());

                        boolean bool = theActivity.mWXApi.sendReq(req);
                        LogTool.d("sendReq :" + bool);
                    }
            }
        }
    }

    private MyHandler mHandler = new MyHandler(this);
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        mContext = this;
        mWXApi = WXAPIFactory.createWXAPI(this, WX_APP_ID);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        initToolBar(this, R.string.wallet);
//        initBackgroudColor();
        mToolbar.inflateMenu(R.menu.menu_toolbar_wallet);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_detail:
                        startActivity(new Intent(WalletActivity.this, BalanceDetailActivity.class));
                        break;
                }
                return true;
            }
        });
        recyclerview_acount = (RecyclerView) findViewById(R.id.recyclerview_acount);
        ballance = (TextView) findViewById(R.id.ballance);
        wechat = (ImageView) findViewById(R.id.wechat);
        alipay = (ImageView) findViewById(R.id.alipay);
        wechat_layout = (RelativeLayout) findViewById(R.id.wechat_layout);
        alipay_layout = (RelativeLayout) findViewById(R.id.alipay_layout);
        wechat_layout.setOnClickListener(this);
        alipay_layout.setOnClickListener(this);

        recyclerview_acount.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new ChargeAmountAdapter(this);
        recyclerview_acount.setAdapter(adapter);
        adapter.setOnClickListener(this);
        recyclerview_acount.addItemDecoration(new ChargeAmountDividerDecoration(10));

        mBtnBook = (Button) findViewById(R.id.btn_book);
        mBtnBook.setOnClickListener(this);

        String str1 =  getString(R.string.wallet_tip_2);
        String content = getString(R.string.wallet_tip_1, str1);
        SpannableString mMoreFeatrue = new SpannableString(content);
        int index = content.indexOf(str1);
        mMoreFeatrue.setSpan(new UnderlineSpan(), index, content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mMoreFeatrue.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.colorAccent)),
                index, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((TextView) findViewById(R.id.tv_pay_protocol)).setText(mMoreFeatrue);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar_wallet,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestPayResult();
    }

    private void initBalance(){
        mUser = BmobUser.getCurrentUser(MyUser.class);
        mBalance = mUser.getBalance() == null ? 0f : mUser.getBalance().floatValue();
        if(mUser != null && mUser.getType() != null && mUser.getType().intValue() == 1){
            requestAllEarn();
        }
        String acount_ballance = getString(R.string.account_ballance, mBalance, "");
        Utils.setSpannableStr(ballance, acount_ballance, acount_ballance.length() - 3,
                acount_ballance.length() - 2, 1.2f, Color.parseColor("#393939"));
    }

    private void updateBalance(){
        mUser = BmobUser.getCurrentUser(MyUser.class);
        mBalance = mUser.getBalance();
        if(mUser != null && mUser.getType() != null && mUser.getType().intValue() == 1) {
//            if(isWithdrawBalance){
            if(true){
                mAmout = mAllEarn + mBalance - mAllWithDraw;
            }else {
                mAmout = mAllEarn - mAllWithDraw;
            }
            if (mAmout < 0) {
                mAmout = 0f;
            }
            String str1 = getString(R.string.account_withdraw);
            String str2 = getString(R.string.account_ballance, mAmout, str1);
            int index = str2.indexOf(str1);
            Utils.setSpannableStr(ballance, str2, index, str2.length(), 0.6f,
                    ContextCompat.getColor(mContext, R.color.common_blue_main));
        }else {
            String acount_ballance = getString(R.string.account_ballance, mBalance, "");
            Utils.setSpannableStr(ballance, acount_ballance, acount_ballance.length() - 3,
                    acount_ballance.length() - 2, 1.2f, Color.parseColor("#393939"));
        }
    }

    private void requestAllEarn(){
        mProgressHelper.showProgressDialog(getString(R.string.initing));
        BmobQuery<Car> query = new BmobQuery<>();
        query.addWhereEqualTo(REQUEST_KEY_BY_USER, mUser.getObjectId());
        query.sum(new String[] { "earn" });
        addSubscription(query.findStatistics(Car.class,new QueryListener<JSONArray>() {

            @Override
            public void done(JSONArray ary, BmobException e) {
                if(e==null){
                    if(ary!=null){//
                        try {
                            JSONObject obj = ary.getJSONObject(0);
                            mAllEarn = Float.valueOf(obj.getInt("_sumEarn"));//_(关键字)+首字母大写的列名
                            LogTool.d("reuslt : " + mAllEarn);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }else{
                        mAllEarn = 0;
//                        toast("查询成功，无数据");
                    }
                    requestAllWithdraw();
                }else{
                    mProgressHelper.dismissProgressDialog();
                    toast(getString(R.string.initing_fail));
                    LogTool.d("失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        }));

    }

    private void requestAllWithdraw(){
        BmobQuery<Withdraw> query = new BmobQuery<>();
        query.addWhereEqualTo(REQUEST_KEY_BY_USER, mUser.getObjectId());
        query.sum(new String[] { "amout" });
        addSubscription(query.findStatistics(Withdraw.class,new QueryListener<JSONArray>() {

            @Override
            public void done(JSONArray ary, BmobException e) {
                mProgressHelper.dismissProgressDialog();
                if(e==null){
                    if(ary!=null){//
                        try {
                            JSONObject obj = ary.getJSONObject(0);
                            mAllWithDraw = Float.valueOf(obj.getInt("_sumAmout"));//_(关键字)+首字母大写的列名
                            LogTool.d("reuslt : " + mAllWithDraw);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }else{
                        mAllWithDraw = 0;
//                        toast("查询成功，无数据");
                    }
                    mHandler.sendEmptyMessage(MSG_UPDATE_UI);
//                    requesAllCost();
                }else{
                    mProgressHelper.dismissProgressDialog();
                    toast(getString(R.string.initing_fail));
                    LogTool.d("失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        }));

    }

    private void requesAllCost(){
//        mAllCost = new java.util.Random().nextFloat();
        BmobQuery<UseRecord> query = new BmobQuery<>();
        query.addWhereEqualTo(REQUEST_KEY_BY_USER, mUser.getObjectId());
        query.sum(new String[] { "cost" });
        addSubscription(query.findStatistics(UseRecord.class,new QueryListener<JSONArray>() {

            @Override
            public void done(JSONArray ary, BmobException e) {
                mProgressHelper.dismissProgressDialog();
                if(e==null){
                    if(ary!=null){//
                        try {
                            JSONObject obj = ary.getJSONObject(0);
                            mAllCost = Float.valueOf(obj.getInt("_sumCost"));//_(关键字)+首字母大写的列名
                            LogTool.d("reuslt : " + mAllCost);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }else{
                        mAllCost = 0;
//                        toast("查询成功，无数据");
                    }
                }else{
                    LogTool.d("失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        }));
    }

    @Override
    protected void initData() {
        isWithdrawBalance = ProtocolPreferences.getIsWithdrawBalance(mContext);
        initBalance();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE:
                if(resultCode == RESULT_OK){
                    initBalance();
                }
                break;
        }
    }

    public void doApply(View view){
        if(mUser != null && mUser.getType() != null &&
                mUser.getType().intValue() == Constant.USER_TYPE_CUSTOMER){
//            if(mAmout > WITHDRAW_AMOUNT_DEFAULT){
                Intent intent = new Intent(WalletActivity.this, WithdrawActivity.class);
                intent.putExtra(BUNDLE_KEY_AMOUNT, mAmout);
                intent.putExtra(BUNDLE_KEY_ALL_EARN, mAllEarn);
                intent.putExtra(BUNDLE_KEY_ALL_WITHDRAW, mAllWithDraw);
                intent.putExtra(BUNDLE_KEY_BLANACE, mBalance);
                startActivityForResult(intent, Constant.REQUEST_CODE);
//            }else {
//                Utils.showDialog(mContext, getString(R.string.tip), getString(R.string.withdraw_refund,
//                        WITHDRAW_AMOUNT_DEFAULT));
//            }
        }
    }

    @Override
    public void onItemClick(View v, int position) {
        adapter.setSelectPosition(position);
        try {
            mAmountPay = Integer.valueOf(adapter.getValueSelect(position)) * 100;
        } catch (NumberFormatException e) {
            mAmountPay = PAY_AMOUNT_DEFAULT;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.wechat_layout:
                isPayByWechat = true;
                wechat.setImageResource(R.mipmap.type_select);
                alipay.setImageResource(R.mipmap.type_unselect);
                break;
            case R.id.alipay_layout:
                isPayByWechat = false;
                wechat.setImageResource(R.mipmap.type_unselect);
                alipay.setImageResource(R.mipmap.type_select);
                break;
            case R.id.tv_pay_protocol:
                Utils.showProtocol(mContext, Constant.GUIDE_TYPE_PAY);
                break;
            case R.id.btn_book:
                if(mAmountPay < PAY_AMOUNT_DEFAULT){
                    toast(getString(R.string.pay_error_amout));
                }
                if(isPayByWechat){
                    wxRequest();
                }else {
                    payV2(view);
                }
                break;
        }
    }

    //获取支付结果
    private void wxRequest(){
        //微信支付订单
        if(BuildConfig.DEBUG){
            mAmountPay = 10;
            requestWxPay(String.valueOf(mAmountPay));
        }else {
            requestWxPay(String.valueOf(mAmountPay));
        }
//        wxPayFromApp();
    }

    private void wxPayFromApp(){
        /*WechatPayTools.wechatPayUnifyOrder(mContext,
                WX_APP_ID, //微信分配的APP_ID
                WX_MCH_ID, //微信分配的 PARTNER_ID (商户ID)
                WX_PRIVATE_KEY, //微信分配的 PRIVATE_KEY (私钥)
                new WechatModel(getOrderId(), //订单ID (唯一)
                        "1", //价格
                        "YoYo Pay", //商品名称
                        "YoYo Pay"), //商品描述详情
                new onRequestListener() {
                    @Override
                    public void onSuccess(String s) {}

                    @Override
                    public void onError(String s) {}
                });*/
    }

    public String getOrderId(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate =  new Date(System.currentTimeMillis());
        String strOrderId = formatter.format(curDate) + getRandomString(16);
        LogTool.d("OrderId : " + strOrderId);
        return strOrderId;
    }


    /**
     * 支付宝支付业务
     *
     * @param v
     */
    public void payV2(View v) {
        /*AliPayTools.aliPay(WalletActivity.this, Constant.ALI_APP_ID, true, RSA2_PRIVATE,
                new AliPayModel(getOrderId(),
                        "1",
                        "yoyocar",
                        "yoyocar Pay"), new onRequestListener() {
                    @Override
                    public void onSuccess(String s) {
                        LogTool.e("onSuccess : " + s);
                    }

                    @Override
                    public void onError(String s) {
                        LogTool.e("onError : " + s);
                    }
                });*/
        if (TextUtils.isEmpty(Constant.ALI_APP_ID) || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))) {
            new AlertDialog.Builder(this).setTitle("警告").setMessage("需要配置APPID | RSA_PRIVATE")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            //
                            finish();
                        }
                    }).show();
            return;
        }

        /**
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo的获取必须来自服务端；
         */
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
        final String orderInfo = orderParam + "&" + sign;

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(WalletActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                LogTool.d(result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                ailiHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;

    @SuppressLint("HandlerLeak")
    private Handler ailiHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(WalletActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(WalletActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case SDK_AUTH_FLAG: {
                    @SuppressWarnings("unchecked")
                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();

                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
                        // 传入，则支付账户为该授权账户
                        Toast.makeText(WalletActivity.this,
                                "授权成功\n" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        // 其他状态值则为授权失败
                        Toast.makeText(WalletActivity.this,
                                "授权失败" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT).show();

                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    /** 支付宝支付业务：入参app_id */
    public static final String APPID = "";

    /** 支付宝账户登录授权业务：入参pid值 */
    public static final String PID = "";
    /** 支付宝账户登录授权业务：入参target_id值 */
    public static final String TARGET_ID = "";

    /** 商户私钥，pkcs8格式 */
    /** 如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个 */
    /** 如果商户两个都设置了，优先使用 RSA2_PRIVATE */
    /** RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议使用 RSA2_PRIVATE */
    /** 获取 RSA2_PRIVATE，建议使用支付宝提供的公私钥生成工具生成， */
    /** 工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1 */
    public static final String RSA2_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJ5VsIVOrXJTyoJG\n" +
            "InAg890eMiVWDwcxNW3KcjDjgDcRX+D+46QcO7bQB4kd8s1pLf1Cr2XDoc8kkeEh\n" +
            "SqF31cBfbVFyQZr4l5GkCkRtrJSvjHpMk5vCxySdMgR02aOW43FTAj/iKov6lbaI\n" +
            "eR+V70DGzxNfiadESZUMX6IAjcF3AgMBAAECgYBkDVfnVSlLNmGgYrs+ScRv9LXR\n" +
            "XAlRDSpq/2ObOxd5NNR2c/rbaC/fvKMWZUNZw94YzLvTPYURRVWdgpELaZM65zMU\n" +
            "rTOJUohYASbrJ8EUcA7dvdwZrcqQWI6TPoiHsYyT/buojSFYAatfpC56MqkxT5sX\n" +
            "8f52Koa1pmmTrcArAQJBAND3YgDd6BOyBawF6h4XFthAVeQgs8Z9h09lquDXac8t\n" +
            "+tBA5H3XfY1st9WFhUxdsLJv2LQsg/NTAXmYGXIlLoECQQDB+OsxxEou2YBpiw4L\n" +
            "PQTSgs7ByWvHUmKm3RwLpkEDG0I8qGGmJans4fjihlJIaQSEoMHcdkcVUT9g9dIo\n" +
            "smP3AkEAgW9aIxNQtzJj1QPs2iqPGe/vw9iFwoLql0FwMMj9XzkpzGkFnvUlbb5T\n" +
            "uEx2HrFBy6T/48pXCRb3KOwPhuaFAQJAMRNORiAYeLP0xj81RWihwLTxpJvWVe6l\n" +
            "IPyOLPBaQHP0FS6wzf13eYROmNlNFh7j0r5tbd7K6zzMITbwffVsTwJBAKm1Z1YT\n" +
            "ZdJDg0w6EPpWInQYIzOfG1DL+3EJf2W4O3X1lyry1Zs/5+QhuxCjdEe2S/fNjbj+\n" +
            "qnf5Ugf6qu5kego=";
    public static final String RSA_PRIVATE = "";

    private void paySuccess() {
        mProgressHelper.showProgressDialog(getString(R.string.initing));
        mUser = BmobUser.getCurrentUser(MyUser.class);
        MyUser newUser = new MyUser();
        float balance;
        if(mUser.getBalance() == null){
            balance = Float.valueOf(mAmountPay) / 100;
        }else {
            balance = (mUser.getBalance() + Float.valueOf(mAmountPay) / 100);
        }
        newUser.setBalance(balance);
        addSubscription(newUser.update(mUser.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    LogTool.d("update balance success");
                    mProgressHelper.dismissProgressDialog();
                    mHandler.sendEmptyMessage(MSG_UPDATE_UI);
                    CashRecord cashRecord = new CashRecord(mUser,
                            isPayByWechat ? Constant.PAY_TYPE_WX : Constant.PAY_TYPE_ALI,
                            Float.valueOf(mAmountPay) / 100);
                    addSubscription(cashRecord.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e==null){
                                LogTool.d("save cash record success");
                            }else{
                                loge(e);
                                toast(getString(R.string.submit_faile));
                            }
                        }
                    }));
                }else{
                    mProgressHelper.dismissProgressDialog();
                    loge(e);
                    toast(getString(R.string.submit_faile));
                }
            }
        }));
    }

    protected void initBackgroudColor(){
        // 用来提取颜色的Bitmap
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.theme_bg);
        // Palette的部分
        Palette.Builder builder = Palette.from(bitmap);
        builder.generate(new Palette.PaletteAsyncListener() {@Override public void onGenerated(Palette palette) {
            //获取到充满活力的这种色调
//            Palette.Swatch vibrant = palette.getVibrantSwatch();
            Palette.Swatch s1 = palette.getVibrantSwatch();       //获取到充满活力的这种色调
            Palette.Swatch s2 = palette.getDarkVibrantSwatch();    //获取充满活力的黑
            Palette.Swatch s3 = palette.getLightVibrantSwatch();   //获取充满活力的亮
            Palette.Swatch s4 = palette.getMutedSwatch();           //获取柔和的色调
            Palette.Swatch s5 = palette.getDarkMutedSwatch();      //获取柔和的黑
            Palette.Swatch s6 = palette.getLightMutedSwatch();    //获取柔和的亮
            List<Palette.Swatch> swatches = palette.getSwatches();
            for(Palette.Swatch s : swatches){
                LogTool.d("swatch :" + s.getRgb());
            }
            //根据调色板Palette获取到图片中的颜色设置到toolbar和tab中背景，标题等，使整个UI界面颜色统一
            /*toolbar_tab.setBackgroundColor(vibrant.getRgb());
            toolbar_tab.setSelectedTabIndicatorColor(colorBurn(vibrant.getRgb()));*/
            mToolbar.setBackgroundColor(s1.getRgb());
            if(s2 != null)
                ballance.setBackgroundColor(s2.getRgb());
            if(s3 != null)
                mBtnBook.setBackgroundColor(s3.getRgb());
            if(s6 != null)
                wechat_layout.setBackgroundColor(s6.getRgb());
            if (android.os.Build.VERSION.SDK_INT >= 21) {
                Window window = getWindow();
                if(s4 != null)
                    window.setStatusBarColor(s4.getRgb());
                if(s5 != null)
                    window.setNavigationBarColor(s5.getRgb());
            }

            /*if (android.os.Build.VERSION.SDK_INT >= 21) {
                Window window = getWindow();
                window.setStatusBarColor(colorBurn(vibrant.getRgb()));
                window.setNavigationBarColor(colorBurn(vibrant.getRgb()));
            }*/
        }
        });

    }

    public void requestWxPay(String amout){
        mProgressHelper.showProgressDialog(getString(R.string.initing));
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                "http://1.rockingcar.applinzi.com/wechatPay?amount=" + amout,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mProgressHelper.dismissProgressDialog();
                        LogTool.d(response);
                        try {
                            OrderWx order = new Gson().fromJson(response, OrderWx.class);
                            if(order != null && order.getData() != null){
                                Message msg = mHandler.obtainMessage(MSG_ORDER_SUCCESS_WX);
                                msg.obj = order.getData();
                                msg.sendToTarget();
                            }else {
                                LogTool.e("order or data null");
                                toast(getString(R.string.pay_order_fail));
                            }
                        } catch (JsonSyntaxException e) {
                            LogTool.e("e :" + e.toString());
                            toast(getString(R.string.pay_order_fail));
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressHelper.dismissProgressDialog();
                toast(getString(R.string.initing_fail));
                LogTool.e("Error: " + error.getMessage());
            }
        });
        if(mQueue == null){
            mQueue = Volley.newRequestQueue(mContext);
        }
        mQueue.add(stringRequest);
    }

    public void requestPayResult(){
        String url;
        if(isPayByWechat){
            if(TextUtils.isEmpty(mWXTradeNo)){
                return;
            }
            url = "http://1.rockingcar.applinzi.com/wechatPay?query=" + mWXTradeNo;
        }else {
            url = "";
        }
        mProgressHelper.showProgressDialog(getString(R.string.initing));
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mProgressHelper.dismissProgressDialog();
                        LogTool.d(response);
                        try {
                            OrderWxResult result = new Gson().fromJson(response, OrderWxResult.class);
                            if(result != null){
                                if("NOTPAY".equalsIgnoreCase(result.trade_state)){
                                    toast(getString(R.string.pay_fail_nopay));
                                }else if("SUCCESS".equalsIgnoreCase(result.trade_state)){
                                    toast(getString(R.string.pay_success,
                                            Float.valueOf(result.total_fee) / 100));
                                    paySuccess();
                                }else {
                                    toast(getString(R.string.pay_result_fail));
                                }
                            }else {
                                LogTool.e("reslut == null");
                                toast(getString(R.string.pay_fail));
                            }
                        } catch (JsonSyntaxException e) {
                            LogTool.e("e:" + e.toString());
                            toast(getString(R.string.pay_result_fail));
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgressHelper.dismissProgressDialog();
                toast(getString(R.string.pay_result_fail));
                LogTool.e("Error: " + error.getMessage());
            }
        });
        if(mQueue == null){
            mQueue = Volley.newRequestQueue(mContext);
        }
        mQueue.add(stringRequest);
        mWXTradeNo = null;
    }

}
