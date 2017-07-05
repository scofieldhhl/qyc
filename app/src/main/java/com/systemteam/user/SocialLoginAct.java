package com.systemteam.user;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.systemteam.R;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SocialLoginAct extends RequestBaseAct {

    public static final String TAG = SocialLoginAct.class.getSimpleName();
    WebView webView;
    WebViewClient client;
    String type;
    int oauthSite;
    //    private ProDialog dialog;
    private ProgressDialogHelper mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_login);
        type = getIntent().getStringExtra("type");

        if (type.equals(LoginUtils.TYPE_FACEBOOK))
            oauthSite = 0;
        else if (type.equals(LoginUtils.TYPE_TWITTER))
            oauthSite = 1;
        else
            return;

        mProgress = new ProgressDialogHelper(this);

        setupResponseCallback();
        webView = (WebView) findViewById(R.id.login_web);
        webView.getSettings().setJavaScriptEnabled(true);
        client = new CustomWebClient();
        webView.setWebViewClient(client);
        getRequestToken(listener);

    }

    @Override
    protected void setupResponseCallback() {
        listener = new ResponseListener(this) {
            @Override
            public void onResponse(String s) {
                super.onResponse(s);
                if (LoginUtils.isResponseOk(s)) {
                    String oauthUrl = ProtocolEncode.encodeOauthLogin(SocialLoginAct.this, oauthSite);
                    Log.d(TAG, "oauthUrl : " + oauthUrl);
//                    showDialog();
                    mProgress.showProgressDialog(getResources().getString(R.string.account_waiting_server_response));
                    webView.loadUrl(oauthUrl);
                }
            }
        };
    }

//    private synchronized void showDialog() {
//        if (null == dialog) {
//            dialog = new ProDialog(this, 2);
//            Log.d(TAG, "show Dialog");
//            dialog.show();
//        }
//    }
//
//    private synchronized void dismissDialog() {
//        if (null != dialog && dialog.isShowing()) {
//            Log.d(TAG, "dismiss Dialog");
//            dialog.dismiss();
//            dialog = null;
//        }
//    }

    private class CustomWebClient extends WebViewClient {
        Pattern pattern = Pattern.compile("result=(.*)");
        boolean successFlag = false;

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d(TAG + " in WebClient :", url);
//            dismissDialog();
            mProgress.dismissProgressDialog();
            if (url.contains(LoginUtils.FLAG_SUCCESS)) {
                if (!successFlag) {
                    successFlag = true;
                    Log.d(TAG + " in WebClient : ", "web SUCCESS : " + url);
                    webView.setVisibility(View.INVISIBLE);
                    CommonLoginBean result = extractInfo(url);
                    if (null != result) {
                        processOauthResult(result);
                        GACollect.getInstance().CollectEvent(type + "SignUpSuccess");
                    } else {
                        showToastShort(R.string.loading_failed);
                        GACollect.getInstance().CollectEvent(type + "SignUpFail");
                    }
                } else
                    Log.d(TAG + " in WebClient : ", "get  SUCCESS flag again : " + url);
            }
        }

        private void processOauthResult(CommonLoginBean result) {
            String getUserInfoUrl = ProtocolEncode.encodeGetUserInfo(SocialLoginAct.this, result.getMember_id());
            StringRequest request = new StringRequest(Request.Method.GET, getUserInfoUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String s) {
                    if (LoginUtils.isResponseOk(s)) {
                        s = s.replaceFirst("\"social_network\":\\[.*?\\],", "");
                        UserInfoBean bean = new Gson().fromJson(s, UserInfoBean.class);
                        Log.d(TAG, " email : " + bean.getEmail());
                        processUserInfo(bean, s);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            }) {
                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String str = null;
                    try {
                        str = new String(response.data, "UTF-8");
                        Log.d(TAG, "raw : " + str);
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    return Response.success(str, HttpHeaderParser.parseCacheHeaders(response));
                }
            };
            requestQueue.add(request);
        }

        private void processUserInfo(UserInfoBean bean, String rawContent) {
            // TODO: 2016/3/7  persist user info
//            if (null == bean.getEmail() || "".equals(bean.getEmail())) {
//                Log.d(TAG, " email : " + bean.getEmail());
//                Intent it = new Intent(SocialLoginAct.this, SignUpAfterOauthAct.class);
//                it.putExtra("type", type);
//                it.putExtra(ProtocolPreferences.MEMBER_ID, bean.getMember_id());
//                startActivity(it);
//                finish();
//            } else {
                Log.d(TAG, " rawContent : " + rawContent);
                ProtocolPreferences.setUserInfo(SocialLoginAct.this, rawContent);
//                startActivity(new Intent(SocialLoginAct.this, MainActivity.class));
//                finish();
                ProtocolPreferences.setMemberId(SocialLoginAct.this, bean.getMember_id());
                PrefsAccessor.getInstance(SocialLoginAct.this).saveBoolean(Constants.KEY_THIRD_LOGIN, true);
                // TODO: 2016/3/7  change to account info activity
//            }
        }

        private CommonLoginBean extractInfo(String url) {
            Matcher mo = pattern.matcher(url);
            String result;
            if (mo.find()) {
                result = mo.group(1);
                Log.d(TAG, result);
                try {
                    String decodedResult = URLDecoder.decode(result, "UTF-8");
                    Log.d(TAG, " decode Result : " + decodedResult);
                    CommonLoginBean bean = new Gson().fromJson(decodedResult, CommonLoginBean.class);
                    ProtocolPreferences.setAccountInfo(SocialLoginAct.this, decodedResult);
                    ProtocolPreferences.setTokenAccess(SocialLoginAct.this, bean.getAccess_token());
//                    ProtocolPreferences.setMemberId(SocialLoginAct.this, bean.getMember_id());
                    ProtocolPreferences.setPotocolLoginTimestamp(SocialLoginAct.this, "" + System.currentTimeMillis() / 1000);
                    return bean;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
