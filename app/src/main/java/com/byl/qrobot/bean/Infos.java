package com.byl.qrobot.bean;

/**
 * ��Ѷ��������
 * 
 * @author white
 */
public class Infos {
	private String title;// ����
	private String summary;// ��Ҫ
	private String content;// ����
	private String imageLink;// ͼƬ��ַ
	private int type;// ����

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSummary() {
		return this.summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
		this.type = 2;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getImageLink() {
		return this.imageLink;
	}

	public void setImageLink(String imageLink) {
		this.imageLink = imageLink;
		this.type = 4;
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String toString() {
		return "Infos [title=" + this.title + ", summary=" + this.summary + ", content=" + this.content + ", imageLink=" + this.imageLink + ", type=" + this.type + "]";
	}

}
