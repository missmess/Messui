package com.missmess.messui.builtin;

import com.missmess.messui.SystemUIBuilder;

/**
 * An subclass with no generic type and not abstract
 *
 * @author wl
 * @since 2018/07/17 13:49
 */
public class SystemUIBuilderRaw extends SystemUIBuilder<SystemUIBuilderRaw.Setting> {

    public static class Setting extends SystemUIBuilder.Setting<Param, Setting> {
    }

    public static class Param extends SystemUIBuilder.Param {

    }
}
