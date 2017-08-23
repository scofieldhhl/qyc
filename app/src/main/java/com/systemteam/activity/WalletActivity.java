package com.systemteam.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.systemteam.BaseActivity;
import com.systemteam.R;
import com.systemteam.adapter.ChargeAmountAdapter;
import com.systemteam.adapter.ChargeAmountDividerDecoration;
import com.systemteam.bean.Car;
import com.systemteam.bean.CashRecord;
import com.systemteam.bean.MyUser;
import com.systemteam.bean.UseRecord;
import com.systemteam.bean.Withdraw;
import com.systemteam.util.Constant;
import com.systemteam.util.LogTool;
import com.systemteam.util.Utils;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;

import static com.systemteam.util.Constant.BUNDLE_KEY_ALL_EARN;
import static com.systemteam.util.Constant.BUNDLE_KEY_ALL_WITHDRAW;
import static com.systemteam.util.Constant.BUNDLE_KEY_AMOUNT;
import static com.systemteam.util.Constant.BUNDLE_KEY_BLANACE;
import static com.systemteam.util.Constant.MSG_UPDATE_UI;
import static com.systemteam.util.Constant.PAY_AMOUNT_DEFAULT;
import static com.systemteam.util.Constant.REQUEST_CODE;
import static com.systemteam.util.Constant.REQUEST_KEY_BY_USER;

/**
 * Created by gaolei on 16/12/29.
 */

public class WalletActivity extends BaseActivity implements ChargeAmountAdapter.OnItemClickListener{

    RecyclerView recyclerview_acount;
    ChargeAmountAdapter adapter;
    TextView ballance;
    ImageView wechat, alipay;
    RelativeLayout wechat_layout, alipay_layout;
    Button mBtnBook;
    boolean isPayByWechat = true;
    private float mAmout = 0f, mAllEarn, mAllWithDraw, mBalance,mAllCost,  mAmountPay = 0f;

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
            }
        }
    }

    private MyHandler mHandler = new MyHandler(this);
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        mContext = this;
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar_wallet,menu);

        return super.onCreateOptionsMenu(menu);
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
        if(mUser != null && mUser.getType() != null && mUser.getType().intValue() == 1) {
            mAmout = mAllEarn + mBalance - mAllWithDraw;
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
            mAmountPay = Float.valueOf(adapter.getValueSelect(position));
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
            case R.id.btn_book:
                if(isPayByWechat){
                    wxRequest();
                }else {
                    payV2(view);
                }
//                saveNewObject();
                break;
        }
    }

    private void wxRequest(){
        //TODO Wechat pay
        IWXAPI api = WXAPIFactory.createWXAPI(this, Constant.WX_APP_ID);
        WXTextObject textObj = new WXTextObject();
        textObj.text = "text";

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.description = "text";

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("text");
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;

        api.sendReq(req);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    /**
     * 支付宝支付业务
     *
     * @param v
     */
    public void payV2(View v) {
        /*if (TextUtils.isEmpty(APPID) || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))) {
            new AlertDialog.Builder(this).setTitle("警告").setMessage("需要配置APPID | RSA_PRIVATE")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            //
                            finish();
                        }
                    }).show();
            return;
        }

        *//**
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo的获取必须来自服务端；
         *//*
        *//*boolean rsa2 = (RSA2_PRIVATE.length() > 0);
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
        final String orderInfo = orderParam + "&" + sign;*//*
        //TODO alipay orderinfo
        final String orderInfo = "orderParam + & + sign";

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(WalletActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                LogTool.d(result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();*/
    }

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;

    /*@SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    *//**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     *//*
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

    *//** 支付宝支付业务：入参app_id *//*
    public static final String APPID = "";

    *//** 支付宝账户登录授权业务：入参pid值 *//*
    public static final String PID = "";
    *//** 支付宝账户登录授权业务：入参target_id值 *//*
    public static final String TARGET_ID = "";

    *//** 商户私钥，pkcs8格式 *//*
    *//** 如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个 *//*
    *//** 如果商户两个都设置了，优先使用 RSA2_PRIVATE *//*
    *//** RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议使用 RSA2_PRIVATE *//*
    *//** 获取 RSA2_PRIVATE，建议使用支付宝提供的公私钥生成工具生成， *//*
    *//** 工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1 *//*
    public static final String RSA2_PRIVATE = "";
    public static final String RSA_PRIVATE = "";*/

    private void saveNewObject() {
        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        CashRecord cashRecord = new CashRecord(user,
                isPayByWechat ? Constant.PAY_TYPE_WX : Constant.PAY_TYPE_ALI, mAmountPay);
        addSubscription(cashRecord.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                mProgressHelper.dismissProgressDialog();
                if(e==null){
                    toast(getString(R.string.add_success));
//                    mHandler.sendEmptyMessage(MSG_UPDATE_UI);
                }else{
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
}
