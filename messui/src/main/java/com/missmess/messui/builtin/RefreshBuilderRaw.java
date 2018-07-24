package com.missmess.messui.builtin;

import com.missmess.messui.RefreshBuilder;

/**
 * An subclass with no generic type and not abstract

 * @author wl
 * @since 2018/07/17 13:46
 */
public class RefreshBuilderRaw extends RefreshBuilder<RefreshBuilderRaw.Setting> {

    public static class Setting extends RefreshBuilder.Setting<Param, Setting> {
    }

    public static class Param extends RefreshBuilder.Param {

    }
}
