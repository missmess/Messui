package com.missmess.messui;

import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;

/**
 * RefreshBuilder
 *
 * @author wl
 * @since 2018/02/23 17:06
 */
public abstract class RefreshBuilder<S1 extends RefreshBuilder.Setting> extends IBuilder<S1, RefreshBuilder.Param> {

    @SuppressWarnings("unchecked")
    public abstract static class Setting<P extends Param, S2 extends Setting<P, S2>> extends ISetting<P> {

        /**
         * Enable pull-to-refresh function
         * @param onRefresh on refresh listener
         */
        public S2 enableRefresh(Runnable onRefresh) {
            p.refreshEnabled = true;
            p.onRefresh = onRefresh;
            return (S2) this;
        }

        /**
         * Enable pull-to-loadmore function
         * @param onLoadMore on loadmore listener
         */
        public S2 enableLoadMore(Runnable onLoadMore) {
            p.loadMoreEnabled = true;
            p.onLoadMore = onLoadMore;
            return (S2) this;
        }

        /**
         * Auto load more when scroll to bottom
         */
        public S2 autoLoadMore(boolean enable) {
            p.autoLoadMore = enable;
            return (S2) this;
        }

        /**
         * When is refreshing, whether user can scroll the contentView
         */
        public S2 scrollableWhenRefreshing(boolean enable) {
            p.scrollWhenRefresh = enable;
            return (S2) this;
        }

        /**
         * Enable over scroll effect.
         */
        public S2 overScroll(boolean enable) {
            p.overScroll = enable;
            return (S2) this;
        }

        /**
         * The anchor view to wrap with refresh widget ViewGroup
         */
        public S2 anchor(@IdRes int anchorId) {
            p.anchorViewId = anchorId;
            return (S2) this;
        }

        /**
         * Color schemas used with refresh header
         */
        public S2 colorSchemas(@ColorInt int... colors) {
            p.colors = colors;
            return (S2) this;
        }
    }

    public static class Param implements IParam {
        public boolean refreshEnabled = false;
        public Runnable onRefresh;
        public Runnable onLoadMore;
        public boolean loadMoreEnabled = false;
        public boolean autoLoadMore = true;
        public boolean scrollWhenRefresh = true;
        public boolean overScroll = true;
        public int[] colors;
        public @IdRes
        Integer anchorViewId;
    }
}
