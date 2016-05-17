package com.byl.qrobot.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.byl.qrobot.R;
import com.byl.qrobot.bean.Music;
import com.byl.qrobot.config.Const;
import com.byl.qrobot.ui.login.LoginActivity;
import com.byl.qrobot.util.LogUtil;
import com.byl.qrobot.util.MusicSearchUtil;
import com.byl.qrobot.util.PreferencesUtils;
import com.byl.qrobot.util.SysUtils;
import com.byl.qrobot.util.SystemBarTintManager;
import com.byl.qrobot.util.ToastUtil;
import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.AdKeys;
import com.iflytek.voiceads.IFLYAdListener;
import com.iflytek.voiceads.IFLYAdSize;
import com.iflytek.voiceads.IFLYFullScreenAd;


public class WelcomeActivity extends Activity {

    String versioncode = "";//当前版本号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.transparent);//通知栏所需颜色
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //防止点击home键，再点击APP图标时应用重新启动
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        versioncode = SysUtils.getVersionCode(this);//获得当前APP版本号
        initFile();
        initAD();
    }


    /**
     * 初始化文件夹
     */
    public void initFile() {
        if (SysUtils.extraUse()) {
            SysUtils.initFiles();
        } else {
            ToastUtil.showToast(this, "请安装存储卡");
        }
    }

    private void initData(int delay_s) {
        //如果版本更新时更新了引导图，则将Const.VERSION_CODE名改变即可,没有更新则不改变
        String versionCode = PreferencesUtils.getSharePreStr(WelcomeActivity.this, Const.VERSION_CODE);
        //两种情况需要显示引导页
        //1.如果保存的版本号为空（新安装的）；
        //2.当前版本号与保存的版本号不同（升级）；
        if (TextUtils.isEmpty(versionCode)) {
            Intent intent = new Intent(WelcomeActivity.this, GuidActivity.class);
            startActivity(intent);
            finish();
        } else {
//            //显示欢迎页，3秒后进入主页
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (SysUtils.isLogin(WelcomeActivity.this)) {
                        SysUtils.startActivity(WelcomeActivity.this, MainActivity.class);//已登录进入首页
                    } else {
                        SysUtils.startActivity(WelcomeActivity.this, LoginActivity.class);//未登录则进入登录
                    }
                    finish();
                }
            }, delay_s * 1000);
        }

    }

    void initAD() {
        final IFLYFullScreenAd fullScreenAd = IFLYFullScreenAd.createFullScreenAd(this, Const.XF_AD_FULLSCREEN_ID);
        fullScreenAd.setAdSize(IFLYAdSize.FULLSCREEN);
        fullScreenAd.setParameter(AdKeys.SHOW_TIME_FULLSCREEN, "6000");
        fullScreenAd.setParameter(AdKeys.DOWNLOAD_ALERT, "true");//下载广告前，提示?􄖭􁒯􀩺􀡽􀋈􁕩􃃇􁨀􂽪
        fullScreenAd.loadAd(new IFLYAdListener() {
            @Override
            public void onAdReceive() {
                fullScreenAd.showAd();
                initData(5);
            }

            @Override
            public void onAdFailed(AdError error) {
                initData(3);
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

    }

    /**
     * 返回键监听,禁止返回键
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}