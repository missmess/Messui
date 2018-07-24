package com.missmess.messui;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.ViewAnimator;

import com.missmess.messui.builtin.DefaultLayoutFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Helper class to create and add base layout on the original content view.
 *
 * @author wl
 * @since 2016/05/04 16:43
 */
public final class LayoutHelper {

    private View titleView;

    @IntDef(flag = true, value = {TYPE_NONE, TYPE_T, TYPE_L, TYPE_R})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LayoutType {}

    /**
     * no any decoration view.
     */
    public static final int TYPE_NONE = 0;
    /**
     * wrap original view with a title bar.
     */
    public static final int TYPE_T = 0x0001;
    /**
     * wrap original view with load-views.
     */
    public static final int TYPE_L = 0x0001 << 1;
    /**
     * wrap original view with refresh layout.
     */
    public static final int TYPE_R = 0x0001 << 2;

    private final Context mContext;
    private ViewState mState = ViewState.Initial;
    private ViewAnimator va_container;
    private ViewGroup srl_layout;
    @LayoutType private int mType;
    private int DEFAULT_LOADING_DELAY = 300;
    private final Handler handler = new Handler();
    private ILayoutFactory layoutFactory;
    private ILayoutFactory.ILoadViewFactory loadViewFactory;
    private ILayoutFactory.ITitleBarFactory titleBarFactory;
    private ILayoutFactory.IRefreshLayoutFactory refreshLayoutFactory;

    /**
     * Create a LayoutHelper use a {@link DefaultLayoutFactory}.
     *
     * @param context context used to inflate layout.
     * @param type    be one of {@link #TYPE_NONE}, {@link #TYPE_T}, {@link #TYPE_L}, {@link LayoutHelper#TYPE_R}, or combined flags.
     */
    public LayoutHelper(Context context, @LayoutType int type) {
        this(context, type, new DefaultLayoutFactory(context));
    }

    /**
     * Create a LayoutHelper use a specified ILayoutFactory.
     *
     * @param context context used to inflate layout.
     * @param type    be one of {@link #TYPE_NONE}, {@link #TYPE_T}, {@link #TYPE_L}, {@link LayoutHelper#TYPE_R}, or combined flags.
     * @param factory ILayoutFactory
     */
    public LayoutHelper(Context context, @LayoutType int type, @NonNull ILayoutFactory factory) {
        this.mContext = context;
        this.mType = type;
        this.layoutFactory = factory;
    }

    /**
     * Wrap the original layout with decorator views and create the new wrapped layout.
     * <b>Look out the LayoutParams.</b>
     *
     * @param contentView original layout
     * @return enhanced new layout
     */
    public View createView(@Nullable View contentView) {
        int type = mType;
        View wrappedLayout = null;
        if (contentView != null) {
            // 1. if require refresh layout, wrap content
            if ((type & TYPE_R) != 0) {
                refreshLayoutFactory = layoutFactory.madeRefreshLayoutFactory();
                contentView = wrapContentWithR(contentView);
            }
            // 2. if require load-views, wrap content
            if ((type & TYPE_L) != 0) {
                loadViewFactory = layoutFactory.madeLoadViewFactory();
                contentView = wrapContentWithL(contentView);
            }
            // 3. finally, we fill in title view if needed.
            if ((type & TYPE_T) != 0) { // contains title bar
                titleBarFactory = layoutFactory.madeTitleBarFactory();
                if (titleBarFactory == null)
                    throw new IllegalStateException("layout want a title but no ITitleBarFactory provided");

                wrappedLayout = View.inflate(mContext, R.layout.base_title_layout, null);
                wrappedLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                // title
                titleView = titleBarFactory.createTitleBar((ViewGroup) wrappedLayout);
                if (titleView.getId() == View.NO_ID) {
                    titleView.setId(R.id.title);
                }
                ((ViewGroup) wrappedLayout).addView(titleView);
                // below title LayoutParams
                RelativeLayout.LayoutParams belowLps = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT);
                belowLps.addRule(RelativeLayout.BELOW, titleView.getId());
                // view below title
                View viewBelowTitle = titleBarFactory.createLayoutBelowTitle((ViewGroup) wrappedLayout);
                if (viewBelowTitle != null) {
                    ((ViewGroup) wrappedLayout).addView(viewBelowTitle, belowLps);
                }
                if (titleBarFactory.titleViewOverlayContent()) {
                    ((ViewGroup) wrappedLayout).addView(contentView, 0);
                } else {
                    // content view
                    ((ViewGroup) wrappedLayout).addView(contentView, 0, belowLps);
                }
            } else {
                wrappedLayout = contentView;
            }
        }
        return wrappedLayout;
    }

    private View wrapContentWithL(@NonNull View contentView) {
        if (loadViewFactory == null)
            throw new IllegalStateException("layout want load-views but no ILoadViewFactory provided");

        va_container = (ViewAnimator) View.inflate(mContext, R.layout.base_load_layout, null);
        va_container.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        int anchorId = loadViewFactory.anchorView();
        View anchorView = null;
        if (anchorId != 0) {
            anchorView = contentView.findViewById(anchorId);
        }
        if (anchorView == null || anchorView.getParent() == null) {
            // orders of view adding should be the same with ViewState class
            va_container.addView(contentView);
            va_container.addView(loadViewFactory.createNoDataLayout(va_container));
            va_container.addView(loadViewFactory.createLoadingLayout(va_container));
            va_container.addView(loadViewFactory.createLoadFailLayout(va_container));
            return va_container;
        } else {
            // add anchor view as content and use va_container replace the position
            // of anchor view
            ViewGroup parent = (ViewGroup) anchorView.getParent();
            if (parent == srl_layout) {
                // the anchor view has just been added to refreshLayout, so we should wrap the
                // refreshLayout then.
                anchorView = srl_layout;
                parent = (ViewGroup) srl_layout.getParent();
            }
            if (parent != null) {
                int index = parent.indexOfChild(anchorView);
                ViewGroup.LayoutParams lps = anchorView.getLayoutParams();
                parent.removeView(anchorView);
                va_container.addView(anchorView);
                va_container.addView(loadViewFactory.createNoDataLayout(va_container));
                va_container.addView(loadViewFactory.createLoadingLayout(va_container));
                va_container.addView(loadViewFactory.createLoadFailLayout(va_container));
                parent.addView(va_container, index, lps);
                return contentView;
            } else {
                va_container.addView(anchorView);
                va_container.addView(loadViewFactory.createNoDataLayout(va_container));
                va_container.addView(loadViewFactory.createLoadingLayout(va_container));
                va_container.addView(loadViewFactory.createLoadFailLayout(va_container));
                return va_container;
            }
        }
    }

    private View wrapContentWithR(@NonNull View contentView) {
        if (refreshLayoutFactory == null)
            throw new IllegalStateException("layout want refresh layout but no IRefreshLayoutFactory provided");

        int anchorId = refreshLayoutFactory.anchorView();
        View anchorView = null;
        if (anchorId != 0) {
            anchorView = contentView.findViewById(anchorId);
        }
        if (anchorView == null || anchorView.getParent() == null) {
            srl_layout = refreshLayoutFactory.createRefreshLayout(contentView);
            return srl_layout;
        } else {
            // let srl_layout replace the position of anchorView
            ViewGroup parent = (ViewGroup) anchorView.getParent();
            int index = parent.indexOfChild(anchorView);
            ViewGroup.LayoutParams lps = anchorView.getLayoutParams();
            parent.removeView(anchorView);
            srl_layout = refreshLayoutFactory.createRefreshLayout(anchorView);
            parent.addView(srl_layout, index, lps);
            return contentView;
        }
    }

    /**
     * Set animation when transform the ViewState from one to another. For example, from loading view
     * to load-fail view, the load-fail view will start an in-animation, and loading view will
     * start an out-animation.
     *
     * @param inAnimation  in animation
     * @param outAnimation out animation
     */
    public void setTransformAnimation(Animation inAnimation, Animation outAnimation) {
        va_container.setInAnimation(inAnimation);
        va_container.setOutAnimation(outAnimation);
    }

    public View getTitleView() {
        return titleView;
    }

    public ViewGroup getRefreshLayout() {
        return srl_layout;
    }

    private Runnable contentRunnable = new Runnable() {
        @Override
        public void run() {
            mState = ViewState.Content;
            if (va_container != null)
                va_container.setDisplayedChild(mState.ordinal());
        }
    };

    /**
     * Show content view.
     */
    public boolean showContentView() {
        if ((mType & TYPE_L) == 0) {
            return false;
        }
        handler.removeCallbacks(contentRunnable);
        handler.removeCallbacks(nodataRunnable);
        if (mState == ViewState.Content)
            return false;

        handler.postDelayed(contentRunnable, DEFAULT_LOADING_DELAY);
        return true;
    }

    /**
     * Show loading view.
     */
    public boolean showLoadingView() {
        if ((mType & TYPE_L) == 0) {
            return false;
        }
        handler.removeCallbacks(contentRunnable);
        handler.removeCallbacks(nodataRunnable);
        if (mState == ViewState.Loading)
            return false;

        if (loadViewFactory != null) {
            loadViewFactory.onShowLoading();
        }
        mState = ViewState.Loading;
        va_container.setDisplayedChild(mState.ordinal());
        return true;
    }

    /**
     * Show load-fail view
     */
    public boolean showLoadFailView() {
        if ((mType & TYPE_L) == 0) {
            return false;
        }

        handler.removeCallbacks(contentRunnable);
        handler.removeCallbacks(nodataRunnable);
        if (mState == ViewState.LoadFail)
            return false;

        if (loadViewFactory != null) {
            loadViewFactory.onShowFail();
        }
        mState = ViewState.LoadFail;
        va_container.setDisplayedChild(mState.ordinal());
        return true;
    }

    private Runnable nodataRunnable = new Runnable() {
        @Override
        public void run() {
            if (loadViewFactory != null) {
                loadViewFactory.onShowNodata();
            }
            mState = ViewState.NoData;
            if (va_container != null)
                va_container.setDisplayedChild(mState.ordinal());
        }
    };

    /**
     * Show no-data view.
     */
    public boolean showNoDataView() {
        if ((mType & TYPE_L) == 0) {
            return false;
        }

        handler.removeCallbacks(contentRunnable);
        handler.removeCallbacks(nodataRunnable);
        if (mState == ViewState.NoData)
            return false;

        handler.postDelayed(nodataRunnable, DEFAULT_LOADING_DELAY);
        return true;
    }

    /**
     * Get te current showing state of the layout.
     *
     * @return {@link ViewState}
     */
    public ViewState getState() {
        if ((mType & TYPE_L) == 0) {
            return ViewState.Content;
        }

        return mState;
    }
}
