package com.missmess.messui.builtin;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.missmess.messui.ILayoutFactory;
import com.missmess.messui.R;
import com.missmess.messui.widget.TitleView;

/**
 * Default title bar factory: contains a {@link TitleView}, WebPage loading indicator, hint bar.
 */
public class DefaultTitleBarFactory implements ILayoutFactory.ITitleBarFactory<TitleView> {
    public static final int DEFAULT_HINT_SHOW_TIME = 3000; //ms
    private Context mContext;
    private TitleView titleView;
    private View hint_bar;
    private TextView hint_bar_text;
    private View hint_bar_close;
    private ProgressBar web_page_indicator;
    private Animation out_anim;
    private Animation in_anim;
    private Handler handler = new Handler();
    private boolean overlayEnabled = false;

    public DefaultTitleBarFactory(Context context, boolean overlayEnabled) {
        this.mContext = context;
        this.overlayEnabled = overlayEnabled;
    }

    protected
    @LayoutRes
    int titleBarResId() {
        return R.layout.view_default_title;
    }

    @Override
    public final TitleView createTitleBar(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        titleView = (TitleView) inflater.inflate(titleBarResId(), parent, false);
        return titleView;
    }

    /**
     * Get TitleView, may be null if LayoutType doesn't contains T.
     *
     * @return TitleView
     */
    public TitleView getTitleView() {
        return titleView;
    }

    protected
    @LayoutRes
    int layoutBelowTitleResId() {
        return R.layout.view_default_below_title;
    }

    @Override
    public View createLayoutBelowTitle(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View viewBelowTitle = inflater.inflate(layoutBelowTitleResId(), parent, false);
        hint_bar = viewBelowTitle.findViewById(R.id.hint_bar);
        hint_bar_text = (TextView) viewBelowTitle.findViewById(R.id.hint_bar_text);
        hint_bar_close = viewBelowTitle.findViewById(R.id.hint_bar_close);
        web_page_indicator = (ProgressBar) viewBelowTitle.findViewById(R.id.web_page_indicator);

        hint_bar_text.setVisibility(View.GONE);
        hint_bar_close.setVisibility(View.GONE);
        hint_bar_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePersistedHintBar();
            }
        });
        web_page_indicator.setProgress(0);
        return viewBelowTitle;
    }

    @Override
    public boolean titleViewOverlayContent() {
        return overlayEnabled;
    }

    /**
     * Return a ProgressBar indicates the loading process of a web page, may be null if
     * LayoutType doesn't contains T.
     *
     * @return ProgressBar
     */
    public ProgressBar getWebPageLoadIndicator() {
        return web_page_indicator;
    }

    /**
     * Show persisted hint bar at top of content and below of title. Normally use for notifications.
     *
     * @param msg         msg
     * @param clickHintOp hint bar clicked
     */
    public void showPersistedHintBar(String msg, View.OnClickListener clickHintOp) {
        if (hint_bar == null)
            return;
        checkHintDisposable();

        hint_bar_text.setVisibility(View.VISIBLE);
        hint_bar_close.setVisibility(View.VISIBLE);
        hint_bar_text.setGravity(Gravity.START);
        hint_bar_text.setOnClickListener(clickHintOp);

        hint_bar_text.setText(msg);
        doHintBarShow();
    }

    /**
     * Hide persisted hint bar.
     */
    public void hidePersistedHintBar() {
        if (hint_bar == null)
            return;
        checkHintDisposable();
        doHintBarHide();
    }

    /**
     * Display a message and dismiss after a default delay.
     *
     * @param msg msg
     */
    public void showFastHintBar(String msg) {
        showFastHintBar(msg, DEFAULT_HINT_SHOW_TIME);
    }

    /**
     * Display a message and dismiss after a specified delay.
     *
     * @param msg   msg
     * @param delay delay
     */
    public void showFastHintBar(String msg, int delay) {
        if (hint_bar == null)
            return;
        checkHintDisposable();

        hint_bar_text.setVisibility(View.VISIBLE);
        hint_bar_close.setVisibility(View.GONE);
        hint_bar_text.setGravity(Gravity.CENTER);
        hint_bar_text.setOnClickListener(null);

        hint_bar_text.setText(msg);
        doHintBarShow();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                doHintBarHide();
            }
        }, delay);
    }

    private void checkHintDisposable() {
        handler.removeCallbacksAndMessages(null);
    }

    private void doHintBarShow() {
        if (in_anim == null)
            in_anim = AnimationUtils.loadAnimation(mContext, R.anim.slide_down_in);
        hint_bar.startAnimation(in_anim);
    }

    private void doHintBarHide() {
        if (out_anim == null)
            out_anim = AnimationUtils.loadAnimation(mContext, R.anim.slide_top_out);
        hint_bar.startAnimation(out_anim);
    }
}
