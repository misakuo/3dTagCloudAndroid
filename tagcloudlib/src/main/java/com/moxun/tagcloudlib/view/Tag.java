package com.moxun.tagcloudlib.view;

import android.graphics.Color;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.view.View;
import com.moxun.tagcloudlib.view.graphics.Point3DF;

/**
 * Copyright © 2016 moxun
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the “Software”),
 * to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

public class Tag implements Comparable<Tag>{

    private int mPopularity;
    private float mScale;
    private float[] mColor;
    private View mView;
    private PointF mFlatCenter;
    private Point3DF mSpatialCenter;

    private static final int DEFAULT_POPULARITY = 5;



    public Tag() {
        this(0f, 0f, 0f, 1.0f, 0);
    }

    public Tag(int popularity) {
        this(0f, 0f, 0f, 1.0f, popularity);
    }

    public Tag(float x, float y, float z) {
        this(x, y, z, 1.0f, DEFAULT_POPULARITY);
    }

    public Tag(float x, float y, float z, float scale) {
        this(x, y, z, scale, DEFAULT_POPULARITY);
    }

    public Tag(float x, float y, float z, float scale, int popularity) {
        this.mSpatialCenter = new Point3DF(x, y, z);
        this.mFlatCenter = new PointF(0f, 0f);

        this.mColor = new float[]{1.0f, 0.5f, 0.5f, 0.5f};

        this.mScale = scale;
        this.mPopularity = popularity;
    }

    public float getSpatialX() {
        return mSpatialCenter.x;
    }

    public void setSpatialX(float x) {
        this.mSpatialCenter.x = x;
    }

    public float getSpatialY() {
        return mSpatialCenter.y;
    }

    public void setSpatialY(float y) {
        this.mSpatialCenter.y = y;
    }

    public float getSpatialZ() {
        return mSpatialCenter.z;
    }

    public void setSpatialZ(float z) {
        this.mSpatialCenter.z = z;
    }

    public float getScale() {
        return mScale;
    }

    public void setScale(float scale) {
        this.mScale = scale;
    }

    public View getView() {
        return mView;
    }

    public void bindingView(View view) {
        this.mView = view;
    }

    public void setAlpha(float alpha) {
        this.mColor[0] = alpha;
    }

    public int getPopularity() {
        return mPopularity;
    }

    public float getFlatX() {
        return mFlatCenter.x;
    }

    public void setFlatX(float x) {
        this.mFlatCenter.x = x;
    }

    public float getFlatY() {
        return mFlatCenter.y;
    }

    public void setFlatY(float y) {
        this.mFlatCenter.y = y;
    }

    public void setColorComponent(float[] rgb) {
        if (rgb != null) {
            System.arraycopy(rgb, 0, this.mColor, this.mColor.length - rgb.length, rgb.length);
        }
    }

    public float getAlpha() {
        return mColor[0];
    }

    public int getColor() {
        int[] result = new int[4];
        for (int i = 0; i < 4; i++) {
            result[i] = (int) (this.mColor[i] * 0xff);
        }
        return Color.argb(result[0], result[1], result[2], result[3]);
    }

    @Override
    public int compareTo(@NonNull Tag another) {
        return this.getScale() > another.getScale() ? 1 : -1;
    }
}
