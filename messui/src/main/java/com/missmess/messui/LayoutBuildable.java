package com.missmess.messui;

import android.view.View;

/**
 * Which layout can be built by builders such as {@link TitleBuilder}, {@link LoadViewBuilder}.etc.
 *
 * @author wl
 * @since 2017/07/27 14:58
 */
public interface LayoutBuildable {

    /**
     * Configure how to build the title bar, load-views, refresh layout and system UI by its mapping builder, normally
     * used for UI initialization, so it may take no effect if to reuse the builders after initialization has finished.
     * <p>This method always called before {@link android.app.Activity#setContentView(View) setContentView}
     * called.</p>
     * <p>For future settings, such as change title dynamically, you suggest NOT to use the <code>tb</code> again,
     * please get the title widget and set title name by title widget API.</p>
     *
     * @param kit use kit to get builders,
     *            use {@link TitleBuilder#enable()} to enable title bar,
     *            use {@link LoadViewBuilder#enable()} to enable load-view,
     *            use {@link RefreshBuilder#enable()} to enable load-view,
     *            use {@link SystemUIBuilder#enable()} to enable system ui control.
     */
    void configureBuild(BuilderKit kit);

    /**
     * Call it after {@link #configureBuild(BuilderKit)}
     * has done. This method do the real jobs to apply the configuration of builders.
     *
     * @param layoutFactory the factory which provide layout to apply the configuration.
     */
    void applyBuild(ILayoutFactory layoutFactory);

    /**
     * Provide a ILayoutFactory.
     *
     * @return ILayoutFactory
     */
    ILayoutFactory provideLayoutFactory();
}
