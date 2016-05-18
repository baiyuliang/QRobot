package com.byl.qrobot.view;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class ProgressWebView extends WebView {

    //    private ProgressBar progressbar;
    OnWebTitleChangedListener onWebTitleChangedListener;
    OnLoadingListener onLoadingListener;
    OnScrollChangedListener onScrollChangedListener;

    @SuppressWarnings("deprecation")
    public ProgressWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWebChromeClient(new WebChromeClient());
    }

    public class WebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                if (onLoadingListener != null) {
                    onLoadingListener.onFinish();
                }
            } else {
                if (onLoadingListener != null) {
                    onLoadingListener.onStart();
                }
            }
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            onWebTitleChangedListener.OnWebTitleChangedFinish(title);
        }

    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (onScrollChangedListener != null) {
            onScrollChangedListener.onScrollChanged(l, t);
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public interface OnWebTitleChangedListener {
        public void OnWebTitleChangedFinish(String title);
    }


    public void setOnWebTitleChangedListener(
            OnWebTitleChangedListener onWebTitleChangedListener) {
        this.onWebTitleChangedListener = onWebTitleChangedListener;
    }

    public interface OnLoadingListener {
        public void onStart();

        public void onFinish();
    }

    public void setOnLoadingListener(OnLoadingListener onLoadingListener) {
        this.onLoadingListener = onLoadingListener;
    }

    public interface OnScrollChangedListener {
        void onScrollChanged(int x, int y);
    }

    public void setOnScrollChangedListener(OnScrollChangedListener onScrollChangedListener) {
        this.onScrollChangedListener = onScrollChangedListener;
    }
}
