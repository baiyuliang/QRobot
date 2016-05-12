package com.byl.qrobot.ui;


import android.app.TabActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.Toast;

import com.byl.qrobot.R;
import com.byl.qrobot.service.LocService;
import com.byl.qrobot.ui.tab.tab1.Tab1Activity;
import com.byl.qrobot.ui.tab.tab2.Tab2Activity;
import com.byl.qrobot.ui.tab.tab3.Tab3Activity;
import com.byl.qrobot.util.SystemBarTintManager;

/**
 * 主程序
 */
@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity implements CompoundButton.OnCheckedChangeListener {

    private TabHost mTabHost,mainTabHost;
    private RadioButton rb_tab1, rb_tab2,rb_tab3;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.common_title_bg);//通知栏所需颜色
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        setupIntent();

        startService(new Intent(this,LocService.class));//启动后台服务
    }

    //初始化控件
    private void initView() {
        rb_tab1 = (RadioButton) this.findViewById(R.id.rb_tab1);
        rb_tab2 = (RadioButton) this.findViewById(R.id.rb_tab2);
        rb_tab3 = (RadioButton) this.findViewById(R.id.rb_tab3);

        rb_tab1.setOnCheckedChangeListener(this);
        rb_tab2.setOnCheckedChangeListener(this);
        rb_tab3.setOnCheckedChangeListener(this);

    }

    //初始化选项卡
    private void setupIntent() {
        mTabHost = getTabHost();
        mainTabHost = this.mTabHost;
        intent = new Intent().setClass(this, Tab1Activity.class);
        mainTabHost.addTab(buildTabSpec("tab1", null, intent));
        intent = new Intent().setClass(this, Tab2Activity.class);
        mainTabHost.addTab(buildTabSpec("tab2", null, intent));
        intent = new Intent().setClass(this, Tab3Activity.class);
        mainTabHost.addTab(buildTabSpec("tab3", null, intent));
    }

    private TabHost.TabSpec buildTabSpec(String tag, String label, final Intent content) {
        return this.mTabHost.newTabSpec(tag).setIndicator(label).setContent(content);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            switch (buttonView.getId()) {
                case R.id.rb_tab1:
                    mTabHost.setCurrentTabByTag("tab1");
                    break;
                case R.id.rb_tab2:
                    mTabHost.setCurrentTabByTag("tab2");
                    break;
                case R.id.rb_tab3:
                    mTabHost.setCurrentTabByTag("tab3");
                    break;
            }
        }
    }


    int keyBackClickCount=0;
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN  ) {
            switch (keyBackClickCount++) {
                case 0:
                    Toast.makeText(this,"再按一次退出程序",Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            keyBackClickCount = 0;
                        }
                    }, 2000);
                    break;
                case 1:
                    moveTaskToBack(true);
                    break;
                default:
                    break;
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

}
