package com.missmess.messui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.missmess.messui.builtin.DefaultLayoutDelegate;
import com.missmess.messui.builtin.DefaultLayoutFactory;
import com.missmess.messui.builtin.SystemUIBuilderRaw;

/**
 * CoreActivity provides convenient layout-build function using {@link LayoutDelegate}. By default,
 * {@link DefaultLayoutDelegate} is used, if you want to use other {@link ILayoutFactory} or
 * other {@link IBuilder}, provide a LayoutDelegate implementation in {@link #createLayoutDelegate()}.
 * <p></p><b>Keep the generic type the same in {@link LayoutDelegate} and activity.</b>
 * <p>
 * <p>Call orders:</p>
 * <p>
 * {@link #buildLayout(BuilderKit)} -> {@link #setContentView(int)} ->
 * {@link #initView(Bundle)}
 * </p>
 *
 * @author wl
 * @since 2018/07/16 10:03
 */
public abstract class CoreActivity<TB extends TitleBuilder, LB extends LoadViewBuilder,
        RB extends RefreshBuilder>
        extends AppCompatActivity implements LayoutBuildable {
    protected LayoutDelegate mDelegate;
    private ILayoutFactory mFactory;
    protected LayoutHelper layoutHelper;
    private View mRawContentView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mDelegate == null) {
            mDelegate = createLayoutDelegate();
        }
        View contentLayout = createContentLayout(getLayoutResId());
        if (contentLayout != null) {
            mDelegate.onViewCreated(contentLayout);
            setContentView(contentLayout);
        }

        initView(savedInstanceState);
    }

    protected LayoutDelegate createLayoutDelegate() {
        return new DefaultLayoutDelegate(this);
    }

    /**
     * Create a layout factory used in this activity. Use {@link DefaultLayoutFactory} as default,
     * by override this method to specify a layout factory you want.
     *
     * @return layout factory used to setting UI.
     */
    @Override
    public final ILayoutFactory provideLayoutFactory() {
        return mDelegate.createLayoutFactory(this);
    }

    @Override
    public final void applyBuild(ILayoutFactory layoutFactory) {
        mDelegate.applyBuild(layoutFactory);
        mDelegate.applyBuildSystemUI();
    }

    protected View createContentLayout(int layoutResID) {
        View originalContentView;
        if (layoutResID == 0) {
            originalContentView = null;
        } else {
            LayoutInflater inflater = LayoutInflater.from(this);
            originalContentView = inflater.inflate(layoutResID, new FrameLayout(this), false);
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
        layoutHelper = new LayoutHelper(this, getLayoutType(), factory);
        View layoutView = layoutHelper.createView(originalContentView);
        mRawContentView = layoutView;
        return layoutView;
    }

    protected View getActivityRootView() {
        return mRawContentView;
    }

    /**
     * Return the title view widget.
     *
     * @return View, because we do not know what the raw type of your title view, cast it by
     * yourself. May be null if no title need.
     */
    public View getTitleView() {
        return layoutHelper.getTitleView();
    }

    /**
     * Return refresh layout.
     *
     * @return may be null.
     */
    public ViewGroup getRefreshLayout() {
        return layoutHelper.getRefreshLayout();
    }

    public ViewState getViewState() {
        return layoutHelper.getState();
    }

    /**
     * Set current load-views state.
     *
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
     * show fail, so we return false, user may show toast or other instead, result should always be
     * checked.
     *
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
        ((TitleBuilder.Setting) kit.tb.getDefault())
                .title(getTitle().toString());
        // default enable primaryDark status bar and white navigation bar
        ((SystemUIBuilder.Setting) kit.sb.enable())
                .statusBarColor(getColorPrimaryDark())
                .navigationBarColor(Color.WHITE);
    }

    private int getColorPrimaryDark() {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        return typedValue.data;
    }

    /**
     * Always do get data OP which relative to displays on UI, this method default will be called
     * when load-fail view showing and retry button has be clicked.
     */
    public void loadData() {
    }

    /**
     * Put you init operation here, this will be called AFTER onCreate
     *
     * @param savedInstanceState savedInstanceState
     */
    protected abstract void initView(Bundle savedInstanceState);

    /**
     * Layout resource ID.
     *
     * @return R.layout.xxx
     */
    @LayoutRes
    protected abstract int getLayoutResId();

    /**
     * @see LayoutBuildable#configureBuild(BuilderKit) configureBuild
     */
    public abstract void buildLayout(BuilderKit<TB, LB, RB, SystemUIBuilderRaw> kit);
}
