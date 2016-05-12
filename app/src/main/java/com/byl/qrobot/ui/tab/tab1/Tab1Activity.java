package com.byl.qrobot.ui.tab.tab1;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.byl.qrobot.R;
import com.byl.qrobot.bean.Adv;
import com.byl.qrobot.bean.InfoItem;
import com.byl.qrobot.config.Const;
import com.byl.qrobot.util.PraseUtil;
import com.byl.qrobot.util.StringUtil;
import com.byl.qrobot.util.URLUtil;
import com.byl.qrobot.ui.base.BaseActivity;
import com.byl.qrobot.util.LogUtil;
import com.byl.qrobot.util.ToastUtil;
import com.byl.qrobot.view.CirclePageIndicator;
import com.byl.qrobot.view.HomeViewPaper;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 主程序
 */
public class Tab1Activity extends BaseActivity {

    LinearLayout ll_news, ll_loadmore;
    TextView tv_loadmore;
    ProgressBar pb_loadmore;

    CirclePageIndicator indicator;
    private List<View> imageViews; // 滑动的图片集合
    private HomeViewPaper vp;
    private MyAdapter myAdapter;
    private int currentItem = 0; // 当前图片的索引号
    private List<Adv> imageResUrl; // 图片ID
    private Timer adTimer = null;

    private LayoutInflater mInflater;

    FinalHttp fh;
    int page = 1;
    List<InfoItem> listInfoItem;

    // 切换当前显示的图片
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    vp.setCurrentItem(currentItem);// 切换当前显示的图片
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab1);
        initTitleBar("", "首页", "", null);
        fh = new FinalHttp();
        mInflater = LayoutInflater.from(this);
        initView();
        initData();
        initViewPager(imageResUrl);
        getNews(Const.TYPE_MOBILE, false);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        vp = (HomeViewPaper) findViewById(R.id.vp);
        indicator = (CirclePageIndicator) findViewById(R.id.indicator);//viewpaper指示器
        ll_news = (LinearLayout) findViewById(R.id.ll_news);
        ll_loadmore = (LinearLayout) findViewById(R.id.ll_loadmore);
        tv_loadmore = (TextView) findViewById(R.id.tv_loadmore);
        pb_loadmore = (ProgressBar) findViewById(R.id.pb_loadmore);
        ll_loadmore.setOnClickListener(this);
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.ll_loadmore:
                if (pb_loadmore.getVisibility()!= View.VISIBLE) {//如果正在加载更多则不执行
                    getNews(Const.TYPE_MOBILE, true);
                }
                break;
        }
    }

    /**
     * 初始化vp数据
     */
    private void initData() {
        imageResUrl = new ArrayList<>();
        Adv adv = new Adv();
        adv.setAdvimg("http://img0.imgtn.bdimg.com/it/u=1993054971,1265255301&fm=21&gp=0.jpg");
        imageResUrl.add(adv);

        adv = new Adv();
        adv.setAdvimg("http://img1.imgtn.bdimg.com/it/u=142510033,1541164236&fm=21&gp=0.jpg");
        adv.setAdvhref("http://blog.csdn.net/baiyuliang2013");
        imageResUrl.add(adv);

        adv = new Adv();
        adv.setAdvimg("http://img4.imgtn.bdimg.com/it/u=2339574921,1409672029&fm=21&gp=0.jpg");
        adv.setAdvhref("http://blog.csdn.net/baiyuliang2013");
        imageResUrl.add(adv);
    }

    /**
     * 初始化vp
     *
     * @param imageResUrl
     */
    private void initViewPager(List<Adv> imageResUrl) {
        imageViews = new ArrayList<>();
        // 初始化图片资源
        if (imageResUrl != null && imageResUrl.size() > 0) {
            for (int i = 0; i < imageResUrl.size(); i++) {
                final Adv adv = imageResUrl.get(i);
                View view = mInflater.inflate(R.layout.vp_ad_img, null);
                ImageView imageView = (ImageView) view.findViewById(R.id.img);
                finalImageLoader.display(imageView, adv.getAdvimg());
                //设置广告点击事件
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(adv.getAdvhref())) {
                            Uri uri = Uri.parse(adv.getAdvhref());
                            Intent it = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(it);
                        }
                    }
                });
                imageViews.add(view);
            }
        } else {
            View view = mInflater.inflate(R.layout.vp_ad_img, null);
            imageViews.add(view);
        }
        myAdapter = new MyAdapter();
        vp.setAdapter(myAdapter);// 设置填充ViewPager页面的适配器

        if (imageViews.size() > 1) {//只有当view数量>1时才显示指示器
            indicator.setViewPager(vp);
            indicator.setOnpageSelected(new CirclePageIndicator.OnpageSelected() {
                @Override
                public void pageSelected(int pos) {
                    currentItem = pos;
                }
            });
        }
    }

    /**
     * 获取网络数据
     */
    private void getNews(int infoType, final boolean isLoadMore) {
        LogUtil.e("url>>" + URLUtil.getUrl(infoType, page));
        fh.get(URLUtil.getUrl(infoType, page++), new AjaxCallBack<Object>() {
            @Override
            public void onStart() {
                super.onStart();
                if (!isLoadMore) {
                    pb.setVisibility(View.VISIBLE);//显示标题栏处的progress
                }else{
                    loadMoreStart();
                }
            }

            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);
                pb.setVisibility(View.GONE);
                LogUtil.e("获取数据成功");
                String result = (String) o;
                listInfoItem = PraseUtil.getInfosItems(Const.TYPE_MOBILE, result);
                if (!isLoadMore) {//首次加载/或刷新
                    ll_news.removeAllViews();
                    if (listInfoItem.size() <= 0) {
                        ToastUtil.showToast(Tab1Activity.this, "暂无数据");
                        ll_loadmore.setVisibility(View.GONE);
                    } else {
                        ll_loadmore.setVisibility(View.VISIBLE);
                    }
                } else {//加载更多
                    loadMoreEnd();
                    if (listInfoItem.size() <= 0) {
                        ToastUtil.showToast(Tab1Activity.this, "没有更多数据了");
                        ll_loadmore.setVisibility(View.GONE);
                    }
                }
                for (int i = 0; i < listInfoItem.size(); i++) {
                    ll_news.addView(getView(listInfoItem.get(i)));
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                pb.setVisibility(View.GONE);
                LogUtil.e("error>>" + strMsg);
                ToastUtil.showToast(Tab1Activity.this, "网络连接失败");

            }
        });
    }

    /**
     * 动态生成View
     *
     * @param infoItem
     * @return
     */
    View getView(final InfoItem infoItem) {
        View view = mInflater.inflate(R.layout.info_item_cell, null);
        TextView title = (TextView) view.findViewById(R.id.title);//标题
        TextView content = (TextView) view.findViewById(R.id.content);//内容
        TextView date = (TextView) view.findViewById(R.id.date);//日期
        ImageView image = (ImageView) view.findViewById(R.id.image);//图片

        title.setText(StringUtil.CN2EN(infoItem.getTitle()));
        content.setText(StringUtil.CN2EN(infoItem.getContent()));
        date.setText(infoItem.getDate());
        if (infoItem.getImgLink() != null) {
            image.setVisibility(View.VISIBLE);
            finalImageLoader.display(image, infoItem.getImgLink());
        } else {
            image.setVisibility(View.GONE);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(infoItem.getLink())) {
                    Uri uri = Uri.parse(infoItem.getLink());
                    Intent it = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(it);
                }
            }
        });

        return view;
    }

    /**
     * 加载更多开始
     */
    void loadMoreStart() {
        tv_loadmore.setVisibility(View.GONE);
        pb_loadmore.setVisibility(View.VISIBLE);
    }

    /**
     * 加载更多结束
     */
    void loadMoreEnd() {
        tv_loadmore.setVisibility(View.VISIBLE);
        pb_loadmore.setVisibility(View.GONE);
    }


    @Override
    protected void onStart() {
        super.onStart();
        currentItem = 0;
        adTimer = new Timer();
        adTimer.schedule(new ScrollTask(), 3 * 1000, 3 * 1000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adTimer != null) {
            adTimer.cancel();
            adTimer = null;
        }
    }

    /**
     * 换行切换任务
     *
     * @author byl
     */
    private class ScrollTask extends TimerTask {
        public void run() {
            if (vp.isTouchVp) return;
            if (imageViews != null && imageViews.size() > 0) {
                currentItem = (currentItem + 1) % imageViews.size();
                handler.sendEmptyMessage(1); // 通过Handler切换图片
            }
        }
    }

    /**
     * 填充ViewPager页面的适配器
     *
     * @author byl
     */
    private class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageViews.size();
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(imageViews.get(arg1));
            return imageViews.get(arg1);
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }

}
