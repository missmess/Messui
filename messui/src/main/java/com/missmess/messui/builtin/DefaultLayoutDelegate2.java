package com.missmess.messui.builtin;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.missmess.messui.LayoutBuildable;
import com.missmess.messui.LayoutDelegate;
import com.missmess.messui.LayoutDelegate2;
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
 * @deprecated use {@link DefaultLayoutDelegate}
 */
public class DefaultLayoutDelegate2 extends LayoutDelegate2<DefaultLayoutFactory, TitleBuilderRaw, TitleBuilderRaw.Param,
        LoadViewBuilderRaw, LoadViewBuilderRaw.Param, RefreshBuilderRaw, RefreshBuilderRaw.Param> {

    public DefaultLayoutDelegate2(LayoutBuildable iBase) {
        super(iBase);
    }

    @Override
    protected void preworkTitle(DefaultLayoutFactory factory, TitleBuilderRaw.Param p) {
        if (p.overlay)
            factory.setTitleBarOverlay();
    }

    @Override
    protected void applyBuildTitle(DefaultLayoutFactory factory, final TitleBuilderRaw.Param p) {
        TitleView tv = factory.getTitleBarFactory().getTitleView();
        if (p.titleStr != null)
            tv.setTitleText(p.titleStr);
        if (p.titleView != null)
            tv.setCustomTitleView(p.titleView);
        if (p.titleStrRes != 0)
            tv.setTitleText(p.titleStrRes);
        if (p.titleViewRes != 0)
            tv.setCustomTitleView(p.titleViewRes);
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

    @Override
    protected void preworkLoadViews(DefaultLayoutFactory factory, LoadViewBuilderRaw.Param p) {
        if (p.anchor != 0)
            factory.setLoadViewAnchor(p.anchor);
    }

    @Override
    protected void applyBuildLoadViews(DefaultLayoutFactory factory, LoadViewBuilderRaw.Param p) {
        DefaultLoadViewFactory loadViewFactory = factory.getLoadViewFactory();
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

    @Override
    protected void preworkRefreshLayout(DefaultLayoutFactory factory, RefreshBuilderRaw.Param p) {
        if (p.anchorViewId != null)
            factory.setRefreshLayoutAnchor(p.anchorViewId);
    }

    @Override
    protected void applyBuildRefreshLayout(DefaultLayoutFactory factory, RefreshBuilderRaw.Param p) {
        DefaultRefreshLayoutFactory refreshLayoutFactory = factory.getRefreshLayoutFactory();
        SwipeRefreshLayout refreshLayout = refreshLayoutFactory.getRefreshLayout();
    }

    @Override
    protected DefaultLayoutFactory createFactoryInstance(Context context) {
        return new DefaultLayoutFactory(context);
    }
}
