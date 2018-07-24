package com.missmess.messui;

import android.support.annotation.IdRes;
import android.view.View;
import android.view.ViewGroup;

/**
 * A layout factory used in {@link LayoutHelper} to help to easily create a base layout contains
 * loading-view, load-fail, no-data, title bar, pull to refresh-load layout, etc.
 *
 * @author wl
 * @since 2016/07/01 15:59
 */
public interface ILayoutFactory<T extends ILayoutFactory.ITitleBarFactory, L extends ILayoutFactory.ILoadViewFactory, R extends ILayoutFactory.IRefreshLayoutFactory> {

    T madeTitleBarFactory();
    T getTitleBarFactory();

    L madeLoadViewFactory();
    L getLoadViewFactory();

    R madeRefreshLayoutFactory();
    R getRefreshLayoutFactory();

    interface ITitleBarFactory<Title extends View> {
        /**
         * Create title bar.
         *
         * @param parent parent
         * @return title bar
         */
        Title createTitleBar(ViewGroup parent);

        /**
         * Create layout below title bar and cover over load-views.
         *
         * @param parent parent
         * @return layout
         */
        View createLayoutBelowTitle(ViewGroup parent);

        /**
         * If title view overlay the content.
         * @return true - overlay
         */
        boolean titleViewOverlayContent();
    }

    interface ILoadViewFactory {
        /**
         * Create loading view.
         *
         * @param parent parent
         * @return loading view
         */
        View createLoadingLayout(ViewGroup parent);

        /**
         * Create no-data view.
         *
         * @param parent parent
         * @return no-data view
         */
        View createNoDataLayout(ViewGroup parent);

        /**
         * Create load-fail view.
         *
         * @param parent parent
         * @return load-fail view
         */
        View createLoadFailLayout(ViewGroup parent);

        /**
         * The anchor view is where load-views showing.
         * @return for no anchor view if 0.
         */
        @IdRes
        int anchorView();

        /**
         * On loading view showing.
         */
        void onShowLoading();

        /**
         * On no-data view showing.
         */
        void onShowNodata();

        /**
         * On load-fail view showing.
         */
        void onShowFail();
    }

    /**
     * Refresh layout contains pull to refresh, pull to load-more functions.
     */
    interface IRefreshLayoutFactory<Refresh extends ViewGroup> {
        /**
         * Create refresh layout
         * @param refreshChild child view which will be added to refresh layout
         * @return refresh layout
         */
        Refresh createRefreshLayout(View refreshChild);

        /**
         * The anchor view is where refresh layout add on.
         * @return for no anchor view if 0.
         */
        @IdRes
        int anchorView();
    }
}
