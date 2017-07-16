package com.systemteam.car;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.systemteam.BaseActivity;
import com.systemteam.R;
import com.systemteam.activity.QRCodeScanActivity;
import com.systemteam.bean.Car;
import com.systemteam.bean.MyUser;
import com.systemteam.util.Utils;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import static com.systemteam.util.Constant.BUNDLE_KEY_CODE;
import static com.systemteam.util.Constant.REQUEST_CODE;

public class NewCarActivity extends BaseActivity {
    private TextView mTvCode;
    private String mCarNo;
    private Car mCar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_car);
        mContext = this;
        initView();
        initData();
    }

    @Override
    protected void initView() {
        initToolBar(NewCarActivity.this, R.string.new_car);
        mTvCode = (TextView) findViewById(R.id.tv_title_code);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_icon:
                finish();
                break;
        }
    }

    public void doSubmit(View view) {
        saveNewCar();
    }

    public void gotoScan(View view) {
        startActivityForResult(new Intent(NewCarActivity.this, QRCodeScanActivity.class),
                REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && data !=null) {
            mCarNo = data.getStringExtra(BUNDLE_KEY_CODE);
            mTvCode.setText(mCarNo);
        }
    }

    private void saveNewCar() {
        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        checkExist();
        if(mCar == null){
            mCar = new Car();
            mCar.setCarNo(mCarNo);
            mCar.setPosition(new BmobGeoPoint());
            mCar.setAuthor(user);
            addSubscription(mCar.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    log("SaveListener: " + s);
                    if(e==null){
                        toast("注册成功:" +s.toString());
                    }else{
                        loge(e);
                    }
                }
            }));
        }else if(mCar.getAuthor() != null){
            if(user.getObjectId().equalsIgnoreCase(mCar.getAuthor().getObjectId())){
                toast("该摇摇车已被激活");
            }else {
                Utils.showDialog(NewCarActivity.this, getString(R.string.tip),
                        "该摇摇车已被其他商户激活，有什么问题及时联系客服！");
            }
        }else {
            mCar.setCarNo(mCarNo);
            mCar.setPosition(new BmobGeoPoint());
            mCar.setAuthor(user);
            addSubscription(mCar.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(e==null){
                        toast("成功:");
                    }else{
                        loge(e);
                    }
                }
            }));
        }

    }

    private void checkExist() {
        BmobQuery<Car> query = new BmobQuery<>();
        query.addWhereEqualTo("carNo", mCarNo);
        addSubscription(query.findObjects(new FindListener<Car>() {

            @Override
            public void done(List<Car> object, BmobException e) {
                if(e==null){
                    toast("查询密码成功:" + object.size());
                    if(object != null && object.size() > 0){
                        mCar = object.get(0);
                    }
                }else{
                    loge(e);
                }
            }
        }));
    }
}