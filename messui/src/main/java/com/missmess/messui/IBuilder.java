package com.missmess.messui;

import com.missmess.messui.utils.TUtil;

/**
 * Builder to build a set of configurations. Use {@link #enable()} to enable it and create settings.
 * A {@link IParam param} will be created after build, the param will later be applied by using
 * {@link LayoutDelegate}.
 */
abstract class IBuilder<SETTING extends ISetting, PARAM extends IParam> {
    SETTING s;
    private boolean enabled;

    public IBuilder() {
        this.s = TUtil.getT(this, 0);
    }

    /**
     * Get the default settings, may take effect if builder is enabled.
     *
     * @return default settings
     */
    public SETTING getDefault() {
        if (enabled)
            throw new IllegalStateException("Do not get default settings after enabled!");
        return s;
    }

    /**
     * Enable load-views and edit its settings.
     *
     * @return {@link LoadViewBuilder.Setting}
     */
    public SETTING enable() {
        enabled = true;
        return s;
    }

    public boolean isEnabled() {
        return enabled;
    }

    PARAM build() {
        if (!enabled)
            return null;

        return (PARAM) s.build();
    }
}
