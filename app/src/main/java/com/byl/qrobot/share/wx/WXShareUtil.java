package com.byl.qrobot.share.wx;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.byl.qrobot.R;
import com.byl.qrobot.share.ShareContent;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;

import java.io.ByteArrayOutputStream;


public class WXShareUtil {

//	/**
//	 * 分享到朋友
//	 * @param api
//	 * @param text
//	 */
//	public static void sendTextToWX(IWXAPI api, String text) {
//		WXTextObject textObj = new WXTextObject();
//		textObj.text = text;


//		// 用WXTextObject对象初始化一个WXMediaMessage对象
//		WXMediaMessage msg = new WXMediaMessage();
//		msg.mediaObject = textObj;
//		msg.description = text;
//		// 构造一个Req
//		SendMessageToWX.Req req = new SendMessageToWX.Req();
//		req.transaction = buildTransaction("text"); //
//		// transaction字段用于唯一标识一个请求
//		req.message = msg;
//		req.scene = SendMessageToWX.Req.WXSceneSession;
//		api.sendReq(req);
//	}
//
//	/**
//	 * 分享到朋友圈
//	 * @param api
//	 * @param text
//	 */
//	public static void sendTextToWXCircle(IWXAPI api, String text) {
//		WXTextObject textObj = new WXTextObject();
//		textObj.text = text;
//		WXMediaMessage msg = new WXMediaMessage();
//		msg.mediaObject = textObj;
//		msg.description = text;
//		SendMessageToWX.Req req = new SendMessageToWX.Req();
//		req.transaction = buildTransaction("text"); //
//		req.message = msg;
//		req.scene = SendMessageToWX.Req.WXSceneTimeline;
//		api.sendReq(req);
//	}

    /**
     * 分享到朋友
     *
     * @param api
     * @param shareContent
     */
    public static void shareToWX(Context context, IWXAPI api, ShareContent shareContent) {
        WXWebpageObject webpageObj = new WXWebpageObject();
        webpageObj.webpageUrl = shareContent.url;
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = webpageObj;
        msg.title = shareContent.title;
        msg.description = shareContent.content;
        Bitmap thumbBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.app_icon);
        msg.thumbData = bmpToByteArray(thumbBmp, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        api.sendReq(req);

    }

    /**
     * 分享到朋友圈
     *
     * @param api
     * @param shareContent
     */
    public static void shareToWXCircle(Context context, IWXAPI api, ShareContent shareContent) {
        WXWebpageObject webpageObj = new WXWebpageObject();
        webpageObj.webpageUrl = shareContent.url;
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = webpageObj;
        msg.title = shareContent.title;
        msg.description = shareContent.content;
        Bitmap thumbBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.app_icon);
        msg.thumbData = bmpToByteArray(thumbBmp, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);

    }

    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis())
                : type + System.currentTimeMillis();
    }

    private static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
