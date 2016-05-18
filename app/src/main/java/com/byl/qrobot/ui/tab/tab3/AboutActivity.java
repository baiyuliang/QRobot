package com.byl.qrobot.ui.tab.tab3;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.byl.qrobot.R;
import com.byl.qrobot.share.PopShareHelper;
import com.byl.qrobot.share.ShareContent;
import com.byl.qrobot.ui.base.AppBaseActivity;
import com.byl.qrobot.ui.base.SlideBackActivity;
import com.byl.qrobot.util.SysUtils;

/**
 * 关于
 * @author 白玉梁
 * @date 2016-5-12 下午5:07:54
 */
public class AboutActivity extends SlideBackActivity{

    String appname = "";
    int version_now;//当前版本号

    PopShareHelper popShareHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        appname = SysUtils.getAppName(this);
        initTitleBar("我的","关于","分享",this);
        popShareHelper = new PopShareHelper(this);
        initView();
        version_now = Integer.parseInt(SysUtils.getVersionCode(this));//当前版本号
    }

    private void initView() {
        ((TextView) findViewById(R.id.app_name_and_version)).setText(appname + "V" + SysUtils.getAppVersionName(this));
        Linkify.addLinks(((TextView) findViewById(R.id.tv_csdn)),Linkify.ALL);
        Linkify.addLinks(((TextView) findViewById(R.id.tv_git)),Linkify.ALL);
    }

    @Override
    public void onClick(View arg0) {
        super.onClick(arg0);
        switch (arg0.getId()){
            case R.id.tv_right:
                ShareContent shareContent = new ShareContent("小Q聊天机器人", "您的娱乐好伙伴", "http://blog.csdn.net/baiyuliang2013");
                popShareHelper.setShareContent(shareContent);
                popShareHelper.show(tv_right);
                break;
        }
    }
}
