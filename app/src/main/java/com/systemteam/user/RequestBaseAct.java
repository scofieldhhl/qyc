package com.systemteam.user;

import android.content.Context;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;


/**
 * Created by admin on 2016/3/9.
 */
public abstract class RequestBaseAct extends BaseToolbarActivity {
    protected RequestQueue requestQueue;
    protected Response.Listener<String> listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = mApp.getRequestQueue();
    }

    protected void getRequestToken(Response.Listener<String> listener) {
        String requestUrl = ProtocolEncode.encodeRequestToken(this);
        StringRequest request = new StringRequest(Request.Method.GET, requestUrl, listener
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        requestQueue.add(request);
    }

    protected abstract void setupResponseCallback();

    public static class ResponseListener implements Response.Listener<String> {
        private Context context;

        public ResponseListener(Context context) {
            this.context = context;
        }

        @Override
        public void onResponse(String s) {
            if (LoginUtils.isResponseOk(s)) {
                RequestTokenBean requestToken = new Gson().fromJson(s, RequestTokenBean.class);
                ProtocolPreferences.setTokenRequest(context, requestToken.getRequest_token());
            }
        }
    }

//    public void setUpOutsideTouchListener(View view) {
//        //Set up touch listener for non-text box views to hide keyboard.
//        if (!(view instanceof EditText)) {
//
//            view.setOnTouchListener(new View.OnTouchListener() {
//
//                public boolean onTouch(View v, MotionEvent event) {
//                    hideSoftKeyboard(RequestBaseAct.this);
//                    return false;
//                }
//
//            });
//        }
//
//        //If a layout container, iterate over children and seed recursion.
//        if (view instanceof ViewGroup) {
//            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
//                View innerView = ((ViewGroup) view).getChildAt(i);
//                setUpOutsideTouchListener(innerView);
//            }
//        }
//    }



}
