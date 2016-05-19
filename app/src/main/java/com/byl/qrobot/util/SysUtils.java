package com.byl.qrobot.util;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.ScrollView;

import com.byl.qrobot.R;
import com.byl.qrobot.config.Const;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
     * 移动文件
     * @param oldPath
     * @param newPath
     * @return
     */
    public static boolean copyFile(String oldPath, String newPath) {
        File oldFile = new File(oldPath);
        if (!oldFile.exists()) {
            return false;
        }
        if(oldFile.renameTo(new File(newPath))){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 删除指定目录下文件及目录
     *
     * @return
     */
    public static void deleteFolderFile(String filePath) {
        try {
            if (!TextUtils.isEmpty(filePath)) {
                File file = new File(filePath);
                if (file.isDirectory()) {//如果该文件是文件夹，怎删除该文件夹内的内容
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        if (files[i].getAbsolutePath().endsWith(".png")
                                || files[i].getAbsolutePath().endsWith(".amr")
                                || files[i].getAbsolutePath().endsWith(".wav")) {
                            deleteFolderFile(files[i].getAbsolutePath());
                        }
                    }
                } else {//否则删除该文件
                    file.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("删除文件异常>>" + e.getMessage());
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
            return "小Q聊天机器人";
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
     * 创建文件夹
     */
    public static void initFiles() {
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

    /**
     * 判断集合是否为空
     *
     * @param c
     * @return
     */
    public static boolean isEmpty(Collection<?> c) {
        if (c == null || c.size() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断是否登录（用户名和密码是否为空）
     *
     * @param context
     * @return
     */
    public static boolean isLogin(Context context) {
        boolean isLogin = false;
        if (!TextUtils.isEmpty(PreferencesUtils.getSharePreStr(context, Const.LOGIN_PWD))) {
            isLogin = true;
        }
        return isLogin;
    }

    /**
     * 无参数跳转
     *
     * @param activity
     * @param cla
     */
    public static <T> void startActivity(Activity activity, Class<T> cla) {
        Intent intent = new Intent(activity, cla);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.push_in_right, R.anim.push_out_left);
    }

    /**
     * 带参数跳转
     *
     * @param activity
     * @param cla
     * @param b        注意，接收Bundle的key为“b”
     */
    public static <T> void startActivity(Activity activity, Class<T> cla, Bundle b) {
        Intent intent = new Intent(activity, cla);
        if (b != null) {
            intent.putExtra("b", b);
        }
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.push_in_right, R.anim.push_out_left);
    }

    /**
     * 销毁Activity
     *
     * @param activity
     */
    public static void finish(Activity activity) {
        activity.finish();
        activity.overridePendingTransition(R.anim.push_in_left, R.anim.push_out_right);
    }


    /**
     * 获取相册图片路径
     *
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
                final String[] selectionArgs = new String[]{split[1]};

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
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
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
     *
     * @param scrollView
     */
    public static void setOverScrollMode(ScrollView scrollView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO)
            scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    /**
     * 防止滑动listView到顶部或底部时出现蓝边现象
     *
     * @param listView
     */
    public static void setOverScrollMode(ListView listView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO)
            listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }


    /**
     * 状态栏高度算法
     *
     * @param activity
     * @return
     */
    public static int getStatusHeight(Activity activity) {
        int statusHeight = 0;
        Rect localRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = activity.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

}
