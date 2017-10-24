package com.systemteam.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.systemteam.BaseActivity;
import com.systemteam.R;
import com.systemteam.bean.Car;
import com.systemteam.bean.EventMessage;
import com.systemteam.provider.ProtocolEncode;
import com.systemteam.service.RouteService;
import com.systemteam.util.Constant;
import com.systemteam.util.LogTool;
import com.systemteam.util.Utils;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static com.systemteam.provider.ProtocolEncode.getRandomString;
import static com.systemteam.util.Constant.BUNDLE_CAR;
import static com.systemteam.util.Constant.BUNDLE_KEY_SUBMIT_SUCCESS;
import static com.systemteam.util.Constant.REQUEST_CODE_BREAK;

//5588输码解锁变成2588
//扫码or输码启动后，校准遥遥车定位，更新遥遥车定位。移动偏差比较大时记录。
public abstract class BaseActiveActivity extends BaseActivity {
    private Car mCar;
    RequestQueue mQueue;
    protected boolean isGaming = false;   //游戏是否在游戏中
    protected boolean isFree = false;     //是否免费使用：使用过程中申报故障成功。

    protected void checkCarExist(final Context context, String carNo) {
        LogTool.d("checkCarExist :" + carNo);
        if(carNo == null || TextUtils.isEmpty(carNo)){
            return;
        }
        mProgressHelper.showProgressDialog(getString(R.string.initing_device));
        BmobQuery<Car> query = new BmobQuery<>();
        query.addWhereEqualTo("carNo", carNo);
        addSubscription(query.findObjects(new FindListener<Car>() {

            @Override
            public void done(List<Car> object, BmobException e) {
                if(e==null){
                    if(object != null && object.size() > 0){
                        mCar = object.get(0);
                        checkCarAvaliable(context, mCar);
                    }else {
                        mProgressHelper.dismissProgressDialog();
//                        toast(getString(R.string.error_car_no));
                        Utils.showDialog(context, getString(R.string.tip), getString(R.string.error_car_no));
                    }
                }else{
                    mProgressHelper.dismissProgressDialog();
                    toast(getString(R.string.initing_fail));
                    if(e instanceof BmobException){//TODO done L83--错误码：400,错误描述：{"data":{},"result":{"code":141,"message":"sdk time error"}}--
                        LogTool.e("错误码："+((BmobException)e).getErrorCode()+",错误描述："+((BmobException)e).getMessage());
                        //TODO 链接超时重试L83--错误码：9015,错误描述：java.net.SocketTimeoutException
                    }else{
                        LogTool.e("错误描述："+e.getMessage());
                    }
                }
            }
        }));
    }
    protected void checkCarAvaliable(Context context, Car car){
        if(car == null){
            mProgressHelper.dismissProgressDialog();
            return;
        }
        if(car.getStatus() == null){
            startRouteService(context, car);
        }else {
            switch (car.getStatus()){
                case Constant.STATUS_NORMAL:
                    startRouteService(context, car);
                    break;
                case Constant.BREAK_STATUS_LOCK:
                    mProgressHelper.dismissProgressDialog();
                    showTipDialog(context, getString(R.string.tip_lock), car);
                    break;
                default:
                    mProgressHelper.dismissProgressDialog();
                    showTipDialog(context, getString(R.string.tip_break), car);
                    break;
            }
        }
    }

    protected void startRouteService(final Context context, final Car car) {
        //测试使用
        if (car.getCarNo().startsWith("1878")) {
            mProgressHelper.dismissProgressDialog();
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
                            }, 4000);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mProgressHelper.dismissProgressDialog();
                    toast(getString(R.string.initing_fail_device_server));
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

     //开锁4s
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
                mProgressHelper.dismissProgressDialog();
                LogTool.d("response: " + response);
                if(response == null){
                    LogTool.e("response == null");
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

    private void showTipDialog(final Context context, String msg, final Car car){
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(getString(R.string.tip));
        alertDialog.setMessage(msg);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.confirm_continu),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startRouteService(context, car);
                    }
                });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.cancel_change),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        alertDialog.show();
    }

    protected void toastDialog(Activity activity, final boolean isFree) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(R.string.exist);
        builder.setTitle(R.string.tip);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                /*Intent intent = new Intent(MainActivity.this, RouteService.class);
                stopService(intent);*/
                EventBus.getDefault().post(new EventMessage(isFree, EventMessage.ACTION_GAMEOVER));
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    protected void checkBackFromBreak(int requestCode, Intent data){
        if(requestCode == REQUEST_CODE_BREAK && data != null){
            if(data.getBooleanExtra(BUNDLE_KEY_SUBMIT_SUCCESS, false)){
                isFree = true;
                EventBus.getDefault().post(new EventMessage(isFree, EventMessage.ACTION_GAMEOVER));
            }
        }
    }
}
