package com.missmess.messuidemo;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;

import com.missmess.messui.BuilderKit;
import com.missmess.messui.CoreActivity;
import com.missmess.messui.LayoutDelegate;
import com.missmess.messui.ViewState;
import com.missmess.messui.builtin.LoadViewBuilderRaw;
import com.missmess.messui.builtin.RefreshBuilderRaw;
import com.missmess.messui.builtin.SystemUIBuilderRaw;
import com.missmess.messui.builtin.TitleBuilderRaw;

public class MainActivity extends CoreActivity<TitleBuilderRaw, LoadViewBuilderRaw, RefreshBuilderRaw> {

    @Override
    protected void initView(Bundle savedInstanceState) {
        setViewState(ViewState.Loading);
        loadData();
    }

    @Override
    public void loadData() {
        super.loadData();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setViewState(ViewState.Content);
            }
        }, 2000);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    public void buildLayout(BuilderKit<TitleBuilderRaw, LoadViewBuilderRaw, RefreshBuilderRaw, SystemUIBuilderRaw> kit) {
        kit.tb.enable().title("您好啊，开发者").bgColor(Color.GRAY)
                .navigateText("饭回")
                .navigateTextColor(Color.GREEN)
                .titleColor(Color.BLACK);
        kit.lb.enable().loadingTip("😊 稍等一下哦");
        kit.rb.enable().enableRefresh(new Runnable() {
            @Override
            public void run() {
                ((SwipeRefreshLayout) getRefreshLayout()).setRefreshing(false);
            }
        });
        kit.sb.enable().statusBarColor(Color.BLACK).navigationBarColor(Color.RED);
    }
}
