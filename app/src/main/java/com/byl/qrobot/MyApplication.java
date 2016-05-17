package com.byl.qrobot;

import android.app.Application;
import android.content.Context;

import com.baidu.mapapi.SDKInitializer;
import com.byl.qrobot.config.Const;
import com.byl.qrobot.util.LogUtil;
import com.iflytek.cloud.SpeechUtility;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/*
                   _ooOoo_
                  o8888888o
                  88" . "88
                  (| -_- |)
                  O\  =  /O
               ____/`---'\____
             .'  \\|     |//  `.
            /  \\|||  :  |||//  \
           /  _||||| -:- |||||-  \
           |   | \\\  -  /// |   |
           | \_|  ''\---/''  |   |
           \  .-\__  `-`  ___/-. /
         ___`. .'  /--.--\  `. . __
      ."" '<  `.___\_<|>_/___.'  >'"".
     | | :  `- \`.;`\ _ /`;.`/ - ` : | |
     \  \ `-.   \_ __\ /__ _/   .-` /  /
======`-.____`-.___\_____/___.-`____.-'======
                   `=---='
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
         佛祖保佑       永无BUG
*/
//          佛曰:
//                  写字楼里写字间，写字间里程序员；
//                  程序人员写程序，又拿程序换酒钱。
//                  酒醒只在网上坐，酒醉还来网下眠；
//                  酒醉酒醒日复日，网上网下年复年。
//                  但愿老死电脑间，不愿鞠躬老板前；
//                  奔驰宝马贵者趣，公交自行程序员。
//                  别人笑我忒疯癫，我笑自己命太贱；
//                  不见满街漂亮妹，哪个归得程序员？
/**
 * @author 白玉梁
 * @blog http://blog.csdn.net/baiyuliang2013
 * @weibo http://weibo.com/2611894214/profile?topnav=1&wvr=6&is_all=1
 *
 * */
public class MyApplication extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();
		initImageLoader(getApplicationContext());        //初始化图片加载器相关配置
		SDKInitializer.initialize(this);	    //初始化地图相关
		SpeechUtility.createUtility(this, "appid="+ Const.XF_VOICE_APPID);
		// 以下语句用于设置日志开关（默认开启），设置成false时关闭语音云SDK日志打印
		// Setting.setShowLog(false);
		LogUtil.isShowLog=true;//是否打印log
	}
	
	public  void initImageLoader(Context context) {

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2)// 设置线程的优先级
				.denyCacheImageMultipleSizesInMemory()// 当同一个Uri获取不同大小的图片，缓存到内存时，只缓存一个。默认会缓存多个不同的大小的相同图片
				.discCacheFileNameGenerator(new Md5FileNameGenerator())// 设置缓存文件的名字
				.discCacheFileCount(60)// 缓存文件的最大个数
				.tasksProcessingOrder(QueueProcessingType.LIFO)// 设置图片下载和显示的工作队列排序
				.build();
		ImageLoader.getInstance().init(config);
	}

}
