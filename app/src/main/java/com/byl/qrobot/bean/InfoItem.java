package com.byl.qrobot.bean;

public class InfoItem {
	private int id;// 唯一标识
	private String title; // 标题
	private String link; // 链接
	private String date; // 发布日期
	private String imgLink;// 图片的链接
	private String content;// 内容
	private int infoType;// 类型

	public int getInfoType() {
		return infoType;
	}

	public void setInfoType(int newsType) {
		this.infoType = newsType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getImgLink() {
		return imgLink;
	}

	public void setImgLink(String imgLink) {
		this.imgLink = imgLink;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "InfoItem [id=" + id + ", title=" + title + ", link=" + link + ", date=" + date + ", imgLink=" + imgLink + ", content=" + content + ", infoType=" + infoType + "]";
	}
}
