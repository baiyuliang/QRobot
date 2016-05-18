package com.byl.qrobot.share.qq;

import android.app.Activity;
import android.os.Bundle;

import com.byl.qrobot.share.ShareContent;
import com.byl.qrobot.util.SysUtils;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

import java.util.ArrayList;


public class QQShareUtil {


    /**
     * 分享到朋友
     * @param shareContent
     */
    public static void shareToQQ(Activity context, Tencent mTencent, ShareContent shareContent,IUiListener iUiListener) {
        Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, shareContent.title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  shareContent.content);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  shareContent.url);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://pp.myapp.com/ma_icon/0/icon_10626662_1452868973/96");
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, SysUtils.getAppName(context));
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
        mTencent.shareToQQ(context, params, iUiListener);
    }

    /**
     * 分享到空间
     * @param shareContent
     */
    public static void shareToQQZone(Activity context, Tencent mTencent, ShareContent shareContent, IUiListener iUiListener) {
        //分享类型
        Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, shareContent.title);//必填
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, shareContent.content);//选填
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, shareContent.url);//必填
        ArrayList<String> arrayList=new ArrayList<>();
        arrayList.add("http://pp.myapp.com/ma_icon/0/icon_10626662_1452868973/96");
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, arrayList);
        mTencent.shareToQzone(context, params,iUiListener);
    }

}
