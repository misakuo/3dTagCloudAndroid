package com.moxun.tagcloudlib.view;

/**
 * Copyright © 2016 moxun
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the “Software”),
 * to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.moxun.tagcloudlib.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class TagCloudView extends ViewGroup implements Runnable, TagsAdapter.OnDataSetChangeListener {
    private static final float TOUCH_SCALE_FACTOR = 0.8f;
    private float mSpeed = 2f;
    private TagCloud mTagCloud;
    private float mInertiaX = 0.5f;
    private float mInertiaY = 0.5f;
    private float mCenterX, mCenterY;
    private float mRadius;
    private float mRadiusPercent = 0.9f;

    private float[] mDarkColor = new float[]{1f, 0f, 0f, 1f};//rgba
    private float[] mLightColor = new float[]{0.9412f, 0.7686f, 0.2f, 1f};//rgba

    @IntDef({MODE_DISABLE, MODE_DECELERATE, MODE_UNIFORM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {
    }

    public static final int MODE_DISABLE = 0;
    public static final int MODE_DECELERATE = 1;
    public static final int MODE_UNIFORM = 2;
    public int mMode;

    private MarginLayoutParams mLayoutParams;
    private int mMinSize;

    private boolean mIsOnTouch = false;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private TagsAdapter mAdapter = new NOPTagsAdapter();
    private OnTagClickListener mOnTagClickListener;

    public TagCloudView(Context context) {
        super(context);
        init(context, null);
    }

    public TagCloudView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TagCloudView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setFocusableInTouchMode(true);
        mTagCloud = new TagCloud();
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TagCloudView);

            String m = typedArray.getString(R.styleable.TagCloudView_autoScrollMode);
            mMode = Integer.valueOf(m);

            int light = typedArray.getColor(R.styleable.TagCloudView_lightColor, Color.WHITE);
            setLightColor(light);

            int dark = typedArray.getColor(R.styleable.TagCloudView_darkColor, Color.BLACK);
            setDarkColor(dark);

            float p = typedArray.getFloat(R.styleable.TagCloudView_radiusPercent, mRadiusPercent);
            setRadiusPercent(p);

            float s = typedArray.getFloat(R.styleable.TagCloudView_scrollSpeed, 2f);
            setScrollSpeed(s);

            typedArray.recycle();
        }

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            wm.getDefaultDisplay().getSize(point);
        } else {
            point.x = wm.getDefaultDisplay().getWidth();
            point.y = wm.getDefaultDisplay().getHeight();
        }
        int screenWidth = point.x;
        int screenHeight = point.y;
        mMinSize = screenHeight < screenWidth ? screenHeight : screenWidth;
    }

    public void setAutoScrollMode(@Mode int mode) {
        this.mMode = mode;
    }

    @Mode
    public int getAutoScrollMode() {
        return this.mMode;
    }

    public final void setAdapter(TagsAdapter adapter) {
        mAdapter = adapter;
        mAdapter.setOnDataSetChangeListener(this);
        onChange();
    }

    public void setLightColor(int color) {
        float[] argb = new float[4];
        argb[3] = Color.alpha(color) / 1.0f / 0xff;
        argb[0] = Color.red(color) / 1.0f / 0xff;
        argb[1] = Color.green(color) / 1.0f / 0xff;
        argb[2] = Color.blue(color) / 1.0f / 0xff;

        mLightColor = argb.clone();
        onChange();
    }

    public void setDarkColor(int color) {
        float[] argb = new float[4];
        argb[3] = Color.alpha(color) / 1.0f / 0xff;
        argb[0] = Color.red(color) / 1.0f / 0xff;
        argb[1] = Color.green(color) / 1.0f / 0xff;
        argb[2] = Color.blue(color) / 1.0f / 0xff;

        mDarkColor = argb.clone();
        onChange();
    }

    public void setRadiusPercent(float percent) {
        if (percent > 1f || percent < 0f) {
            throw new IllegalArgumentException("percent value not in range 0 to 1");
        } else {
            mRadiusPercent = percent;
            onChange();
        }
    }

    private void initFromAdapter() {
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCenterX = (getRight() - getLeft()) / 2;
                mCenterY = (getBottom() - getTop()) / 2;
                mRadius = Math.min(mCenterX * mRadiusPercent, mCenterY * mRadiusPercent);
                mTagCloud.setRadius((int) mRadius);

                mTagCloud.setTagColorLight(mLightColor);//higher color
                mTagCloud.setTagColorDark(mDarkColor);//lower color

                mTagCloud.clear();
                removeAllViews();
                for (int i = 0; i < mAdapter.getCount(); i++) {
                    //binding view to each tag
                    Tag tag = new Tag(mAdapter.getPopularity(i));
                    View view = mAdapter.getView(getContext(), i, TagCloudView.this);
                    tag.bindingView(view);
                    mTagCloud.add(tag);
                    addListener(view, i);
                }

                mTagCloud.setInertia(mInertiaX, mInertiaY);
                mTagCloud.create(true);

                resetChildren();
            }
        }, 0);
    }

    private void addListener(View view, final int position) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            if (!view.hasOnClickListeners() && mOnTagClickListener != null) {
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnTagClickListener.onItemClick(TagCloudView.this, v, position);
                    }
                });
            }
        } else {
            if (mOnTagClickListener != null) {
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnTagClickListener.onItemClick(TagCloudView.this, v, position);
                    }
                });
                Log.e("TagCloudView", "Build version is less than 15, the OnClickListener may be overwritten.");
            }
        }
    }

    public void setScrollSpeed(float scrollSpeed) {
        mSpeed = scrollSpeed;
    }

    private void resetChildren() {
        removeAllViews();
        //必须保证getChildAt(i) == mTagCloud.getTagList().get(i)
        for (Tag tag : mTagCloud.getTagList()) {
            addView(tag.getView());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int contentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int contentHeight = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (mLayoutParams == null) {
            mLayoutParams = (MarginLayoutParams) getLayoutParams();
        }

        int dimensionX = widthMode == MeasureSpec.EXACTLY ? contentWidth : mMinSize
            - mLayoutParams.leftMargin - mLayoutParams.rightMargin;
        int dimensionY = heightMode == MeasureSpec.EXACTLY ? contentHeight : mMinSize
            - mLayoutParams.leftMargin - mLayoutParams.rightMargin;
        setMeasuredDimension(dimensionX, dimensionY);

        measureChildren(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mHandler.post(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            Tag tag = mTagCloud.get(i);
            if (child != null && child.getVisibility() != GONE) {
                mAdapter.onThemeColorChanged(child, tag.getColor());
                child.setScaleX(tag.getScale());
                child.setScaleY(tag.getScale());
                int left, top;
                left = (int) (mCenterX + tag.getFlatX()) - child.getMeasuredWidth() / 2;
                top = (int) (mCenterY + tag.getFlatY()) - child.getMeasuredHeight() / 2;

                child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());
            }
        }
    }

    public void reset() {
        mTagCloud.reset();
        resetChildren();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        handleTouchEvent(ev);
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        handleTouchEvent(e);
        return true;
    }

    private float downX, downY;

    private void handleTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = e.getX();
                downY = e.getY();
                mIsOnTouch = true;
            case MotionEvent.ACTION_MOVE:
                //rotate elements depending on how far the selection point is from center of cloud
                float dx = e.getX() - downX;
                float dy = e.getY() - downY;
                if (isValidMove(dx, dy)) {
                    mInertiaX = (dy / mRadius) * mSpeed * TOUCH_SCALE_FACTOR;
                    mInertiaY = (-dx / mRadius) * mSpeed * TOUCH_SCALE_FACTOR;
                    processTouch();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsOnTouch = false;
                break;
        }
    }

    private boolean isValidMove(float dx, float dy) {
        int minDistance = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        return (Math.abs(dx) > minDistance || Math.abs(dy) > minDistance);
    }

    private void processTouch() {
        if (mTagCloud != null) {
            mTagCloud.setInertia(mInertiaX, mInertiaY);
            mTagCloud.update();
        }
        resetChildren();
    }

    @Override
    public void onChange() {
        initFromAdapter();
    }

    @Override
    public void run() {
        if (!mIsOnTouch && mMode != MODE_DISABLE) {
            if (mMode == MODE_DECELERATE) {
                if (mInertiaX > 0.04f) {
                    mInertiaX -= 0.02f;
                }
                if (mInertiaY > 0.04f) {
                    mInertiaY -= 0.02f;
                }
                if (mInertiaX < -0.04f) {
                    mInertiaX += 0.02f;
                }
                if (mInertiaY < 0.04f) {
                    mInertiaY += 0.02f;
                }
            }
            processTouch();
        }

        mHandler.postDelayed(this, 16);
    }

    public void setOnTagClickListener(OnTagClickListener listener) {
        mOnTagClickListener = listener;
    }

    public interface OnTagClickListener {
        void onItemClick(ViewGroup parent, View view, int position);
    }
}
