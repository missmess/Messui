package com.missmess.messui;

import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.annotation.IntRange;

/**
 * SystemUIBuilder
 *
 * @author wl
 * @since 2017/10/25 14:11
 */
public abstract class SystemUIBuilder<S1 extends SystemUIBuilder.Setting> extends IBuilder<S1, SystemUIBuilder.Param> {

    @SuppressWarnings("unchecked")
    public abstract static class Setting<P extends Param, S2 extends Setting<P, S2>> extends ISetting<P> {

        /**
         * 全屏，与其它模式冲突
         * @return for link call
         */
        public S2 fullscreen() {
            p.hideBar = true;
            return (S2) this;
        }

        /**
         * 状态栏颜色值,argb色
         * @param color color
         * @return for link call
         */
        public S2 statusBarColor(@ColorInt int color) {
            p.hideBar = false;
            p.statusBarColor = color;
            return (S2) this;
        }

        /**
         * 额外为状态栏增加透明蒙层，overlap模式并且设置barColor后，蒙层失效。
         * @param alpha 透明蒙层alpha值
         * @return for link call
         */
        public S2 statusBarMasking(@IntRange(from = 0, to = 255) int alpha) {
            p.hideBar = false;
            p.statusBarMaskAlpha = alpha;
            return (S2) this;
        }

        /**
         * 是否状态栏与内容重叠
         * @param offsetView 这个view将offset，以显示在状态栏下。0表示不传
         * @return for link call
         */
        public S2 overlap(@IdRes int offsetView) {
            p.hideBar = false;
            p.overlap = true;
            p.overlapExclude = offsetView;
            return (S2) this;
        }

        /**
         * 设置底部导航栏颜色
         * @param color argb
         * @return for link call
         */
        public S2 navigationBarColor(@ColorInt int color) {
            p.navigationBarColor = color;
            return (S2) this;
        }

        /**
         * 在Lollipop上和Kitkat上使用同样的方法，无差异。但可能会导致导航栏的颜色无法设置，只能
         * 显示透明。
         * @return for link call
         */
        public S2 drawBarCompat() {
            p.drawRawOnL = false;
            return (S2) this;
        }
    }

    public static class Param implements IParam {
        public boolean hideBar;
        public boolean overlap;
        public @IdRes
        int overlapExclude;
        public Integer navigationBarColor;
        public int statusBarColor = 0;
        public int statusBarMaskAlpha = 0;
        public boolean drawRawOnL = true;
    }
}
