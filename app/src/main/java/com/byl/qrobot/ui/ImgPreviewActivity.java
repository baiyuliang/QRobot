package com.byl.qrobot.ui;


import com.byl.qrobot.R;
import com.byl.qrobot.config.Const;
import com.byl.qrobot.util.PreferencesUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.polites.android.GestureImageView;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ImgPreviewActivity extends Activity{

	private GestureImageView img;
	private RelativeLayout loading_progress;
	private TextView  loadingText;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;
	String url;
	TextView tv_address;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_img_page);
		
		options = new DisplayImageOptions.Builder()
		.resetViewBeforeLoading(true)
		.cacheOnDisc(true)
		.imageScaleType(ImageScaleType.EXACTLY)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.considerExifParams(true)
		.displayer(new FadeInBitmapDisplayer(0))
		.build();
		
		url=getIntent().getStringExtra("url");
		
		initView();
		initData();
	}


	private void initView() {
		img=(GestureImageView) findViewById(R.id.img);
		loading_progress= (RelativeLayout)findViewById(R.id.loading_progress);
		loadingText=(TextView) findViewById(R.id.loadingText);
		tv_address=(TextView) findViewById(R.id.tv_address);
		tv_address.setText(PreferencesUtils.getSharePreStr(this, Const.ADDRESS));
	}
	
	private void initData() {
		imageLoader.displayImage(url, img, options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				loading_progress.setVisibility(View.VISIBLE);
			}
			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				String message = null;
				switch (failReason.getType()) {
					case IO_ERROR:
						message = "网络异常";
						break;
					case DECODING_ERROR:
						message = "图片解析失败";
						break;
					case NETWORK_DENIED:
						message = "图片加载失败";
						break;
					case OUT_OF_MEMORY:
						message = "内存溢出";
						break;
					case UNKNOWN:
						message = "未知错误";
						break;
				}
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
				loading_progress.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				loading_progress.setVisibility(View.GONE);
			}
		},new ImageLoadingProgressListener() {        
		   @Override  
		    public void onProgressUpdate(String imageUri, View view, int current,int total) {     
		     //在这里更新 ProgressBar的进度信息  
			   int progress=0;
			   if(current!=0&&current<=total){
				   progress=(int)((float) current/(float)total*100);
				   loadingText.setText(""+progress+"%");
			   }
		     }  
		   });  
	}
	

}
