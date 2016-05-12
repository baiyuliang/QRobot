package com.byl.qrobot.ui.base;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.byl.qrobot.R;
import com.byl.qrobot.config.Const;
import com.byl.qrobot.util.SystemBarTintManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import net.tsz.afinal.FinalBitmap;

import java.io.File;

/**
 * @author 白玉梁
 */
public class AppBaseActivity extends Activity implements OnClickListener {

    public FinalBitmap finalImageLoader;
    public ImageLoader imageLoader;

    public TextView tv_left, tv_title, tv_right;
    public ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.common_title_bg);//通知栏所需颜色
        }
        super.onCreate(savedInstanceState);

        finalImageLoader = FinalBitmap.create(this);
        finalImageLoader.configDiskCachePath(new File(Environment.getExternalStorageDirectory(), "qrobot/images/cache").getAbsolutePath());
        imageLoader = ImageLoader.getInstance();

    }

    /**
     * 隐藏软键盘
     * hideSoftInputView
     *
     * @param
     * @return void
     * @throws
     * @Title: hideSoftInputView
     */
    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 弹出输入法窗口
     */
    public void showSoftInputView(final EditText et) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ((InputMethodManager) et.getContext().getSystemService(Service.INPUT_METHOD_SERVICE)).toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 0);
    }

    /**
     * 初始化titlebar，该方法只有在标题栏布局符合此规则时才能调用
     * @param left titlebar左按钮
     * @param title titlebar标题
     * @param right titlebar 右按钮
     * @param onClickListener 左右按钮点击事件
     */
    public void initTitleBar(String left,String title,String right,OnClickListener onClickListener){
        tv_left=(TextView) findViewById(R.id.tv_left);//返回按钮
        tv_title=(TextView) findViewById(R.id.tv_title);//标题
        tv_right=(TextView) findViewById(R.id.tv_right);//更多(右侧)按钮
        pb=(ProgressBar) findViewById(R.id.pb);// 标题栏数据加载ProgressBar

        if(!TextUtils.isEmpty(left)){
            tv_left.setText(left);
            tv_left.setVisibility(View.VISIBLE);
            tv_left.setOnClickListener(onClickListener);
        }

        if(!TextUtils.isEmpty(title)){
            tv_title.setText(title);
        }

        if(!TextUtils.isEmpty(right)){
            tv_right.setText(right);
            tv_right.setVisibility(View.VISIBLE);
            tv_right.setOnClickListener(onClickListener);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_in_left, R.anim.push_out_right);
    }

    /**
     * 如果子类支持点击左上角返回按钮返回，则在子类的onClick方法中需添加super.onClick(View view);
     */
    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.tv_left:
                finish();
                break;
        }
    }

}
