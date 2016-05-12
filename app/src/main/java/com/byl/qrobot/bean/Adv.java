package com.byl.qrobot.bean;

/**
 * 首页banner
 * @author 白玉梁
 * @date 2015-8-4 下午1:49:29
 */
public class Adv {
	
	private String id;
	private String advimg;//图片地址
	private String advhref;//点击跳转地址
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAdvimg() {
		return advimg;
	}
	public String getAdvhref() {
		return advhref;
	}
	public void setAdvimg(String advimg) {
		this.advimg = advimg;
	}
	public void setAdvhref(String advhref) {
		this.advhref = advhref;
	}
	
	
	

}
