package com.byl.qrobot.ui.tab.tab2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byl.qrobot.R;
import com.byl.qrobot.adapter.ChatAdapter;
import com.byl.qrobot.adapter.FaceVPAdapter;
import com.byl.qrobot.bean.Answer;
import com.byl.qrobot.bean.Msg;
import com.byl.qrobot.config.Const;
import com.byl.qrobot.db.ChatMsgDao;
import com.byl.qrobot.ui.ImgPreviewActivity;
import com.byl.qrobot.ui.base.BaseActivity;
import com.byl.qrobot.util.DialogUtil;
import com.byl.qrobot.util.ExpressionUtil;
import com.byl.qrobot.util.LogUtil;
import com.byl.qrobot.util.PraseUtil;
import com.byl.qrobot.util.PreferencesUtils;
import com.byl.qrobot.util.ToastUtil;
import com.byl.qrobot.view.ActionSheetBottomDialog;
import com.byl.qrobot.view.DropdownListView;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;


/**
 * 聊天界面
 *
 * @author 白玉梁
 * @blog http://blog.csdn.net/baiyuliang2013
 * @weibo http://weibo.com/2611894214/profile?topnav=1&wvr=6&is_all=1
 */
@SuppressLint("SimpleDateFormat")
public class ChatActivity extends BaseActivity implements DropdownListView.OnRefreshListenerHeader, ChatAdapter.OnClickMsgListener {
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

    private TextView tv_weather,//图片
            tv_xingzuo,//拍照
            tv_joke,//笑话
            tv_loc,//位置
            tv_gg,//帅哥
            tv_mm;//美女

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

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mLvAdapter.notifyDataSetChanged();
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
        fh = new FinalHttp();
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        sd = new SimpleDateFormat("MM-dd HH:mm");
        msgDao = new ChatMsgDao(this);
        staticFacesList = ExpressionUtil.initStaticFaces(this);
        //初始化控件
        initViews();
        //初始化表情
        initViewPager();
        //初始化更多选项（即表情图标右侧"+"号内容）
        initAdd();
        //初始化数据
        initData();
    }

    /**
     * 初始化控件
     */
    private void initViews() {
        mListView = (DropdownListView) findViewById(R.id.message_chat_listview);
        //表情图标
        image_face = (ImageView) findViewById(R.id.image_face);
        //更多图标
        image_add = (ImageView) findViewById(R.id.image_add);
        //表情布局
        chat_face_container = (LinearLayout) findViewById(R.id.chat_face_container);
        //更多
        chat_add_container = (LinearLayout) findViewById(R.id.chat_add_container);

        mViewPager = (ViewPager) findViewById(R.id.face_viewpager);
        mViewPager.setOnPageChangeListener(new PageChange());
        //表情下小圆点
        mDotsLayout = (LinearLayout) findViewById(R.id.face_dots_container);
        input = (EditText) findViewById(R.id.input_sms);
        send = (TextView) findViewById(R.id.send_sms);
        input.setOnClickListener(this);

        //表情按钮
        image_face.setOnClickListener(this);
        //更多按钮
        image_add.setOnClickListener(this);
        // 发送
        send.setOnClickListener(this);

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

        tv_weather.setOnClickListener(this);
        tv_xingzuo.setOnClickListener(this);
        tv_joke.setOnClickListener(this);
        tv_loc.setOnClickListener(this);
        tv_gg.setOnClickListener(this);
        tv_mm.setOnClickListener(this);
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
            case R.id.send_sms:
                String content = input.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    return;
                }
                sendMsgText(content, true);
                break;
            case R.id.input_sms:
                if (chat_face_container.getVisibility() == View.VISIBLE) {
                    chat_face_container.setVisibility(View.GONE);
                }
                if (chat_add_container.getVisibility() == View.VISIBLE) {
                    chat_add_container.setVisibility(View.GONE);
                }
                break;
            case R.id.image_face:
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
            case R.id.image_add:
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
        }
    }

    /**
     * 执行发送消息 文本类型
     * isReqApi 是否调用api回答问题
     *
     * @param content
     */
    void sendMsgText(String content, boolean isReqApi) {
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
        fh.get(Const.ROBOT_URL + info, new AjaxCallBack<Object>() {
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
        }
    }

    @Override
    public void longClick(int position) {//长按
        Msg msg = listMsg.get(position);
        switch (msg.getType()) {
            case Const.MSG_TYPE_TEXT://文本
                clip(msg,position);
                break;
            case Const.MSG_TYPE_IMG://图片
                break;
            case Const.MSG_TYPE_LOCATION://位置
                delonly(msg,position);
                break;
            case Const.MSG_TYPE_VOICE://语音
                break;
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
                        ToastUtil.showToast(ChatActivity.this,"已复制到剪切板");
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
                    }
                })
                .show();
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

    /**
     * 接收消息记录操作广播：删除复制
     *
     * @author baiyuliang
     */
    private class MsgOperReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int type = intent.getIntExtra("type", 0);
            final int position = intent.getIntExtra("position", 0);
            if (listMsg.size() <= 0) {
                return;
            }
            final Msg msg = listMsg.get(position);
            switch (type) {
                case 1://聊天记录操作
                    Builder bd = new Builder(ChatActivity.this);
                    String[] items = null;
                    if (msg.getType().equals(Const.MSG_TYPE_TEXT)) {
                        items = new String[]{"删除记录", "删除全部记录", "复制文字"};
                    } else {
                        items = new String[]{"删除记录", "删除全部记录"};
                    }
                    bd.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            switch (arg1) {
                                case 0://删除
                                    listMsg.remove(position);
                                    offset = listMsg.size();
                                    mLvAdapter.notifyDataSetChanged();
                                    msgDao.deleteMsgById(msg.getMsgId());
                                    break;
//						case 1://删除全部
//							listMsg.removeAll(listMsg);
//							offset=listMsg.size();
//							mLvAdapter.notifyDataSetChanged();
//							msgDao.deleteAllMsg(YOU, I);
//							break;
                                case 2://复制
                                    ClipboardManager cmb = (ClipboardManager) ChatActivity.this.getSystemService(ChatActivity.CLIPBOARD_SERVICE);
                                    cmb.setText(msg.getContent());
                                    Toast.makeText(getApplicationContext(), "已复制到剪切板", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    });
                    bd.show();
                    break;
            }

        }
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

    ;

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
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
