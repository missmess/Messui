package com.missmess.messui;

import android.support.annotation.IdRes;

/**
 * LoadViewBuilder used to build the load-views.
 *
 * @author wl
 * @since 2017/07/17 18:36
 */
public abstract class LoadViewBuilder<S1 extends LoadViewBuilder.Setting> extends IBuilder<S1, LoadViewBuilder.Param> {

    @SuppressWarnings("unchecked")
    public abstract static class Setting<P extends Param, S2 extends Setting<P, S2>> extends ISetting<P> {

        /**
         * Retry operation
         */
        public S2 retry(Runnable op) {
            p.retryOP = op;
            return (S2) this;
        }

        /**
         * Tips showing on loading
         */
        public S2 loadingTip(CharSequence loadTip) {
            p.loadTip = loadTip;
            return (S2) this;
        }

        /**
         * Tips showing on load fail
         */
        public S2 loadFailTip(CharSequence failTip) {
            p.failTip = failTip;
            return (S2) this;
        }

        /**
         * Tips showing on no-data
         */
        public S2 noDataTip(CharSequence noDataTip) {
            p.noDataTip = noDataTip;
            return (S2) this;
        }

        /**
         * By default load-views is in center of contentView, call this make this feature disable,
         * and set padding top, bottom.
         */
        public S2 viewOffset(int paddingTop, int paddingBottom) {
            p.padEdited = true;
            p.paddingTop = paddingTop;
            p.paddingBottom = paddingBottom;
            return (S2) this;
        }

        /**
         * The anchor view will wrap with load-views
         */
        public S2 anchor(@IdRes int anchor) {
            p.anchor = anchor;
            return (S2) this;
        }
    }

    public static class Param implements IParam {
        public Runnable retryOP;
        public CharSequence loadTip;
        public CharSequence failTip;
        public CharSequence noDataTip;
        public int paddingTop;
        public int paddingBottom;
        public boolean padEdited;
        public @IdRes
        int anchor;
    }
}
