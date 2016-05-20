package com.byl.qrobot.adapter;

import java.util.List;

import net.tsz.afinal.FinalBitmap;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.byl.qrobot.R;
import com.byl.qrobot.bean.Answer;
import com.byl.qrobot.bean.Cook;
import com.byl.qrobot.bean.Msg;
import com.byl.qrobot.bean.News;
import com.byl.qrobot.config.Const;
import com.byl.qrobot.listener.RecordPlayClickListener;
import com.byl.qrobot.ui.WebActivity;
import com.byl.qrobot.util.ExpressionUtil;
import com.byl.qrobot.util.PraseUtil;
import com.byl.qrobot.util.SysUtils;
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
    //新闻，菜谱等列表类信息
    private final int TYPE_RECEIVER_LIST = 9;

    private FinalBitmap finalImageLoader;
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
            case Const.MSG_TYPE_LIST:
                return TYPE_RECEIVER_LIST;
            default:
                return -1;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 10;
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
            case Const.MSG_TYPE_LIST://列表
                return createView(R.layout.item_chat_news_rece);
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

        LinearLayout ll_news_list = (LinearLayout) convertView.findViewById(R.id.ll_news_list);
        ImageView iv_news_top_img = (ImageView) convertView.findViewById(R.id.iv_news_top_img);
        TextView tv_news_top_title = (TextView) convertView.findViewById(R.id.tv_news_top_title);

        chat_time.setText(msg.getDate());//时间

        switch (msg.getType()) {
            case Const.MSG_TYPE_TEXT://文本
                tv_text.setText(ExpressionUtil.prase(mContext, tv_text, msg.getContent()));
                Linkify.addLinks(tv_text, Linkify.ALL);
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
                        } else {
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
            case Const.MSG_TYPE_LIST://列表 (注意，正常情况下，应判断list是否为空等异常情况)
                Answer answer = PraseUtil.praseMsgText(msg.getContent());
                viewList(ll_news_list,iv_news_top_img,tv_news_top_title,answer);
                iv_news_top_img.setOnLongClickListener(new onLongCilck(position));
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
     * 列表类型
     * @param ll_news_list
     * @param iv_news_top_img
     * @param tv_news_top_title
     * @param answer
     */
    private void viewList(LinearLayout ll_news_list,ImageView iv_news_top_img,TextView tv_news_top_title,Answer answer) {
        switch (answer.getCode()) {
            case "302000"://新闻
                List<News> listNews = answer.getListNews();
                final News news = listNews.get(0);
                if (TextUtils.isEmpty(news.getIcon())) {
                    iv_news_top_img.setImageResource(R.drawable.splash_screen_b);
                }else{
                    finalImageLoader.display(iv_news_top_img,news.getIcon());
                }
                iv_news_top_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle b=new Bundle();
                        b.putString("url",news.getDetailurl());
                        SysUtils.startActivity((Activity) mContext, WebActivity.class,b);
                    }
                });
                ll_news_list.removeAllViews();
                for(int i=1;i<4;i++){
                    ll_news_list.addView(createNewsView(listNews.get(i)));
                }
                tv_news_top_title.setText(news.getArticle());
                break;
            case "308000"://菜谱
                List<Cook> listCook = answer.getListCook();
                final Cook cook = listCook.get(0);
                if (TextUtils.isEmpty(cook.getIcon())) {
                    iv_news_top_img.setImageResource(R.drawable.splash_screen_b);
                }else{
                    finalImageLoader.display(iv_news_top_img,cook.getIcon());
                }
                iv_news_top_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle b=new Bundle();
                        b.putString("url",cook.getDetailurl());
                        SysUtils.startActivity((Activity) mContext, WebActivity.class,b);
                    }
                });
                ll_news_list.removeAllViews();
                for(int i=1;i<4;i++){
                    ll_news_list.addView(createCookView(listCook.get(i)));
                }
                tv_news_top_title.setText(cook.getInfo());
                break;
        }
    }

    View createNewsView(final News news){
        View view =mInflater.inflate(R.layout.item_list,null);
        ImageView iv= (ImageView) view.findViewById(R.id.iv);
        TextView tv= (TextView) view.findViewById(R.id.tv);
        if (TextUtils.isEmpty(news.getIcon())) {
            iv.setImageResource(R.drawable.splash_screen_b);
        }else{
            finalImageLoader.display(iv,news.getIcon());
        }
        tv.setText(news.getArticle());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b=new Bundle();
                b.putString("url",news.getDetailurl());
                SysUtils.startActivity((Activity) mContext, WebActivity.class,b);
            }
        });
        return view;
    }

    View createCookView(final Cook cook){
        View view =mInflater.inflate(R.layout.item_list,null);
        ImageView iv= (ImageView) view.findViewById(R.id.iv);
        TextView tv= (TextView) view.findViewById(R.id.tv);
        if (TextUtils.isEmpty(cook.getIcon())) {
            iv.setImageResource(R.drawable.splash_screen_b);
        }else{
            finalImageLoader.display(iv,cook.getIcon());
        }
        tv.setText(cook.getInfo());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b=new Bundle();
                b.putString("url",cook.getDetailurl());
                SysUtils.startActivity((Activity) mContext, WebActivity.class,b);
            }
        });
        return view;
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
