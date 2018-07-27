package com.missmess.messui;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

import com.missmess.messui.builtin.DefaultLayoutDelegate;
import com.missmess.messui.builtin.SystemUIBuilderRaw;
import com.missmess.messui.utils.StatusBarUtils;

/**
 * LayoutDelegate do the whole layout-build jobs, and apply the builder params to {@link ILayoutFactory},
 * it connect LayoutFactory and layout builders.
 *
 * @author wl
 * @see DefaultLayoutDelegate
 * @since 2018/07/16 15:06
 */
public abstract class LayoutDelegate<FACTORY extends ILayoutFactory, TB extends TitleBuilder,
        LB extends LoadViewBuilder, RB extends RefreshBuilder> {
    private BuilderKit<TitleBuilder, LoadViewBuilder, RefreshBuilder, SystemUIBuilderRaw> kit;
    private LayoutBuildable iBase;
    private FACTORY layoutFactory;
    private Provider<FACTORY, TB, TB.Param> tP;
    private Provider<FACTORY, LB, LB.Param> lP;
    private Provider<FACTORY, RB, RB.Param> rP;
    private SystemUIBuilderRaw.Param sp;
    protected Context context;
    protected View contentView;
    private int layoutType = -1;

    public LayoutDelegate(LayoutBuildable iBase) {
        this.iBase = iBase;
    }

    /**
     * Call this when the layout has just created.
     */
    void onViewCreated(View contentView) {
        this.contentView = contentView;
        iBase.applyBuild(layoutFactory);
    }

    FACTORY createLayoutFactory(Context context) {
        if (layoutFactory != null) {
            throw new IllegalStateException("Do NOT call createLayoutFactory many times");
        }
        layoutFactory = createFactoryInstance(context);
        this.context = context;
        return layoutFactory;
    }

    /**
     * @see LayoutBuildable#applyBuild(ILayoutFactory)
     */
    void applyBuild(FACTORY layoutFactory) {
        // 1
        if (kit.tb.isEnabled()) {
            tP.apply(layoutFactory, (TitleBuilder.Param) tP.builder.build());
        }
        // 2
        if (kit.lb.isEnabled()) {
            lP.apply(layoutFactory, (LoadViewBuilder.Param) lP.builder.build());
        }
        // 3
        if (kit.rb.isEnabled()) {
            rP.apply(layoutFactory, (RefreshBuilder.Param) rP.builder.build());
        }
    }

    /**
     * We handle out with system ui.
     */
    void applyBuildSystemUI() {
        // 4
        if (kit.sb.isEnabled()) {
            SystemUIBuilder.Param p = sp;
            Activity activity;
            try {
                activity = (Activity) context;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            if (p.hideBar) { // fullscreen
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                }
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                return;
            }

            int statusBarColor = p.statusBarColor;
            if (p.overlap) { // content overlap system bar
                if (p.drawRawOnL)
                    StatusBarUtils.setColorOverlapLolipop(activity, statusBarColor, contentView.findViewById(p.overlapExclude));
                else
                    StatusBarUtils.setColorOverlap(activity, statusBarColor, contentView.findViewById(p.overlapExclude));
            } else { // content fit system bar
                if (p.drawRawOnL)
                    StatusBarUtils.setColorLolipop(activity, statusBarColor, p.statusBarMaskAlpha);
                else
                    StatusBarUtils.setColor(activity, statusBarColor, p.statusBarMaskAlpha);
            }

            if (p.navigationBarColor != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    activity.getWindow().setNavigationBarColor(p.navigationBarColor);
                }
            }
        }
    }

    /**
     * Call {@link LayoutBuildable#configureBuild(BuilderKit)}
     * method in this method.
     *
     * @return layout type
     */
    @LayoutHelper.LayoutType
    @SuppressWarnings("unchecked")
    int getLayoutType() {
        if (layoutType != -1)
            return layoutType;

        int t = LayoutHelper.TYPE_NONE;
        // create builders
        kit = new BuilderKit<>();
        tP = provideTitleBuilder();
        lP = provideLoadViewBuilder();
        rP = provideRefreshBuilder();

        SystemUIBuilderRaw systemUIBuilder = new SystemUIBuilderRaw();
        kit.inject(tP.builder, lP.builder, rP.builder, systemUIBuilder);
        // IMPORTANT STEP! the LayoutBuildable is about to config the layout.
        iBase.configureBuild(kit);
        // obtain the correct layout type
        if (tP.builder.isEnabled()) {
            t |= LayoutHelper.TYPE_T;
            // pre-doing work.
            tP.preWork(layoutFactory, (TitleBuilder.Param) tP.builder.build());
        }
        if (lP.builder.isEnabled()) {
            t |= LayoutHelper.TYPE_L;
            // pre-doing work.
            lP.preWork(layoutFactory, (LoadViewBuilder.Param) lP.builder.build());
        }
        if (rP.builder.isEnabled()) {
            t |= LayoutHelper.TYPE_R;
            // pre-doing work.
            rP.preWork(layoutFactory, (RefreshBuilder.Param) rP.builder.build());
        }
        if (systemUIBuilder.isEnabled()) {
            sp = (SystemUIBuilderRaw.Param) ((SystemUIBuilder) systemUIBuilder).build();
        }
        layoutType = t;
        return t;
    }

    public abstract <P extends TitleBuilder.Param> Provider<FACTORY, TB, P> provideTitleBuilder();

    public abstract <P extends LoadViewBuilder.Param> Provider<FACTORY, LB, P> provideLoadViewBuilder();

    public abstract <P extends RefreshBuilder.Param> Provider<FACTORY, RB, P> provideRefreshBuilder();

    /**
     * Do create a layout factory
     *
     * @param context context
     * @return factory
     */
    protected abstract FACTORY createFactoryInstance(Context context);

    /**
     * Provider take focus on providing builder and applying builder params.
     * @param <F> factory
     * @param <B> builder
     * @param <P> param of builder
     */
    public static abstract class Provider<F extends ILayoutFactory, B extends IBuilder, P extends IParam> {
        private B builder;

        protected Provider() {
            builder = provideBuilder();
        }

        protected abstract B provideBuilder();

        /**
         * pre-work with title before layout-factory made its children factory
         *
         * @param factory factory
         * @param p       params
         */
        public abstract void preWork(F factory, P p);

        /**
         * Do how params apply to relative layout
         *
         * @param factory factory
         * @param p       params
         */
        public abstract void apply(F factory, P p);
    }
}
