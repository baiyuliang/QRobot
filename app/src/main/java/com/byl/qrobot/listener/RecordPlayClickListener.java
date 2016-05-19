package com.byl.qrobot.listener;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.byl.qrobot.R;
import com.byl.qrobot.util.ToastUtil;

import java.io.File;
import java.io.FileInputStream;


/**
 * 播放录音文件
 * @ClassName: RecordPlayClickListener
 * @author 白玉梁
 */
public class RecordPlayClickListener implements OnClickListener {

	public ImageView iv_voice;
	public AnimationDrawable anim = null;
	public Context context;
	public MediaPlayer mediaPlayer = null;
	
	public static RecordPlayClickListener currentPlayListener = null;
	
	public AudioManager audioManager;

	public String filepath;

	public RecordPlayClickListener(Context context, ImageView voice, String filepath) {
		this.iv_voice = voice;
		this.context = context;
		this.filepath=filepath;
	}
	
	/**
	 * 播放语音
	 */
	@SuppressWarnings("resource")
	public void startPlayRecord(String filePath) {
		if(audioManager==null){
			audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		}
		if(mediaPlayer==null){
			mediaPlayer = new MediaPlayer();
		}
		audioManager.setMode(AudioManager.MODE_NORMAL);
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
		audioManager.setSpeakerphoneOn(true);
		try {
			mediaPlayer.reset();
			FileInputStream fis = new FileInputStream(new File(filePath));
			mediaPlayer.setDataSource(fis.getFD());
			mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer arg0) {
					arg0.start();
					startRecordAnimation();
				}
			});
			mediaPlayer.prepare();
			//语音播放完成监听
			mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					stop();
				}
			});
		} catch (Exception e) {
			
		}
	}
	
	/**
	 * 播放结束
	 */
	public void stop(){
		if(null!=currentPlayListener){
			currentPlayListener.stopPlayRecord();
			currentPlayListener=null;
		}
	}

	/**
	 * 停止播放
	 */
	public void stopPlayRecord() {
		if (mediaPlayer != null) {
			try {
				mediaPlayer.stop();
				mediaPlayer.release();
			} catch (Exception e) {
				
			}
			mediaPlayer=null;
		}
		stopRecordAnimation();
	}

	/**
	 * 开启播放动画
	 */
	@SuppressWarnings("ResourceType")
	public void startRecordAnimation() {
		iv_voice.setImageResource(R.drawable.anim_chat_voice_right);
		anim = (AnimationDrawable) iv_voice.getDrawable();
		anim.start();
	}

	/**
	 * 停止播放动画
	 */
	public void stopRecordAnimation() {
		iv_voice.setImageResource(R.drawable.voice_left3);
		if (anim != null) {
			anim.stop();
			anim=null;
		}
	}

	@Override
	public void onClick(View arg0) {
		if(null!=currentPlayListener){
			if(currentPlayListener.filepath.equals(filepath)){//点击的相同语音
				currentPlayListener.stopPlayRecord();
				currentPlayListener=null;
				return;
			}else{
				currentPlayListener.stopPlayRecord();
				currentPlayListener=null;
			}
		}
		if (!(new File(filepath).exists())) {
			ToastUtil.showToast(context, "语音文件不存在...");
			return;
		}
		currentPlayListener=this;
		startPlayRecord(filepath);//播放语音
	}

}