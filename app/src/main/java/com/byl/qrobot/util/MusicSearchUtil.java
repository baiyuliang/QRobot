package com.byl.qrobot.util;

/**
 * Created by baiyuliang on 2016-5-13.
 */
import com.byl.qrobot.bean.Music;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 百度音乐搜索API操作类
 */
public class MusicSearchUtil {
    /**
     * 根据名称和作者搜索音乐(注意此方法阻塞线程，调用时放在线程中)
     *
     * @param musicTitle 音乐名称
     * @param musicAuthor 音乐作者
     * @return Music
     */
    public static Music searchMusic(String musicTitle, String musicAuthor) {
        // 百度音乐搜索地址
        String requestUrl = "http://box.zhangmen.baidu.com/x?op=12&count=1&title={TITLE}$${AUTHOR}$$$$";
        // 对音乐名称、作者进URL编码
        requestUrl = requestUrl.replace("{TITLE}", urlEncodeUTF8(musicTitle));
        requestUrl = requestUrl.replace("{AUTHOR}", urlEncodeUTF8(musicAuthor));
        // 处理名称、作者中间的空格
        requestUrl = requestUrl.replaceAll("\\+", "%20");

        // 查询并获取返回结果
        InputStream inputStream = httpRequest(requestUrl);
        // 从返回结果中解析出Music
        Music music = parseMusic(inputStream);

        // 如果music不为null，设置标题和描述
        if (null != music) {
            music.setTitle(musicTitle);
            // 如果作者不为""，将描述设置为作者
            if (!"".equals(musicAuthor))
                music.setDescription(musicAuthor);
            else
                music.setDescription("来自百度音乐");
        }
        return music;
    }

    /**
     * UTF-8编码
     *
     * @param source
     * @return
     */
    private static String urlEncodeUTF8(String source) {
        String result = source;
        try {
            result = java.net.URLEncoder.encode(source, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 发送http请求取得返回的输入流
     *
     * @param requestUrl 请求地址
     * @return InputStream
     */
    private static InputStream httpRequest(String requestUrl) {
        InputStream inputStream = null;
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
            httpUrlConn.setDoInput(true);
            httpUrlConn.setRequestMethod("GET");
            httpUrlConn.connect();
            // 获得返回的输入流
            inputStream = httpUrlConn.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    /**
     * 解析音乐参数
     *
     * @param inputStream 百度音乐搜索API返回的输入流
     * @return Music
     */
    @SuppressWarnings("unchecked")
    private static Music parseMusic(InputStream inputStream) {
        Music music = null;
        try {
            // 使用dom4j解析xml字符串
            SAXReader reader = new SAXReader();
            Document document = reader.read(inputStream);
            // 得到xml根元素
            Element root = document.getRootElement();
            // count表示搜索到的歌曲数
            String count = root.element("count").getText();
            // 当搜索到的歌曲数大于0时
            if (!"0".equals(count)) {
                // 普通品质
                List<Element> urlList = root.elements("url");
                // 高品质
                List<Element> durlList = root.elements("durl");

                // 普通品质的encode、decode
                String urlEncode = urlList.get(0).element("encode").getText();
                String urlDecode = urlList.get(0).element("decode").getText();
                // 普通品质音乐的URL
                String url = urlEncode.substring(0, urlEncode.lastIndexOf("/") + 1) + urlDecode;
                if (-1 != urlDecode.lastIndexOf("&"))
                    url = urlEncode.substring(0, urlEncode.lastIndexOf("/") + 1) + urlDecode.substring(0, urlDecode.lastIndexOf("&"));

                // 默认情况下，高音质音乐的URL 等于 普通品质音乐的URL
                String durl = url;

                // 判断高品质节点是否存在
                Element durlElement = durlList.get(0).element("encode");
                if (null != durlElement) {
                    // 高品质的encode、decode
                    String durlEncode = durlList.get(0).element("encode").getText();
                    String durlDecode = durlList.get(0).element("decode").getText();
                    // 高品质音乐的URL
                    durl = durlEncode.substring(0, durlEncode.lastIndexOf("/") + 1) + durlDecode;
                    if (-1 != durlDecode.lastIndexOf("&"))
                        durl = durlEncode.substring(0, durlEncode.lastIndexOf("/") + 1) + durlDecode.substring(0, durlDecode.lastIndexOf("&"));
                }
                music = new Music();
                // 设置普通品质音乐链接
                music.setMusicUrl(url);
                // 设置高品质音乐链接
                music.setHQMusicUrl(durl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return music;
    }
}