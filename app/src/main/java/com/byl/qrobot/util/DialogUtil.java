package com.byl.qrobot.util;


import android.content.Context;
import android.text.TextUtils;
import android.view.View.OnClickListener;

import com.byl.qrobot.view.AlertDialog;

public class DialogUtil {

	/**
	 * 单按钮Dialog
	 * @param context
	 * @param msg 内容显示
	 */
	public static void showErrorMsg(Context context,String msg){
		try {
			new AlertDialog(context).builder()
					.setMsg(msg)
					.setCancelable(false)
					.setNegativeButton("确定", true,null).show();
		}catch (Exception e){

		}
	}

	/**
	 *单按钮Dialog
	 * @param context
	 * @param msg 内容显示
	 * @param btn 底部按钮内容
	 * @param isDismiss 点击按钮后是否隐藏Diaolg，true：隐藏，false：不隐藏
	 * @param onClickListener 按钮点击监听事件
	 */
	public static void showErrorMsgWithClick(Context context,String msg,String btn,boolean isDismiss,OnClickListener onClickListener){
		try {
			new AlertDialog(context).builder()
			.setMsg(msg)
			.setCancelable(false)
			.setNegativeButton(btn, isDismiss, onClickListener).show();
		}catch (Exception e){

		}
	}


	/**
	 *单按钮Dialog
	 * @param context
	 * @param title 标题
	 * @param msg 内容显示
	 * @param btn 底部按钮内容
	 * @param isDismiss 点击按钮后是否隐藏Diaolg，true：隐藏，false：不隐藏
	 * @param onClickListener 按钮点击监听事件
	 */
	public static void showErrorMsgWithClick(Context context,String title,String msg,String btn,boolean isDismiss,OnClickListener onClickListener){
		try {
			new AlertDialog(context).builder()
					.setTitle(title)
					.setMsg(msg)
					.setCancelable(false)
					.setNegativeButton(btn, isDismiss,onClickListener).show();
		}catch (Exception e){

		}
	}

	/**
	 *
	 * @param context
	 * @param title
	 * @param msg
	 * @param ok
	 * @param cancel
	 * @param onClickListener
	 */
	public static void showChooseDialog(Context context,String title,String msg,String ok,String cancel,OnClickListener onClickListener){
		try {
			if(TextUtils.isEmpty(ok)){
				ok="确定";
			}
			if(TextUtils.isEmpty(cancel)){
				cancel="取消";
			}
			if(TextUtils.isEmpty(title)){
				new AlertDialog(context).builder()
				.setMsg(msg)
				.setCancelable(false)
				.setPositiveButton(ok, onClickListener).setNegativeButton(cancel, true,null).show();
			}else{
				new AlertDialog(context).builder().setTitle(title)
				.setMsg(msg)
				.setCancelable(false)
				.setPositiveButton(ok, onClickListener).setNegativeButton(cancel, true,null).show();
			}
		}catch (Exception e){

		}
		
	}
	
	public static void showChooseDialog(Context context,String title,String msg,String ok,String cancel,OnClickListener onOkClickListener,OnClickListener onCancelClickListener){
		try {
			if(TextUtils.isEmpty(ok)){
				ok="确定";
			}
			if(TextUtils.isEmpty(cancel)){
				cancel="取消";
			}
			if(TextUtils.isEmpty(title)){
				new AlertDialog(context).builder()
				.setMsg(msg)
				.setCancelable(false)
				.setPositiveButton(ok, onOkClickListener).setNegativeButton(cancel, true,onCancelClickListener).show();
			}else{
				new AlertDialog(context).builder().setTitle(title)
				.setMsg(msg)
				.setCancelable(false)
				.setPositiveButton(ok, onOkClickListener).setNegativeButton(cancel, true,onCancelClickListener).show();
			}
		}catch (Exception e){

		}
	}

}
