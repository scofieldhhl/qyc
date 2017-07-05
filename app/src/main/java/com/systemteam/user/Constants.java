package com.systemteam.user;

/**
 * Created by chenjiang on 2016/5/16.
 */
public class Constants {


    public static final String REQUESTCODE = "requestcode";
    public static final String SHAERD_FILE_NAME = "shaerd_file_name";
    public static final String STATUS_CODE = "status_code";
    public static final String STATUS_DESCRIBE = "status_describe";
    public static final String STATUS_CODE_PRE = "status_code_pre"; //上次申请状态值

    public class Shared_Experter {
        public static final String PHOTO_PATH = "experter_name";
        public static final String NAME = "experter_name";
        public static final String COUNTRY = "experter_name";
        public static final String LANGUAGE = "experter_name";
        public static final String EXIST_STORE = "experter_name";
        public static final String STORE_NAME = "experter_name";
        public static final String STORE_ADDRESS = "experter_name";
        public static final String STORE_PHOTO = "experter_name";
        public static final String SKILL = "experter_name";

        public static final String STATUS_CODE = "status_code";
    }

    public static class Status {
        public static final int APPLY = -2;
        public static final int PENDING = 0;
        public static final int SUCCESS = 1;
        public static final int FAILURE = -1;

        public static final int STATUS_NO_UPDATE = 4106;

        public static final int UPDATE_SIGN_UPDATE = 1; //1是更新 0是申请
        public static final int UPDATE_SIGN_APPLY = 0;
    }

    public static class Msg {
        public static final int REQUEST_IMAGE_USER = 0x123;
        public static final int REQUEST_IMAGE_STORE = 0x124;
        public static final int MSG_NOTIF_USERPHOTO = 0x125;
        public static final int MSG_DEL_USERPHOTO = 0x126;
        public static final int MSG_NOTIF_STOREPHOTO = 0x127;
        public static final int MSG_DEL_STOREPHOTO = 0x128;
        public static final int MSG_WAIT = 0x129;
        public static final int MSG_INOF_NOTIFY = 0x130;
    }
}
