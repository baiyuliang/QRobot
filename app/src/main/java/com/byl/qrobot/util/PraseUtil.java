package com.byl.qrobot.util;

import com.byl.qrobot.bean.Answer;
import com.byl.qrobot.bean.Cook;
import com.byl.qrobot.bean.InfoItem;
import com.byl.qrobot.bean.Infos;
import com.byl.qrobot.bean.InfosDto;
import com.byl.qrobot.bean.News;

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

    /**
     * 解析机器人回复信息
     * 100000	文本类
     * 200000	链接类
     * 302000	新闻类
     * 308000	菜谱类
     * 313000（儿童版）	儿歌类
     * 314000（儿童版）	诗词类
     *
     * @param result
     * @return
     */
    public static Answer praseMsgText(String result) {
        Answer answer;
        JSONObject jsonObject;
        try {
            answer = new Answer();
            jsonObject = (JSONObject) new JSONTokener(result).nextValue();
            answer.setJsoninfo(result);
            answer.setCode(jsonObject.optString("code"));
            //因为本人的key使用的是上线项目《聊天小公主》中的key，机器人名称被本人设定为了“小公主”，因此需要替换一下（个人重新申请的key不用替换）
            answer.setText(jsonObject.optString("text").replace("小公主", "小Q"));
            switch (answer.getCode()) {
                case "40001"://参数key错误
                case "40002"://请求内容info为空
                case "40004"://当天请求次数已使用完
                case "40007"://数据格式异常
                case "100000":
                    //因text 字段 各类型都会返回，answer.setText已做处理，因此这里不做处理
                    break;
                case "200000":
                    answer.setUrl(jsonObject.optString("url"));
                    break;
                case "302000":
                    try {
                        JSONArray listArray = jsonObject.getJSONArray("list");
                        List<News> listNews=new ArrayList<>();
                        for (int i = 0; i < listArray.length(); i++) {
                            jsonObject = listArray.getJSONObject(i);
                            News news=new News();
                            news.setArticle(jsonObject.optString("article"));
                            news.setIcon(jsonObject.optString("icon"));
                            news.setSource(jsonObject.optString("source"));
                            news.setDetailurl(jsonObject.optString("detailurl"));
                            listNews.add(news);
                        }
                        answer.setListNews(listNews);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "308000":
                    try {
                        JSONArray listArray = jsonObject.getJSONArray("list");
                        List<Cook> listCook=new ArrayList<>();
                        for (int i = 0; i < listArray.length(); i++) {
                            jsonObject = listArray.getJSONObject(i);
                            Cook cook=new Cook();
                            cook.setName(jsonObject.optString("name"));
                            cook.setIcon(jsonObject.optString("icon"));
                            cook.setInfo(jsonObject.optString("info"));
                            cook.setDetailurl(jsonObject.optString("detailurl"));
                            listCook.add(cook);
                        }
                        answer.setListCook(listCook);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }

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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.toString();
    }

}
