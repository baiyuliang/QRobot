package com.byl.qrobot.ui.tab.tab3;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.byl.qrobot.R;
import com.byl.qrobot.db.ChatMsgDao;
import com.byl.qrobot.ui.base.SlideBackActivity;
import com.byl.qrobot.util.DialogUtil;
import com.byl.qrobot.util.ToastUtil;

/**
 * 聊天历史纪录
 * @author 白玉梁
 */
public class MsgHistoryActivity extends SlideBackActivity {
	private RelativeLayout rl_msg_history;
	private ChatMsgDao chatMsgDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_msg_history);
		chatMsgDao=new ChatMsgDao(this);
		initTitleBar("设置","聊天记录","",this);
		initView();
	}


	/**
	 * 
	 */
	private void initView() {
		rl_msg_history=(RelativeLayout) findViewById(R.id.rl_msg_history);//清空消息
		rl_msg_history.setOnClickListener(this);
	}


	@Override
	public void onClick(View arg0) {
		super.onClick(arg0);
		switch (arg0.getId()) {//返回
			case R.id.rl_msg_history://清空所有聊天记录
				DialogUtil.showChooseDialog(this,"提示","您确定要情况聊天记录吗？","立马清空","容朕想想", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						chatMsgDao.deleteTableData();
						ToastUtil.showToast(MsgHistoryActivity.this,"聊天记录已情况");
					}
				});
				break;
		}
	}
	

}
