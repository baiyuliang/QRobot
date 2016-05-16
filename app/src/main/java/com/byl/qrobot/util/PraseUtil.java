package com.byl.qrobot.util;

import com.byl.qrobot.bean.Answer;
import com.byl.qrobot.bean.InfoItem;
import com.byl.qrobot.bean.Infos;
import com.byl.qrobot.bean.InfosDto;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理InfoItem的业务类
 */
public class PraseUtil {
    public static List<InfoItem> getInfosItems(int infosType, String htmlStr) {
        List<InfoItem> infosItems = new ArrayList<>();

        InfoItem infosItem = null;

        Document doc = Jsoup.parse(htmlStr);// 解析html数据
        Elements units = doc.getElementsByClass("unit");
        for (int i = 0; i < units.size(); i++) {
            infosItem = new InfoItem();
            infosItem.setInfoType(infosType);

            Element unit_ele = units.get(i);

            Element h1_ele = unit_ele.getElementsByTag("h1").get(0);
            Element h1_a_ele = h1_ele.child(0);

            String title = StringUtil.ToDBC(h1_a_ele.text());
            String href = h1_a_ele.attr("href");

            infosItem.setLink(href);
            infosItem.setTitle(title);

            Element h4_ele = unit_ele.getElementsByTag("h4").get(0);
            Element ago_ele = h4_ele.getElementsByClass("ago").get(0);
            String date = ago_ele.text();

            infosItem.setDate(date);

            Element dl_ele = unit_ele.getElementsByTag("dl").get(0);
            Element dt_ele = dl_ele.child(0);

            try {
                Element img_ele = dt_ele.child(0);
                String imgLink = img_ele.child(0).attr("src");
                infosItem.setImgLink(imgLink);
            } catch (IndexOutOfBoundsException localIndexOutOfBoundsException) {

            }

            Element content_ele = dl_ele.child(1);
            String content = StringUtil.ToDBC(content_ele.text());
            infosItem.setContent(content);
            infosItems.add(infosItem);
        }
        return infosItems;
    }

    public InfosDto getInfos(String htmlStr) {
        InfosDto infosDto = new InfosDto();
        List<Infos> infosList = new ArrayList<>();
        Document doc = Jsoup.parse(htmlStr);

        Element detailEle = doc.select(".left .detail").get(0);

        Element titleEle = detailEle.select("h1.title").get(0);
        Infos infos = new Infos();
        infos.setTitle(titleEle.text());
        infos.setType(1);
        infosList.add(infos);

        Element summaryEle = detailEle.select("div.summary").get(0);
        infos = new Infos();
        infos.setSummary(summaryEle.text());
        infosList.add(infos);

        Element contentEle = detailEle.select("div.con.news_content").get(0);
        Elements childrenEle = contentEle.children();

        for (Element child : childrenEle) {
            Elements imgEles = child.getElementsByTag("img");

            if (imgEles.size() > 0) {
                for (Element imgEle : imgEles) {
                    if (imgEle.attr("src").equals(""))
                        continue;
                    infos = new Infos();
                    infos.setImageLink(imgEle.attr("src"));
                    infosList.add(infos);
                }
            }

            imgEles.remove();

            if (child.text().equals("")) {
                continue;
            }
            infos = new Infos();
            infos.setType(3);
            try {
                if (child.children().size() == 1) {
                    Element cc = child.child(0);
                    if (cc.tagName().equals("b")) {
                        infos.setType(5);
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            infos.setContent(child.outerHtml());
            infosList.add(infos);
        }
        infosDto.setInfoList(infosList);
        return infosDto;
    }

    public static Answer praseMsgText(String result) {
        Answer answer;
        JSONObject jsonObject;
        try {
            answer = new Answer();
            jsonObject = (JSONObject) new JSONTokener(result).nextValue();
            answer.setCode(jsonObject.optString("code"));
            //因为本人的key使用的是上线项目《聊天小公主》中的key，机器人名称被本人设定为了“小公主”，因此需要替换一下（个人重新申请的key不用替换）
            answer.setText(jsonObject.optString("text").replace("小公主","小Q"));
            answer.setUrl(jsonObject.optString("url"));
        } catch (Exception e) {
            return null;
        }
        return answer;
    }

    public static String parseIatResult(String json) {
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);

            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                // 转写结果词，默认使用第一个结果
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                JSONObject obj = items.getJSONObject(0);
                ret.append(obj.getString("w"));
//				如果需要多候选结果，解析数组其他字段
//				for(int j = 0; j < items.length(); j++)
//				{
//					JSONObject obj = items.getJSONObject(j);
//					ret.append(obj.getString("w"));
//				}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.toString();
    }

}
