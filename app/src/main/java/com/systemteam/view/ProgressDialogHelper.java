package com.systemteam.view;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.systemteam.R;

/**
 * Created by chenjiang on 2015/8/28.
 */
public class ProgressDialogHelper {

    public Context mContext;

    private Dialog mProgressDialog;

    public ProgressDialogHelper(Context mContext) {
        this.mContext = mContext;
    }

    public Dialog showProgressDialog(String message) {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                return null;
            }


            //此处直接new一个Dialog对象出来，在实例化的时候传入主题
            mProgressDialog = new Dialog(mContext, R.style.MyDialog);
            //设置它的ContentView
            mProgressDialog.setContentView(R.layout.dialog_process);
            TextView text = (TextView) mProgressDialog.findViewById(R.id.progress_text);
            text.setText(message);
            mProgressDialog.setCanceledOnTouchOutside(false);

            mProgressDialog.show();

            return mProgressDialog;
        } catch (Exception e) {
        }
        return null;
    }

    public void dismissProgressDialog() {
        try {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
