package com.byl.qrobot.ui.tab.tab2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.byl.qrobot.R;
import com.byl.qrobot.adapter.ChatAdapter;
import com.byl.qrobot.adapter.FaceVPAdapter;
import com.byl.qrobot.bean.Answer;
import com.byl.qrobot.bean.Msg;
import com.byl.qrobot.bean.Music;
import com.byl.qrobot.config.Const;
import com.byl.qrobot.db.ChatMsgDao;
import com.byl.qrobot.speech.SpeechRecognizerUtil;
import com.byl.qrobot.speech.SpeechSynthesizerUtil;
import com.byl.qrobot.ui.ImgPreviewActivity;
import com.byl.qrobot.ui.base.AppBaseActivity;
import com.byl.qrobot.ui.base.BaseActivity;
import com.byl.qrobot.ui.base.SlideBackActivity;
import com.byl.qrobot.util.ExpressionUtil;
import com.byl.qrobot.util.LogUtil;
import com.byl.qrobot.util.MusicPlayManager;
import com.byl.qrobot.util.MusicSearchUtil;
import com.byl.qrobot.util.PraseUtil;
import com.byl.qrobot.util.PreferencesUtils;
import com.byl.qrobot.util.SysUtils;
import com.byl.qrobot.util.ToastUtil;
import com.byl.qrobot.view.ActionSheetBottomDialog;
import com.byl.qrobot.view.DropdownListView;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 聊天界面
 *
 * @author 白玉梁
 * @blog http://blog.csdn.net/baiyuliang2013
 * @weibo http://weibo.com/2611894214/profile?topnav=1&wvr=6&is_all=1
 */
@SuppressLint("SimpleDateFormat")
public class ChatActivity extends AppBaseActivity implements DropdownListView.OnRefreshListenerHeader,
        ChatAdapter.OnClickMsgListener, SpeechRecognizerUtil.RecoListener {
    private ViewPager mViewPager;
    private LinearLayout mDotsLayout;
    private EditText input;
    private TextView send;
    private DropdownListView mListView;
    private ChatAdapter mLvAdapter;
    private ChatMsgDao msgDao;

    private LinearLayout chat_face_container, chat_add_container;
    private ImageView image_face;//表情图标
    private ImageView image_add;//更多图标
    private ImageView image_voice;//语音
    private TextView tv_weather,//图片
            tv_xingzuo,//拍照
            tv_joke,//笑话
            tv_loc,//位置
            tv_gg,//帅哥
            tv_mm,//美女
            tv_music;//歌曲

    private LinearLayout ll_playing;//顶部正在播放布局
    private TextView tv_playing;

    //表情图标每页6列4行
    private int columns = 6;
    private int rows = 4;
    //每页显示的表情view
    private List<View> views = new ArrayList<View>();
    //表情列表
    private List<String> staticFacesList;
    //消息
    private List<Msg> listMsg;
    private SimpleDateFormat sd;
    private LayoutInflater inflater;
    private int offset;

    //发送者和接收者固定为小Q和自己
    private final String from = "xiaoq";//来自小Q
    private final String to = "master";//发送者为自己

    FinalHttp fh;

    //在线音乐播放工具类
    MusicPlayManager musicPlayManager;
    // 语音听写工具
    SpeechRecognizerUtil speechRecognizerUtil;
    // 语音合成工具
    SpeechSynthesizerUtil speechSynthesizerUtil;

    String voice_type;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mLvAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    Music music = (Music) msg.obj;
                    if (music == null) {
                        changeList(Const.MSG_TYPE_TEXT, "歌曲获取失败");
                    } else {
                        changeList(Const.MSG_TYPE_MUSIC, music.getMusicUrl() + Const.SPILT + music.getTitle() + Const.SPILT + music.getDescription());
                    }
                    break;
            }
        }
    };

    @SuppressLint("ShowToast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initTitleBar("消息", "小Q", "", this);
        musicPlayManager = new MusicPlayManager();
        fh = new FinalHttp();
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        sd = new SimpleDateFormat("MM-dd HH:mm");
        msgDao = new ChatMsgDao(this);
        staticFacesList = ExpressionUtil.initStaticFaces(this);
        voice_type = PreferencesUtils.getSharePreStr(this, Const.IM_VOICE_TPPE);
        //初始化控件
        initViews();
        //初始化表情
        initViewPager();
        //初始化更多选项（即表情图标右侧"+"号内容）
        initAdd();
        //初始化数据
        initData();
        //初始化语音听写及合成部分
        initSpeech();
    }

    private void initSpeech() {
        speechRecognizerUtil = new SpeechRecognizerUtil(this);
        speechRecognizerUtil.setRecoListener(this);
        speechSynthesizerUtil = new SpeechSynthesizerUtil(this);
    }

    /**
     * 初始化控件
     */
    private void initViews() {
        ll_playing = (LinearLayout) findViewById(R.id.ll_playing);
        tv_playing = (TextView) findViewById(R.id.tv_playing);

        mListView = (DropdownListView) findViewById(R.id.message_chat_listview);
        SysUtils.setOverScrollMode(mListView);

        image_face = (ImageView) findViewById(R.id.image_face); //表情图标
        image_add = (ImageView) findViewById(R.id.image_add);//更多图标
        image_voice = (ImageView) findViewById(R.id.image_voice);//语音
        chat_face_container = (LinearLayout) findViewById(R.id.chat_face_container);//表情布局
        chat_add_container = (LinearLayout) findViewById(R.id.chat_add_container);//更多

        mViewPager = (ViewPager) findViewById(R.id.face_viewpager);
        mViewPager.setOnPageChangeListener(new PageChange());
        //表情下小圆点
        mDotsLayout = (LinearLayout) findViewById(R.id.face_dots_container);
        input = (EditText) findViewById(R.id.input_sms);
        send = (TextView) findViewById(R.id.send_sms);
        input.setOnClickListener(this);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    send.setVisibility(View.VISIBLE);
                    image_voice.setVisibility(View.GONE);
                } else {
                    send.setVisibility(View.GONE);
                    image_voice.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        image_face.setOnClickListener(this);//表情按钮
        image_add.setOnClickListener(this);//更多按钮
        image_voice.setOnClickListener(this);//语音按钮
        send.setOnClickListener(this); // 发送

        mListView.setOnRefreshListenerHead(this);
        mListView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
                    if (chat_face_container.getVisibility() == View.VISIBLE) {
                        chat_face_container.setVisibility(View.GONE);
                    }
                    if (chat_add_container.getVisibility() == View.VISIBLE) {
                        chat_add_container.setVisibility(View.GONE);
                    }
                    hideSoftInputView();
                }
                return false;
            }
        });
    }

    public void initAdd() {
        tv_weather = (TextView) findViewById(R.id.tv_weather);
        tv_xingzuo = (TextView) findViewById(R.id.tv_xingzuo);
        tv_joke = (TextView) findViewById(R.id.tv_joke);
        tv_loc = (TextView) findViewById(R.id.tv_loc);
        tv_gg = (TextView) findViewById(R.id.tv_gg);
        tv_mm = (TextView) findViewById(R.id.tv_mm);
        tv_music = (TextView) findViewById(R.id.tv_music);

        tv_weather.setOnClickListener(this);
        tv_xingzuo.setOnClickListener(this);
        tv_joke.setOnClickListener(this);
        tv_loc.setOnClickListener(this);
        tv_gg.setOnClickListener(this);
        tv_mm.setOnClickListener(this);
        tv_music.setOnClickListener(this);
    }

    public void initData() {
        offset = 0;
        listMsg = msgDao.queryMsg(from, to, offset);
        offset = listMsg.size();
        mLvAdapter = new ChatAdapter(this, listMsg, this);
        mListView.setAdapter(mLvAdapter);
        mListView.setSelection(listMsg.size());
    }

    /**
     * 初始化表情
     */
    private void initViewPager() {
        int pagesize = ExpressionUtil.getPagerCount(staticFacesList.size(), columns, rows);
        // 获取页数
        for (int i = 0; i < pagesize; i++) {
            views.add(ExpressionUtil.viewPagerItem(this, i, staticFacesList, columns, rows, input));
            LayoutParams params = new LayoutParams(16, 16);
            mDotsLayout.addView(dotsItem(i), params);
        }
        FaceVPAdapter mVpAdapter = new FaceVPAdapter(views);
        mViewPager.setAdapter(mVpAdapter);
        mDotsLayout.getChildAt(0).setSelected(true);
    }

    /**
     * 表情页切换时，底部小圆点
     *
     * @param position
     * @return
     */
    private ImageView dotsItem(int position) {
        View layout = inflater.inflate(R.layout.dot_image, null);
        ImageView iv = (ImageView) layout.findViewById(R.id.face_dot);
        iv.setId(position);
        return iv;
    }


    @Override
    public void onClick(View arg0) {
        super.onClick(arg0);
        switch (arg0.getId()) {
            case R.id.send_sms://发送
                String content = input.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    return;
                }
                sendMsgText(content, true);
                break;
            case R.id.input_sms://点击输入框
                if (chat_face_container.getVisibility() == View.VISIBLE) {
                    chat_face_container.setVisibility(View.GONE);
                }
                if (chat_add_container.getVisibility() == View.VISIBLE) {
                    chat_add_container.setVisibility(View.GONE);
                }
                break;
            case R.id.image_face://点击表情按钮
                hideSoftInputView();//隐藏软键盘
                if (chat_add_container.getVisibility() == View.VISIBLE) {
                    chat_add_container.setVisibility(View.GONE);
                }
                if (chat_face_container.getVisibility() == View.GONE) {
                    chat_face_container.setVisibility(View.VISIBLE);
                } else {
                    chat_face_container.setVisibility(View.GONE);
                }
                break;
            case R.id.image_add://点击加号按钮
                hideSoftInputView();//隐藏软键盘
                if (chat_face_container.getVisibility() == View.VISIBLE) {
                    chat_face_container.setVisibility(View.GONE);
                }
                if (chat_add_container.getVisibility() == View.GONE) {
                    chat_add_container.setVisibility(View.VISIBLE);
                } else {
                    chat_add_container.setVisibility(View.GONE);
                }
                break;
            case R.id.image_voice://点击语音按钮
                if (!TextUtils.isEmpty(voice_type) && voice_type.equals("1")) {//以语音形式发送
                    speechRecognizerUtil.say(input, false);
                } else {//以文本形式发送
                    speechRecognizerUtil.say(input, true);
                }
                break;
            case R.id.tv_weather:
                sendMsgText(PreferencesUtils.getSharePreStr(this, Const.CITY) + "天气", true);
                break;
            case R.id.tv_xingzuo:
                input.setText("星座#");
                input.setSelection(input.getText().toString().length());//光标移至最后
                changeList(Const.MSG_TYPE_TEXT, "请输入星座#您的星座查询");
                chat_add_container.setVisibility(View.GONE);
                showSoftInputView(input);
                break;
            case R.id.tv_joke:
                sendMsgText("笑话", true);
                break;
            case R.id.tv_loc:
                sendMsgText("位置", false);
                String lat = PreferencesUtils.getSharePreStr(this, Const.LOCTION);//经纬度
                if (TextUtils.isEmpty(lat)) {
                    lat = "116.404,39.915";//北京
                }
                changeList(Const.MSG_TYPE_LOCATION, Const.LOCATION_URL_S + lat + "&markers=|" + lat + "&markerStyles=l,A,0xFF0000");//传入地图（图片）路径
                break;
            case R.id.tv_gg:
                sendMsgText("帅哥", true);
                break;
            case R.id.tv_mm:
                sendMsgText("美女", true);
                break;
            case R.id.tv_music:
                input.setText("歌曲##");
                input.setSelection(input.getText().toString().length() - 1);
                changeList(Const.MSG_TYPE_TEXT, "请输入：歌曲#歌曲名#演唱者");
                chat_add_container.setVisibility(View.GONE);
                showSoftInputView(input);
                break;
        }
    }

    /**
     * 执行发送消息 文本类型
     * isReqApi 是否调用api回答问题
     *
     * @param content
     */
    void sendMsgText(String content, boolean isReqApi) {
        if (content.endsWith("##")) {
            ToastUtil.showToast(this, "输入有误");
            return;
        }
        Msg msg = getChatInfoTo(content, Const.MSG_TYPE_TEXT);
        msg.setMsgId(msgDao.insert(msg));
        listMsg.add(msg);
        offset = listMsg.size();
        mLvAdapter.notifyDataSetChanged();
        input.setText("");
        if (isReqApi) getFromMsg(Const.MSG_TYPE_TEXT, content);
    }

    /**
     * 执行发送消息 图片类型
     */
    void sendMsgImg(String imgpath) {
        Msg msg = getChatInfoTo(imgpath, Const.MSG_TYPE_IMG);
        msg.setMsgId(msgDao.insert(msg));
        listMsg.add(msg);
        offset = listMsg.size();
        mLvAdapter.notifyDataSetChanged();

    }

    /**
     * 执行发送消息 位置类型
     *
     * @param content
     */
    void sendMsgLocation(String content) {
        Msg msg = getChatInfoTo(content, Const.MSG_TYPE_LOCATION);
        msg.setMsgId(msgDao.insert(msg));
        listMsg.add(msg);
        offset = listMsg.size();
        mLvAdapter.notifyDataSetChanged();
    }

    /**
     * 发送语音
     *
     * @param content
     */
    void sendMsgVoice(String content) {
        String[] _content = content.split(Const.SPILT);
        Msg msg = getChatInfoTo(content, Const.MSG_TYPE_VOICE);
        msg.setMsgId(msgDao.insert(msg));
        listMsg.add(msg);
        offset = listMsg.size();
        mLvAdapter.notifyDataSetChanged();
        getFromMsg(Const.MSG_TYPE_TEXT, _content[1]);
    }

    /**
     * 发送的信息
     * from为收到的消息，to为自己发送的消息
     *
     * @return
     */
    private Msg getChatInfoTo(String message, String msgtype) {
        String time = sd.format(new Date());
        Msg msg = new Msg();
        msg.setFromUser(from);
        msg.setToUser(to);
        msg.setType(msgtype);
        msg.setIsComing(1);
        msg.setContent(message);
        msg.setDate(time);
        return msg;
    }

    /**
     * 获取结果
     *
     * @param msgtype
     * @param info
     */
    private void getFromMsg(final String msgtype, String info) {
        if (info.startsWith("星座#") && info.length() > 3) {
            getResponse(msgtype, info.split("#")[1] + "运势");
        } else if (info.startsWith("歌曲#") && info.split("#").length == 3) {
            String[] _info = info.split("#");
            if (TextUtils.isEmpty(_info[1]) || TextUtils.isEmpty(_info[2])) {
                ToastUtil.showToast(this, "输入有误");
                return;
            }
            getMusic(_info[1], _info[2]);
        } else {
            getResponse(msgtype, info);
        }
    }

    /**
     * 调用机器人api获取回答结果
     *
     * @param msgtype
     * @param info
     */
    void getResponse(final String msgtype, String info) {
        fh.get(Const.ROBOT_URL + info.replace("#", ""), new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                LogUtil.e("response>>" + o);
                Answer answer = PraseUtil.praseMsgText((String) o);
                String responeContent;
                if (answer == null) {
                    responeContent = "网络错误";
                } else {
                    if (!TextUtils.isEmpty(answer.getUrl())) {
                        responeContent = answer.getText() + answer.getUrl();
                    } else {
                        responeContent = answer.getText();
                    }
                }
                changeList(msgtype, responeContent);
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                changeList(msgtype, "网络连接失败");
            }
        });
    }

    /**
     * 获取音乐链接
     *
     * @param name
     * @param author
     */
    void getMusic(final String name, final String author) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Music music = MusicSearchUtil.searchMusic(name, author);
                Message msg = new Message();
                msg.what = 2;
                msg.obj = music;
                mHandler.sendMessage(msg);
            }
        }).start();
    }

    /**
     * 刷新数据
     *
     * @param msgtype
     * @param responeContent
     */
    private void changeList(String msgtype, String responeContent) {
        Msg msg = new Msg();
        msg.setIsComing(0);
        msg.setContent(responeContent);
        msg.setType(msgtype);
        msg.setFromUser(from);
        msg.setToUser(to);
        msg.setDate(sd.format(new Date()));
        msg.setMsgId(msgDao.insert(msg));
        listMsg.add(msg);
        offset = listMsg.size();
        mLvAdapter.notifyDataSetChanged();
        if (msg.getType().equals(Const.MSG_TYPE_TEXT)) {
            String speech_type = PreferencesUtils.getSharePreStr(this, Const.IM_SPEECH_TPPE);
            if (!TextUtils.isEmpty(speech_type) && speech_type.equals("1")) {
                speechSynthesizerUtil.speech(msg.getContent());
            }
        }

    }

    @Override
    public void click(int position) {//点击
        Msg msg = listMsg.get(position);
        switch (msg.getType()) {
            case Const.MSG_TYPE_TEXT://文本
                break;
            case Const.MSG_TYPE_IMG://图片
                break;
            case Const.MSG_TYPE_LOCATION://位置
                Intent intent = new Intent(this, ImgPreviewActivity.class);
                intent.putExtra("url", msg.getContent());
                startActivity(intent);
                break;
            case Const.MSG_TYPE_VOICE://语音

                break;
            case Const.MSG_TYPE_MUSIC://音乐
                String[] musicinfo = msg.getContent().split(Const.SPILT);
                if (musicinfo.length == 3) {//音乐链接，歌曲名，作者
                    if (TextUtils.isEmpty(msg.getBak1()) || msg.getBak1().equals("0")) {
                        stopOldMusic();
                        msg.setBak1("1");
                        listMsg.remove(position);
                        listMsg.add(position, msg);
                        mLvAdapter.notifyDataSetChanged();
                        playMusic(musicinfo);
                    } else {
                        if (musicPlayManager != null) {
                            ll_playing.setVisibility(View.GONE);
                            musicPlayManager.stop();
                        }
                        msg.setBak1("0");
                        listMsg.remove(position);
                        listMsg.add(position, msg);
                        mLvAdapter.notifyDataSetChanged();
                    }

                }
                break;
        }
    }

    @Override
    public void longClick(int position) {//长按
        Msg msg = listMsg.get(position);
        switch (msg.getType()) {
            case Const.MSG_TYPE_TEXT://文本
                clip(msg, position);
                break;
            case Const.MSG_TYPE_IMG://图片
                break;
            case Const.MSG_TYPE_LOCATION://位置
            case Const.MSG_TYPE_MUSIC://音乐
            case Const.MSG_TYPE_VOICE://语音
                delonly(msg, position);
                break;
        }
    }

    /**
     * 播放网络音乐
     *
     * @param musicinfo
     */
    void playMusic(final String[] musicinfo) {
        ll_playing.setVisibility(View.VISIBLE);
        tv_playing.setText("正在播放歌曲：《" + musicinfo[1] + "》—" + musicinfo[2]);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    musicPlayManager.play(musicinfo[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.e("音乐播放异常>>" + e.getMessage());
                    stopOldMusic();
                    Looper.prepare();
                    ToastUtil.showToast(ChatActivity.this, "播放错误，请重试");
                    Looper.loop();
                }
            }
        }).start();
    }

    /**
     * 停止之前正在播放的音乐
     */
    void stopOldMusic() {
        for (int i = 0; i < listMsg.size(); i++) {
            Msg msg = listMsg.get(i);
            if (!TextUtils.isEmpty(msg.getBak1()) && msg.getBak1().equals("1")) {
                msg.setBak1("0");
                listMsg.remove(i);
                listMsg.add(i, msg);
                mLvAdapter.notifyDataSetChanged();
                if (musicPlayManager != null) {
                    ll_playing.setVisibility(View.GONE);
                    musicPlayManager.stop();
                }
                break;
            }
        }
    }

    /**
     * 带复制文本的操作
     */
    void clip(final Msg msg, final int position) {
        new ActionSheetBottomDialog(this)
                .builder()
                .addSheetItem("复制", ActionSheetBottomDialog.SheetItemColor.Blue, new ActionSheetBottomDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        ClipboardManager cmb = (ClipboardManager) ChatActivity.this.getSystemService(ChatActivity.CLIPBOARD_SERVICE);
                        cmb.setText(msg.getContent());
                        ToastUtil.showToast(ChatActivity.this, "已复制到剪切板");
                    }
                })
                .addSheetItem("朗读", ActionSheetBottomDialog.SheetItemColor.Blue, new ActionSheetBottomDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        speechSynthesizerUtil.speech(msg.getContent());
                    }
                })
                .addSheetItem("删除", ActionSheetBottomDialog.SheetItemColor.Blue, new ActionSheetBottomDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        listMsg.remove(position);
                        offset = listMsg.size();
                        mLvAdapter.notifyDataSetChanged();
                        msgDao.deleteMsgById(msg.getMsgId());
                    }
                })
                .show();
    }

    /**
     * 仅有删除操作
     */
    void delonly(final Msg msg, final int position) {
        new ActionSheetBottomDialog(this)
                .builder()
                .addSheetItem("删除", ActionSheetBottomDialog.SheetItemColor.Blue, new ActionSheetBottomDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        listMsg.remove(position);
                        offset = listMsg.size();
                        mLvAdapter.notifyDataSetChanged();
                        msgDao.deleteMsgById(msg.getMsgId());
                        if (msg.getType().equals(Const.MSG_TYPE_MUSIC)) {
                            if (musicPlayManager != null) {
                                ll_playing.setVisibility(View.GONE);
                                musicPlayManager.stop();
                            }
                        }
                    }
                })
                .show();
    }

    /**
     * 录音完毕
     * text 录音转文字后的内容
     */
    @Override
    public void recoComplete(String text) {
        String voicepath = Const.FILE_VOICE_CACHE + System.currentTimeMillis() + ".wav";
        if (SysUtils.copyFile(Const.FILE_VOICE_CACHE + "iat.wav", voicepath)) {
            sendMsgVoice(voicepath + Const.SPILT + text);
        } else {
            ToastUtil.showToast(this, "录音失败");
        }
    }

    /**
     * 表情页改变时，dots效果也要跟着改变
     */
    class PageChange implements OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            for (int i = 0; i < mDotsLayout.getChildCount(); i++) {
                mDotsLayout.getChildAt(i).setSelected(false);
            }
            mDotsLayout.getChildAt(arg0).setSelected(true);
        }
    }

    /**
     * 下拉加载更多
     */
    @Override
    public void onRefresh() {
        List<Msg> list = msgDao.queryMsg(from, to, offset);
        if (list.size() <= 0) {
            mListView.setSelection(0);
            mListView.onRefreshCompleteHeader();
            return;
        }
        listMsg.addAll(0, list);
        offset = listMsg.size();
        mListView.onRefreshCompleteHeader();
        mLvAdapter.notifyDataSetChanged();
        mListView.setSelection(list.size());
    }


    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //让输入框获取焦点
                input.requestFocus();
                if (chat_face_container.getVisibility() == View.VISIBLE || chat_add_container.getVisibility() == View.VISIBLE) {
                    hideSoftInputView();
                }
            }
        }, 100);

    }

    /**
     * 监听返回键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            hideSoftInputView();
            if (chat_face_container.getVisibility() == View.VISIBLE) {
                chat_face_container.setVisibility(View.GONE);
            } else if (chat_add_container.getVisibility() == View.VISIBLE) {
                chat_add_container.setVisibility(View.GONE);
            } else {
                if (musicPlayManager != null && musicPlayManager.isPlaying()) {
                    musicPlayManager.stop();
                }
                if (speechSynthesizerUtil != null) {
                    speechSynthesizerUtil.stopSpeech();
                }
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
