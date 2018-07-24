package com.missmess.messui;

/**
 * ViewState used to indicate the current state of load-views. NOTIFY that the order should
 * always be the same as the order in the layout xml.
 *
 * @author wl
 * @since 2015-7-20 下午6:48:01
 */
public enum ViewState {
    /**
     * normal content state
     */
    Content,
    /**
     * no-data state
     */
    NoData,
	/**
	 * loading state
	 */
	Loading, 
	/**
	 * load-fail state
	 */
	LoadFail,
    /**
     * initial state
     */
    Initial
}
