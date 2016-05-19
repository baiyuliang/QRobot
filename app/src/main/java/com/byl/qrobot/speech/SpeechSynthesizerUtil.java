package com.byl.qrobot.speech;

import android.app.Activity;
import android.text.TextUtils;

import com.byl.qrobot.config.Const;
import com.byl.qrobot.util.PreferencesUtils;
import com.byl.qrobot.util.ToastUtil;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechSynthesizer;

/**
 * 语音合成工具类
 * Created by baiyuliang on 2016-5-17.
 */
public class SpeechSynthesizerUtil {

    Activity context;
    private SpeechSynthesizer mTts;

    public SpeechSynthesizerUtil(Activity context) {
        this.context = context;
        this.mTts = SpeechSynthesizer.createSynthesizer(context, mTtsInitListener);
        setParamIts();
    }

    public void speech(String content) {
        if (!TextUtils.isEmpty(content))
            mTts.startSpeaking(content, null);
    }

    public void stopSpeech() {
        if (mTts!=null)
            mTts.stopSpeaking();
    }

    /**
     * 语音合成参数设置
     *
     * @return
     */
    private void setParamIts() {
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        // 根据合成引擎设置相应参数(默认 云)
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置在线合成发音人
        String l= PreferencesUtils.getSharePreStr(context,Const.XF_SET_VOICE_READ);
        if(TextUtils.isEmpty(l)){
            mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyu");
        }else{
            mTts.setParameter(SpeechConstant.VOICE_NAME, l);
        }
        //设置合成语速
        mTts.setParameter(SpeechConstant.SPEED, "50");
        //设置合成音调
        mTts.setParameter(SpeechConstant.PITCH, "50");
        //设置合成音量
        mTts.setParameter(SpeechConstant.VOLUME, "100");
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Const.FILE_VOICE_CACHE + "tts.wav");
    }

    /**
     * 语音合成初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                ToastUtil.showToast(context, "初始化失败,错误码：" + code);
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
                // mTts.startSpeaking(text, mTtsListener);
            }
        }
    };

}
