package com.byl.qrobot.adapter;

import net.tsz.afinal.FinalBitmap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.byl.qrobot.R;
import com.byl.qrobot.bean.Msg;
import com.byl.qrobot.config.Const;
import com.byl.qrobot.util.ExpressionUtil;
import com.byl.qrobot.view.CircleImageView;

import java.util.List;


/**
 * from为收到的消息，to为自己的消息
 *
 * @author baiyuliang
 */
@SuppressLint("NewApi")
public class ChatAdapter extends BaseAdapter {
    private Context mContext;
    private List<Msg> list;
    private FinalBitmap finalImageLoader;

    OnClickMsgListener onClickMsgListener;

    public ChatAdapter(Context mContext, List<Msg> list,OnClickMsgListener onClickMsgListener) {
        super();
        this.mContext = mContext;
        this.list = list;
        this.onClickMsgListener=onClickMsgListener;
        finalImageLoader = FinalBitmap.create(mContext);
    }

    public void setList(List<Msg> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHodler hodler;
        if (convertView == null) {
            hodler = new ViewHodler();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.chat_lv_item, null);
            hodler.rl_chat = (RelativeLayout) convertView.findViewById(R.id.rl_chat);//聊天布局
            //接收的消息
            hodler.fromIcon = (CircleImageView) convertView.findViewById(R.id.chatfrom_icon);//他人头像
            hodler.toIcon = (CircleImageView) convertView.findViewById(R.id.chatto_icon);//自己头像
            hodler.fromContainer = (LinearLayout) convertView.findViewById(R.id.chart_from_container);
            hodler.fromText = (TextView) convertView.findViewById(R.id.chatfrom_content);//文本
            hodler.fromImg = (ImageView) convertView.findViewById(R.id.chatfrom_img);//图片
            hodler.fromLocation = (ImageView) convertView.findViewById(R.id.chatfrom_location);//位置
            hodler.progress_load = (ProgressBar) convertView.findViewById(R.id.progress_load);//ProgressBar
            //发送的消息
            hodler.toContainer = (RelativeLayout) convertView.findViewById(R.id.chart_to_container);
            hodler.toText = (TextView) convertView.findViewById(R.id.chatto_content);//文本
            hodler.toImg = (ImageView) convertView.findViewById(R.id.chatto_img);//图片
            hodler.toLocation = (ImageView) convertView.findViewById(R.id.chatto_location);//位置
            //时间
            hodler.time = (TextView) convertView.findViewById(R.id.chat_time);

            convertView.setTag(hodler);
        } else {
            hodler = (ViewHodler) convertView.getTag();
        }

        final Msg msg = list.get(position);

        if (msg.getIsComing() == 0) {// 收到消息 from显示
            hodler.toContainer.setVisibility(View.GONE);//隐藏右侧布局
            hodler.fromContainer.setVisibility(View.VISIBLE);
            hodler.time.setText(msg.getDate());
            if (msg.getType().equals(Const.MSG_TYPE_TEXT)) {//文本类型
                hodler.fromText.setVisibility(View.VISIBLE);//文本
                hodler.fromImg.setVisibility(View.GONE);//图片
                hodler.fromLocation.setVisibility(View.GONE);//位置
                hodler.progress_load.setVisibility(View.GONE);
                SpannableStringBuilder sb = ExpressionUtil.prase(mContext, hodler.fromText, msg.getContent());// 对内容做处理
                hodler.fromText.setText(sb);
                Linkify.addLinks(hodler.fromText, Linkify.ALL);//增加文本链接类型
            } else if (msg.getType().equals(Const.MSG_TYPE_IMG)) {//图片类型
                hodler.fromText.setVisibility(View.GONE);//文本
                hodler.fromImg.setVisibility(View.VISIBLE);//图片
                hodler.fromLocation.setVisibility(View.GONE);//位置
                hodler.progress_load.setVisibility(View.GONE);
                finalImageLoader.display(hodler.fromImg, msg.getContent());//加载图片
            } else if (msg.getType().equals(Const.MSG_TYPE_LOCATION)) {//位置类型
                hodler.fromText.setVisibility(View.GONE);//文本
                hodler.fromImg.setVisibility(View.GONE);//图片
                hodler.fromLocation.setVisibility(View.VISIBLE);//位置
                hodler.progress_load.setVisibility(View.GONE);
                finalImageLoader.display(hodler.fromLocation, msg.getContent());//加载网络图片
            }
        } else {// 发送消息 to显示（目前发送消息只能发送文本类型，后期将会增加其它类型）
            hodler.toContainer.setVisibility(View.VISIBLE);
            hodler.fromContainer.setVisibility(View.GONE);
            hodler.time.setText(msg.getDate());
            if (msg.getType().equals(Const.MSG_TYPE_TEXT)) {//文本类型
                hodler.toText.setVisibility(View.VISIBLE);//文本
                hodler.toImg.setVisibility(View.GONE);//图片
                hodler.toLocation.setVisibility(View.GONE);//位置
                SpannableStringBuilder sb = ExpressionUtil.prase(mContext, hodler.toText, msg.getContent());// 对内容做处理
                hodler.toText.setText(sb);
                Linkify.addLinks(hodler.toText, Linkify.ALL);
            } else if (msg.getType().equals(Const.MSG_TYPE_IMG)) {//图片类型
                hodler.toText.setVisibility(View.GONE);//文本
                hodler.toImg.setVisibility(View.VISIBLE);//图片
                hodler.toLocation.setVisibility(View.GONE);//位置
                finalImageLoader.display(hodler.toImg, msg.getContent());//加载图片
            } else if (msg.getType().equals(Const.MSG_TYPE_LOCATION)) {//位置类型
                hodler.toText.setVisibility(View.GONE);//文本
                hodler.toImg.setVisibility(View.GONE);//图片
                hodler.toLocation.setVisibility(View.VISIBLE);//位置
                finalImageLoader.display(hodler.toLocation, msg.getContent());//加载网络图片
            }
        }

        // 文本点击
        hodler.fromText.setOnClickListener(new onClick(position));
        hodler.fromText.setOnLongClickListener(new onLongCilck(position));
        hodler.toText.setOnClickListener(new onClick(position));
        hodler.toText.setOnLongClickListener(new onLongCilck(position));
        //图片点击
        hodler.fromImg.setOnClickListener(new onClick(position));
        hodler.fromImg.setOnLongClickListener(new onLongCilck(position));
        hodler.toImg.setOnClickListener(new onClick(position));
        hodler.toImg.setOnLongClickListener(new onLongCilck(position));
        //位置
        hodler.fromLocation.setOnClickListener(new onClick(position));
        hodler.fromLocation.setOnLongClickListener(new onLongCilck(position));
        hodler.toLocation.setOnClickListener(new onClick(position));
        hodler.toLocation.setOnLongClickListener(new onLongCilck(position));

        return convertView;
    }

    class ViewHodler {
        RelativeLayout rl_chat;
        CircleImageView fromIcon, toIcon;
        ImageView fromImg, fromLocation, toImg, toLocation;
        TextView fromText, toText, time;
        LinearLayout fromContainer;
        RelativeLayout toContainer;
        ProgressBar progress_load;
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
    class onClick implements OnClickListener {
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
    class onLongCilck implements OnLongClickListener {
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
