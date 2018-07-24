package com.missmess.messui;

/**
 * A suit of layout builders kit.
 *
 * @author wl
 * @since 2018/07/16 16:06
 */
public class BuilderKit<TB extends TitleBuilder, LB extends LoadViewBuilder,
        RB extends RefreshBuilder, SB extends SystemUIBuilder> {
    public TB tb;
    public LB lb;
    public RB rb;
    public SB sb;

    BuilderKit() {}

    void inject(TB titleBuilder, LB loadViewBuilder, RB refreshLayoutBuilder, SB systemUIBuilder) {
        this.tb = titleBuilder;
        this.lb = loadViewBuilder;
        this.rb = refreshLayoutBuilder;
        this.sb = systemUIBuilder;
    }
}
