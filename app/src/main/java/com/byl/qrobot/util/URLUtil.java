package com.byl.qrobot.util;

import com.byl.qrobot.config.Const;

public class URLUtil {

	public static String getUrl(int newsType, int currentPage) {
		currentPage = currentPage > 0 ? currentPage : 1;
		String urlStr = "";
		switch (newsType) {
		case Const.TYPE_NEWS:
			urlStr = Const.URL_NEWS;
			break;
		case Const.TYPE_MOBILE:
			urlStr = Const.URL_MOBILE;
			break;
		case Const.TYPE_SD:
			urlStr = Const.URL_SD;
			break;
		case Const.TYPE_PROGRAMMER:
			urlStr = Const.URL_PROGRAMMER;
			break;
		case Const.TYPE_CLOUD:
			urlStr = Const.URL_CLOUD;
			break;
		}

		urlStr += "/" + currentPage;

		return urlStr;

	}
}
