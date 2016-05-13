package com.byl.qrobot.util;

import android.media.AudioManager;
import android.media.MediaPlayer;

/**
 * 音乐在线播放工具类
 * Created by baiyuliang on 2016-5-13.
 */
public class MusicPlayManager implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {


    public MediaPlayer mediaPlayer; // 媒体播放器

    public void play(String url) throws Exception {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置媒体流类型
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            LogUtil.e("音乐播放开始>>" + url);
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url); // 设置数据源
            mediaPlayer.prepare(); // prepare自动播放
        }
    }

    // 暂停
    public void pause() {
        if (mediaPlayer != null)
            mediaPlayer.pause();
    }

    // 停止
    public void stop() {
        LogUtil.e("音乐播放停止");
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    // 播放完成
    @Override
    public void onCompletion(MediaPlayer mp) {
        LogUtil.e("音乐播放完成");
        mp.start();
    }

    public boolean isPlaying() {
        if (mediaPlayer != null)
            return true;
        else
            return false;
    }

}
