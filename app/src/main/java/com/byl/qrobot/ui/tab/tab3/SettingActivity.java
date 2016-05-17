package com.byl.qrobot.ui.tab.tab3;

import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.byl.qrobot.R;
import com.byl.qrobot.config.Const;
import com.byl.qrobot.db.ChatMsgDao;
import com.byl.qrobot.ui.base.SlideBackActivity;
import com.byl.qrobot.util.DialogUtil;
import com.byl.qrobot.util.PreferencesUtils;
import com.byl.qrobot.util.SysUtils;
import com.byl.qrobot.util.ToastUtil;
import com.byl.qrobot.view.ActionSheetBottomDialog;
import com.byl.qrobot.view.ActionSheetCenterDialog;

/**
 * @author 白玉梁
 */
public class SettingActivity extends SlideBackActivity {
    private RelativeLayout rl_1, rl_2, rl_3;
    TextView tv_1, tv_2;

    ActionSheetCenterDialog actionSheetCenterDialog1, actionSheetCenterDialog2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initTitleBar("我的", "设置", "", this);
        initView();
        initData();
    }

    private void initView() {
        rl_1 = (RelativeLayout) findViewById(R.id.rl_1);//聊天记录
        rl_2 = (RelativeLayout) findViewById(R.id.rl_2);//录音设置
        rl_3 = (RelativeLayout) findViewById(R.id.rl_3);//朗读语言

        tv_1 = (TextView) findViewById(R.id.tv_1);
        tv_2 = (TextView) findViewById(R.id.tv_2);

        rl_1.setOnClickListener(this);
        rl_2.setOnClickListener(this);
        rl_3.setOnClickListener(this);
    }

    private void initData() {
        String str1 = PreferencesUtils.getSharePreStr(this, Const.XF_SET_VOICE_RECORD);
        String str2 = PreferencesUtils.getSharePreStr(this, Const.XF_SET_VOICE_READ);
        if (TextUtils.isEmpty(str1)) {
            tv_1.setText("录音语言：mandarin");
        } else {
            tv_1.setText("录音语言："+str1);
        }
        if (TextUtils.isEmpty(str2)) {
            tv_2.setText("朗读语言：xiaoyu");
        } else {
            tv_2.setText("朗读语言："+str2);
        }
    }

    @Override
    public void onClick(View arg0) {
        super.onClick(arg0);
        switch (arg0.getId()) {//返回
            case R.id.rl_1:
                SysUtils.startActivity(this, MsgHistoryActivity.class);
                break;
            case R.id.rl_2:
                actionSheetCenterDialog1 = new ActionSheetCenterDialog(this)
                        .builder()
                        .addSheetItem("mandarin(普通话)", ActionSheetCenterDialog.SheetItemColor.Blue, onSheetItemClickListener1)
                        .addSheetItem("cantonese(粤语)", ActionSheetCenterDialog.SheetItemColor.Blue, onSheetItemClickListener1)
                        .addSheetItem("henanese(河南话)", ActionSheetCenterDialog.SheetItemColor.Blue, onSheetItemClickListener1)
                        .addSheetItem("en_us(英语)", ActionSheetCenterDialog.SheetItemColor.Blue, onSheetItemClickListener1)
                ;
                actionSheetCenterDialog1.show();
                break;
            case R.id.rl_3:
                actionSheetCenterDialog2 = new ActionSheetCenterDialog(this)
                        .builder()
                        .addSheetItem("xiaoyu(男普通话)", ActionSheetCenterDialog.SheetItemColor.Blue, onSheetItemClickListener2)
                        .addSheetItem("xiaoyan(女普通话)", ActionSheetCenterDialog.SheetItemColor.Blue, onSheetItemClickListener2)
                        .addSheetItem("xiaomei(女粤语)", ActionSheetCenterDialog.SheetItemColor.Blue, onSheetItemClickListener2)
                        .addSheetItem("xiaolin(女台湾话)", ActionSheetCenterDialog.SheetItemColor.Blue, onSheetItemClickListener2)
                        .addSheetItem("xiaorong(女四川话)", ActionSheetCenterDialog.SheetItemColor.Blue, onSheetItemClickListener2)
                        .addSheetItem("xiaokun(男河南话)", ActionSheetCenterDialog.SheetItemColor.Blue, onSheetItemClickListener2);
                actionSheetCenterDialog2.show();
                break;
        }
    }

    ActionSheetCenterDialog.OnSheetItemClickListener onSheetItemClickListener1 = new ActionSheetCenterDialog.OnSheetItemClickListener() {
        @Override
        public void onClick(int which) {
            switch (which) {
                case 1:
                    tv_1.setText("录音语言：mandarin");
                    PreferencesUtils.putSharePre(SettingActivity.this,Const.XF_SET_VOICE_RECORD,"mandarin");
                    break;
                case 2:
                    tv_1.setText("录音语言：cantonese");
                    PreferencesUtils.putSharePre(SettingActivity.this,Const.XF_SET_VOICE_RECORD,"cantonese");
                    break;
                case 3:
                    tv_1.setText("录音语言：henanese");
                    PreferencesUtils.putSharePre(SettingActivity.this,Const.XF_SET_VOICE_RECORD,"henanese");
                    break;
                case 4:
                    tv_1.setText("录音语言：en_us");
                    PreferencesUtils.putSharePre(SettingActivity.this,Const.XF_SET_VOICE_RECORD,"en_us");
                    break;
            }
        }
    };

    ActionSheetCenterDialog.OnSheetItemClickListener onSheetItemClickListener2 = new ActionSheetCenterDialog.OnSheetItemClickListener() {
        @Override
        public void onClick(int which) {
            switch (which) {
                case 1:
                    tv_2.setText("朗读语言：xiaoyu");
                    PreferencesUtils.putSharePre(SettingActivity.this,Const.XF_SET_VOICE_READ,"xiaoyu");
                    break;
                case 2:
                    tv_2.setText("朗读语言：xiaoyan");
                    PreferencesUtils.putSharePre(SettingActivity.this,Const.XF_SET_VOICE_READ,"xiaoyan");
                    break;
                case 3:
                    tv_2.setText("朗读语言：xiaomei");
                    PreferencesUtils.putSharePre(SettingActivity.this,Const.XF_SET_VOICE_READ,"xiaomei");
                    break;
                case 4:
                    tv_2.setText("朗读语言：xiaolin");
                    PreferencesUtils.putSharePre(SettingActivity.this,Const.XF_SET_VOICE_READ,"xiaolin");
                    break;
                case 5:
                    tv_2.setText("朗读语言：xiaorong");
                    PreferencesUtils.putSharePre(SettingActivity.this,Const.XF_SET_VOICE_READ,"xiaorong");
                    break;
                case 6:
                    tv_2.setText("朗读语言：xiaokun");
                    PreferencesUtils.putSharePre(SettingActivity.this,Const.XF_SET_VOICE_READ,"xiaokun");
                    break;
            }
        }
    };

}
