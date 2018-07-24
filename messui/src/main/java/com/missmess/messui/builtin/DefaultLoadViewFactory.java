package com.missmess.messui.builtin;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.missmess.messui.ILayoutFactory;
import com.missmess.messui.R;

/**
 * Default load-view factory: contains loading view, no-data view, load-fail view with click retry OP.
 */
public class DefaultLoadViewFactory implements ILayoutFactory.ILoadViewFactory {
    private Context mContext;
    private Runnable retryOp;
    private TextView tv_nodata;
    private LinearLayout loadingView;
    private LinearLayout nodataView;
    private LinearLayout loadfailView;
    private TextView tv_fail;
    private TextView tv_load;
    private int anchorView;

    public DefaultLoadViewFactory(Context context, @IdRes int anchorView) {
        this.mContext = context;
        this.anchorView = anchorView;
    }

    /**
     * Do OP when click retry button when error view showing.
     *
     * @param retryOp op
     */
    public void setDoRetry(Runnable retryOp) {
        this.retryOp = retryOp;
    }

    /**
     * Set hint message on loading view
     */
    public void setLoadingHint(CharSequence msg) {
        if (tv_load != null)
            tv_load.setText(msg);
    }

    /**
     * Set hint message on no-data view
     */
    public void setNoDataHint(CharSequence msg) {
        if (tv_nodata != null)
            tv_nodata.setText(msg);
    }

    /**
     * Set hint message on load-fail view
     */
    public void setLoadFailHint(CharSequence msg) {
        if (tv_fail != null)
            tv_fail.setText(msg);
    }

    /**
     * Set indicator icon on no-data view.
     * @param drawable icon drawable
     */
    public void setNoDataIcon(Drawable drawable) {
        if (tv_nodata != null) {
            tv_nodata.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        }
    }

    /**
     * Set indicator icon on load-fail view.
     * @param drawable icon drawable
     */
    public void setLoadFailIcon(Drawable drawable) {
        if (tv_fail != null) {
            tv_fail.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
        }
    }

    /**
     * Normally load-views is at center of its container, call this to set load-views only center horizontally
     * and have a top and bottom margins with specified values.
     *
     * @param top top margin
     * @param bottom bottom margin
     */
    public void setLoadViewPadding(int top, int bottom) {
        if (loadingView == null)
            return;
        changeMarginsInternal(loadingView, top, bottom);
        changeMarginsInternal(nodataView, top, bottom);
        changeMarginsInternal(loadfailView, top, bottom);
    }

    private void changeMarginsInternal(LinearLayout view, int top, int bottom) {
        view.setGravity(Gravity.CENTER_HORIZONTAL);
        view.setPadding(view.getPaddingLeft(), top, view.getPaddingRight(), bottom);
        view.getChildAt(0).setPadding(0, 0, 0, 0);
    }

    protected
    @LayoutRes
    int loadingLayoutResId() {
        return R.layout.view_default_loading;
    }

    @Override
    public View createLoadingLayout(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View contentView = inflater.inflate(loadingLayoutResId(), parent, false);
        loadingView = (LinearLayout) contentView.findViewById(R.id.ll_loading);
        tv_load = (TextView) loadingView.findViewById(R.id.tv_load);
        return contentView;
    }

    protected
    @LayoutRes
    int noDataLayoutResId() {
        return R.layout.view_default_nodata;
    }

    @Override
    public View createNoDataLayout(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View contentView = inflater.inflate(noDataLayoutResId(), parent, false);
        nodataView = (LinearLayout) contentView.findViewById(R.id.ll_nodata);
        tv_nodata = (TextView) nodataView.findViewById(R.id.tv_nodata);
        return contentView;
    }

    protected
    @LayoutRes
    int loadFailLayoutResId() {
        return R.layout.view_default_fail;
    }

    @Override
    public View createLoadFailLayout(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View contentView = inflater.inflate(loadFailLayoutResId(), parent, false);
        loadfailView = (LinearLayout) contentView.findViewById(R.id.ll_loadfail);
        tv_fail = (TextView) loadfailView.findViewById(R.id.tv_fail);
        loadfailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (retryOp != null)
                    retryOp.run();
            }
        });
        return contentView;
    }

    @Override
    public int anchorView() {
        return anchorView;
    }

    @Override
    public void onShowLoading() {
        //no-op
    }

    @Override
    public void onShowNodata() {
        //no-op
    }

    @Override
    public void onShowFail() {
        //no-op
    }
}
