package com.missmess.messui;

import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Px;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * TitleBuilder used to build a title bar easily.
 *
 * @author wl
 * @since 2017/07/17 18:15
 */
public abstract class TitleBuilder<S1 extends TitleBuilder.Setting> extends IBuilder<S1, TitleBuilder.Param> {

    @SuppressWarnings("unchecked")
    public abstract static class Setting<P extends Param, S2 extends Setting<P, S2>> extends ISetting<P> {

        public S2 title(String title) {
            p.titleStr = title;
            p.titleStrRes = 0;
            p.titleView = null;
            p.titleViewRes = 0;
            return (S2) this;
        }

        public S2 title(@StringRes int title) {
            p.titleStr = null;
            p.titleStrRes = title;
            p.titleView = null;
            p.titleViewRes = 0;
            return (S2) this;
        }

        public S2 customTitle(View titleView) {
            p.titleStr = null;
            p.titleStrRes = 0;
            p.titleView = titleView;
            p.titleViewRes = 0;
            return (S2) this;
        }

        public S2 customTitle(@LayoutRes int titleLayoutRes) {
            p.titleStr = null;
            p.titleStrRes = 0;
            p.titleView = null;
            p.titleViewRes = titleLayoutRes;
            return (S2) this;
        }

        public S2 height(@Px int height) {
            p.height = height;
            return (S2) this;
        }

        /**
         * Set title show on the right side behind navigation button, not in center.
         * @return Setting for link call.
         */
        public S2 titleBehindNavigate() {
            p.titleBehindNavigate = true;
            return (S2) this;
        }

        public S2 hideNavigate() {
            p.hideNavigate = true;
            return (S2) this;
        }

        public S2 additionalBtn(String text, View.OnClickListener click) {
            p.addiBtnText = text;
            p.addiBtnClicker = click;
            return (S2) this;
        }

        /**
         * Set title view overlay the content view.
         * @return Setting for link call.
         */
        public S2 overlay() {
            p.overlay = true;
            return (S2) this;
        }

        public S2 navigateIcon(@DrawableRes int icon) {
            p.navigateIcon = icon;
            return (S2) this;
        }

        public S2 navigateText(String text) {
            p.navigateText = text;
            return (S2) this;
        }

        public S2 navigateClick(Runnable click) {
            p.navigateClicker = click;
            return (S2) this;
        }

        public S2 rightView(@DrawableRes int icon, View.OnClickListener click) {
            p.rightViews.add(new RightView(icon, click));
            return (S2) this;
        }

        public S2 rightView(String text, View.OnClickListener click) {
            p.rightViews.add(new RightView(text, click));
            return (S2) this;
        }

        public S2 rightView(View view) {
            p.rightViews.add(new RightView(view));
            return (S2) this;
        }

        public S2 padding(@Px int paddingLeft, @Px int paddingTop, @Px int paddingRight, @Px int paddingBottom) {
            p.paddings = new int[] {paddingLeft, paddingTop, paddingRight, paddingBottom};
            return (S2) this;
        }

        public S2 bgColor(@ColorInt int color) {
            p.bgColor = color;
            return (S2) this;
        }
    }

    public static class Param implements IParam {
        public String titleStr;
        public int titleStrRes;
        public int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        public View titleView;
        public int titleViewRes;
        public boolean titleBehindNavigate;
        public boolean hideNavigate;
        public int navigateIcon;
        public String navigateText;
        public String addiBtnText;
        public View.OnClickListener addiBtnClicker;
        public boolean overlay;
        public Runnable navigateClicker;
        public List<RightView> rightViews = new ArrayList<>();
        public int[] paddings;
        public Integer bgColor;
    }

    public static class RightView {
        public @DrawableRes
        int icon;
        public String text;
        public View view;
        public View.OnClickListener click;

        RightView(int icon, View.OnClickListener click) {
            this.icon = icon;
            this.click = click;
        }

        RightView(String text, View.OnClickListener click) {
            this.text = text;
            this.click = click;
        }

        RightView(View view) {
            this.view = view;
        }
    }
}
