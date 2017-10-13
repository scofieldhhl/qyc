package com.systemteam.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;

import com.systemteam.BaseActivity;
import com.systemteam.R;
import com.systemteam.bean.Car;
import com.systemteam.bean.EventMessage;
import com.systemteam.util.Constant;
import com.systemteam.util.LogTool;
import com.systemteam.util.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static com.systemteam.util.Constant.BUNDLE_KEY_SUBMIT_SUCCESS;
import static com.systemteam.util.Constant.REQUEST_CODE_BREAK;

//5588输码解锁变成2588
//扫码or输码启动后，校准遥遥车定位，更新遥遥车定位。移动偏差比较大时记录。
public abstract class BaseActiveActivity extends BaseActivity {
    private Car mCar;
    protected boolean isGaming = false;   //游戏是否在游戏中
    protected boolean isFree = false;     //是否免费使用：使用过程中申报故障成功。

    protected void checkCarExist(final Context context, String carNo) {
        LogTool.d("checkCarExist :" + carNo);
        if(carNo == null || TextUtils.isEmpty(carNo)){
            return;
        }
        mProgressHelper.showProgressDialog(getString(R.string.initing));
        BmobQuery<Car> query = new BmobQuery<>();
        query.addWhereEqualTo("carNo", carNo);
        addSubscription(query.findObjects(new FindListener<Car>() {

            @Override
            public void done(List<Car> object, BmobException e) {
                mProgressHelper.dismissProgressDialog();
                if(e==null){
                    if(object != null && object.size() > 0){
                        mCar = object.get(0);
                        checkCarAvaliable(context, mCar);
                    }else {
//                        toast(getString(R.string.error_car_no));
                        Utils.showDialog(context, getString(R.string.tip), getString(R.string.error_car_no));
                    }
                }else{
                    toast(getString(R.string.initing_fail));
                    if(e instanceof BmobException){
                        LogTool.e("错误码："+((BmobException)e).getErrorCode()+",错误描述："+((BmobException)e).getMessage());
                    }else{
                        LogTool.e("错误描述："+e.getMessage());
                    }
                }
            }
        }));
    }
    protected void checkCarAvaliable(Context context, Car car){
        if(car == null){
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
                    showTipDialog(context, getString(R.string.tip_lock), car);
                    break;
                default:
                    showTipDialog(context, getString(R.string.tip_break), car);
                    break;
            }
        }
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
