package com.missmess.messui;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

import com.missmess.messui.builtin.DefaultLayoutDelegate;
import com.missmess.messui.builtin.SystemUIBuilderRaw;
import com.missmess.messui.utils.StatusBarUtils;
import com.missmess.messui.utils.TUtil;

/**
 * LayoutDelegate do the whole layout-build jobs, and apply the builder params to base layout,
 * it connect LayoutFactory and layout builders.
 *
 * @author wl
 * @see DefaultLayoutDelegate
 * @since 2018/07/16 15:06
 * @deprecated use {@link LayoutDelegate}
 */
public abstract class LayoutDelegate2<FACTORY extends ILayoutFactory,
        TB extends TitleBuilder, TP extends TitleBuilder.Param,
        LB extends LoadViewBuilder, LP extends LoadViewBuilder.Param,
        RB extends RefreshBuilder, RP extends RefreshBuilder.Param> {
    private BuilderKit<TB, LB, RB, SystemUIBuilderRaw> kit;
    private LayoutBuildable iBase;
    private FACTORY layoutFactory;
    private TP tp;
    private LP lp;
    private SystemUIBuilderRaw.Param sp;
    private RP rp;
    protected Context context;
    protected View contentView;
    private int layoutType = -1;

    public LayoutDelegate2(LayoutBuildable iBase) {
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
            applyBuildTitle(layoutFactory, tp);
        }
        // 2
        if (kit.lb.isEnabled()) {
            applyBuildLoadViews(layoutFactory, lp);
        }
        // 3
        if (kit.rb.isEnabled()) {
            applyBuildRefreshLayout(layoutFactory, rp);
        }
    }

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



        TB titleBuilder = TUtil.getT(this, 1);
        LB loadViewBuilder = TUtil.getT(this, 3);
        RB refreshLayoutBuilder = TUtil.getT(this, 5);
        SystemUIBuilderRaw systemUIBuilder = new SystemUIBuilderRaw();
        kit.inject(titleBuilder, loadViewBuilder, refreshLayoutBuilder, systemUIBuilder);
        // LayoutBuildable to config the layout.
        iBase.configureBuild(kit);
        // obtain the correct layout type
        if (titleBuilder != null && titleBuilder.isEnabled()) {
            t |= LayoutHelper.TYPE_T;
            tp = (TP) titleBuilder.build();
            // pre-doing work.
            preworkTitle(layoutFactory, tp);
        }
        if (loadViewBuilder != null && loadViewBuilder.isEnabled()) {
            t |= LayoutHelper.TYPE_L;
            lp = (LP) loadViewBuilder.build();
            // pre-doing work.
            preworkLoadViews(layoutFactory, lp);
        }
        if (refreshLayoutBuilder != null && refreshLayoutBuilder.isEnabled()) {
            t |= LayoutHelper.TYPE_R;
            rp = (RP) refreshLayoutBuilder.build();
            // pre-doing work.
            preworkRefreshLayout(layoutFactory, rp);
        }
        if (systemUIBuilder.isEnabled()) {
            sp = (SystemUIBuilderRaw.Param) ((SystemUIBuilder) systemUIBuilder).build();
        }
        layoutType = t;
        return t;
    }

    /**
     * pre-work with title before layoutfactory made its children factory
     */
    protected void preworkTitle(FACTORY factory, TP tp) {
    }

    /**
     * Do how params apply to relative layout
     * @param factory factory
     * @param tp params
     */
    protected abstract void applyBuildTitle(FACTORY factory, TP tp);

    /**
     * pre-work with title before layoutfactory made its children factory
     */
    protected void preworkLoadViews(FACTORY factory, LP lp) {
    }

    /**
     * Do how params apply to relative layout
     * @param factory factory
     * @param lp params
     */
    protected abstract void applyBuildLoadViews(FACTORY factory, LP lp);

    /**
     * pre-work with title before layoutfactory made its children factory
     */
    protected void preworkRefreshLayout(FACTORY factory, RP rp) {
    }

    /**
     * Do how params apply to relative layout
     * @param factory factory
     * @param rp params
     */
    protected abstract void applyBuildRefreshLayout(FACTORY factory, RP rp);

    /**
     * Do create a layout factory
     * @param context context
     * @return factory
     */
    protected abstract FACTORY createFactoryInstance(Context context);
}
