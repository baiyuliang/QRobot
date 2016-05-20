package com.byl.qrobot.bean;

/**
 * 菜谱信息
 * name	菜名
 * info	菜谱信息
 * detailurl	详情链接
 * icon	信息图标
 * Created by baiyuliang on 2016-5-20.
 */
public class Cook {

    private String name;
    private String info;
    private String icon;
    private String detailurl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDetailurl() {
        return detailurl;
    }

    public void setDetailurl(String detailurl) {
        this.detailurl = detailurl;
    }
}
