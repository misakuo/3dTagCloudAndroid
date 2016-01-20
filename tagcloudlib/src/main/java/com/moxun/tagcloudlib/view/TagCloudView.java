package com.moxun.tagcloudlib.view;
/**
 * Komodo Lab: Tagin! Project: 3D Tag Cloud
 * Google Summer of Code 2011
 *
 * @authors Reza Shiftehfar, Sara Khosravinasr and Jorge Silva
 */

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moxun.tagcloudlib.R;

import java.util.List;

public class TagCloudView extends ViewGroup implements Runnable,TagsAdapter.OnDataSetChangeListener {
    private RelativeLayout navigation_bar;
    private TextView mTextView1;
    private final float TOUCH_SCALE_FACTOR = .8f;
    private final float TRACKBALL_SCALE_FACTOR = 10;
    private float tspeed;
    private TagCloud mTagCloud;
    private float mAngleX = 0.5f;
    private float mAngleY = 0.5f;
    private float centerX, centerY;
    private float radius;
    private Context mContext;
    private int textSizeMin, textSizeMax;

    public static final int MODE_DECELERATE = 1;
    public static final int MODE_UNIFORM = 2;
    public int mode = 2;

    private boolean isOnTouch = false;
    private Handler handler = new Handler(Looper.getMainLooper());
    private OnTagClickListener onTagClickListener;

    private TagsAdapter tagsAdapter;

    public final void setAdapter(TagsAdapter adapter) {
        tagsAdapter = adapter;
        tagsAdapter.setOnDataSetChangeListener(this);
    }

    public void setOnTagClickListener(OnTagClickListener clickListener) {
        onTagClickListener = clickListener;
    }

    public TagCloudView(Context mContext, int width, int height, List<Tag> tagList) {
        this(mContext, width, height, tagList, 6, 34, 2); //default for min/max text size
    }

    public TagCloudView(Context mContext, int width, int height, List<Tag> tagList,
                        int textSizeMin, int textSizeMax, int scrollSpeed) {

        super(mContext);
        this.mContext = mContext;
        this.textSizeMin = textSizeMin;
        this.textSizeMax = textSizeMax;

        tspeed = scrollSpeed;

        //set the center of the sphere on center of our screen:
        centerX = width / 2;
        centerY = height / 2;
        radius = Math.min(centerX * 0.6f, centerY * 0.6f); //use 95% of screen
        //since we set tag margins from left of screen, we shift the whole tags to left so that
        //it looks more realistic and symmetric relative to center of screen in X direction
        //shiftLeft = (int) (Math.min(centerX * 0.15f, centerY * 0.15f));

        // initialize the TagCloud from a list of tags
        //Filter() func. screens tagList and ignores Tags with same text (Case Insensitive)
        mTagCloud = new TagCloud(tagList, (int) radius,
                textSizeMin,
                textSizeMax
        );
        float[] tempColor1 = {0.9412f, 0.7686f, 0.2f, 1}; //rgb Alpha
        //{1f,0f,0f,1}  red       {0.3882f,0.21568f,0.0f,1} orange
        //{0.9412f,0.7686f,0.2f,1} light orange
        float[] tempColor2 = {1f, 0f, 0f, 1}; //rgb Alpha
        //{0f,0f,1f,1}  blue      {0.1294f,0.1294f,0.1294f,1} grey
        //{0.9412f,0.7686f,0.2f,1} light orange
        mTagCloud.setTagColor1(tempColor1);//higher color
        mTagCloud.setTagColor2(tempColor2);//lower color
        mTagCloud.setRadius((int) radius);
        mTagCloud.create(true); // to put each Tag at its correct initial location


        //update the transparency/scale of tags
        mTagCloud.setAngleX(mAngleX);
        mTagCloud.setAngleY(mAngleY);
        mTagCloud.update();

        //views = new ArrayList<View>();
        //mParams = new ArrayList<LayoutParams>();
        //Now Draw the 3D objects: for all the tags in the TagCloud

        for (Tag tag : mTagCloud.getTagList()) {
            initTag();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(MeasureSpec.EXACTLY,MeasureSpec.EXACTLY);

        setMeasuredDimension(600,600);
    }

    private void initTag() {
        View view = new View(getContext());
        MarginLayoutParams lp = new MarginLayoutParams(100, 100);
        view.setLayoutParams(lp);
        addView(view);
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void addTag(Tag newTag) {
        mTagCloud.add(newTag);
        initTag();
    }

    private void updateChild() {
        requestLayout();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        centerX = (getBottom() - getTop()) / 2;
        centerY = (getRight() - getLeft()) / 2;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                Tag tag = mTagCloud.getTagList().get(i);
                child.setBackgroundColor(tag.getColor());
                child.setScaleX(tag.getScale());
                child.setScaleY(tag.getScale());

                int left, top;
                left = (int) (centerX + tag.getLoc2DX()) - child.getMeasuredWidth() / 2;
                top = (int) (centerY + tag.getLoc2DY()) - child.getMeasuredHeight() / 2;

                child.layout(left,top,left + child.getMeasuredWidth(),top + child.getMeasuredHeight());
            }
        }
    }

    public boolean Replace(Tag newTag, String oldTagText) {
        boolean result = false;
        int j = mTagCloud.Replace(newTag, oldTagText);
        if (j >= 0) {
            //then oldTagText was found and replaced with newTag data
            updateChild();
            result = true;
        }
        return result;
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
        mTagCloud.setAngleX(mAngleX);
        mTagCloud.setAngleY(mAngleY);
        mTagCloud.update();

        updateChild();
    }


    //for handling the click on the tags
    //onclick open the tag url in a new window. Back button will bring you back to TagCloud
    public OnClickListener OnTagClickListener(final Object object) {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTagClickListener != null) {
                    onTagClickListener.onTagClick(v, object);
                }
            }
        };
    }

    @Override
    public void onChange() {

    }

    public interface OnTagClickListener {
        void onTagClick(View v, Object obj);
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
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
