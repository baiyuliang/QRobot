package com.byl.qrobot.ui.login;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.byl.qrobot.R;
import com.byl.qrobot.config.Const;
import com.byl.qrobot.ui.MainActivity;
import com.byl.qrobot.util.DialogUtil;
import com.byl.qrobot.util.PreferencesUtils;
import com.byl.qrobot.util.RegexUtil;
import com.byl.qrobot.util.SysUtils;
import com.byl.qrobot.util.SystemBarTintManager;
import com.byl.qrobot.view.LoadingDialog;
import com.byl.qrobot.view.MyEditText;

import org.json.JSONObject;

/**
 * 登录
 * LoginActivity.java
 *
 * @author 白玉梁
 */
public class LoginActivity extends Activity implements OnClickListener {
    private Button btn_login;
    private MyEditText et_account, et_pwd;
    private String account = null,
            account_bak = null,
            password = null;

    public TextView tv_back, tv_title;

    LoadingDialog loadingDialog;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
            //保存用户名和密码
            PreferencesUtils.putSharePre(LoginActivity.this, Const.LOGIN_PHONE, account);
            PreferencesUtils.putSharePre(LoginActivity.this, Const.LOGIN_PWD, password);
            //跳转至首页
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();

        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.common_title_bg);//通知栏所需颜色
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initTitleBar();
        initView();
        account = account_bak = PreferencesUtils.getSharePreStr(LoginActivity.this, Const.LOGIN_PHONE);
        if (!TextUtils.isEmpty(account)) {//保存在本地的account
            et_account.setText(account);
            et_account.setSelection(account.length());
        }
    }

    public void initTitleBar() {
        tv_back = (TextView) findViewById(R.id.tv_left);//返回按钮
        tv_back.setVisibility(View.VISIBLE);
        tv_title = (TextView) findViewById(R.id.tv_title);//标题
        tv_title.setText("登录");

        tv_back.setOnClickListener(this);
    }

    public void initView() {
        btn_login = (Button) findViewById(R.id.btn_login);//登录键
        et_account = (MyEditText) findViewById(R.id.et_account);//账号
        et_pwd = (MyEditText) findViewById(R.id.et_pwd);//密码

        btn_login.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_left:
                finish();
                break;
            case R.id.tv_right:
                break;
            case R.id.btn_login:
                doLogin();
                break;
            default:
                break;
        }
    }

    private void doLogin() {
        account = et_account.getText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            DialogUtil.showErrorMsg(LoginActivity.this, "请输入手机号码");
            return;
        }
        if (!RegexUtil.checkMobile(account)) {
            DialogUtil.showErrorMsg(LoginActivity.this, "请输入正确的手机号码");
            return;
        }
        password = et_pwd.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            DialogUtil.showErrorMsg(LoginActivity.this, "请输入密码");
            return;
        }
        if (password.length() < 6 || password.length() > 16) {
            DialogUtil.showErrorMsg(LoginActivity.this, "请输入6-16位英文字母、数字");
            return;
        }
        //执行登录
        loadingDialog = new LoadingDialog(this);
        loadingDialog.setTitle("正在登录...");
        loadingDialog.show();
        //...........

        //登录成功
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        }, 2000);
    }


}
