package com.moxun.tagcloudlib.view;

/**
 * Created by moxun on 19/1/2016
 */

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

public class TagCloudView extends ViewGroup implements Runnable, TagsAdapter.OnDataSetChangeListener {
    private final float TOUCH_SCALE_FACTOR = .8f;
    private final float TRACKBALL_SCALE_FACTOR = 10;
    private float tspeed = 2f;
    private TagCloud mTagCloud;
    private float mAngleX = 0.5f;
    private float mAngleY = 0.5f;
    private float centerX, centerY;
    private float radius;

    public static final int MODE_DECELERATE = 1;
    public static final int MODE_UNIFORM = 2;
    public int mode = 1;

    private boolean isOnTouch = false;
    private Handler handler = new Handler(Looper.getMainLooper());

    private TagsAdapter tagsAdapter;

    public TagCloudView(Context context) {
        super(context);
        setFocusableInTouchMode(true);
    }

    public TagCloudView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusableInTouchMode(true);
    }

    public TagCloudView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFocusableInTouchMode(true);
    }

    public final void setAdapter(TagsAdapter adapter) {
        tagsAdapter = adapter;
        tagsAdapter.setOnDataSetChangeListener(this);
        onChange();
    }

    private void initFromAdapter() {
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                centerX = (getRight() - getLeft()) / 2;
                centerY = (getBottom() - getTop()) / 2;
                radius = Math.min(centerX * 0.6f, centerY * 0.6f);
                mTagCloud = new TagCloud((int) radius);

                float[] tempColor1 = {0.9412f, 0.7686f, 0.2f, 1}; //rgb Alpha
                //{1f,0f,0f,1}  red       {0.3882f,0.21568f,0.0f,1} orange
                //{0.9412f,0.7686f,0.2f,1} light orange
                float[] tempColor2 = {1f, 0f, 0f, 1}; //rgb Alpha
                //{0f,0f,1f,1}  blue      {0.1294f,0.1294f,0.1294f,1} grey
                //{0.9412f,0.7686f,0.2f,1} light orange
                mTagCloud.setTagColorLight(tempColor1);//higher color
                mTagCloud.setTagColorDark(tempColor2);//lower color
                mTagCloud.setRadius((int) radius);

                for (int i = 0; i < tagsAdapter.getCount(); i++) {
                    addView(tagsAdapter.getView(getContext(), i, TagCloudView.this));
                    TagCloudView.this.mTagCloud.add(new Tag(tagsAdapter.getPopularity(i)));
                }

                mTagCloud.create(true);

                mTagCloud.setAngleX(mAngleX);
                mTagCloud.setAngleY(mAngleY);
                mTagCloud.update();
            }
        }, 0);
    }

    public void setScrollSpeed(int scrollSpeed) {
        tspeed = scrollSpeed;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int contentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int contentHeight = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        measureChildren(MeasureSpec.EXACTLY, MeasureSpec.EXACTLY);
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        MarginLayoutParams layoutParams = (MarginLayoutParams) getLayoutParams();
        int screenWidth = wm.getDefaultDisplay().getWidth();
        int dimensionX = widthMode == MeasureSpec.EXACTLY ? contentWidth  : screenWidth - layoutParams.leftMargin - layoutParams.rightMargin;
        int dimensionY  = heightMode == MeasureSpec.EXACTLY ? contentHeight : screenWidth - layoutParams.leftMargin - layoutParams.rightMargin;
        setMeasuredDimension(dimensionX, dimensionY);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        handler.post(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacksAndMessages(null);
    }

    private void updateChild() {
        requestLayout();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                Tag tag = mTagCloud.get(i);
                child.setBackgroundColor(tag.getColor());
                child.setScaleX(tag.getScale());
                child.setScaleY(tag.getScale());

                int left, top;
                left = (int) (centerX + tag.getLoc2DX()) - child.getMeasuredWidth() / 2;
                top = (int) (centerY + tag.getLoc2DY()) - child.getMeasuredHeight() / 2;

                child.layout(left, top, left + child.getMeasuredWidth(), top + child.getMeasuredHeight());
            }
        }
    }

    public void reset() {
        mTagCloud.reset();
        updateChild();
    }

    @Override
    public boolean onTrackballEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();

        mAngleX = (y) * tspeed * TRACKBALL_SCALE_FACTOR;
        mAngleY = (-x) * tspeed * TRACKBALL_SCALE_FACTOR;

        mTagCloud.setAngleX(mAngleX);
        mTagCloud.setAngleY(mAngleY);
        mTagCloud.update();

        updateChild();
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isOnTouch = true;
                break;
            case MotionEvent.ACTION_MOVE:
                //rotate elements depending on how far the selection point is from center of cloud
                float dx = x - centerX;
                float dy = y - centerY;
                mAngleX = (dy / radius) * tspeed * TOUCH_SCALE_FACTOR;
                mAngleY = (-dx / radius) * tspeed * TOUCH_SCALE_FACTOR;

                processTouch();

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isOnTouch = false;
                break;
        }

        return true;
    }

    private void processTouch() {
        if (mTagCloud != null) {
            mTagCloud.setAngleX(mAngleX);
            mTagCloud.setAngleY(mAngleY);
            mTagCloud.update();
        }
        updateChild();
    }

    @Override
    public void onChange() {
        initFromAdapter();
    }

    @Override
    public void run() {
        if (!isOnTouch) {
            if (mode == MODE_DECELERATE) {
                if (mAngleX > 0.04f) {
                    mAngleX -= 0.02f;
                }
                if (mAngleY > 0.04f) {
                    mAngleY -= 0.02f;
                }
                if (mAngleX < -0.04f) {
                    mAngleX += 0.02f;
                }
                if (mAngleY < 0.04f) {
                    mAngleY += 0.02f;
                }
            }
            processTouch();
        }

        handler.postDelayed(this, 50);
    }
}
