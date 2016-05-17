package com.byl.qrobot.speech;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.EditText;

import com.byl.qrobot.config.Const;
import com.byl.qrobot.util.PraseUtil;
import com.byl.qrobot.util.PreferencesUtils;
import com.byl.qrobot.util.ToastUtil;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * 语音识别工具类
 * Created by baiyuliang on 2016-5-17.
 */
public class SpeechRecognizerUtil {

    Activity context;
    // 语音听写对象
    private SpeechRecognizer mIat;
    // 语音听写UI
    private RecognizerDialog mIatDialog;

    private HashMap<String, String> mIatResults;

    EditText editText;

    public SpeechRecognizerUtil(Activity context) {
        this.context = context;
        // 初始化识别对象
        mIat = SpeechRecognizer.createRecognizer(context, mInitListener);
        mIatDialog = new RecognizerDialog(context, mInitListener);
        mIatResults = new LinkedHashMap<>();
        setParamIat();
    }

    /**
     * 录音
     * @param editText
     */
    public void say(EditText editText) {
        this.editText=editText;
        mIatDialog.setListener(mRecognizerDialogListener);
        mIatDialog.show();
    }

    /**
     * 语音听写参数设置
     *
     * @return
     */
    public void setParamIat() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);
        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
        // 设置语言
        String l= PreferencesUtils.getSharePreStr(context,Const.XF_SET_VOICE_RECORD);
        if(TextUtils.isEmpty(l)){//默认中文普通话
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
        }else{
            if (l.equals("en_us")) {
                mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
            } else {
                mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
                mIat.setParameter(SpeechConstant.ACCENT, l);
            }
        }
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域
        mIat.setParameter(SpeechConstant.ACCENT, "zh_cn");
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "3000");
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");
        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "0");
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Const.FILE_VOICE_CACHE + "iat.wav");
    }

    /**
     * 语音听写初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                ToastUtil.showToast(context, "初始化失败，错误码：" + code);
            }
        }
    };

    /**
     * 语音听写UI监听器
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            String text = PraseUtil.parseIatResult(results.getResultString());
            String sn = null;
            // 读取json结果中的sn字段
            try {
                JSONObject resultJson = new JSONObject(results.getResultString());
                sn = resultJson.optString("sn");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mIatResults.put(sn, text);
            StringBuffer resultBuffer = new StringBuffer();
            for (String key : mIatResults.keySet()) {
                resultBuffer.append(mIatResults.get(key));
            }
            if(editText!=null){
                editText.setText(resultBuffer.toString());
                editText.setSelection(editText.length());
            }
        }

        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {
            ToastUtil.showToast(context, error.getPlainDescription(true));
        }

    };

}
