package com.missmess.messui.builtin;

import com.missmess.messui.TitleBuilder;

/**
 * An subclass with no generic type and not abstract
 *
 * @author wl
 * @since 2018/07/17 10:30
 */
public class TitleBuilderRaw extends TitleBuilder<TitleBuilderRaw.Setting> {

    public static class Setting extends TitleBuilder.Setting<Param, Setting> {
    }

    public static class Param extends TitleBuilder.Param {
    }
}
