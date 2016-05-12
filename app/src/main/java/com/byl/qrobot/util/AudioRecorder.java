package com.byl.qrobot.util;

import android.media.MediaRecorder;

import com.byl.qrobot.config.Const;

import java.io.File;
import java.io.IOException;

/**
 * 录音工具
 * @author 白玉梁
 * @date 2015-6-17 上午11:22:24
 */
@SuppressWarnings("deprecation")
public class AudioRecorder {
	private static int SAMPLE_RATE_IN_HZ = 8000;

	final MediaRecorder recorder = new MediaRecorder();
	final String path;

	public AudioRecorder(String name) {
		this.path = sanitizePath(name);
	}

	private String sanitizePath(String name) {
		SysUtils.initFiles();
		return Const.FILE_VOICE_CACHE + name;
	}

	public void start() throws IOException {
		String state = android.os.Environment.getExternalStorageState();
		if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
			throw new IOException("SD Card is not mounted,It is  " + state+ ".");
		}
		File directory = new File(path).getParentFile();
		if (!directory.exists() && !directory.mkdirs()) {
			throw new IOException("Path to file could not be created");
		}
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setAudioSamplingRate(SAMPLE_RATE_IN_HZ);
		recorder.setOutputFile(path);
		recorder.prepare();
		recorder.start();
	}

	public void stop() throws IOException {
		recorder.stop();
		recorder.release();
	}

	public double getAmplitude() {
		if (recorder != null) {
			return (recorder.getMaxAmplitude());
		} else
			return 0;
	}
}