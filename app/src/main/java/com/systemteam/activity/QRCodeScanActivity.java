package com.systemteam.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Toast;

import com.systemteam.BaseActivity;
import com.systemteam.R;
import com.systemteam.util.Constant;
import com.systemteam.util.LogTool;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zbar.ZBarView;

public class QRCodeScanActivity extends BaseActivity implements QRCodeView.Delegate {
    private boolean isLightOpened = false;
    private QRCodeView mQRCodeView;
    private String mCarNo;
    private boolean isUnLock = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        mContext = this;
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startScan();
    }

    private void startScan(){
        mQRCodeView.startCamera();
        mQRCodeView.showScanRect();
        mQRCodeView.startSpot();
    }

    @Override
    protected void onStop() {
        mQRCodeView.closeFlashlight();
        mQRCodeView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mQRCodeView.onDestroy();
        super.onDestroy();
    }

    private void exist(String code){
        setResult(RESULT_OK, new Intent().putExtra(Constant.BUNDLE_KEY_CODE, code)); //intent为A传来的带有Bundle的intent，当然也可以自己定义新的Bundle
        finish();//此处一定要调用finish()方法
    }

    @Override
    protected void initView() {
        initToolBar(this, R.string.title_scan);
        mImm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        mQRCodeView = (ZBarView) findViewById(R.id.zbarview);
        mQRCodeView.setDelegate(this);
    }

    @Override
    protected void initData() {
        checkSDK();
        isUnLock = getIntent().getBooleanExtra(Constant.BUNDLE_KEY_UNLOCK, false);//是否是解锁还是获取编号
        if(!isUnLock){
            ((TextView)findViewById(R.id.tv_inputcode)).setText(R.string.code_manual);
        }
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        LogTool.d("result:" + result);
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        vibrate();
        exist(result);
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        LogTool.e("打开相机出错");
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
            /*case R.id.start_spot:
                mQRCodeView.startSpot();
                break;
            case R.id.stop_spot:
                mQRCodeView.stopSpot();
                break;
            case R.id.start_spot_showrect:
                mQRCodeView.startSpotAndShowRect();
                break;
            case R.id.stop_spot_hiddenrect:
                mQRCodeView.stopSpotAndHiddenRect();
                break;
            case R.id.show_rect:
                mQRCodeView.showScanRect();
                break;
            case R.id.hidden_rect:
                mQRCodeView.hiddenScanRect();
                break;
            case R.id.start_preview:
                mQRCodeView.startCamera();
                break;
            case R.id.stop_preview:
                mQRCodeView.stopCamera();
                break;*/
            case R.id.iv_light:
            case R.id.open_flashlight:
                switchFlashlight();
                break;
            /*case R.id.close_flashlight:

                break;
            case R.id.scan_barcode:
                mQRCodeView.changeToScanBarcodeStyle();
                break;
            case R.id.scan_qrcode:
                mQRCodeView.changeToScanQRCodeStyle();
                break;
            case R.id.choose_qrcde_from_gallery:
                *//*
                从相册选取二维码图片，这里为了方便演示，使用的是
                https://github.com/bingoogolapple/BGAPhotoPicker-Android
                这个库来从图库中选择二维码图片，这个库不是必须的，你也可以通过自己的方式从图库中选择图片
                 *//*

                // 识别图片中的二维码还有问题，占时不要用
//                startActivityForResult(BGAPhotoPickerActivity.newIntent(this, null, 1, null, false), REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY);
                break;*/
        }
    }

    private void switchFlashlight(){
        if(isLightOpened){
            mQRCodeView.closeFlashlight();
            isLightOpened = false;
            ((TextView)findViewById(R.id.open_flashlight)).setText(R.string.light_open);
        }else {
            mQRCodeView.openFlashlight();
            isLightOpened = true;
            ((TextView)findViewById(R.id.open_flashlight)).setText(R.string.light_close);
        }
    }

    private void unlockSucess(){

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*// 识别图片中的二维码还有问题，占时不要用
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY) {
            final String picturePath = BGAPhotoPickerActivity.getSelectedImages(data).get(0);

            *//*
            这里为了偷懒，就没有处理匿名 AsyncTask 内部类导致 Activity 泄漏的问题
            请开发在使用时自行处理匿名内部类导致Activity内存泄漏的问题，处理方式可参考 https://github.com/GeniusVJR/LearningNotes/blob/master/Part1/Android/Android%E5%86%85%E5%AD%98%E6%B3%84%E6%BC%8F%E6%80%BB%E7%BB%93.md
             *//*
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    Bitmap bitmap = getDecodeAbleBitmap(picturePath);
                    int picw = bitmap.getWidth();
                    int pich = bitmap.getHeight();
                    int[] pix = new int[picw * pich];
                    byte[] pixytes = new byte[picw * pich];
                    bitmap.getPixels(pix, 0, picw, 0, 0, picw, pich);
                    int R, G, B, Y;

                    for (int y = 0; y < pich; y++) {
                        for (int x = 0; x < picw; x++) {
                            int index = y * picw + x;
                            R = (pix[index] >> 16) & 0xff;     //bitwise shifting
                            G = (pix[index] >> 8) & 0xff;
                            B = pix[index] & 0xff;

                            //R,G.B - Red, Green, Blue
                            //to restore the values after RGB modification, use
                            //next statement
                            pixytes[index] = (byte) (0xff000000 | (R << 16) | (G << 8) | B);
                        }
                    }
                    ByteBuffer buffer = ByteBuffer.allocate(bitmap.getByteCount());
                    byte[] data = new byte[(int) (bitmap.getHeight() * bitmap.getWidth() * 1.5)];
                    rgba2Yuv420(pixytes, data, bitmap.getWidth(), bitmap.getHeight());
                    return mQRCodeView.processData(data, bitmap.getWidth(), bitmap.getHeight(), true);
                }

                @Override
                protected void onPostExecute(String result) {
                    if (TextUtils.isEmpty(result)) {
                        Toast.makeText(QRCodeScanActivity.this, "未发现二维码", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(QRCodeScanActivity.this, result, Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute();
        }*/
    }


    /**
     * 将本地图片文件转换成可解码二维码的 Bitmap。为了避免图片太大，这里对图片进行了压缩。感谢 https://github.com/devilsen 提的 PR
     *
     * @param picturePath 本地图片文件路径
     * @return
     */
    private static Bitmap getDecodeAbleBitmap(String picturePath) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(picturePath, options);
            int sampleSize = options.outHeight / 400;
            if (sampleSize <= 0) {
                sampleSize = 1;
            }
            options.inSampleSize = sampleSize;
            options.inJustDecodeBounds = false;

            return BitmapFactory.decodeFile(picturePath, options);
        } catch (Exception e) {
            return null;
        }
    }

    public static void rgba2Yuv420(byte[] src, byte[] dst, int width, int height) {
        // Y
        for (int y = 0; y < height; y++) {
            int dstOffset = y * width;
            int srcOffset = y * width * 4;
            for (int x = 0; x < width && dstOffset < dst.length && srcOffset < src.length; x++) {
                dst[dstOffset] = src[srcOffset];
                dstOffset += 1;
                srcOffset += 4;
            }
        }
        /* Cb and Cr */
        for (int y = 0; y < height / 2; y++) {
            int dstUOffset = y * width + width * height;
            int srcUOffset = y * width * 8 + 1;

            int dstVOffset = y * width + width * height + 1;
            int srcVOffset = y * width * 8 + 2;
            for (int x = 0; x < width / 2 && dstUOffset < dst.length && srcUOffset < src.length && dstVOffset < dst.length && srcVOffset < src.length; x++) {
                dst[dstUOffset] = src[srcUOffset];
                dst[dstVOffset] = src[srcVOffset];

                dstUOffset += 2;
                dstVOffset += 2;

                srcUOffset += 8;
                srcVOffset += 8;
            }
        }
    }

    public Dialog showInputDialog() {
        final Dialog dialog = new Dialog(mContext, R.style.MyDialog);
        //设置它的ContentView
        dialog.setContentView(R.layout.activity_code_unlock);
        dialog.setCancelable(true);
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
                        mImm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                        final String code = String.valueOf(((EditText) dialog.findViewById(R.id.et_code)).getText());
                        if(!TextUtils.isEmpty(code) && code.length() > 5){
                            //TODO 校验输入车牌的有效性
                        }
                        dialog.dismiss();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(isUnLock)
                                    startActivity(new Intent(mContext, ActiveActivity.class));
                                exist(code);
                            }
                        }, 100);
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
        startScan();
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