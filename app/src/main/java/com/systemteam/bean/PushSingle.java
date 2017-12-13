package com.systemteam.bean;

/**
 * 类描述：
 * 创建人：ccy
 * 创建时间：2017/12/12 22:06
 */

import static com.systemteam.util.Constant.GT_APP_ID;

/**
 * "message":{
 "appkey":"pMEgGQ9bgz5LVAPX8q8WH4",
 "is_offline":false,
 "msgtype":"transmission"
 },
 "transmission":{
 "transmission_type":false,
 "transmission_content":"this is the transmission_content",
 "duration_begin":"2017-03-22 11:40:00",
 "duration_end":"2017-03-29 11:40:00"
 },

 "cid":"1fa0795a57c863ecc9a9ea6437b8924f",
 "requestid":"123456789"
 * */
public class PushSingle {
    public Message message;
    public Transmission transmission;
    public String cid;
    public String requestid = "112233";

    public PushSingle(){
        message = new Message();
        transmission = new Transmission();
    }

    public class Message{
        public String appkey = GT_APP_ID;
        public boolean is_offline = false;
        public String msgtype = "transmission";
    }

    public class Transmission{
        public String transmission_content;
    }
}


