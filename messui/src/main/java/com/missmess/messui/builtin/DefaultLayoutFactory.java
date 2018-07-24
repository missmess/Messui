package com.missmess.messui.builtin;

import android.content.Context;
import android.support.annotation.IdRes;

import com.missmess.messui.ILayoutFactory;


/**
 * Default layout factory use {@link DefaultTitleBarFactory} and {@link DefaultLoadViewFactory}.
 *
 * @author wl
 * @since 2017/07/13 17:39
 */
public final class DefaultLayoutFactory implements ILayoutFactory<DefaultTitleBarFactory, DefaultLoadViewFactory, DefaultRefreshLayoutFactory> {
    private Context mContext;
    private DefaultTitleBarFactory titleBarFactory;
    private DefaultLoadViewFactory loadViewFactory;
    private DefaultRefreshLayoutFactory refreshLayoutFactory;
    private boolean loadViewMade = false;
    private boolean titlebarMade = false;
    private boolean refreshLayoutMade = false;

    private boolean overlayEnable = false;
    private int loadViewAnchor = 0;
    private int refreshLayoutAnchor = 0;

    public DefaultLayoutFactory(Context context) {
        this.mContext = context;
    }

    public void setTitleBarOverlay() {
        if(titlebarMade)
            throwCallOnWrongTimingException();
        this.overlayEnable = true;
    }

    public void setLoadViewAnchor(@IdRes int anchor) {
        if(loadViewMade)
            throwCallOnWrongTimingException();
        this.loadViewAnchor = anchor;
    }

    public void setRefreshLayoutAnchor(@IdRes int anchor) {
        if(refreshLayoutMade)
            throwCallOnWrongTimingException();
        this.refreshLayoutAnchor = anchor;
    }

    private void throwCallOnWrongTimingException() {
        String callerMethodName = Thread.currentThread().getStackTrace()[3].getMethodName();
        throw new IllegalStateException(callerMethodName + " can only be called before its relative factory has been made");
    }

    @Override
    public DefaultTitleBarFactory madeTitleBarFactory() {
        titleBarFactory = new DefaultTitleBarFactory(mContext, overlayEnable);
        return titleBarFactory;
    }

    @Override
    public DefaultLoadViewFactory madeLoadViewFactory() {
        loadViewFactory = new DefaultLoadViewFactory(mContext, loadViewAnchor);
        return loadViewFactory;
    }

    @Override
    public DefaultRefreshLayoutFactory madeRefreshLayoutFactory() {
        refreshLayoutFactory = new DefaultRefreshLayoutFactory(mContext, refreshLayoutAnchor);
        return refreshLayoutFactory;
    }

    @Override
    public DefaultTitleBarFactory getTitleBarFactory() {
        return titleBarFactory;
    }

    @Override
    public DefaultLoadViewFactory getLoadViewFactory() {
        return loadViewFactory;
    }

    @Override
    public DefaultRefreshLayoutFactory getRefreshLayoutFactory() {
        return refreshLayoutFactory;
    }

}
