package com.systemteam.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import com.systemteam.BaseActivity;
import com.systemteam.R;
import com.systemteam.bean.Car;
import com.systemteam.bean.EventMessage;
import com.systemteam.util.Constant;

import org.greenrobot.eventbus.EventBus;

import static com.systemteam.util.Constant.BUNDLE_KEY_SUBMIT_SUCCESS;
import static com.systemteam.util.Constant.REQUEST_CODE_BREAK;

public abstract class BaseActiveActivity extends BaseActivity {
    protected boolean isGaming = false;   //游戏是否在游戏中
    protected boolean isFree = false;     //是否免费使用：使用过程中申报故障成功。

    protected void checkCarAvaliable(Activity activity, Car car){
        if(car == null){
           return;
        }
        if(car.getStatus() == null){
            startRouteService(activity, car);
        }else {
            switch (car.getStatus()){
                case Constant.STATUS_NORMAL:
                    startRouteService(activity, car);
                    break;
                case Constant.BREAK_STATUS_LOCK:
                    showTipDialog(activity, getString(R.string.tip_lock), car);
                    break;
                default:
                    showTipDialog(activity, getString(R.string.tip_break), car);
                    break;
            }
        }
    }

    private void showTipDialog(final Activity context, String msg, final Car car){
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
