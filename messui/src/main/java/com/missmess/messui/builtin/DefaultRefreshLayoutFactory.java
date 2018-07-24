package com.missmess.messui.builtin;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;

import com.missmess.messui.ILayoutFactory;
import com.missmess.messui.R;

/**
 * Default refresh layout factory.
 *
 * @author wl
 * @since 2018/02/09 15:35
 */
public class DefaultRefreshLayoutFactory implements ILayoutFactory.IRefreshLayoutFactory<SwipeRefreshLayout> {
    private Context mContext;
    private SwipeRefreshLayout refreshLayout;
    private int anchor;

    public DefaultRefreshLayoutFactory(Context context, int anchor) {
        this.mContext = context;
        this.anchor = anchor;
    }

    @Override
    public SwipeRefreshLayout createRefreshLayout(View refreshChild) {
        refreshLayout = (SwipeRefreshLayout) View.inflate(mContext, R.layout.view_default_refresh, null);
        refreshLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        refreshLayout.addView(refreshChild);
        return refreshLayout;
    }

    public SwipeRefreshLayout getRefreshLayout() {
        return refreshLayout;
    }

    @Override
    public int anchorView() {
        return anchor;
    }
}
