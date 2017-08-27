package com.systemteam.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.systemteam.BaseActivity;
import com.systemteam.R;
import com.systemteam.bean.Car;
import com.systemteam.util.Constant;

public abstract class BaseActiveActivity extends BaseActivity {

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
}
