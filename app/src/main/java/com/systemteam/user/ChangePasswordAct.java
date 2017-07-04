package com.systemteam.user;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.systemteam.R;

import org.json.JSONException;
import org.json.JSONObject;


public class ChangePasswordAct extends RequestBaseAct {

    IconEditFullTextView oldPasswdEt;
    IconEditFullTextView newPasswdEt;
    IconEditFullTextView confirmNewPasswdEt;
    Button submit;
    TextView tip;

    private ProgressDialogHelper mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        mProgress = new ProgressDialogHelper(this);
        setupResponseCallback();
        initializeView();
    }

    @Override
    public void setupResponseCallback() {
        listener = new ResponseListener(this) {
            @Override
            public void onResponse(String s) {
                super.onResponse(s);
                String oldPassword = oldPasswdEt.getInputText();
                String newPassword = newPasswdEt.getInputText();
                String changePasswordUrl = ProtocolEncode.encodePwdUpdate(ChangePasswordAct.this, oldPassword, newPassword);
                StringRequest request = new StringRequest(Request.Method.GET, changePasswordUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        mProgress.dismissProgressDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            if (!jsonObject.isNull("error")) {
                                JSONObject object = jsonObject.optJSONObject("error");
                                if (object.optInt("code") == 470) {
                                    showToastShort(R.string.account_tip_original_pwd_error);
                                } else {
                                    showToastShort(object.optString("msg"));
                                }
                            } else {
                                showToastShort(R.string.account_tip_change_pwd_success);
                                ChangePasswordAct.this.finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        mProgress.dismissProgressDialog();
                        showToastShort(volleyError.getMessage());
                    }
                });
                requestQueue.add(request);
            }
        };
    }

    private boolean checkPasswords() {
        String oldPassword = oldPasswdEt.getInputText();
        String newPassword = newPasswdEt.getInputText();
        String confirmPassword = confirmNewPasswdEt.getInputText();
        if (TextUtils.isEmpty(oldPassword)) {
            tip.setText(R.string.account_tip_original_pwd);
            return false;
        }
        if (TextUtils.isEmpty(newPassword)) {
            tip.setText(R.string.account_tip_new_pwd);
            return false;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            tip.setText(R.string.account_tip_confirm_pwd);
            return false;
        } else if (!newPassword.equals(confirmPassword)) {
            tip.setText(R.string.account_passwords_different);
            return false;
        } else if (oldPassword.length() < 6 || oldPassword.length() > 16) {
            showToastShort(R.string.account_tip_pwd_length);
            return false;
        } else if (newPassword.length() < 6 || newPassword.length() > 16) {
            showToastShort(R.string.account_tip_pwd_length);
            return false;
        } else {
            return true;
        }
    }

    private void initializeView() {
        oldPasswdEt = (IconEditFullTextView) findViewById(R.id.old_passwd);
        oldPasswdEt.clearPadding();
        oldPasswdEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        newPasswdEt = (IconEditFullTextView) findViewById(R.id.new_passwd);
        newPasswdEt.clearPadding();
        newPasswdEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        confirmNewPasswdEt = (IconEditFullTextView) findViewById(R.id.confirm_new_passwd);
        confirmNewPasswdEt.clearPadding();
        confirmNewPasswdEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        if (newPasswdEt != null) {
            newPasswdEt.setMaxLength(20);
        }
        if (confirmNewPasswdEt != null) {
            confirmNewPasswdEt.setMaxLength(20);
        }

        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPasswords()) {
                    if (!NetworkUtil.isNetworkAvailable(ChangePasswordAct.this)) {
                        showToastShort(R.string.networkerror);
                        return;
                    }
                    mProgress.showProgressDialog(getResources().getString(R.string.account_tip_changing_pwd));
                    getRequestToken(listener);
                }
            }
        });

        tip = (TextView) findViewById(R.id.tip);
    }


}
