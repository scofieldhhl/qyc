package com.systemteam.provider;

//import com.wondershare.dfnlibrary.BuildConfig;


/**
 * @author rivers
 * @version 1.0
 * @Function 协议静态变量
 * @date ${date}
 */
public class ProtocolConstant {

    /**
     * 用来存储下发的协议头
     */
    public final static String PROTOCOL_HEAD_URL = "protocol_head_url";
    public final static String PROTOCOL_LUCKDRAW_TIMEOUT = "protocol_luckydraw_timeout";
    public final static String PROTOCOL_LOGIN_HEAD_URL = "protocol_login_head_url";

    /**
     * 固定SKU值
     */
    public final static String PROTOCOL_SKU = "sku-ween";
    /**
     * was固定KEY值
     */
    public final static String APP_KEY = "84f9500b03937b9b5f8514710145306d";//"d41929b32e19874ee41a138bbb3a6780";
    /**
     * was加密指纹
     */
    public final static String APP_FP = "1dd592bbfae96c749ab1edac8db0d0f3";//"9bec7bf12e00665b2c80b9bc854786db";
    /**
     * myphone固定KEY值
     */
    public final static String APP_KEY_MYPHONE = "47342D1BEE153385294760BDDB8A7F49";
    /**
     * myphone加密指纹
     */
    public final static String APP_FP_MYPHONE = "36137B1C564AA9CEDB6123F203422483";
    /**
     * 固定入口URL
     */
    public final static String URL_INIT_RELEASE = "http://myphone-api.wondershare.cc/MobileGo/interface/GetMGCInit.json";
    /**
     * 固定url请求头
     */
    public final static String URL_HEAD_RELEASE = "http://myphone-api.wondershare.cc/MobileGo/interface";

    public final static String URL_INIT_DEBUG = "http://192.168.9.50/MobileGo/interface/GetMGCInit.json";
    public final static String URL_HEAD_DEBUG = "http://192.168.9.50/MobileGo/interface";

    public static String URL_INIT;
    public static String URL_HEAD;


    /**
     * 动态接口url
     */
    public final static String URL_DYNAMIC = "/v1";//去掉原来的index.php

    /**
     * 抽奖接口
     */
    public final static String PROTOCOL_LUCKDRAW = "/luckydraw";
    /**
     * 抽奖查询接口
     */
    public final static String PROTOCOL_LUCKDRAW_CHECK = "/luckydrawCheck";
    /**
     * 发送邮件活动接口
     */
    public final static String PROTOCOL_EMAIL_ACTIVITYSEND = "/emailActivitySend";
    /**
     * 广告接口
     */
    public final static String PROTOCOL_ACTIVITY_SHOW = "/activityShow";

    public final static String WINNING = "winning";
    public final static String NO_WIN = "not_winning";

    /**
     * 获取token
     */
    public final static String PROTOCOL_TOKEN_GET = "common.request.get";
    /**
     * 通过refreshToken获取accessToken
     */
    public final static String PROTOCOL_ACCESS_REFRESH = "common.access.refresh";
    /**
     * 注册
     */
    public final static String PROTOCOL_AUTH_REGISTER = "member.auth.register";
    /**
     * 普通登录
     */
    public final static String PROTOCOL_COMMON_LOGIN = "member.common.login";
    /**
     * 登录.fb
     */
    public final static String PROTOCOL_FB_LOGIN = "oauth.fb.login";
    /**
     * 登录.twitter
     */
    public final static String PROTOCOL_TWITTER_LOGIN = "oauth.twitter.login";
    /**
     * 登录.google
     */
    public final static String PROTOCOL_GOOGLE_LOGIN = "oauth.google.login";
    /**
     * 自动登录
     */
    public final static String PROTOCOL_COMMON_AUTO = "member.common.auto";
    /**
     * 通过授权码登录
     */
    public final static String PROTOCOL_COMMON_BYCODE = "member.common.bycode";
    /**
     * 登出
     */
    public final static String PROTOCOL_AUTH_LOGOUT = "member.auth.logout";
    /**
     * 修改密码
     */
    public final static String PROTOCOL_PASSWORD_UPDATE = "member.password.update";
    /**
     * 获取个人资料
     */
    public final static String PROTOCOL_INFO_GET = "member.info.get";
    /**
     * 更新个人资料
     */
    public final static String PROTOCOL_INFO_UPDATE = "member.info.update";
    /**
     * 设置头像
     */
    public final static String PROTOCOL_AVATAR_SET = "member.avatar.set";
    /**
     * 单点登录.my
     */
    public final static String PROTOCOL_SSO_MY = "member.sso.my";
    /**
     * 单点登录.answer
     */
    public final static String PROTOCOL_SSO_ANSWER = "member.sso.answer";
    /**
     * 单点登录.mgactivity
     */
    public final static String PROTOCOL_SSO_MGAACTIVITY = "member.sso.mgactivity";
    /**
     * 修复空邮箱
     */
    public final static String PROTOCOL_INFO_FIXEMAIL = "member.info.fixemail";
    /**
     * 获取临时member id
     */
    public final static String PROTOCOL_GETEMPMEMBERID = "member.info.getTmpMemberId";
    /**
     * 获取历史记录
     */
    public final static String PROTOCOL_GETHISTORYS = "/remote/expert/history";
    /**
     * 上传历史记录
     */
    public final static String PROTOCOL_RECORDHISTORY = "/remote/expert/use";

    /**
     * 公用数据
     */
    public final static String PROTOCOL_COMMONDATA = "/v2/remote/common/data";

    /**
     * 16、	专家注册申请
     */
    public final static String PROTOCOL_EXPERTERAPPLY = "/v2.1/remote/expert/apply";

    /**
     * 17、	专家申请状态
     */
    public final static String PROTOCOL_APPLYSTATUS = "/remote/expert/status";

    /**
     * 18、	获取专家信息
     */
    public final static String PROTOCOL_EXPERTERDETAIL = "/v2/remote/expert/get";

    /**
     * 19、	专家信息更新
     */
    public final static String PROTOCOL_EXPERTERUPDATE = "/v2.1/remote/expert/update";

    /**
     * 19、	专家信息更新
     */
    public final static String PROTOCOL_EXPERTERLIST = "/v2/remote/expert/list";

    /**
     * 20、	图片上传
     */
    public final static String PROTOCOL_PICTUREUPLOAD = "/resource/picture/upload";

    /**
     * 19、	获取维修家订单列表
     */
    public final static String PROTOCOL_MERCHANTLISTS = "/order/info/merchant-lists";
    /**
     * 19、	获取用户订单列表
     */
    public final static String PROTOCOL_CUSTOMERLISTS = "/order/info/customer-lists";

    /**
     * 19、	获取维修家订单详情
     */
    public final static String PROTOCOL_MERCHANTDETAIL = "/order/info/merchant-detail";
    /**
     * 19、	获取用户订单列表
     */
    public final static String PROTOCOL_CUSTOMERDETAIL = "/order/info/customer-detail";

    /**
     * 19、	维修家提现
     */
    public final static String PROTOCOL_WITHDRAWAL = "/order/cash/withdraw";

    /**
     * 19、	维修家提现信息
     */
    public final static String PROTOCOL_CASH_SITUATION = "/order/cash/situation";

    /**
     * 19、	维修家提现列表
     */
    public final static String PROTOCOL_CASH_LIST = "/order/cash/lists";

    /*
    * 获取用户进行中的订单
    * */
    public final static String PROTOCOL_CUSTOMERPROCESS = "/order/info/customer-process";

    /*
    * 获取专家进行中的订单
    * */
    public final static String PROTOCOL_MERCHANTPROCESS = "/order/info/merchant-process";

    /*
    * 获取购买链接
    * */
    public final static String PROTOCOL_GETURL = "/order/shop/get-url";

    /*
    * 拒绝支付
    * */
    public final static String PROTOCOL_CANCELORDERPAY = "/order/info/refuse";

    /**
     * 完成订单支付
     */
    public final static String PROTOCOL_COMPLETEORDERPAY = "/order/info/complete";

    /*
    * 维修端确认完成服务
    * */
    public final static String PROTOCOL_SLOVEDORDER = "/order/info/solved";

    /*
    * 创建订单
    * */
    public final static String PROTOCOL_CREATEORDER = "/order/shop/generate-url";

    /*
    * 申请退款
    * */
    public final static String PROTOCOL_REFUNAPPLY = "/order/info/refund-apply";

    /*
    * 用户订单概要
    * */
    public final static String PROTOCOL_CUSTOMERSUMMARY = "/order/info/customer-summary";

    /*
    * 维修家订单概要
    * */
    public final static String PROTOCOL_MERCHANTSUMMARY="/order/info/merchant-summary";

    /*
    * 维修家订单概要
    * */
    public final static String PROTOCOL_EXPERTER_BEHAVIOR = "/v2.1/remote/expert/behavior";

    public final static String URL_DFN = "http://api.drfone.wondershare.com";
//    public final static String URL_DFN = "http://remote.yu.com";

}
