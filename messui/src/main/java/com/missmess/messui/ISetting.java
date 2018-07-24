package com.missmess.messui;

import com.missmess.messui.utils.TUtil;

/**
 * A set of settings use to config layout.
 */
abstract class ISetting<P extends IParam> {
    protected P p;

    ISetting() {
        p = TUtil.getT(this, 0);
    }

    P build() {
        return p;
    }
}