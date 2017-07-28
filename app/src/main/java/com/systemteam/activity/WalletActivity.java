package com.systemteam.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.systemteam.BaseActivity;
import com.systemteam.R;
import com.systemteam.adapter.ChargeAmountAdapter;
import com.systemteam.adapter.ChargeAmountDividerDecoration;
import com.systemteam.bean.MyUser;
import com.systemteam.util.Utils;

import cn.bmob.v3.BmobUser;

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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
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
//                        startActivity(new Intent(WalletActivity.this, SearchActivity.class));
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

        mUser = BmobUser.getCurrentUser(MyUser.class);
        if(mUser != null && mUser.getType() != null && mUser.getType().intValue() == 1){
            String str1 = getString(R.string.account_withdraw);
            String str2 =  getString(R.string.account_ballance,
                    mUser.getBalance() == null ? 0f : mUser.getBalance().floatValue(), str1);
            int index = str2.indexOf(str1);
            Utils.setSpannableStr(ballance, str2, index, str2.length(), 0.6f,
                    getResources().getColor(R.color.common_blue_main));
        }else {

            String acount_ballance = getString(R.string.account_ballance,
                    mUser.getBalance() == null ? 0f : mUser.getBalance().floatValue(), "");
            Utils.setSpannableStr(ballance, acount_ballance, acount_ballance.length() - 3,
                    acount_ballance.length() - 2, 1.2f, Color.parseColor("#393939"));
        }
    }

    @Override
    protected void initData() {

    }

    public void doApply(View view){
        if(mUser != null && mUser.getType() != null && mUser.getType().intValue() == 1){

        }
    }

    @Override
    public void onItemClick(View v, int position) {
        adapter.setSelectPosition(position);
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
                break;
        }
    }

    private void wxRequest(){
        /*//TODO Wechat pay
        IWXAPI api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
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
        finish();*/
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
}
