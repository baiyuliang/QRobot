package com.byl.qrobot.config;

import android.os.Environment;

public class Const {

    public static final String FILE_IMG_CACHE = Environment.getExternalStorageDirectory() + "/qrobot/images/cache/";
    public static final String FILE_VOICE_CACHE = Environment.getExternalStorageDirectory() + "/qrobot/voice/";
    public static final String FILE_DOWNLOAD = Environment.getExternalStorageDirectory() + "/qrobot/download/";

    public static final String XF_VOICE_APPID="573945a6";//讯飞语音appid
    public static final String XF_AD_APPID="573a6ddc";//讯飞广告appid
    public static final String XF_AD_FULLSCREEN_ID="D5B0845FF3FCF739CF88AF2FB45723F5";//讯飞广告位id
    public static final String XF_AD_BANNER_ID="78154B642F559C48E2BB53C2E46E83A3";//讯飞广告位id
    public static final String XF_AD_BANNER2_ID="3A072782D7257046E8F13FDDCBD031EF";//讯飞广告位id
    public final static String XF_SET_VOICE_RECORD="VOICE_RECORD";//录音语言
    public final static String XF_SET_VOICE_READ="XF_SET_VOICE_READ";//朗读语言

    public final static String IM_VOICE_TPPE="IM_VOICE_TPPE";//语音聊天形式
    public final static String IM_SPEECH_TPPE="IM_SPEECH_TPPE";//聊天回复是否直接朗读

    /**
     * 登录手机号
     */
    public final static String LOGIN_PHONE = "LOGIN_PHONE";
    /**
     * 登录密码
     */
    public final static String LOGIN_PWD = "LOGIN_PWD";
    /**
     * 默认横坐标
     */
    public final static double LOC_LONGITUDE = 116.403119;
    /**
     * 默认纵坐标
     */
    public final static double LOC_LATITUDE = 39.915378;
    /**
     * 实时定位地址
     */
    public final static String ADDRESS = "ADDRESS";
    /**
     * 实时定位城市
     */
    public final static String CITY = "CITY";
    /**
     * 实时定位坐标
     */
    public final static String LOCTION = "LOCTION";

    /**
     * 版本号,如果版本更新时更新了引导图，则将"VERSION_CODE"名改变即可,没有更新则不改变；
     * 为了统一，更改策略为：
     * VERSION_CODE="VERSION_CODE_1"
     * VERSION_CODE="VERSION_CODE_2"
     * VERSION_CODE="VERSION_CODE_3"
     * 。。。。。。
     */
    public final static String VERSION_CODE = "VERSION_CODE";

    // 资讯标识常量 Start
    public static final int TYPE_NEWS = 1;// 业界资讯标识
    public static final int TYPE_MOBILE = 2;// 移动资讯标识
    public static final int TYPE_CLOUD = 3;// 云计算资讯标识
    public static final int TYPE_SD = 4;// 软件研发资讯标识
    public static final int TYPE_PROGRAMMER = 5;// 程序员资讯标识
    // 资讯标识常量 End

    // 资讯地址常量 Start
    public static final String URL_NEWS = "http://news.csdn.net/news";// 业界
    public static final String URL_MOBILE = "http://mobile.csdn.net/mobile";// 移动
    public static final String URL_CLOUD = "http://cloud.csdn.net/cloud";// 云计算
    public static final String URL_SD = "http://sd.csdn.net/sd";// 软件研发
    public static final String URL_PROGRAMMER = "http://programmer.csdn.net/programmer";// 程序员

    // 资讯地址常量 End

    // 资讯Type标识常量 Start
    public static final int TITLE = 1;
    public static final int SUMMARY = 2;
    public static final int CONTENT = 3;
    public static final int IMG = 4;
    public static final int BOLD_TITLE = 5;
    // 资讯Type标识常量 End

    //静态地图API
    public static  final String LOCATION_URL_S = "http://api.map.baidu.com/staticimage?width=320&height=240&zoom=17&center=";
    public static  final String LOCATION_URL_L = "http://api.map.baidu.com/staticimage?width=480&height=800&zoom=17&center=";

    public static final String MSG_TYPE_TEXT="msg_type_text";//文本消息
    public static final String MSG_TYPE_IMG="msg_type_img";//图片
    public static final String MSG_TYPE_VOICE="msg_type_voice";//语音
    public static final String MSG_TYPE_LOCATION="msg_type_location";//位置
    public static final String MSG_TYPE_MUSIC="msg_type_music";//音乐
    public static final String MSG_TYPE_LIST="msg_type_list";//新闻

    //机器人api，注意key为本人所有，使用时请到图灵机器人官网注册http://www.tuling123.com
    public static final String ROBOT_URL="http://www.tuling123.com/openapi/api";
    public static final String ROBOT_KEY="24cf362cd4b88f7b8ef3cdf207c8765f";

    /**
     * 分享
     */
    public final static String  WX_APP_ID = "wx8d2b441fb6f44075";

    public final static String  QQ_APP_ID = "1105404732";

    /**
     * 分隔符
     */
    public final static String  SPILT = "☆";

}
