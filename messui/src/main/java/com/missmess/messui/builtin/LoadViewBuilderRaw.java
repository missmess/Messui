package com.missmess.messui.builtin;

import com.missmess.messui.LoadViewBuilder;

/**
 * An subclass with no generic type and not abstract
 *
 * @author wl
 * @since 2018/07/17 13:41
 */
public class LoadViewBuilderRaw extends LoadViewBuilder<LoadViewBuilderRaw.Setting> {
    public static class Setting extends LoadViewBuilder.Setting<Param, Setting> {
    }

    public static class Param extends LoadViewBuilder.Param {

    }
}
