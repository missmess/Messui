package com.missmess.messui.builtin;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.missmess.messui.LayoutBuildable;
import com.missmess.messui.LayoutDelegate;
import com.missmess.messui.LoadViewBuilder;
import com.missmess.messui.RefreshBuilder;
import com.missmess.messui.TitleBuilder;
import com.missmess.messui.widget.TitleView;

/**
 * DefaultLayoutDelegate use {@link DefaultLayoutFactory} and default Builders.
 * <p>
 * If you want to use different factory or use enhanced builders(such as add other params on
 * TitleBuilder), create a new {@link LayoutDelegate}.
 *
 * @author wl
 * @since 2018/07/16 15:42
 */
public class DefaultLayoutDelegate extends LayoutDelegate<DefaultLayoutFactory, TitleBuilder, LoadViewBuilder, RefreshBuilder> {

    public DefaultLayoutDelegate(LayoutBuildable iBase) {
        super(iBase);
    }

    @Override
    public Provider<DefaultLayoutFactory, TitleBuilder, TitleBuilder.Param> provideTitleBuilder() {
        return new TitleProvider();
    }

    @Override
    public Provider<DefaultLayoutFactory, LoadViewBuilder, LoadViewBuilder.Param> provideLoadViewBuilder() {
        return new LoadViewProvider();
    }

    @Override
    public Provider<DefaultLayoutFactory, RefreshBuilder, RefreshBuilder.Param> provideRefreshBuilder() {
        return new RefreshProvider();
    }

    @Override
    protected DefaultLayoutFactory createFactoryInstance(Context context) {
        return new DefaultLayoutFactory(context);
    }

    public static class TitleProvider extends Provider<DefaultLayoutFactory, TitleBuilder, TitleBuilder.Param> {

        @Override
        protected TitleBuilder provideBuilder() {
            return new TitleBuilderRaw();
        }

        @Override
        public void preWork(DefaultLayoutFactory factory, TitleBuilder.Param p) {
            if (p.overlay)
                factory.setTitleBarOverlay();
        }

        @Override
        public void apply(DefaultLayoutFactory factory, final TitleBuilder.Param p) {
            TitleView tv = factory.getTitleBarFactory().getTitleView();
            applyTo(tv, p);
        }

        public void applyTo(TitleView tv, final TitleBuilder.Param p) {
            if (p.titleStr != null)
                tv.setTitleText(p.titleStr);
            if (p.titleView != null)
                tv.setCustomTitleView(p.titleView);
            if (p.titleColor != null)
                tv.setTitleTextColor(p.titleColor);
            if (p.titleBehindNavigate)
                tv.setTitleGravity(TitleView.GRAVITY_BEHIND_NAVIGATE);
            if (p.hideNavigate)
                tv.showNavigateButton(false);
            if (p.addiBtnText != null) {
                tv.setAdditionalButton(p.addiBtnText, -1, p.addiBtnClicker);
            }
            if (p.paddings != null) {
                tv.setPadding(p.paddings[0], p.paddings[1], p.paddings[2], p.paddings[3]);
            }
            if (p.navigateIcon != 0)
                tv.setNavigateIcon(p.navigateIcon);
            if (p.navigateText != null)
                tv.setNavigateText(p.navigateText);
            if (p.navigateTextColor != null)
                tv.setNavigateTextColor(p.navigateTextColor);
            if (p.navigateClicker != null)
                tv.setNavigateClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        p.navigateClicker.run();
                    }
                });
            if (p.bgColor != null)
                tv.setBackgroundColor(p.bgColor);
            tv.getLayoutParams().height = p.height;

            for (TitleBuilder.RightView rightView : p.rightViews) {
                if (rightView.icon != 0)
                    tv.addActionView(rightView.icon, rightView.click);
                else if (rightView.text != null)
                    tv.addActionView(rightView.text, rightView.click);
                else if (rightView.view != null)
                    tv.addActionView(rightView.view);
            }
        }
    }

    public static class LoadViewProvider extends Provider<DefaultLayoutFactory, LoadViewBuilder, LoadViewBuilder.Param> {
        @Override
        protected LoadViewBuilder provideBuilder() {
            return new LoadViewBuilderRaw();
        }

        @Override
        public void preWork(DefaultLayoutFactory factory, LoadViewBuilder.Param p) {
            if (p.anchor != 0)
                factory.setLoadViewAnchor(p.anchor);
        }

        @Override
        public void apply(DefaultLayoutFactory factory, LoadViewBuilder.Param p) {
            DefaultLoadViewFactory loadViewFactory = factory.getLoadViewFactory();
            applyTo(loadViewFactory, p);
        }

        public void applyTo(DefaultLoadViewFactory loadViewFactory, LoadViewBuilder.Param p) {
            if (p.retryOP != null)
                loadViewFactory.setDoRetry(p.retryOP);
            if (p.loadTip != null)
                loadViewFactory.setLoadingHint(p.loadTip);
            if (p.failTip != null)
                loadViewFactory.setLoadFailHint(p.failTip);
            if (p.noDataTip != null)
                loadViewFactory.setNoDataHint(p.noDataTip);
            if (p.padEdited) {
                loadViewFactory.setLoadViewPadding(p.paddingTop, p.paddingBottom);
            }
        }
    }

    public static class RefreshProvider extends Provider<DefaultLayoutFactory, RefreshBuilder, RefreshBuilder.Param> {
        @Override
        protected RefreshBuilder provideBuilder() {
            return new RefreshBuilderRaw();
        }

        @Override
        public void preWork(DefaultLayoutFactory factory, RefreshBuilder.Param p) {
            if (p.anchorViewId != null)
                factory.setRefreshLayoutAnchor(p.anchorViewId);
        }

        @Override
        public void apply(DefaultLayoutFactory factory, RefreshBuilder.Param p) {
            DefaultRefreshLayoutFactory refreshLayoutFactory = factory.getRefreshLayoutFactory();
            SwipeRefreshLayout refreshLayout = refreshLayoutFactory.getRefreshLayout();
            applyTo(refreshLayout, p);
        }

        public void applyTo(SwipeRefreshLayout refreshLayout, RefreshBuilder.Param param) {
            // todo
            final RefreshBuilder.Param p = param;
            refreshLayout.setEnabled(p.refreshEnabled);
            if (p.loadMoreEnabled) {
                // it's a pity that SwipeRefreshLayout doesn't support load more now!
            }
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    p.onRefresh.run();
                }
            });
            if (p.colors != null) {
                refreshLayout.setColorSchemeColors(p.colors);
            }
        }
    }
}
