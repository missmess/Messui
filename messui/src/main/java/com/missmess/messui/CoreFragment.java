package com.missmess.messui;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.missmess.messui.builtin.DefaultLayoutDelegate;
import com.missmess.messui.builtin.DefaultLayoutFactory;
import com.missmess.messui.builtin.SystemUIBuilderRaw;

/**
 * CoreFragment.
 *
 * @see CoreActivity
 * @author wl
 * @since 2018/07/17 16:26
 */
public abstract class CoreFragment<TB extends TitleBuilder, LB extends LoadViewBuilder,
        RB extends RefreshBuilder>
        extends Fragment implements LayoutBuildable {
    protected CoreActivity mActivity;
    protected LayoutDelegate mDelegate;
    private ILayoutFactory mFactory;
    protected LayoutHelper layoutHelper;
    private View mRawContentView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mDelegate == null) {
            mDelegate = createLayoutDelegate();
        }
        mActivity = (CoreActivity) getContext();
        View contentLayout = createContentLayout(getLayoutResId());
        if (contentLayout != null) {
            mDelegate.onViewCreated(contentLayout);
        }
        return contentLayout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view, savedInstanceState);
    }

    protected LayoutDelegate createLayoutDelegate() {
        return new DefaultLayoutDelegate(this);
    }

    /**
     * Create a layout factory used in this fragment. Use {@link DefaultLayoutFactory} as default,
     * by override this method to specify a layout factory you want.
     *
     * @return layout factory used to setting UI.
     */
    @Override
    public final ILayoutFactory provideLayoutFactory() {
        return mDelegate.createLayoutFactory(getContext());
    }

    @Override
    public final void applyBuild(ILayoutFactory layoutFactory) {
        mDelegate.applyBuild(layoutFactory);
    }

    protected View createContentLayout(int layoutResID) {
        View originalContentView;
        if (layoutResID == 0) {
            originalContentView = null;
        } else {
            LayoutInflater inflater = LayoutInflater.from(mActivity);
            originalContentView = inflater.inflate(layoutResID, new FrameLayout(mActivity), false);
            // keep the layout params
            originalContentView.setLayoutParams(new ViewGroup.LayoutParams(originalContentView.getLayoutParams()));
        }

        return createContentLayout(originalContentView);
    }

    protected View createContentLayout(View originalContentView) {
        ILayoutFactory factory = mFactory;
        if (factory == null) {
            factory = provideLayoutFactory();
            if (factory == null) {
                throw new NullPointerException("layout factory can not be null");
            }
            mFactory = factory;
        }
        layoutHelper = new LayoutHelper(mActivity, getLayoutType(), factory);
        View layoutView = layoutHelper.createView(originalContentView);
        mRawContentView = layoutView;
        return layoutView;
    }

    /**
     * Return the title view widget.
     * @return View, because we do not know what the raw type of your title view, cast it by
     * yourself. May be null if no title need.
     */
    public View getTitleView() {
        return layoutHelper.getTitleView();
    }

    /**
     * Return refresh layout.
     * @return may be null.
     */
    public ViewGroup getRefreshLayout() {
        return layoutHelper.getRefreshLayout();
    }

    public View getFragmentRootView() {
        return mRawContentView;
    }

    public ViewState getViewState() {
        return layoutHelper.getState();
    }

    /**
     * Set current load-views state.
     * @param state see {@link ViewState}
     * @return true - success change to state, false - fail by other reason.
     */
    public boolean setViewState(ViewState state) {
        switch (state) {
            case Content:
                return showContentView();
            case NoData:
                return showNoDataView();
            case Loading:
                return showLoadingView();
            case LoadFail:
                return showLoadFailView();
        }
        return false;
    }

    private boolean showContentView() {
        return layoutHelper.showContentView();
    }

    private boolean showLoadingView() {
        return showLoadingView(false);
    }

    private boolean showLoadingView(boolean force) {
        if (!force && (getViewState() == ViewState.Content || getViewState() == ViewState.NoData)) {
            return false;
        } else {
            return layoutHelper.showLoadingView();
        }
    }

    /**
     * Attempt to show load-fail view, if current state is content or no-data, we shall not actually
     * show fail, so we return false, user may show toast or other instead, result should always check.
     * @return true - success show fail view, false otherwise
     */
    @CheckResult
    private boolean showLoadFailView() {
        if (getViewState() == ViewState.Content || getViewState() == ViewState.NoData) {
            return false;
        } else {
            return layoutHelper.showLoadFailView();
        }
    }

    private boolean showNoDataView() {
        return layoutHelper.showNoDataView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mActivity = null;
    }

    public ILayoutFactory getLayoutFactory() {
        return mFactory;
    }

    /**
     * Layout type.
     *
     * @return One of {@link LayoutHelper#TYPE_NONE}, {@link LayoutHelper#TYPE_T},
     * {@link LayoutHelper#TYPE_L}, {@link LayoutHelper#TYPE_R}, or combined flags.
     */
    @LayoutHelper.LayoutType
    @CallSuper
    protected int getLayoutType() {
        return mDelegate.getLayoutType();
    }

    @Override
    public final void configureBuild(BuilderKit kit) {
        configDefault(kit);
        buildLayout(kit);
    }

    protected void configDefault(BuilderKit<TB, LB, RB, SystemUIBuilderRaw> kit) {
        ((LoadViewBuilder.Setting) kit.lb.getDefault())
                .retry(new Runnable() {
                    @Override
                    public void run() {
                        showLoadingView();
                        loadData();
                    }
                });
    }

    /**
     * Always do get data OP which relative to displays on UI, this method default will be called
     * when load-fail view showing and retry button has be clicked.
     */
    public void loadData() {}

    /**
     * Put you init operation here, this will be called AFTER onViewCreated.
     * @param view view
     * @param savedInstanceState savedInstanceState
     */
    protected abstract void initView(View view, Bundle savedInstanceState);

    /**
     * Layout resource ID.
     * @return R.layout.xxx
     */
    @LayoutRes
    protected abstract int getLayoutResId();

    /**
     * @see LayoutBuildable#configureBuild(BuilderKit) configureBuild
     */
    public abstract void buildLayout(BuilderKit<TB, LB, RB, SystemUIBuilderRaw> kit);

}
