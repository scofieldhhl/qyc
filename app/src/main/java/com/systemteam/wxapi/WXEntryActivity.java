package com.systemteam.wxapi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.systemteam.BaseActivity;
import com.systemteam.Main2Activity;
import com.systemteam.R;
import com.systemteam.bean.MyUser;
import com.systemteam.util.Constant;
import com.systemteam.util.LogTool;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.ShowMessageFromWX;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 微信登陆AccessToken查询https://1.rockingcar.applinzi.com/wxAccessToken?code="123456"
 * 返回：{"code": "1", "data": "{\"errcode\":40029,\"errmsg\":\"invalid code,
 * hints: [ req_id: QATHsa0479th48 ]\"}"}{"code": "0","msg":"错误原因"}
 * https://1.rockingcar.applinzi.com/wxAccessToken?code= 这个是微信SDK返回的
 * **/
public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler{

	private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;

	private Button gotoBtn, regBtn, launchBtn, checkBtn, scanBtn;

	// IWXAPI 是第三方app和微信通信的openapi接口
	private IWXAPI api;
	private Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogTool.d("onCreate");
		mContext = this;
		/*setContentView(R.layout.entry);*/

		// 通过WXAPIFactory工厂，获取IWXAPI的实例
		api = WXAPIFactory.createWXAPI(this, Constant.WX_APP_ID, false);

		/*regBtn = (Button) findViewById(R.id.reg_btn);
		regBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 将该app注册到微信
				api.registerApp(Constant.APP_ID);
			}
		});

		gotoBtn = (Button) findViewById(R.id.goto_send_btn);
		gotoBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(WXEntryActivity.this, SendToWXActivity.class));
				finish();
			}
		});

		launchBtn = (Button) findViewById(R.id.launch_wx_btn);
		launchBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(WXEntryActivity.this, "launch result = " + api.openWXApp(), Toast.LENGTH_LONG).show();
			}
		});

		checkBtn = (Button) findViewById(R.id.check_timeline_supported_btn);
		checkBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				int wxSdkVersion = api.getWXAppSupportAPI();
				if (wxSdkVersion >= TIMELINE_SUPPORTED_VERSION) {
					Toast.makeText(WXEntryActivity.this, "wxSdkVersion = " + Integer.toHexString(wxSdkVersion) + "\ntimeline supported", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(WXEntryActivity.this, "wxSdkVersion = " + Integer.toHexString(wxSdkVersion) + "\ntimeline not supported", Toast.LENGTH_LONG).show();
				}
			}
		});

		scanBtn = (Button) findViewById(R.id.scan_qrcode_login_btn);
		scanBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(WXEntryActivity.this, ScanQRCodeLoginActivity.class));
				finish();
			}
		});*/

		//注意：
		//第三方开发者如果使用透明界面来实现WXEntryActivity，
		// 需要判断handleIntent的返回值，如果返回值为false，
		// 则说明入参不合法未被SDK处理，应finish当前透明界面，
		// 避免外部通过传递非法参数的Intent导致停留在透明界面，引起用户的疑惑
		try {
			api.handleIntent(getIntent(), this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		LogTool.d("onNewIntent");
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	// 微信发送请求到第三方应用时，会回调到该方法
	@Override
	public void onReq(BaseReq req) {
		LogTool.d("onReq");
		switch (req.getType()) {
			case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
				goToGetMsg();
				break;
			case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
				goToShowMsg((ShowMessageFromWX.Req) req);
				break;
			default:
				break;
		}
	}

	private static final int RETURN_MSG_TYPE_LOGIN = 1;
	private static final int RETURN_MSG_TYPE_SHARE = 2;

	// 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
	@Override
	public void onResp(BaseResp resp) {
		int result;
		LogTool.e("onPayFinish errCode = " + resp.errCode);

		switch (resp.errCode) {
			case BaseResp.ErrCode.ERR_AUTH_DENIED:
				result = R.string.errcode_deny;
				break;
			case BaseResp.ErrCode.ERR_UNSUPPORT:
				result = R.string.errcode_unsupported;
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL:
				if (RETURN_MSG_TYPE_SHARE == resp.getType())
					Toast.makeText(this,"分享失败", Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(this, R.string.account_tip_login_failed, Toast.LENGTH_SHORT).show();
				this.finish();
				break;
			case BaseResp.ErrCode.ERR_OK:
				switch (resp.getType()) {
					case RETURN_MSG_TYPE_LOGIN:
						//拿到了微信返回的code,立马再去请求access_token
						String code = ((SendAuth.Resp) resp).code;
						LogTool.d("code = " + code);
						//TODO 改成通过后台方式
//						getAccess_token(code);
						loadWxInfo(code);
						//就在这个地方，用网络库什么的或者自己封的网络api，发请求去咯，注意是get请求

						break;

					case RETURN_MSG_TYPE_SHARE:
						Toast.makeText(this,"微信分享成功", Toast.LENGTH_SHORT).show();
						finish();
						break;
				}
				break;

			default:
				result = R.string.errcode_unknown;
				break;
		}

	}

	private void goToGetMsg() {
		/*Intent intent = new Intent(this, GetFromWXActivity.class);
		intent.putExtras(getIntent());
		startActivity(intent);
		finish();*/
	}

	private void goToShowMsg(ShowMessageFromWX.Req showReq) {
		/*WXMediaMessage wxMsg = showReq.message;
		WXAppExtendObject obj = (WXAppExtendObject) wxMsg.mediaObject;

		StringBuffer msg = new StringBuffer(); // 组织一个待显示的消息内容
		msg.append("description: ");
		msg.append(wxMsg.description);
		msg.append("\n");
		msg.append("extInfo: ");
		msg.append(obj.extInfo);
		msg.append("\n");
		msg.append("filePath: ");
		msg.append(obj.filePath);

		Intent intent = new Intent(this, ShowFromWXActivity.class);
		intent.putExtra(Constant.ShowMsgActivity.STitle, wxMsg.title);
		intent.putExtra(Constant.ShowMsgActivity.SMessage, msg.toString());
		intent.putExtra(Constant.ShowMsgActivity.BAThumbData, wxMsg.thumbData);
		startActivity(intent);
		finish();*/
	}

	@Override
	protected void onStart() {
		LogTool.d("onStart");
		super.onStart();
	}

	@Override
	protected void onRestart() {
		LogTool.d("onRestart");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		LogTool.d("onResume");
		super.onResume();
	}

	@Override
	protected void onStop() {
		LogTool.d("onStop");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		LogTool.d("onDestroy");
		super.onDestroy();
	}

	@Override
	protected void initView() {

	}

	@Override
	protected void initData() {

	}

	/**
	 * 获取openid accessToken值用于后期操作
	 * (new HostnameVerifier() {
	@Override
	public boolean verify(String hostname, SSLSession session) {
	return true;
	}
	})
	 * @param code 请求码
	 */
	private void loadWxInfo(final String code) {
		String path = "https://1.rockingcar.applinzi.com/wxAccessToken?code="+ code;
		LogTool.d("loadWxInfo：" + path);
//		OkHttpClient client = new OkHttpClient();
		final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
			@Override
			public void checkClientTrusted(
					java.security.cert.X509Certificate[] chain,
					String authType) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(
					java.security.cert.X509Certificate[] chain,
					String authType) throws CertificateException {
			}

			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}
		}};

		try {
			// Install the all-trusting trust manager
			final SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
			OkHttpClient client = new OkHttpClient().newBuilder().sslSocketFactory(sslContext.getSocketFactory())
                    .hostnameVerifier(new HostnameVerifier() {
						@Override
						public boolean verify(String hostname, SSLSession session) {
							return true;
						}
					}).build();
			Request request = new Request.Builder()
                    .url(path)
                    .build();
			client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    LogTool.e(e.toString());
					Toast.makeText(mContext, R.string.account_tip_login_failed, Toast.LENGTH_SHORT).show();
					finish();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    // do something...
                    LogTool.e(response.body().string());
                    /**
                     * {"code": "1", "data":
                     * "{\"access_token\":\"jrkD104nUcfSn6Xy8udHDNJiFzvXpLrZWqGIARquku3Rikaye-DBTpiWc5a_hcbat3y-bjVHxlrWZg1N8udb8g\",
                     * \"expires_in\":7200,\
                     * "refresh_token\":\"_-V6wE4XgXe-Wb4fYf34lFOKSU8exdW7tcg1e7q1thRc6PONglAf-TMGb_mYcLueqw4NpzrzqhOX3Xy8Ezvrdw\",\
                     * "openid\":\"oaxY41fhWTbStB1wH9KViiBlDn1M\",\"scope\":\"snsapi_userinfo\",\"unionid\":\"oTojM1BRcHkr2TWRsOq3IShP9fyI\"}"}
                     * */
					try {
						String result = response.body().string();
                        String[] arrResult = result.split("\\\\");
						if(arrResult != null && arrResult.length > 0){
							if("\"access_token".equalsIgnoreCase(arrResult[1]) && "\"openid".equalsIgnoreCase(arrResult[9])){
                                String access_token = arrResult[3].replace("\"", "");
                                String openid = arrResult[11].replace("\"", "");
                                getUserMesg(access_token, openid);
                            }
						}else {
							Toast.makeText(mContext, R.string.account_tip_login_failed, Toast.LENGTH_SHORT).show();
							finish();
						}
					} catch (Exception e) {
						e.printStackTrace();
						LogTool.e(e.toString());
						Toast.makeText(mContext, R.string.account_tip_login_failed, Toast.LENGTH_SHORT).show();
						finish();
					}
                }

            });
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取openid accessToken值用于后期操作
	 * @param code 请求码
	 */
	/*RequestQueue mQueue;
	private void getAccess_token(final String code) {
		String path = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
				+ Constant.WX_APP_ID
				+ "&secret="
				+ "f085346874dd4e3b3438734779fbb787"
				+ "&code="
				+ code
				+ "&grant_type=authorization_code";
		LogTool.d("getAccess_token：" + path);
		//网络请求，根据自己的请求方式

		StringRequest stringRequest = new StringRequest(Request.Method.GET, path,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						LogTool.d("getAccess_token_result:" + response);
						JSONObject jsonObject = null;
						try {
							jsonObject = new JSONObject(response);
							String openid = jsonObject.getString("openid").toString().trim();
							String access_token = jsonObject.getString("access_token").toString().trim();
							getUserMesg(access_token, openid);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				LogTool.e("Error: " + error.getMessage());
			}
		});
		if(mQueue == null){
			mQueue = Volley.newRequestQueue(mContext);
		}
		mQueue.add(stringRequest);
	}*/


	/**
	 * 获取微信的个人信息
	 * @param access_token
	 * @param openid
	 */
	private void getUserMesg(final String access_token, final String openid) {
		/*String path = "https://api.weixin.qq.com/sns/userinfo?access_token="
				+ access_token
				+ "&openid="
				+ openid;
		LogTool.d("getUserMesg：" + path);


		StringRequest stringRequest = new StringRequest(Request.Method.GET, path,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						*//**
						 * {"openid":"oaxY41fhWTbStB1wH9KViiBlDn1M",
						 * "nickname":"Scofield.H","sex":1,"language":"zh_CN","city":"Shenzhen",
						 * "province":"Guangdong","country":"CN",
						 * "headimgurl":"http:\/\/wx.qlogo.cn\/mmopen\/vi_32\/ibYCL2nbctJCDm3ZH9kWYYuCI3yibjKy5x67GEJVREqZwB0iaEV8m41ObgFnZBWrnGAxmmFO8uqwNYqzAmoO7Ku3w\/0","privilege":[],"unionid":"oTojM1BRcHkr2TWRsOq3IShP9fyI"}
						 * *//*
						LogTool.d("getAccess_token_result:" + response);
						try {
							JSONObject jsonObject = new JSONObject(response);
							String openId = jsonObject.getString("nickname");
							String nickname = jsonObject.getString("nickname");
							int sex = Integer.parseInt(jsonObject.get("sex").toString());
							String headimgurl = jsonObject.getString("headimgurl");
							MyUser myUser = new MyUser();
							myUser.setOpenid(openId);
							myUser.setUsername(nickname);
							myUser.setPhotoPath(headimgurl);
							registerUser(myUser);
							LogTool.d("用户基本信息:");
							LogTool.d("nickname:" + nickname);
							LogTool.d("sex:" + sex);
							LogTool.d("headimgurl:" + headimgurl);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				LogTool.e("Error: " + error.getMessage());
			}
		});
		if(mQueue == null){
			mQueue = Volley.newRequestQueue(mContext);
		}
		mQueue.add(stringRequest);*/
		try {
			String path = "https://api.weixin.qq.com/sns/userinfo?access_token="
					+ access_token
					+ "&openid="
					+ openid;
			LogTool.d("getUserMesg：" + path);
			OkHttpClient client = new OkHttpClient();
			Request request = new Request.Builder()
					.url(path)
					.build();
			client.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					LogTool.e(e.toString());
					Toast.makeText(mContext, R.string.account_tip_login_failed, Toast.LENGTH_SHORT).show();
					finish();
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					LogTool.e(response.body().string());
					try {
						JSONObject jsonObject = new JSONObject(response.body().string());
						String openId = jsonObject.getString("nickname");
						String nickname = jsonObject.getString("nickname");
						int sex = Integer.parseInt(jsonObject.get("sex").toString());
						String headimgurl = jsonObject.getString("headimgurl");
						MyUser myUser = new MyUser();
						myUser.setOpenid(openId);
						myUser.setUsername(nickname);
						myUser.setPhotoPath(headimgurl);
						registerUser(myUser);
						LogTool.d("用户基本信息:");
						LogTool.d("nickname:" + nickname);
						LogTool.d("sex:" + sex);
						LogTool.d("headimgurl:" + headimgurl);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

			});
		} catch (Exception e) {
			e.printStackTrace();
			LogTool.e(e.toString());
			Toast.makeText(mContext, R.string.account_tip_login_failed, Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	@Override
	public void onClick(View view) {

	}

	private void registerUser(MyUser myUser){
		addSubscription(myUser.signOrLogin("123456", new SaveListener<MyUser>() {
			@Override
			public void done(MyUser s, BmobException e) {
				if(e==null){
					toast(getString(R.string.reg_success));
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							startActivity(new Intent(mContext, Main2Activity.class));
							finish();
						}
					}, 500);
				}else{
					toast(getString(R.string.reg_failed));
					loge(e);
				}
			}
		}));
	}
}