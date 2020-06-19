package com.missmess.messui.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.missmess.messui.R;


/**
 * TitleView can support most of apps to implement their own title bar.
 * Extends to FrameLayout so you can add any custom layout directly.
 *
 * @author wl
 * @since 2014-12-25 13:29:51
 */
public class TitleView extends FrameLayout {
    public static final int GRAVITY_ALWAYS_CENTER = 0;
    public static final int GRAVITY_BEHIND_NAVIGATE = 1;
    private TextView navigate_btn;
    private TextView additional_btn;
    private TextView title_text;
    private FrameLayout title_zone;
    private LinearLayout right_zone;
    private ProgressBar load_indicator;
    private FrameLayout title_custom_container;
    private Context mCtx;
    private OnClickListener navigateUpListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            try {
                ((Activity) mCtx).onBackPressed();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private int defaultWrapContentHeight;

    public TitleView(Context context) {
        super(context);
        mCtx = context;
        init();
    }

    public TitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCtx = context;
        init();
        initAttrs(context, attrs);
    }

    private void init() {
        View.inflate(getContext(), R.layout.title_view_layout, this);
        navigate_btn = (TextView) findViewById(R.id.navigate_btn);
        additional_btn = (TextView) findViewById(R.id.additional_btn);
        title_text = (TextView) findViewById(R.id.title_text);
        title_custom_container = (FrameLayout) findViewById(R.id.title_custom_container);
        title_zone = (FrameLayout) findViewById(R.id.title_zone);
        right_zone = (LinearLayout) findViewById(R.id.right_zone);
        load_indicator = (ProgressBar) findViewById(R.id.load_indicator);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TitleView);
        // title
        String titleStr = typedArray.getString(R.styleable.TitleView_titleStr);
        int titleAppearance = typedArray.getResourceId(R.styleable.TitleView_titleAppearance, -1);
        int titleGravity = typedArray.getInt(R.styleable.TitleView_titleGravity, GRAVITY_ALWAYS_CENTER);
        // navigate
        int navigateBtnIcon = typedArray.getResourceId(R.styleable.TitleView_navigateBtnIcon, R.drawable.ic_arrow_up);
        String navigateBtnStr = typedArray.getString(R.styleable.TitleView_navigateBtnStr);
        int navigateBtnAppearance = typedArray.getResourceId(R.styleable.TitleView_navigateBtnAppearance, -1);
        boolean showNavigateBtn = typedArray.getBoolean(R.styleable.TitleView_showNavigateBtn, true);
        boolean navigateBtnAsUp = typedArray.getBoolean(R.styleable.TitleView_navigateBtnAsUp, true);
        typedArray.recycle();

        title_text.setText(titleStr);
        if (titleAppearance != -1) {
            title_text.setTextAppearance(context, titleAppearance);
        }
        // title limit to 1/2 screen width
        int screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        title_text.setMaxWidth(screenWidth / 2);
        applyTitleGravity(titleGravity);

        navigate_btn.setVisibility(showNavigateBtn ? View.VISIBLE : View.GONE);
        navigate_btn.setText(navigateBtnStr);
        navigate_btn.setCompoundDrawablesWithIntrinsicBounds(navigateBtnIcon, 0, 0, 0);
        if (navigateBtnAppearance != -1) {
            navigate_btn.setTextAppearance(context, navigateBtnAppearance);
        }
        setNavigateAsUpEnabled(navigateBtnAsUp);

        additional_btn.setVisibility(View.GONE);
        load_indicator.setVisibility(View.GONE);
        TypedValue typedValue = new TypedValue();
        // set default height when wrap_content from attr actionBarSize
        context.getTheme().resolveAttribute(R.attr.actionBarSize, typedValue, true);
        int dimenResId = typedValue.resourceId;
        defaultWrapContentHeight = getResources().getDimensionPixelSize(dimenResId);
        // set default background color from attr colorPrimary
        if (getBackground() == null) {
            context.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
            int bgResId = typedValue.resourceId;
            setBackgroundResource(bgResId);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED) {
            heightMode = MeasureSpec.EXACTLY;
            heightSize = defaultWrapContentHeight + getPaddingTop() + getPaddingBottom();
        }
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(heightSize, heightMode));
    }

    private void applyTitleGravity(int gravity) {
        RelativeLayout.LayoutParams titleLps = (RelativeLayout.LayoutParams) title_zone.getLayoutParams();
        RelativeLayout.LayoutParams indicateLps = (RelativeLayout.LayoutParams) load_indicator.getLayoutParams();
        if(gravity == GRAVITY_ALWAYS_CENTER) {
            titleLps.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            titleLps.addRule(RelativeLayout.RIGHT_OF, -1);
            indicateLps.addRule(RelativeLayout.LEFT_OF, R.id.title_zone);
            indicateLps.addRule(RelativeLayout.RIGHT_OF, -1);
        } else if(gravity == GRAVITY_BEHIND_NAVIGATE) {
            titleLps.addRule(RelativeLayout.CENTER_HORIZONTAL, 0);
            titleLps.addRule(RelativeLayout.RIGHT_OF, R.id.left_zone);
            indicateLps.addRule(RelativeLayout.LEFT_OF, -1);
            indicateLps.addRule(RelativeLayout.RIGHT_OF, R.id.title_zone);
        }
        if(ViewCompat.isLaidOut(this)) {
            title_zone.setLayoutParams(titleLps);
            load_indicator.setLayoutParams(indicateLps);
        }
    }

    public void setTitleText(int titleRes) {
        setTitleText(getContext().getString(titleRes));
    }

    public void setTitleText(String title) {
        title_text.setVisibility(View.VISIBLE);
        title_text.setText(title);
        title_custom_container.setVisibility(View.GONE);
    }

    public void setCustomTitleView(View view) {
        title_text.setVisibility(View.GONE);
        title_custom_container.setVisibility(View.VISIBLE);
        title_custom_container.removeAllViews();
        if(view != null)
            title_custom_container.addView(view);
    }

    public void setCustomTitleView(int viewRes) {
        title_text.setVisibility(View.GONE);
        title_custom_container.setVisibility(View.VISIBLE);
        title_custom_container.removeAllViews();
        if(viewRes != 0) {
            View.inflate(getContext(), viewRes, title_custom_container);
        }
    }

    public void setTitleTextColor(@ColorInt int color) {
        title_text.setTextColor(color);
    }

    /**
     * Title gravity
     * @param gravity {@link #GRAVITY_ALWAYS_CENTER}, {@link #GRAVITY_BEHIND_NAVIGATE}
     */
    public void setTitleGravity(int gravity) {
        applyTitleGravity(gravity);
    }

    public void showNavigateButton(boolean show) {
        navigate_btn.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void setNavigateText(String text) {
        navigate_btn.setText(text);
    }

    public void setNavigateIcon(int icon) {
        navigate_btn.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
    }

    public void setNavigateTextColor(int color) {
        navigate_btn.setTextColor(color);
    }

    public void setNavigateApperance(int apperance) {
        if (apperance != -1) {
            navigate_btn.setTextAppearance(getContext(), apperance);
        }
    }

    public void setNavigateClickListener(OnClickListener listener) {
        navigate_btn.setOnClickListener(listener);
    }

    /**
     * Set navigate button click event to call activity's {@link Activity#onBackPressed()}.
     * @param enabled true - call onBackPressed(), false - none click listener
     */
    public void setNavigateAsUpEnabled(boolean enabled) {
        if(enabled) {
            navigate_btn.setOnClickListener(navigateUpListener);
        } else {
            navigate_btn.setOnClickListener(null);
        }
    }

    /**
     * Set additional button behind navigate button.
     * @param btnStr if null, hide additional button
     * @param btnAppearance if -1, use default (light-grey color)
     * @param listener if null, no click.
     */
    public void setAdditionalButton(String btnStr, int btnAppearance, OnClickListener listener) {
        if (btnStr != null) {
            additional_btn.setVisibility(View.VISIBLE);
            additional_btn.setText(btnStr);
            if(btnAppearance != -1)
                additional_btn.setTextAppearance(getContext(), btnAppearance);
            additional_btn.setOnClickListener(listener);
        } else {
            additional_btn.setVisibility(View.GONE);
        }
    }

    /**
     * show loading indicator in title view
     */
    public void showLoadIndicator() {
        load_indicator.setVisibility(View.VISIBLE);
    }

    /**
     * hide loading indicator in title view
     */
    public void hideLoadIndicator() {
        load_indicator.setVisibility(View.GONE);
    }

    public void addActionView(View view) {
        right_zone.addView(view);
    }

    public void addActionView(@DrawableRes int icon, OnClickListener li) {
        ImageView view = new ImageView(getContext());
        view.setImageResource(icon);
        view.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        view.setOnClickListener(li);
        right_zone.addView(view, new LinearLayout.LayoutParams(getResources().getDimensionPixelOffset(R.dimen.min_action_view_width),
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void addActionView(String text, OnClickListener li) {
        TextView view = new TextView(getContext());
        view.setTextColor(Color.WHITE);
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, navigate_btn.getTextSize());
        view.setGravity(Gravity.CENTER);
        view.setText(text);
        view.setMinWidth(getResources().getDimensionPixelOffset(R.dimen.min_action_view_width));
        view.setMaxWidth(getResources().getDimensionPixelOffset(R.dimen.max_action_view_width));
        view.setSingleLine();
        view.setEllipsize(TextUtils.TruncateAt.END);
        view.setOnClickListener(li);
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        right_zone.addView(view);
    }

    public void removeActionView(View view) {
        right_zone.removeView(view);
    }

    public void removeActionView(int index) {
        right_zone.removeViewAt(index);
    }

    public View getActionView(int index) {
        return right_zone.getChildAt(index);
    }
}
