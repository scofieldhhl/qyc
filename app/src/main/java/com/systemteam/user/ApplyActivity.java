package com.systemteam.user;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.gson.Gson;
import com.systemteam.BaseActivity;
import com.systemteam.R;
import com.systemteam.util.LogTool;
import com.systemteam.view.IconEditTextView;
import com.systemteam.view.ProgressDialogHelper;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.bean.Image;

import static com.systemteam.util.Constant.REQUEST_IMAGE;

/**
 * @author scofield.hhl@gmail.com
 * @Description
 * @time 2016/6/16
 */
public class ApplyActivity extends BaseActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {
    private final String FLAG_URL = "http:";
    private final int MAX_NUM_STORE_PHOTO = 4;
    private final int SELECT_COUNTRY = 1;
    private final int SELECT_LANGUAGE = 2;
    private final int SELECT_EXISTSTORE = 3;
    private final int SOCKET_TIMEOUT = 1000;
    public RequestQueue mQueue;

    private RelativeLayout iv_savelayout;
    private ImageView mIvUserPhoto, mIvDelPhoto, mIvAddPhoto, mIvCountrySelect,
            mIvLanguageSelect, mIvSave, mIvClose;
    private IconEditTextView mIetFirstName, mIetLastName, mIetCountry, mIetLanguage, mIetExistStore, mIetNameStore,
            mIetAddressStore, mIetSkill, mIetOtherSkill, mIetPhoneNum, mIetWhatsapp, mIetPrice;
    private ImageView mIvAddStorePhoto;
    private LinearLayout mLlStoreInfo, mLlStorePhoto, mLlSkillSelection;
    private ArrayList<String> mArrUserPhotos = new ArrayList<>();
    private String mUserPhotoPath = "";
    private Bitmap mUserBitmap;
    private ArrayList<String> mArrStorePhotos = new ArrayList<>();//本地加载店铺图片集合
    private ArrayList<Bitmap> mArrStoreBitmap = new ArrayList<>();
    private String mMemberId;
    private Dialog mAlertDialog;
    private ListView mLvInfo;
    private ArrayAdapter<String> mAdpaterInfo;
    private int mIndexOnfo = 1;//1country,2language,3existStore
    private boolean isUpdateInfo = false;
    private UserInfoBean mUserBean;
    private RelativeLayout mRlUploadResult;
    private StringBuilder mSbResult = new StringBuilder("");//申请返回结果
    private RadioGroup mRgExistStore;
    private RadioButton mRbExistStore, mRbExistStoreNo;
    private List<String> mStore_photosList = new ArrayList<>();//记录当显示店铺图片路径
    private String[] mArrStorePathUploadSuccess;
    private boolean isAvatarChanged = false;
    private InputMethodManager imm;
    private ProgressDialogHelper mProgressHelper;
    private int mNumStorePhoto = 0;
    private RelativeLayout mRlCountry, mRlLanguage;

    private int mPrice = 1;
    private String mPhoneNum, mWhatsapp;

    private Handler mHalder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                /*case Constants.Msg.MSG_NOTIF_USERPHOTO:
                    if (mUserPhotoPath != null && !TextUtils.isEmpty(mUserPhotoPath)) {
                        File file = new File(mUserPhotoPath);
                        if (file.exists()) {
                            Glide.with(mContext)
                                    .load(mUserPhotoPath)
                                    .asBitmap()
                                    .placeholder(R.drawable.account_default_head_portrait)
                                    .centerCrop()
                                    .into(new BitmapImageViewTarget(mIvUserPhoto) {
                                        @Override
                                        protected void setResource(Bitmap resource) {
                                            RoundedBitmapDrawable circularBitmapDrawable =
                                                    RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                                            circularBitmapDrawable.setCircular(true);
                                            mIvUserPhoto.setImageDrawable(circularBitmapDrawable);
                                        }
                                    });
                        }
                    }
                    break;
                case Constants.Msg.MSG_NOTIF_STOREPHOTO:
                    List<String> photoPathList = (List<String>) msg.obj;
                    showStorePhotos(photoPathList);
                    break;
                case Constants.Msg.MSG_DEL_STOREPHOTO:
                    LogTool.d("MSG_DEL_STOREPHOTO" + (String) msg.obj);
                    String tagDel = (String) msg.obj;
                    if (tagDel != null) {
                        for (IconImageView imageView : mArrIvStorePhoto) {
                            LogTool.d("imageView tag" + (String) msg.obj);
                            if (tagDel.equalsIgnoreCase(String.valueOf(imageView.getTag()))) {
                                --mNumStorePhoto;
                                if (mNumStorePhoto < 0) {
                                    mNumStorePhoto = 0;
                                }
                                mLlStorePhoto.removeView(imageView);
                                if (!tagDel.startsWith(FLAG_URL)) {
                                    mArrStorePhotos.remove(tagDel);
                                }
                                for (Iterator<String> it = mStore_photosList.iterator(); it.hasNext(); ) {
                                    String path = it.next();
                                    if (tagDel.equalsIgnoreCase(path)) {
                                        it.remove();
                                        break;
                                    }
                                }
                                if (mLlStorePhoto.getChildCount() <= MAX_NUM_STORE_PHOTO) {
                                    mIvAddStorePhoto.setVisibility(View.VISIBLE);
                                }
                                break;
                            }
                        }
                    }
                    break;
                case Constants.Msg.MSG_INOF_NOTIFY:
                    if (mInfo != null && mInfo.getSkill() != null && mInfo.getSkill().size() > 0) {
                        mTCbSkill = new TitleCheckbox[mInfo.getSkill().size()];
                        for (int i = 0; i < mInfo.getSkill().size(); i++) {
                            TitleCheckbox cbox = new TitleCheckbox(mContext);
                            mTCbSkill[i] = cbox;
                            cbox.setTitle(Info.getSkillName(mContext, mInfo.getSkill().get(i)));
                            mLlSkillSelection.addView(cbox);
                        }
                    }
                    initData();
                    break;*/
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply);
        initToolBar(this, R.string.app_name);
        mContext = this;
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        initView();
        initInfo();
    }

    protected void initView() {
        /*mIvUserPhoto = (ImageView) findViewById(R.id.iv_user_photo);
        mIvDelPhoto = (ImageView) findViewById(R.id.iv_del_photo);
        mIvAddPhoto = (ImageView) findViewById(R.id.iv_add_photo);
        mIvClose = (ImageView) findViewById(R.id.iv_close);
        mIvSave.setOnClickListener(this);
        mIvDelPhoto.setOnClickListener(this);
        mIvAddPhoto.setOnClickListener(this);
        mIvClose.setOnClickListener(this);

        mIvCountrySelect.setOnClickListener(this);
        mIvLanguageSelect.setOnClickListener(this);
        mRlCountry.setOnClickListener(this);
        mRlLanguage.setOnClickListener(this);
        iv_savelayout.setOnClickListener(this);

        *//*mIetFirstName = (IconEditTextView) findViewById(R.id.iet_firstname_apply);
        mIetLastName = (IconEditTextView) findViewById(R.id.iet_lastname_apply);*//*
        mIetCountry.setOnClickListener(this);
        mIetLanguage.setOnClickListener(this);
        *//*mIetSkill = (IconEditTextView) findViewById(R.id.iet_skill_apply);
        mLlSkillSelection = (LinearLayout) findViewById(R.id.ll_skills_selection);
        mIetOtherSkill = (IconEditTextView) findViewById(R.id.iet_skill_other);*//*

        mRlUploadResult = (RelativeLayout) findViewById(R.id.rl_upload_result);
        mRlUploadResult.setVisibility(View.GONE);
        mRlUploadResult.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        mRbExistStore.setOnCheckedChangeListener(this);
        mRbExistStoreNo.setOnCheckedChangeListener(this);

        if (mIetFirstName != null) {
            mIetFirstName.setMaxLength(20);
        }
        if (mIetLastName != null) {
            mIetLastName.setMaxLength(20);
        }
        if (mIetNameStore != null) {
            mIetNameStore.setMaxLength(100);
        }
        if (mIetAddressStore != null) {
            mIetAddressStore.setMaxLength(100);
        }
        if (mIetOtherSkill != null) {
            mIetOtherSkill.setMaxLength(100);
        }

        mIetPhoneNum.setEnable(true);
        mIetWhatsapp.setEnable(true);
        mIetPrice.setEnable(true);*/

    }

    @Override
    protected void initData() {
        mProgressHelper = new ProgressDialogHelper(this);
        mSharedPre = mContext.getSharedPreferences(Constants.SHAERD_FILE_NAME, Context.MODE_PRIVATE);
        /*mExperter = (Experter) getIntent().getSerializableExtra("experter");
        mUserBean = reload(mContext);
        if (mExperter != null) {
            isUpdateInfo = true;
            initExperterInfo(mExperter);
            setInfo(mExperter);
        } else {
            mMemberId = ProtocolPreferences.getMemberId(mContext);
            mExperter = new Experter();
            mExperter.setMember_id(mMemberId);
            if (mUserBean != null) {
                mExperter.setAvatar(mUserBean.getAvatar());
                mExperter.setNickname(mUserBean.getNickname());
                mExperter.setFirst_name(mUserBean.getFirstname());
                mExperter.setLast_name(mUserBean.getLastname());
                mExperter.setEmail(mUserBean.getEmail());
                showNickNameInfo(mExperter);
                setInfo(mExperter);
            }
        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUserBitmap != null) {
            mUserBitmap.recycle();
            mUserBitmap = null;
        }
        if (mArrStoreBitmap != null && mArrStoreBitmap.size() > 0) {
            for (Bitmap bitmap : mArrStoreBitmap) {
                if (bitmap != null) {
                    bitmap.recycle();
                    mArrStoreBitmap.remove(bitmap);
                    bitmap = null;
                }
            }
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void requestData() {
        /*mQueue = Volley.newRequestQueue(mContext, new HurlStack());
        StringRequest mUpdate = new StringRequest(Request.Method.GET, ProtocolEncode.encodeCommontInfo(mContext), createMyReqSuccessListener(), createMyReqErrorListener()) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {//设置字符集为UTF-8,并采用gzip压缩传输
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Charset", "UTF-8");
                headers.put("Content-Type", "application/x-javascript");
                headers.put("Accept-Encoding", "gzip,deflate");
                return headers;
            }

            @Override
            public RetryPolicy getRetryPolicy() {//超时设置
                RetryPolicy retryPolicy = new DefaultRetryPolicy(SOCKET_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                return retryPolicy;
            }
        };
        mQueue.add(mUpdate);*/
    }

    //请求成功
    private Response.Listener<String> createMyReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String paramObject) {
                LogTool.i("paramObject:" + paramObject);
                if (null != paramObject && !TextUtils.isEmpty(paramObject)) {
                    /*mInfo = Info.saveInfo(mContext, paramObject);
                    if (mInfo != null) {
                        initInfo();
                    }*/
                }
            }
        };
    }

    //请求失败
    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    LogTool.e("TimeoutError");
                } else if (error instanceof AuthFailureError) {
                    LogTool.e("AuthFailureError=" + error.toString());
                }
            }
        };
    }


    @Override
    public void onClick(View v) {
        v.requestFocus();
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
        /*switch (v.getId()) {
            case R.id.iv_close:
                finish();
                break;
            case R.id.iv_del_photo:
                mHalder.sendEmptyMessage(Constants.Msg.MSG_DEL_USERPHOTO);
                break;
            case R.id.iv_add_photo:
                MultiImageSelector selector = MultiImageSelector.create(mContext);
                selector.showCamera(true);
                selector.single();
                selector.clip(true);//增加裁剪
                selector.start(ApplyActivity.this, Constants.Msg.REQUEST_IMAGE_USER);

                break;
            case R.id.iv_add_store_photo:
                if (mNumStorePhoto < MAX_NUM_STORE_PHOTO) {
                    Intent addintent = new Intent(mContext, MultiImageSelectorActivity.class);
                    addintent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);// whether show camera
                    addintent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, MAX_NUM_STORE_PHOTO - mNumStorePhoto);// max select image amount
                    // select mode (MultiImageSelectorActivity.MODE_SINGLE OR MultiImageSelectorActivity.MODE_MULTI)
                    addintent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
                    // default select images (support array list)
                    addintent.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mArrUserPhotos);
                    startActivityForResult(addintent, Constants.Msg.REQUEST_IMAGE_STORE);
                }
                break;
            case R.id.iet_country_apply:
                LogTool.d("iet_country_apply");
            case R.id.rl_apply_country:
                LogTool.d("rl_apply_country");
            case R.id.iv_country_arrow:
                mIndexOnfo = SELECT_COUNTRY;
                if (mInfo != null && mInfo.getCountry() != null && mInfo.getCountry().size() > 0) {
                    String[] arrStr = new String[mInfo.getCountry().size()];
                    for (int i = 0; i < mInfo.getCountry().size(); i++) {
                        arrStr[i] = mInfo.getCountry().get(i).getCountry_name();
                    }
                    showWindow(arrStr);
                } else {
                    LogTool.e("mInfo.getCountry() != null && mInfo.getCountry().size() > 0");
                }
                break;
            case R.id.iet_language_apply:
            case R.id.rl_apply_language:
            case R.id.iv_language_arrow:
                mIndexOnfo = SELECT_LANGUAGE;
                if (mInfo != null && mInfo.getLanguage() != null && mInfo.getLanguage().size() > 0) {
                    String[] arrStr = new String[mInfo.getLanguage().size()];
                    for (int i = 0; i < mInfo.getLanguage().size(); i++) {
                        arrStr[i] = mInfo.getLanguage().get(i).getLanguage_name();
                    }
                    showWindow(arrStr);
                }
                break;
            case R.id.iv_savelayout:
            case R.id.iv_save: {
                if (!checkInput()) {
                    return;
                }
                mProgressHelper.showProgressDialog(getString(R.string.apply_uploading));
                mExperter.setFirst_name(mIetFirstName.getInputText());
                mExperter.setLast_name(mIetLastName.getInputText());
                mExperter.setNickname(mIetFirstName.getInputText() + " " + mIetLastName.getInputText());

                mUserBean.setFirstname(mIetFirstName.getInputText());
                mUserBean.setLastname(mIetLastName.getInputText());
                mUserBean.setNickname(mIetFirstName.getInputText() + " " + mIetLastName.getInputText());
                if (mRbExistStore.isChecked()) {
                    mExperter.setHave_store(1);//默认
                } else {
                    mExperter.setHave_store(0);
                }
                mExperter.setStore_name(mIetNameStore.getInputText());
                mExperter.setStore_addr(mIetAddressStore.getInputText());

                if (mTCbSkill != null && mTCbSkill.length > 0) {
                    String[] arrSkill = new String[mTCbSkill.length];
                    for (int i = 0; i < mTCbSkill.length; i++) {
                        if (mTCbSkill[i].isChecked()) {
                            arrSkill[i] = String.valueOf(i + 1);
                        }
                    }
                    mExperter.setGoodat(arrSkill);
                }

                String[] arrOtherSkills = new String[1];
                arrOtherSkills[0] = mIetOtherSkill.getInputText();
                mExperter.setOther_goodat(arrOtherSkills);

                mExperter.setTel(mIetPhoneNum.getInputText());
                mExperter.setWhatsapp(mIetWhatsapp.getInputText());
                mExperter.setPrice(mIetPrice.getInputText());

                if (mUserPhotoPath != null && !TextUtils.isEmpty(mUserPhotoPath) && isAvatarChanged) {
                    uploadAvatar(mUserPhotoPath);
                } else if (mArrStorePhotos != null && mArrStorePhotos.size() > 0) {
                    compressPictures(mArrStorePhotos);
                } else {
                    updateUserInfo(mUserBean);
                }
            }
            break;
        }*/
    }

    /**
     * 选择头像剪切回调
     *
     * @param image
     */
    public void onAvatarSelect(Image image) {
//        ImageUtil.compressByQuality(image.getPath());
        mUserPhotoPath = image.path;
        Glide.with(this).load(image.path)
                .asBitmap()
                .placeholder(R.drawable.account_default_head_portrait)
                .centerCrop()
                .into(new BitmapImageViewTarget(mIvUserPhoto) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(mContext.getResources(), ImageCompressUtil.compressByQuality(resource));
                        circularBitmapDrawable.setCircular(true);
                        mIvUserPhoto.setImageDrawable(circularBitmapDrawable);
                    }
                });
        isAvatarChanged = true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        imm.hideSoftInputFromWindow(buttonView.getWindowToken(), 0); //强制隐藏键盘
        /*switch (buttonView.getId()) {
            case R.id.rb_exist_store:
                if (isChecked) {
                    mRbExistStoreNo.setChecked(false);
                    mLlStoreInfo.setVisibility(View.VISIBLE);
                } else {
                    mRbExistStoreNo.setChecked(true);
                    mRbExistStore.setChecked(false);
                    mLlStoreInfo.setVisibility(View.GONE);
                }
                break;
            case R.id.rb_exist_store_no:
                if (isChecked) {
                    mRbExistStore.setChecked(false);
                    mLlStoreInfo.setVisibility(View.GONE);
                } else {
                    mRbExistStoreNo.setChecked(false);
                    mRbExistStore.setChecked(true);
                    mLlStoreInfo.setVisibility(View.VISIBLE);
                }
                break;
            default:
                showSkills(mExperter);
                break;
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == Constants.Msg.REQUEST_IMAGE_USER) {
            if (resultCode == RESULT_OK) {
                List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                if (path != null && path.size() > 0) {
                    mUserPhotoPath = path.get(0);
                    mHalder.sendEmptyMessage(Constants.Msg.MSG_NOTIF_USERPHOTO);
                }
            }
        } else if (requestCode == Constants.Msg.REQUEST_IMAGE_STORE) {
            if (resultCode == RESULT_OK) {
                List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                if (path != null && path.size() > 0) {
                    mArrStorePhotos.addAll(path);
                    mStore_photosList.addAll(path);
                    Message msg = mHalder.obtainMessage(Constants.Msg.MSG_NOTIF_STOREPHOTO);
                    msg.obj = mStore_photosList;
                    msg.sendToTarget();
                }
            }
        }*/
    }

    /**
     * 初始化国家、语言、技能信息
     */
    private void initInfo() {
        /*mSharedPre = mContext.getSharedPreferences(Constants.SHAERD_FILE_NAME, Context.MODE_PRIVATE);

        String strInfoContent = Info.getInfoContent(mContext);
        if (strInfoContent != null && !TextUtils.isEmpty(strInfoContent)) {
            try {
                mInfo = new Gson().fromJson(strInfoContent, Info.class);
                mHalder.sendEmptyMessage(Constants.Msg.MSG_INOF_NOTIFY);
            } catch (Exception e) {
                mInfo = null;
                 e.printStackTrace();
            }
        }
        if (mInfo == null) {
            requestData();
        }*/
    }

    /**
     * 填充国家、语言、技能相关信息，申请状态默认第一项
     */
    private void setInfo() {
        /*if (mInfo != null && mInfo.getCountry() != null && mInfo.getCountry().size() > 0) {
            if (experter.getCountry_name() == null) {
                String countrySetting = Locale.getDefault().getCountry();
                int index = 0;
                if (countrySetting != null) {
                    index = mInfo.getCountryIndex(mInfo.getCountry(), countrySetting);
                }
                mIetCountry.setText(mInfo.getCountry().get(index).getCountry_name());
                experter.setCountry_id(mInfo.getCountry().get(index).getCountry_id());
                experter.setCountry_code(mInfo.getCountry().get(index).getCode());
            } else {
                mIetCountry.setText(experter.getCountry_name());
            }
        } else {
            LogTool.e("mInfo.getCountry() != null && mInfo.getCountry().size() > 0");
        }
        if (mInfo != null && mInfo.getLanguage() != null && mInfo.getLanguage().size() > 0) {
            if (experter.getLanguageName() == null) {
                String languageSetting = Locale.getDefault().getLanguage();
                int index = 0;
                if (languageSetting != null) {
                    index = mInfo.getLanguageIndex(mInfo.getLanguage(), languageSetting);
                }
                mIetLanguage.setText(mInfo.getLanguage().get(index).getLanguage_name());
                experter.setLanguage_id(mInfo.getLanguage().get(index).getLanguage_id());
            } else {
                mIetLanguage.setText(experter.getLanguageName());
            }
        }*/
    }

    /**
     * 国家语言选择框
     *
     * @param strs
     */
    private void showWindow(String[] strs) {
        /*if (mAlertDialog == null) {
            mLvInfo = new ListView(mContext);
            mAdpaterInfo = new ArrayAdapter(this, R.layout.item_list_info, strs);
            mLvInfo.setDivider(getResources().getDrawable(R.drawable.xml_list_divider));
            mLvInfo.setDividerHeight(1);
            mLvInfo.setAdapter(mAdpaterInfo);
            mAlertDialog = new AlertDialog.Builder(this).create();
            mAlertDialog.show();
            mAlertDialog.getWindow().setContentView(mLvInfo);
            // 创建一个PopuWidow对象
            DisplayMetrics metric = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metric);
            int width = metric.widthPixels;     // 屏幕宽度（像素）
            int height = metric.heightPixels;   // 屏幕高度（像素）
            mAlertDialog.getWindow().setLayout((int) (width * 0.75), (int) (height * 0.8));
        }
        mAdpaterInfo = new ArrayAdapter(this, R.layout.item_list_info, strs);
        mLvInfo.setAdapter(mAdpaterInfo);
        mLvInfo.setDivider(getResources().getDrawable(R.drawable.xml_list_divider));
        mLvInfo.setDividerHeight(1);
        mAdpaterInfo.notifyDataSetChanged();
        mAlertDialog.show();
        mAlertDialog.getWindow().setContentView(mLvInfo);
        mLvInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (mIndexOnfo) {
                    case SELECT_COUNTRY:
                        mIetCountry.setText(mInfo.getCountry().get(position).getCountry_name());
                        mExperter.setCountry_id(mInfo.getCountry().get(position).getCountry_id());
                        mExperter.setCountry_code(mInfo.getCountry().get(position).getCode());
                        break;
                    case SELECT_LANGUAGE:
                        mIetLanguage.setText(mInfo.getLanguage().get(position).getLanguage_name());
                        mExperter.setLanguage_id(mInfo.getLanguage().get(position).getLanguage_id());
                        break;
                    case SELECT_EXISTSTORE:
                        mIetExistStore.setText(mContext.getResources().getStringArray(R.array.arr_exist_store)[position]);
                        if (position == 0) {
                            mExperter.setHave_store(1);
                        } else {
                            mExperter.setHave_store(0);
                        }
                        break;
                }
                mAlertDialog.dismiss();
            }
        });*/
    }

    /**
     * 提交申请或提交修改
     *
     * @param isUpdate
     */
    private void requstApply(final boolean isUpdate) {
        //
        /*if (mStore_photosList != null && mStore_photosList.size() > 0) {
            for (Iterator<String> it = mStore_photosList.iterator(); it.hasNext(); ) {
                String path = it.next();
                if (path != null && !path.startsWith(FLAG_URL)) {
                    it.remove();
                }
            }
        }
        if (mArrStorePathUploadSuccess != null && mArrStorePathUploadSuccess.length > 0) {
            for (String path : mArrStorePathUploadSuccess) {
                mStore_photosList.add(path);
            }
        }
        if (mStore_photosList != null && mStore_photosList.size() > 0) {
            String[] newArr = new String[mStore_photosList.size()];
            for (int i = 0; i < mStore_photosList.size(); i++) {
                newArr[i] = mStore_photosList.get(i);
            }
            mExperter.setStore_photos(newArr);
        } else {
            mExperter.setStore_photos(new String[]{});
        }

        if (isUpdate) {
            if (mExperter.getStatus() == Constants.Status.FAILURE &&
                    mExperter.getUpdate_sign() == Constants.Status.UPDATE_SIGN_APPLY) {//申请失败
                String applyUrl = ProtocolEncode.encodeExperterApply(mContext, mExperter);
                uploadExperterPost(applyUrl);
                return;
            }
            String requestUrl = ProtocolEncode.encodeRequestToken(mContext);//修改信息A级接口，重新请求requestToken
            StringRequest rqrequest = new StringRequest(Request.Method.GET, requestUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    if (LoginUtils.isResponseOk(s)) {
                        RequestTokenBean requestToken = new Gson().fromJson(s, RequestTokenBean.class);
                        String requesttoken = requestToken.getRequest_token();
                        String applyUrl = ProtocolEncode.encodeExperterUpdate(mContext, mExperter, requesttoken);
                        uploadExperterPost(applyUrl);
                    } else {
                        showDialog(getString(R.string.Title_apply), String.valueOf(s));
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    showDialog(getString(R.string.Title_apply), mContext.getString(R.string.lbConnectTimeout));
                }
            });

            if (mQueue == null) {
                mQueue = Volley.newRequestQueue(mContext, new HurlStack());
            }
            mQueue.add(rqrequest);
        } else {
            String applyUrl = ProtocolEncode.encodeExperterApply(mContext, mExperter);
            uploadExperterPost(applyUrl);
        }*/
    }

    /**
     * 修改专家信息为审核中
     */
    private void updateExperterStatusPending() {
        /*if (mSharedPre != null) {
            SharedPreferences.Editor editor = mSharedPre.edit();
            editor.putInt(Constants.Shared_Experter.STATUS_CODE, Constants.Status.PENDING);
            editor.commit();
        }*/
    }

    /**
     * post上传专家信息
     *
     * @param url
     */
    private void uploadExperterPost(String url) {
        /*StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                mProgressHelper.dismissProgressDialog();
                LogTool.d("onResponse : " + s);
                try {
                    ResponseStatus status = new Gson().fromJson(s, ResponseStatus.class);
                    if (ResponseStatus.RESOPNSE_SUCCESS.equalsIgnoreCase(status.getStatus())) {
                        mProgressHelper.dismissProgressDialog();
                        mRlUploadResult.setVisibility(View.VISIBLE);
                        mIvSave.setVisibility(View.GONE);
                        updateExperterStatusPending();
                        GACollect.getInstance().CollectEvent("ExpertApplySuccess");
                        return;
                    } else if (status.getError() == Constants.Status.STATUS_NO_UPDATE) {//没有修改内容提交
                        DFNToast.Show(mContext, R.string.apply_update_no, Toast.LENGTH_SHORT);
                    } else {
                        showDialog(getString(R.string.Title_apply), status.getError() + status.getMsg());
                        GACollect.getInstance().CollectEvent("ExpertApplyFails");
                    }
                } catch (Exception e) {
                    showDialog(getString(R.string.Title_apply), String.valueOf(e));
                    GACollect.getInstance().CollectEvent("ExpertApplyFails");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mProgressHelper.dismissProgressDialog();
                volleyError.printStackTrace();
                showDialog(getString(R.string.Title_apply), mContext.getString(R.string.lbConnectTimeout));
                LogTool.e("get tmpid error" + volleyError.getMessage());
                GACollect.getInstance().CollectEvent("ExpertApplyFails");
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new LinkedHashMap<>();
                map.put("info[member_id]", mExperter.getMember_id());
                map.put("info[nickname]", mExperter.getNickname());
                map.put("info[first_name]", mExperter.getFirst_name());
                map.put("info[last_name]", mExperter.getLast_name());
                map.put("info[avatar]", mExperter.getAvatar());
                map.put("info[language_id]", mExperter.getLanguage_id());
                map.put("info[country_id]", mExperter.getCountry_id());

                map.put("info[tel]", mExperter.getTel());
                map.put("info[whatsapp]", mExperter.getWhatsapp());
                map.put("info[price]", mExperter.getPrice());

                boolean hasGoodat = false;
                String[] arrGoodat = mExperter.getGoodat();
                if (arrGoodat != null && arrGoodat.length > 0) {
                    int i = 0;
                    for (String strGoodat : arrGoodat) {
                        if (strGoodat != null && !TextUtils.isEmpty(strGoodat)) {
                            hasGoodat = true;
                            map.put("info[goodat][" + (i++) + "]", String.valueOf(strGoodat));
                        }
                    }
                } else {
                    LogTool.e("mExperter.getGoodat() == null");
                }
                if (!hasGoodat) {
                    map.put("info[goodat][]", "");
                }
                hasGoodat = false;
                map.put("info[have_store]", String.valueOf(mExperter.getHave_store()));
                if (mExperter.getHave_store() == 1) {
                    map.put("info[store_name]", mExperter.getStore_name());
                    map.put("info[store_addr]", mExperter.getStore_addr());
                    String[] arrPhotos = mExperter.getStore_photos();
                    if (arrPhotos != null && arrPhotos.length > 0) {
                        int i = 0;
                        for (String strPhoto : arrPhotos) {
                            if (strPhoto != null && !TextUtils.isEmpty(strPhoto)) {
                                hasGoodat = true;
                                map.put("info[store_photos][" + (i++) + "]", strPhoto);
                            }
                        }
                    }
                } else {
                    map.put("info[store_name]", "");
                    map.put("info[store_addr]", "");
                }
                if (!hasGoodat) {
                    map.put("info[store_photos][]", "");
                }
                hasGoodat = false;
                String[] arrOther = mExperter.getOther_goodat();
                if (arrOther != null && arrOther.length > 0) {
                    int i = 0;
                    for (String strOther : arrOther) {
                        if (strOther != null && !TextUtils.isEmpty(strOther)) {
                            hasGoodat = true;
                            map.put("info[other_goodat][" + (i++) + "]", strOther);
                        }
                    }
                }
                if (!hasGoodat) {
                    map.put("info[other_goodat][]", "");
                }
                LogTool.d(String.valueOf(checkParams(map)));
                return map;
            }

            private Map<String, String> checkParams(Map<String, String> map) {
                Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> pairs = it.next();
                    if (pairs.getValue() == null) {
                        map.put(pairs.getKey(), "");
                    }
                }
                return map;
            }
        };
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(mContext, new HurlStack());
        }
        mQueue.add(request);*/

    }

    /**
     * 初始化 detail传过来的数据
     */
    private void initExperterInfo() {
        /*showNickNameInfo(experter);
        mIetCountry.setText(experter.getCountry_name());
        mIetLanguage.setText(experter.getLanguageName());
        mIetPhoneNum.setText(experter.getTel());
        mIetWhatsapp.setText(experter.getWhatsapp());

        if (Double.valueOf(mExperter.getPrice()).intValue() > 0) {
            mIetPrice.setText(String.valueOf(Double.valueOf(mExperter.getPrice()).intValue()));
        }

        switch (experter.getHave_store()) {
            case 1:
                mRbExistStore.setChecked(true);
                mRbExistStoreNo.setChecked(false);
                mLlStoreInfo.setVisibility(View.VISIBLE);
                break;
            case 0:
                mRbExistStoreNo.setChecked(true);
                mRbExistStore.setChecked(false);
                mLlStoreInfo.setVisibility(View.GONE);
                break;
        }
        if (experter.getHave_store() == 1) {
            mIetNameStore.setText(experter.getStore_name());
            mIetAddressStore.setText(experter.getStore_addr());
            if (experter.getStore_photos() != null && experter.getStore_photos().length > 0) {//加载店铺照片
                Message msg = mHalder.obtainMessage(Constants.Msg.MSG_NOTIF_STOREPHOTO);
                for (String path : experter.getStore_photos()) {
                    mStore_photosList.add(path);
                }
                msg.obj = mStore_photosList;
                msg.sendToTarget();
            }
        }
        if (experter.getGoodat() != null && experter.getGoodat().length > 0 && mTCbSkill != null && mTCbSkill.length > 0) {
            for (String skillId : experter.getGoodat()) {
                try {
                    int id = Integer.valueOf(skillId);
                    mTCbSkill[id - 1].setChecked(true);
                } catch (Exception e) {
                    LogTool.e("error" + e.getMessage());
                }
            }
            showSkills(experter);
        }
        if (experter.getOther_goodat() != null && experter.getOther_goodat().length > 0) {
            StringBuilder sbOtherSkill = new StringBuilder();
            for (String otherSkill : experter.getOther_goodat()) {
                sbOtherSkill.append(otherSkill);
                sbOtherSkill.append("/");
            }
            if (sbOtherSkill != null && sbOtherSkill.length() > 0) {
                mIetOtherSkill.setText(sbOtherSkill.substring(0, sbOtherSkill.length() - 1));
            }
        }*/
    }


    private void showSkills() {
        /*StringBuilder strSkill = new StringBuilder("");
        mIetSkill.setText(String.valueOf(strSkill));
        for (int i = 0; i < mCbSkill.length; i++) {
            CheckBox cbox = mCbSkill[i];
            if (cbox.isChecked()) {
                strSkill.append(String.format(Locale.US, mInfo.getSkill().get(i).getSkill_name()));
                strSkill.append("/");
            }
        }
        if (experter.getOther_goodat() != null && experter.getOther_goodat().length > 0) {
            for (String skillId : experter.getOther_goodat()) {
                strSkill.append(skillId);
                strSkill.append("/");
            }
        }
        if (!TextUtils.isEmpty(strSkill)) {
            String skills = strSkill.substring(0, (strSkill.length() - 1));
            mIetSkill.setText(skills);
        }*/
    }

    /**
     * 读取注册用户信息
     *
     * @param context
     */
    public UserInfoBean reload(Context context) {
        UserInfoBean userInfoBean = null;
        String jsonStr = ProtocolPreferences.getUserInfo(context);
        if (!"".equals(jsonStr)) {
            userInfoBean = new Gson().fromJson(jsonStr, UserInfoBean.class);
        }
        return userInfoBean;
    }

    /**
     * 加载注册的信息
     *
     */
    private void showNickNameInfo() {
        /*if (experter.getAvatar() != null && !TextUtils.isEmpty(experter.getAvatar())) {
            Glide.with(mContext)
                    .load(mExperter.getAvatar())
                    .asBitmap()
                    .placeholder(R.drawable.account_default_head_portrait)
                    .centerCrop()
                    .into(new BitmapImageViewTarget(mIvUserPhoto) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            mIvUserPhoto.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        }
        mIetFirstName.setText(experter.getFirst_name());
        mIetLastName.setText(experter.getLast_name());*/
    }

    /**
     * 上传头像
     *
     * @param avatarPath
     */
    private void uploadAvatar(final String avatarPath) {
        /*String loginUrl = ProtocolEncode.encodeUploadPicture(mContext, avatarPath);
        PhotoMultipartRequest imageUploadReq = new PhotoMultipartRequest<>(loginUrl, AvatarBean.class, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                showDialog(getString(R.string.apply_upload_avatar), mContext.getString(R.string.lbConnectTimeout));
            }
        }, new Response.Listener<AvatarBean>() {
            @Override
            public void onResponse(AvatarBean avatarBean) {
                Log.i("ashes", avatarBean.toString());
                mSbResult.append("UPLOAD Avatar: \n");
                mSbResult.append(avatarBean.toString());
                mSbResult.append("\n");
                if (avatarBean != null && !TextUtils.isEmpty(String.valueOf(avatarBean.getAvatar()))) {
                    mExperter.setAvatar(avatarBean.getAvatar());
                    if (mUserBean != null) {
                        mUserBean.setAvatar(avatarBean.getAvatar());
                        ProtocolPreferences.setUserInfo(mContext, new Gson().toJson(mUserBean));
                    }
                    if (mArrStorePhotos != null && mArrStorePhotos.size() > 0) {
                        compressPictures(mArrStorePhotos);
                    } else {
                        updateUserInfo(mUserBean);
                    }
                }
            }
        }, new File(avatarPath));
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(mContext, new HurlStack());
        }
        mQueue.add(imageUploadReq);*/
    }

    /**
     * 上传店铺照片
     *
     * @param pictureList
     */
    private void compressPictures(List<String> pictureList) {
        new CompressTask().execute(pictureList);
    }


    /**
     * 提交前判空提醒
     */
    private boolean checkInput() {
        /*if ((mUserPhotoPath == null || TextUtils.isEmpty(mUserPhotoPath)) && (mExperter.getAvatar() == null || TextUtils.isEmpty(mExperter.getAvatar()))) {
            DFNToast.Show(mContext, getString(R.string.apply_avatar) + getString(R.string.apply_check_input_null), Toast.LENGTH_SHORT);
            return false;
        }
        String fist_name = mIetFirstName.getInputText();
        String last_name = mIetLastName.getInputText();
        String country = mIetCountry.getInputText();
        String language = mIetLanguage.getInputText();
        String skill = mIetSkill.getInputText();
        String storeName = mIetNameStore.getInputText();
        String storeAddr = mIetAddressStore.getInputText();
        String phoneNum = mIetPhoneNum.getInputText();
        String price = mIetPrice.getInputText();
        if (fist_name == null || TextUtils.isEmpty(fist_name)) {
            DFNToast.Show(mContext, getString(R.string.firstname_apply) + getString(R.string.apply_check_input_null), Toast.LENGTH_SHORT);
            return false;
        } else if (last_name == null || TextUtils.isEmpty(last_name)) {
            DFNToast.Show(mContext, getString(R.string.lastname_apply) + getString(R.string.apply_check_input_null), Toast.LENGTH_SHORT);
            return false;
        } else if (country == null || TextUtils.isEmpty(country)) {
            DFNToast.Show(mContext, getString(R.string.country_apply) + getString(R.string.apply_check_input_null), Toast.LENGTH_SHORT);
            return false;
        } else if (language == null || TextUtils.isEmpty(language)) {
            DFNToast.Show(mContext, getString(R.string.language_apply) + getString(R.string.apply_check_input_null), Toast.LENGTH_SHORT);
            return false;
        } else if (phoneNum == null || TextUtils.isEmpty(phoneNum)) {
            DFNToast.Show(mContext, getString(R.string.phonenum_apply) + getString(R.string.apply_check_input_null), Toast.LENGTH_SHORT);
            return false;
        } else if (price == null || TextUtils.isEmpty(price)) {
            DFNToast.Show(mContext, getString(R.string.price_apply) + getString(R.string.apply_check_input_null), Toast.LENGTH_SHORT);
            return false;
        } else if (Double.valueOf(price).intValue() == 0 || Double.valueOf(price).intValue() > DFNConfig.getInstance().getMaxCharge()) {
            String tip = String.format(getString(R.string.apply_check_price), DFNConfig.getInstance().getMaxCharge());
            DFNToast.Show(mContext, tip, Toast.LENGTH_SHORT);
            return false;
        } else if (mRbExistStore.isChecked()) {
            if (storeName == null || TextUtils.isEmpty(storeName)) {
                DFNToast.Show(mContext, getString(R.string.name_store_apply) + getString(R.string.apply_check_input_null), Toast.LENGTH_SHORT);
                return false;
            } else if (storeAddr == null || TextUtils.isEmpty(storeAddr)) {
                DFNToast.Show(mContext, getString(R.string.address_store_apply) + getString(R.string.apply_check_input_null), Toast.LENGTH_SHORT);
                return false;
            } else if (mStore_photosList == null || mStore_photosList.size() == 0) {
                DFNToast.Show(mContext, getString(R.string.photo_store_apply) + getString(R.string.apply_check_input_null), Toast.LENGTH_SHORT);
                return false;
            }
        }*/


        /*else if(skill == null || TextUtils.isEmpty(skill)){
            DFNToast.Show(mContext, getString(R.string.apply_skill_info) + getString(R.string.apply_check_input_null), Toast.LENGTH_SHORT);
            return false;
        }*/
        return true;
    }

    /**
     * 修改WAF信息
     */
    private void updateUserInfo(final UserInfoBean userInfoBean) {
        /*if (userInfoBean == null) {
            return;
        }
        String url = ProtocolEncode.encodeUpdateUserInfo(this, userInfoBean.getFirstname(), userInfoBean.getLastname());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.i("ashes", "updateUserInfo:  " + s);
                try {
                    JSONObject object = new JSONObject(s);
                    if (object.optString("status").equals("ok")) {
                        requstApply(isUpdateInfo);
                        ProtocolPreferences.setUserInfo(mContext, new Gson().toJson(mUserBean));
                        mContext.getSharedPreferences(Constants.SHAERD_FILE_NAME, Context.MODE_PRIVATE).edit().putInt(Constants.Shared_Experter.STATUS_CODE, mExperter.getStatus()).commit();
                        EventBus.getDefault().post(mUserBean);
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                showDialog(getString(R.string.apply_upload_experter_info), mContext.getString(R.string.time_out));
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("firstname", userInfoBean.getFirstname());
                params.put("lastname", userInfoBean.getLastname());
                return params;
            }
        };
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(mContext, new HurlStack());
        }
        mQueue.add(request);*/
    }

    /*
     * 异步任务执行压缩图片
     * */
    public class CompressTask extends AsyncTask<List<String>, Void, List<Map<String, Object>>> {
        //上面的方法中，第一个参数：网络图片的路径，第二个参数的包装类：进度的刻度，第三个参数：任务执行的返回结果
        @Override
        //在界面上显示进度条
        protected void onPreExecute() {
            LogTool.d("onPreExecute");
        }

        @Override
        protected List<Map<String, Object>> doInBackground(List<String>... params) {
            LogTool.d("doInBackground");
            return compressPicture(params[0]);
        }

        //主要是更新UI
        @Override
        protected void onPostExecute(List<Map<String, Object>> result) {
            LogTool.d("onPostExecute");
            super.onPostExecute(result);
            //更新UI
            if (result != null && result.size() > 0) {
//                uploadPictures(result, mExperter.getMember_id());
            }
        }
    }

    private List<Map<String, Object>> compressPicture(List<String> mArrImgPath) {
        List<Map<String, Object>> list = new ArrayList<>();
        /*for (String path : mArrImgPath) {
            Map<String, Object> map = new HashMap<>();
            LogTool.d("compressPicture" + path);
            File imgFile = new File(path);
            LogTool.d("Befor commpressor file size:" + getReadableFileSize(imgFile.length()));
            File compressedImageFile = Compressor.getDefault(mContext).compressByQualityToFile(imgFile);
            path = compressedImageFile.getPath();
            LogTool.d("commpressor file size:" + getReadableFileSize(compressedImageFile.length()) + "pathNew:" + path);
            map.put(PictureMultUploadRequest.IMG_PATH, path);
            map.put(PictureMultUploadRequest.IMG_FILE, compressedImageFile);
            list.add(map);
        }*/
        return list;
    }

    /**
     * 获取文件大小
     *
     * @param size
     * @return
     */
    public String getReadableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public void doSelectPicture(View view){
        MultiImageSelector.create(mContext)
                .showCamera(true)
                .count(1)
                .single()
                .start(ApplyActivity.this, REQUEST_IMAGE);
    }
}
