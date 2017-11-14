package com.systemteam.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.systemteam.BaseActivity;
import com.systemteam.Main2Activity;
import com.systemteam.R;
import com.systemteam.bean.MyUser;
import com.systemteam.util.Constant;
import com.systemteam.util.LogTool;
import com.uuzuche.lib_zxing.activity.CaptureFragment;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobUser;

import static com.systemteam.util.Constant.BUNDLE_KEY_CODE;

/**
 * 定制化显示扫描界面
 */
public class ZxingActivity extends BaseActivity {

    private CaptureFragment captureFragment;
    private boolean isUnLock = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        mContext = this;
        captureFragment = new CaptureFragment();
        // 为二维码扫描界面设置定制化界面
        CodeUtils.setFragmentArgs(captureFragment, R.layout.my_camera);
        captureFragment.setAnalyzeCallback(analyzeCallback);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_my_container, captureFragment).commit();

        initView();
        initData();
    }

    public static boolean isOpen = false;

    @Override
    protected void initView() {
        initToolBar(this, R.string.title_scan);
        mImm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        String model = android.os.Build.MODEL;
        LogTool.d("dd:" + model);
    }

    /**
     * 二维码解析回调函数
     */
    CodeUtils.AnalyzeCallback analyzeCallback = new CodeUtils.AnalyzeCallback() {
        @Override
        public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
            /*Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_SUCCESS);
            bundle.putString(CodeUtils.RESULT_STRING, result);
            resultIntent.putExtras(bundle);
            ZxingActivity.this.setResult(RESULT_OK, resultIntent);
            ZxingActivity.this.finish();*/
            LogTool.d("result:" + result);//result:http://pay.yiqiniubi.com/18789--
            vibrate();
            if(result != null && !TextUtils.isEmpty(result)){
                String[] arrResult = result.split("/");
                if(!isNumeric(arrResult[arrResult.length - 1])){
                    arrResult = result.split("=");
                }
                checkCode(arrResult[arrResult.length - 1]);
            }else {
                toast(getString(R.string.code_unvalid));
            }
        }

        @Override
        public void onAnalyzeFailed() {
            /*Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt(CodeUtils.RESULT_TYPE, CodeUtils.RESULT_FAILED);
            bundle.putString(CodeUtils.RESULT_STRING, "");
            resultIntent.putExtras(bundle);
            ZxingActivity.this.setResult(RESULT_OK, resultIntent);
            ZxingActivity.this.finish();*/
        }
    };

    private void exist(String code){
        setResult(RESULT_OK, new Intent().putExtra(BUNDLE_KEY_CODE, code)); //intent为A传来的带有Bundle的intent，当然也可以自己定义新的Bundle
        finish();//此处一定要调用finish()方法
    }


    @Override
    protected void initData() {
        checkSDK();
        mUser = BmobUser.getCurrentUser(MyUser.class);
        isUnLock = getIntent().getBooleanExtra(Constant.BUNDLE_KEY_UNLOCK, false);//是否是解锁还是获取编号
        if(!isUnLock){
            ((TextView)findViewById(R.id.tv_inputcode)).setText(R.string.code_manual);
        }
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    public boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_icon:
                finish();
                break;
            case R.id.iv_code:
            case R.id.tv_inputcode:
//                startActivity(new Intent(QRCodeScanActivity.this, CodeUnlockActivity.class));
                showInputDialog();
                break;
            case R.id.iv_light:
            case R.id.open_flashlight:
                switchFlashlight();
                break;
        }
    }

    private void switchFlashlight(){
        /*if(isLightOpened){
            mQRCodeView.closeFlashlight();
            isLightOpened = false;
            ((TextView)findViewById(R.id.open_flashlight)).setText(R.string.light_open);
        }else {
            mQRCodeView.openFlashlight();
            isLightOpened = true;
            ((TextView)findViewById(R.id.open_flashlight)).setText(R.string.light_close);
        }*/
        if (!isOpen) {
            CodeUtils.isLightEnable(true);
            isOpen = true;
        } else {
            CodeUtils.isLightEnable(false);
            isOpen = false;
        }
    }

    //开灯替换为红色icon
    public Dialog showInputDialog() {
        final Dialog dialog = new Dialog(mContext, R.style.MyDialog);
        //设置它的ContentView
        dialog.setContentView(R.layout.activity_code_unlock);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_unlock:
                        mImm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
                        String code = String.valueOf(((EditText) dialog.findViewById(R.id.et_code)).getText());
                        if(checkCode(code)){
                            dialog.dismiss();
                        }
                        break;
                    case R.id.iv_light:
                        switchFlashlight();
                        break;
                    case R.id.menu_icon:
                    case R.id.iv_scan:
                        mImm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
                        dialog.dismiss();
                        break;
                }
            }
        };
        dialog.findViewById(R.id.btn_unlock).setOnClickListener(listener);
        dialog.findViewById(R.id.iv_light).setOnClickListener(listener);
        dialog.findViewById(R.id.menu_icon).setOnClickListener(listener);
        dialog.findViewById(R.id.iv_scan).setOnClickListener(listener);
        if(!isUnLock){
            ((Button)dialog.findViewById(R.id.btn_unlock)).setText(R.string.confirm);
        }
        dialog.show();
//        mImm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        return dialog;
    }

    private boolean checkCode(final String code){
        if(TextUtils.isEmpty(code)){
            toast(getString(R.string.code_null));
            return false;
        }
        if(checkNetworkAvailable(mContext) == Constant.NETWORK_STATUS_NO){
            return false;
        }
        if(isUnLock) {
            mUser = BmobUser.getCurrentUser(MyUser.class);
            if (!checkBalance(mUser, ZxingActivity.this)) {
                return false;
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
//                                        Intent intent = new Intent(mContext, ActiveActivity.class);
                    Intent intent = new Intent(mContext, Main2Activity.class);
                    intent.putExtra(BUNDLE_KEY_CODE, code);
                    startActivity(intent);
                }
            }, 100);
        }
        exist(code);
        return true;
    }

    private void checkSDK(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int cameraPermission = checkSelfPermission(Manifest.permission.CAMERA);

            List<String> permissions = new ArrayList<String>();
            if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.CAMERA);
            }

            if (!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[permissions.size()]),
                        REQUEST_CODE_SOME_FEATURES_PERMISSIONS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        System.out.println("Permissions --> " + "Permission Granted: " + permissions[i]);
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        System.out.println("Permissions --> " + "Permission Denied: " + permissions[i]);
                    }
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }
}
