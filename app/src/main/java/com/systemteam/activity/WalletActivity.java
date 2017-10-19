package com.systemteam.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.systemteam.R;
import com.systemteam.adapter.ChargeAmountAdapter;
import com.systemteam.adapter.ChargeAmountDividerDecoration;
import com.systemteam.bean.Car;
import com.systemteam.bean.CashRecord;
import com.systemteam.bean.MyUser;
import com.systemteam.bean.OrderAli;
import com.systemteam.bean.OrderWx;
import com.systemteam.bean.OrderWxResult;
import com.systemteam.bean.UseRecord;
import com.systemteam.bean.Withdraw;
import com.systemteam.provider.alipay.AliPayModel;
import com.systemteam.provider.alipay.AliPayTools;
import com.systemteam.provider.alipay.PayResult;
import com.systemteam.provider.model.onRequestListener;
import com.systemteam.util.Arith;
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
import java.util.Locale;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import static com.systemteam.provider.ProtocolEncode.getRandomString;
import static com.systemteam.util.Constant.BUNDLE_KEY_ALL_EARN;
import static com.systemteam.util.Constant.BUNDLE_KEY_ALL_WITHDRAW;
import static com.systemteam.util.Constant.BUNDLE_KEY_AMOUNT;
import static com.systemteam.util.Constant.BUNDLE_KEY_BLANACE;
import static com.systemteam.util.Constant.MSG_ORDER_SUCCESS_ALI;
import static com.systemteam.util.Constant.MSG_ORDER_SUCCESS_WX;
import static com.systemteam.util.Constant.MSG_UPDATE_UI;
import static com.systemteam.util.Constant.PAY_AMOUNT_DEFAULT;
import static com.systemteam.util.Constant.PAY_AMOUNT_MIN;
import static com.systemteam.util.Constant.REQUEST_CODE;
import static com.systemteam.util.Constant.REQUEST_KEY_BY_USER;
import static com.systemteam.util.Constant.WX_APP_ID;

// float数值增加精度运算
// 普通用户充值没有及时更新账户余额(pay返回json格式修改导致)
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
                    break;
                case MSG_ORDER_SUCCESS_ALI: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);

                    //对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                    /**
                     * 9000	订单支付成功
                     8000	正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
                     4000	订单支付失败
                     5000	重复请求
                     6001	用户中途取消
                     6002	网络连接出错
                     6004	支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
                     * */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        theActivity.toast(theActivity.getString(R.string.pay_success));
                        theActivity.paySuccess();
                    } else if(TextUtils.equals(resultStatus, "8000")){
                        theActivity.toast(theActivity.getString(R.string.pay_fail_8000));
                    }else if(TextUtils.equals(resultStatus, "4000")){
                        theActivity.toast(theActivity.getString(R.string.pay_fail_4000));
                    }else if(TextUtils.equals(resultStatus, "5000")){
                        theActivity.toast(theActivity.getString(R.string.pay_fail_5000));
                    }else if(TextUtils.equals(resultStatus, "6001")){
                        theActivity.toast(theActivity.getString(R.string.pay_fail_nopay));
                    }else if(TextUtils.equals(resultStatus, "6002")){
                        theActivity.toast(theActivity.getString(R.string.pay_fail_6002));
                    }else if(TextUtils.equals(resultStatus, "6004")){
                        theActivity.toast(theActivity.getString(R.string.pay_fail_6004));
                    }else{
                        theActivity.toast(theActivity.getString(R.string.pay_fail));
                    }
                    break;
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
        adapter.setSelectPosition(4);
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
                float value = Arith.add(mAllEarn, mBalance);
                mAmout = Arith.sub(value, mAllWithDraw);
            }else {
                mAmout = Arith.sub(mAllEarn, mAllWithDraw);
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
                if(mAmountPay < PAY_AMOUNT_MIN){
                    toast(getString(R.string.pay_error_amout));
                    return;
                }
                if(Utils.isSuperAdmin(mUser.getMobilePhoneNumber())){
                    mAmountPay = 1;
                }
                if(isPayByWechat){
                    //微信支付订单
                    requestWxPay(String.valueOf(mAmountPay));
                }else {
//                    payV2(view);
                    requestAliPay(Arith.div(mAmountPay, 100f));
                }
                break;
        }
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
     * @param v
     */
    //增加支付宝支付返回码提示
    public static final String RSA_PRIVATE = "";
    public void payV2(View v) {
        if(TextUtils.isEmpty(RSA_PRIVATE)){
            toast(getString(R.string.pay_order_fail));
            return;
        }
        String srtAmount = String.valueOf(Float.valueOf(mAmountPay) / 100);
        AliPayTools.aliPay(WalletActivity.this, Constant.ALI_APP_ID, false, RSA_PRIVATE,
                new AliPayModel(getOrderId(),
                        srtAmount,
                        "摇星球",
                        "摇星球充值"), new onRequestListener() {
                    @Override
                    public void onSuccess(String s) {
                        LogTool.e("onSuccess : " + s);
                        if("9000".equalsIgnoreCase(s)){
                            toast(getString(R.string.pay_success));
                            paySuccess();
                        }else {
                            toast(getString(R.string.pay_fail));
                        }
                    }

                    @Override
                    public void onError(String s) {
                        LogTool.e("onError : " + s);
                        if("6001".equalsIgnoreCase(s)){
                            toast(getString(R.string.pay_fail_nopay));
                        }else {
                            toast(getString(R.string.pay_fail));
                        }
                    }
                });
    }

    private void paySuccess() {
        mProgressHelper.showProgressDialog(getString(R.string.initing));
        mUser = BmobUser.getCurrentUser(MyUser.class);
        MyUser newUser = new MyUser();
        float balance;
        final float amout = Arith.div(Float.valueOf(mAmountPay), 100);
        if(mUser.getBalance() == null){
            balance = amout;
        }else {
            balance = Arith.add(mUser.getBalance(), amout);
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
                            isPayByWechat ? Constant.PAY_TYPE_WX : Constant.PAY_TYPE_ALI, amout);
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
                LogTool.e("Error: " + error.toString());
            }
        });
        if(mQueue == null){
            mQueue = Volley.newRequestQueue(mContext);
        }
        mQueue.add(stringRequest);
    }

    public void requestPayResult(){
        if(TextUtils.isEmpty(mWXTradeNo)){
            return;
        }
        String url;
        if(isPayByWechat){
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
                            if(result != null && result.data != null){
                                if("NOTPAY".equalsIgnoreCase(result.data.trade_state)){
                                    toast(getString(R.string.pay_fail_nopay));
                                }else if("SUCCESS".equalsIgnoreCase(result.data.trade_state)){
                                    toast(getString(R.string.pay_success));
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

    /**
     * https://1.rockingcar.applinzi.com/aliPay?amount=5  amount充值多少
     返回值：


     https://1.rockingcar.applinzi.com/aliPay?query=tradeNo  tradeNo是前面返回的订单号，用来查询
     返回值：
     {"code": "1", "data": {"trade_no": "2017091721001004740266026815", "code": "10000", "buyer_user_id": "2088202211761743", "buyer_logon_id": "447***@qq.com", "send_pay_date": "2017-09-17 22:47:35", "receipt_amount": "0.00", "out_trade_no": "KV74Y2XHTUL9BJW", "buyer_pay_amount": "0.00", "invoice_amount": "0.00", "msg": "Success", "point_amount": "0.00", "trade_status": "TRADE_SUCCESS", "total_amount": "0.01"}}

     失败：
     {"code": "0","msg":{支付宝错误的结果}}
     * */
    public void requestAliPay(float amout){
        mProgressHelper.showProgressDialog(getString(R.string.initing));
        String url = String.format(Locale.US, "http://1.rockingcar.applinzi.com/aliPay?amount=%.2f", amout);
        LogTool.d("url :" + url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mProgressHelper.dismissProgressDialog();
                        LogTool.d(response);
                        try {
                            final OrderAli order = new Gson().fromJson(response, OrderAli.class);
                            if(order != null && order.data != null && order.data.aliPayStr != null){
                                Runnable payRunnable = new Runnable() {

                                    @Override
                                    public void run() {
                                        PayTask alipay = new PayTask(WalletActivity.this);
                                        Map<String, String> result = alipay.payV2(order.data.aliPayStr, true);
                                        LogTool.d("result :" + result.toString());

                                        Message msg = new Message();
                                        msg.what = MSG_ORDER_SUCCESS_ALI;
                                        msg.obj = result;
                                        mHandler.sendMessage(msg);
                                    }
                                };

                                Thread payThread = new Thread(payRunnable);
                                payThread.start();

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
                LogTool.e("Error: " + error.toString());
            }
        });
        if(mQueue == null){
            mQueue = Volley.newRequestQueue(mContext);
        }
        mQueue.add(stringRequest);
    }

}
