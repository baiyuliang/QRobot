package com.byl.qrobot.bean;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 聊天信息表
 * @author 白玉梁
 */
@SuppressWarnings("serial")
public class Msg implements Serializable {

	private int msgId;//id
	private String fromUser;//发送者
	private String toUser;//接收者
	private String type;//信息类型
	private String content;//信息内容
	private String url;//图片或语音链接
	private int isComing;//0表接收的消息，1表发送的消息
	private String date;//时间
	private String isReaded;//是否已读
	private String jsoninfo;//
	
	private String bak1;//扩展1 //音乐是否正在播放 0未播放 1正在播放
	private String bak2;//扩展2
	private String bak3;//扩展3
	private String bak4;//扩展4
	private String bak5;//扩展5
	private String bak6;//扩展6
	
	
	public int getMsgId() {
		return msgId;
	}
	public void setMsgId(int msgId) {
		this.msgId = msgId;
	}
	public String getFromUser() {
		return fromUser;
	}
	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}
	public String getToUser() {
		return toUser;
	}
	public void setToUser(String toUser) {
		this.toUser = toUser;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public int getIsComing() {
		return isComing;
	}
	public void setIsComing(int isComing) {
		this.isComing = isComing;
	}
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getBak1() {
		return bak1;
	}
	public void setBak1(String bak1) {
		this.bak1 = bak1;
	}
	public String getBak2() {
		return bak2;
	}
	public void setBak2(String bak2) {
		this.bak2 = bak2;
	}
	public String getBak3() {
		return bak3;
	}
	public void setBak3(String bak3) {
		this.bak3 = bak3;
	}
	public String getBak4() {
		return bak4;
	}
	public void setBak4(String bak4) {
		this.bak4 = bak4;
	}
	public String getBak5() {
		return bak5;
	}
	public void setBak5(String bak5) {
		this.bak5 = bak5;
	}
	public String getBak6() {
		return bak6;
	}
	public void setBak6(String bak6) {
		this.bak6 = bak6;
	}
	public String getIsReaded() {
		return isReaded;
	}
	public void setIsReaded(String isReaded) {
		this.isReaded = isReaded;
	}

	public String getJsoninfo() {
		return jsoninfo;
	}

	public void setJsoninfo(String jsoninfo) {
		this.jsoninfo = jsoninfo;
	}
}
