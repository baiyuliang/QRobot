package com.byl.qrobot.ui.tab.tab2;


import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.byl.qrobot.R;
import com.byl.qrobot.bean.Msg;
import com.byl.qrobot.config.Const;
import com.byl.qrobot.db.ChatMsgDao;
import com.byl.qrobot.ui.base.BaseActivity;
import com.byl.qrobot.util.ExpressionUtil;
import com.byl.qrobot.util.SysUtils;
import com.byl.qrobot.util.ToastUtil;
import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.AdKeys;
import com.iflytek.voiceads.IFLYAdListener;
import com.iflytek.voiceads.IFLYAdSize;
import com.iflytek.voiceads.IFLYBannerAd;

/**
 * 主程序
 */
public class Tab2Activity extends BaseActivity {

    LinearLayout ll_chat;
    TextView tv_content, tv_time;

    ChatMsgDao chatMsgDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab2);
        setPadding(R.id.activity_tab2);
        initTitleBar("", "消息", "", null);
        chatMsgDao = new ChatMsgDao(this);
        initView();
        initAD();
    }

    /**
     * 横幅广告
     */
    private void initAD() {
        final IFLYBannerAd bannerView = IFLYBannerAd.createBannerAd(this, Const.XF_AD_BANNER_ID);
        bannerView.setAdSize(IFLYAdSize.BANNER);
		bannerView.setParameter(AdKeys.DOWNLOAD_ALERT, "true");
        bannerView.loadAd(new IFLYAdListener() {
            @Override
            public void onAdReceive() {
                bannerView.showAd();
            }

            @Override
            public void onAdFailed(AdError adError) {

            }

            @Override
            public void onAdClick() {

            }

            @Override
            public void onAdClose() {

            }

            @Override
            public void onAdExposure() {

            }
        });
        LinearLayout ll_ad = (LinearLayout)findViewById(R.id.ll_ad);
        ll_ad.removeAllViews();
        ll_ad.addView(bannerView);
    }

    private void initView() {
        ll_chat = (LinearLayout) findViewById(R.id.ll_chat);
        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_time = (TextView) findViewById(R.id.tv_time);
        ll_chat.setOnClickListener(this);
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.ll_chat:
                SysUtils.startActivity(getParent(), ChatActivity.class);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Msg msg = chatMsgDao.queryTheLastMsg();
        if (msg != null) {
            tv_time.setText(msg.getDate());
            switch (msg.getType()) {
                case Const.MSG_TYPE_TEXT:
                    tv_content.setText(ExpressionUtil.prase(this,tv_content,msg.getContent()));
                    break;
                case Const.MSG_TYPE_IMG:
                    tv_content.setText("[图片]");
                    break;
                case Const.MSG_TYPE_VOICE:
                    tv_content.setText("[语音]");
                    break;
                case Const.MSG_TYPE_LOCATION:
                    tv_content.setText("[位置]");
                    break;
                case Const.MSG_TYPE_MUSIC:
                    tv_content.setText("[歌曲]");
                    break;
            }
        }
    }
}
