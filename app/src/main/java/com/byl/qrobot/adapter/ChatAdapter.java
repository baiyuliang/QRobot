package com.byl.qrobot.adapter;

import java.util.List;

import net.tsz.afinal.FinalBitmap;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.byl.qrobot.R;
import com.byl.qrobot.bean.Msg;
import com.byl.qrobot.config.Const;
import com.byl.qrobot.listener.RecordPlayClickListener;
import com.byl.qrobot.util.PreferencesUtils;
import com.byl.qrobot.view.CircleImageView;


/**
 * 聊天适配器
 *
 * @author baiyuliang
 * @ClassName: MessageChatAdapter
 */
public class ChatAdapter extends BaseListAdapter<Msg> {

    //文本
    private final int TYPE_RECEIVER_TXT = 0;
    private final int TYPE_SEND_TXT = 1;
    //图片
    private final int TYPE_SEND_IMAGE = 2;
    private final int TYPE_RECEIVER_IMAGE = 3;
    //位置
    private final int TYPE_SEND_LOCATION = 4;
    private final int TYPE_RECEIVER_LOCATION = 5;
    //语音
    private final int TYPE_SEND_VOICE = 6;
    private final int TYPE_RECEIVER_VOICE = 7;
    //音乐
    private final int TYPE_RECEIVER_MUSIC = 8;

    private FinalBitmap finalImageLoader;
    AnimationDrawable anim;
    OnClickMsgListener onClickMsgListener;

    public ChatAdapter(Context context, List<Msg> msgList, OnClickMsgListener onClickMsgListener) {
        super(context, msgList);
        mContext = context;
        finalImageLoader = FinalBitmap.create(context);
        this.onClickMsgListener = onClickMsgListener;
    }

    //获取item类型
    @Override
    public int getItemViewType(int position) {
        Msg msg = list.get(position);
        switch (msg.getType()) {
            case Const.MSG_TYPE_TEXT:
                return msg.getIsComing() == 0 ? TYPE_RECEIVER_TXT : TYPE_SEND_TXT;
            case Const.MSG_TYPE_IMG:
                return msg.getIsComing() == 0 ? TYPE_RECEIVER_IMAGE : TYPE_SEND_IMAGE;
            case Const.MSG_TYPE_LOCATION:
                return msg.getIsComing() == 0 ? TYPE_RECEIVER_LOCATION : TYPE_SEND_LOCATION;
            case Const.MSG_TYPE_VOICE:
                return msg.getIsComing() == 0 ? TYPE_RECEIVER_VOICE : TYPE_SEND_VOICE;
            case Const.MSG_TYPE_MUSIC:
                return TYPE_RECEIVER_MUSIC;
            default:
                return -1;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 9;
    }

    /**
     * 根据消息类型，使用对应布局
     *
     * @param msg
     * @param position
     * @return
     */
    private View createViewByType(Msg msg, int position) {
        switch (msg.getType()) {
            case Const.MSG_TYPE_TEXT://文本
                return getItemViewType(position) == TYPE_RECEIVER_TXT ? createView(R.layout.item_chat_text_rece) : createView(R.layout.item_chat_text_sent);
            case Const.MSG_TYPE_IMG://图片
                return getItemViewType(position) == TYPE_RECEIVER_IMAGE ? createView(R.layout.item_chat_image_rece) : createView(R.layout.item_chat_image_sent);
            case Const.MSG_TYPE_LOCATION://位置
                return getItemViewType(position) == TYPE_RECEIVER_LOCATION ? createView(R.layout.item_chat_location_rece) : createView(R.layout.item_chat_location_sent);
            case Const.MSG_TYPE_VOICE://语音
                return getItemViewType(position) == TYPE_RECEIVER_VOICE ? createView(R.layout.item_chat_voice_rece) : createView(R.layout.item_chat_voice_sent);
            case Const.MSG_TYPE_MUSIC://音乐
                return createView(R.layout.item_chat_music_rece);
            default:
                return null;
        }
    }

    private View createView(int id) {
        return mInflater.inflate(id, null);
    }

    @Override
    public View bindView(final int position, View convertView, ViewGroup parent) {
        final Msg msg = list.get(position);
        if (convertView == null) {
            convertView = createViewByType(msg, position);
        }

        CircleImageView head_view = ViewHolder.get(convertView, R.id.head_view);//头像
        TextView chat_time = ViewHolder.get(convertView, R.id.chat_time);//时间
        TextView tv_text = ViewHolder.get(convertView, R.id.tv_text);//文本
        ImageView iv_image = ViewHolder.get(convertView, R.id.iv_image);//图片
        ImageView iv_location = ViewHolder.get(convertView, R.id.iv_location);//位置

        LinearLayout layout_voice = ViewHolder.get(convertView, R.id.layout_voice);//语音 语音播放按钮父控件
        ImageView iv_voice = ViewHolder.get(convertView, R.id.iv_voice);//动画
        ImageView iv_fy = ViewHolder.get(convertView, R.id.iv_fy);//翻译按钮
        final TextView tv_fy = ViewHolder.get(convertView, R.id.tv_fy);//翻译内容

        LinearLayout ll_music = (LinearLayout) convertView.findViewById(R.id.ll_music);//音乐
        ImageView iv_music = (ImageView) convertView.findViewById(R.id.iv_music);
        ProgressBar pb_music = (ProgressBar) convertView.findViewById(R.id.pb_music);
        TextView tv_song_name = (TextView) convertView.findViewById(R.id.tv_song_name);//音乐名
        TextView tv_song_author = (TextView) convertView.findViewById(R.id.tv_song_author);//音乐作者

        chat_time.setText(msg.getDate());//时间

        switch (msg.getType()) {
            case Const.MSG_TYPE_TEXT://文本
                tv_text.setText(msg.getContent());
                tv_text.setOnClickListener(new onClick(position));
                tv_text.setOnLongClickListener(new onLongCilck(position));
                break;
            case Const.MSG_TYPE_IMG://图片
                finalImageLoader.display(iv_image, msg.getContent());
                iv_image.setOnClickListener(new onClick(position));
                iv_image.setOnLongClickListener(new onLongCilck(position));
                break;
            case Const.MSG_TYPE_LOCATION://位置
                finalImageLoader.display(iv_location, msg.getContent());
                iv_location.setOnClickListener(new onClick(position));
                iv_location.setOnLongClickListener(new onLongCilck(position));
                break;
            case Const.MSG_TYPE_VOICE://语音
                final String[] _content = msg.getContent().split(Const.SPILT);
                tv_fy.setText(_content[1]);
                tv_fy.setVisibility(View.GONE);
                layout_voice.setOnClickListener(new RecordPlayClickListener(mContext, iv_voice, _content[0]));
                iv_fy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tv_fy.getVisibility() == View.GONE) {
                            tv_fy.setVisibility(View.VISIBLE);
                        }else{
                            tv_fy.setVisibility(View.GONE);
                        }
                    }
                });
                layout_voice.setOnLongClickListener(new onLongCilck(position));
                break;
            case Const.MSG_TYPE_MUSIC://音乐
                if (!TextUtils.isEmpty(msg.getBak1()) && msg.getBak1().equals("1")) {
                    pb_music.setVisibility(View.VISIBLE);
                    iv_music.setVisibility(View.GONE);
                } else {
                    pb_music.setVisibility(View.GONE);
                    iv_music.setVisibility(View.VISIBLE);
                }
                String[] musicinfo = msg.getContent().split(Const.SPILT);
                if (musicinfo.length == 3) {//音乐链接，歌曲名，作者
                    tv_song_name.setText(musicinfo[1]);
                    tv_song_author.setText(musicinfo[2]);
                }
                ll_music.setOnClickListener(new onClick(position));
                ll_music.setOnLongClickListener(new onLongCilck(position));
                break;
        }

        return convertView;
    }

    /**
     * 屏蔽listitem的所有事件
     */
    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    /**
     * 点击监听
     *
     * @author 白玉梁
     */
    class onClick implements View.OnClickListener {
        int position;

        public onClick(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View arg0) {
            onClickMsgListener.click(position);
        }

    }

    /**
     * 长按监听
     *
     * @author 白玉梁
     */
    class onLongCilck implements View.OnLongClickListener {
        int position;

        public onLongCilck(int position) {
            this.position = position;
        }

        @Override
        public boolean onLongClick(View arg0) {
            onClickMsgListener.longClick(position);
            return true;
        }
    }

    public interface OnClickMsgListener {
        void click(int position);

        void longClick(int position);
    }

}
