package com.byl.qrobot.share;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.byl.qrobot.R;
import com.byl.qrobot.config.Const;
import com.byl.qrobot.share.qq.QQShareUtil;
import com.byl.qrobot.share.wx.WXShareUtil;
import com.byl.qrobot.util.ToastUtil;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;


/**
 * Created by baiyuliang on 2015-11-24.
 */
public class PopShareHelper {

    private Activity context;
    private PopupWindow pop;
    private View view;
    private OnClickOkListener onClickOkListener;

    // 微信分享
    public IWXAPI wxApi;
    //QQ分享
    public Tencent mTencent;
    IUiListener iuiListener;
    //分享內容
    ShareContent shareContent;

    public int share_type;//1.微信，2.微博，3.QQ

    public PopShareHelper(Activity context) {
        this.context = context;
        // 微信
        wxApi = WXAPIFactory.createWXAPI(context, Const.WX_APP_ID, true);
        wxApi.registerApp(Const.WX_APP_ID);
        // QQ
        mTencent = Tencent.createInstance(Const.QQ_APP_ID, context);

        view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.pop_share, null);
        pop = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        initPop();
        initView();
    }

    public void setShareContent(ShareContent shareContent) {
        this.shareContent = shareContent;
    }

    private void initPop() {
        pop.setAnimationStyle(android.R.style.Animation_InputMethod);
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    private void initView() {
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        ImageView iv_share_wx = (ImageView) view.findViewById(R.id.iv_share_wx);//分享到微信
        ImageView iv_share_wx_circle = (ImageView) view.findViewById(R.id.iv_share_wx_circle);//分享到朋友圈
        ImageView iv_share_qq = (ImageView) view.findViewById(R.id.iv_share_qq);//分享到qq
        ImageView iv_share_qzone = (ImageView) view.findViewById(R.id.iv_share_qzone);//分享到qq空间

        iv_share_wx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share_type = 1;
                if (wxApi.isWXAppInstalled()) {
                    pop.dismiss();
                    WXShareUtil.shareToWX(context, wxApi, shareContent);
                } else {
                    ToastUtil.showToast(context, "请先安装微信");
                }

            }
        });

        iv_share_wx_circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share_type = 1;
                if (wxApi.isWXAppInstalled()) {
                    pop.dismiss();
                    WXShareUtil.shareToWXCircle(context, wxApi, shareContent);
                } else {
                    ToastUtil.showToast(context, "请先安装微信");
                }

            }
        });

        iv_share_qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share_type = 3;
                pop.dismiss();
                QQShareUtil.shareToQQ(context, mTencent, shareContent, null);
            }
        });

        iv_share_qzone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share_type = 3;
                pop.dismiss();
                QQShareUtil.shareToQQZone(context, mTencent, shareContent, null);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();

            }
        });
    }

    /**
     * 显示
     *
     * @param view
     */
    public void show(View view) {
        pop.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 隐藏监听
     *
     * @param onDismissListener
     */
    public void setOnDismissListener(PopupWindow.OnDismissListener onDismissListener) {
        pop.setOnDismissListener(onDismissListener);
    }

    public void setOnClickOkListener(OnClickOkListener onClickOkListener) {
        this.onClickOkListener = onClickOkListener;
    }

    public interface OnClickOkListener {
        public void onClickOk(String str);
    }

    /**
     * 设置QQ分享监听
     *
     * @param iuiListener
     */
    public void setIUiListener(IUiListener iuiListener) {
        this.iuiListener = iuiListener;
    }


}
