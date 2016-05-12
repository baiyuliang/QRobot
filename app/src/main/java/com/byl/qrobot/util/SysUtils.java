package com.byl.qrobot.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.ScrollView;

import com.byl.qrobot.R;
import com.byl.qrobot.config.Const;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.Collection;

public class SysUtils {
	/**
	 * 判断内存卡是否可用
	 *
	 * @return true 可用 false 不可用
	 */
	public static final boolean extraUse() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断网络连接使用可用
	 *
	 * @param context
	 * @return true 可用 false 不可用
	 */
	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 缩放bitmap
	 *
	 * @param bitmap
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) width / w);
		float scaleHeight = ((float) height / h);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		return newbmp;
	}

	/**
	 * 根据路径加载bitmap
	 *
	 * @param path
	 *            路径
	 * @param w
	 *            款
	 * @param h
	 *            长
	 * @return
	 */
	public static final Bitmap convertToBitmap(String path, int w, int h) {
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			// 设置为ture只获取图片大小
			opts.inJustDecodeBounds = true;
			opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
			// 返回为空
			BitmapFactory.decodeFile(path, opts);
			int width = opts.outWidth;
			int height = opts.outHeight;
			float scaleWidth = 0.f, scaleHeight = 0.f;
			if (width > w || height > h) {
				// 缩放
				scaleWidth = ((float) width) / w;
				scaleHeight = ((float) height) / h;
			}
			opts.inJustDecodeBounds = false;
			float scale = Math.max(scaleWidth, scaleHeight);
			opts.inSampleSize = (int) scale;
			WeakReference<Bitmap> weak = new WeakReference<Bitmap>(BitmapFactory.decodeFile(path, opts));
			Bitmap bMapRotate = Bitmap.createBitmap(weak.get(), 0, 0, weak.get().getWidth(), weak.get().getHeight(), null, true);
			if (bMapRotate != null) {
				return bMapRotate;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static final Bitmap convertToSeeBitmap(String path) {

		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			// 设置为ture只获取图片大小
			opts.inJustDecodeBounds = true;
			opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
			// 返回为空
			BitmapFactory.decodeFile(path, opts);
			@SuppressWarnings("unused")
			int width = opts.outWidth;

			// if (width < 300 || ) {
			// int height = opts.outHeight * 300 / opts.outWidth;
			// opts.outWidth = 300;
			// opts.outHeight = height;
			// opts.inJustDecodeBounds = false;
			// }

			WeakReference<Bitmap> weak = new WeakReference<Bitmap>(BitmapFactory.decodeFile(path, opts));

			Bitmap bMapRotate = Bitmap.createBitmap(weak.get(), 0, 0, weak.get().getWidth(), weak.get().getHeight(), null, true);
			if (bMapRotate != null) {
				return bMapRotate;
			}
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 从view 得到图片
	 *
	 * @param view
	 * @return
	 */
	public static Bitmap getBitmapFromView(View view) {
		view.destroyDrawingCache();
		view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.setDrawingCacheEnabled(true);
		Bitmap bitmap = view.getDrawingCache(true);
		return bitmap;
	}

	public static Bitmap getBitmap(String path) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		// 设置为ture只获取图片大小
		opts.inJustDecodeBounds = true;
		opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
		// 返回为空
		BitmapFactory.decodeFile(path, opts);
		int width = opts.outWidth;
		int height = opts.outHeight;
		Log.i("pic", "width=" + width + " height=" + height);
		Bitmap bt = BitmapFactory.decodeFile(path);
		return bt;
	}

	public static boolean isPic(String path) {
		if (path.endsWith(".jpg") || path.endsWith(".png") || path.endsWith(".bmp")) {
			File file = new File(path);
			if (file.exists()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	// 是否是可用的图片
	public static boolean isPicSmall(String path) {
		try {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			// 设置为ture只获取图片大小
			opts.inJustDecodeBounds = true;
			opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
			// 返回为空
			BitmapFactory.decodeFile(path, opts);
			int width = opts.outWidth;
			int height = opts.outHeight;
			if (width > 300 && height > 300) {
				return true;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
		return false;
	}

	/**
	 * 对bitmap进行压缩
	 *
	 * @param bitmap
	 * @param pc
	 * @return
	 */
	@SuppressWarnings("unused")
	public static Bitmap resize_img(Bitmap bitmap, float pc) {
		Bitmap resizeBmp = null;
		try {
			Matrix matrix = new Matrix();
			matrix.postScale(pc, pc); // 长和宽放大缩小的比例
			resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			int width = resizeBmp.getWidth();
			int height = resizeBmp.getHeight();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return bitmap;
		}
		return resizeBmp;

	}

	public static Bitmap rotate(Bitmap b, int degrees) {
		if (degrees != 0 && b != null) {
			Matrix m = new Matrix();
			m.setRotate(degrees, (float) b.getWidth() / 2, (float) b.getHeight() / 2);
			try {
				Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
				if (b2 != null) {
					b.recycle(); // Bitmap操作完应该显示的释放
				}
				return b2;
			} catch (OutOfMemoryError ex) {
				Log.e("jj", "OutOfMemoryError");
				return b;
			}
		}
		return b;
	}

	/**
	 * 删除指定目录下文件及目录
	 * @return
	 */
	public static void deleteFolderFile(String filePath){
		try {
			if (!TextUtils.isEmpty(filePath)) {
				File file = new File(filePath);
				if (file.isDirectory()) {//如果该文件是文件夹，怎删除该文件夹内的内容
					File files[] = file.listFiles();
					for (int i = 0; i < files.length; i++) {
						if(files[i].getAbsolutePath().endsWith(".png")
								||files[i].getAbsolutePath().endsWith(".amr")
								||files[i].getAbsolutePath().endsWith(".wav")){
							deleteFolderFile(files[i].getAbsolutePath());
						}
					}
				}else{//否则删除该文件
					file.delete();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.e("删除文件异常>>"+e.getMessage());
		}

	}

	// 获取当前APP版本号
	public static String getVersionCode(Context context) {
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			return "1";
		}
		return String.valueOf(packInfo.versionCode);
	}

	// 获取当前APP版本名
	public static String getAppVersionName(Context context) {
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			return "1.0.0";
		}
		return packInfo.versionName;
	}

	// 获取当前APP名称
	public static String getAppName(Context context) {
		PackageManager packageManager = context.getPackageManager();
		ApplicationInfo applicationInfo = null;
		try {
			applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
		} catch (Exception e) {
			return "e护天使";
		}
		return String.valueOf(packageManager.getApplicationLabel(applicationInfo));
	}

	public static int Dp2Px(Context context, float dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	public static int Px2Dp(Context context, float px) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (px / scale + 0.5f);
	}

	/**
	 * 加载本地图片
	 *
	 * @param url
	 * @return
	 */
	public static Bitmap getLoacalBitmap(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis); // /把流转化为Bitmap图片
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取字库
	 *
	 * @param context
	 * @return
	 */
	public static Typeface getTypeface(Context context) {
		return Typeface.createFromAsset(context.getResources().getAssets(), "b.ttf");
	}

	/**
	 * 保存信息到文件中
	 *
	 * @param sb
	 *            信息内容
	 * @param filename
	 *            文件名
	 * @return
	 */
	public static boolean saveFile(StringBuffer sb, String filename) {
		try {
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				String path = Environment.getExternalStorageDirectory() + "/qrobot/log/";
				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(path + filename + ".txt");
				fos.write(sb.toString().getBytes());
				fos.close();
			}
		} catch (Exception e) {
			return false;// 保存失败
		}
		return true;// 保存成功
	}

	/**
	 * 保存信息到文件中
	 */
	@SuppressLint("SimpleDateFormat")
	public static boolean saveFile(String content) {
		try {
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				String path = Environment.getExternalStorageDirectory() + "/qrobot/log/";
				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				path = path + "error.txt";
				File file = new File(path);
				if(!file.exists()) {
					file.createNewFile();
				}
				appendFile(path, content + "\r\n");
			}
		} catch (Exception e) {
			return false;// 保存失败
		}
		return true;// 保存成功
	}

	/**
	 * 文件追加
	 * @param fileName
	 * @param content
	 */
	public static void appendFile(String fileName, String content) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(fileName, true);
			writer.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取a-b间的随机数
	 * @return
	 */
	public static int getRandomNum(int a,int b){
		return (int) Math.round(Math.random()*(b-a)+a);
	}

	public static void fileChannelCopy(File s, File t) {
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(s);
            fo = new FileOutputStream(t);
            in = fi.getChannel();//得到对应的文件通道
            out = fo.getChannel();//得到对应的文件通道
            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fi.close();
                in.close();
                fo.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

	/**
	 * 创建文件夹
	 */
	public static void initFiles(){
		File file = new File(Environment.getExternalStorageDirectory(), "qrobot/data");
		if (!file.exists())
			file.mkdirs();
		file = new File(Environment.getExternalStorageDirectory(), "qrobot/images/upload");
		if (!file.exists())
			file.mkdirs();
		file = new File(Environment.getExternalStorageDirectory(), "qrobot/images/cache");
		if (!file.exists())
			file.mkdirs();
		file = new File(Environment.getExternalStorageDirectory(), "qrobot/download");
		if (!file.exists())
			file.mkdirs();
		file = new File(Environment.getExternalStorageDirectory(), "qrobot/voice");
		if (!file.exists())
			file.mkdirs();
	}

	  public static void Vibrate(Context context, long milliseconds) {
          Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
          vib.vibrate(milliseconds);
   }

	  @SuppressWarnings("deprecation")
	public static int getScreenWidth(Activity activity){
		  int width = 0;
		  WindowManager windowManager = activity.getWindowManager();
          Display display = windowManager.getDefaultDisplay();
          width=display.getWidth();
		  return width;
	  }

		/**
		 * 判断集合是否为空
		 * @param c
		 * @return
		 */
		public static boolean isEmpty(Collection<?> c){
	        if(c == null || c.size() == 0){
	            return true;
	        }else{
	            return false;
	        }
	    }

		/**
		 * 获取手机IMEI
		 * @param context
		 * @return
		 */
		public static String getDeviceIMEI(Context context) {
			String imei="";
		    try{
		      android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		      imei = tm.getDeviceId();
		    }catch(Exception e){
		      e.printStackTrace();
		    }
		    return imei;
		}

		/**
		 * 判断是否登录（用户名和密码是否为空）
		 * @param context
		 * @return
		 */
		public static boolean isLogin(Context context){
			boolean isLogin=false;
			if(!TextUtils.isEmpty(PreferencesUtils.getSharePreStr(context, Const.LOGIN_PWD))){
				isLogin=true;
			}
			return isLogin;
		}

		/**
		 * 无参数跳转
		 * @param activity
		 * @param cla
		 */
		public static <T> void startActivity(Activity activity,Class<T> cla){
			Intent intent=new Intent(activity, cla);
			activity.startActivity(intent);
			activity.overridePendingTransition(R.anim.push_in_right, R.anim.push_out_left);
		}

		/**
		 * 带参数跳转
		 * @param activity
		 * @param cla
		 * @param b  注意，接收Bundle的key为“b”
		 */
		public static <T> void startActivity(Activity activity,Class<T> cla,Bundle b){
			Intent intent=new Intent(activity, cla);
			if(b!=null){
				intent.putExtra("b", b);
			}
			activity.startActivity(intent);
			activity.overridePendingTransition(R.anim.push_in_right, R.anim.push_out_left);
		}

		/**
		 * 销毁Activity
		 * @param activity
		 */
		public static  void finish(Activity activity){
			activity.finish();
			activity.overridePendingTransition(R.anim.push_in_left, R.anim.push_out_right);
		}


		/**
		 * 获取相册图片路径
		 * @param context
		 * @param uri
		 * @return
		 */
		@SuppressLint("NewApi")
		public static String getPath(final Context context, final Uri uri) {

		    final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		    // DocumentProvider
		    if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
		        // ExternalStorageProvider
		        if (isExternalStorageDocument(uri)) {
		            final String docId = DocumentsContract.getDocumentId(uri);
		            final String[] split = docId.split(":");
		            final String type = split[0];

		            if ("primary".equalsIgnoreCase(type)) {
		                return Environment.getExternalStorageDirectory() + "/" + split[1];
		            }

		        }
		        // DownloadsProvider
		        else if (isDownloadsDocument(uri)) {
		            final String id = DocumentsContract.getDocumentId(uri);
		            final Uri contentUri = ContentUris.withAppendedId(
		                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
		            return getDataColumn(context, contentUri, null, null);
		        }
		        // MediaProvider
		        else if (isMediaDocument(uri)) {
		            final String docId = DocumentsContract.getDocumentId(uri);
		            final String[] split = docId.split(":");
		            final String type = split[0];

		            Uri contentUri = null;
		            if ("image".equals(type)) {
		                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		            } else if ("video".equals(type)) {
		                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
		            } else if ("audio".equals(type)) {
		                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		            }

		            final String selection = "_id=?";
		            final String[] selectionArgs = new String[] {split[1]};

		            return getDataColumn(context, contentUri, selection, selectionArgs);
		        }
		    }
		    // MediaStore (and general)
		    else if ("content".equalsIgnoreCase(uri.getScheme())) {

		        // Return the remote address
		        if (isGooglePhotosUri(uri))
		            return uri.getLastPathSegment();

		        return getDataColumn(context, uri, null, null);
		    }
		    // File
		    else if ("file".equalsIgnoreCase(uri.getScheme())) {
		        return uri.getPath();
		    }

		    return null;
		}

		/**
		 * Get the value of the data column for this Uri. This is useful for
		 * MediaStore Uris, and other file-based ContentProviders.
		 *
		 * @param context The context.
		 * @param uri The Uri to query.
		 * @param selection (Optional) Filter used in the query.
		 * @param selectionArgs (Optional) Selection arguments used in the query.
		 * @return The value of the _data column, which is typically a file path.
		 */
		public static String getDataColumn(Context context, Uri uri, String selection,
		        String[] selectionArgs) {

		    Cursor cursor = null;
		    final String column = "_data";
		    final String[] projection = {
		            column
		    };

		    try {
		        cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
		                null);
		        if (cursor != null && cursor.moveToFirst()) {
		            final int index = cursor.getColumnIndexOrThrow(column);
		            return cursor.getString(index);
		        }
		    } finally {
		        if (cursor != null)
		            cursor.close();
		    }
		    return null;
		}


		/**
		 * @param uri The Uri to check.
		 * @return Whether the Uri authority is ExternalStorageProvider.
		 */
		public static boolean isExternalStorageDocument(Uri uri) {
		    return "com.android.externalstorage.documents".equals(uri.getAuthority());
		}

		/**
		 * @param uri The Uri to check.
		 * @return Whether the Uri authority is DownloadsProvider.
		 */
		public static boolean isDownloadsDocument(Uri uri) {
		    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
		}

		/**
		 * @param uri The Uri to check.
		 * @return Whether the Uri authority is MediaProvider.
		 */
		public static boolean isMediaDocument(Uri uri) {
		    return "com.android.providers.media.documents".equals(uri.getAuthority());
		}

		/**
		 * @param uri The Uri to check.
		 * @return Whether the Uri authority is Google Photos.
		 */
		public static boolean isGooglePhotosUri(Uri uri) {
		    return "com.google.android.apps.photos.content".equals(uri.getAuthority());
		}

		/**
		 * 防止滑动Scrollview到顶部或底部时出现蓝边现象
		 * @param scrollView
		 */
		public static void setOverScrollMode(ScrollView scrollView){
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO)
				scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		}

		/**
		 * 防止滑动listView到顶部或底部时出现蓝边现象
		 * @param listView
		 */
		public static void setOverScrollMode(ListView listView){
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO)
				listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		}


		/**
		 * 状态栏高度算法
		 * @param activity
		 * @return
		 */
		public static int getStatusHeight(Activity activity){
		    int statusHeight = 0;
		    Rect localRect = new Rect();
		    activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
		    statusHeight = localRect.top;
		    if (0 == statusHeight){
		        Class<?> localClass;
		        try {
		            localClass = Class.forName("com.android.internal.R$dimen");
		            Object localObject = localClass.newInstance();
		            int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
		            statusHeight = activity.getResources().getDimensionPixelSize(i5);
		        } catch (ClassNotFoundException e) {
		            e.printStackTrace();
		        } catch (IllegalAccessException e) {
		            e.printStackTrace();
		        } catch (InstantiationException e) {
		            e.printStackTrace();
		        } catch (NumberFormatException e) {
		            e.printStackTrace();
		        } catch (IllegalArgumentException e) {
		            e.printStackTrace();
		        } catch (SecurityException e) {
		            e.printStackTrace();
		        } catch (NoSuchFieldException e) {
		            e.printStackTrace();
		        }
		    }
		    return statusHeight;
		}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

}
